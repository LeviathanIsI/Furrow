package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.CanningBatchDao
import com.furrow.app.data.local.dao.DehydratingBatchDao
import com.furrow.app.data.local.dao.FermentingBatchDao
import com.furrow.app.data.local.dao.FreezingBatchDao
import com.furrow.app.data.local.dao.PantryItemDao
import com.furrow.app.data.local.dao.SmokingCuringBatchDao
import com.furrow.app.data.local.entity.CanningBatch
import com.furrow.app.data.local.entity.DehydratingBatch
import com.furrow.app.data.local.entity.FermentingBatch
import com.furrow.app.data.local.entity.FreezingBatch
import com.furrow.app.data.local.entity.PantryItem
import com.furrow.app.data.local.entity.SmokingCuringBatch
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreservationRepository @Inject constructor(
    private val canningBatchDao: CanningBatchDao,
    private val dehydratingBatchDao: DehydratingBatchDao,
    private val fermentingBatchDao: FermentingBatchDao,
    private val freezingBatchDao: FreezingBatchDao,
    private val smokingCuringBatchDao: SmokingCuringBatchDao,
    private val pantryItemDao: PantryItemDao,
) {
    // --- Canning Batches ---

    fun getAllCanningBatches(): Flow<List<CanningBatch>> = canningBatchDao.getAll()

    fun getCanningBatchById(id: Long): Flow<CanningBatch?> = canningBatchDao.getById(id)

    suspend fun insertCanningBatch(batch: CanningBatch): Long = canningBatchDao.insert(batch)

    suspend fun updateCanningBatch(batch: CanningBatch) = canningBatchDao.update(batch)

    suspend fun deleteCanningBatch(batch: CanningBatch) = canningBatchDao.delete(batch)

    // --- Dehydrating Batches ---

    fun getAllDehydratingBatches(): Flow<List<DehydratingBatch>> = dehydratingBatchDao.getAll()

    fun getDehydratingBatchById(id: Long): Flow<DehydratingBatch?> = dehydratingBatchDao.getById(id)

    suspend fun insertDehydratingBatch(batch: DehydratingBatch): Long = dehydratingBatchDao.insert(batch)

    suspend fun updateDehydratingBatch(batch: DehydratingBatch) = dehydratingBatchDao.update(batch)

    suspend fun deleteDehydratingBatch(batch: DehydratingBatch) = dehydratingBatchDao.delete(batch)

    // --- Fermenting Batches ---

    fun getAllFermentingBatches(): Flow<List<FermentingBatch>> = fermentingBatchDao.getAll()

    fun getFermentingBatchById(id: Long): Flow<FermentingBatch?> = fermentingBatchDao.getById(id)

    fun getActiveFermentingBatches(): Flow<List<FermentingBatch>> = fermentingBatchDao.getActive()

    suspend fun insertFermentingBatch(batch: FermentingBatch): Long = fermentingBatchDao.insert(batch)

    suspend fun updateFermentingBatch(batch: FermentingBatch) = fermentingBatchDao.update(batch)

    suspend fun deleteFermentingBatch(batch: FermentingBatch) = fermentingBatchDao.delete(batch)

    // --- Freezing Batches ---

    fun getAllFreezingBatches(): Flow<List<FreezingBatch>> = freezingBatchDao.getAll()

    fun getFreezingBatchById(id: Long): Flow<FreezingBatch?> = freezingBatchDao.getById(id)

    suspend fun insertFreezingBatch(batch: FreezingBatch): Long = freezingBatchDao.insert(batch)

    suspend fun updateFreezingBatch(batch: FreezingBatch) = freezingBatchDao.update(batch)

    suspend fun deleteFreezingBatch(batch: FreezingBatch) = freezingBatchDao.delete(batch)

    // --- Smoking & Curing Batches ---

    fun getAllSmokingCuringBatches(): Flow<List<SmokingCuringBatch>> = smokingCuringBatchDao.getAll()

    fun getSmokingCuringBatchById(id: Long): Flow<SmokingCuringBatch?> = smokingCuringBatchDao.getById(id)

    suspend fun insertSmokingCuringBatch(batch: SmokingCuringBatch): Long = smokingCuringBatchDao.insert(batch)

    suspend fun updateSmokingCuringBatch(batch: SmokingCuringBatch) = smokingCuringBatchDao.update(batch)

    suspend fun deleteSmokingCuringBatch(batch: SmokingCuringBatch) = smokingCuringBatchDao.delete(batch)

    // --- Pantry Items ---

    fun getAllPantryItems(): Flow<List<PantryItem>> = pantryItemDao.getAll()

    fun getPantryItemById(id: Long): Flow<PantryItem?> = pantryItemDao.getById(id)

    fun getInStockPantryItems(): Flow<List<PantryItem>> = pantryItemDao.getInStock()

    fun getPantryItemsByCategory(category: String): Flow<List<PantryItem>> =
        pantryItemDao.getByCategory(category)

    fun getExpiringSoonPantryItems(beforeDate: Long): Flow<List<PantryItem>> =
        pantryItemDao.getExpiringSoon(beforeDate)

    suspend fun insertPantryItem(item: PantryItem): Long = pantryItemDao.insert(item)

    suspend fun updatePantryItem(item: PantryItem) = pantryItemDao.update(item)

    suspend fun deletePantryItem(item: PantryItem) = pantryItemDao.delete(item)
}
