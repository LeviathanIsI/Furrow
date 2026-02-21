package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.BeeFeedingLog
import kotlinx.coroutines.flow.Flow

@Dao
interface BeeFeedingLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: BeeFeedingLog): Long

    @Update
    suspend fun update(log: BeeFeedingLog)

    @Delete
    suspend fun delete(log: BeeFeedingLog)

    @Query("SELECT * FROM bee_feeding_logs WHERE hive_id = :hiveId ORDER BY date DESC")
    fun getForHive(hiveId: Long): Flow<List<BeeFeedingLog>>

    @Query("SELECT * FROM bee_feeding_logs WHERE id = :id")
    fun getById(id: Long): Flow<BeeFeedingLog?>
}
