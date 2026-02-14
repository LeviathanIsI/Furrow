package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.GardenBed
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenBedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bed: GardenBed): Long

    @Update
    suspend fun update(bed: GardenBed)

    @Delete
    suspend fun delete(bed: GardenBed)

    @Query("SELECT * FROM garden_beds WHERE isActive = 1 ORDER BY name")
    fun getActiveBeds(): Flow<List<GardenBed>>

    @Query("SELECT * FROM garden_beds ORDER BY isActive DESC, name")
    fun getAll(): Flow<List<GardenBed>>

    @Query("SELECT * FROM garden_beds WHERE id = :id")
    fun getById(id: Long): Flow<GardenBed?>
}
