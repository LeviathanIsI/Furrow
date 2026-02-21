package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.WaterSource
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterSourceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(waterSource: WaterSource): Long

    @Update
    suspend fun update(waterSource: WaterSource)

    @Delete
    suspend fun delete(waterSource: WaterSource)

    @Query("SELECT * FROM water_sources ORDER BY type")
    fun getAll(): Flow<List<WaterSource>>

    @Query("SELECT * FROM water_sources WHERE id = :id")
    fun getById(id: Long): Flow<WaterSource?>
}
