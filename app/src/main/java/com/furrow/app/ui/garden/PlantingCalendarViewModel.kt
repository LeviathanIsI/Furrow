package com.furrow.app.ui.garden

import androidx.lifecycle.viewModelScope
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.PlantVariety
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.data.repository.GardenRepository
import com.furrow.app.data.repository.PlantRepository
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.ui.FurrowViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

enum class CalendarEventType { PLANTED, HARVEST, WATERED, FERTILIZED }

data class CalendarEvent(
    val date: LocalDate,
    val type: CalendarEventType,
    val label: String,
)

data class ActiveWindowItem(
    val plantName: String,
    val method: String,
    val startMonth: Int,
    val endMonth: Int,
    val isPlanted: Boolean,
)

data class UpcomingHarvestItem(
    val plantingId: Long,
    val bedId: Long,
    val plantName: String,
    val variety: String?,
    val bedName: String,
    val estimatedDate: LocalDate,
    val daysUntil: Long,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlantingCalendarViewModel @Inject constructor(
    private val gardenRepository: GardenRepository,
    private val plantRepository: PlantRepository,
    private val userProfileRepository: UserProfileRepository,
) : FurrowViewModel() {

    internal val zone: ZoneId = ZoneId.systemDefault()
    private val today = LocalDate.now(zone)

    val userProfile: StateFlow<UserProfile?> = userProfileRepository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val allPlants: StateFlow<List<PlantInfo>> = plantRepository.getAllPlants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val plantInfoMap: StateFlow<Map<String, PlantInfo>> = allPlants.map { plants ->
        plants.associateBy { it.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val allVarieties: StateFlow<List<PlantVariety>> = plantRepository.getAllVarieties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val activePlantings = gardenRepository.getActivePlantings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val activeBeds = gardenRepository.getActiveBeds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calendar events for the displayed month
    fun getEventsForMonth(yearMonth: YearMonth): StateFlow<List<CalendarEvent>> {
        val startMillis = yearMonth.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1

        val plantingEvents = activePlantings.map { plantings ->
            plantings.mapNotNull { planting ->
                val plantedDate = Instant.ofEpochMilli(planting.datePlanted).atZone(zone).toLocalDate()
                if (YearMonth.from(plantedDate) == yearMonth) {
                    CalendarEvent(plantedDate, CalendarEventType.PLANTED, planting.plantName)
                } else null
            }
        }

        val harvestEvents = gardenRepository.getHarvestsForDateRange(startMillis, endMillis).map { logs ->
            logs.map { log ->
                val logDate = Instant.ofEpochMilli(log.date).atZone(zone).toLocalDate()
                CalendarEvent(logDate, CalendarEventType.HARVEST, "Harvest")
            }
        }

        val waterEvents = gardenRepository.getWateringLogsForDateRange(startMillis, endMillis).map { logs ->
            logs.map { log ->
                val logDate = Instant.ofEpochMilli(log.date).atZone(zone).toLocalDate()
                CalendarEvent(logDate, CalendarEventType.WATERED, "Watered")
            }
        }

        val fertilizerEvents = gardenRepository.getFertilizerLogsForDateRange(startMillis, endMillis).map { logs ->
            logs.map { log ->
                val logDate = Instant.ofEpochMilli(log.date).atZone(zone).toLocalDate()
                CalendarEvent(logDate, CalendarEventType.FERTILIZED, "Fertilized")
            }
        }

        return combine(plantingEvents, harvestEvents, waterEvents, fertilizerEvents) { p, h, w, f ->
            p + h + w + f
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // Active planting windows for this month
    val activeWindowsThisMonth: StateFlow<List<ActiveWindowItem>> =
        userProfile.flatMapLatest { profile ->
            if (profile != null) {
                combine(
                    plantRepository.getActiveWindowsForMonth(profile.zoneGroup, today.monthValue),
                    activePlantings,
                ) { windows, plantings ->
                    val plantedNames = plantings.map { it.plantName.lowercase() }.toSet()
                    windows.map { window ->
                        ActiveWindowItem(
                            plantName = window.plantName,
                            method = window.method,
                            startMonth = window.startMonth,
                            endMonth = window.endMonth,
                            isPlanted = window.plantName.lowercase() in plantedNames,
                        )
                    }
                }
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Upcoming harvests sorted by date
    val upcomingHarvests: StateFlow<List<UpcomingHarvestItem>> =
        combine(
            activePlantings,
            plantInfoMap,
            allVarieties,
            activeBeds,
        ) { plantings, infoMap, varieties, beds ->
            val varietyMap = varieties.associateBy { it.id }
            val bedMap = beds.associateBy { it.id }
            plantings.mapNotNull { planting ->
                val plantInfo = infoMap[planting.plantName] ?: return@mapNotNull null
                val variety = planting.varietyId?.let { varietyMap[it] }
                val daysMin = variety?.daysToHarvestMin ?: plantInfo.daysToHarvestMin
                val plantedDate = Instant.ofEpochMilli(planting.datePlanted).atZone(zone).toLocalDate()
                val harvestDate = plantedDate.plusDays(daysMin.toLong())
                val daysUntil = ChronoUnit.DAYS.between(today, harvestDate)
                if (daysUntil < -14) return@mapNotNull null // skip long-past harvests
                UpcomingHarvestItem(
                    plantingId = planting.id,
                    bedId = planting.bedId,
                    plantName = planting.plantName,
                    variety = planting.variety,
                    bedName = bedMap[planting.bedId]?.name ?: "Unknown",
                    estimatedDate = harvestDate,
                    daysUntil = daysUntil,
                )
            }.sortedBy { it.estimatedDate }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
