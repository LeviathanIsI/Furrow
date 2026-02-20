package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.PestDiseaseLog
import kotlinx.coroutines.flow.Flow

@Dao
interface PestDiseaseLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: PestDiseaseLog): Long

    @Update
    suspend fun update(log: PestDiseaseLog)

    @Delete
    suspend fun delete(log: PestDiseaseLog)

    @Query("SELECT * FROM pest_disease_logs WHERE bedId = :bedId ORDER BY date DESC")
    fun getForBed(bedId: Long): Flow<List<PestDiseaseLog>>

    @Query("SELECT * FROM pest_disease_logs WHERE plantingId = :plantingId ORDER BY date DESC")
    fun getForPlanting(plantingId: Long): Flow<List<PestDiseaseLog>>

    @Query("""
        SELECT plantingId, COUNT(*) as count
        FROM pest_disease_logs
        WHERE resolved = 0 AND plantingId IS NOT NULL
        GROUP BY plantingId
    """)
    fun getActiveCountPerPlanting(): Flow<List<PlantingPestCount>>
}

data class PlantingPestCount(
    val plantingId: Long,
    val count: Int,
)
