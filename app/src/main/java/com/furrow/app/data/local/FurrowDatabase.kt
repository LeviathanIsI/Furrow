package com.furrow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.furrow.app.data.local.dao.AnimalBreedInfoDao
import com.furrow.app.data.local.dao.AnimalDao
import com.furrow.app.data.local.dao.ApiaryDao
import com.furrow.app.data.local.dao.BarterTradeDao
import com.furrow.app.data.local.dao.BeeFeedingLogDao
import com.furrow.app.data.local.dao.BeeHarvestLogDao
import com.furrow.app.data.local.dao.BeeRaceInfoDao
import com.furrow.app.data.local.dao.BloomRecordDao
import com.furrow.app.data.local.dao.BreedingRecordDao
import com.furrow.app.data.local.dao.CanningBatchDao
import com.furrow.app.data.local.dao.ChillHoursLogDao
import com.furrow.app.data.local.dao.CompostBinDao
import com.furrow.app.data.local.dao.CropRotationLogDao
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
import com.furrow.app.data.local.dao.PruningLogDao
import com.furrow.app.data.local.dao.QueenRecordDao
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
import com.furrow.app.data.local.entity.Animal
import com.furrow.app.data.local.entity.AnimalBreedInfo
import com.furrow.app.data.local.entity.Apiary
import com.furrow.app.data.local.entity.BarterTrade
import com.furrow.app.data.local.entity.BeeFeedingLog
import com.furrow.app.data.local.entity.BeeHarvestLog
import com.furrow.app.data.local.entity.BeeRaceInfo
import com.furrow.app.data.local.entity.BloomRecord
import com.furrow.app.data.local.entity.BreedingRecord
import com.furrow.app.data.local.entity.CanningBatch
import com.furrow.app.data.local.entity.ChillHoursLog
import com.furrow.app.data.local.entity.CompostBin
import com.furrow.app.data.local.entity.CropRotationLog
import com.furrow.app.data.local.entity.CompostInputLog
import com.furrow.app.data.local.entity.ComplianceDocument
import com.furrow.app.data.local.entity.ComplianceInspection
import com.furrow.app.data.local.entity.DehydratingBatch
import com.furrow.app.data.local.entity.DepreciationAsset
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.data.local.entity.EnterpriseBudget
import com.furrow.app.data.local.entity.Expense
import com.furrow.app.data.local.entity.FeedLog
import com.furrow.app.data.local.entity.Fence
import com.furrow.app.data.local.entity.FenceMaintenanceLog
import com.furrow.app.data.local.entity.FermentingBatch
import com.furrow.app.data.local.entity.FertilizerLog
import com.furrow.app.data.local.entity.FiberLog
import com.furrow.app.data.local.entity.FreezingBatch
import com.furrow.app.data.local.entity.Garden
import com.furrow.app.data.local.entity.GardenBed
import com.furrow.app.data.local.entity.GraftingLog
import com.furrow.app.data.local.entity.GrantRecord
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.data.local.entity.HealthRecord
import com.furrow.app.data.local.entity.Hive
import com.furrow.app.data.local.entity.HiveEvent
import com.furrow.app.data.local.entity.Inspection
import com.furrow.app.data.local.entity.LabelTemplate
import com.furrow.app.data.local.entity.LicensePermit
import com.furrow.app.data.local.entity.MileageLog
import com.furrow.app.data.local.entity.MilkLog
import com.furrow.app.data.local.entity.OrchardHarvest
import com.furrow.app.data.local.entity.OrchardPlant
import com.furrow.app.data.local.entity.Paddock
import com.furrow.app.data.local.entity.PantryItem
import com.furrow.app.data.local.entity.PastureRotationLog
import com.furrow.app.data.local.entity.PestDiseaseLog
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.PlantVariety
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.data.local.entity.ProcessingRecord
import com.furrow.app.data.local.entity.Property
import com.furrow.app.data.local.entity.PruningLog
import com.furrow.app.data.local.entity.QueenRecord
import com.furrow.app.data.local.entity.Revenue
import com.furrow.app.data.local.entity.SalesTracker
import com.furrow.app.data.local.entity.SeasonExtension
import com.furrow.app.data.local.entity.SeedInventory
import com.furrow.app.data.local.entity.SmokingCuringBatch
import com.furrow.app.data.local.entity.SoilAmendmentLog
import com.furrow.app.data.local.entity.SoilTest
import com.furrow.app.data.local.entity.SprayLog
import com.furrow.app.data.local.entity.Structure
import com.furrow.app.data.local.entity.StructureMaintenanceLog
import com.furrow.app.data.local.entity.Treatment
import com.furrow.app.data.local.entity.UserModulePreference
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.data.local.entity.VarroaTest
import com.furrow.app.data.local.entity.WaterSource
import com.furrow.app.data.local.entity.WateringLog
import com.furrow.app.data.local.entity.WeatherLog
import com.furrow.app.data.local.entity.WeightLog

