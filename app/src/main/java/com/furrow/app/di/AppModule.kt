package com.furrow.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.furrow.app.data.PlantDatabaseSeeder
import com.furrow.app.data.local.FurrowDatabase
import com.furrow.app.data.local.MIGRATION_12_13
import com.furrow.app.data.local.MIGRATION_3_7
import com.furrow.app.data.local.dao.BeeRaceInfoDao
import com.furrow.app.data.local.dao.ChickenBreedInfoDao
import com.furrow.app.data.local.dao.ChickenDao
import com.furrow.app.data.local.dao.EggLogDao
import com.furrow.app.data.local.dao.GardenBedDao
import com.furrow.app.data.local.dao.HiveDao
import com.furrow.app.data.local.dao.InspectionDao
import com.furrow.app.data.local.dao.PlantInfoDao
import com.furrow.app.data.local.dao.FertilizerLogDao
import com.furrow.app.data.local.dao.PestDiseaseLogDao
import com.furrow.app.data.local.dao.PlantVarietyDao
import com.furrow.app.data.local.dao.PlantingDao
import com.furrow.app.data.local.dao.UserProfileDao
import com.furrow.app.data.local.dao.WateringLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.furrowDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "furrow_preferences",
)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.furrowDataStore

    @Provides
    @Singleton
    fun provideFurrowDatabase(@ApplicationContext context: Context): FurrowDatabase {
        return Room.databaseBuilder(
            context,
            FurrowDatabase::class.java,
            "furrow_database"
        ).addMigrations(MIGRATION_3_7, MIGRATION_12_13)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .addCallback(PlantDatabaseSeeder)
            .build()
    }

    @Provides
    fun provideHiveDao(database: FurrowDatabase): HiveDao = database.hiveDao()

    @Provides
    fun provideInspectionDao(database: FurrowDatabase): InspectionDao = database.inspectionDao()

    @Provides
    fun provideBeeRaceInfoDao(database: FurrowDatabase): BeeRaceInfoDao =
        database.beeRaceInfoDao()

    @Provides
    fun provideChickenDao(database: FurrowDatabase): ChickenDao = database.chickenDao()

    @Provides
    fun provideChickenBreedInfoDao(database: FurrowDatabase): ChickenBreedInfoDao =
        database.chickenBreedInfoDao()

    @Provides
    fun provideEggLogDao(database: FurrowDatabase): EggLogDao = database.eggLogDao()

    @Provides
    fun provideGardenBedDao(database: FurrowDatabase): GardenBedDao = database.gardenBedDao()

    @Provides
    fun providePlantingDao(database: FurrowDatabase): PlantingDao = database.plantingDao()

    @Provides
    fun provideUserProfileDao(database: FurrowDatabase): UserProfileDao = database.userProfileDao()

    @Provides
    fun providePlantInfoDao(database: FurrowDatabase): PlantInfoDao = database.plantInfoDao()

    @Provides
    fun providePlantVarietyDao(database: FurrowDatabase): PlantVarietyDao =
        database.plantVarietyDao()

    @Provides
    fun provideWateringLogDao(database: FurrowDatabase): WateringLogDao =
        database.wateringLogDao()

    @Provides
    fun provideFertilizerLogDao(database: FurrowDatabase): FertilizerLogDao =
        database.fertilizerLogDao()

    @Provides
    fun providePestDiseaseLogDao(database: FurrowDatabase): PestDiseaseLogDao =
        database.pestDiseaseLogDao()
}
