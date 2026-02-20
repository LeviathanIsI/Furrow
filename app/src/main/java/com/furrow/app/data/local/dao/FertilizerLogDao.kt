package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.FertilizerLog
import kotlinx.coroutines.flow.Flow

@Dao
interface FertilizerLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: FertilizerLog): Long

    @Update
    suspend fun update(log: FertilizerLog)

    @Delete
    suspend fun delete(log: FertilizerLog)

    @Query("SELECT * FROM fertilizer_logs WHERE bedId = :bedId ORDER BY date DESC")
    fun getForBed(bedId: Long): Flow<List<FertilizerLog>>
}
