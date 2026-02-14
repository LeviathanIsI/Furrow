package com.furrow.app.ui.garden

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.local.entity.GardenBed
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting

import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.data.repository.GardenRepository
import com.furrow.app.data.repository.PlantRepository
import com.furrow.app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class PlantingSummary(
    val name: String,
    val status: String,
)

data class DailyHarvest(
    val date: LocalDate,
    val totalOz: Double,
    val isToday: Boolean,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GardenViewModel @Inject constructor(
    private val repository: GardenRepository,
    private val plantRepository: PlantRepository,
    private val userProfileRepository: UserProfileRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bedId: Long? = savedStateHandle.get<Long>("bedId")

    private val zone = ZoneId.systemDefault()
    private val today = LocalDate.now(zone)
    private val thirtyDaysAgo = today.minusDays(29)
    private val rangeStartMillis = thirtyDaysAgo.atStartOfDay(zone).toInstant().toEpochMilli()
    private val rangeEndMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1

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

    // -- List screen --

    val activeBeds: StateFlow<List<GardenBed>> = repository.getActiveBeds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activePlantingCounts: StateFlow<Map<Long, Int>> =
        repository.getActivePlantingCountPerBed()
            .map { list -> list.associate { it.bedId to it.count } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val activePlantingsByBed: StateFlow<Map<Long, List<PlantingSummary>>> =
        repository.getActivePlantings()
            .map { plantings ->
                plantings.groupBy { it.bedId }
                    .mapValues { (_, list) -> list.map { PlantingSummary(it.plantName, it.status) } }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // -- 30-day harvest chart --

    private val recentHarvests: StateFlow<List<HarvestLog>> =
        repository.getHarvestsForDateRange(rangeStartMillis, rangeEndMillis)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyHarvests: StateFlow<List<DailyHarvest>> = recentHarvests.map { logs ->
        val byDay = logs.groupBy { log ->
            Instant.ofEpochMilli(log.date).atZone(zone).toLocalDate()
        }
        (0L..29L).map { offset ->
            val date = thirtyDaysAgo.plusDays(offset)
            val dayLogs = byDay[date] ?: emptyList()
            DailyHarvest(
                date = date,
                totalOz = dayLogs.sumOf { it.amountOz ?: 0.0 },
                isToday = date == today,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Detail screen (driven by bedId nav arg) --

    val selectedBed: StateFlow<GardenBed?> = bedId?.let {
        repository.getBedById(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    } ?: MutableStateFlow(null)

    val plantings: StateFlow<List<Planting>> = bedId?.let {
        repository.getPlantingsForBed(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val harvests: StateFlow<List<HarvestLog>> = bedId?.let {
        repository.getHarvestsForBed(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    // -- Lookups for edit mode --

    fun getPlantingById(id: Long) = repository.getPlantingById(id)

    fun getHarvestById(id: Long) = repository.getHarvestById(id)

    // -- Actions --

    fun addBed(bed: GardenBed) {
        viewModelScope.launch { repository.insertBed(bed) }
    }

    fun updateBed(bed: GardenBed) {
        viewModelScope.launch { repository.updateBed(bed) }
    }

    fun deleteBed(bed: GardenBed) {
        viewModelScope.launch { repository.deleteBed(bed) }
    }

    fun addPlanting(planting: Planting) {
        viewModelScope.launch { repository.insertPlanting(planting) }
    }

    fun updatePlanting(planting: Planting) {
        viewModelScope.launch { repository.updatePlanting(planting) }
    }

    fun addHarvest(harvest: HarvestLog) {
        viewModelScope.launch { repository.insertHarvest(harvest) }
    }

    fun updateHarvest(harvest: HarvestLog) {
        viewModelScope.launch { repository.updateHarvest(harvest) }
    }

    fun deletePlanting(planting: Planting) {
        viewModelScope.launch { repository.deletePlanting(planting) }
    }

    fun deleteHarvest(harvest: HarvestLog) {
        viewModelScope.launch { repository.deleteHarvest(harvest) }
    }

    fun insertPlant(plant: PlantInfo) {
        viewModelScope.launch { plantRepository.insertPlant(plant) }
    }

    fun updatePlant(plant: PlantInfo) {
        viewModelScope.launch { plantRepository.updatePlant(plant) }
    }

    fun deletePlant(plant: PlantInfo) {
        viewModelScope.launch { plantRepository.deletePlant(plant) }
    }
}
