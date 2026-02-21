package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.BreedingRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedingRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BreedingRecord): Long

    @Update
    suspend fun update(record: BreedingRecord)

    @Delete
    suspend fun delete(record: BreedingRecord)

    @Query("SELECT * FROM breeding_records ORDER BY breeding_date DESC")
    fun getAll(): Flow<List<BreedingRecord>>

    @Query("SELECT * FROM breeding_records WHERE id = :id")
    fun getById(id: Long): Flow<BreedingRecord?>

    @Query("SELECT * FROM breeding_records WHERE sire_id = :animalId OR dam_id = :animalId ORDER BY breeding_date DESC")
    fun getForAnimal(animalId: Long): Flow<List<BreedingRecord>>

    @Query("SELECT * FROM breeding_records WHERE due_date > :now AND birth_date IS NULL ORDER BY due_date ASC")
    fun getUpcomingBirths(now: Long): Flow<List<BreedingRecord>>
}
