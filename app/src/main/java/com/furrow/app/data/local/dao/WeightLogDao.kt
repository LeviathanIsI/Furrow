package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.WeightLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WeightLog): Long

    @Update
    suspend fun update(log: WeightLog)

    @Delete
    suspend fun delete(log: WeightLog)

    @Query("SELECT * FROM weight_logs WHERE animal_id = :animalId ORDER BY date DESC")
    fun getForAnimal(animalId: Long): Flow<List<WeightLog>>

    @Query("SELECT * FROM weight_logs WHERE id = :id")
    fun getById(id: Long): Flow<WeightLog?>
}
