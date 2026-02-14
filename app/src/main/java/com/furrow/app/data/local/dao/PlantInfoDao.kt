package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.PlantingWindow
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantInfoDao {

    @Query("SELECT * FROM plant_info ORDER BY name")
    fun getAllPlants(): Flow<List<PlantInfo>>

    @Query("SELECT * FROM plant_info WHERE name = :name")
    fun getPlantByName(name: String): Flow<PlantInfo?>

    @Query("SELECT * FROM plant_info WHERE name LIKE '%' || :query || '%'")
    fun searchPlants(query: String): Flow<List<PlantInfo>>

    @Query("SELECT * FROM plant_info WHERE minZone <= :zone AND maxZone >= :zone ORDER BY name")
    fun getPlantsForZone(zone: Int): Flow<List<PlantInfo>>

    @Insert
    suspend fun insert(plant: PlantInfo): Long

    @Update
    suspend fun update(plant: PlantInfo)

    @Delete
    suspend fun delete(plant: PlantInfo)

    @Insert
    suspend fun insertAll(plants: List<PlantInfo>)

    @Query("SELECT * FROM planting_windows WHERE zoneGroup = :zoneGroup ORDER BY startMonth")
    fun getWindowsForZoneGroup(zoneGroup: String): Flow<List<PlantingWindow>>

    @Query("SELECT * FROM planting_windows WHERE plantName = :plantName ORDER BY zoneGroup, startMonth")
    fun getWindowsForPlant(plantName: String): Flow<List<PlantingWindow>>

    @Query(
        """SELECT * FROM planting_windows WHERE zoneGroup = :zoneGroup AND (
            (startMonth <= endMonth AND :month >= startMonth AND :month <= endMonth) OR
            (startMonth > endMonth AND (:month >= startMonth OR :month <= endMonth))
        ) ORDER BY plantName"""
    )
    fun getActiveWindowsForMonth(zoneGroup: String, month: Int): Flow<List<PlantingWindow>>

    @Insert
    suspend fun insertAllWindows(windows: List<PlantingWindow>)
}
