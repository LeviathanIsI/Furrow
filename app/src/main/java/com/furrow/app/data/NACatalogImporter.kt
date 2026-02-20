package com.furrow.app.data

import androidx.sqlite.db.SupportSQLiteDatabase
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

data class CatalogCategoryStats(
    var inserted: Int = 0,
    var matched: Int = 0,
)

data class CatalogImportStats(
    val chickens: CatalogCategoryStats = CatalogCategoryStats(),
    val bees: CatalogCategoryStats = CatalogCategoryStats(),
    val plants: CatalogCategoryStats = CatalogCategoryStats(),
)

object NACatalogImporter {
    private val scientificRegex = Regex("""(?i)\bscientific:\s*([^|]+)""")

    fun seed(db: SupportSQLiteDatabase): CatalogImportStats {
        val root = JSONObject(NACatalogJson.RAW)
        val stats = CatalogImportStats()

        importChickens(db, root.optJSONArray("chickens") ?: JSONArray(), stats.chickens)
        importBees(db, root.optJSONArray("bees") ?: JSONArray(), stats.bees)
        importPlants(db, root.optJSONArray("plants") ?: JSONArray(), stats.plants)

        return stats
    }

    private fun importChickens(
        db: SupportSQLiteDatabase,
        items: JSONArray,
        stats: CatalogCategoryStats,
    ) {
        val existingNames = readNormalizedNameSet(db, "chicken_breed_info", "name")
        val seen = mutableSetOf<String>()

        for (i in 0 until items.length()) {
            val obj = items.optJSONObject(i) ?: continue
            val commonName = obj.optString("common_name").trim()
            if (commonName.isBlank()) continue

            val dedupeKey = pickDedupeKey(obj, commonName)
            if (!seen.add(dedupeKey)) continue

            val normalizedCommon = normalize(commonName)
            if (existingNames.contains(normalizedCommon)) {
                stats.matched += 1
                continue
            }

            val primaryPurpose = obj.optJSONArray("primary_purpose")
            val tags = jsonArrayToList(obj.optJSONArray("tags"))
            val notes = obj.optString("notes").trim()
            val scientific = obj.optString("scientific_name").trim().ifBlank { null }
            val sizeClass = obj.optString("size_class").trim().ifBlank { "standard" }

            val purpose = when {
                purposeContains(primaryPurpose, "dual-purpose") -> "dual"
                purposeContains(primaryPurpose, "eggs") -> "layer"
                purposeContains(primaryPurpose, "meat") -> "meat"
                else -> "ornamental"
            }

            val eggsPerYear = when (purpose) {
                "layer" -> 240
                "dual" -> 190
                else -> 120
            }

            val climateNotes = composeNotes(scientific, tags, notes)
            val weight = if (sizeClass.equals("bantam", ignoreCase = true)) "2 lb" else "6 lb"
            val broodiness = if (purposeContains(primaryPurpose, "broody")) "high" else "moderate"
            val heatTolerance = if (tags.any { it.equals("commercial", ignoreCase = true) }) 4 else 3
            val coldTolerance = if (tags.any { it.equals("heritage", ignoreCase = true) }) 4 else 3

            db.execSQL(
                """INSERT INTO chicken_breed_info
                   (name, eggsPerYear, eggColor, eggSize, weight, purpose, temperament,
                    combType, heatTolerance, coldTolerance, broodiness, climateNotes, isCustom)
                   VALUES (?,?,?,?,?,?,?,?,?,?,?,?,0)""",
                arrayOf<Any?>(
                    commonName,
                    eggsPerYear,
                    "Brown",
                    "Medium",
                    weight,
                    purpose,
                    "Calm",
                    "Single",
                    heatTolerance,
                    coldTolerance,
                    broodiness,
                    climateNotes,
                ),
            )

            existingNames.add(normalizedCommon)
            stats.inserted += 1
        }
    }

    private fun importBees(
        db: SupportSQLiteDatabase,
        items: JSONArray,
        stats: CatalogCategoryStats,
    ) {
        val existingNames = readNormalizedNameSet(db, "bee_race_info", "name")
        val existingScientific =
            readScientificSetFromNotes(db, "bee_race_info", "notes")
        val seen = mutableSetOf<String>()

        for (i in 0 until items.length()) {
            val obj = items.optJSONObject(i) ?: continue
            val commonName = obj.optString("common_name").trim()
            if (commonName.isBlank()) continue

            val scientific = obj.optString("scientific_name").trim().ifBlank { null }
            val dedupeKey = pickDedupeKey(obj, commonName)
            if (!seen.add(dedupeKey)) continue

            val normalizedCommon = normalize(commonName)
            val normalizedScientific = scientific?.let(::normalize)

            if ((normalizedScientific != null && existingScientific.contains(normalizedScientific))
                || existingNames.contains(normalizedCommon)
            ) {
                stats.matched += 1
                continue
            }

            val tags = jsonArrayToList(obj.optJSONArray("tags"))
            val notes = obj.optString("notes").trim()
            val combinedNotes = composeNotes(scientific, tags, notes)

            val climateSuitability = when {
                tags.any { it.equals("greenhouse", ignoreCase = true) } -> "warm"
                tags.any { it.equals("regional", ignoreCase = true) } -> "moderate"
                else -> "all climates"
            }

            db.execSQL(
                """INSERT INTO bee_race_info
                   (name, temperament, honeyProduction, miteResistance, swarmingTendency,
                    overwinteringAbility, climateSuitability, notes, isCustom)
                   VALUES (?,?,?,?,?,?,?,?,0)""",
                arrayOf<Any?>(
                    commonName,
                    "Moderate",
                    if (tags.any { it.equals("commercial", ignoreCase = true) }) 4 else 3,
                    if (tags.any { it.equals("mite-tolerant", ignoreCase = true) }) 5 else 3,
                    3,
                    3,
                    climateSuitability,
                    combinedNotes,
                ),
            )

            existingNames.add(normalizedCommon)
            normalizedScientific?.let { existingScientific.add(it) }
            stats.inserted += 1
        }
    }

