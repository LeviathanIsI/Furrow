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
import com.furrow.app.data.local.dao.AnimalBreedInfoDao
import com.furrow.app.data.local.dao.AnimalDao
import com.furrow.app.data.local.dao.BarterTradeDao
import com.furrow.app.data.local.dao.ApiaryDao
import com.furrow.app.data.local.dao.BeeFeedingLogDao
import com.furrow.app.data.local.dao.BeeHarvestLogDao
import com.furrow.app.data.local.dao.BeeRaceInfoDao
import com.furrow.app.data.local.dao.BloomRecordDao
import com.furrow.app.data.local.dao.BreedingRecordDao
import com.furrow.app.data.local.dao.CanningBatchDao
import com.furrow.app.data.local.dao.CropRotationLogDao
import com.furrow.app.data.local.dao.ChillHoursLogDao
import com.furrow.app.data.local.dao.CompostBinDao
import com.furrow.app.data.local.dao.CompostInputLogDao
import com.furrow.app.data.local.dao.ComplianceDocumentDao
import com.furrow.app.data.local.dao.ComplianceInspectionDao
import com.furrow.app.data.local.dao.DehydratingBatchDao
import com.furrow.app.data.local.dao.DepreciationAssetDao
import com.furrow.app.data.local.dao.EggLogDao
import com.furrow.app.data.local.dao.EnterpriseBudgetDao
import com.furrow.app.data.local.dao.ExpenseDao
import com.furrow.app.data.local.dao.FeedLogDao
import com.furrow.app.data.local.dao.FenceDao
import com.furrow.app.data.local.dao.FenceMaintenanceLogDao
import com.furrow.app.data.local.dao.FermentingBatchDao
import com.furrow.app.data.local.dao.FertilizerLogDao
import com.furrow.app.data.local.dao.FiberLogDao
import com.furrow.app.data.local.dao.FreezingBatchDao
import com.furrow.app.data.local.dao.GardenBedDao
import com.furrow.app.data.local.dao.GardenDao
import com.furrow.app.data.local.dao.GraftingLogDao
import com.furrow.app.data.local.dao.GrantRecordDao
import com.furrow.app.data.local.dao.HealthRecordDao
import com.furrow.app.data.local.dao.HiveDao
import com.furrow.app.data.local.dao.HiveEventDao
import com.furrow.app.data.local.dao.InspectionDao
import com.furrow.app.data.local.dao.LabelTemplateDao
import com.furrow.app.data.local.dao.LicensePermitDao
import com.furrow.app.data.local.dao.MileageLogDao
import com.furrow.app.data.local.dao.MilkLogDao
import com.furrow.app.data.local.dao.OrchardHarvestDao
import com.furrow.app.data.local.dao.OrchardPlantDao
import com.furrow.app.data.local.dao.PaddockDao
import com.furrow.app.data.local.dao.PantryItemDao
import com.furrow.app.data.local.dao.PastureRotationLogDao
import com.furrow.app.data.local.dao.PestDiseaseLogDao
import com.furrow.app.data.local.dao.PlantInfoDao
import com.furrow.app.data.local.dao.PlantVarietyDao
import com.furrow.app.data.local.dao.PlantingDao
import com.furrow.app.data.local.dao.ProcessingRecordDao
import com.furrow.app.data.local.dao.PropertyDao
import com.furrow.app.data.local.dao.QueenRecordDao
import com.furrow.app.data.local.dao.PruningLogDao
import com.furrow.app.data.local.dao.RevenueDao
import com.furrow.app.data.local.dao.SalesTrackerDao
import com.furrow.app.data.local.dao.SeasonExtensionDao
import com.furrow.app.data.local.dao.SeedInventoryDao
import com.furrow.app.data.local.dao.SmokingCuringBatchDao
import com.furrow.app.data.local.dao.SoilAmendmentLogDao
import com.furrow.app.data.local.dao.SoilTestDao
import com.furrow.app.data.local.dao.SprayLogDao
import com.furrow.app.data.local.dao.StructureDao
import com.furrow.app.data.local.dao.StructureMaintenanceLogDao
import com.furrow.app.data.local.dao.UserModulePreferenceDao
import com.furrow.app.data.local.dao.UserProfileDao
import com.furrow.app.data.local.dao.VarroaTestDao
import com.furrow.app.data.local.dao.WaterSourceDao
import com.furrow.app.data.local.dao.WateringLogDao
import com.furrow.app.data.local.dao.WeatherLogDao
import com.furrow.app.data.local.dao.WeightLogDao
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

    // ── Bees ──

    @Provides
    fun provideHiveDao(database: FurrowDatabase): HiveDao = database.hiveDao()

    @Provides
    fun provideInspectionDao(database: FurrowDatabase): InspectionDao = database.inspectionDao()

    @Provides
    fun provideBeeRaceInfoDao(database: FurrowDatabase): BeeRaceInfoDao =
        database.beeRaceInfoDao()

    @Provides
    fun provideApiaryDao(database: FurrowDatabase): ApiaryDao = database.apiaryDao()

    @Provides
    fun provideQueenRecordDao(database: FurrowDatabase): QueenRecordDao =
        database.queenRecordDao()

    @Provides
    fun provideVarroaTestDao(database: FurrowDatabase): VarroaTestDao = database.varroaTestDao()

    @Provides
    fun provideBeeFeedingLogDao(database: FurrowDatabase): BeeFeedingLogDao =
        database.beeFeedingLogDao()

    @Provides
    fun provideBeeHarvestLogDao(database: FurrowDatabase): BeeHarvestLogDao =
        database.beeHarvestLogDao()

    @Provides
    fun provideHiveEventDao(database: FurrowDatabase): HiveEventDao = database.hiveEventDao()

    // ── Poultry (legacy) ──

    @Provides
    fun provideEggLogDao(database: FurrowDatabase): EggLogDao = database.eggLogDao()

    @Provides
    fun provideFeedLogDao(database: FurrowDatabase): FeedLogDao = database.feedLogDao()

    // ── Garden ──

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

    @Provides
    fun provideGardenDao(database: FurrowDatabase): GardenDao = database.gardenDao()

    @Provides
    fun provideCropRotationLogDao(database: FurrowDatabase): CropRotationLogDao =
        database.cropRotationLogDao()

    @Provides
    fun provideSoilAmendmentLogDao(database: FurrowDatabase): SoilAmendmentLogDao =
        database.soilAmendmentLogDao()

    @Provides
    fun provideSeedInventoryDao(database: FurrowDatabase): SeedInventoryDao =
        database.seedInventoryDao()

    @Provides
    fun provideSeasonExtensionDao(database: FurrowDatabase): SeasonExtensionDao =
        database.seasonExtensionDao()

    // ── Animals ──

    @Provides
    fun provideAnimalDao(database: FurrowDatabase): AnimalDao = database.animalDao()

    @Provides
    fun provideAnimalBreedInfoDao(database: FurrowDatabase): AnimalBreedInfoDao =
        database.animalBreedInfoDao()

    @Provides
    fun provideHealthRecordDao(database: FurrowDatabase): HealthRecordDao =
        database.healthRecordDao()

    @Provides
    fun provideBreedingRecordDao(database: FurrowDatabase): BreedingRecordDao =
        database.breedingRecordDao()

    @Provides
    fun provideMilkLogDao(database: FurrowDatabase): MilkLogDao = database.milkLogDao()

    @Provides
    fun provideFiberLogDao(database: FurrowDatabase): FiberLogDao = database.fiberLogDao()

    @Provides
    fun provideWeightLogDao(database: FurrowDatabase): WeightLogDao = database.weightLogDao()

    @Provides
    fun provideProcessingRecordDao(database: FurrowDatabase): ProcessingRecordDao =
        database.processingRecordDao()

    // ── Orchard ──

    @Provides
    fun provideOrchardPlantDao(database: FurrowDatabase): OrchardPlantDao =
        database.orchardPlantDao()

    @Provides
    fun providePruningLogDao(database: FurrowDatabase): PruningLogDao = database.pruningLogDao()

    @Provides
    fun provideSprayLogDao(database: FurrowDatabase): SprayLogDao = database.sprayLogDao()

    @Provides
    fun provideBloomRecordDao(database: FurrowDatabase): BloomRecordDao =
        database.bloomRecordDao()

    @Provides
    fun provideOrchardHarvestDao(database: FurrowDatabase): OrchardHarvestDao =
        database.orchardHarvestDao()

    @Provides
    fun provideGraftingLogDao(database: FurrowDatabase): GraftingLogDao =
        database.graftingLogDao()

    @Provides
    fun provideChillHoursLogDao(database: FurrowDatabase): ChillHoursLogDao =
        database.chillHoursLogDao()

    // ── Food Preservation ──

    @Provides
    fun provideCanningBatchDao(database: FurrowDatabase): CanningBatchDao =
        database.canningBatchDao()

    @Provides
    fun provideDehydratingBatchDao(database: FurrowDatabase): DehydratingBatchDao =
        database.dehydratingBatchDao()

    @Provides
    fun provideFermentingBatchDao(database: FurrowDatabase): FermentingBatchDao =
        database.fermentingBatchDao()

    @Provides
    fun provideFreezingBatchDao(database: FurrowDatabase): FreezingBatchDao =
        database.freezingBatchDao()

    @Provides
    fun provideSmokingCuringBatchDao(database: FurrowDatabase): SmokingCuringBatchDao =
        database.smokingCuringBatchDao()

    @Provides
    fun providePantryItemDao(database: FurrowDatabase): PantryItemDao = database.pantryItemDao()

    // ── Land Management ──

    @Provides
    fun providePropertyDao(database: FurrowDatabase): PropertyDao = database.propertyDao()

    @Provides
    fun provideFenceDao(database: FurrowDatabase): FenceDao = database.fenceDao()

    @Provides
    fun provideFenceMaintenanceLogDao(database: FurrowDatabase): FenceMaintenanceLogDao =
        database.fenceMaintenanceLogDao()

    @Provides
    fun provideStructureDao(database: FurrowDatabase): StructureDao = database.structureDao()

    @Provides
    fun provideStructureMaintenanceLogDao(database: FurrowDatabase): StructureMaintenanceLogDao =
        database.structureMaintenanceLogDao()

    @Provides
    fun providePaddockDao(database: FurrowDatabase): PaddockDao = database.paddockDao()

    @Provides
    fun providePastureRotationLogDao(database: FurrowDatabase): PastureRotationLogDao =
        database.pastureRotationLogDao()

    @Provides
    fun provideSoilTestDao(database: FurrowDatabase): SoilTestDao = database.soilTestDao()

    @Provides
    fun provideWaterSourceDao(database: FurrowDatabase): WaterSourceDao =
        database.waterSourceDao()

    @Provides
    fun provideCompostBinDao(database: FurrowDatabase): CompostBinDao = database.compostBinDao()

    @Provides
    fun provideCompostInputLogDao(database: FurrowDatabase): CompostInputLogDao =
        database.compostInputLogDao()

    @Provides
    fun provideWeatherLogDao(database: FurrowDatabase): WeatherLogDao = database.weatherLogDao()

    // ── Finances ──

    @Provides
    fun provideExpenseDao(database: FurrowDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun provideRevenueDao(database: FurrowDatabase): RevenueDao = database.revenueDao()

    @Provides
    fun provideMileageLogDao(database: FurrowDatabase): MileageLogDao = database.mileageLogDao()

    @Provides
    fun provideBarterTradeDao(database: FurrowDatabase): BarterTradeDao =
        database.barterTradeDao()

    @Provides
    fun provideGrantRecordDao(database: FurrowDatabase): GrantRecordDao =
        database.grantRecordDao()

    @Provides
    fun provideDepreciationAssetDao(database: FurrowDatabase): DepreciationAssetDao =
        database.depreciationAssetDao()

    @Provides
    fun provideEnterpriseBudgetDao(database: FurrowDatabase): EnterpriseBudgetDao =
        database.enterpriseBudgetDao()

    // ── Compliance ──

    @Provides
    fun provideLicensePermitDao(database: FurrowDatabase): LicensePermitDao =
        database.licensePermitDao()

    @Provides
    fun provideComplianceInspectionDao(database: FurrowDatabase): ComplianceInspectionDao =
        database.complianceInspectionDao()

    @Provides
    fun provideSalesTrackerDao(database: FurrowDatabase): SalesTrackerDao =
        database.salesTrackerDao()

    @Provides
    fun provideLabelTemplateDao(database: FurrowDatabase): LabelTemplateDao =
        database.labelTemplateDao()

    @Provides
    fun provideComplianceDocumentDao(database: FurrowDatabase): ComplianceDocumentDao =
        database.complianceDocumentDao()

    @Provides
    fun provideUserModulePreferenceDao(database: FurrowDatabase): UserModulePreferenceDao =
        database.userModulePreferenceDao()
}
