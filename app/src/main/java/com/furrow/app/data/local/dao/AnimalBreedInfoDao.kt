package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.AnimalBreedInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalBreedInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(breed: AnimalBreedInfo): Long

    @Update
    suspend fun update(breed: AnimalBreedInfo)

    @Delete
    suspend fun delete(breed: AnimalBreedInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(breeds: List<AnimalBreedInfo>)

    @Query("SELECT * FROM animal_breed_info ORDER BY species, name")
    fun getAllBreeds(): Flow<List<AnimalBreedInfo>>

    @Query("SELECT * FROM animal_breed_info WHERE name = :name")
    fun getBreedByName(name: String): Flow<AnimalBreedInfo?>

    @Query("SELECT * FROM animal_breed_info WHERE species = :species ORDER BY name")
    fun getBreedsBySpecies(species: String): Flow<List<AnimalBreedInfo>>

    @Query("SELECT * FROM animal_breed_info WHERE name LIKE '%' || :query || '%'")
    fun searchBreeds(query: String): Flow<List<AnimalBreedInfo>>
}
