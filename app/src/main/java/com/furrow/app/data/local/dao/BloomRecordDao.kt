package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.BloomRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface BloomRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BloomRecord): Long

    @Update
    suspend fun update(record: BloomRecord)

    @Delete
    suspend fun delete(record: BloomRecord)

    @Query("SELECT * FROM bloom_records WHERE plant_id = :plantId ORDER BY year DESC")
    fun getForPlant(plantId: Long): Flow<List<BloomRecord>>

    @Query("SELECT * FROM bloom_records WHERE id = :id")
    fun getById(id: Long): Flow<BloomRecord?>
}
