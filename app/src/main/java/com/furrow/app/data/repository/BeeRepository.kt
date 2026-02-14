package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.BeeRaceInfoDao
import com.furrow.app.data.local.dao.HiveActiveTreatment
import com.furrow.app.data.local.dao.HiveDao
import com.furrow.app.data.local.dao.HiveInspectionDate
import com.furrow.app.data.local.dao.InspectionDao
import com.furrow.app.data.local.entity.BeeRaceInfo
import com.furrow.app.data.local.entity.Hive
import com.furrow.app.data.local.entity.Inspection
import com.furrow.app.data.local.entity.Treatment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeeRepository @Inject constructor(
    private val hiveDao: HiveDao,
    private val inspectionDao: InspectionDao,
    private val beeRaceInfoDao: BeeRaceInfoDao,
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
}
