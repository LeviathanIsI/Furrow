package com.furrow.app.ui.land

import com.furrow.app.data.local.entity.CompostBin
import com.furrow.app.data.local.entity.CompostInputLog
import com.furrow.app.data.local.entity.Fence
import com.furrow.app.data.local.entity.FenceMaintenanceLog
import com.furrow.app.data.local.entity.Paddock
import com.furrow.app.data.local.entity.PastureRotationLog
import com.furrow.app.data.local.entity.Property
import com.furrow.app.data.local.entity.SoilTest
import com.furrow.app.data.local.entity.Structure
import com.furrow.app.data.local.entity.StructureMaintenanceLog
import com.furrow.app.data.local.entity.WaterSource
import com.furrow.app.data.local.entity.WeatherLog
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.repository.LandRepository
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.ui.FurrowViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class LandViewModel @Inject constructor(
    private val repository: LandRepository,
    private val userProfileRepository: UserProfileRepository,
) : FurrowViewModel() {

    internal val zone: ZoneId = ZoneId.systemDefault()

    val properties: StateFlow<List<Property>?> = repository.getAllProperties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val totalAcreage: StateFlow<Double> = properties.map { list ->
        list.orEmpty().sumOf { it.totalAcreage ?: 0.0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val fences: StateFlow<List<Fence>> = repository.getAllFences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val structures: StateFlow<List<Structure>> = repository.getAllStructures()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paddocks: StateFlow<List<Paddock>> = repository.getAllPaddocks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val waterSources: StateFlow<List<WaterSource>> = repository.getAllWaterSources()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val compostBins: StateFlow<List<CompostBin>> = repository.getAllCompostBins()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val soilTests: StateFlow<List<SoilTest>> = repository.getAllSoilTests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weatherLogs: StateFlow<List<WeatherLog>> = repository.getAllWeatherLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Lookups for edit mode --
    fun getPropertyById(id: Long) = repository.getPropertyById(id)
    fun getFenceById(id: Long) = repository.getFenceById(id)
    fun getStructureById(id: Long) = repository.getStructureById(id)
    fun getPaddockById(id: Long) = repository.getPaddockById(id)
    fun getWaterSourceById(id: Long) = repository.getWaterSourceById(id)
    fun getCompostBinById(id: Long) = repository.getCompostBinById(id)
    fun getSoilTestById(id: Long) = repository.getSoilTestById(id)
    fun getWeatherLogById(id: Long) = repository.getWeatherLogById(id)

    // -- Actions --
    fun addProperty(p: Property) { safeLaunchWithSuccess("Property saved") { repository.insertProperty(p) } }
    fun updateProperty(p: Property) { safeLaunchWithSuccess("Property updated") { repository.updateProperty(p) } }
    fun deleteProperty(p: Property) { safeLaunch { repository.deleteProperty(p) } }

    fun addFence(f: Fence) { safeLaunchWithSuccess("Fence saved") { repository.insertFence(f) } }
    fun updateFence(f: Fence) { safeLaunchWithSuccess("Fence updated") { repository.updateFence(f) } }
    fun deleteFence(f: Fence) { safeLaunch { repository.deleteFence(f) } }

    fun addStructure(s: Structure) { safeLaunchWithSuccess("Structure saved") { repository.insertStructure(s) } }
    fun updateStructure(s: Structure) { safeLaunchWithSuccess("Structure updated") { repository.updateStructure(s) } }
    fun deleteStructure(s: Structure) { safeLaunch { repository.deleteStructure(s) } }

    fun addPaddock(p: Paddock) { safeLaunchWithSuccess("Paddock saved") { repository.insertPaddock(p) } }
    fun updatePaddock(p: Paddock) { safeLaunchWithSuccess("Paddock updated") { repository.updatePaddock(p) } }
    fun deletePaddock(p: Paddock) { safeLaunch { repository.deletePaddock(p) } }

    fun addWaterSource(w: WaterSource) { safeLaunchWithSuccess("Water source saved") { repository.insertWaterSource(w) } }
    fun updateWaterSource(w: WaterSource) { safeLaunchWithSuccess("Water source updated") { repository.updateWaterSource(w) } }
    fun deleteWaterSource(w: WaterSource) { safeLaunch { repository.deleteWaterSource(w) } }

    fun addCompostBin(b: CompostBin) { safeLaunchWithSuccess("Compost bin saved") { repository.insertCompostBin(b) } }
    fun updateCompostBin(b: CompostBin) { safeLaunchWithSuccess("Compost bin updated") { repository.updateCompostBin(b) } }
    fun deleteCompostBin(b: CompostBin) { safeLaunch { repository.deleteCompostBin(b) } }

    fun addSoilTest(s: SoilTest) { safeLaunchWithSuccess("Soil test saved") { repository.insertSoilTest(s) } }
    fun updateSoilTest(s: SoilTest) { safeLaunchWithSuccess("Soil test updated") { repository.updateSoilTest(s) } }
    fun deleteSoilTest(s: SoilTest) { safeLaunch { repository.deleteSoilTest(s) } }

    fun addWeatherLog(w: WeatherLog) { safeLaunchWithSuccess("Weather log saved") { repository.insertWeatherLog(w) } }
    fun updateWeatherLog(w: WeatherLog) { safeLaunchWithSuccess("Weather log updated") { repository.updateWeatherLog(w) } }
    fun deleteWeatherLog(w: WeatherLog) { safeLaunch { repository.deleteWeatherLog(w) } }
}
