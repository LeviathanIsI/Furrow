package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.WateringLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WateringLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WateringLog): Long

    @Update
    suspend fun update(log: WateringLog)

    @Delete
    suspend fun delete(log: WateringLog)

    @Query("SELECT * FROM watering_logs WHERE bedId = :bedId ORDER BY date DESC")
    fun getForBed(bedId: Long): Flow<List<WateringLog>>

    @Query("SELECT * FROM watering_logs WHERE date >= :startMillis AND date <= :endMillis ORDER BY date ASC")
    fun getForDateRange(startMillis: Long, endMillis: Long): Flow<List<WateringLog>>

    @Query("""
        SELECT bedId, MAX(date) as lastWatered
        FROM watering_logs
        GROUP BY bedId
    """)
    fun getLastWateredPerBed(): Flow<List<BedLastWatered>>
}

data class BedLastWatered(
    val bedId: Long,
    val lastWatered: Long,
)
