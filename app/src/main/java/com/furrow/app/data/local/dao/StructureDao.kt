package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.Structure
import kotlinx.coroutines.flow.Flow

@Dao
interface StructureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(structure: Structure): Long

    @Update
    suspend fun update(structure: Structure)

    @Delete
    suspend fun delete(structure: Structure)

    @Query("SELECT * FROM structures ORDER BY name")
    fun getAll(): Flow<List<Structure>>

    @Query("SELECT * FROM structures WHERE id = :id")
    fun getById(id: Long): Flow<Structure?>
}
