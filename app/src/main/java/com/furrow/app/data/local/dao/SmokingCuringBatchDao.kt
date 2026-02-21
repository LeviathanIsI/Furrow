package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.SmokingCuringBatch
import kotlinx.coroutines.flow.Flow

@Dao
interface SmokingCuringBatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(batch: SmokingCuringBatch): Long

    @Update
    suspend fun update(batch: SmokingCuringBatch)

    @Delete
    suspend fun delete(batch: SmokingCuringBatch)

    @Query("SELECT * FROM smoking_curing_batches ORDER BY cure_start DESC")
    fun getAll(): Flow<List<SmokingCuringBatch>>

    @Query("SELECT * FROM smoking_curing_batches WHERE id = :id")
    fun getById(id: Long): Flow<SmokingCuringBatch?>
}
