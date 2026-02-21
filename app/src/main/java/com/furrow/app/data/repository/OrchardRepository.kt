package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.BloomRecordDao
import com.furrow.app.data.local.dao.ChillHoursLogDao
import com.furrow.app.data.local.dao.GraftingLogDao
import com.furrow.app.data.local.dao.OrchardHarvestDao
import com.furrow.app.data.local.dao.OrchardPlantDao
import com.furrow.app.data.local.dao.PruningLogDao
import com.furrow.app.data.local.dao.SprayLogDao
import com.furrow.app.data.local.entity.BloomRecord
import com.furrow.app.data.local.entity.ChillHoursLog
import com.furrow.app.data.local.entity.GraftingLog
import com.furrow.app.data.local.entity.OrchardHarvest
import com.furrow.app.data.local.entity.OrchardPlant
import com.furrow.app.data.local.entity.PruningLog
import com.furrow.app.data.local.entity.SprayLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrchardRepository @Inject constructor(
    private val orchardPlantDao: OrchardPlantDao,
    private val pruningLogDao: PruningLogDao,
    private val sprayLogDao: SprayLogDao,
    private val bloomRecordDao: BloomRecordDao,
    private val orchardHarvestDao: OrchardHarvestDao,
    private val graftingLogDao: GraftingLogDao,
    private val chillHoursLogDao: ChillHoursLogDao,
) {
    // --- Orchard Plants ---

    fun getAllPlants(): Flow<List<OrchardPlant>> = orchardPlantDao.getAll()

    fun getPlantById(id: Long): Flow<OrchardPlant?> = orchardPlantDao.getById(id)

    fun getPlantsByCategory(category: String): Flow<List<OrchardPlant>> =
        orchardPlantDao.getByCategory(category)

    fun getActivePlants(): Flow<List<OrchardPlant>> = orchardPlantDao.getActive()

    suspend fun insertPlant(plant: OrchardPlant): Long = orchardPlantDao.insert(plant)

    suspend fun updatePlant(plant: OrchardPlant) = orchardPlantDao.update(plant)

    suspend fun deletePlant(plant: OrchardPlant) = orchardPlantDao.delete(plant)

    // --- Pruning Logs ---

    fun getPruningLogsForPlant(plantId: Long): Flow<List<PruningLog>> =
        pruningLogDao.getForPlant(plantId)

    fun getPruningLogById(id: Long): Flow<PruningLog?> = pruningLogDao.getById(id)

    suspend fun insertPruningLog(log: PruningLog): Long = pruningLogDao.insert(log)

    suspend fun updatePruningLog(log: PruningLog) = pruningLogDao.update(log)

    suspend fun deletePruningLog(log: PruningLog) = pruningLogDao.delete(log)

    // --- Spray Logs ---

    fun getSprayLogsForPlant(plantId: Long): Flow<List<SprayLog>> =
        sprayLogDao.getForPlant(plantId)

    fun getSprayLogById(id: Long): Flow<SprayLog?> = sprayLogDao.getById(id)

    suspend fun insertSprayLog(log: SprayLog): Long = sprayLogDao.insert(log)

    suspend fun updateSprayLog(log: SprayLog) = sprayLogDao.update(log)

    suspend fun deleteSprayLog(log: SprayLog) = sprayLogDao.delete(log)

    // --- Bloom Records ---

    fun getBloomRecordsForPlant(plantId: Long): Flow<List<BloomRecord>> =
        bloomRecordDao.getForPlant(plantId)

    fun getBloomRecordById(id: Long): Flow<BloomRecord?> = bloomRecordDao.getById(id)

    suspend fun insertBloomRecord(record: BloomRecord): Long = bloomRecordDao.insert(record)

    suspend fun updateBloomRecord(record: BloomRecord) = bloomRecordDao.update(record)

    suspend fun deleteBloomRecord(record: BloomRecord) = bloomRecordDao.delete(record)

    // --- Orchard Harvests ---

    fun getHarvestsForPlant(plantId: Long): Flow<List<OrchardHarvest>> =
        orchardHarvestDao.getForPlant(plantId)

    fun getHarvestById(id: Long): Flow<OrchardHarvest?> = orchardHarvestDao.getById(id)

    fun getAllHarvests(): Flow<List<OrchardHarvest>> = orchardHarvestDao.getAll()

    suspend fun insertHarvest(harvest: OrchardHarvest): Long = orchardHarvestDao.insert(harvest)

    suspend fun updateHarvest(harvest: OrchardHarvest) = orchardHarvestDao.update(harvest)

    suspend fun deleteHarvest(harvest: OrchardHarvest) = orchardHarvestDao.delete(harvest)

    // --- Grafting Logs ---

    fun getGraftingLogsForPlant(plantId: Long): Flow<List<GraftingLog>> =
        graftingLogDao.getForPlant(plantId)

    fun getGraftingLogById(id: Long): Flow<GraftingLog?> = graftingLogDao.getById(id)

    suspend fun insertGraftingLog(log: GraftingLog): Long = graftingLogDao.insert(log)

    suspend fun updateGraftingLog(log: GraftingLog) = graftingLogDao.update(log)

    suspend fun deleteGraftingLog(log: GraftingLog) = graftingLogDao.delete(log)

    // --- Chill Hours Logs ---

    fun getAllChillHoursLogs(): Flow<List<ChillHoursLog>> = chillHoursLogDao.getAll()

    fun getChillHoursLogBySeason(season: String): Flow<ChillHoursLog?> =
        chillHoursLogDao.getBySeason(season)

    fun getChillHoursLogById(id: Long): Flow<ChillHoursLog?> = chillHoursLogDao.getById(id)

    suspend fun insertChillHoursLog(log: ChillHoursLog): Long = chillHoursLogDao.insert(log)

    suspend fun updateChillHoursLog(log: ChillHoursLog) = chillHoursLogDao.update(log)

    suspend fun deleteChillHoursLog(log: ChillHoursLog) = chillHoursLogDao.delete(log)
}
