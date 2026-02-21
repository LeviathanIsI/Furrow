package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.AnimalBreedInfoDao
import com.furrow.app.data.local.dao.AnimalDao
import com.furrow.app.data.local.dao.BreedingRecordDao
import com.furrow.app.data.local.dao.EggLogDao
import com.furrow.app.data.local.dao.FeedLogDao
import com.furrow.app.data.local.dao.FiberLogDao
import com.furrow.app.data.local.dao.HealthRecordDao
import com.furrow.app.data.local.dao.MilkLogDao
import com.furrow.app.data.local.dao.ProcessingRecordDao
import com.furrow.app.data.local.dao.WeightLogDao
import com.furrow.app.data.local.entity.Animal
import com.furrow.app.data.local.entity.AnimalBreedInfo
import com.furrow.app.data.local.entity.BreedingRecord
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.data.local.entity.FeedLog
import com.furrow.app.data.local.entity.FiberLog
import com.furrow.app.data.local.entity.HealthRecord
import com.furrow.app.data.local.entity.MilkLog
import com.furrow.app.data.local.entity.ProcessingRecord
import com.furrow.app.data.local.entity.WeightLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimalRepository @Inject constructor(
    private val animalDao: AnimalDao,
    private val breedInfoDao: AnimalBreedInfoDao,
    private val healthRecordDao: HealthRecordDao,
    private val breedingRecordDao: BreedingRecordDao,
    private val milkLogDao: MilkLogDao,
    private val fiberLogDao: FiberLogDao,
    private val weightLogDao: WeightLogDao,
    private val processingRecordDao: ProcessingRecordDao,
    private val feedLogDao: FeedLogDao,
    private val eggLogDao: EggLogDao,
) {
    // --- Animals ---

    fun getAllAnimals(): Flow<List<Animal>> = animalDao.getAll()

    fun getAnimalById(id: Long): Flow<Animal?> = animalDao.getById(id)

    fun getActiveAnimals(): Flow<List<Animal>> = animalDao.getActiveAnimals()

    fun getAnimalsBySpecies(species: String): Flow<List<Animal>> = animalDao.getBySpecies(species)

    fun getActiveAnimalsBySpecies(species: String): Flow<List<Animal>> = animalDao.getActiveBySpecies(species)

    suspend fun insertAnimal(animal: Animal): Long = animalDao.insert(animal)

    suspend fun updateAnimal(animal: Animal) = animalDao.update(animal)

    suspend fun deleteAnimal(animal: Animal) = animalDao.delete(animal)

    // --- Breed reference data ---

    fun getAllBreeds(): Flow<List<AnimalBreedInfo>> = breedInfoDao.getAllBreeds()

    fun getBreedByName(name: String): Flow<AnimalBreedInfo?> = breedInfoDao.getBreedByName(name)

    fun getBreedsBySpecies(species: String): Flow<List<AnimalBreedInfo>> = breedInfoDao.getBreedsBySpecies(species)

    fun searchBreeds(query: String): Flow<List<AnimalBreedInfo>> = breedInfoDao.searchBreeds(query)

    suspend fun insertBreed(breed: AnimalBreedInfo): Long = breedInfoDao.insert(breed)

    suspend fun updateBreed(breed: AnimalBreedInfo) = breedInfoDao.update(breed)

    suspend fun deleteBreed(breed: AnimalBreedInfo) = breedInfoDao.delete(breed)

    suspend fun insertAllBreeds(breeds: List<AnimalBreedInfo>) = breedInfoDao.insertAll(breeds)

    // --- Health records ---

    fun getHealthRecordsForAnimal(animalId: Long): Flow<List<HealthRecord>> = healthRecordDao.getForAnimal(animalId)

    fun getHealthRecordById(id: Long): Flow<HealthRecord?> = healthRecordDao.getById(id)

    fun getActiveWithdrawals(now: Long): Flow<List<HealthRecord>> = healthRecordDao.getActiveWithdrawals(now)

    suspend fun insertHealthRecord(record: HealthRecord): Long = healthRecordDao.insert(record)

    suspend fun updateHealthRecord(record: HealthRecord) = healthRecordDao.update(record)

    suspend fun deleteHealthRecord(record: HealthRecord) = healthRecordDao.delete(record)

    // --- Breeding records ---

    fun getAllBreedingRecords(): Flow<List<BreedingRecord>> = breedingRecordDao.getAll()

    fun getBreedingRecordById(id: Long): Flow<BreedingRecord?> = breedingRecordDao.getById(id)

    fun getBreedingRecordsForAnimal(animalId: Long): Flow<List<BreedingRecord>> = breedingRecordDao.getForAnimal(animalId)

    fun getUpcomingBirths(now: Long): Flow<List<BreedingRecord>> = breedingRecordDao.getUpcomingBirths(now)

    suspend fun insertBreedingRecord(record: BreedingRecord): Long = breedingRecordDao.insert(record)

    suspend fun updateBreedingRecord(record: BreedingRecord) = breedingRecordDao.update(record)

    suspend fun deleteBreedingRecord(record: BreedingRecord) = breedingRecordDao.delete(record)

    // --- Milk logs ---

    fun getMilkLogsForAnimal(animalId: Long): Flow<List<MilkLog>> = milkLogDao.getForAnimal(animalId)

    fun getMilkLogById(id: Long): Flow<MilkLog?> = milkLogDao.getById(id)

    suspend fun insertMilkLog(log: MilkLog): Long = milkLogDao.insert(log)

    suspend fun updateMilkLog(log: MilkLog) = milkLogDao.update(log)

    suspend fun deleteMilkLog(log: MilkLog) = milkLogDao.delete(log)

    // --- Fiber logs ---

    fun getFiberLogsForAnimal(animalId: Long): Flow<List<FiberLog>> = fiberLogDao.getForAnimal(animalId)

    fun getFiberLogById(id: Long): Flow<FiberLog?> = fiberLogDao.getById(id)

    suspend fun insertFiberLog(log: FiberLog): Long = fiberLogDao.insert(log)

    suspend fun updateFiberLog(log: FiberLog) = fiberLogDao.update(log)

    suspend fun deleteFiberLog(log: FiberLog) = fiberLogDao.delete(log)

    // --- Weight logs ---

    fun getWeightLogsForAnimal(animalId: Long): Flow<List<WeightLog>> = weightLogDao.getForAnimal(animalId)

    fun getWeightLogById(id: Long): Flow<WeightLog?> = weightLogDao.getById(id)

    suspend fun insertWeightLog(log: WeightLog): Long = weightLogDao.insert(log)

    suspend fun updateWeightLog(log: WeightLog) = weightLogDao.update(log)

    suspend fun deleteWeightLog(log: WeightLog) = weightLogDao.delete(log)

    // --- Processing records ---

    fun getAllProcessingRecords(): Flow<List<ProcessingRecord>> = processingRecordDao.getAll()

    fun getProcessingRecordsForAnimal(animalId: Long): Flow<List<ProcessingRecord>> = processingRecordDao.getForAnimal(animalId)

    fun getProcessingRecordById(id: Long): Flow<ProcessingRecord?> = processingRecordDao.getById(id)

    suspend fun insertProcessingRecord(record: ProcessingRecord): Long = processingRecordDao.insert(record)

    suspend fun updateProcessingRecord(record: ProcessingRecord) = processingRecordDao.update(record)

    suspend fun deleteProcessingRecord(record: ProcessingRecord) = processingRecordDao.delete(record)

    // --- Feed logs ---

    fun getAllFeedLogs(): Flow<List<FeedLog>> = feedLogDao.getAll()

    fun getFeedLogsForAnimal(animalId: Long): Flow<List<FeedLog>> = feedLogDao.getForAnimal(animalId)

    fun getFeedLogsForFlock(flockId: String): Flow<List<FeedLog>> = feedLogDao.getForFlock(flockId)

    fun getFeedLogById(id: Long): Flow<FeedLog?> = feedLogDao.getById(id)

    fun getFeedLogsForDateRange(startDate: Long, endDate: Long): Flow<List<FeedLog>> =
        feedLogDao.getForDateRange(startDate, endDate)

    suspend fun insertFeedLog(log: FeedLog): Long = feedLogDao.insert(log)

    suspend fun updateFeedLog(log: FeedLog) = feedLogDao.update(log)

    suspend fun deleteFeedLog(log: FeedLog) = feedLogDao.delete(log)

    // --- Egg logs ---

    fun getAllEggLogs(): Flow<List<EggLog>> = eggLogDao.getAllEggLogs()

    fun getEggLogById(id: Long): Flow<EggLog?> = eggLogDao.getEggLogById(id)

    fun getEggLogsForDateRange(startMillis: Long, endMillis: Long): Flow<List<EggLog>> =
        eggLogDao.getEggLogsForDateRange(startMillis, endMillis)

    fun getEggCountForDateRange(startMillis: Long, endMillis: Long): Flow<Int> =
        eggLogDao.getEggCountForDateRange(startMillis, endMillis)

    suspend fun insertEggLog(eggLog: EggLog): Long = eggLogDao.insertEggLog(eggLog)

    suspend fun updateEggLog(eggLog: EggLog) = eggLogDao.updateEggLog(eggLog)

    suspend fun deleteEggLog(eggLog: EggLog) = eggLogDao.deleteEggLog(eggLog)

    // --- Chicken convenience ---

    fun getActiveChickens(): Flow<List<Animal>> = animalDao.getActiveBySpecies("chicken")

    fun getChickenBreeds(): Flow<List<AnimalBreedInfo>> = breedInfoDao.getBreedsBySpecies("chicken")
}
