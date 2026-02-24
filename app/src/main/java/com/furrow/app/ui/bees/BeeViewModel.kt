package com.furrow.app.ui.bees

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.BeeCalendar
import com.furrow.app.data.local.entity.BeeRaceInfo
import com.furrow.app.data.local.entity.Hive
import com.furrow.app.data.local.entity.Inspection
import com.furrow.app.data.local.entity.Treatment
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.data.repository.BeeRepository
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.ui.FurrowViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.furrow.app.util.DateUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class BeeViewModel @Inject constructor(
    private val repository: BeeRepository,
    private val userProfileRepository: UserProfileRepository,
    savedStateHandle: SavedStateHandle,
) : FurrowViewModel() {

    private val hiveId: Long? = savedStateHandle.get<Long>("hiveId")

    internal val zone: ZoneId = runBlocking {
        DateUtil.profileZone(userProfileRepository.getProfile().firstOrNull())
    }

    // -- Bee race reference data --

    val allRaces: StateFlow<List<BeeRaceInfo>> = repository.getAllRaces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val raceInfoMap: StateFlow<Map<String, BeeRaceInfo>> = allRaces.map { races ->
        races.associateBy { it.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val userProfile: StateFlow<UserProfile?> = userProfileRepository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // -- Bee Calendar (current month × zone group) --

    private val currentMonth = LocalDate.now(zone).monthValue

    val currentMonthCalendar: StateFlow<BeeCalendar.MonthlyInfo?> = userProfile.map { profile ->
        profile?.let { BeeCalendar.getMonthlyInfo(currentMonth, it.zoneGroup) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // -- List screen --

    val activeHives = repository.getActiveHives()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lastInspectionDates: StateFlow<Map<Long, Long>> =
        repository.getLastInspectionDatePerHive()
            .map { list -> list.associate { it.hiveId to it.date } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    /** Active treatment type per hive (for list screen badges) */
    val activeTreatmentsPerHive: StateFlow<Map<Long, String>> =
        repository.getActiveTreatments(Instant.now().toEpochMilli())
            .map { list -> list.associate { it.hiveId to it.type } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    /** Latest full inspection per hive (for health ring on cards) */
    val latestInspectionPerHive: StateFlow<Map<Long, Inspection>> =
        repository.getLatestInspectionPerHive()
            .map { list -> list.associateBy { it.hiveId } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    /** Last 5 inspection dates per hive (for mini-timeline dots, newest first) */
    val recentInspectionsPerHive: StateFlow<Map<Long, List<Long>>> =
        repository.getAllInspectionDates()
            .map { list ->
                list.groupBy { it.hiveId }
                    .mapValues { (_, dates) -> dates.take(5).map { it.date } }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // -- Detail screen (driven by hiveId nav arg) --

    val selectedHive: StateFlow<Hive?> = hiveId?.let {
        repository.getHiveById(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    } ?: MutableStateFlow(null)

    val inspections: StateFlow<List<Inspection>> = hiveId?.let {
        repository.getInspectionsForHive(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val treatments: StateFlow<List<Treatment>> = hiveId?.let {
        repository.getTreatmentsForHive(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val latestQueenConfirmation: StateFlow<Long?> = inspections.map { list ->
        list.firstOrNull { it.queenSeen }?.date
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeTreatments: StateFlow<List<Treatment>> = treatments.map { list ->
        list.filter { it.endDate == null || it.endDate > Instant.now().toEpochMilli() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Lookups for edit mode --

    fun getInspectionById(id: Long) = repository.getInspectionById(id)

    fun getTreatmentById(id: Long) = repository.getTreatmentById(id)

    // -- Actions --

    fun addHive(hive: Hive) {
        safeLaunch { repository.insertHive(hive) }
    }

    fun updateHive(hive: Hive) {
        safeLaunch { repository.updateHive(hive) }
    }

    fun deleteHive(hive: Hive) {
        safeLaunch { repository.deleteHive(hive) }
    }

    fun addInspection(inspection: Inspection) {
        safeLaunch { repository.insertInspection(inspection) }
    }

    fun updateInspection(inspection: Inspection) {
        safeLaunch { repository.updateInspection(inspection) }
    }

    fun deleteInspection(inspection: Inspection) {
        safeLaunch { repository.deleteInspection(inspection) }
    }

    fun addTreatment(treatment: Treatment) {
        safeLaunch { repository.insertTreatment(treatment) }
    }

    fun updateTreatment(treatment: Treatment) {
        safeLaunch { repository.updateTreatment(treatment) }
    }

    fun deleteTreatment(treatment: Treatment) {
        safeLaunch { repository.deleteTreatment(treatment) }
    }

    fun insertRace(race: BeeRaceInfo) {
        safeLaunch { repository.insertRace(race) }
    }

    fun updateRace(race: BeeRaceInfo) {
        safeLaunch { repository.updateRace(race) }
    }

    fun deleteRace(race: BeeRaceInfo) {
        safeLaunch { repository.deleteRace(race) }
    }
}
