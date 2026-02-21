package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.CanningBatch
import kotlinx.coroutines.flow.Flow

@Dao
interface CanningBatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(batch: CanningBatch): Long

    @Update
    suspend fun update(batch: CanningBatch)

    @Delete
    suspend fun delete(batch: CanningBatch)

    @Query("SELECT * FROM canning_batches ORDER BY date_processed DESC")
    fun getAll(): Flow<List<CanningBatch>>

    @Query("SELECT * FROM canning_batches WHERE id = :id")
    fun getById(id: Long): Flow<CanningBatch?>
}
