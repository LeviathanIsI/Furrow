package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.GardenBedDao
import com.furrow.app.data.local.dao.PlantingDao
import com.furrow.app.data.local.entity.GardenBed
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.data.local.entity.Planting
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GardenRepository @Inject constructor(
    private val gardenBedDao: GardenBedDao,
    private val plantingDao: PlantingDao,
) {
    // --- Garden Beds ---

    fun getActiveBeds(): Flow<List<GardenBed>> = gardenBedDao.getActiveBeds()

    fun getAllBeds(): Flow<List<GardenBed>> = gardenBedDao.getAll()

    fun getBedById(id: Long): Flow<GardenBed?> = gardenBedDao.getById(id)

    suspend fun insertBed(bed: GardenBed): Long = gardenBedDao.insert(bed)

    suspend fun updateBed(bed: GardenBed) = gardenBedDao.update(bed)

    suspend fun deleteBed(bed: GardenBed) = gardenBedDao.delete(bed)

    // --- Plantings ---

    fun getPlantingsForBed(bedId: Long): Flow<List<Planting>> =
        plantingDao.getPlantingsForBed(bedId)

    fun getPlantingById(id: Long): Flow<Planting?> =
        plantingDao.getPlantingById(id)

    fun getActivePlantingCount(bedId: Long): Flow<Int> =
        plantingDao.getActivePlantingCount(bedId)

    suspend fun insertPlanting(planting: Planting): Long =
        plantingDao.insertPlanting(planting)

    suspend fun updatePlanting(planting: Planting) =
        plantingDao.updatePlanting(planting)

    suspend fun deletePlanting(planting: Planting) =
        plantingDao.deletePlanting(planting)

    // --- Harvests ---

    fun getHarvestById(id: Long): Flow<HarvestLog?> =
        plantingDao.getHarvestById(id)

    fun getHarvestsForPlanting(plantingId: Long): Flow<List<HarvestLog>> =
        plantingDao.getHarvestsForPlanting(plantingId)

    fun getHarvestsForBed(bedId: Long): Flow<List<HarvestLog>> =
        plantingDao.getHarvestsForBed(bedId)

    fun getRecentHarvests(limit: Int): Flow<List<HarvestLog>> =
        plantingDao.getRecentHarvests(limit)

    suspend fun insertHarvest(harvest: HarvestLog): Long =
        plantingDao.insertHarvest(harvest)

    suspend fun updateHarvest(harvest: HarvestLog) =
        plantingDao.updateHarvest(harvest)

    suspend fun deleteHarvest(harvest: HarvestLog) =
        plantingDao.deleteHarvest(harvest)

    // --- Summary ---

    fun getActivePlantingCountPerBed() = plantingDao.getActivePlantingCountPerBed()

    fun getActivePlantings(): Flow<List<Planting>> = plantingDao.getActivePlantings()

    fun getHarvestsForDateRange(startMillis: Long, endMillis: Long): Flow<List<HarvestLog>> =
        plantingDao.getHarvestsForDateRange(startMillis, endMillis)
}
