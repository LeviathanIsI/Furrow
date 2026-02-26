package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.Inspection
import com.furrow.app.data.local.entity.Treatment
import kotlinx.coroutines.flow.Flow

@Dao
interface InspectionDao {

    // --- Inspections ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: Inspection): Long

    @Update
    suspend fun updateInspection(inspection: Inspection)

    @Delete
    suspend fun deleteInspection(inspection: Inspection)

    @Query("SELECT * FROM inspections WHERE hiveId = :hiveId ORDER BY date DESC")
    fun getInspectionsForHive(hiveId: Long): Flow<List<Inspection>>

    @Query("SELECT * FROM inspections WHERE hiveId = :hiveId ORDER BY date DESC LIMIT 1")
    fun getLatestInspection(hiveId: Long): Flow<Inspection?>

    @Query("SELECT * FROM inspections WHERE id = :id")
    fun getInspectionById(id: Long): Flow<Inspection?>

    // --- Treatments ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreatment(treatment: Treatment): Long

    @Update
    suspend fun updateTreatment(treatment: Treatment)

    @Delete
    suspend fun deleteTreatment(treatment: Treatment)

    @Query("SELECT * FROM treatments WHERE hiveId = :hiveId ORDER BY date DESC")
    fun getTreatmentsForHive(hiveId: Long): Flow<List<Treatment>>

    @Query("SELECT * FROM treatments WHERE id = :id")
    fun getTreatmentById(id: Long): Flow<Treatment?>

    @Query("SELECT hiveId, MAX(date) as date FROM inspections GROUP BY hiveId")
    fun getLastInspectionDatePerHive(): Flow<List<HiveLastInspection>>

    @Query("SELECT hiveId, type FROM treatments WHERE endDate IS NULL OR endDate > :now")
    fun getActiveTreatments(now: Long): Flow<List<HiveActiveTreatment>>

    @Query("SELECT hiveId, date FROM inspections ORDER BY date DESC")
    fun getAllInspectionDates(): Flow<List<HiveInspectionDate>>

    @Query("""
        SELECT i.* FROM inspections i
        INNER JOIN (
            SELECT hiveId, MAX(date) as maxDate FROM inspections GROUP BY hiveId
        ) latest ON i.hiveId = latest.hiveId AND i.date = latest.maxDate
    """)
    fun getLatestInspectionPerHive(): Flow<List<Inspection>>

    @Query("""
        SELECT h.name AS hiveName, i.date, i.queenSeen, i.temperament,
               i.broodPattern, i.honeyStores, i.notes
        FROM inspections i
        INNER JOIN hives h ON i.hiveId = h.id
        ORDER BY i.date DESC
    """)
    fun getAllInspectionsForExport(): Flow<List<InspectionExport>>

    @Query("SELECT * FROM treatments WHERE endDate IS NULL OR endDate > :now ORDER BY endDate ASC")
    fun getAllActiveTreatmentsFull(now: Long): Flow<List<Treatment>>
}

data class HiveLastInspection(
    val hiveId: Long,
    val date: Long,
)

data class HiveActiveTreatment(
    val hiveId: Long,
    val type: String,
)

data class HiveInspectionDate(
    val hiveId: Long,
    val date: Long,
)

data class InspectionExport(
    val hiveName: String,
    val date: Long,
    val queenSeen: Boolean,
    val temperament: String?,
    val broodPattern: String?,
    val honeyStores: String?,
    val notes: String?,
)
