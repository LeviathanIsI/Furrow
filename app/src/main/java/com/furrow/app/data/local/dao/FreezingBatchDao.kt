package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.FreezingBatch
import kotlinx.coroutines.flow.Flow

@Dao
interface FreezingBatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(batch: FreezingBatch): Long

    @Update
    suspend fun update(batch: FreezingBatch)

    @Delete
    suspend fun delete(batch: FreezingBatch)

    @Query("SELECT * FROM freezing_batches ORDER BY date_frozen DESC")
    fun getAll(): Flow<List<FreezingBatch>>

    @Query("SELECT * FROM freezing_batches WHERE id = :id")
    fun getById(id: Long): Flow<FreezingBatch?>
}
