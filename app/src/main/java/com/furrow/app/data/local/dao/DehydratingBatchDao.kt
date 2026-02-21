package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.DehydratingBatch
import kotlinx.coroutines.flow.Flow

@Dao
interface DehydratingBatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(batch: DehydratingBatch): Long

    @Update
    suspend fun update(batch: DehydratingBatch)

    @Delete
    suspend fun delete(batch: DehydratingBatch)

    @Query("SELECT * FROM dehydrating_batches ORDER BY date DESC")
    fun getAll(): Flow<List<DehydratingBatch>>

    @Query("SELECT * FROM dehydrating_batches WHERE id = :id")
    fun getById(id: Long): Flow<DehydratingBatch?>
}
