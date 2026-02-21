package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.OrchardPlant
import kotlinx.coroutines.flow.Flow

@Dao
interface OrchardPlantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plant: OrchardPlant): Long

    @Update
    suspend fun update(plant: OrchardPlant)

    @Delete
    suspend fun delete(plant: OrchardPlant)

    @Query("SELECT * FROM orchard_plants ORDER BY species, variety")
    fun getAll(): Flow<List<OrchardPlant>>

    @Query("SELECT * FROM orchard_plants WHERE id = :id")
    fun getById(id: Long): Flow<OrchardPlant?>

    @Query("SELECT * FROM orchard_plants WHERE category = :category ORDER BY species")
    fun getByCategory(category: String): Flow<List<OrchardPlant>>

    @Query("SELECT * FROM orchard_plants WHERE status != 'removed' ORDER BY species")
    fun getActive(): Flow<List<OrchardPlant>>
}
