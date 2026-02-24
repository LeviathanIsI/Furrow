package com.furrow.app.data

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase

object AnimalBreedInfoSeeder {

    private const val EXPECTED_COUNT = 95

    /** Gestation / incubation days by species, useful for calculating breeding due dates. */
    val GESTATION_DAYS: Map<String, Int> = mapOf(
        "chicken" to 21,
        "duck" to 28,
        "turkey" to 28,
        "goose" to 30,
        "quail" to 17,
        "goat" to 150,
        "sheep" to 147,
        "pig" to 114,
        "cow" to 283,
        "rabbit" to 31,
        "horse" to 340,
        "alpaca" to 345,
        "llama" to 350,
        "donkey" to 365,
    )

    /**
     * Inserts breed data for all non-chicken species.
     * Chicken breeds are already seeded by [PlantDatabaseSeeder.seedBreedInfo] and [NACatalogImporter].
     */
    fun seedBreeds(context: Context, db: SupportSQLiteDatabase) {
        val breeds = SeedLoader.loadJsonArray(context, "animal_breeds.json")
        check(breeds.length() == EXPECTED_COUNT) {
            "animal_breeds.json: expected $EXPECTED_COUNT breeds, got ${breeds.length()}"
        }
        for (i in 0 until breeds.length()) {
            val b = breeds.getJSONObject(i)
            db.execSQL(
                """INSERT INTO animal_breed_info (species, name, purpose, temperament, weight,
                   gestation_days, eggs_per_year, egg_color, egg_size, milk_production_gal_day,
                   fiber_yield_lbs, mature_weight_lbs, dress_percentage, heat_tolerance,
                   cold_tolerance, climate_suitability, notes, is_custom)
                   VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0)""",
                arrayOf<Any?>(
                    b.getString("species"),
                    b.getString("name"),
                    b.optString("purpose", null),
                    b.optString("temperament", null),
                    null, // weight — unused
                    b.optIntOrNull("gestationDays"),
                    b.optIntOrNull("eggsPerYear"),
                    b.optString("eggColor", null),
                    b.optString("eggSize", null),
                    b.optDoubleOrNull("milkProductionGalDay"),
                    b.optDoubleOrNull("fiberYieldLbs"),
                    b.optDoubleOrNull("matureWeightLbs"),
                    b.optDoubleOrNull("dressPercentage"),
                    b.optIntOrNull("heatTolerance"),
                    b.optIntOrNull("coldTolerance"),
                    null, // climateSuitability — unused
                    b.optString("notes", null),
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