@Database(
    entities = [
        // Bees
        Hive::class,
        Inspection::class,
        Treatment::class,
        BeeRaceInfo::class,
        Apiary::class,
        QueenRecord::class,
        VarroaTest::class,
        BeeFeedingLog::class,
        BeeHarvestLog::class,
        HiveEvent::class,
        // Poultry (legacy egg/feed logs)
        EggLog::class,
        FeedLog::class,
        // Garden
        Garden::class,
        GardenBed::class,
        Planting::class,
        HarvestLog::class,
        PlantInfo::class,
        PlantingWindow::class,
        PlantVariety::class,
        WateringLog::class,
        FertilizerLog::class,
        PestDiseaseLog::class,
        CropRotationLog::class,
        SoilAmendmentLog::class,
        SeedInventory::class,
        SeasonExtension::class,
        // User
        UserProfile::class,
        // Animals (universal)
        Animal::class,
        AnimalBreedInfo::class,
        HealthRecord::class,
        BreedingRecord::class,
        MilkLog::class,
        FiberLog::class,
        WeightLog::class,
        ProcessingRecord::class,
        // Orchard
        OrchardPlant::class,
        PruningLog::class,
        SprayLog::class,
        BloomRecord::class,
        OrchardHarvest::class,
        GraftingLog::class,
        ChillHoursLog::class,
        // Food Preservation
        CanningBatch::class,
        DehydratingBatch::class,
        FermentingBatch::class,
        FreezingBatch::class,
        SmokingCuringBatch::class,
        PantryItem::class,
        // Land Management
        Property::class,
        Fence::class,
        FenceMaintenanceLog::class,
        Structure::class,
        StructureMaintenanceLog::class,
        Paddock::class,
        PastureRotationLog::class,
        SoilTest::class,
        WaterSource::class,
        CompostBin::class,
        CompostInputLog::class,
        WeatherLog::class,
        // Finances
        Expense::class,
        Revenue::class,
        MileageLog::class,
        BarterTrade::class,
        GrantRecord::class,
        DepreciationAsset::class,
        EnterpriseBudget::class,
        // Compliance
        LicensePermit::class,
        ComplianceInspection::class,
        SalesTracker::class,
        LabelTemplate::class,
        ComplianceDocument::class,
        UserModulePreference::class,
    ],
    version = 17,
    exportSchema = false,
)
abstract class FurrowDatabase : RoomDatabase() {
    // Bees
    abstract fun hiveDao(): HiveDao
    abstract fun inspectionDao(): InspectionDao
    abstract fun beeRaceInfoDao(): BeeRaceInfoDao
    abstract fun apiaryDao(): ApiaryDao
    abstract fun queenRecordDao(): QueenRecordDao
    abstract fun varroaTestDao(): VarroaTestDao
    abstract fun beeFeedingLogDao(): BeeFeedingLogDao
    abstract fun beeHarvestLogDao(): BeeHarvestLogDao
    abstract fun hiveEventDao(): HiveEventDao
    // Poultry (legacy)
    abstract fun eggLogDao(): EggLogDao
    abstract fun feedLogDao(): FeedLogDao
    // Garden
    abstract fun gardenDao(): GardenDao
    abstract fun gardenBedDao(): GardenBedDao
    abstract fun plantingDao(): PlantingDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun plantInfoDao(): PlantInfoDao
    abstract fun plantVarietyDao(): PlantVarietyDao
    abstract fun wateringLogDao(): WateringLogDao
    abstract fun fertilizerLogDao(): FertilizerLogDao
    abstract fun pestDiseaseLogDao(): PestDiseaseLogDao
    abstract fun cropRotationLogDao(): CropRotationLogDao
    abstract fun soilAmendmentLogDao(): SoilAmendmentLogDao
    abstract fun seedInventoryDao(): SeedInventoryDao
    abstract fun seasonExtensionDao(): SeasonExtensionDao
    // Animals
    abstract fun animalDao(): AnimalDao
    abstract fun animalBreedInfoDao(): AnimalBreedInfoDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun breedingRecordDao(): BreedingRecordDao
    abstract fun milkLogDao(): MilkLogDao
    abstract fun fiberLogDao(): FiberLogDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun processingRecordDao(): ProcessingRecordDao
    // Orchard
    abstract fun orchardPlantDao(): OrchardPlantDao
    abstract fun pruningLogDao(): PruningLogDao
    abstract fun sprayLogDao(): SprayLogDao
    abstract fun bloomRecordDao(): BloomRecordDao
    abstract fun orchardHarvestDao(): OrchardHarvestDao
    abstract fun graftingLogDao(): GraftingLogDao
    abstract fun chillHoursLogDao(): ChillHoursLogDao
    // Food Preservation
    abstract fun canningBatchDao(): CanningBatchDao
    abstract fun dehydratingBatchDao(): DehydratingBatchDao
    abstract fun fermentingBatchDao(): FermentingBatchDao
    abstract fun freezingBatchDao(): FreezingBatchDao
    abstract fun smokingCuringBatchDao(): SmokingCuringBatchDao
    abstract fun pantryItemDao(): PantryItemDao
    // Land Management
    abstract fun propertyDao(): PropertyDao
    abstract fun fenceDao(): FenceDao
    abstract fun fenceMaintenanceLogDao(): FenceMaintenanceLogDao
    abstract fun structureDao(): StructureDao
    abstract fun structureMaintenanceLogDao(): StructureMaintenanceLogDao
    abstract fun paddockDao(): PaddockDao
    abstract fun pastureRotationLogDao(): PastureRotationLogDao
    abstract fun soilTestDao(): SoilTestDao
    abstract fun waterSourceDao(): WaterSourceDao
    abstract fun compostBinDao(): CompostBinDao
    abstract fun compostInputLogDao(): CompostInputLogDao
    abstract fun weatherLogDao(): WeatherLogDao
    // Finances
    abstract fun expenseDao(): ExpenseDao
    abstract fun revenueDao(): RevenueDao
    abstract fun mileageLogDao(): MileageLogDao
    abstract fun barterTradeDao(): BarterTradeDao
    abstract fun grantRecordDao(): GrantRecordDao
    abstract fun depreciationAssetDao(): DepreciationAssetDao
    abstract fun enterpriseBudgetDao(): EnterpriseBudgetDao
    // Compliance
    abstract fun licensePermitDao(): LicensePermitDao
    abstract fun complianceInspectionDao(): ComplianceInspectionDao
    abstract fun salesTrackerDao(): SalesTrackerDao
    abstract fun labelTemplateDao(): LabelTemplateDao
    abstract fun complianceDocumentDao(): ComplianceDocumentDao
    abstract fun userModulePreferenceDao(): UserModulePreferenceDao
}
