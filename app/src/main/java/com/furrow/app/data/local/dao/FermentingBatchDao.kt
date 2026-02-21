package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.FermentingBatch
import kotlinx.coroutines.flow.Flow

@Dao
interface FermentingBatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(batch: FermentingBatch): Long

    @Update
    suspend fun update(batch: FermentingBatch)

    @Delete
    suspend fun delete(batch: FermentingBatch)

    @Query("SELECT * FROM fermenting_batches ORDER BY start_date DESC")
    fun getAll(): Flow<List<FermentingBatch>>

    @Query("SELECT * FROM fermenting_batches WHERE id = :id")
    fun getById(id: Long): Flow<FermentingBatch?>

    @Query("SELECT * FROM fermenting_batches WHERE end_date IS NULL ORDER BY start_date DESC")
    fun getActive(): Flow<List<FermentingBatch>>
}
