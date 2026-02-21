package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.HealthRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: HealthRecord): Long

    @Update
    suspend fun update(record: HealthRecord)

    @Delete
    suspend fun delete(record: HealthRecord)

    @Query("SELECT * FROM health_records WHERE animal_id = :animalId ORDER BY date DESC")
    fun getForAnimal(animalId: Long): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE id = :id")
    fun getById(id: Long): Flow<HealthRecord?>

    @Query("SELECT * FROM health_records WHERE withdrawal_end_date > :now")
    fun getActiveWithdrawals(now: Long): Flow<List<HealthRecord>>
}
