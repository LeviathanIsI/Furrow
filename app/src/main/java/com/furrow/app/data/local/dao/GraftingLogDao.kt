package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.GraftingLog
import kotlinx.coroutines.flow.Flow

@Dao
interface GraftingLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: GraftingLog): Long

    @Update
    suspend fun update(log: GraftingLog)

    @Delete
    suspend fun delete(log: GraftingLog)

    @Query("SELECT * FROM grafting_logs WHERE plant_id = :plantId ORDER BY date DESC")
    fun getForPlant(plantId: Long): Flow<List<GraftingLog>>

    @Query("SELECT * FROM grafting_logs WHERE id = :id")
    fun getById(id: Long): Flow<GraftingLog?>
}
