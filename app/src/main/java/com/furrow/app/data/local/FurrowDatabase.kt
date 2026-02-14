package com.furrow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.furrow.app.data.local.dao.BeeRaceInfoDao
import com.furrow.app.data.local.dao.ChickenBreedInfoDao
import com.furrow.app.data.local.dao.ChickenDao
import com.furrow.app.data.local.dao.EggLogDao
import com.furrow.app.data.local.dao.GardenBedDao
import com.furrow.app.data.local.dao.HiveDao
import com.furrow.app.data.local.dao.InspectionDao
import com.furrow.app.data.local.dao.PlantInfoDao
import com.furrow.app.data.local.dao.PlantingDao
import com.furrow.app.data.local.dao.UserProfileDao
import com.furrow.app.data.local.entity.BeeRaceInfo
import com.furrow.app.data.local.entity.Chicken
import com.furrow.app.data.local.entity.ChickenBreedInfo
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.data.local.entity.FeedLog
import com.furrow.app.data.local.entity.GardenBed
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.data.local.entity.Hive
import com.furrow.app.data.local.entity.Inspection
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.data.local.entity.Treatment
import com.furrow.app.data.local.entity.UserProfile

@Database(
    entities = [
        Hive::class,
        Inspection::class,
        Treatment::class,
        BeeRaceInfo::class,
        Chicken::class,
        ChickenBreedInfo::class,
        EggLog::class,
        FeedLog::class,
        GardenBed::class,
        Planting::class,
        HarvestLog::class,
        UserProfile::class,
        PlantInfo::class,
        PlantingWindow::class,
    ],
    version = 8,
    exportSchema = false
)
abstract class FurrowDatabase : RoomDatabase() {
    abstract fun hiveDao(): HiveDao
    abstract fun inspectionDao(): InspectionDao
    abstract fun beeRaceInfoDao(): BeeRaceInfoDao
    abstract fun chickenDao(): ChickenDao
    abstract fun chickenBreedInfoDao(): ChickenBreedInfoDao
    abstract fun eggLogDao(): EggLogDao
    abstract fun gardenBedDao(): GardenBedDao
    abstract fun plantingDao(): PlantingDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun plantInfoDao(): PlantInfoDao
}
