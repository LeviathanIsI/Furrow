package com.furrow.app.data

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Seeds the expanded plant database (141 additional plants, 300 varieties, 432 planting windows).
 * Called from PlantDatabaseSeeder.seedAllReferenceTables().
 */
internal fun seedExpandedPlants(context: Context, db: SupportSQLiteDatabase) {
    seedExpandedPlantInfo(context, db)
    seedExpandedVarieties(context, db)
    seedExpandedWindows(context, db)
}

private const val EXPECTED_PLANTS = 141
private const val EXPECTED_VARIETIES = 300
private const val EXPECTED_WINDOWS = 432

private fun seedExpandedPlantInfo(context: Context, db: SupportSQLiteDatabase) {
    val plants = SeedLoader.loadJsonArray(context, "expanded_plants.json")
    check(plants.length() == EXPECTED_PLANTS) {
        "expanded_plants.json: expected $EXPECTED_PLANTS plants, got ${plants.length()}"
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

private fun seedExpandedVarieties(context: Context, db: SupportSQLiteDatabase) {
    val varieties = SeedLoader.loadJsonArray(context, "expanded_varieties.json")
    check(varieties.length() == EXPECTED_VARIETIES) {
        "expanded_varieties.json: expected $EXPECTED_VARIETIES varieties, got ${varieties.length()}"
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

private fun seedExpandedWindows(context: Context, db: SupportSQLiteDatabase) {
    val windows = SeedLoader.loadJsonArray(context, "expanded_windows.json")
    check(windows.length() == EXPECTED_WINDOWS) {
        "expanded_windows.json: expected $EXPECTED_WINDOWS windows, got ${windows.length()}"
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

/** Returns null if key is missing, instead of 0. */
private fun org.json.JSONObject.optIntOrNull(key: String): Int? =
    if (has(key)) getInt(key) else null

/** Returns null if key is missing, instead of 0.0. */
private fun org.json.JSONObject.optDoubleOrNull(key: String): Double? =
    if (has(key)) getDouble(key) else null
