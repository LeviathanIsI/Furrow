package com.furrow.app.ui.garden

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.furrow.app.ui.FurrowViewModel
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.PlantVariety
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.data.repository.GardenRepository
import com.furrow.app.data.repository.PlantRepository
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.time.ZoneId
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    private val gardenRepository: GardenRepository,
    private val plantRepository: PlantRepository,
    private val userProfileRepository: UserProfileRepository,
    savedStateHandle: SavedStateHandle,
) : FurrowViewModel() {

    private val plantingId: Long = checkNotNull(savedStateHandle.get<Long>("plantingId"))

    val zone: ZoneId = runBlocking {
        DateUtil.profileZone(userProfileRepository.getProfile().firstOrNull())
    }

    val planting: StateFlow<Planting?> = gardenRepository.getPlantingById(plantingId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val plant: StateFlow<PlantInfo?> = planting.flatMapLatest { p ->
        if (p != null) plantRepository.getPlantByName(p.plantName) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val varieties: StateFlow<List<PlantVariety>> = plant.flatMapLatest { p ->
        if (p != null) plantRepository.getVarietiesForPlant(p.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val plantingWindows: StateFlow<List<PlantingWindow>> = plant.flatMapLatest { p ->
        if (p != null) {
            userProfileRepository.getProfile().flatMapLatest { profile ->
                if (profile != null) {
                    plantRepository.getWindowsForZoneGroup(profile.zoneGroup).map { windows ->
                        windows.filter { it.plantName == p.name }
                    }
                } else {
                    flowOf(emptyList())
                }
            }
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
