package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.PlantInfoDao
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.PlantingWindow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRepository @Inject constructor(
    private val plantInfoDao: PlantInfoDao,
) {
    fun getAllPlants(): Flow<List<PlantInfo>> = plantInfoDao.getAllPlants()

    fun getPlantByName(name: String): Flow<PlantInfo?> = plantInfoDao.getPlantByName(name)

    fun searchPlants(query: String): Flow<List<PlantInfo>> = plantInfoDao.searchPlants(query)

    fun getPlantsForZone(zone: Int): Flow<List<PlantInfo>> = plantInfoDao.getPlantsForZone(zone)

    fun getWindowsForZoneGroup(zoneGroup: String): Flow<List<PlantingWindow>> =
        plantInfoDao.getWindowsForZoneGroup(zoneGroup)

    fun getWindowsForPlant(plantName: String): Flow<List<PlantingWindow>> =
        plantInfoDao.getWindowsForPlant(plantName)

    fun getActiveWindowsForMonth(zoneGroup: String, month: Int): Flow<List<PlantingWindow>> =
        plantInfoDao.getActiveWindowsForMonth(zoneGroup, month)

    suspend fun insertPlant(plant: PlantInfo): Long = plantInfoDao.insert(plant)

    suspend fun updatePlant(plant: PlantInfo) = plantInfoDao.update(plant)

    suspend fun deletePlant(plant: PlantInfo) = plantInfoDao.delete(plant)
}
