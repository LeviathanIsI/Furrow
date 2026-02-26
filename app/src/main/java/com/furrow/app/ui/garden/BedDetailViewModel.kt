package com.furrow.app.ui.garden

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.local.entity.GardenBed
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.data.local.entity.FertilizerLog
import com.furrow.app.data.local.entity.PestDiseaseLog
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.PlantVariety
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.WateringLog
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.data.repository.GardenRepository
import com.furrow.app.data.repository.PlantRepository
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.ui.FurrowViewModel
import com.furrow.app.util.WidgetRefreshUtil
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BedDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: GardenRepository,
    private val plantRepository: PlantRepository,
    private val userProfileRepository: UserProfileRepository,
    savedStateHandle: SavedStateHandle,
) : FurrowViewModel() {

    private val bedId: Long = checkNotNull(savedStateHandle.get<Long>("bedId"))

    internal val zone: ZoneId = ZoneId.systemDefault()
    private val today = LocalDate.now(zone)

    // -- Bed data --

    val selectedBed: StateFlow<GardenBed?> = repository.getBedById(bedId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val plantings: StateFlow<List<Planting>> = repository.getPlantingsForBed(bedId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val harvests: StateFlow<List<HarvestLog>> = repository.getHarvestsForBed(bedId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Plant reference data --

    val userProfile: StateFlow<UserProfile?> = userProfileRepository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allPlants: StateFlow<List<PlantInfo>> = plantRepository.getAllPlants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val plantInfoMap: StateFlow<Map<String, PlantInfo>> = allPlants.map { plants ->
        plants.associateBy { it.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val currentMonth = today.monthValue

    val activeWindows: StateFlow<List<PlantingWindow>> = userProfile.flatMapLatest { profile ->
        if (profile != null) {
            plantRepository.getActiveWindowsForMonth(profile.zoneGroup, currentMonth)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val zoneWindows: StateFlow<List<PlantingWindow>> = userProfile.flatMapLatest { profile ->
        if (profile != null) {
            plantRepository.getWindowsForZoneGroup(profile.zoneGroup)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Varieties --

    val allVarieties: StateFlow<List<PlantVariety>> = plantRepository.getAllVarieties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val varietiesByPlantId: StateFlow<Map<Long, List<PlantVariety>>> = allVarieties.map { varieties ->
        varieties.groupBy { it.plantId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun getVarietiesForPlant(plantId: Long) = plantRepository.getVarietiesForPlant(plantId)

    // -- Harvest predictions --

    val harvestPredictions: StateFlow<Map<Long, HarvestPrediction>> =
        combine(
            plantings,
            plantInfoMap,
            allVarieties,
        ) { bedPlantings, infoMap, varieties ->
            val varietyMap = varieties.associateBy { it.id }
            bedPlantings.mapNotNull { planting ->
                val plantInfo = infoMap[planting.plantName] ?: return@mapNotNull null
                val variety = planting.varietyId?.let { varietyMap[it] }
                val daysMin = variety?.daysToHarvestMin ?: plantInfo.daysToHarvestMin
                val daysMax = variety?.daysToHarvestMax ?: plantInfo.daysToHarvestMax
                val plantedDate = Instant.ofEpochMilli(planting.datePlanted)
                    .atZone(zone).toLocalDate()
                val earliest = plantedDate.plusDays(daysMin.toLong())
                val latest = plantedDate.plusDays(daysMax.toLong())
                val daysUntil = ChronoUnit.DAYS.between(today, earliest)
                planting.id to HarvestPrediction(
                    plantingId = planting.id,
                    earliestHarvest = earliest,
                    latestHarvest = latest,
                    daysUntilEarliest = daysUntil,
                    isReady = !today.isBefore(earliest),
                    isOverdue = today.isAfter(latest),
                )
            }.toMap()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // -- Care logs --

    val wateringLogs: StateFlow<List<WateringLog>> = repository.getWateringLogsForBed(bedId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fertilizerLogs: StateFlow<List<FertilizerLog>> = repository.getFertilizerLogsForBed(bedId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pestLogs: StateFlow<List<PestDiseaseLog>> = repository.getPestLogsForBed(bedId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Lookups --

    fun getPlantingById(id: Long) = repository.getPlantingById(id)

    fun getHarvestById(id: Long) = repository.getHarvestById(id)

    // -- Planting actions --

    fun addPlanting(planting: Planting) {
        safeLaunchWithSuccess("Planting saved") {
            repository.insertPlanting(planting)
            WidgetRefreshUtil.refresh(context)
        }
    }

    fun updatePlanting(planting: Planting) {
        safeLaunchWithSuccess("Planting updated") {
            repository.updatePlanting(planting)
            WidgetRefreshUtil.refresh(context)
        }
    }

    fun plantNow(planting: Planting) = safeLaunchWithSuccess("Planting started") {
        val todayMillis = java.time.LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli()
        repository.updatePlanting(
            planting.copy(
                datePlanted = todayMillis,
                status = "Growing",
                targetPlantDate = null,
            ),
        )
        WidgetRefreshUtil.refresh(context)
    }

    fun deletePlanting(planting: Planting) {
        safeLaunch {
            repository.deletePlanting(planting)
            WidgetRefreshUtil.refresh(context)
        }
    }

    // -- Harvest actions --

    fun addHarvest(harvest: HarvestLog) {
        safeLaunchWithSuccess("Harvest saved") {
            repository.insertHarvest(harvest)
            WidgetRefreshUtil.refresh(context)
        }
    }

    fun updateHarvest(harvest: HarvestLog) {
        safeLaunchWithSuccess("Harvest updated") {
            repository.updateHarvest(harvest)
            WidgetRefreshUtil.refresh(context)
        }
    }

    fun deleteHarvest(harvest: HarvestLog) {
        safeLaunch {
            repository.deleteHarvest(harvest)
            WidgetRefreshUtil.refresh(context)
        }
    }

    // -- Watering actions --

    fun addWateringLog(log: WateringLog) {
        safeLaunchWithSuccess("Watering logged") { repository.insertWateringLog(log) }
    }

    fun deleteWateringLog(log: WateringLog) {
        safeLaunch { repository.deleteWateringLog(log) }
    }

    // -- Fertilizer actions --

    fun addFertilizerLog(log: FertilizerLog) {
        safeLaunchWithSuccess("Fertilizer logged") { repository.insertFertilizerLog(log) }
    }

    fun deleteFertilizerLog(log: FertilizerLog) {
        safeLaunch { repository.deleteFertilizerLog(log) }
    }

    // -- Pest/Disease actions --

    fun addPestLog(log: PestDiseaseLog) {
        safeLaunchWithSuccess("Pest report saved") { repository.insertPestLog(log) }
    }

    fun updatePestLog(log: PestDiseaseLog) {
        safeLaunchWithSuccess("Pest report updated") { repository.updatePestLog(log) }
    }

    fun deletePestLog(log: PestDiseaseLog) {
        safeLaunch { repository.deletePestLog(log) }
    }

    // -- Plant CRUD (for custom plants in forms) --

    fun insertPlant(plant: PlantInfo) {
        safeLaunch { plantRepository.insertPlant(plant) }
    }

    fun updatePlant(plant: PlantInfo) {
        safeLaunch { plantRepository.updatePlant(plant) }
    }

    fun deletePlant(plant: PlantInfo) {
        safeLaunch { plantRepository.deletePlant(plant) }
    }

    fun insertVariety(variety: PlantVariety) {
        safeLaunch { plantRepository.insertVariety(variety) }
    }

    fun updateVariety(variety: PlantVariety) {
        safeLaunch { plantRepository.updateVariety(variety) }
    }

    fun deleteVariety(variety: PlantVariety) {
        safeLaunch { plantRepository.deleteVariety(variety) }
    }
}
