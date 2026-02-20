package com.furrow.app.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.local.entity.GardenBed
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.PlantVariety
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.data.repository.GardenRepository
import com.furrow.app.data.repository.PlantRepository
import com.furrow.app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

data class PlantingSummary(
    val name: String,
    val variety: String?,
    val status: String,
)

data class DailyHarvest(
    val date: LocalDate,
    val totalOz: Double,
    val isToday: Boolean,
)

data class HarvestPrediction(
    val plantingId: Long,
    val earliestHarvest: LocalDate,
    val latestHarvest: LocalDate,
    val daysUntilEarliest: Long,
    val isReady: Boolean,
    val isOverdue: Boolean,
)

data class ReadyToHarvestItem(
    val plantingId: Long,
    val bedId: Long,
    val plantName: String,
    val variety: String?,
    val bedName: String,
    val isOverdue: Boolean,
)

data class SuccessionSuggestion(
    val plantingId: Long,
    val bedId: Long,
    val plantName: String,
    val variety: String?,
    val bedName: String,
    val plantByDate: LocalDate?,
    val seasonComplete: Boolean,
)

data class BedWateringStatus(
    val bedId: Long,
    val daysSinceWatered: Long?,
)

sealed class GardenAlert(val priority: Int) {
    data class HarvestOverdue(val item: ReadyToHarvestItem) : GardenAlert(1)
    data class HarvestReady(val item: ReadyToHarvestItem) : GardenAlert(2)
    data class NeedsWater(val bedId: Long, val bedName: String, val daysSince: Long) : GardenAlert(3)
    data class GerminationOverdue(
        val plantingId: Long,
        val bedId: Long,
        val plantName: String,
        val bedName: String,
        val daysPast: Long,
    ) : GardenAlert(4)
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GardenViewModel @Inject constructor(
    private val repository: GardenRepository,
    private val plantRepository: PlantRepository,
    private val userProfileRepository: UserProfileRepository,
) : ViewModel() {

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
                    .mapValues { (_, list) ->
                        list.map { PlantingSummary(it.plantName, it.variety, it.status) }
                    }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // -- Varieties (needed for global harvest predictions) --

    private val allVarieties: StateFlow<List<PlantVariety>> = plantRepository.getAllVarieties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Harvest predictions (global, across all beds) --

    val harvestPredictions: StateFlow<Map<Long, HarvestPrediction>> =
        combine(
            repository.getActivePlantings(),
            plantInfoMap,
            allVarieties,
        ) { plantings, infoMap, varieties ->
            val varietyMap = varieties.associateBy { it.id }
            plantings.mapNotNull { planting ->
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

    val readyToHarvest: StateFlow<List<ReadyToHarvestItem>> =
        combine(
            repository.getActivePlantings(),
            harvestPredictions,
            activeBeds,
        ) { plantings, predictions, beds ->
            val bedMap = beds.associateBy { it.id }
            plantings.filter { planting ->
                planting.status in listOf("growing", "producing") &&
                    predictions[planting.id]?.isReady == true
            }.map { planting ->
                val pred = predictions[planting.id]!!
                ReadyToHarvestItem(
                    plantingId = planting.id,
                    bedId = planting.bedId,
                    plantName = planting.plantName,
                    variety = planting.variety,
                    bedName = bedMap[planting.bedId]?.name ?: "Unknown",
                    isOverdue = pred.isOverdue,
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Watering status per bed --

    val lastWateredPerBed: StateFlow<Map<Long, BedWateringStatus>> =
        repository.getLastWateredPerBed()
            .map { list ->
                list.associate { bw ->
                    val lastWateredDate = Instant.ofEpochMilli(bw.lastWatered)
                        .atZone(zone).toLocalDate()
                    val daysSince = ChronoUnit.DAYS.between(lastWateredDate, today)
                    bw.bedId to BedWateringStatus(bw.bedId, daysSince)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // -- Succession planting suggestions --

    val successionSuggestions: StateFlow<List<SuccessionSuggestion>> =
        combine(
            repository.getActivePlantings(),
            plantInfoMap,
            activeBeds,
            userProfile,
        ) { plantings, infoMap, beds, profile ->
            val bedMap = beds.associateBy { it.id }
            val frostDateStr = profile?.firstFrostDate
            val frostDate = frostDateStr?.let { parseFrostDate(it, today.year) }
            plantings.filter { planting ->
                planting.status in listOf("harvested", "producing", "finished") &&
                    infoMap[planting.plantName]?.canSuccessionPlant == true
            }.map { planting ->
                val plantInfo = infoMap[planting.plantName]!!
                val daysNeeded = plantInfo.daysToHarvestMin.toLong()
                val plantByDate = frostDate?.let { frost ->
                    frost.minusDays(daysNeeded)
                }
                val seasonComplete = plantByDate != null && today.isAfter(plantByDate)
                SuccessionSuggestion(
                    plantingId = planting.id,
                    bedId = planting.bedId,
                    plantName = planting.plantName,
                    variety = planting.variety,
                    bedName = bedMap[planting.bedId]?.name ?: "Unknown",
                    plantByDate = plantByDate,
                    seasonComplete = seasonComplete,
                )
            }.filter { !it.seasonComplete }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Germination alerts --

    private val germinationAlerts: StateFlow<List<GardenAlert>> =
        combine(
            repository.getActivePlantings(),
            plantInfoMap,
            activeBeds,
        ) { plantings, infoMap, beds ->
            val bedMap = beds.associateBy { it.id }
            plantings.filter { it.source == "seed" && it.germinationDate == null && it.status == "growing" }
                .mapNotNull { planting ->
                    val plantInfo = infoMap[planting.plantName] ?: return@mapNotNull null
                    val germMax = plantInfo.germinationDaysMax ?: return@mapNotNull null
                    val plantedDate = Instant.ofEpochMilli(planting.datePlanted).atZone(zone).toLocalDate()
                    val overdueDate = plantedDate.plusDays(germMax.toLong())
                    if (today.isAfter(overdueDate)) {
                        GardenAlert.GerminationOverdue(
                            plantingId = planting.id,
                            bedId = planting.bedId,
                            plantName = planting.plantName,
                            bedName = bedMap[planting.bedId]?.name ?: "Unknown",
                            daysPast = ChronoUnit.DAYS.between(overdueDate, today),
                        )
                    } else null
                }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Dashboard alerts (priority-ordered) --

    val dashboardAlerts: StateFlow<List<GardenAlert>> =
        combine(
            readyToHarvest,
            lastWateredPerBed,
            germinationAlerts,
            activeBeds,
        ) { harvest, watered, germAlerts, beds ->
            val alerts = mutableListOf<GardenAlert>()

            // Priority 1: Harvest alerts
            harvest.forEach { item ->
                if (item.isOverdue) {
                    alerts.add(GardenAlert.HarvestOverdue(item))
                } else {
                    alerts.add(GardenAlert.HarvestReady(item))
                }
            }

            // Priority 2: Watering alerts (beds not watered in 3+ days)
            val bedMap = beds.associateBy { it.id }
            watered.forEach { (bedId, status) ->
                val days = status.daysSinceWatered
                if (days != null && days >= 3) {
                    alerts.add(
                        GardenAlert.NeedsWater(
                            bedId = bedId,
                            bedName = bedMap[bedId]?.name ?: "Unknown",
                            daysSince = days,
                        )
                    )
                }
            }

            // Priority 3: Germination overdue
            alerts.addAll(germAlerts)

            alerts.sortedBy { it.priority }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    // -- Bed actions --

    fun addBed(bed: GardenBed) {
        viewModelScope.launch { repository.insertBed(bed) }
    }

    fun updateBed(bed: GardenBed) {
        viewModelScope.launch { repository.updateBed(bed) }
    }

    fun deleteBed(bed: GardenBed) {
        viewModelScope.launch { repository.deleteBed(bed) }
    }

    private fun parseFrostDate(dateStr: String, year: Int): LocalDate? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("MMM d", Locale.US)
            val parsed = formatter.parse(dateStr)
            val month = parsed.get(java.time.temporal.ChronoField.MONTH_OF_YEAR)
            val day = parsed.get(java.time.temporal.ChronoField.DAY_OF_MONTH)
            LocalDate.of(year, month, day)
        } catch (_: Exception) {
            null
        }
    }
}
