package com.furrow.app.ui.garden

import androidx.lifecycle.viewModelScope
import com.furrow.app.data.local.entity.CropRotationLog
import com.furrow.app.data.local.entity.GardenBed
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.data.repository.GardenRepository
import com.furrow.app.data.repository.PlantRepository
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.ui.FurrowViewModel
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.furrow.app.util.WidgetRefreshUtil
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class PlantSelection(
    val plantInfo: PlantInfo,
    val window: PlantingWindow?,
    val assignedBedId: Long? = null,
    val targetDate: Long? = null,
)

data class RotationWarning(
    val bedId: Long,
    val bedName: String,
    val plantName: String,
    val category: String,
    val lastCategory: String,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SeasonPlannerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gardenRepository: GardenRepository,
    private val plantRepository: PlantRepository,
    private val userProfileRepository: UserProfileRepository,
) : FurrowViewModel() {

    private val zone: ZoneId = ZoneId.systemDefault()
    private val today = LocalDate.now(zone)
    private val currentMonth = today.monthValue

    // ── Wizard step ──

    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    // ── Selections accumulated across steps ──

    private val _selections = MutableStateFlow<List<PlantSelection>>(emptyList())
    val selections: StateFlow<List<PlantSelection>> = _selections.asStateFlow()

    // ── Reference data ──

    val allPlants: StateFlow<List<PlantInfo>> = plantRepository.getAllPlants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBeds: StateFlow<List<GardenBed>> = gardenRepository.getActiveBeds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val userProfile = userProfileRepository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeWindows: StateFlow<List<PlantingWindow>> = userProfile.flatMapLatest { profile ->
        if (profile != null) {
            plantRepository.getActiveWindowsForMonth(profile.zoneGroup, currentMonth)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alreadyPlantedNames: StateFlow<Set<String>> = gardenRepository.getActivePlantings()
        .map { plantings -> plantings.map { it.plantName }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // ── Rotation warnings ──

    private val oneYearAgoMillis = today.minusYears(1).atStartOfDay(zone).toInstant().toEpochMilli()

    val rotationWarnings: StateFlow<List<RotationWarning>> = combine(
        _selections,
        activeBeds,
        allPlants,
    ) { sels, beds, plants ->
        val plantMap = plants.associateBy { it.name }
        val bedMap = beds.associateBy { it.id }
        val warnings = mutableListOf<RotationWarning>()

        // Group selections by assigned bed
        val selsByBed = sels.filter { it.assignedBedId != null }.groupBy { it.assignedBedId!! }

        selsByBed.forEach { (bedId, bedSels) ->
            // Get recent plantings for this bed
            val recentPlantings = gardenRepository.getRecentPlantingsForBed(bedId, oneYearAgoMillis).firstOrNull() ?: emptyList()

            bedSels.forEach { sel ->
                val category = sel.plantInfo.category
                // Check against recent actual plantings
                recentPlantings.forEach { pastPlanting ->
                    val pastCategory = plantMap[pastPlanting.plantName]?.category
                    if (pastCategory != null && pastCategory.equals(category, ignoreCase = true)) {
                        warnings.add(
                            RotationWarning(
                                bedId = bedId,
                                bedName = bedMap[bedId]?.name ?: "Bed",
                                plantName = sel.plantInfo.name,
                                category = category,
                                lastCategory = pastCategory,
                            ),
                        )
                    }
                }
            }
        }

        warnings.distinctBy { "${it.bedId}_${it.plantName}" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Actions ──

    fun togglePlant(plant: PlantInfo, window: PlantingWindow?) {
        val current = _selections.value.toMutableList()
        val existing = current.indexOfFirst { it.plantInfo.name == plant.name }
        if (existing >= 0) {
            current.removeAt(existing)
        } else {
            val defaultDate = window?.let {
                val startDate = today.withMonth(it.startMonth).withDayOfMonth(1)
                val effectiveDate = if (startDate.isBefore(today)) today else startDate
                effectiveDate.atStartOfDay(zone).toInstant().toEpochMilli()
            }
            current.add(PlantSelection(plant, window, targetDate = defaultDate))
        }
        _selections.value = current
    }

    fun assignBed(plantName: String, bedId: Long) {
        _selections.value = _selections.value.map {
            if (it.plantInfo.name == plantName) it.copy(assignedBedId = bedId) else it
        }
    }

    fun updateTargetDate(plantName: String, date: Long) {
        _selections.value = _selections.value.map {
            if (it.plantInfo.name == plantName) it.copy(targetDate = date) else it
        }
    }

    fun nextStep() {
        if (_currentStep.value < 4) _currentStep.value++
    }

    fun prevStep() {
        if (_currentStep.value > 1) _currentStep.value--
    }

    fun goToStep(step: Int) {
        if (step in 1..4) _currentStep.value = step
    }

    fun savePlan() = safeLaunch {
        val plantings = _selections.value.filter { it.assignedBedId != null }.map { sel ->
            val targetMillis = sel.targetDate
                ?: today.atStartOfDay(zone).toInstant().toEpochMilli()
            Planting(
                bedId = sel.assignedBedId!!,
                plantName = sel.plantInfo.name,
                datePlanted = targetMillis,
                source = sel.window?.method ?: "Transplant",
                status = "Planned",
                targetPlantDate = targetMillis,
            )
        }

        gardenRepository.insertPlantings(plantings)

        // Insert rotation log entries
        val year = today.year
        plantings.forEach { planting ->
            val plant = _selections.value.firstOrNull { it.plantInfo.name == planting.plantName }
            val family = plant?.plantInfo?.rotationGroup ?: plant?.plantInfo?.category ?: return@forEach
            gardenRepository.insertCropRotationLog(
                CropRotationLog(
                    bedId = planting.bedId,
                    year = year,
                    season = "spring",
                    cropFamilyPlanted = family,
                ),
            )
        }

        WidgetRefreshUtil.refresh(context)
    }
}
