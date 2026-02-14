package com.furrow.app.ui.poultry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.local.entity.Chicken
import com.furrow.app.data.local.entity.ChickenBreedInfo
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.data.repository.PoultryRepository
import com.furrow.app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class DailyEggCount(
    val dayLabel: String,
    val count: Int,
    val isToday: Boolean,
)

@HiltViewModel
class PoultryViewModel @Inject constructor(
    private val repository: PoultryRepository,
    private val userProfileRepository: UserProfileRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val chickenId: Long? = savedStateHandle.get<Long>("chickenId")

    private val zone = ZoneId.systemDefault()
    private val today = LocalDate.now(zone)
    private val weekStart = today.minusDays(6)

    private val todayStartMillis = today.atStartOfDay(zone).toInstant().toEpochMilli()
    private val todayEndMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
    private val weekStartMillis = weekStart.atStartOfDay(zone).toInstant().toEpochMilli()

    // -- Breed reference data --

    val allBreeds: StateFlow<List<ChickenBreedInfo>> = repository.getAllBreeds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val breedInfoMap: StateFlow<Map<String, ChickenBreedInfo>> = allBreeds.map { breeds ->
        breeds.associateBy { it.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val userProfile: StateFlow<UserProfile?> = userProfileRepository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // -- Flock list --

    val activeChickens: StateFlow<List<Chicken>> = repository.getActiveChickens()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Egg logs --

    val eggLogs: StateFlow<List<EggLog>> = repository.getAllEggLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Summary stats --

    val todayEggCount: StateFlow<Int> = repository.getEggCountForDateRange(todayStartMillis, todayEndMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weeklyTotal: StateFlow<Int> = repository.getEggCountForDateRange(weekStartMillis, todayEndMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weeklyEggLogs: StateFlow<List<EggLog>> = repository.getEggLogsForDateRange(weekStartMillis, todayEndMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyEggCounts: StateFlow<List<DailyEggCount>> = weeklyEggLogs.map { logs ->
        (0L..6L).map { offset ->
            val date = weekStart.plusDays(offset)
            val dayStartMillis = date.atStartOfDay(zone).toInstant().toEpochMilli()
            val dayEndMillis = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
            val count = logs.filter { it.date in dayStartMillis..dayEndMillis }.sumOf { it.count }
            DailyEggCount(
                dayLabel = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                count = count,
                isToday = date == today,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flockSize: StateFlow<Int> = activeChickens.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val layRatePercent: StateFlow<Int?> = combine(todayEggCount, flockSize) { eggs, birds ->
        if (birds > 0) (eggs * 100) / birds else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val dailyAvg: StateFlow<Double> = weeklyTotal.map { it / 7.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // -- Expected weekly production from breed data --

    val expectedWeeklyEggs: StateFlow<Double> =
        combine(activeChickens, breedInfoMap) { chickens, breeds ->
            chickens.sumOf { chicken ->
                val breed = breeds[chicken.breed]
                (breed?.eggsPerYear ?: 0) / 52.0
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // -- Chicken detail (driven by chickenId nav arg) --

    val selectedChicken: StateFlow<Chicken?> = chickenId?.let {
        repository.getChickenById(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    } ?: MutableStateFlow(null)

    // -- Lookups for edit mode --

    fun getEggLogById(id: Long) = repository.getEggLogById(id)

    // -- Actions --

    fun addEggLog(eggLog: EggLog) {
        viewModelScope.launch { repository.insertEggLog(eggLog) }
    }

    fun addChicken(chicken: Chicken) {
        viewModelScope.launch { repository.insertChicken(chicken) }
    }

    fun updateChicken(chicken: Chicken) {
        viewModelScope.launch { repository.updateChicken(chicken) }
    }

    fun deleteChicken(chicken: Chicken) {
        viewModelScope.launch { repository.deleteChicken(chicken) }
    }

    fun updateEggLog(eggLog: EggLog) {
        viewModelScope.launch { repository.updateEggLog(eggLog) }
    }

    fun deleteEggLog(eggLog: EggLog) {
        viewModelScope.launch { repository.deleteEggLog(eggLog) }
    }

    fun insertBreed(breed: ChickenBreedInfo) {
        viewModelScope.launch { repository.insertBreed(breed) }
    }

    fun updateBreed(breed: ChickenBreedInfo) {
        viewModelScope.launch { repository.updateBreed(breed) }
    }

    fun deleteBreed(breed: ChickenBreedInfo) {
        viewModelScope.launch { repository.deleteBreed(breed) }
    }
}
