package com.furrow.app.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furrow.app.data.NACatalogImporter
import com.furrow.app.data.PlantDatabaseSeeder

/**
 * Migration from v3 (original schema) to v7 (location-aware intelligence).
 *
 * Schema changes:
 * - Creates plant_info table
 * - Creates planting_windows table
 * - Creates chicken_breed_info table
 * - Creates bee_race_info table
 * - Adds beeRace column to hives table
 *
 * Also seeds the new reference tables since onCreate() only fires on fresh installs.
 */
val MIGRATION_3_7 = object : Migration(3, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // ── New tables ──

        db.execSQL(
            """CREATE TABLE IF NOT EXISTS plant_info (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                minZone INTEGER NOT NULL,
                maxZone INTEGER NOT NULL,
                daysToHarvestMin INTEGER NOT NULL,
                daysToHarvestMax INTEGER NOT NULL,
                sunRequirement TEXT NOT NULL,
                waterFrequency TEXT NOT NULL,
                containerSuitable INTEGER NOT NULL,
                containerMinGallons INTEGER NOT NULL,
                companionPlants TEXT NOT NULL,
                incompatiblePlants TEXT NOT NULL,
                notes TEXT
            )"""
        )

        db.execSQL(
            """CREATE TABLE IF NOT EXISTS planting_windows (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                plantName TEXT NOT NULL,
                zoneGroup TEXT NOT NULL,
                startMonth INTEGER NOT NULL,
                endMonth INTEGER NOT NULL,
                method TEXT NOT NULL,
                notes TEXT
            )"""
        )

        db.execSQL(
            """CREATE TABLE IF NOT EXISTS chicken_breed_info (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                eggsPerYear INTEGER NOT NULL,
                eggColor TEXT NOT NULL,
                eggSize TEXT NOT NULL,
                weight TEXT NOT NULL,
                purpose TEXT NOT NULL,
                temperament TEXT NOT NULL,
                combType TEXT NOT NULL,
                heatTolerance INTEGER NOT NULL,
                coldTolerance INTEGER NOT NULL,
                broodiness TEXT NOT NULL,
                climateNotes TEXT
            )"""
        )

        db.execSQL(
            """CREATE TABLE IF NOT EXISTS bee_race_info (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                temperament TEXT NOT NULL,
                honeyProduction INTEGER NOT NULL,
                miteResistance INTEGER NOT NULL,
                swarmingTendency INTEGER NOT NULL,
                overwinteringAbility INTEGER NOT NULL,
                climateSuitability TEXT NOT NULL,
                notes TEXT
            )"""
        )

        // ── New column on existing table ──

        db.execSQL("ALTER TABLE hives ADD COLUMN beeRace TEXT DEFAULT NULL")

        // ── Seed reference data (onCreate won't fire during migration) ──

        PlantDatabaseSeeder.seedAllReferenceTables(db)
    }
}

val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE user_profile ADD COLUMN timezone TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_17_18 = object : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE inspections ADD COLUMN photoUri TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE harvest_logs ADD COLUMN photoUri TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE watering_logs ADD COLUMN photoUri TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE pest_disease_logs ADD COLUMN photoUri TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE health_records ADD COLUMN photoUri TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE soil_tests ADD COLUMN photoUri TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE compliance_inspections ADD COLUMN photoUri TEXT DEFAULT NULL")
    }
}

val MIGRATION_18_19 = object : Migration(18, 19) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE plantings ADD COLUMN targetPlantDate INTEGER DEFAULT NULL")
    }
}

val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val stats = NACatalogImporter.seed(PlantDatabaseSeeder.appContext, db)
        println(
            "NA catalog migration import: " +
                "chickens inserted=${stats.chickens.inserted}, matched=${stats.chickens.matched}; " +
                "bees inserted=${stats.bees.inserted}, matched=${stats.bees.matched}; " +
                "plants inserted=${stats.plants.inserted}, matched=${stats.plants.matched}",
        )
    }
}
