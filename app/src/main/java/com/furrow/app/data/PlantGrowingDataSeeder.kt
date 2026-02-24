package com.furrow.app.data

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase

object PlantGrowingDataSeeder {

    private const val EXPECTED_COUNT = 108

    private val BOOL_COLUMNS = setOf(
        "mulchRecommended", "stakingRequired", "scarification", "canSuccessionPlant",
    )

    fun seedGrowingData(context: Context, db: SupportSQLiteDatabase) {
        val entries = SeedLoader.loadJsonArray(context, "plant_growing_data.json")
        check(entries.length() == EXPECTED_COUNT) {
            "plant_growing_data.json: expected $EXPECTED_COUNT entries, got ${entries.length()}"
        }
        for (i in 0 until entries.length()) {
            val e = entries.getJSONObject(i)
            val name = e.getString("name")

            val setClauses = mutableListOf<String>()
            val args = mutableListOf<Any?>()

            val keys = e.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                if (key == "name") continue

                if (key in BOOL_COLUMNS) {
                    if (e.getBoolean(key)) {
                        setClauses.add("$key = ?")
                        args.add(1)
                    }
                } else {
                    setClauses.add("$key = ?")
                    args.add(e.get(key))
                }
            }

            if (setClauses.isNotEmpty()) {
                args.add(name)
                db.execSQL(
                    "UPDATE plant_info SET ${setClauses.joinToString(", ")} WHERE name = ?",
                    args.toTypedArray(),
                )
            }
        }
    }
}
