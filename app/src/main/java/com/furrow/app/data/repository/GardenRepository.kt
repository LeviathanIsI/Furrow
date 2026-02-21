package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.CropRotationLogDao
import com.furrow.app.data.local.dao.FertilizerLogDao
import com.furrow.app.data.local.dao.GardenBedDao
import com.furrow.app.data.local.dao.GardenDao
import com.furrow.app.data.local.dao.PestDiseaseLogDao
import com.furrow.app.data.local.dao.PlantingDao
import com.furrow.app.data.local.dao.SeasonExtensionDao
import com.furrow.app.data.local.dao.SeedInventoryDao
import com.furrow.app.data.local.dao.SoilAmendmentLogDao
import com.furrow.app.data.local.dao.WateringLogDao
import com.furrow.app.data.local.entity.CropRotationLog
import com.furrow.app.data.local.entity.FertilizerLog
import com.furrow.app.data.local.entity.Garden
import com.furrow.app.data.local.entity.GardenBed
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.data.local.entity.PestDiseaseLog
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.SeasonExtension
import com.furrow.app.data.local.entity.SeedInventory
import com.furrow.app.data.local.entity.SoilAmendmentLog
import com.furrow.app.data.local.entity.WateringLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GardenRepository @Inject constructor(
    private val gardenBedDao: GardenBedDao,
    private val plantingDao: PlantingDao,
    private val wateringLogDao: WateringLogDao,
    private val fertilizerLogDao: FertilizerLogDao,
    private val pestDiseaseLogDao: PestDiseaseLogDao,
    private val gardenDao: GardenDao,
    private val cropRotationLogDao: CropRotationLogDao,
    private val soilAmendmentLogDao: SoilAmendmentLogDao,
    private val seedInventoryDao: SeedInventoryDao,
    private val seasonExtensionDao: SeasonExtensionDao,
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

    // --- Watering Logs ---

    fun getWateringLogsForBed(bedId: Long): Flow<List<WateringLog>> =
        wateringLogDao.getForBed(bedId)

    fun getLastWateredPerBed() = wateringLogDao.getLastWateredPerBed()

    suspend fun insertWateringLog(log: WateringLog): Long =
        wateringLogDao.insert(log)

    suspend fun updateWateringLog(log: WateringLog) =
        wateringLogDao.update(log)

    suspend fun deleteWateringLog(log: WateringLog) =
        wateringLogDao.delete(log)

    // --- Fertilizer Logs ---

    fun getFertilizerLogsForBed(bedId: Long): Flow<List<FertilizerLog>> =
        fertilizerLogDao.getForBed(bedId)

    suspend fun insertFertilizerLog(log: FertilizerLog): Long =
        fertilizerLogDao.insert(log)

    suspend fun updateFertilizerLog(log: FertilizerLog) =
        fertilizerLogDao.update(log)

    suspend fun deleteFertilizerLog(log: FertilizerLog) =
        fertilizerLogDao.delete(log)

    // --- Pest & Disease Logs ---

    fun getPestLogsForBed(bedId: Long): Flow<List<PestDiseaseLog>> =
        pestDiseaseLogDao.getForBed(bedId)

    fun getPestLogsForPlanting(plantingId: Long): Flow<List<PestDiseaseLog>> =
        pestDiseaseLogDao.getForPlanting(plantingId)

    fun getActivePestCountPerPlanting() = pestDiseaseLogDao.getActiveCountPerPlanting()

    suspend fun insertPestLog(log: PestDiseaseLog): Long =
        pestDiseaseLogDao.insert(log)

    suspend fun updatePestLog(log: PestDiseaseLog) =
        pestDiseaseLogDao.update(log)

    suspend fun deletePestLog(log: PestDiseaseLog) =
        pestDiseaseLogDao.delete(log)

    // --- Gardens ---

    fun getAllGardens(): Flow<List<Garden>> = gardenDao.getAll()

    fun getGardenById(id: Long): Flow<Garden?> = gardenDao.getById(id)

    suspend fun insertGarden(garden: Garden): Long = gardenDao.insert(garden)

    suspend fun updateGarden(garden: Garden) = gardenDao.update(garden)

    suspend fun deleteGarden(garden: Garden) = gardenDao.delete(garden)

    // --- Crop Rotation Logs ---

    fun getCropRotationLogsForBed(bedId: Long): Flow<List<CropRotationLog>> =
        cropRotationLogDao.getForBed(bedId)

    fun getCropRotationLogById(id: Long): Flow<CropRotationLog?> =
        cropRotationLogDao.getById(id)

    fun getCropFamilyHistoryForBed(bedId: Long, family: String): Flow<List<CropRotationLog>> =
        cropRotationLogDao.getFamilyHistoryForBed(bedId, family)

    suspend fun insertCropRotationLog(log: CropRotationLog): Long =
        cropRotationLogDao.insert(log)

    suspend fun updateCropRotationLog(log: CropRotationLog) =
        cropRotationLogDao.update(log)

    suspend fun deleteCropRotationLog(log: CropRotationLog) =
        cropRotationLogDao.delete(log)

    // --- Soil Amendment Logs ---

    fun getSoilAmendmentLogsForBed(bedId: Long): Flow<List<SoilAmendmentLog>> =
        soilAmendmentLogDao.getForBed(bedId)

    fun getSoilAmendmentLogById(id: Long): Flow<SoilAmendmentLog?> =
        soilAmendmentLogDao.getById(id)

    suspend fun insertSoilAmendmentLog(log: SoilAmendmentLog): Long =
        soilAmendmentLogDao.insert(log)

    suspend fun updateSoilAmendmentLog(log: SoilAmendmentLog) =
        soilAmendmentLogDao.update(log)

    suspend fun deleteSoilAmendmentLog(log: SoilAmendmentLog) =
        soilAmendmentLogDao.delete(log)

    // --- Seed Inventory ---

    fun getAllSeeds(): Flow<List<SeedInventory>> = seedInventoryDao.getAll()

    fun getSeedById(id: Long): Flow<SeedInventory?> = seedInventoryDao.getById(id)

    fun getExpiringSeedsBefore(date: Long): Flow<List<SeedInventory>> =
        seedInventoryDao.getExpiringSoon(date)

    suspend fun insertSeed(seed: SeedInventory): Long = seedInventoryDao.insert(seed)

    suspend fun updateSeed(seed: SeedInventory) = seedInventoryDao.update(seed)

    suspend fun deleteSeed(seed: SeedInventory) = seedInventoryDao.delete(seed)

    // --- Season Extensions ---

    fun getSeasonExtensionsForBed(bedId: Long): Flow<List<SeasonExtension>> =
        seasonExtensionDao.getForBed(bedId)

    fun getSeasonExtensionById(id: Long): Flow<SeasonExtension?> =
        seasonExtensionDao.getById(id)

    fun getActiveSeasonExtensions(): Flow<List<SeasonExtension>> =
        seasonExtensionDao.getActive()

    suspend fun insertSeasonExtension(ext: SeasonExtension): Long =
        seasonExtensionDao.insert(ext)

    suspend fun updateSeasonExtension(ext: SeasonExtension) =
        seasonExtensionDao.update(ext)

    suspend fun deleteSeasonExtension(ext: SeasonExtension) =
        seasonExtensionDao.delete(ext)
}
