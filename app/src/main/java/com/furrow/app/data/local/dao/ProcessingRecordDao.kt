package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.ProcessingRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ProcessingRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: ProcessingRecord): Long

    @Update
    suspend fun update(record: ProcessingRecord)

    @Delete
    suspend fun delete(record: ProcessingRecord)

    @Query("SELECT * FROM processing_records ORDER BY date DESC")
    fun getAll(): Flow<List<ProcessingRecord>>

    @Query("SELECT * FROM processing_records WHERE animal_id = :animalId ORDER BY date DESC")
    fun getForAnimal(animalId: Long): Flow<List<ProcessingRecord>>

    @Query("SELECT * FROM processing_records WHERE id = :id")
    fun getById(id: Long): Flow<ProcessingRecord?>
}
