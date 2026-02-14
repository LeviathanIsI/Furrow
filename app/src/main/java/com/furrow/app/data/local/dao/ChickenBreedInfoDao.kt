package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.ChickenBreedInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ChickenBreedInfoDao {

    @Query("SELECT * FROM chicken_breed_info ORDER BY name")
    fun getAllBreeds(): Flow<List<ChickenBreedInfo>>

    @Query("SELECT * FROM chicken_breed_info WHERE name = :name")
    fun getBreedByName(name: String): Flow<ChickenBreedInfo?>

    @Query("SELECT * FROM chicken_breed_info WHERE name LIKE '%' || :query || '%'")
    fun searchBreeds(query: String): Flow<List<ChickenBreedInfo>>

    @Insert
    suspend fun insert(breed: ChickenBreedInfo): Long

    @Update
    suspend fun update(breed: ChickenBreedInfo)

    @Delete
    suspend fun delete(breed: ChickenBreedInfo)

    @Insert
    suspend fun insertAll(breeds: List<ChickenBreedInfo>)
}
