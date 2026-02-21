package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.OrchardHarvest
import kotlinx.coroutines.flow.Flow

@Dao
interface OrchardHarvestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(harvest: OrchardHarvest): Long

    @Update
    suspend fun update(harvest: OrchardHarvest)

    @Delete
    suspend fun delete(harvest: OrchardHarvest)

    @Query("SELECT * FROM orchard_harvests WHERE plant_id = :plantId ORDER BY date DESC")
    fun getForPlant(plantId: Long): Flow<List<OrchardHarvest>>

    @Query("SELECT * FROM orchard_harvests WHERE id = :id")
    fun getById(id: Long): Flow<OrchardHarvest?>

    @Query("SELECT * FROM orchard_harvests ORDER BY date DESC")
    fun getAll(): Flow<List<OrchardHarvest>>
}
