package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.FeedLog
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: FeedLog): Long

    @Update
    suspend fun update(log: FeedLog)

    @Delete
    suspend fun delete(log: FeedLog)

    @Query("SELECT * FROM feed_logs WHERE animal_id = :animalId ORDER BY date DESC")
    fun getForAnimal(animalId: Long): Flow<List<FeedLog>>

    @Query("SELECT * FROM feed_logs WHERE flock_id = :flockId ORDER BY date DESC")
    fun getForFlock(flockId: String): Flow<List<FeedLog>>

    @Query("SELECT * FROM feed_logs WHERE id = :id")
    fun getById(id: Long): Flow<FeedLog?>

    @Query("SELECT * FROM feed_logs ORDER BY date DESC")
    fun getAll(): Flow<List<FeedLog>>

    @Query("SELECT * FROM feed_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getForDateRange(startDate: Long, endDate: Long): Flow<List<FeedLog>>
}
