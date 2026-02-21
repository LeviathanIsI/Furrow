package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.Garden
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(garden: Garden): Long

    @Update
    suspend fun update(garden: Garden)

    @Delete
    suspend fun delete(garden: Garden)

    @Query("SELECT * FROM gardens ORDER BY name")
    fun getAll(): Flow<List<Garden>>

    @Query("SELECT * FROM gardens WHERE id = :id")
    fun getById(id: Long): Flow<Garden?>
}
