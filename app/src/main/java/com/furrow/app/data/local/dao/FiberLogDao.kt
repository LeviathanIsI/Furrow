package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.FiberLog
import kotlinx.coroutines.flow.Flow

@Dao
interface FiberLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: FiberLog): Long

    @Update
    suspend fun update(log: FiberLog)

    @Delete
    suspend fun delete(log: FiberLog)

    @Query("SELECT * FROM fiber_logs WHERE animal_id = :animalId ORDER BY shearing_date DESC")
    fun getForAnimal(animalId: Long): Flow<List<FiberLog>>

    @Query("SELECT * FROM fiber_logs WHERE id = :id")
    fun getById(id: Long): Flow<FiberLog?>
}
