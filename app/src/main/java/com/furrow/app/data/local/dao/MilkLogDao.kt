package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.MilkLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MilkLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: MilkLog): Long

    @Update
    suspend fun update(log: MilkLog)

    @Delete
    suspend fun delete(log: MilkLog)

    @Query("SELECT * FROM milk_logs WHERE animal_id = :animalId ORDER BY date DESC")
    fun getForAnimal(animalId: Long): Flow<List<MilkLog>>

    @Query("SELECT * FROM milk_logs WHERE id = :id")
    fun getById(id: Long): Flow<MilkLog?>
}
