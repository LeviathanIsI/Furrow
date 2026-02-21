package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.AnimalBreedInfoDao
import com.furrow.app.data.local.dao.AnimalDao
import com.furrow.app.data.local.dao.EggLogDao
import com.furrow.app.data.local.entity.Animal
import com.furrow.app.data.local.entity.AnimalBreedInfo
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.data.local.entity.FeedLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoultryRepository @Inject constructor(
    private val animalDao: AnimalDao,
    private val eggLogDao: EggLogDao,
    private val breedInfoDao: AnimalBreedInfoDao,
) {
    // --- Breed reference data ---

    fun getAllBreeds(): Flow<List<AnimalBreedInfo>> = breedInfoDao.getBreedsBySpecies("chicken")

    fun getBreedByName(name: String): Flow<AnimalBreedInfo?> = breedInfoDao.getBreedByName(name)

    fun searchBreeds(query: String): Flow<List<AnimalBreedInfo>> = breedInfoDao.searchBreeds(query)

    suspend fun insertBreed(breed: AnimalBreedInfo): Long = breedInfoDao.insert(breed)

    suspend fun updateBreed(breed: AnimalBreedInfo) = breedInfoDao.update(breed)

    suspend fun deleteBreed(breed: AnimalBreedInfo) = breedInfoDao.delete(breed)

    // --- Animals (chickens) ---

    fun getActiveAnimals(): Flow<List<Animal>> = animalDao.getActiveBySpecies("chicken")

    fun getAllAnimals(): Flow<List<Animal>> = animalDao.getBySpecies("chicken")

    fun getAnimalById(id: Long): Flow<Animal?> = animalDao.getById(id)

    suspend fun insertAnimal(animal: Animal): Long = animalDao.insert(animal)

    suspend fun updateAnimal(animal: Animal) = animalDao.update(animal)

    suspend fun deleteAnimal(animal: Animal) = animalDao.delete(animal)

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
