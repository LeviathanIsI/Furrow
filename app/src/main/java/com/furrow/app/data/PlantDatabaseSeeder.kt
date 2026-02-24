package com.furrow.app.data

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

object PlantDatabaseSeeder : RoomDatabase.Callback() {

    lateinit var appContext: Context

    private const val EXPECTED_PLANTS = 84
    private const val EXPECTED_WINDOWS = 440
    private const val EXPECTED_VARIETIES = 472
    private const val EXPECTED_CHICKEN_BREEDS = 31
    private const val EXPECTED_BEE_RACES = 9

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        seedAllReferenceTables(db)
    }

    /** Called from both onCreate (fresh install) and Migration (upgrade). */
    fun seedAllReferenceTables(db: SupportSQLiteDatabase) {
        seedPlantInfo(db)
        seedPlantVarieties(db)
        seedPlantingWindows(db)
        seedExpandedPlants(appContext, db)
        PlantGrowingDataSeeder.seedGrowingData(appContext, db)
        seedBreedInfo(db)
        AnimalBreedInfoSeeder.seedBreeds(appContext, db)
        seedBeeRaceInfo(db)
        val naCatalogStats = NACatalogImporter.seed(appContext, db)
        println(
            "NA catalog import: " +
                "chickens inserted=${naCatalogStats.chickens.inserted}, matched=${naCatalogStats.chickens.matched}; " +
                "bees inserted=${naCatalogStats.bees.inserted}, matched=${naCatalogStats.bees.matched}; " +
                "plants inserted=${naCatalogStats.plants.inserted}, matched=${naCatalogStats.plants.matched}",
        )
        seedModulePreferences(db)
    }

    private fun seedModulePreferences(db: SupportSQLiteDatabase) {
        com.furrow.app.data.FurrowModule.entries.forEach { module ->
            db.execSQL(
                """INSERT OR IGNORE INTO user_module_preferences (module_name, enabled, display_order)
                   VALUES (?, ?, ?)""",
                arrayOf<Any?>(
                    module.key,
                    if (module.enabledByDefault) 1 else 0,
                    module.displayOrder,
                ),
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  PLANT INFO — 84 plants loaded from plants.json
    // ═══════════════════════════════════════════════════════════════

    private fun seedPlantInfo(db: SupportSQLiteDatabase) {
        val plants = SeedLoader.loadJsonArray(appContext, "plants.json")
        check(plants.length() == EXPECTED_PLANTS) {
            "plants.json: expected $EXPECTED_PLANTS plants, got ${plants.length()}"
        }
        for (i in 0 until plants.length()) {
            val p = plants.getJSONObject(i)
            db.execSQL(
                """INSERT INTO plant_info (name, category, minZone, maxZone, daysToHarvestMin, daysToHarvestMax,
                   sunRequirement, waterFrequency, containerSuitable, containerMinGallons,
                   companionPlants, incompatiblePlants, notes,
                   plantingDepthInches, spacingInches, rowSpacingInches,
                   germinationDaysMin, germinationDaysMax,
                   plantHeight, soilPH, frostTolerant, perennial,
                   sowMethod, harvestMethod, isCustom)
                   VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0)""",
                arrayOf<Any?>(
                    p.getString("name"),
                    p.getString("category"),
                    p.getInt("minZone"),
                    p.getInt("maxZone"),
                    p.getInt("daysToHarvestMin"),
                    p.getInt("daysToHarvestMax"),
                    p.getString("sunRequirement"),
                    p.getString("waterFrequency"),
                    if (p.getBoolean("containerSuitable")) 1 else 0,
                    p.getInt("containerMinGallons"),
                    p.getString("companionPlants"),
                    p.getString("incompatiblePlants"),
                    p.optString("notes", null),
                    p.optDoubleOrNull("plantingDepthInches"),
                    p.optIntOrNull("spacingInches"),
                    p.optIntOrNull("rowSpacingInches"),
                    p.optIntOrNull("germinationDaysMin"),
                    p.optIntOrNull("germinationDaysMax"),
                    p.optString("plantHeight", null),
                    p.optString("soilPH", null),
                    if (p.optBoolean("frostTolerant", false)) 1 else 0,
                    if (p.optBoolean("perennial", false)) 1 else 0,
                    p.optString("sowMethod", null),
                    p.optString("harvestMethod", null),
                ),
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  PLANTING WINDOWS — 440 entries loaded from planting_windows.json
    // ═══════════════════════════════════════════════════════════════

    private fun seedPlantingWindows(db: SupportSQLiteDatabase) {
        val windows = SeedLoader.loadJsonArray(appContext, "planting_windows.json")
        check(windows.length() == EXPECTED_WINDOWS) {
            "planting_windows.json: expected $EXPECTED_WINDOWS windows, got ${windows.length()}"
        }
        for (i in 0 until windows.length()) {
            val w = windows.getJSONObject(i)
            db.execSQL(
                """INSERT INTO planting_windows (plantName, zoneGroup, startMonth, endMonth, method, notes)
                   VALUES (?,?,?,?,?,?)""",
                arrayOf<Any?>(
                    w.getString("plantName"),
                    w.getString("zoneGroup"),
                    w.getInt("startMonth"),
                    w.getInt("endMonth"),
                    w.getString("method"),
                    w.optString("notes", null),
                ),
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  PLANT VARIETIES — 472 entries loaded from plant_varieties.json
    // ═══════════════════════════════════════════════════════════════

    private fun seedPlantVarieties(db: SupportSQLiteDatabase) {
        val varieties = SeedLoader.loadJsonArray(appContext, "plant_varieties.json")
        check(varieties.length() == EXPECTED_VARIETIES) {
            "plant_varieties.json: expected $EXPECTED_VARIETIES varieties, got ${varieties.length()}"
        }
        for (i in 0 until varieties.length()) {
            val v = varieties.getJSONObject(i)
            db.execSQL(
                """INSERT INTO plant_variety (plantId, name, daysToHarvestMin, daysToHarvestMax, description, notes, isCustom)
                   VALUES ((SELECT id FROM plant_info WHERE name = ?), ?, ?, ?, ?, NULL, 0)""",
                arrayOf<Any?>(
                    v.getString("plantName"),
                    v.getString("name"),
                    v.optIntOrNull("daysToHarvestMin"),
                    v.optIntOrNull("daysToHarvestMax"),
                    v.getString("description"),
                ),
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  CHICKEN BREEDS — 31 breeds loaded from chicken_breeds.json
    // ═══════════════════════════════════════════════════════════════

    private fun seedBreedInfo(db: SupportSQLiteDatabase) {
        val breeds = SeedLoader.loadJsonArray(appContext, "chicken_breeds.json")
        check(breeds.length() == EXPECTED_CHICKEN_BREEDS) {
            "chicken_breeds.json: expected $EXPECTED_CHICKEN_BREEDS breeds, got ${breeds.length()}"
        }
        for (i in 0 until breeds.length()) {
            val b = breeds.getJSONObject(i)
            db.execSQL(
                """INSERT INTO animal_breed_info (species, name, eggs_per_year, egg_color, egg_size, weight, purpose,
                   temperament, comb_type, heat_tolerance, cold_tolerance, broodiness, notes, is_custom)
                   VALUES ('chicken',?,?,?,?,?,?,?,?,?,?,?,?,0)""",
                arrayOf<Any?>(
                    b.getString("name"),
                    b.getInt("eggsPerYear"),
                    b.getString("eggColor"),
                    b.getString("eggSize"),
                    b.getString("weight"),
                    b.getString("purpose"),
                    b.getString("temperament"),
                    b.getString("combType"),
                    b.getInt("heatTolerance"),
                    b.getInt("coldTolerance"),
                    b.getString("broodiness"),
                    b.optString("notes", null),
                ),
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  BEE RACE INFO — 9 races loaded from bee_races.json
    // ═══════════════════════════════════════════════════════════════

    private fun seedBeeRaceInfo(db: SupportSQLiteDatabase) {
        val races = SeedLoader.loadJsonArray(appContext, "bee_races.json")
        check(races.length() == EXPECTED_BEE_RACES) {
            "bee_races.json: expected $EXPECTED_BEE_RACES races, got ${races.length()}"
        }
        for (i in 0 until races.length()) {
            val r = races.getJSONObject(i)
            db.execSQL(
                """INSERT INTO bee_race_info (name, temperament, honeyProduction, miteResistance,
                   swarmingTendency, overwinteringAbility, climateSuitability, notes, isCustom)
                   VALUES (?,?,?,?,?,?,?,?,0)""",
                arrayOf<Any?>(
                    r.getString("name"),
                    r.getString("temperament"),
                    r.getInt("honeyProduction"),
                    r.getInt("miteResistance"),
                    r.getInt("swarmingTendency"),
                    r.getInt("overwinteringAbility"),
                    r.getString("climateSuitability"),
                    r.optString("notes", null),
                ),
            )
        }
    }
}

/** Returns null if key is missing, instead of 0. */
private fun org.json.JSONObject.optIntOrNull(key: String): Int? =
    if (has(key)) getInt(key) else null

/** Returns null if key is missing, instead of 0.0. */
private fun org.json.JSONObject.optDoubleOrNull(key: String): Double? =
    if (has(key)) getDouble(key) else null
