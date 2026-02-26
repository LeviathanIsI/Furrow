package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.BinLastInput
import com.furrow.app.data.local.dao.CompostBinDao
import com.furrow.app.data.local.dao.CompostInputLogDao
import com.furrow.app.data.local.dao.FenceDao
import com.furrow.app.data.local.dao.FenceMaintenanceLogDao
import com.furrow.app.data.local.dao.PaddockDao
import com.furrow.app.data.local.dao.PastureRotationLogDao
import com.furrow.app.data.local.dao.PropertyDao
import com.furrow.app.data.local.dao.SoilTestDao
import com.furrow.app.data.local.dao.StructureDao
import com.furrow.app.data.local.dao.StructureMaintenanceLogDao
import com.furrow.app.data.local.dao.WaterSourceDao
import com.furrow.app.data.local.dao.WeatherLogDao
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
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LandRepository @Inject constructor(
    private val propertyDao: PropertyDao,
    private val fenceDao: FenceDao,
    private val fenceMaintenanceLogDao: FenceMaintenanceLogDao,
    private val structureDao: StructureDao,
    private val structureMaintenanceLogDao: StructureMaintenanceLogDao,
    private val paddockDao: PaddockDao,
    private val pastureRotationLogDao: PastureRotationLogDao,
    private val soilTestDao: SoilTestDao,
    private val waterSourceDao: WaterSourceDao,
    private val compostBinDao: CompostBinDao,
    private val compostInputLogDao: CompostInputLogDao,
    private val weatherLogDao: WeatherLogDao,
) {
    // --- Properties ---

    fun getAllProperties(): Flow<List<Property>> = propertyDao.getAll()

    fun getPropertyById(id: Long): Flow<Property?> = propertyDao.getById(id)

    suspend fun insertProperty(property: Property): Long = propertyDao.insert(property)

    suspend fun updateProperty(property: Property) = propertyDao.update(property)

    suspend fun deleteProperty(property: Property) = propertyDao.delete(property)

    // --- Fences ---

    fun getAllFences(): Flow<List<Fence>> = fenceDao.getAll()

    fun getFenceById(id: Long): Flow<Fence?> = fenceDao.getById(id)

    suspend fun insertFence(fence: Fence): Long = fenceDao.insert(fence)

    suspend fun updateFence(fence: Fence) = fenceDao.update(fence)

    suspend fun deleteFence(fence: Fence) = fenceDao.delete(fence)

    // --- Fence Maintenance Logs ---

    fun getFenceMaintenanceLogsForFence(fenceId: Long): Flow<List<FenceMaintenanceLog>> =
        fenceMaintenanceLogDao.getForFence(fenceId)

    fun getFenceMaintenanceLogById(id: Long): Flow<FenceMaintenanceLog?> =
        fenceMaintenanceLogDao.getById(id)

    suspend fun insertFenceMaintenanceLog(log: FenceMaintenanceLog): Long =
        fenceMaintenanceLogDao.insert(log)

    suspend fun updateFenceMaintenanceLog(log: FenceMaintenanceLog) =
        fenceMaintenanceLogDao.update(log)

    suspend fun deleteFenceMaintenanceLog(log: FenceMaintenanceLog) =
        fenceMaintenanceLogDao.delete(log)

    // --- Structures ---

    fun getAllStructures(): Flow<List<Structure>> = structureDao.getAll()

    fun getStructureById(id: Long): Flow<Structure?> = structureDao.getById(id)

    suspend fun insertStructure(structure: Structure): Long = structureDao.insert(structure)

    suspend fun updateStructure(structure: Structure) = structureDao.update(structure)

    suspend fun deleteStructure(structure: Structure) = structureDao.delete(structure)

    // --- Structure Maintenance Logs ---

    fun getStructureMaintenanceLogsForStructure(structureId: Long): Flow<List<StructureMaintenanceLog>> =
        structureMaintenanceLogDao.getForStructure(structureId)

    fun getStructureMaintenanceLogById(id: Long): Flow<StructureMaintenanceLog?> =
        structureMaintenanceLogDao.getById(id)

    suspend fun insertStructureMaintenanceLog(log: StructureMaintenanceLog): Long =
        structureMaintenanceLogDao.insert(log)

    suspend fun updateStructureMaintenanceLog(log: StructureMaintenanceLog) =
        structureMaintenanceLogDao.update(log)

    suspend fun deleteStructureMaintenanceLog(log: StructureMaintenanceLog) =
        structureMaintenanceLogDao.delete(log)

    // --- Paddocks ---

    fun getAllPaddocks(): Flow<List<Paddock>> = paddockDao.getAll()

    fun getPaddockById(id: Long): Flow<Paddock?> = paddockDao.getById(id)

    suspend fun insertPaddock(paddock: Paddock): Long = paddockDao.insert(paddock)

    suspend fun updatePaddock(paddock: Paddock) = paddockDao.update(paddock)

    suspend fun deletePaddock(paddock: Paddock) = paddockDao.delete(paddock)

    // --- Pasture Rotation Logs ---

    fun getRotationLogsForPaddock(paddockId: Long): Flow<List<PastureRotationLog>> =
        pastureRotationLogDao.getForPaddock(paddockId)

    fun getRotationLogById(id: Long): Flow<PastureRotationLog?> =
        pastureRotationLogDao.getById(id)

    fun getCurrentRotations(): Flow<List<PastureRotationLog>> =
        pastureRotationLogDao.getCurrentRotations()

    suspend fun insertRotationLog(log: PastureRotationLog): Long =
        pastureRotationLogDao.insert(log)

    suspend fun updateRotationLog(log: PastureRotationLog) =
        pastureRotationLogDao.update(log)

    suspend fun deleteRotationLog(log: PastureRotationLog) =
        pastureRotationLogDao.delete(log)

    // --- Soil Tests ---

    fun getAllSoilTests(): Flow<List<SoilTest>> = soilTestDao.getAll()

    fun getSoilTestById(id: Long): Flow<SoilTest?> = soilTestDao.getById(id)

    suspend fun insertSoilTest(soilTest: SoilTest): Long = soilTestDao.insert(soilTest)

    suspend fun updateSoilTest(soilTest: SoilTest) = soilTestDao.update(soilTest)

    suspend fun deleteSoilTest(soilTest: SoilTest) = soilTestDao.delete(soilTest)

    // --- Water Sources ---

    fun getAllWaterSources(): Flow<List<WaterSource>> = waterSourceDao.getAll()

    fun getWaterSourceById(id: Long): Flow<WaterSource?> = waterSourceDao.getById(id)

    suspend fun insertWaterSource(waterSource: WaterSource): Long =
        waterSourceDao.insert(waterSource)

    suspend fun updateWaterSource(waterSource: WaterSource) =
        waterSourceDao.update(waterSource)

    suspend fun deleteWaterSource(waterSource: WaterSource) =
        waterSourceDao.delete(waterSource)

    // --- Compost Bins ---

    fun getAllCompostBins(): Flow<List<CompostBin>> = compostBinDao.getAll()

    fun getCompostBinById(id: Long): Flow<CompostBin?> = compostBinDao.getById(id)

    fun getActiveCompostBins(): Flow<List<CompostBin>> = compostBinDao.getActive()

    suspend fun insertCompostBin(bin: CompostBin): Long = compostBinDao.insert(bin)

    suspend fun updateCompostBin(bin: CompostBin) = compostBinDao.update(bin)

    suspend fun deleteCompostBin(bin: CompostBin) = compostBinDao.delete(bin)

    // --- Compost Input Logs ---

    fun getCompostInputLogsForBin(binId: Long): Flow<List<CompostInputLog>> =
        compostInputLogDao.getForBin(binId)

    fun getCompostInputLogById(id: Long): Flow<CompostInputLog?> =
        compostInputLogDao.getById(id)

    suspend fun insertCompostInputLog(log: CompostInputLog): Long =
        compostInputLogDao.insert(log)

    suspend fun updateCompostInputLog(log: CompostInputLog) =
        compostInputLogDao.update(log)

    suspend fun deleteCompostInputLog(log: CompostInputLog) =
        compostInputLogDao.delete(log)

    fun getLastCompostInputDatePerBin(): Flow<List<BinLastInput>> =
        compostInputLogDao.getLastInputDatePerBin()

    // --- Weather Logs ---

    fun getAllWeatherLogs(): Flow<List<WeatherLog>> = weatherLogDao.getAll()

    fun getWeatherLogById(id: Long): Flow<WeatherLog?> = weatherLogDao.getById(id)

    fun getWeatherLogsForDateRange(startMillis: Long, endMillis: Long): Flow<List<WeatherLog>> =
        weatherLogDao.getForDateRange(startMillis, endMillis)

    suspend fun insertWeatherLog(log: WeatherLog): Long = weatherLogDao.insert(log)

    suspend fun updateWeatherLog(log: WeatherLog) = weatherLogDao.update(log)

    suspend fun deleteWeatherLog(log: WeatherLog) = weatherLogDao.delete(log)
}
