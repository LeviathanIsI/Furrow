package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.ApiaryDao
import com.furrow.app.data.local.dao.BeeFeedingLogDao
import com.furrow.app.data.local.dao.BeeHarvestLogDao
import com.furrow.app.data.local.dao.BeeRaceInfoDao
import com.furrow.app.data.local.dao.HiveActiveTreatment
import com.furrow.app.data.local.dao.HiveDao
import com.furrow.app.data.local.dao.HiveEventDao
import com.furrow.app.data.local.dao.HiveInspectionDate
import com.furrow.app.data.local.dao.InspectionDao
import com.furrow.app.data.local.dao.QueenRecordDao
import com.furrow.app.data.local.dao.VarroaTestDao
import com.furrow.app.data.local.entity.Apiary
import com.furrow.app.data.local.entity.BeeFeedingLog
import com.furrow.app.data.local.entity.BeeHarvestLog
import com.furrow.app.data.local.entity.BeeRaceInfo
import com.furrow.app.data.local.entity.Hive
import com.furrow.app.data.local.entity.HiveEvent
import com.furrow.app.data.local.entity.Inspection
import com.furrow.app.data.local.entity.QueenRecord
import com.furrow.app.data.local.entity.Treatment
import com.furrow.app.data.local.entity.VarroaTest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeeRepository @Inject constructor(
    private val hiveDao: HiveDao,
    private val inspectionDao: InspectionDao,
    private val beeRaceInfoDao: BeeRaceInfoDao,
    private val apiaryDao: ApiaryDao,
    private val queenRecordDao: QueenRecordDao,
    private val varroaTestDao: VarroaTestDao,
    private val beeFeedingLogDao: BeeFeedingLogDao,
    private val beeHarvestLogDao: BeeHarvestLogDao,
    private val hiveEventDao: HiveEventDao,
) {
    // --- Bee race reference data ---

    fun getAllRaces(): Flow<List<BeeRaceInfo>> = beeRaceInfoDao.getAllRaces()

    fun getRaceByName(name: String): Flow<BeeRaceInfo?> = beeRaceInfoDao.getRaceByName(name)

    fun searchRaces(query: String): Flow<List<BeeRaceInfo>> = beeRaceInfoDao.searchRaces(query)

    suspend fun insertRace(race: BeeRaceInfo): Long = beeRaceInfoDao.insert(race)

    suspend fun updateRace(race: BeeRaceInfo) = beeRaceInfoDao.update(race)

    suspend fun deleteRace(race: BeeRaceInfo) = beeRaceInfoDao.delete(race)

    // --- Hives ---

    fun getActiveHives(): Flow<List<Hive>> = hiveDao.getActiveHives()

    fun getAllHives(): Flow<List<Hive>> = hiveDao.getAll()

    fun getHiveById(id: Long): Flow<Hive?> = hiveDao.getById(id)

    suspend fun insertHive(hive: Hive): Long = hiveDao.insert(hive)

    suspend fun updateHive(hive: Hive) = hiveDao.update(hive)

    suspend fun deleteHive(hive: Hive) = hiveDao.delete(hive)

    // --- Inspections ---

    fun getInspectionsForHive(hiveId: Long): Flow<List<Inspection>> =
        inspectionDao.getInspectionsForHive(hiveId)

    fun getLatestInspection(hiveId: Long): Flow<Inspection?> =
        inspectionDao.getLatestInspection(hiveId)

    fun getInspectionById(id: Long): Flow<Inspection?> =
        inspectionDao.getInspectionById(id)

    suspend fun insertInspection(inspection: Inspection): Long =
        inspectionDao.insertInspection(inspection)

    suspend fun updateInspection(inspection: Inspection) =
        inspectionDao.updateInspection(inspection)

    suspend fun deleteInspection(inspection: Inspection) =
        inspectionDao.deleteInspection(inspection)

    // --- Treatments ---

    fun getTreatmentsForHive(hiveId: Long): Flow<List<Treatment>> =
        inspectionDao.getTreatmentsForHive(hiveId)

    fun getTreatmentById(id: Long): Flow<Treatment?> =
        inspectionDao.getTreatmentById(id)

    suspend fun insertTreatment(treatment: Treatment): Long =
        inspectionDao.insertTreatment(treatment)

    suspend fun updateTreatment(treatment: Treatment) =
        inspectionDao.updateTreatment(treatment)

    suspend fun deleteTreatment(treatment: Treatment) =
        inspectionDao.deleteTreatment(treatment)

    fun getLastInspectionDatePerHive() = inspectionDao.getLastInspectionDatePerHive()

    fun getActiveTreatments(now: Long): Flow<List<HiveActiveTreatment>> =
        inspectionDao.getActiveTreatments(now)

    fun getAllInspectionDates(): Flow<List<HiveInspectionDate>> =
        inspectionDao.getAllInspectionDates()

    fun getLatestInspectionPerHive(): Flow<List<Inspection>> =
        inspectionDao.getLatestInspectionPerHive()

    // --- Apiaries ---

    fun getAllApiaries(): Flow<List<Apiary>> = apiaryDao.getAll()

    fun getApiaryById(id: Long): Flow<Apiary?> = apiaryDao.getById(id)

    suspend fun insertApiary(apiary: Apiary): Long = apiaryDao.insert(apiary)

    suspend fun updateApiary(apiary: Apiary) = apiaryDao.update(apiary)

    suspend fun deleteApiary(apiary: Apiary) = apiaryDao.delete(apiary)

    // --- Queen Records ---

    fun getQueenRecordsForHive(hiveId: Long): Flow<List<QueenRecord>> =
        queenRecordDao.getForHive(hiveId)

    fun getQueenRecordById(id: Long): Flow<QueenRecord?> = queenRecordDao.getById(id)

    fun getCurrentQueen(hiveId: Long): Flow<QueenRecord?> =
        queenRecordDao.getCurrentQueen(hiveId)

    suspend fun insertQueenRecord(record: QueenRecord): Long = queenRecordDao.insert(record)

    suspend fun updateQueenRecord(record: QueenRecord) = queenRecordDao.update(record)

    suspend fun deleteQueenRecord(record: QueenRecord) = queenRecordDao.delete(record)

    // --- Varroa Tests ---

    fun getVarroaTestsForHive(hiveId: Long): Flow<List<VarroaTest>> =
        varroaTestDao.getForHive(hiveId)

    fun getVarroaTestById(id: Long): Flow<VarroaTest?> = varroaTestDao.getById(id)

    fun getLatestVarroaTest(hiveId: Long): Flow<VarroaTest?> =
        varroaTestDao.getLatestForHive(hiveId)

    suspend fun insertVarroaTest(test: VarroaTest): Long = varroaTestDao.insert(test)

    suspend fun updateVarroaTest(test: VarroaTest) = varroaTestDao.update(test)

    suspend fun deleteVarroaTest(test: VarroaTest) = varroaTestDao.delete(test)

    // --- Bee Feeding Logs ---

    fun getBeeFeedingLogsForHive(hiveId: Long): Flow<List<BeeFeedingLog>> =
        beeFeedingLogDao.getForHive(hiveId)

    fun getBeeFeedingLogById(id: Long): Flow<BeeFeedingLog?> = beeFeedingLogDao.getById(id)

    suspend fun insertBeeFeedingLog(log: BeeFeedingLog): Long = beeFeedingLogDao.insert(log)

    suspend fun updateBeeFeedingLog(log: BeeFeedingLog) = beeFeedingLogDao.update(log)

    suspend fun deleteBeeFeedingLog(log: BeeFeedingLog) = beeFeedingLogDao.delete(log)

    // --- Bee Harvest Logs ---

    fun getBeeHarvestLogsForHive(hiveId: Long): Flow<List<BeeHarvestLog>> =
        beeHarvestLogDao.getForHive(hiveId)

    fun getBeeHarvestLogById(id: Long): Flow<BeeHarvestLog?> = beeHarvestLogDao.getById(id)

    fun getBeeHarvestLogsByProduct(product: String): Flow<List<BeeHarvestLog>> =
        beeHarvestLogDao.getByProduct(product)

    suspend fun insertBeeHarvestLog(log: BeeHarvestLog): Long = beeHarvestLogDao.insert(log)

    suspend fun updateBeeHarvestLog(log: BeeHarvestLog) = beeHarvestLogDao.update(log)

    suspend fun deleteBeeHarvestLog(log: BeeHarvestLog) = beeHarvestLogDao.delete(log)

    // --- Hive Events ---

    fun getAllHiveEvents(): Flow<List<HiveEvent>> = hiveEventDao.getAll()

    fun getHiveEventById(id: Long): Flow<HiveEvent?> = hiveEventDao.getById(id)

    fun getHiveEventsForHive(hiveId: Long): Flow<List<HiveEvent>> =
        hiveEventDao.getForHive(hiveId)

    suspend fun insertHiveEvent(event: HiveEvent): Long = hiveEventDao.insert(event)

    suspend fun updateHiveEvent(event: HiveEvent) = hiveEventDao.update(event)

    suspend fun deleteHiveEvent(event: HiveEvent) = hiveEventDao.delete(event)
}