    private fun importPlants(
        db: SupportSQLiteDatabase,
        items: JSONArray,
        stats: CatalogCategoryStats,
    ) {
        val existingNames = readNormalizedNameSet(db, "plant_info", "name")
        val existingScientific =
            readScientificSetFromNotes(db, "plant_info", "notes")
        val seen = mutableSetOf<String>()

        for (i in 0 until items.length()) {
            val obj = items.optJSONObject(i) ?: continue
            val commonName = obj.optString("common_name").trim()
            if (commonName.isBlank()) continue

            val scientific = obj.optString("scientific_name").trim().ifBlank { null }
            val dedupeKey = pickDedupeKey(obj, commonName)
            if (!seen.add(dedupeKey)) continue

            val normalizedCommon = normalize(commonName)
            val normalizedScientific = scientific?.let(::normalize)

            if ((normalizedScientific != null && existingScientific.contains(normalizedScientific))
                || existingNames.contains(normalizedCommon)
            ) {
                stats.matched += 1
                continue
            }

            val plantType = obj.optString("plant_type").trim().ifBlank { "vegetable" }
            val tags = jsonArrayToList(obj.optJSONArray("tags"))
            val notes = obj.optString("notes").trim()
            val combinedNotes = composeNotes(scientific, tags, notes)

            val category = when (plantType.lowercase(Locale.US)) {
                "fruit_tree", "berry", "vine" -> "fruit"
                "nut_tree" -> "nut"
                "cover_crop", "grain" -> "grain"
                "flower" -> "flower"
                "herb" -> "herb"
                else -> "vegetable"
            }

            val containerSuitable = when (plantType.lowercase(Locale.US)) {
                "fruit_tree", "nut_tree" -> 0
                else -> 1
            }
            val containerMinGallons = when (plantType.lowercase(Locale.US)) {
                "fruit_tree", "nut_tree" -> 20
                "vine" -> 10
                "berry" -> 7
                else -> 5
            }
            val perennial = if (tags.any { it.equals("perennial", ignoreCase = true) }) 1 else 0

            db.execSQL(
                """INSERT INTO plant_info
                   (name, category, minZone, maxZone, daysToHarvestMin, daysToHarvestMax,
                    sunRequirement, waterFrequency, containerSuitable, containerMinGallons,
                    companionPlants, incompatiblePlants, notes, frostTolerant, perennial, isCustom)
                   VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0)""",
                arrayOf<Any?>(
                    commonName,
                    category,
                    3,
                    10,
                    60,
                    90,
                    "full sun",
                    "moderate",
                    containerSuitable,
                    containerMinGallons,
                    "",
                    "",
                    combinedNotes,
                    0,
                    perennial,
                ),
            )

            existingNames.add(normalizedCommon)
            normalizedScientific?.let { existingScientific.add(it) }
            stats.inserted += 1
        }
    }

    private fun readNormalizedNameSet(
        db: SupportSQLiteDatabase,
        table: String,
        nameColumn: String,
    ): MutableSet<String> {
        val out = mutableSetOf<String>()
        db.query("SELECT $nameColumn FROM $table").use { cursor ->
            while (cursor.moveToNext()) {
                val value = cursor.getString(0) ?: continue
                val normalized = normalize(value)
                if (normalized.isNotBlank()) out.add(normalized)
            }
        }
        return out
    }

    private fun readScientificSetFromNotes(
        db: SupportSQLiteDatabase,
        table: String,
        notesColumn: String,
    ): MutableSet<String> {
        val out = mutableSetOf<String>()
        db.query("SELECT $notesColumn FROM $table WHERE $notesColumn IS NOT NULL").use { cursor ->
            while (cursor.moveToNext()) {
                val notes = cursor.getString(0) ?: continue
                val scientific = scientificRegex.find(notes)?.groupValues?.getOrNull(1)?.trim().orEmpty()
                if (scientific.isNotBlank()) {
                    out.add(normalize(scientific))
                }
            }
        }
        return out
    }

    private fun pickDedupeKey(obj: JSONObject, commonName: String): String {
        val scientific = obj.optString("scientific_name").trim().ifBlank { null }
        return normalize(scientific ?: commonName)
    }

    private fun normalize(value: String): String {
        val noParen = value.lowercase(Locale.US).replace(Regex("\\s*\\([^)]*\\)"), " ")
        return noParen
            .replace(Regex("[^a-z0-9]+"), " ")
            .trim()
            .replace(Regex("\\s+"), " ")
    }

    private fun purposeContains(array: JSONArray?, value: String): Boolean {
        if (array == null) return false
        for (i in 0 until array.length()) {
            if (array.optString(i).equals(value, ignoreCase = true)) return true
        }
        return false
    }

    private fun jsonArrayToList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        val out = mutableListOf<String>()
        for (i in 0 until array.length()) {
            val value = array.optString(i).trim()
            if (value.isNotBlank()) out += value
        }
        return out
    }

    private fun composeNotes(
        scientific: String?,
        tags: List<String>,
        note: String?,
    ): String? {
        val parts = mutableListOf<String>()
        scientific?.takeIf { it.isNotBlank() }?.let { parts += "Scientific: $it" }
        if (tags.isNotEmpty()) {
            parts += "Tags: ${tags.joinToString(", ")}"
        }
        note?.takeIf { it.isNotBlank() }?.let { parts += it }
        return if (parts.isEmpty()) null else parts.joinToString(" | ")
    }
}
