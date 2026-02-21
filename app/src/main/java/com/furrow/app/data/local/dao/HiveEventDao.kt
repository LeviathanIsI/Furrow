package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.HiveEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface HiveEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: HiveEvent): Long

    @Update
    suspend fun update(event: HiveEvent)

    @Delete
    suspend fun delete(event: HiveEvent)

    @Query("SELECT * FROM hive_events ORDER BY date DESC")
    fun getAll(): Flow<List<HiveEvent>>

    @Query("SELECT * FROM hive_events WHERE id = :id")
    fun getById(id: Long): Flow<HiveEvent?>

    @Query("SELECT * FROM hive_events WHERE origin_hive_id = :hiveId OR destination_hive_id = :hiveId ORDER BY date DESC")
    fun getForHive(hiveId: Long): Flow<List<HiveEvent>>
}
