package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.PlantVariety
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantVarietyDao {

    @Query("SELECT * FROM plant_variety WHERE plantId = :plantId ORDER BY name")
    fun getByPlantId(plantId: Long): Flow<List<PlantVariety>>

    @Query("SELECT * FROM plant_variety ORDER BY name")
    fun getAll(): Flow<List<PlantVariety>>

    @Insert
    suspend fun insert(variety: PlantVariety): Long

    @Insert
    suspend fun insertAll(varieties: List<PlantVariety>)

    @Update
    suspend fun update(variety: PlantVariety)

    @Delete
    suspend fun delete(variety: PlantVariety)
}
