package com.furrow.app.ui.preservation

import com.furrow.app.data.local.entity.CanningBatch
import com.furrow.app.data.local.entity.DehydratingBatch
import com.furrow.app.data.local.entity.FermentingBatch
import com.furrow.app.data.local.entity.FreezingBatch
import com.furrow.app.data.local.entity.PantryItem
import com.furrow.app.data.local.entity.SmokingCuringBatch
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.repository.PreservationRepository
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
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class PreservationViewModel @Inject constructor(
    private val repository: PreservationRepository,
    private val userProfileRepository: UserProfileRepository,
) : FurrowViewModel() {

    internal val zone: ZoneId = runBlocking {
        DateUtil.profileZone(userProfileRepository.getProfile().firstOrNull())
    }

    // -- Canning --
    val canningBatches: StateFlow<List<CanningBatch>> = repository.getAllCanningBatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Dehydrating --
    val dehydratingBatches: StateFlow<List<DehydratingBatch>> = repository.getAllDehydratingBatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Fermenting --
    val fermentingBatches: StateFlow<List<FermentingBatch>> = repository.getAllFermentingBatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeFermenting: StateFlow<List<FermentingBatch>> = repository.getActiveFermentingBatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Freezing --
    val freezingBatches: StateFlow<List<FreezingBatch>> = repository.getAllFreezingBatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Smoking/Curing --
    val smokingCuringBatches: StateFlow<List<SmokingCuringBatch>> = repository.getAllSmokingCuringBatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Pantry --
    val pantryItems: StateFlow<List<PantryItem>> = repository.getAllPantryItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inStockPantry: StateFlow<List<PantryItem>> = repository.getInStockPantryItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val thirtyDaysFromNow = Instant.now().atZone(zone)
        .plusDays(30).toInstant().toEpochMilli()

    val expiringSoon: StateFlow<List<PantryItem>> = repository.getExpiringSoonPantryItems(thirtyDaysFromNow)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Total batch count --
    val totalBatches: StateFlow<Int> = kotlinx.coroutines.flow.combine(
        canningBatches, dehydratingBatches, fermentingBatches, freezingBatches, smokingCuringBatches,
    ) { c, d, f, fr, s -> c.size + d.size + f.size + fr.size + s.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // -- Lookups for edit mode --
    fun getCanningBatchById(id: Long) = repository.getCanningBatchById(id)
    fun getDehydratingBatchById(id: Long) = repository.getDehydratingBatchById(id)
    fun getFermentingBatchById(id: Long) = repository.getFermentingBatchById(id)
    fun getFreezingBatchById(id: Long) = repository.getFreezingBatchById(id)
    fun getSmokingCuringBatchById(id: Long) = repository.getSmokingCuringBatchById(id)
    fun getPantryItemById(id: Long) = repository.getPantryItemById(id)

    // -- Actions --
    fun addCanningBatch(batch: CanningBatch) { safeLaunch { repository.insertCanningBatch(batch) } }
    fun updateCanningBatch(batch: CanningBatch) { safeLaunch { repository.updateCanningBatch(batch) } }
    fun deleteCanningBatch(batch: CanningBatch) { safeLaunch { repository.deleteCanningBatch(batch) } }

    fun addDehydratingBatch(batch: DehydratingBatch) { safeLaunch { repository.insertDehydratingBatch(batch) } }
    fun updateDehydratingBatch(batch: DehydratingBatch) { safeLaunch { repository.updateDehydratingBatch(batch) } }
    fun deleteDehydratingBatch(batch: DehydratingBatch) { safeLaunch { repository.deleteDehydratingBatch(batch) } }

    fun addFermentingBatch(batch: FermentingBatch) { safeLaunch { repository.insertFermentingBatch(batch) } }
    fun updateFermentingBatch(batch: FermentingBatch) { safeLaunch { repository.updateFermentingBatch(batch) } }
    fun deleteFermentingBatch(batch: FermentingBatch) { safeLaunch { repository.deleteFermentingBatch(batch) } }

    fun addFreezingBatch(batch: FreezingBatch) { safeLaunch { repository.insertFreezingBatch(batch) } }
    fun updateFreezingBatch(batch: FreezingBatch) { safeLaunch { repository.updateFreezingBatch(batch) } }
    fun deleteFreezingBatch(batch: FreezingBatch) { safeLaunch { repository.deleteFreezingBatch(batch) } }

    fun addSmokingCuringBatch(batch: SmokingCuringBatch) { safeLaunch { repository.insertSmokingCuringBatch(batch) } }
    fun updateSmokingCuringBatch(batch: SmokingCuringBatch) { safeLaunch { repository.updateSmokingCuringBatch(batch) } }
    fun deleteSmokingCuringBatch(batch: SmokingCuringBatch) { safeLaunch { repository.deleteSmokingCuringBatch(batch) } }

    fun addPantryItem(item: PantryItem) { safeLaunch { repository.insertPantryItem(item) } }
    fun updatePantryItem(item: PantryItem) { safeLaunch { repository.updatePantryItem(item) } }
    fun deletePantryItem(item: PantryItem) { safeLaunch { repository.deletePantryItem(item) } }
}
