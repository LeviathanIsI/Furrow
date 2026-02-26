package com.furrow.app.ui.animals

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.local.entity.Animal
import com.furrow.app.data.local.entity.AnimalBreedInfo
import com.furrow.app.data.local.entity.BreedingRecord
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.data.local.entity.FeedLog
import com.furrow.app.data.local.entity.FiberLog
import com.furrow.app.data.local.entity.HealthRecord
import com.furrow.app.data.local.entity.MilkLog
import com.furrow.app.data.local.entity.ProcessingRecord
import com.furrow.app.data.local.entity.WeightLog
import com.furrow.app.data.repository.AnimalRepository
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.ui.FurrowViewModel
import com.furrow.app.util.WidgetRefreshUtil
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnimalViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AnimalRepository,
    private val userProfileRepository: UserProfileRepository,
    savedStateHandle: SavedStateHandle,
) : FurrowViewModel() {

    private val animalId: Long? = savedStateHandle.get<Long>("animalId")

    internal val zone: ZoneId = ZoneId.systemDefault()
    private val today = LocalDate.now(zone)
    private val weekStart = today.minusDays(6)

    private val todayStartMillis = today.atStartOfDay(zone).toInstant().toEpochMilli()
    private val todayEndMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
    private val weekStartMillis = weekStart.atStartOfDay(zone).toInstant().toEpochMilli()

    // -- Species filter --

    val selectedSpecies = MutableStateFlow<String?>(null)

    val activeAnimals: StateFlow<List<Animal>> = repository.getActiveAnimals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredAnimals: StateFlow<List<Animal>?> = combine(
        activeAnimals,
        selectedSpecies,
    ) { animals, species ->
        if (species == null) animals else animals.filter { it.species == species }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val speciesList: StateFlow<List<String>> = activeAnimals.map { animals ->
        animals.map { it.species }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Breed reference data --

    val breedsBySpecies: StateFlow<List<AnimalBreedInfo>> = selectedSpecies.flatMapLatest { species ->
        if (species != null) repository.getBreedsBySpecies(species) else repository.getAllBreeds()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getBreedsForSpecies(species: String) = repository.getBreedsBySpecies(species)

    // -- Summary stats --

    private val now = Instant.now().toEpochMilli()

    val upcomingBirths: StateFlow<List<BreedingRecord>> = repository.getUpcomingBirths(now)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeWithdrawals: StateFlow<List<HealthRecord>> = repository.getActiveWithdrawals(now)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Egg tracking (merged from PoultryViewModel) --

    val allChickenBreeds: StateFlow<List<AnimalBreedInfo>> = repository.getChickenBreeds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chickenBreedInfoMap: StateFlow<Map<String, AnimalBreedInfo>> = allChickenBreeds.map { breeds ->
        breeds.associateBy { it.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val activeChickens: StateFlow<List<Animal>> = repository.getActiveChickens()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chickenCount: StateFlow<Int> = activeChickens.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val eggLogs: StateFlow<List<EggLog>> = repository.getAllEggLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    val layRatePercent: StateFlow<Int?> = combine(todayEggCount, chickenCount) { eggs, birds ->
        if (birds > 0) (eggs * 100) / birds else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val dailyAvg: StateFlow<Double> = weeklyTotal.map { it / 7.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val expectedWeeklyEggs: StateFlow<Double> =
        combine(activeChickens, chickenBreedInfoMap) { animals, breeds ->
            animals.sumOf { animal ->
                val breed = breeds[animal.breed]
                (breed?.eggsPerYear ?: 0) / 52.0
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // -- Detail screen (driven by animalId nav arg) --

    val selectedAnimal: StateFlow<Animal?> = animalId?.let {
        repository.getAnimalById(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    } ?: MutableStateFlow(null)

    val healthRecords: StateFlow<List<HealthRecord>> = animalId?.let {
        repository.getHealthRecordsForAnimal(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val breedingRecords: StateFlow<List<BreedingRecord>> = animalId?.let {
        repository.getBreedingRecordsForAnimal(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val milkLogs: StateFlow<List<MilkLog>> = animalId?.let {
        repository.getMilkLogsForAnimal(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val fiberLogs: StateFlow<List<FiberLog>> = animalId?.let {
        repository.getFiberLogsForAnimal(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val weightLogs: StateFlow<List<WeightLog>> = animalId?.let {
        repository.getWeightLogsForAnimal(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val feedLogs: StateFlow<List<FeedLog>> = animalId?.let {
        repository.getFeedLogsForAnimal(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val processingRecords: StateFlow<List<ProcessingRecord>> = animalId?.let {
        repository.getProcessingRecordsForAnimal(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    // -- Lookups for edit mode --

    fun getAnimalById(id: Long) = repository.getAnimalById(id)
    fun getHealthRecordById(id: Long) = repository.getHealthRecordById(id)
    fun getBreedingRecordById(id: Long) = repository.getBreedingRecordById(id)
    fun getMilkLogById(id: Long) = repository.getMilkLogById(id)
    fun getFiberLogById(id: Long) = repository.getFiberLogById(id)
    fun getWeightLogById(id: Long) = repository.getWeightLogById(id)
    fun getFeedLogById(id: Long) = repository.getFeedLogById(id)
    fun getProcessingRecordById(id: Long) = repository.getProcessingRecordById(id)
    fun getEggLogById(id: Long) = repository.getEggLogById(id)

    // -- Actions --

    fun addAnimal(animal: Animal) { safeLaunchWithSuccess("Animal saved") { repository.insertAnimal(animal) } }
    fun updateAnimal(animal: Animal) { safeLaunchWithSuccess("Animal updated") { repository.updateAnimal(animal) } }
    fun deleteAnimal(animal: Animal) { safeLaunch { repository.deleteAnimal(animal) } }

    fun addHealthRecord(record: HealthRecord) { safeLaunchWithSuccess("Health record saved") { repository.insertHealthRecord(record) } }
    fun updateHealthRecord(record: HealthRecord) { safeLaunchWithSuccess("Health record updated") { repository.updateHealthRecord(record) } }
    fun deleteHealthRecord(record: HealthRecord) { safeLaunch { repository.deleteHealthRecord(record) } }

    fun addBreedingRecord(record: BreedingRecord) { safeLaunchWithSuccess("Breeding record saved") { repository.insertBreedingRecord(record) } }
    fun updateBreedingRecord(record: BreedingRecord) { safeLaunchWithSuccess("Breeding record updated") { repository.updateBreedingRecord(record) } }
    fun deleteBreedingRecord(record: BreedingRecord) { safeLaunch { repository.deleteBreedingRecord(record) } }

    fun addMilkLog(log: MilkLog) { safeLaunchWithSuccess("Milk log saved") { repository.insertMilkLog(log) } }
    fun updateMilkLog(log: MilkLog) { safeLaunchWithSuccess("Milk log updated") { repository.updateMilkLog(log) } }
    fun deleteMilkLog(log: MilkLog) { safeLaunch { repository.deleteMilkLog(log) } }

    fun addFiberLog(log: FiberLog) { safeLaunchWithSuccess("Fiber log saved") { repository.insertFiberLog(log) } }
    fun updateFiberLog(log: FiberLog) { safeLaunchWithSuccess("Fiber log updated") { repository.updateFiberLog(log) } }
    fun deleteFiberLog(log: FiberLog) { safeLaunch { repository.deleteFiberLog(log) } }

    fun addWeightLog(log: WeightLog) { safeLaunchWithSuccess("Weight recorded") { repository.insertWeightLog(log) } }
    fun updateWeightLog(log: WeightLog) { safeLaunchWithSuccess("Weight updated") { repository.updateWeightLog(log) } }
    fun deleteWeightLog(log: WeightLog) { safeLaunch { repository.deleteWeightLog(log) } }

    fun addFeedLog(log: FeedLog) { safeLaunchWithSuccess("Feed log saved") { repository.insertFeedLog(log) } }
    fun updateFeedLog(log: FeedLog) { safeLaunchWithSuccess("Feed log updated") { repository.updateFeedLog(log) } }
    fun deleteFeedLog(log: FeedLog) { safeLaunch { repository.deleteFeedLog(log) } }

    fun addProcessingRecord(record: ProcessingRecord) { safeLaunchWithSuccess("Processing record saved") { repository.insertProcessingRecord(record) } }
    fun updateProcessingRecord(record: ProcessingRecord) { safeLaunchWithSuccess("Processing record updated") { repository.updateProcessingRecord(record) } }
    fun deleteProcessingRecord(record: ProcessingRecord) { safeLaunch { repository.deleteProcessingRecord(record) } }

    fun addEggLog(eggLog: EggLog) {
        safeLaunchWithSuccess("Egg log saved") {
            repository.insertEggLog(eggLog)
            WidgetRefreshUtil.refresh(context)
        }
    }

    fun updateEggLog(eggLog: EggLog) {
        safeLaunchWithSuccess("Egg log updated") {
            repository.updateEggLog(eggLog)
            WidgetRefreshUtil.refresh(context)
        }
    }

    fun deleteEggLog(eggLog: EggLog) {
        safeLaunch {
            repository.deleteEggLog(eggLog)
            WidgetRefreshUtil.refresh(context)
        }
    }

    fun insertBreed(breed: AnimalBreedInfo) { safeLaunchWithSuccess("Breed saved") { repository.insertBreed(breed) } }
    fun updateBreed(breed: AnimalBreedInfo) { safeLaunchWithSuccess("Breed updated") { repository.updateBreed(breed) } }
    fun deleteBreed(breed: AnimalBreedInfo) { safeLaunch { repository.deleteBreed(breed) } }

    fun setSpeciesFilter(species: String?) { selectedSpecies.value = species }
}
