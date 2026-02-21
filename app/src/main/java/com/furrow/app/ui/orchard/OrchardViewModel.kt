package com.furrow.app.ui.orchard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.local.entity.BloomRecord
import com.furrow.app.data.local.entity.ChillHoursLog
import com.furrow.app.data.local.entity.GraftingLog
import com.furrow.app.data.local.entity.OrchardHarvest
import com.furrow.app.data.local.entity.OrchardPlant
import com.furrow.app.data.local.entity.PruningLog
import com.furrow.app.data.local.entity.SprayLog
import com.furrow.app.data.repository.OrchardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrchardViewModel @Inject constructor(
    private val repository: OrchardRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val plantId: Long? = savedStateHandle.get<Long>("plantId")

    // -- Category filter --

    val selectedCategory = MutableStateFlow<String?>(null)

    val allPlants: StateFlow<List<OrchardPlant>> = repository.getAllPlants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredPlants: StateFlow<List<OrchardPlant>> = combine(
        allPlants,
        selectedCategory,
    ) { plants, category ->
        if (category == null) plants else plants.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<String>> = allPlants.map { plants ->
        plants.map { it.category }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Harvest summary --

    val allHarvests: StateFlow<List<OrchardHarvest>> = repository.getAllHarvests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalHarvestLbs: StateFlow<Double> = allHarvests.map { harvests ->
        harvests.sumOf { it.yieldLbs ?: 0.0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // -- Chill hours --

    val chillHoursLogs: StateFlow<List<ChillHoursLog>> = repository.getAllChillHoursLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Detail screen (driven by plantId nav arg) --

    val selectedPlant: StateFlow<OrchardPlant?> = plantId?.let {
        repository.getPlantById(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    } ?: MutableStateFlow(null)

    val harvests: StateFlow<List<OrchardHarvest>> = plantId?.let {
        repository.getHarvestsForPlant(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val pruningLogs: StateFlow<List<PruningLog>> = plantId?.let {
        repository.getPruningLogsForPlant(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val sprayLogs: StateFlow<List<SprayLog>> = plantId?.let {
        repository.getSprayLogsForPlant(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val bloomRecords: StateFlow<List<BloomRecord>> = plantId?.let {
        repository.getBloomRecordsForPlant(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    val graftingLogs: StateFlow<List<GraftingLog>> = plantId?.let {
        repository.getGraftingLogsForPlant(it)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } ?: MutableStateFlow(emptyList())

    // -- Lookups for edit mode --

    fun getPlantById(id: Long) = repository.getPlantById(id)
    fun getHarvestById(id: Long) = repository.getHarvestById(id)
    fun getPruningLogById(id: Long) = repository.getPruningLogById(id)
    fun getSprayLogById(id: Long) = repository.getSprayLogById(id)
    fun getBloomRecordById(id: Long) = repository.getBloomRecordById(id)
    fun getGraftingLogById(id: Long) = repository.getGraftingLogById(id)
    fun getChillHoursLogById(id: Long) = repository.getChillHoursLogById(id)

    // -- Actions --

    fun addPlant(plant: OrchardPlant) { viewModelScope.launch { repository.insertPlant(plant) } }
    fun updatePlant(plant: OrchardPlant) { viewModelScope.launch { repository.updatePlant(plant) } }
    fun deletePlant(plant: OrchardPlant) { viewModelScope.launch { repository.deletePlant(plant) } }

    fun addHarvest(harvest: OrchardHarvest) { viewModelScope.launch { repository.insertHarvest(harvest) } }
    fun updateHarvest(harvest: OrchardHarvest) { viewModelScope.launch { repository.updateHarvest(harvest) } }
    fun deleteHarvest(harvest: OrchardHarvest) { viewModelScope.launch { repository.deleteHarvest(harvest) } }

    fun addPruningLog(log: PruningLog) { viewModelScope.launch { repository.insertPruningLog(log) } }
    fun updatePruningLog(log: PruningLog) { viewModelScope.launch { repository.updatePruningLog(log) } }
    fun deletePruningLog(log: PruningLog) { viewModelScope.launch { repository.deletePruningLog(log) } }

    fun addSprayLog(log: SprayLog) { viewModelScope.launch { repository.insertSprayLog(log) } }
    fun updateSprayLog(log: SprayLog) { viewModelScope.launch { repository.updateSprayLog(log) } }
    fun deleteSprayLog(log: SprayLog) { viewModelScope.launch { repository.deleteSprayLog(log) } }

    fun addBloomRecord(record: BloomRecord) { viewModelScope.launch { repository.insertBloomRecord(record) } }
    fun updateBloomRecord(record: BloomRecord) { viewModelScope.launch { repository.updateBloomRecord(record) } }
    fun deleteBloomRecord(record: BloomRecord) { viewModelScope.launch { repository.deleteBloomRecord(record) } }

    fun addGraftingLog(log: GraftingLog) { viewModelScope.launch { repository.insertGraftingLog(log) } }
    fun updateGraftingLog(log: GraftingLog) { viewModelScope.launch { repository.updateGraftingLog(log) } }
    fun deleteGraftingLog(log: GraftingLog) { viewModelScope.launch { repository.deleteGraftingLog(log) } }

    fun addChillHoursLog(log: ChillHoursLog) { viewModelScope.launch { repository.insertChillHoursLog(log) } }
    fun updateChillHoursLog(log: ChillHoursLog) { viewModelScope.launch { repository.updateChillHoursLog(log) } }
    fun deleteChillHoursLog(log: ChillHoursLog) { viewModelScope.launch { repository.deleteChillHoursLog(log) } }

    fun setCategoryFilter(category: String?) { selectedCategory.value = category }
}
