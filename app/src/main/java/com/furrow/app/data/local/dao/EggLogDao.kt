package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.data.local.entity.FeedLog
import kotlinx.coroutines.flow.Flow

@Dao
interface EggLogDao {

    // --- Egg logs ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEggLog(eggLog: EggLog): Long

    @Update
    suspend fun updateEggLog(eggLog: EggLog)

    @Delete
    suspend fun deleteEggLog(eggLog: EggLog)

    @Query("SELECT * FROM egg_logs ORDER BY date DESC")
    fun getAllEggLogs(): Flow<List<EggLog>>

    @Query("SELECT * FROM egg_logs WHERE date BETWEEN :startMillis AND :endMillis ORDER BY date DESC")
    fun getEggLogsForDateRange(startMillis: Long, endMillis: Long): Flow<List<EggLog>>

    @Query("SELECT COALESCE(SUM(count), 0) FROM egg_logs WHERE date BETWEEN :startMillis AND :endMillis")
    fun getEggCountForDateRange(startMillis: Long, endMillis: Long): Flow<Int>

    @Query("SELECT * FROM egg_logs WHERE id = :id")
    fun getEggLogById(id: Long): Flow<EggLog?>

    // --- Feed logs ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedLog(feedLog: FeedLog): Long

    @Update
    suspend fun updateFeedLog(feedLog: FeedLog)

    @Delete
    suspend fun deleteFeedLog(feedLog: FeedLog)

    @Query("SELECT * FROM feed_logs ORDER BY date DESC")
    fun getAllFeedLogs(): Flow<List<FeedLog>>
}
