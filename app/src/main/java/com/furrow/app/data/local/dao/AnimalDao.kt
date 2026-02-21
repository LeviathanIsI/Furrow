package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.Animal
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(animal: Animal): Long

    @Update
    suspend fun update(animal: Animal)

    @Delete
    suspend fun delete(animal: Animal)

    @Query("SELECT * FROM animals ORDER BY name")
    fun getAll(): Flow<List<Animal>>

    @Query("SELECT * FROM animals WHERE id = :id")
    fun getById(id: Long): Flow<Animal?>

    @Query("SELECT * FROM animals WHERE status = 'active' ORDER BY name")
    fun getActiveAnimals(): Flow<List<Animal>>

    @Query("SELECT * FROM animals WHERE species = :species ORDER BY name")
    fun getBySpecies(species: String): Flow<List<Animal>>

    @Query("SELECT * FROM animals WHERE species = :species AND status = 'active' ORDER BY name")
    fun getActiveBySpecies(species: String): Flow<List<Animal>>
}
