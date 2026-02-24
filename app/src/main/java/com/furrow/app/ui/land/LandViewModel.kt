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
import com.furrow.app.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class LandViewModel @Inject constructor(
    private val repository: LandRepository,
    private val userProfileRepository: UserProfileRepository,
) : FurrowViewModel() {

    internal val zone: ZoneId = runBlocking {
        DateUtil.profileZone(userProfileRepository.getProfile().firstOrNull())
    }

    val properties: StateFlow<List<Property>> = repository.getAllProperties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalAcreage: StateFlow<Double> = properties.map { list ->
        list.sumOf { it.totalAcreage ?: 0.0 }
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
    fun addProperty(p: Property) { safeLaunch { repository.insertProperty(p) } }
    fun updateProperty(p: Property) { safeLaunch { repository.updateProperty(p) } }
    fun deleteProperty(p: Property) { safeLaunch { repository.deleteProperty(p) } }

    fun addFence(f: Fence) { safeLaunch { repository.insertFence(f) } }
    fun updateFence(f: Fence) { safeLaunch { repository.updateFence(f) } }
    fun deleteFence(f: Fence) { safeLaunch { repository.deleteFence(f) } }

    fun addStructure(s: Structure) { safeLaunch { repository.insertStructure(s) } }
    fun updateStructure(s: Structure) { safeLaunch { repository.updateStructure(s) } }
    fun deleteStructure(s: Structure) { safeLaunch { repository.deleteStructure(s) } }

    fun addPaddock(p: Paddock) { safeLaunch { repository.insertPaddock(p) } }
    fun updatePaddock(p: Paddock) { safeLaunch { repository.updatePaddock(p) } }
    fun deletePaddock(p: Paddock) { safeLaunch { repository.deletePaddock(p) } }

    fun addWaterSource(w: WaterSource) { safeLaunch { repository.insertWaterSource(w) } }
    fun updateWaterSource(w: WaterSource) { safeLaunch { repository.updateWaterSource(w) } }
    fun deleteWaterSource(w: WaterSource) { safeLaunch { repository.deleteWaterSource(w) } }

    fun addCompostBin(b: CompostBin) { safeLaunch { repository.insertCompostBin(b) } }
    fun updateCompostBin(b: CompostBin) { safeLaunch { repository.updateCompostBin(b) } }
    fun deleteCompostBin(b: CompostBin) { safeLaunch { repository.deleteCompostBin(b) } }

    fun addSoilTest(s: SoilTest) { safeLaunch { repository.insertSoilTest(s) } }
    fun updateSoilTest(s: SoilTest) { safeLaunch { repository.updateSoilTest(s) } }
    fun deleteSoilTest(s: SoilTest) { safeLaunch { repository.deleteSoilTest(s) } }

    fun addWeatherLog(w: WeatherLog) { safeLaunch { repository.insertWeatherLog(w) } }
    fun updateWeatherLog(w: WeatherLog) { safeLaunch { repository.updateWeatherLog(w) } }
    fun deleteWeatherLog(w: WeatherLog) { safeLaunch { repository.deleteWeatherLog(w) } }
}
