package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.SoilTest
import kotlinx.coroutines.flow.Flow

@Dao
interface SoilTestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(soilTest: SoilTest): Long

    @Update
    suspend fun update(soilTest: SoilTest)

    @Delete
    suspend fun delete(soilTest: SoilTest)

    @Query("SELECT * FROM soil_tests ORDER BY date DESC")
    fun getAll(): Flow<List<SoilTest>>

    @Query("SELECT * FROM soil_tests WHERE id = :id")
    fun getById(id: Long): Flow<SoilTest?>
}
