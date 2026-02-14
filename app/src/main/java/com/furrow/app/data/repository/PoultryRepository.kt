package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.ChickenBreedInfoDao
import com.furrow.app.data.local.dao.ChickenDao
import com.furrow.app.data.local.dao.EggLogDao
import com.furrow.app.data.local.entity.Chicken
import com.furrow.app.data.local.entity.ChickenBreedInfo
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.data.local.entity.FeedLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoultryRepository @Inject constructor(
    private val chickenDao: ChickenDao,
    private val eggLogDao: EggLogDao,
    private val breedInfoDao: ChickenBreedInfoDao,
) {
    // --- Breed reference data ---

    fun getAllBreeds(): Flow<List<ChickenBreedInfo>> = breedInfoDao.getAllBreeds()

    fun getBreedByName(name: String): Flow<ChickenBreedInfo?> = breedInfoDao.getBreedByName(name)

    fun searchBreeds(query: String): Flow<List<ChickenBreedInfo>> = breedInfoDao.searchBreeds(query)

    suspend fun insertBreed(breed: ChickenBreedInfo): Long = breedInfoDao.insert(breed)

    suspend fun updateBreed(breed: ChickenBreedInfo) = breedInfoDao.update(breed)

    suspend fun deleteBreed(breed: ChickenBreedInfo) = breedInfoDao.delete(breed)

    // --- Chickens ---

    fun getActiveChickens(): Flow<List<Chicken>> = chickenDao.getActiveChickens()

    fun getAllChickens(): Flow<List<Chicken>> = chickenDao.getAll()

    fun getChickenById(id: Long): Flow<Chicken?> = chickenDao.getById(id)

    suspend fun insertChicken(chicken: Chicken): Long = chickenDao.insert(chicken)

    suspend fun updateChicken(chicken: Chicken) = chickenDao.update(chicken)

    suspend fun deleteChicken(chicken: Chicken) = chickenDao.delete(chicken)

    // --- Egg logs ---

    fun getEggLogById(id: Long): Flow<EggLog?> = eggLogDao.getEggLogById(id)

    fun getAllEggLogs(): Flow<List<EggLog>> = eggLogDao.getAllEggLogs()

    fun getEggLogsForDateRange(startMillis: Long, endMillis: Long): Flow<List<EggLog>> =
        eggLogDao.getEggLogsForDateRange(startMillis, endMillis)

    fun getEggCountForDateRange(startMillis: Long, endMillis: Long): Flow<Int> =
        eggLogDao.getEggCountForDateRange(startMillis, endMillis)

    suspend fun insertEggLog(eggLog: EggLog): Long = eggLogDao.insertEggLog(eggLog)

    suspend fun updateEggLog(eggLog: EggLog) = eggLogDao.updateEggLog(eggLog)

    suspend fun deleteEggLog(eggLog: EggLog) = eggLogDao.deleteEggLog(eggLog)

    // --- Feed logs ---

    fun getAllFeedLogs(): Flow<List<FeedLog>> = eggLogDao.getAllFeedLogs()

    suspend fun insertFeedLog(feedLog: FeedLog): Long = eggLogDao.insertFeedLog(feedLog)

    suspend fun updateFeedLog(feedLog: FeedLog) = eggLogDao.updateFeedLog(feedLog)

    suspend fun deleteFeedLog(feedLog: FeedLog) = eggLogDao.deleteFeedLog(feedLog)
}
