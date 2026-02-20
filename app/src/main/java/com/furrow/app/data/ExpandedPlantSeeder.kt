package com.furrow.app.data

import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Seeds the expanded plant database (170+ additional plants, varieties, and planting windows).
 * Called from PlantDatabaseSeeder.seedAllReferenceTables().
 */
internal fun seedExpandedPlants(db: SupportSQLiteDatabase) {
    seedExpandedPlantInfo(db)
    seedExpandedVarieties(db)
    seedExpandedWindows(db)
}

// ── Zone group constants ──
private const val EC = "extreme_cold"
private const val C = "cold"
private const val M = "moderate"
private const val W = "warm"
private const val H = "hot"

// ── Method constants ──
private const val DS = "direct_sow"
private const val TP = "transplant"
private const val EI = "either"

// ── Helpers ──

private fun p(
    db: SupportSQLiteDatabase,
    name: String, cat: String, minZ: Int, maxZ: Int,
    dtmMin: Int, dtmMax: Int, sun: String, water: String,
    container: Boolean, gallon: Int,
    companions: String, incompatible: String, notes: String? = null,
    depth: Float? = null, spacing: Int? = null, rowSpacing: Int? = null,
    germMin: Int? = null, germMax: Int? = null,
    height: String? = null, ph: String? = null,
    frost: Boolean = false, peren: Boolean = false,
    sow: String? = null, harvest: String? = null,
) {
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
            name, cat, minZ, maxZ, dtmMin, dtmMax, sun, water,
            if (container) 1 else 0, gallon, companions, incompatible, notes,
            depth, spacing, rowSpacing, germMin, germMax,
            height, ph, if (frost) 1 else 0, if (peren) 1 else 0,
            sow, harvest,
        ),
    )
}

private fun v(
    db: SupportSQLiteDatabase,
    plant: String, name: String,
    dthMin: Int?, dthMax: Int?, desc: String,
) {
    db.execSQL(
        """INSERT INTO plant_variety (plantId, name, daysToHarvestMin, daysToHarvestMax, description, notes, isCustom)
           VALUES ((SELECT id FROM plant_info WHERE name = ?), ?, ?, ?, ?, NULL, 0)""",
        arrayOf<Any?>(plant, name, dthMin, dthMax, desc),
    )
}

private fun w(
    db: SupportSQLiteDatabase,
    plant: String, zone: String, start: Int, end: Int, method: String, notes: String? = null,
) {
    db.execSQL(
        """INSERT INTO planting_windows (plantName, zoneGroup, startMonth, endMonth, method, notes)
           VALUES (?,?,?,?,?,?)""",
        arrayOf<Any?>(plant, zone, start, end, method, notes),
    )
}

// ═══════════════════════════════════════════════════════════════════════
//  EXPANDED PLANT INFO
// ═══════════════════════════════════════════════════════════════════════

@Suppress("LongMethod")
private fun seedExpandedPlantInfo(db: SupportSQLiteDatabase) {
    seedLeafyGreens(db)
    seedRootVegetables(db)
    seedLegumes(db)
    seedAsianGreens(db)
    seedCucurbitsAndGourds(db)
    seedWarmSeasonVegetables(db)
    seedBerriesAndFruits(db)
    seedWarmZoneFruits(db)
    seedCitrus(db)
    seedHerbsExpanded(db)
    seedEdibleFlowers(db)
}

// ── Leafy Greens ──

private fun seedLeafyGreens(db: SupportSQLiteDatabase) {

    p(db, "Radicchio", "leafy_green", 4, 10, 55, 90,
        "Full", "Regular", true, 5,
        "Rosemary,Sage,Lettuce", "Peas,Beans,Endive,Escarole",
        "Cool-weather crop; heads form in cool temps. May bolt in heat",
        depth = 0.25f, spacing = 8, rowSpacing = 12,
        germMin = 5, germMax = 14, height = "8-12 inches", ph = "6.0-6.5",
        frost = true, sow = EI, harvest = "cut")

    p(db, "Endive", "leafy_green", 4, 9, 50, 100,
        "Full-Partial", "Regular", true, 3,
        "Radish,Turnip,Parsnip", "Pumpkin,Zucchini,Radicchio",
        "Blanch inner leaves 2-3 weeks before harvest by tying outer leaves",
        depth = 0.125f, spacing = 10, rowSpacing = 18,
        germMin = 7, germMax = 14, height = "12-18 inches", ph = "5.0-6.8",
        sow = EI, harvest = "cut")

    p(db, "Escarole", "leafy_green", 4, 9, 50, 100,
        "Full-Partial", "Regular", true, 3,
        "Lettuce,Radish,Turnip,Parsnip", "Pumpkin,Zucchini,Radicchio",
        "Broad-leafed endive relative; blanch before harvest. Tolerates mild frost",
        depth = 0.125f, spacing = 10, rowSpacing = 18,
        germMin = 7, germMax = 14, height = "8-16 inches", ph = "5.0-6.8",
        frost = true, sow = EI, harvest = "cut")

    p(db, "Mache", "leafy_green", 4, 9, 40, 60,
        "Full-Partial", "Regular", true, 2,
        "Spinach,Radish,Peas,Carrot,Onion", "",
        "Extremely cold-hardy (to -5F). Ideal winter salad green. Does not tolerate heat",
        depth = 0.25f, spacing = 3, rowSpacing = 6,
        germMin = 7, germMax = 14, height = "4-6 inches", ph = "6.5-7.0",
        frost = true, sow = DS, harvest = "cut")

    p(db, "Cress", "leafy_green", 3, 9, 15, 20,
        "Full-Partial", "Regular", true, 1,
        "Chives,Onion,Mint,Tomato", "",
        "One of the fastest greens from seed. Peppery flavor. Can grow indoors on windowsill",
        depth = 0.25f, spacing = 3, rowSpacing = 6,
        germMin = 2, germMax = 7, height = "6-12 inches", ph = "6.0-6.8",
        frost = true, sow = DS, harvest = "cut_and_come_again")

    p(db, "Purslane", "leafy_green", 5, 10, 30, 50,
        "Full", "Low", true, 3,
        "Corn,Pepper,Tomato", "",
        "Succulent green rich in omega-3. Extremely drought tolerant. Seeds need light to germinate",
        depth = 0.0f, spacing = 6, rowSpacing = 12,
        germMin = 7, germMax = 14, height = "4-8 inches", ph = "5.5-7.0",
        sow = DS, harvest = "cut_and_come_again")

    p(db, "Sorrel", "leafy_green", 3, 9, 35, 60,
        "Full-Partial", "Regular", true, 3,
        "Strawberry,Thyme,Sage,Rosemary", "Corn,Pole Beans",
        "Lemony-flavored perennial green. One of first greens in spring. Remove flower stalks",
        depth = 0.5f, spacing = 12, rowSpacing = 18,
        germMin = 7, germMax = 21, height = "12-36 inches", ph = "5.5-6.8",
        frost = true, peren = true, sow = EI, harvest = "cut_and_come_again")

    p(db, "Lamb's Quarters", "leafy_green", 3, 10, 40, 60,
        "Full", "Moderate", true, 3,
        "Corn,Zucchini", "",
        "Highly nutritious wild edible. Harvest tender tips at 6-8 inches. Use like spinach",
        depth = 0.125f, spacing = 18, rowSpacing = 36,
        germMin = 7, germMax = 14, height = "36-60 inches", ph = "6.0-7.5",
        sow = DS, harvest = "cut_and_come_again")

    p(db, "Watercress", "leafy_green", 3, 11, 50, 70,
        "Partial-Full", "Regular", true, 2,
        "Mint,Chives", "",
        "Semi-aquatic; keep soil constantly wet or grow in standing water. Peppery flavor",
        depth = 0.125f, spacing = 8, rowSpacing = 12,
        germMin = 7, germMax = 14, height = "6-8 inches", ph = "6.5-7.5",
        frost = true, peren = true, sow = EI, harvest = "cut_and_come_again")

    p(db, "Malabar Spinach", "leafy_green", 7, 11, 55, 70,
        "Full", "Regular", true, 5,
        "Pepper,Eggplant,Tomato", "",
        "Tropical vine, not true spinach. Thrives in heat when real spinach bolts. Mucilaginous texture",
        depth = 0.5f, spacing = 12, rowSpacing = 24,
        germMin = 10, germMax = 21, height = "72-120 inches", ph = "6.5-6.8",
        sow = EI, harvest = "cut_and_come_again")

    p(db, "New Zealand Spinach", "leafy_green", 3, 11, 55, 70,
        "Full-Partial", "Regular", true, 5,
        "Peas,Bush Beans,Corn", "",
        "Heat-tolerant spinach substitute. Spreading ground-cover habit. Soak seeds overnight",
        depth = 0.5f, spacing = 12, rowSpacing = 24,
        germMin = 7, germMax = 21, height = "12-24 inches", ph = "6.0-7.0",
        sow = DS, harvest = "cut_and_come_again")

    p(db, "Amaranth Greens", "leafy_green", 4, 11, 30, 50,
        "Full", "Moderate", true, 5,
        "Corn,Pepper,Tomato", "",
        "Heat-loving green with nutritious leaves. Also produces edible grain if left to mature",
        depth = 0.125f, spacing = 6, rowSpacing = 18,
        germMin = 7, germMax = 14, height = "36-72 inches", ph = "6.0-7.5",
        sow = DS, harvest = "cut_and_come_again")

    p(db, "Moringa", "leafy_green", 9, 11, 180, 240,
        "Full", "Low", true, 7,
        "", "Eggplant,Corn",
        "Warm zones only. Fast-growing tree 10+ ft/year. Extremely nutritious leaves. Drought resistant",
        depth = 0.75f, spacing = 36, rowSpacing = 72,
        germMin = 3, germMax = 14, height = "120-180 inches", ph = "6.3-7.0",
        peren = true, sow = EI, harvest = "cut_and_come_again")
}

// ── Root Vegetables ──

private fun seedRootVegetables(db: SupportSQLiteDatabase) {

    p(db, "Fennel", "vegetable", 4, 10, 60, 115,
        "Full", "Regular", true, 5,
        "Sunflower,Calendula,Nasturtium", "Bush Beans,Tomato,Kohlrabi,Dill,Cilantro",
        "Bulbing fennel requires long cool season. Bolts in heat. Hill soil around bulbs",
        depth = 0.25f, spacing = 8, rowSpacing = 18,
        germMin = 7, germMax = 14, height = "24-39 inches", ph = "5.5-6.8",
        frost = true, sow = DS, harvest = "cut")

    p(db, "Celeriac", "root_vegetable", 3, 9, 110, 120,
        "Full-Partial", "Regular", true, 5,
        "Lettuce,Spinach,Peas,Leek", "Pumpkin,Cucumber,Zucchini",
        "Very long season; start indoors 10-12 weeks before last frost. Seeds need light to germinate",
        depth = 0.125f, spacing = 8, rowSpacing = 24,
        germMin = 14, germMax = 21, height = "18-24 inches", ph = "6.0-7.0",
        frost = true, sow = TP, harvest = "dig")

    p(db, "Rutabaga", "root_vegetable", 3, 9, 80, 100,
        "Full", "Regular", true, 5,
        "Peas,Bush Beans,Nasturtium,Onion,Beet", "Kale,Cabbage,Mustard Greens",
        "Brassica family; flavor improves after frost. Plant midsummer for fall harvest",
        depth = 0.5f, spacing = 8, rowSpacing = 18,
        germMin = 7, germMax = 14, height = "18-24 inches", ph = "6.0-6.8",
        frost = true, sow = DS, harvest = "pull")

    p(db, "Parsnip", "root_vegetable", 2, 9, 100, 120,
        "Full-Partial", "Regular", true, 7,
        "Onion,Garlic,Radish,Peas,Turnip", "Cabbage,Tomato,Fennel,Celery",
        "Slow germinating; use only fresh seed. Flavor sweetens dramatically after frost",
        depth = 0.5f, spacing = 4, rowSpacing = 18,
        germMin = 14, germMax = 28, height = "12-36 inches", ph = "5.5-7.0",
        frost = true, sow = DS, harvest = "dig")

    p(db, "Daikon Radish", "root_vegetable", 2, 11, 50, 70,
        "Full", "Regular", true, 5,
        "Borage,Dill,Nasturtium", "Cucumber,Turnip,Horseradish",
        "Fall planting produces best roots. Roots can reach 18 inches. Also used as cover crop",
        depth = 0.5f, spacing = 6, rowSpacing = 12,
        germMin = 5, germMax = 14, height = "12-24 inches", ph = "5.8-6.8",
        frost = true, sow = DS, harvest = "pull")

    p(db, "Horseradish", "root_vegetable", 3, 9, 140, 160,
        "Full", "Regular", true, 10,
        "Potato,Sweet Potato,Strawberry,Asparagus,Rhubarb", "Broccoli,Cabbage,Cauliflower,Kale,Turnip",
        "From root cuttings, not seed. Very vigorous; grow in containers to prevent spreading",
        depth = 2.0f, spacing = 18, rowSpacing = 30,
        germMin = 7, germMax = 15, height = "24-36 inches", ph = "5.5-6.8",
        frost = true, peren = true, sow = TP, harvest = "dig")

    p(db, "Jicama", "root_vegetable", 7, 10, 150, 180,
        "Full", "Regular", true, 15,
        "Corn,Bush Beans,Sunflower,Cilantro", "Potato,Tomato",
        "Needs 150-180 frost-free days. Nick seed coat and soak before planting. Remove flowers for tubers",
        depth = 1.0f, spacing = 12, rowSpacing = 24,
        germMin = 7, germMax = 21, height = "96-240 inches", ph = "6.0-7.0",
        sow = EI, harvest = "dig")

    p(db, "Taro", "root_vegetable", 8, 11, 200, 365,
        "Partial-Full", "Regular", true, 5,
        "Bush Beans", "",
        "Tropical, needs 200+ frost-free days. Loves wet boggy conditions. Must be cooked before eating",
        depth = 3.0f, spacing = 18, rowSpacing = 36,
        germMin = 10, germMax = 15, height = "36-72 inches", ph = "5.5-6.5",
        peren = true, sow = TP, harvest = "dig")

    p(db, "Ginger", "root_vegetable", 8, 11, 120, 300,
        "Partial-Full", "Regular", true, 5,
        "Turmeric,Lemongrass,Pepper,Cilantro,Bush Beans,Peas", "Garlic,Onion,Eggplant",
        "From rhizome pieces. Baby ginger at 4-6 months; mature at 8-10 months. Warm humid conditions",
        depth = 2.0f, spacing = 10, rowSpacing = 12,
        germMin = 14, germMax = 21, height = "24-36 inches", ph = "5.5-6.5",
        peren = true, sow = TP, harvest = "dig")

    p(db, "Turmeric", "root_vegetable", 8, 11, 120, 300,
        "Partial", "Regular", true, 5,
        "Bush Beans,Lemongrass,Ginger", "Fennel,Carrot,Cabbage",
        "From rhizome pieces. Prefers filtered light. Baby harvest at 4-6 months; mature at 8-10",
        depth = 3.0f, spacing = 10, rowSpacing = 30,
        germMin = 21, germMax = 56, height = "36-48 inches", ph = "6.0-7.0",
        peren = true, sow = TP, harvest = "dig")

    p(db, "Rhubarb", "vegetable", 3, 8, 365, 730,
        "Full", "Regular", true, 10,
        "Strawberry,Garlic,Onion,Bush Beans", "Broccoli,Cauliflower,Cabbage,Potato,Sunflower",
        "Long-lived perennial (15+ yr). No harvest year 1. Needs winter chill. Only eat stalks; leaves toxic",
        depth = 2.0f, spacing = 36, rowSpacing = 48,
        germMin = 7, germMax = 21, height = "24-48 inches", ph = "6.0-6.8",
        frost = true, peren = true, sow = TP, harvest = "pull")

    p(db, "Cassava", "root_vegetable", 9, 11, 240, 365,
        "Full", "Low", true, 10,
        "Bush Beans,Peas", "Potato",
        "Warm zones only. From stem cuttings. Needs 8-11 months frost-free. Must be cooked before eating",
        depth = 3.0f, spacing = 36, rowSpacing = 48,
        germMin = 7, germMax = 14, height = "72-120 inches", ph = "5.5-6.5",
        peren = true, sow = TP, harvest = "dig")
}

// ── Legumes ──

private fun seedLegumes(db: SupportSQLiteDatabase) {

    p(db, "Edamame", "legume", 3, 9, 90, 150,
        "Full", "Regular", true, 5,
        "Corn,Squash,Celery,Strawberry,Marigold", "Peas,Green Beans,Onion,Garlic",
        "Direct sow after last frost when soil is 60F+. Harvest when pods are bright green and plump",
        depth = 1.0f, spacing = 6, rowSpacing = 24,
        germMin = 7, germMax = 14, height = "12-30 inches", ph = "6.0-7.0",
        sow = DS, harvest = "pick")

    p(db, "Peanut", "legume", 5, 11, 120, 150,
        "Full", "Regular", true, 5,
        "Beet,Carrot,Lettuce,Strawberry,Marigold", "Pole Beans,Raspberry",
        "Needs 120-150 frost-free days. Harvest when leaves yellow. Sandy soil preferred",
        depth = 2.0f, spacing = 10, rowSpacing = 24,
        germMin = 5, germMax = 10, height = "12-18 inches", ph = "5.8-6.5",
        sow = DS, harvest = "dig")

    p(db, "Fava Bean", "legume", 3, 11, 80, 100,
        "Full", "Moderate", true, 5,
        "Cucumber,Nasturtium,Strawberry,Potato", "Garlic,Onion,Fennel,Beet,Leek",
        "Cool-season legume. Frost hardy to upper 20sF. Fixes nitrogen in soil. May need staking",
        depth = 1.5f, spacing = 6, rowSpacing = 24,
        germMin = 7, germMax = 14, height = "24-72 inches", ph = "6.0-7.5",
        frost = true, sow = DS, harvest = "pick")

    p(db, "Chickpea", "legume", 3, 10, 90, 110,
        "Full", "Moderate", false, 3,
        "Cucumber,Corn,Strawberry,Celery,Carrot", "Garlic,Onion,Beet,Leek",
        "Cool-season legume. Fixes nitrogen. Prefers warm dry conditions for pod maturation",
        depth = 1.0f, spacing = 6, rowSpacing = 24,
        germMin = 7, germMax = 14, height = "18-24 inches", ph = "6.0-7.0",
        sow = DS, harvest = "pick")

    p(db, "Lentil", "legume", 4, 11, 80, 110,
        "Full", "Low", true, 3,
        "Cucumber,Potato,Tomato", "Garlic,Onion,Leek,Chives",
        "Cool-season legume. Fixes nitrogen. Allow pods to dry on plant. Reduce watering as pods mature",
        depth = 1.0f, spacing = 4, rowSpacing = 18,
        germMin = 7, germMax = 14, height = "18-24 inches", ph = "6.0-7.0",
        frost = true, sow = DS, harvest = "pull")

    p(db, "Winged Bean", "legume", 8, 11, 75, 110,
        "Full", "Regular", true, 5,
        "Corn,Tomato,Carrot,Marigold", "Onion,Garlic,Chives",
        "Tropical legume. All parts edible: pods, leaves, flowers, tubers. Needs trellis, vines 9-13 ft",
        depth = 1.0f, spacing = 24, rowSpacing = 48,
        germMin = 6, germMax = 10, height = "96-156 inches", ph = "6.0-7.0",
        sow = DS, harvest = "pick")

    p(db, "Yard Long Bean", "legume", 5, 11, 70, 90,
        "Full", "Regular", true, 10,
        "Basil,Chives,Marigold,Corn,Cucumber", "Onion,Garlic,Fennel",
        "Heat-loving vine bean. Needs sturdy trellis. Harvest pods at 10-12 inches for best flavor",
        depth = 1.0f, spacing = 12, rowSpacing = 30,
        germMin = 10, germMax = 15, height = "72-144 inches", ph = "5.5-7.5",
        sow = DS, harvest = "pick")
}

// ── Asian Greens ──

private fun seedAsianGreens(db: SupportSQLiteDatabase) {

    p(db, "Bok Choy", "brassica", 2, 11, 30, 60,
        "Partial-Full", "Regular", true, 3,
        "Carrot,Garlic,Beet,Thyme,Sage,Rosemary", "Tomato,Strawberry",
        "Cool-season crop. Bolts in heat. Light frost improves flavor",
        depth = 0.25f, spacing = 8, rowSpacing = 18,
        germMin = 4, germMax = 8, height = "8-12 inches", ph = "6.0-7.5",
        frost = true, sow = EI, harvest = "cut")

    p(db, "Napa Cabbage", "brassica", 4, 9, 60, 90,
        "Full-Partial", "Regular", true, 5,
        "Marigold,Dill,Chamomile,Rosemary,Thyme,Onion", "Tomato,Potato,Fennel",
        "Forms tight cylindrical heads. Best as fall crop. Frost tolerant to 26F when mature",
        depth = 0.25f, spacing = 12, rowSpacing = 24,
        germMin = 4, germMax = 10, height = "10-12 inches", ph = "6.0-7.5",
        frost = true, sow = EI, harvest = "cut")

    p(db, "Mizuna", "brassica", 3, 10, 20, 40,
        "Full-Partial", "Regular", true, 2,
        "Onion,Garlic,Dill,Marigold,Lettuce,Bean,Carrot", "",
        "Fast-growing Asian mustard green. Slow to bolt compared to other greens",
        depth = 0.25f, spacing = 8, rowSpacing = 12,
        germMin = 4, germMax = 7, height = "8-18 inches", ph = "6.0-7.5",
        frost = true, sow = DS, harvest = "cut_and_come_again")

    p(db, "Tatsoi", "brassica", 4, 7, 21, 45,
        "Full-Partial", "Regular", true, 2,
        "Lettuce,Parsley,Spinach,Mizuna,Endive", "Potato",
        "Extremely cold hardy to 15F. Spoon-shaped rosette. Baby leaf 21 days, full head 45",
        depth = 0.25f, spacing = 8, rowSpacing = 18,
        germMin = 4, germMax = 8, height = "6-12 inches", ph = "6.0-7.5",
        frost = true, sow = DS, harvest = "cut_and_come_again")

    p(db, "Komatsuna", "brassica", 4, 9, 25, 50,
        "Full", "Regular", true, 5,
        "Bean,Pea,Lettuce,Onion,Garlic", "Sunflower",
        "Japanese mustard spinach. Cold tolerant to 10-15F. Handles heat and cold better than most brassicas",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 4, germMax = 7, height = "12-18 inches", ph = "5.5-7.5",
        frost = true, sow = DS, harvest = "cut_and_come_again")

    p(db, "Chinese Broccoli", "brassica", 2, 10, 35, 60,
        "Full-Partial", "Regular", true, 3,
        "Rosemary,Thyme,Dill,Sage,Onion,Garlic", "Parsley,Tomato,Fennel",
        "Thick stems with small florets. Harvest when flower buds just start to form",
        depth = 0.25f, spacing = 8, rowSpacing = 18,
        germMin = 4, germMax = 10, height = "8-36 inches", ph = "6.0-6.8",
        frost = true, sow = EI, harvest = "cut")

    p(db, "Broccoli Rabe", "brassica", 3, 10, 40, 60,
        "Full", "Regular", true, 3,
        "Beet,Carrot,Lettuce,Onion,Spinach,Nasturtium", "Pole Beans,Snap Peas,Strawberry",
        "Fast-growing cool-season crop. Harvest when buds are 1 inch wide. Frost tolerant to 25F",
        depth = 0.375f, spacing = 6, rowSpacing = 20,
        germMin = 6, germMax = 9, height = "6-12 inches", ph = "6.0-7.5",
        frost = true, sow = DS, harvest = "cut")

    p(db, "Romanesco", "brassica", 3, 10, 75, 100,
        "Full", "Regular", true, 5,
        "Dill,Mint,Bean,Onion,Spinach,Sage,Thyme,Marigold", "Tomato,Strawberry",
        "Distinctive fractal spiral heads. Needs consistent moisture. Frost tolerant to 20F",
        depth = 0.25f, spacing = 18, rowSpacing = 30,
        germMin = 7, germMax = 14, height = "24-36 inches", ph = "6.0-7.0",
        frost = true, sow = TP, harvest = "cut")
}

// ── Cucurbits and Gourds ──

private fun seedCucurbitsAndGourds(db: SupportSQLiteDatabase) {

    p(db, "Bitter Melon", "cucurbit", 9, 11, 60, 90,
        "Full", "Regular", true, 5,
        "Bean,Corn,Pea,Radish,Marigold,Basil", "Potato",
        "Tropical vine; needs trellis 5-6 ft. Scarify and soak seeds 24hrs. Harvest at 4-6 inches green",
        depth = 0.5f, spacing = 12, rowSpacing = 60,
        germMin = 7, germMax = 14, height = "156-192 inches", ph = "5.5-6.7",
        sow = EI, harvest = "pick")

    p(db, "Luffa", "cucurbit", 6, 11, 120, 200,
        "Full", "Regular", true, 15,
        "Bean,Pea,Radish,Okra,Corn,Marigold", "Potato",
        "Needs 150+ warm frost-free days. Trellis 6+ ft. Harvest young for eating or mature for sponges",
        depth = 0.75f, spacing = 36, rowSpacing = 72,
        germMin = 7, germMax = 21, height = "180-360 inches", ph = "6.0-6.5",
        sow = EI, harvest = "pick")

    p(db, "Chayote", "cucurbit", 7, 11, 120, 150,
        "Full", "Regular", true, 5,
        "Corn,Broccoli,Pepper,Squash", "Celery,Mint,Snap Beans",
        "Plant entire sprouted fruit on side. Perennial zones 8+. Up to 100 fruits per vine",
        depth = 2.0f, spacing = 120, rowSpacing = 120,
        germMin = 14, germMax = 30, height = "240-360 inches", ph = "6.0-6.8",
        peren = true, sow = DS, harvest = "pick")

    p(db, "Calabash", "cucurbit", 2, 11, 60, 120,
        "Full", "Regular", true, 5,
        "Bean,Pea,Corn,Radish,Marigold,Dill", "Potato,Squash",
        "Harvest young for eating or mature for craft shells. Soak seeds 24 hrs. Strong trellis needed",
        depth = 1.0f, spacing = 36, rowSpacing = 72,
        germMin = 7, germMax = 25, height = "120-360 inches", ph = "6.0-7.5",
        sow = EI, harvest = "pick")

    p(db, "Kabocha Squash", "cucurbit", 3, 10, 85, 110,
        "Full", "Regular", true, 5,
        "Corn,Bean,Marigold,Pea,Lettuce,Radish,Nasturtium", "Potato",
        "Japanese winter squash with dense sweet flesh. Cure 5-7 days after harvest. Stores 2-3 months",
        depth = 1.0f, spacing = 36, rowSpacing = 72,
        germMin = 7, germMax = 14, height = "72-120 inches", ph = "6.0-6.8",
        sow = EI, harvest = "cut")

    p(db, "Delicata Squash", "cucurbit", 3, 10, 80, 105,
        "Full", "Regular", true, 7,
        "Corn,Lettuce,Pea,Radish,Borage,Marigold,Nasturtium", "Potato",
        "Winter squash with edible skin. Semi-bush habit. Shorter storage than other winter squash (2-3 mo)",
        depth = 1.0f, spacing = 24, rowSpacing = 72,
        germMin = 7, germMax = 14, height = "12-96 inches", ph = "6.0-6.8",
        sow = EI, harvest = "cut")

    p(db, "Hubbard Squash", "cucurbit", 3, 10, 90, 120,
        "Full", "Regular", false, 20,
        "Corn,Bean,Marigold,Nasturtium,Pea,Radish", "Potato",
        "Very large (15+ lbs). Long trailing vines need lots of space. Hard shell stores 6+ months",
        depth = 1.0f, spacing = 48, rowSpacing = 96,
        germMin = 7, germMax = 14, height = "96-144 inches", ph = "6.0-6.8",
        sow = EI, harvest = "cut")

    p(db, "Patty Pan Squash", "cucurbit", 3, 11, 45, 65,
        "Full", "Regular", true, 5,
        "Corn,Bean,Pea,Radish,Marigold,Nasturtium,Borage", "Potato,Sage",
        "Summer squash, compact bush habit. Harvest young at 2-3 inches. Very prolific producer",
        depth = 1.0f, spacing = 36, rowSpacing = 60,
        germMin = 5, germMax = 10, height = "24-36 inches", ph = "5.8-6.8",
        sow = EI, harvest = "pick")

    p(db, "Snake Gourd", "cucurbit", 8, 11, 55, 100,
        "Full", "Regular", true, 5,
        "Bean,Corn,Marigold,Basil,Mint", "Potato",
        "Tropical vine; needs 5+ ft trellis for fruits to hang straight. Harvest young at 1-2 ft for eating",
        depth = 1.0f, spacing = 42, rowSpacing = 54,
        germMin = 10, germMax = 28, height = "144-180 inches", ph = "5.5-7.0",
        sow = EI, harvest = "pick")
}

// ── Warm-Season Vegetables ──

private fun seedWarmSeasonVegetables(db: SupportSQLiteDatabase) {

    p(db, "Tomatillo", "nightshade", 5, 11, 60, 100,
        "Full", "Regular", true, 5,
        "Basil,Marigold,Nasturtium,Onion,Pepper,Pea,Bean", "Potato,Eggplant,Dill,Fennel,Corn",
        "Requires 2+ plants for cross-pollination. Harvest when husks split and fruit fills husk",
        depth = 0.25f, spacing = 24, rowSpacing = 36,
        germMin = 7, germMax = 14, height = "36-60 inches", ph = "6.0-7.0",
        sow = TP, harvest = "pick")

    p(db, "Ground Cherry", "nightshade", 4, 8, 65, 75,
        "Full", "Regular", true, 3,
        "Basil,Parsley,Carrot,Onion,Marigold,Chives", "Tomato,Eggplant,Pepper,Potato",
        "Harvest when husks dry and papery, fruits yellow, drop to ground. Start indoors 6-8 weeks early",
        depth = 0.25f, spacing = 24, rowSpacing = 36,
        germMin = 7, germMax = 14, height = "18-36 inches", ph = "6.0-6.8",
        sow = TP, harvest = "pick")
}

// ── Berries and Fruits ──

private fun seedBerriesAndFruits(db: SupportSQLiteDatabase) {
    seedBerries1(db)
    seedBerries2(db)
}

private fun seedBerries1(db: SupportSQLiteDatabase) {

    p(db, "Gooseberry", "berry", 3, 8, 730, 1095,
        "Partial-Full", "Regular", true, 10,
        "Tomato,Tansy,Marigold,Chives", "Fennel,Walnut",
        "Thorny shrub. Fruit in 2-3 years. Tart berries for pies and preserves. Prune for air circulation",
        depth = 2.0f, spacing = 48, rowSpacing = 72,
        height = "36-60 inches", ph = "6.0-6.5",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Currant", "berry", 3, 8, 730, 1095,
        "Partial-Full", "Regular", true, 10,
        "Wormwood,Tansy,Marigold,Garlic", "Walnut,White Pine",
        "Red, black, and white varieties. Very cold hardy. Tart fruit for jams and juice. Banned in some states",
        depth = 2.0f, spacing = 48, rowSpacing = 72,
        height = "36-60 inches", ph = "6.0-6.5",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Elderberry", "berry", 3, 9, 730, 1095,
        "Full-Partial", "Regular", false, 15,
        "Comfrey,Lavender,Bee Balm", "",
        "Large shrub/small tree. Berries must be cooked before eating. Flowers also edible. Plant 2+ for pollination",
        depth = 2.0f, spacing = 96, rowSpacing = 120,
        height = "72-144 inches", ph = "5.5-6.5",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Mulberry", "fruit", 4, 8, 730, 1460,
        "Full", "Moderate", true, 20,
        "Comfrey,Chives,Garlic", "",
        "Fast-growing fruit tree. Very prolific; berries stain everything. Red, white, and black species",
        depth = 1.0f, spacing = 180, rowSpacing = 180,
        height = "120-360 inches", ph = "5.5-7.0",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Pawpaw", "fruit", 5, 9, 1460, 2920,
        "Partial-Full", "Regular", true, 15,
        "Comfrey,Bee Balm,Clover", "Walnut",
        "Native understory tree. Tropical-flavored custard fruit. Needs 2+ unrelated trees for pollination",
        depth = 1.0f, spacing = 120, rowSpacing = 120,
        height = "120-300 inches", ph = "5.5-7.0",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "American Persimmon", "fruit", 4, 9, 1095, 2190,
        "Full", "Moderate", true, 15,
        "Comfrey,Clover,Garlic", "",
        "Native tree with sweet fruit after frost. Astringent until fully ripe. Female tree needed for fruit",
        depth = 1.0f, spacing = 180, rowSpacing = 180,
        height = "120-480 inches", ph = "6.0-7.5",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Hardy Kiwi", "vine_fruit", 3, 8, 730, 1825,
        "Full-Partial", "Regular", true, 15,
        "Lavender,Comfrey,Marigold", "",
        "Grape-sized smooth-skinned fruit. Needs sturdy trellis. Plant male and female vines for fruit",
        depth = 0.5f, spacing = 120, rowSpacing = 120,
        germMin = 14, germMax = 60, height = "120-360 inches", ph = "5.0-6.5",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Honeyberry", "berry", 2, 7, 365, 1095,
        "Full-Partial", "Regular", true, 5,
        "Blueberry,Comfrey", "",
        "Also called haskap. Extremely cold hardy (-55F). Earliest fruit of the season. Plant 2+ varieties",
        depth = 1.0f, spacing = 48, rowSpacing = 72,
        height = "36-72 inches", ph = "5.5-8.0",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Serviceberry", "fruit", 2, 9, 730, 1095,
        "Full-Partial", "Regular", true, 15,
        "Blueberry,Comfrey,Clover", "",
        "Native shrub/tree. Sweet blueberry-like fruit in June. Also called saskatoon or juneberry",
        depth = 1.0f, spacing = 96, rowSpacing = 120,
        height = "48-240 inches", ph = "5.5-7.0",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Goji Berry", "berry", 4, 9, 730, 1825,
        "Full", "Low", true, 5,
        "Lavender,Thyme,Rosemary,Marigold", "Tomato,Potato,Eggplant,Fennel",
        "Semi-vine shrub. Drought tolerant once established. Produces midsummer to first frost",
        depth = 1.0f, spacing = 60, rowSpacing = 72,
        germMin = 14, germMax = 28, height = "60-120 inches", ph = "6.8-8.0",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Cranberry", "berry", 2, 7, 1095, 1825,
        "Full", "Regular", true, 5,
        "Blueberry,Thyme", "Potato,Cabbage",
        "Needs very acidic soil and consistent moisture. Ground-cover vine. About 1 lb per 4 sq ft established",
        depth = 1.0f, spacing = 18, rowSpacing = 36,
        height = "6-8 inches", ph = "4.0-5.5",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Lingonberry", "berry", 3, 7, 365, 730,
        "Partial-Full", "Regular", true, 5,
        "Blueberry,Cranberry,Azalea", "Walnut",
        "Low evergreen ground cover. Two harvests: late summer and early fall. Needs acidic soil",
        depth = 2.5f, spacing = 15, rowSpacing = 48,
        height = "12-18 inches", ph = "4.5-5.5",
        frost = true, peren = true, sow = TP, harvest = "pick")
}

private fun seedBerries2(db: SupportSQLiteDatabase) {

    p(db, "Boysenberry", "berry", 5, 9, 365, 730,
        "Full", "Regular", true, 10,
        "Marigold,Lavender,Basil,Pea", "Tomato,Potato,Eggplant",
        "Trailing canes need trellis. Cross of raspberry, blackberry, loganberry. Harvest May-July",
        depth = 2.0f, spacing = 60, rowSpacing = 120,
        height = "48-72 inches", ph = "5.8-6.5",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Loganberry", "berry", 5, 9, 365, 730,
        "Full", "Regular", true, 10,
        "Marigold,Garlic,Chives,Tansy", "Tomato,Potato",
        "Raspberry-blackberry hybrid. Tart, excellent for pies and preserves. Harvest June-July",
        depth = 3.0f, spacing = 48, rowSpacing = 96,
        height = "48-72 inches", ph = "6.0-6.5",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Marionberry", "berry", 6, 9, 365, 730,
        "Full", "Regular", true, 10,
        "Marigold,Garlic,Tansy,Chives", "Tomato,Potato",
        "Oregon signature berry. Intensely aromatic blackberry flavor. Trailing canes need trellis",
        depth = 1.0f, spacing = 60, rowSpacing = 120,
        height = "48-72 inches", ph = "5.5-7.0",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Tayberry", "berry", 5, 9, 365, 730,
        "Full", "Regular", true, 10,
        "Marigold,Garlic,Chives,Tansy", "Tomato,Potato",
        "Scottish blackberry-raspberry cross. Sweeter and larger than loganberry. Harvest June-July",
        depth = 1.0f, spacing = 36, rowSpacing = 72,
        height = "48-72 inches", ph = "5.8-6.5",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Dewberry", "berry", 3, 9, 1460, 1825,
        "Full-Partial", "Moderate", false, 0,
        "Garlic,Tansy,Chives", "Tomato,Potato",
        "Native trailing blackberry relative. Ripe late April-May, earlier than blackberries",
        depth = 1.0f, spacing = 30, rowSpacing = 72,
        height = "12-36 inches", ph = "5.0-6.5",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Huckleberry", "berry", 3, 9, 1095, 1825,
        "Partial-Full", "Regular", true, 7,
        "Blueberry,Rhododendron,Fern", "",
        "Difficult to cultivate, slow growing. Needs acidic soil. Multiple species for different zones",
        depth = 0.5f, spacing = 42, rowSpacing = 72,
        height = "36-72 inches", ph = "4.3-5.5",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Muscadine Grape", "vine_fruit", 7, 10, 730, 1095,
        "Full", "Moderate", true, 20,
        "Clover,Basil,Geranium", "",
        "Native Southern grape, needs humid climate. Thicker skin. Self-fertile varieties available",
        depth = 1.0f, spacing = 240, rowSpacing = 120,
        height = "120-240 inches", ph = "5.8-6.5",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Quince", "fruit", 5, 9, 1825, 2190,
        "Full", "Moderate", true, 15,
        "Comfrey,Garlic,Chives", "",
        "Most varieties require cooking. Very fragrant golden fruit in late autumn. Good for preserves",
        depth = 2.0f, spacing = 144, rowSpacing = 180,
        height = "120-180 inches", ph = "6.0-7.0",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Cape Gooseberry", "berry", 4, 11, 100, 140,
        "Full", "Moderate", true, 5,
        "Basil,Chives,Calendula,Thyme,Marigold", "",
        "Golden berry in papery husk. Annual in frost zones, perennial frost-free. Prolific self-seeder",
        depth = 0.06f, spacing = 24, rowSpacing = 36,
        germMin = 14, germMax = 21, height = "36-60 inches", ph = "5.0-6.5",
        sow = TP, harvest = "pick")

    p(db, "Prickly Pear", "fruit", 4, 11, 1095, 1460,
        "Full", "Low", true, 5,
        "Agave,Yucca,Lavender", "",
        "Both pads (nopales) and fruit (tunas) edible. Extremely drought tolerant. Eastern species hardy to zone 4",
        depth = 2.0f, spacing = 36, rowSpacing = 48,
        height = "12-60 inches", ph = "6.0-7.5",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Che Fruit", "fruit", 5, 9, 1095, 1825,
        "Full", "Moderate", true, 15,
        "Comfrey,Clover", "",
        "Also called Mandarin Melon Berry. Fig-watermelon flavor. Seedless female trees bear without pollinator",
        depth = 2.0f, spacing = 180, rowSpacing = 180,
        height = "120-300 inches", ph = "6.0-7.5",
        frost = true, peren = true, sow = TP, harvest = "pick")
}

// ── Warm Zone Fruits ──

private fun seedWarmZoneFruits(db: SupportSQLiteDatabase) {

    p(db, "Pomegranate", "fruit", 7, 10, 730, 1095,
        "Full", "Moderate", true, 15,
        "Basil,Lavender,Thyme,Dill", "Fennel,Sunflower,Potato",
        "Heat-loving, drought tolerant once established. Needs 120+ days above 85F for best fruit",
        depth = null, spacing = 144, rowSpacing = 180,
        height = "120-240 inches", ph = "5.5-7.0",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Jujube", "fruit", 6, 10, 730, 1460,
        "Full", "Low", true, 15,
        "Comfrey,Borage,Thyme,Oregano", "Walnut",
        "Extremely drought tolerant. Flowers late avoiding frosts. Dormant trees hardy to -20F. Suckers can spread",
        depth = null, spacing = 180, rowSpacing = 240,
        height = "180-360 inches", ph = "5.5-7.5",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Guava", "fruit", 9, 11, 730, 1095,
        "Full", "Regular", true, 15,
        "Citrus,Borage,Chives,Marigold,Comfrey", "Potato,Eggplant",
        "Tropical/subtropical. Damaged below 30F. Can be container-grown in colder zones and brought indoors",
        depth = null, spacing = 144, rowSpacing = 180,
        height = "120-300 inches", ph = "5.0-7.0",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Loquat", "fruit", 8, 10, 730, 1825,
        "Full", "Moderate", true, 15,
        "Clover,Marigold,Comfrey", "Walnut",
        "Tree hardy to 10F but flowers killed at 27F. Blooms fall/winter, fruit ripens spring",
        depth = null, spacing = 180, rowSpacing = 180,
        height = "180-360 inches", ph = "5.5-7.5",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Dragonfruit", "vine_fruit", 10, 11, 365, 1095,
        "Full", "Moderate", true, 15,
        "Marigold,Nasturtium", "",
        "Climbing cactus needing sturdy trellis. Plant from cuttings for faster fruit. Well-drained soil",
        depth = 2.0f, spacing = 72, rowSpacing = 144,
        height = "120-240 inches", ph = "5.5-6.5",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Banana", "fruit", 9, 11, 365, 545,
        "Full", "Regular", true, 15,
        "Sweet Potato,Comfrey,Lemongrass,Ginger,Bean", "Corn",
        "Needs 10-15 frost-free months to flower. Giant herbaceous plant. Heavy feeder",
        depth = 3.0f, spacing = 96, rowSpacing = 120,
        height = "60-180 inches", ph = "5.5-6.5",
        peren = true, sow = TP, harvest = "cut")
}

// ── Citrus ──

private fun seedCitrus(db: SupportSQLiteDatabase) {

    p(db, "Lemon", "fruit", 9, 11, 365, 1095,
        "Full", "Regular", true, 10,
        "Basil,Lavender,Rosemary,Marigold,Chives", "Fennel,Walnut",
        "Most cold-sensitive common citrus. Fruit takes 6-9 months to ripen. Great in containers",
        depth = null, spacing = 180, rowSpacing = 240,
        height = "120-240 inches", ph = "5.5-6.5",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Lime", "fruit", 9, 11, 365, 1095,
        "Full", "Regular", true, 10,
        "Basil,Lavender,Rosemary,Marigold,Chives", "Fennel,Walnut",
        "Most cold-sensitive citrus. Key limes very frost-tender; Persian limes slightly hardier",
        depth = null, spacing = 120, rowSpacing = 180,
        height = "120-240 inches", ph = "5.5-6.5",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Orange", "fruit", 9, 11, 365, 1095,
        "Full", "Regular", true, 10,
        "Basil,Lavender,Rosemary,Marigold,Chives", "Fennel,Walnut",
        "Navel ripens winter, Valencia ripens late spring-summer. Dwarf varieties for containers",
        depth = null, spacing = 180, rowSpacing = 240,
        height = "180-300 inches", ph = "6.0-7.0",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Grapefruit", "fruit", 9, 11, 1095, 1825,
        "Full", "Regular", true, 15,
        "Basil,Lavender,Rosemary,Marigold,Chives", "Fennel,Walnut",
        "Largest citrus tree. Needs most heat for sweet fruit. Harvest late fall to spring",
        depth = null, spacing = 144, rowSpacing = 180,
        height = "180-360 inches", ph = "6.0-6.8",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Kumquat", "fruit", 8, 11, 365, 730,
        "Full", "Regular", true, 10,
        "Basil,Lavender,Rosemary,Marigold,Chives", "Fennel,Walnut",
        "Most cold-hardy citrus, survives to 18F. Eat whole including sweet rind. Compact growth",
        depth = null, spacing = 96, rowSpacing = 120,
        height = "72-180 inches", ph = "6.0-7.0",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Meyer Lemon", "fruit", 8, 11, 365, 730,
        "Full", "Regular", true, 10,
        "Basil,Lavender,Rosemary,Marigold,Chives", "Fennel,Walnut",
        "Lemon-mandarin hybrid. Sweeter than true lemons. More cold-tolerant (hardy to 21F). Great in containers",
        depth = null, spacing = 96, rowSpacing = 120,
        height = "72-120 inches", ph = "5.5-6.5",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Satsuma", "fruit", 8, 11, 365, 1095,
        "Full", "Regular", true, 15,
        "Basil,Lavender,Rosemary,Marigold,Lantana", "Fennel,Walnut",
        "Cold-hardiest true citrus mandarin (18-20F). Seedless, easy peel. Ripens Oct-Jan",
        depth = null, spacing = 96, rowSpacing = 120,
        height = "120-180 inches", ph = "6.0-7.0",
        frost = true, peren = true, sow = TP, harvest = "pick")
}

// ── Herbs (expanded) ──

private fun seedHerbsExpanded(db: SupportSQLiteDatabase) {
    seedHerbs1(db)
    seedHerbs2(db)
}

private fun seedHerbs1(db: SupportSQLiteDatabase) {

    p(db, "Lemon Balm", "herb", 3, 7, 60, 70,
        "Partial-Full", "Moderate", true, 1,
        "Tomato,Pepper,Brassica", "",
        "Perennial. Spreads aggressively, contain in pots. Lemon-scented leaves for tea and cooking",
        depth = 0.0f, spacing = 18, rowSpacing = 24,
        germMin = 10, germMax = 14, height = "12-30 inches", ph = "6.0-7.5",
        frost = true, peren = true, sow = EI, harvest = "cut_and_come_again")

    p(db, "Chamomile", "herb", 3, 9, 60, 65,
        "Full", "Moderate", true, 1,
        "Bean,Brassica,Cucumber,Onion", "Mint",
        "German (annual) or Roman (perennial). Harvest flowers for tea when petals fold back",
        depth = 0.0f, spacing = 8, rowSpacing = 18,
        germMin = 7, germMax = 14, height = "8-24 inches", ph = "5.6-7.5",
        frost = true, sow = EI, harvest = "pick")

    p(db, "Stevia", "herb", 9, 11, 60, 90,
        "Full", "Regular", true, 1,
        "", "",
        "Natural sweetener 200-300x sugar. Annual in cold zones, perennial in zones 9+. Slow to germinate",
        depth = 0.0f, spacing = 18, rowSpacing = 24,
        germMin = 7, germMax = 21, height = "12-24 inches", ph = "6.0-7.0",
        peren = true, sow = TP, harvest = "cut")

    p(db, "Catnip", "herb", 3, 9, 60, 80,
        "Full-Partial", "Moderate", true, 2,
        "Squash,Eggplant,Beet", "",
        "Perennial. Attracts cats, repels mosquitoes and aphids. Vigorous spreader, deadhead to control",
        depth = 0.125f, spacing = 18, rowSpacing = 24,
        germMin = 7, germMax = 10, height = "12-36 inches", ph = "6.1-7.5",
        frost = true, peren = true, sow = EI, harvest = "cut_and_come_again")

    p(db, "Hyssop", "herb", 4, 9, 75, 90,
        "Full", "Low", true, 3,
        "Cabbage,Grape,Lavender", "Radish",
        "Semi-evergreen perennial sub-shrub. Attracts pollinators. Minty-anise flavor for teas and cooking",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 14, germMax = 21, height = "18-24 inches", ph = "7.0-8.0",
        frost = true, peren = true, sow = EI, harvest = "cut")

    p(db, "Borage", "herb", 3, 10, 50, 60,
        "Full", "Moderate", true, 5,
        "Tomato,Strawberry,Squash,Cabbage", "",
        "Annual with cucumber-flavored blue flowers. Attracts pollinators. Self-seeds readily",
        depth = 0.5f, spacing = 18, rowSpacing = 24,
        germMin = 7, germMax = 14, height = "18-36 inches", ph = "6.0-7.0",
        sow = DS, harvest = "pick")

    p(db, "Comfrey", "herb", 3, 9, 365, 730,
        "Full-Partial", "Moderate", true, 5,
        "Fruit Trees,Berry Bushes", "",
        "Dynamic accumulator — chop-and-drop mulch. Propagated from root cuttings. Very difficult to remove once established",
        depth = 0.5f, spacing = 36, rowSpacing = 48,
        height = "24-48 inches", ph = "6.0-7.0",
        frost = true, peren = true, sow = TP, harvest = "cut")

    p(db, "Feverfew", "herb", 5, 10, 80, 110,
        "Full-Partial", "Moderate", true, 2,
        "Rose,Fruit Trees,Calendula", "",
        "Short-lived perennial. Medicinal for migraines. Self-seeds prolifically. Repels pests",
        depth = 0.0f, spacing = 12, rowSpacing = 18,
        germMin = 10, germMax = 14, height = "12-36 inches", ph = "6.0-7.5",
        frost = true, peren = true, sow = EI, harvest = "cut")

    p(db, "Valerian", "herb", 4, 9, 120, 180,
        "Full-Partial", "Regular", true, 5,
        "Comfrey,Yarrow,Echinacea,Thyme", "Onion",
        "Roots harvested fall of second year for medicinal use. Cold stratification improves germination",
        depth = 0.125f, spacing = 18, rowSpacing = 36,
        germMin = 10, germMax = 21, height = "36-60 inches", ph = "5.5-7.0",
        frost = true, peren = true, sow = EI, harvest = "cut")

    p(db, "Echinacea", "herb", 3, 9, 120, 180,
        "Full", "Low", true, 3,
        "Bee Balm,Lavender,Yarrow", "Hosta,Fern",
        "Perennial. May take 2+ years from seed to bloom. Medicinal roots and flowers. Cold stratify seeds",
        depth = 0.125f, spacing = 18, rowSpacing = 24,
        germMin = 10, germMax = 20, height = "24-48 inches", ph = "6.0-7.0",
        frost = true, peren = true, sow = EI, harvest = "cut")

    p(db, "Bee Balm", "herb", 4, 9, 90, 120,
        "Full", "Regular", true, 5,
        "Tomato,Pepper,Echinacea", "",
        "Perennial. Prone to powdery mildew. Edible flowers taste like oregano/thyme. Attracts hummingbirds",
        depth = 0.25f, spacing = 18, rowSpacing = 24,
        germMin = 14, germMax = 21, height = "24-48 inches", ph = "6.0-7.0",
        frost = true, peren = true, sow = EI, harvest = "cut_and_come_again")

    p(db, "Anise", "herb", 4, 9, 75, 130,
        "Full", "Moderate", true, 2,
        "Bean,Coriander,Cabbage", "Carrot,Radish,Basil",
        "Annual. Needs 120+ frost-free days for seed harvest. Taproot does not transplant well",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 7, germMax = 14, height = "18-24 inches", ph = "6.0-7.0",
        sow = DS, harvest = "cut")

    p(db, "Caraway", "herb", 3, 10, 60, 75,
        "Full", "Moderate", false, 5,
        "Pea,Tomato,Broccoli,Strawberry", "Dill,Fennel,Carrot",
        "Biennial. Seeds harvested second year. Taproot does not transplant well. Hardy to -30F",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 4, germMax = 14, height = "12-30 inches", ph = "6.0-7.0",
        frost = true, sow = DS, harvest = "cut")

    p(db, "Herb Fennel", "herb", 4, 9, 60, 90,
        "Full", "Moderate", true, 3,
        "Dill,Coriander,Borage,Chives", "Tomato,Bean,Pepper",
        "Grown for fronds and seeds, not bulb. Allelopathic — plant away from vegetable garden. Self-seeds",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 8, germMax = 14, height = "36-60 inches", ph = "6.0-6.7",
        peren = true, sow = DS, harvest = "cut_and_come_again")

    p(db, "Epazote", "herb", 6, 11, 50, 55,
        "Full", "Low", true, 3,
        "Bean,Squash,Pepper,Marigold", "Root Vegetables",
        "Traditional Mexican bean herb. Self-seeds aggressively. Surface sow, needs light to germinate",
        depth = 0.0f, spacing = 12, rowSpacing = 18,
        germMin = 7, germMax = 14, height = "24-48 inches", ph = "6.0-7.5",
        sow = EI, harvest = "cut_and_come_again")

    p(db, "Shiso", "herb", 2, 11, 55, 70,
        "Partial-Full", "Regular", true, 1,
        "Basil,Parsley,Tomato", "",
        "Japanese/Korean herb. Soak seeds 24 hrs before sowing. Red and green varieties. Annual in most zones",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 7, germMax = 14, height = "18-36 inches", ph = "5.5-6.5",
        sow = EI, harvest = "cut_and_come_again")

    p(db, "Summer Savory", "herb", 1, 11, 60, 70,
        "Full", "Moderate", true, 1,
        "Bean,Tomato,Onion,Garlic", "Cucumber",
        "Annual known as the bean herb. Delicate flavor lighter than winter savory. Attracts honeybees",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 10, germMax = 14, height = "12-18 inches", ph = "6.7-7.3",
        sow = EI, harvest = "cut_and_come_again")

    p(db, "Winter Savory", "herb", 5, 9, 80, 90,
        "Full", "Low", true, 1,
        "Bean,Thyme,Sage,Hyssop,Lavender", "",
        "Perennial sub-shrub. More intense flavor than summer savory. Repels bean weevils and cabbage moths",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 10, germMax = 14, height = "6-18 inches", ph = "6.0-8.0",
        frost = true, peren = true, sow = EI, harvest = "cut_and_come_again")

    p(db, "French Tarragon", "herb", 4, 9, 42, 60,
        "Full", "Low", true, 3,
        "Tomato,Pepper,Eggplant,Basil", "Broccoli,Cabbage",
        "Does NOT produce viable seed; propagate by division/cuttings. Classic anise flavor. Replace every 3-4 yr",
        depth = 0.0f, spacing = 18, rowSpacing = 24,
        height = "24-36 inches", ph = "6.5-7.5",
        frost = true, peren = true, sow = TP, harvest = "cut_and_come_again")

    p(db, "Marjoram", "herb", 9, 11, 60, 80,
        "Full", "Low", true, 1,
        "Tomato,Pepper,Eggplant,Basil,Oregano", "",
        "Tender perennial, annual in cold zones. Sweeter than oregano. Harvest before flowers for peak flavor",
        depth = 0.25f, spacing = 8, rowSpacing = 18,
        germMin = 7, germMax = 21, height = "12-24 inches", ph = "6.7-7.0",
        peren = true, sow = EI, harvest = "cut_and_come_again")
}

private fun seedHerbs2(db: SupportSQLiteDatabase) {

    p(db, "Lovage", "herb", 3, 8, 80, 90,
        "Full-Partial", "Regular", true, 5,
        "Bean,Root Vegetables,Potato", "",
        "Perennial celery-flavored herb. Very large plant. Harvest leaves, stems, seeds, and roots",
        depth = 0.25f, spacing = 24, rowSpacing = 36,
        germMin = 10, germMax = 20, height = "36-72 inches", ph = "6.5-7.0",
        frost = true, peren = true, sow = EI, harvest = "cut_and_come_again")

    p(db, "Angelica", "herb", 3, 9, 60, 90,
        "Full-Partial", "Regular", true, 5,
        "Dill,Chamomile", "Carrot,Fennel",
        "Biennial. Stems candied for confections. Seeds need light and cold to germinate. Grows very tall",
        depth = 0.0f, spacing = 36, rowSpacing = 48,
        germMin = 14, germMax = 28, height = "48-96 inches", ph = "5.0-7.0",
        frost = true, sow = DS, harvest = "cut")

    p(db, "Rue", "herb", 4, 9, 75, 90,
        "Full", "Low", true, 3,
        "Rose,Raspberry,Fig", "Basil,Sage,Mint",
        "Perennial. Repels many pests. Causes skin irritation in some people. Bitter ornamental herb",
        depth = 0.25f, spacing = 18, rowSpacing = 24,
        germMin = 14, germMax = 28, height = "18-36 inches", ph = "6.5-7.0",
        frost = true, peren = true, sow = EI, harvest = "cut")

    p(db, "Sweet Woodruff", "herb", 4, 8, 60, 90,
        "Partial", "Moderate", true, 2,
        "Hosta,Fern,Astilbe", "",
        "Woodland ground cover. Sweet vanilla-hay scent when dried. Used in May wine. Slow to establish",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 14, germMax = 42, height = "6-12 inches", ph = "4.5-5.5",
        frost = true, peren = true, sow = TP, harvest = "cut")

    p(db, "Salad Burnet", "herb", 4, 8, 60, 75,
        "Full-Partial", "Low", true, 2,
        "Thyme,Oregano,Strawberry", "",
        "Perennial. Young leaves taste of cucumber. Evergreen in mild winters. Remove flowers for leaf production",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 7, germMax = 21, height = "12-24 inches", ph = "6.0-7.0",
        frost = true, peren = true, sow = DS, harvest = "cut_and_come_again")

    p(db, "Sweet Cicely", "herb", 3, 7, 90, 120,
        "Partial", "Regular", true, 5,
        "", "",
        "Perennial with anise-flavored leaves and sweet edible seeds. Needs cold stratification to germinate",
        depth = 0.25f, spacing = 24, rowSpacing = 36,
        germMin = 30, germMax = 180, height = "24-48 inches", ph = "6.0-7.0",
        frost = true, peren = true, sow = DS, harvest = "cut")

    p(db, "Chervil", "herb", 3, 7, 40, 60,
        "Partial", "Regular", true, 2,
        "Radish,Lettuce,Broccoli", "",
        "Annual. Delicate anise-parsley flavor. Cool-season herb that bolts in heat. Succession sow",
        depth = 0.25f, spacing = 8, rowSpacing = 12,
        germMin = 7, germMax = 14, height = "12-24 inches", ph = "6.5-7.0",
        frost = true, sow = DS, harvest = "cut_and_come_again")

    p(db, "Curry Leaf", "herb", 9, 11, 60, 90,
        "Full", "Regular", true, 5,
        "", "",
        "Tropical tree essential in South Indian cooking. Not related to curry powder. Bring indoors in cold zones",
        depth = 0.5f, spacing = 48, rowSpacing = 72,
        germMin = 14, germMax = 21, height = "60-180 inches", ph = "6.0-7.0",
        peren = true, sow = TP, harvest = "pick")

    p(db, "Lemon Verbena", "herb", 8, 11, 90, 120,
        "Full", "Moderate", true, 5,
        "Lavender,Rosemary", "",
        "Deciduous shrub. Strongest lemon fragrance of any herb. Loses leaves in winter even where hardy",
        depth = 0.25f, spacing = 48, rowSpacing = 48,
        height = "36-72 inches", ph = "6.0-7.0",
        peren = true, sow = TP, harvest = "cut_and_come_again")

    p(db, "Pineapple Sage", "herb", 8, 11, 60, 90,
        "Full", "Regular", true, 5,
        "Tomato,Pepper", "",
        "Perennial in warm zones, annual elsewhere. Pineapple-scented leaves. Red tubular flowers attract hummingbirds",
        depth = 0.25f, spacing = 24, rowSpacing = 36,
        height = "36-48 inches", ph = "6.0-7.0",
        peren = true, sow = TP, harvest = "cut_and_come_again")

    p(db, "Mexican Oregano", "herb", 9, 11, 75, 90,
        "Full", "Low", true, 3,
        "Tomato,Pepper,Bean", "",
        "Different genus than true oregano (Lippia). Stronger, more citrusy. Essential in Tex-Mex and Latin cooking",
        depth = 0.25f, spacing = 18, rowSpacing = 24,
        germMin = 10, germMax = 14, height = "36-72 inches", ph = "6.0-8.0",
        peren = true, sow = TP, harvest = "cut_and_come_again")

    p(db, "Cuban Oregano", "herb", 9, 11, 60, 90,
        "Partial-Full", "Moderate", true, 3,
        "Tomato,Pepper", "",
        "Succulent herb (Coleus amboinicus). Strong oregano-thyme flavor. Easy from cuttings. Frost-tender",
        depth = 0.5f, spacing = 12, rowSpacing = 18,
        height = "12-18 inches", ph = "6.0-7.5",
        peren = true, sow = TP, harvest = "cut_and_come_again")

    p(db, "Vietnamese Coriander", "herb", 9, 11, 60, 90,
        "Partial", "Regular", true, 2,
        "Pepper,Tomato", "",
        "Heat-loving cilantro substitute. Grows in moist shade. Does not bolt. Propagate from cuttings",
        depth = 0.5f, spacing = 12, rowSpacing = 18,
        height = "12-36 inches", ph = "5.5-7.0",
        peren = true, sow = TP, harvest = "cut_and_come_again")

    p(db, "Culantro", "herb", 8, 11, 75, 90,
        "Partial", "Regular", true, 2,
        "Pepper,Tomato,Bean", "",
        "Stronger cilantro flavor, different plant (Eryngium). Surface sow, needs light. Heat-tolerant, won't bolt",
        depth = 0.0f, spacing = 8, rowSpacing = 12,
        germMin = 14, germMax = 28, height = "8-16 inches", ph = "6.0-7.5",
        sow = EI, harvest = "cut_and_come_again")

    p(db, "Thai Basil", "herb", 4, 11, 50, 75,
        "Full", "Regular", true, 1,
        "Tomato,Pepper", "Sage",
        "Licorice-anise flavor, essential in Thai and Vietnamese cuisine. More heat-tolerant than sweet basil",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 5, germMax = 10, height = "12-24 inches", ph = "6.0-7.0",
        sow = EI, harvest = "cut_and_come_again")

    p(db, "Horehound", "herb", 4, 9, 70, 90,
        "Full", "Low", true, 2,
        "Tomato,Pepper", "",
        "Perennial bitter herb for cough remedies and candy. Very drought tolerant. Can be invasive",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 10, germMax = 14, height = "18-36 inches", ph = "6.0-8.0",
        frost = true, peren = true, sow = EI, harvest = "cut")

    p(db, "Wormwood", "herb", 4, 9, 75, 90,
        "Full", "Low", true, 5,
        "Rose,Carrot", "Fennel,Sage,Anise",
        "Perennial. Strongly aromatic; repels insects. Allelopathic — keep away from edibles. Ornamental silver foliage",
        depth = 0.0f, spacing = 24, rowSpacing = 36,
        germMin = 14, germMax = 28, height = "24-48 inches", ph = "6.0-8.0",
        frost = true, peren = true, sow = EI, harvest = "cut")

    p(db, "Tansy", "herb", 3, 8, 60, 90,
        "Full", "Low", true, 3,
        "Fruit Trees,Rose,Berry Bushes", "",
        "Perennial pest repellent plant. Toxic if ingested in large quantities. Very aggressive spreader",
        depth = 0.0f, spacing = 18, rowSpacing = 36,
        germMin = 7, germMax = 21, height = "24-48 inches", ph = "5.5-7.5",
        frost = true, peren = true, sow = EI, harvest = "cut")

    p(db, "Pennyroyal", "herb", 5, 9, 60, 90,
        "Partial-Full", "Regular", true, 2,
        "", "",
        "Perennial ground cover. Strong mint-family insect repellent. Toxic if ingested — use externally only",
        depth = 0.0f, spacing = 12, rowSpacing = 18,
        germMin = 14, germMax = 21, height = "6-12 inches", ph = "6.0-7.0",
        frost = true, peren = true, sow = EI, harvest = "cut")

    p(db, "Betony", "herb", 4, 8, 75, 90,
        "Full-Partial", "Moderate", true, 3,
        "Rose,Lavender", "",
        "Perennial woodland herb. Traditional medicinal plant for headaches. Attracts pollinators",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 14, germMax = 28, height = "12-24 inches", ph = "5.5-7.0",
        frost = true, peren = true, sow = EI, harvest = "cut")

    p(db, "Meadowsweet", "herb", 3, 8, 90, 120,
        "Partial-Full", "Regular", true, 5,
        "", "",
        "Perennial. Sweet almond-vanilla scented flowers for teas and syrups. Source of aspirin compound",
        depth = 0.0f, spacing = 18, rowSpacing = 36,
        germMin = 14, germMax = 28, height = "24-60 inches", ph = "5.5-7.0",
        frost = true, peren = true, sow = EI, harvest = "cut")

    p(db, "Costmary", "herb", 4, 8, 70, 90,
        "Full-Partial", "Moderate", true, 3,
        "Lavender,Rose", "",
        "Perennial. Balsam-mint scent. Historic Bible leaf herb. Rarely flowers in cooler climates",
        depth = 0.25f, spacing = 24, rowSpacing = 36,
        germMin = 14, germMax = 21, height = "24-48 inches", ph = "6.0-7.0",
        frost = true, peren = true, sow = TP, harvest = "cut")

    p(db, "Soapwort", "herb", 3, 9, 60, 90,
        "Full-Partial", "Moderate", true, 3,
        "", "",
        "Perennial. Roots produce natural soap. Attractive pink flowers. Can be invasive; contain in pots",
        depth = 0.25f, spacing = 12, rowSpacing = 18,
        germMin = 14, germMax = 21, height = "12-24 inches", ph = "6.0-8.0",
        frost = true, peren = true, sow = EI, harvest = "cut")

    p(db, "Elecampane", "herb", 3, 8, 365, 730,
        "Full", "Moderate", true, 10,
        "Lavender,Echinacea", "",
        "Perennial. Large sunflower-like plant. Roots harvested year 2 for medicinal use. Camphor scent",
        depth = 0.25f, spacing = 36, rowSpacing = 48,
        germMin = 14, germMax = 21, height = "48-72 inches", ph = "4.5-7.0",
        frost = true, peren = true, sow = EI, harvest = "dig")
}

// ── Edible Flowers ──

private fun seedEdibleFlowers(db: SupportSQLiteDatabase) {

    p(db, "Nasturtium", "edible_flower", 2, 11, 35, 63,
        "Full", "Moderate", true, 3,
        "Cucumber,Tomato,Bean,Squash,Cabbage", "",
        "Peppery edible flowers, leaves, and seed pods. Trap crop for aphids. Thrives in poor soil",
        depth = 0.5f, spacing = 12, rowSpacing = 12,
        germMin = 7, germMax = 12, height = "12-72 inches", ph = "6.1-7.5",
        sow = DS, harvest = "pick")

    p(db, "Calendula", "edible_flower", 2, 11, 45, 60,
        "Full-Partial", "Regular", true, 2,
        "Tomato,Cucumber,Pea,Carrot,Asparagus", "",
        "Also called pot marigold. Petals for salads, soups (poor man's saffron). Medicinal for salves",
        depth = 0.25f, spacing = 10, rowSpacing = 18,
        germMin = 7, germMax = 14, height = "12-24 inches", ph = "5.5-7.0",
        frost = true, sow = EI, harvest = "pick")

    p(db, "Viola", "edible_flower", 3, 10, 60, 84,
        "Full-Partial", "Regular", true, 1,
        "Alyssum,Primrose,Lobelia,Lettuce", "Mint,Marigold",
        "Mild sweet flowers for garnishing salads, desserts, drinks. Cool-season, blooms through winter zones 7-10",
        depth = 0.125f, spacing = 8, rowSpacing = 12,
        germMin = 10, germMax = 21, height = "6-10 inches", ph = "5.4-6.8",
        frost = true, sow = EI, harvest = "pick")

    p(db, "Sunflower", "edible_flower", 2, 11, 55, 120,
        "Full", "Regular", true, 5,
        "Lettuce,Bean,Cucumber,Squash,Corn", "Potato,Pole Beans,Fennel",
        "Petals and seeds edible. Attracts pollinators and birds. Allelopathic; may inhibit nearby plants",
        depth = 1.0f, spacing = 12, rowSpacing = 30,
        germMin = 7, germMax = 14, height = "24-144 inches", ph = "6.0-7.5",
        sow = DS, harvest = "pick")

    p(db, "Signet Marigold", "edible_flower", 2, 11, 45, 56,
        "Full", "Moderate", true, 2,
        "Tomato,Basil,Pepper,Bean,Cucumber,Squash", "",
        "Tagetes tenuifolia. Citrus-tarragon flavor. Remove bitter white petal base. Repels whiteflies and nematodes",
        depth = 0.25f, spacing = 8, rowSpacing = 12,
        germMin = 5, germMax = 8, height = "6-12 inches", ph = "6.0-7.0",
        sow = EI, harvest = "pick")

    p(db, "Hibiscus (Roselle)", "edible_flower", 4, 11, 120, 150,
        "Full", "Regular", true, 20,
        "Basil,Nasturtium,Pepper", "",
        "Calyces for tea, jams, agua de jamaica. Short-day plant blooms in fall. Start early in cold zones",
        depth = 0.5f, spacing = 36, rowSpacing = 48,
        germMin = 10, germMax = 21, height = "48-84 inches", ph = "5.5-6.8",
        sow = TP, harvest = "pick")

    p(db, "Rose (Rugosa)", "edible_flower", 2, 9, 365, 730,
        "Full", "Low", true, 15,
        "Lavender,Catmint,Allium,Chives,Garlic", "",
        "Petals for rosewater, jams, teas. Hips very high in vitamin C. Extremely hardy and disease resistant",
        depth = 2.0f, spacing = 60, rowSpacing = 72,
        germMin = 14, germMax = 42, height = "36-72 inches", ph = "5.5-7.0",
        frost = true, peren = true, sow = TP, harvest = "pick")

    p(db, "Dianthus", "edible_flower", 3, 9, 70, 120,
        "Full", "Moderate", true, 2,
        "Lavender,Rose,Thyme,Basil", "",
        "Clove-scented edible petals for salads and desserts. Remove bitter white petal base before eating",
        depth = 0.125f, spacing = 9, rowSpacing = 12,
        germMin = 7, germMax = 21, height = "6-18 inches", ph = "6.5-7.5",
        frost = true, peren = true, sow = EI, harvest = "pick")

    p(db, "Edible Chrysanthemum", "edible_flower", 4, 10, 30, 60,
        "Partial-Full", "Regular", true, 2,
        "Mustard,Kale,Lettuce,Radish", "",
        "Shungiku/garland chrysanthemum. Leaves and flowers edible. Quick-growing cool-season crop. Bolts in heat",
        depth = 0.125f, spacing = 6, rowSpacing = 12,
        germMin = 5, germMax = 14, height = "12-36 inches", ph = "6.0-6.5",
        frost = true, sow = DS, harvest = "pick")

    p(db, "Daylily", "edible_flower", 3, 9, 365, 730,
        "Full-Partial", "Moderate", true, 5,
        "Coneflower,Bee Balm,Black-Eyed Susan", "Iris,Mint",
        "Flowers, buds, shoots, tubers all edible. Only Hemerocallis species — NOT true lilies. Each bloom lasts 1 day",
        depth = 1.0f, spacing = 18, rowSpacing = 24,
        germMin = 14, germMax = 60, height = "24-48 inches", ph = "6.0-7.0",
        frost = true, peren = true, sow = TP, harvest = "pick")
}

// ═══════════════════════════════════════════════════════════════════════
//  EXPANDED VARIETIES
// ═══════════════════════════════════════════════════════════════════════

@Suppress("LongMethod")
private fun seedExpandedVarieties(db: SupportSQLiteDatabase) {
    seedExpandedVarietiesBatch1(db)
    seedExpandedVarietiesBatch2(db)
    seedExpandedVarietiesBatch3(db)
    seedExpandedVarietiesBatch4(db)
}

private fun seedExpandedVarietiesBatch1(db: SupportSQLiteDatabase) {
    // ── Leafy Greens ──
    v(db, "Radicchio", "Chioggia", 60, 65, "Classic round head, red and white variegation")
    v(db, "Radicchio", "Treviso", 70, 80, "Elongated head, milder than Chioggia")
    v(db, "Radicchio", "Castelfranco", 75, 90, "Loose head, cream with red speckles, mild")
    v(db, "Endive", "Frisée", 50, 60, "Finely cut curly leaves, classic French")
    v(db, "Endive", "Très Fine Maraîchère", 55, 65, "Very finely cut, tender heart")
    v(db, "Endive", "Rhodos", 50, 55, "Dark green, heat tolerant")
    v(db, "Escarole", "Batavian Full Heart", 85, 100, "Broad thick leaves, blanched heart")
    v(db, "Escarole", "Natacha", 50, 55, "Compact head, bolt resistant")
    v(db, "Escarole", "Eros", 60, 70, "Dark green, cold tolerant")
    v(db, "Mache", "Vit", 40, 50, "Fast growing, small round leaves")
    v(db, "Mache", "D'Etampes", 45, 55, "Large leaves, vigorous")
    v(db, "Mache", "Jade", 50, 60, "Dark green, cold hardy")
    v(db, "Cress", "Wrinkled Crinkled Crumpled", 15, 20, "Curly leaves, peppery")
    v(db, "Cress", "Persian", 15, 20, "Broad leaf, mild flavor")
    v(db, "Cress", "Upland", 18, 20, "Perennial type, watercress flavor on dry land")
    v(db, "Purslane", "Golden", 30, 40, "Yellow-green leaves, upright habit")
    v(db, "Purslane", "Gruner Red", 35, 45, "Red stems, green leaves")
    v(db, "Purslane", "Goldberg", 30, 40, "Large golden leaves, vigorous")
    v(db, "Sorrel", "Large Leaf French", 35, 50, "Classic large-leaf culinary variety")
    v(db, "Sorrel", "Red Veined", 40, 55, "Red-veined leaves, milder lemony flavor")
    v(db, "Sorrel", "Profusion", 35, 50, "Slow to bolt, very productive")
    v(db, "Lamb's Quarters", "Magentaspreen", 40, 50, "Magenta-tipped young leaves, ornamental")
    v(db, "Lamb's Quarters", "Common Wild", 40, 55, "Classic foraging type, very nutritious")
    v(db, "Watercress", "Aqua", 50, 60, "Standard watercress, peppery")
    v(db, "Watercress", "Large Leaf", 55, 65, "Bigger leaves, milder flavor")
    v(db, "Malabar Spinach", "Red Stem", 55, 70, "Red-purple stems, ornamental and edible")
    v(db, "Malabar Spinach", "Green Stem", 55, 65, "All green, slightly milder")
    v(db, "New Zealand Spinach", "Tetragonia", 55, 70, "Only cultivated variety, heat tolerant spinach substitute")
    v(db, "Amaranth Greens", "Red Leaf", 30, 40, "Red-purple foliage, tender young leaves")
    v(db, "Amaranth Greens", "Callaloo", 35, 50, "Caribbean variety, mild flavor")
    v(db, "Amaranth Greens", "White Leaf", 30, 45, "Light green, very tender")
    v(db, "Moringa", "PKM-1", 180, 240, "High-yielding, early fruiting")
    v(db, "Moringa", "PKM-2", 180, 240, "Longer pods than PKM-1, commercial variety")

    // ── Root Vegetables ──
    v(db, "Fennel", "Orion", 75, 85, "Large bulb, bolt resistant, hybrid vigor")
    v(db, "Fennel", "Perfection", 80, 90, "Thick rounded bulb, slow to bolt")
    v(db, "Fennel", "Zefa Fino", 65, 80, "Swiss variety, fast maturing, sweet")
    v(db, "Celeriac", "Brilliant", 110, 120, "Smooth skin, white flesh, vigorous")
    v(db, "Celeriac", "Mars", 110, 120, "Large round root, good storage")
    v(db, "Celeriac", "Diamant", 110, 120, "Dutch variety, very smooth skin")
    v(db, "Rutabaga", "American Purple Top", 80, 95, "Classic variety, purple top, yellow flesh")
    v(db, "Rutabaga", "Laurentian", 90, 100, "Canadian heirloom, sweet after frost")
    v(db, "Rutabaga", "Gilfeather", 85, 95, "Vermont heirloom, white flesh, sweet")
    v(db, "Parsnip", "Hollow Crown", 100, 120, "Classic heirloom, long tapered root")
    v(db, "Parsnip", "Harris Model", 100, 115, "Smooth white skin, fine grained")
    v(db, "Parsnip", "Gladiator", 100, 110, "Hybrid vigor, uniform roots, canker resistant")
    v(db, "Daikon Radish", "Miyashige", 50, 60, "White, 16-18 inches, mild")
    v(db, "Daikon Radish", "Watermelon", 55, 65, "Green outside, pink inside, mild and sweet")
    v(db, "Daikon Radish", "KN-Bravo", 50, 60, "Purple skin and flesh, striking color")
    v(db, "Horseradish", "Big Top Western", 140, 160, "Standard commercial variety, vigorous")
    v(db, "Horseradish", "Maliner Kren", 140, 160, "Czech heirloom, very pungent")
    v(db, "Jicama", "Agua", 150, 180, "Juicy flesh, mild sweet flavor, most common type")
    v(db, "Taro", "Dasheen", 200, 365, "Large central corm, starchy, staple food")
    v(db, "Taro", "Eddoe", 200, 300, "Smaller corms, cold tolerant for taro")
    v(db, "Ginger", "Common Culinary", 120, 300, "Standard culinary ginger, pungent")
    v(db, "Ginger", "Baby Ginger", 120, 150, "Harvested young, pink tips, tender")
    v(db, "Turmeric", "Alleppey", 120, 300, "High curcumin content, deep orange")
    v(db, "Turmeric", "Madras", 120, 300, "Lighter color, standard culinary variety")
    v(db, "Rhubarb", "Victoria", 365, 730, "Classic green and red, reliable, from seed")
    v(db, "Rhubarb", "Crimson Red", 365, 730, "Deep red stalks, sweet flavor")
    v(db, "Rhubarb", "Canada Red", 365, 730, "Very cold hardy, tender red stalks")
    v(db, "Cassava", "Sweet", 240, 365, "Low cyanide, table eating variety")
    v(db, "Cassava", "Bitter", 300, 365, "High starch, requires thorough processing")
}

private fun seedExpandedVarietiesBatch2(db: SupportSQLiteDatabase) {
    // ── Legumes ──
    v(db, "Edamame", "Midori Giant", 90, 100, "Large beans, sweet flavor, early maturing")
    v(db, "Edamame", "Sayamusume", 85, 95, "Japanese variety, compact plants, very sweet")
    v(db, "Edamame", "Chiba Green", 90, 100, "Reliable production, buttery flavor")
    v(db, "Peanut", "Virginia", 130, 150, "Large kernels, roasting type")
    v(db, "Peanut", "Valencia", 120, 140, "3-4 seeds per pod, sweet, boiling type")
    v(db, "Peanut", "Spanish", 120, 130, "Small kernels, high oil, early")
    v(db, "Fava Bean", "Broad Windsor", 80, 100, "Classic large-seeded English type")
    v(db, "Fava Bean", "Aguadulce", 80, 90, "Very long pods, cold hardy, early")
    v(db, "Fava Bean", "Crimson Flowered", 85, 100, "Red flowers, ornamental and edible")
    v(db, "Chickpea", "Desi", 90, 110, "Small dark seeds, nutty flavor, most common worldwide")
    v(db, "Chickpea", "Kabuli", 95, 110, "Large cream seeds, mild, used in hummus")
    v(db, "Lentil", "Crimson Red", 80, 100, "Quick cooking, splits easily, mild flavor")
    v(db, "Lentil", "French Green (Puy)", 90, 110, "Holds shape when cooked, peppery flavor")
    v(db, "Lentil", "Black Beluga", 85, 105, "Small, shiny, holds shape, earthy flavor")
    v(db, "Winged Bean", "Day Neutral", 75, 100, "Flowers in long days unlike most types")
    v(db, "Winged Bean", "Chimbu", 80, 110, "Papua New Guinea origin, vigorous")
    v(db, "Yard Long Bean", "Mosaic", 70, 80, "Purple and green bicolor pods")
    v(db, "Yard Long Bean", "Red Noodle", 70, 85, "Deep red pods, turn green when cooked")
    v(db, "Yard Long Bean", "Stickless Wonder", 70, 80, "Bush type, no trellis needed")

    // ── Asian Greens ──
    v(db, "Bok Choy", "Shanghai Green", 30, 45, "Small, green stems, compact, versatile")
    v(db, "Bok Choy", "Joi Choi", 45, 55, "Large white stems, heat tolerant hybrid")
    v(db, "Bok Choy", "Mei Qing Choi", 35, 45, "Baby type, light green, tender")
    v(db, "Napa Cabbage", "Blues", 60, 70, "Large barrel head, disease resistant hybrid")
    v(db, "Napa Cabbage", "Minuet", 65, 75, "Mini heads, perfect for small gardens")
    v(db, "Napa Cabbage", "Rubicon", 60, 70, "Tall cylindrical head, crisp and sweet")
    v(db, "Mizuna", "Early", 20, 30, "Fast growing, finely serrated leaves")
    v(db, "Mizuna", "Red Streaked", 25, 35, "Purple-red veins, attractive in salads")
    v(db, "Tatsoi", "Yukina Savoy", 25, 45, "Thick crinkled leaves, cold hardy")
    v(db, "Tatsoi", "Rosette", 21, 35, "Flat rosette shape, tender mild flavor")
    v(db, "Komatsuna", "Summerfest", 25, 40, "Heat tolerant, slow bolting")
    v(db, "Komatsuna", "Red Komatsuna", 30, 50, "Purple-red leaves, mild mustard flavor")
    v(db, "Chinese Broccoli", "Kailaan", 35, 50, "Standard variety, thick stems, blue-green")
    v(db, "Chinese Broccoli", "Green Lance", 40, 55, "Hybrid, uniform, tender stems")
    v(db, "Broccoli Rabe", "Sessantina Grossa", 40, 55, "Italian heirloom, large leaves, mild")
    v(db, "Broccoli Rabe", "Quarantina", 40, 45, "Very early, small florets, sharp flavor")
    v(db, "Romanesco", "Veronica", 75, 85, "Uniform fractal heads, bright chartreuse")
    v(db, "Romanesco", "Gitano", 80, 100, "Large heads, good wrapper leaves")

    // ── Cucurbits & Gourds ──
    v(db, "Bitter Melon", "Indian Long", 60, 70, "Dark green, very bumpy, strong bitterness")
    v(db, "Bitter Melon", "Chinese Large", 65, 80, "Pale green, smoother, milder flavor")
    v(db, "Bitter Melon", "Japanese White", 60, 75, "White skin, mild, tender")
    v(db, "Luffa", "Smooth (Angled)", 120, 150, "Ridged skin, eat young or use as sponge")
    v(db, "Luffa", "Common (Smooth)", 150, 200, "Round cross-section, best sponge variety")
    v(db, "Chayote", "Green Smooth", 120, 140, "Standard variety, light green, mild")
    v(db, "Chayote", "White", 120, 150, "Pale cream, slightly sweeter")
    v(db, "Calabash", "Long Handle Dipper", 60, 100, "Long neck, traditional dipper shape")
    v(db, "Calabash", "Bushel Basket", 80, 120, "Large round, used as container")
    v(db, "Kabocha Squash", "Red Kuri", 85, 100, "Red-orange, teardrop shape, chestnut flavor")
    v(db, "Kabocha Squash", "Sweet Mama", 85, 100, "Grey-green, very sweet dense flesh")
    v(db, "Kabocha Squash", "Sunshine", 95, 110, "Bright orange, compact vine, sweet")
    v(db, "Delicata Squash", "Honey Boat", 80, 100, "Extra sweet, dark green stripes")
    v(db, "Delicata Squash", "Sugar Loaf", 85, 100, "Larger fruit, tan with green stripes")
    v(db, "Hubbard Squash", "Blue Hubbard", 100, 120, "Blue-grey, very large, excellent keeper")
    v(db, "Hubbard Squash", "Golden Hubbard", 90, 110, "Orange-red, sweet, slightly smaller")
    v(db, "Patty Pan Squash", "Sunburst", 45, 55, "Bright yellow, scalloped edges")
    v(db, "Patty Pan Squash", "Benning's Green Tint", 45, 55, "Pale green, heirloom, prolific")
    v(db, "Snake Gourd", "Extra Long", 55, 80, "3-5 feet long, eat young")
    v(db, "Snake Gourd", "Short", 55, 70, "12-18 inches, more compact, culinary type")

    // ── Warm Season Vegetables ──
    v(db, "Tomatillo", "Grande Rio Verde", 60, 85, "Large green fruits, high yield")
    v(db, "Tomatillo", "Purple", 70, 100, "Purple skin, sweeter, good fresh")
    v(db, "Tomatillo", "Toma Verde", 60, 80, "Classic green, tangy, perfect for salsa verde")
    v(db, "Ground Cherry", "Aunt Molly's", 65, 75, "Orange fruit, sweet, heirloom from Poland")
    v(db, "Ground Cherry", "Cossack Pineapple", 65, 75, "Pineapple flavor, golden yellow")
    v(db, "Ground Cherry", "Goldie", 65, 75, "Large fruit, very sweet, productive")
}

private fun seedExpandedVarietiesBatch3(db: SupportSQLiteDatabase) {
    // ── Berries 1 ──
    v(db, "Gooseberry", "Invicta", 730, 1095, "Green, mildew resistant, heavy cropping")
    v(db, "Gooseberry", "Hinnomaki Red", 730, 1095, "Red-purple, sweet, disease resistant")
    v(db, "Gooseberry", "Pixwell", 730, 1095, "Pink, nearly thornless, very hardy")
    v(db, "Currant", "Red Lake", 730, 1095, "Red, vigorous, heavy yields, tart")
    v(db, "Currant", "Crandall", 730, 1095, "Black currant, clove-scented flowers, sweet")
    v(db, "Currant", "White Imperial", 730, 1095, "White, sweet, translucent berries")
    v(db, "Elderberry", "Adams", 730, 1095, "Large clusters, reliable, American type")
    v(db, "Elderberry", "York", 730, 1095, "Largest berries, late season, needs pollinator")
    v(db, "Elderberry", "Nova", 730, 1095, "Sweet flavor, mid-season, Canadian variety")
    v(db, "Mulberry", "Illinois Everbearing", 730, 1460, "Long harvest season, sweet-tart, very hardy")
    v(db, "Mulberry", "Silk Hope", 730, 1460, "Large sweet fruit, vigorous grower")
    v(db, "Mulberry", "Oscar", 730, 1460, "Large black fruit, dwarf tree")
    v(db, "Pawpaw", "Sunflower", 1460, 2920, "Self-fertile, medium fruit, reliable")
    v(db, "Pawpaw", "Peterson's Shenandoah", 1460, 2920, "Large fruit, sweet custard flavor")
    v(db, "Pawpaw", "Mango", 1460, 2920, "Rich tropical flavor, medium size")
    v(db, "American Persimmon", "Meader", 1095, 2190, "Self-fertile, early ripening, cold hardy")
    v(db, "American Persimmon", "Prok", 1095, 2190, "Large fruit, excellent flavor, productive")
    v(db, "American Persimmon", "Yates", 1095, 2190, "Small sweet fruit, very cold hardy")
    v(db, "Hardy Kiwi", "Issai", 730, 1825, "Self-fertile, small fruit, reliable")
    v(db, "Hardy Kiwi", "Anna", 730, 1825, "Female, large fruit, needs male pollinator")
    v(db, "Hardy Kiwi", "Ken's Red", 730, 1825, "Red-skinned fruit, ornamental, female")
    v(db, "Honeyberry", "Blue Velvet", 365, 1095, "Sweet, mild flavor, good for fresh eating")
    v(db, "Honeyberry", "Tundra", 365, 1095, "Large fruit, tangy-sweet, Canadian bred")
    v(db, "Honeyberry", "Berry Blue", 365, 1095, "Sweet, oval fruit, vigorous")
    v(db, "Serviceberry", "Regent", 730, 1095, "Compact shrub, sweet fruit, great for hedges")
    v(db, "Serviceberry", "Thiessen", 730, 1095, "Large fruit, saskatoon type, sweet")
    v(db, "Serviceberry", "Martin", 730, 1095, "Large berries, upright tree form")
    v(db, "Goji Berry", "Crimson Star", 730, 1825, "Large sweet berries, heavy producer")
    v(db, "Goji Berry", "Phoenix Tears", 730, 1825, "Sweet, tear-drop shape, early bearing")
    v(db, "Cranberry", "Stevens", 1095, 1825, "Large berries, high yield, commercial standard")
    v(db, "Cranberry", "Ben Lear", 1095, 1825, "Early ripening, large dark red fruit")
    v(db, "Lingonberry", "Koralle", 365, 730, "Heavy cropper, two harvests per year")
    v(db, "Lingonberry", "Red Pearl", 365, 730, "Large berries, compact plant, ornamental")

    // ── Berries 2 ──
    v(db, "Boysenberry", "Thornless", 365, 730, "Thornless canes, large purple-black fruit")
    v(db, "Loganberry", "Thornless", 365, 730, "Thornless sport, dark red, tart-sweet")
    v(db, "Marionberry", "Marion", 365, 730, "Standard variety, complex blackberry flavor")
    v(db, "Tayberry", "Buckingham", 365, 730, "Thornless, large conical red-purple fruit")
    v(db, "Dewberry", "Austin", 1460, 1825, "Southern trailing blackberry, early, sweet")
    v(db, "Dewberry", "Lucretia", 1460, 1825, "Large berries, trailing habit, very hardy")
    v(db, "Huckleberry", "Black", 1095, 1825, "Wild type, intense sweet-tart, mountain habitat")
    v(db, "Huckleberry", "Red", 1095, 1825, "Tart, smaller fruit, shade tolerant")
    v(db, "Muscadine Grape", "Noble", 730, 1095, "Self-fertile, black, wine and juice")
    v(db, "Muscadine Grape", "Carlos", 730, 1095, "Self-fertile, bronze, sweet fresh eating")
    v(db, "Muscadine Grape", "Scuppernong", 730, 1095, "Bronze, female, classic Southern variety")
    v(db, "Quince", "Orange", 1825, 2190, "Orange flesh, aromatic, good for preserves")
    v(db, "Quince", "Aromatnaya", 1825, 2190, "Russian variety, can eat fresh when ripe")
    v(db, "Quince", "Smyrna", 1825, 2190, "Large fruit, tender flesh, Turkish heirloom")
    v(db, "Cape Gooseberry", "Aunt Molly's", 100, 120, "Orange fruit, sweet-tart, prolific")
    v(db, "Cape Gooseberry", "Giant", 110, 140, "Large fruit, sweeter flavor")
    v(db, "Prickly Pear", "Santa Rita", 1095, 1460, "Purple pads, red fruit, ornamental")
    v(db, "Prickly Pear", "Burbank Spineless", 1095, 1460, "Nearly spineless, green fruit, easy harvest")
    v(db, "Che Fruit", "Norris", 1095, 1825, "Sweet red fruit, selected cultivar")

    // ── Warm Zone Fruits ──
    v(db, "Pomegranate", "Wonderful", 730, 1095, "Deep red, large, most popular commercial variety")
    v(db, "Pomegranate", "Eversweet", 730, 1095, "Non-staining clear juice, very sweet")
    v(db, "Pomegranate", "Russian 26", 730, 1095, "Cold hardy to zone 6, medium red fruit")
    v(db, "Jujube", "Li", 730, 1460, "Large round fruit, sweet, early bearing")
    v(db, "Jujube", "Lang", 730, 1460, "Pear-shaped, good for drying, productive")
    v(db, "Jujube", "Sugar Cane", 730, 1460, "Very sweet, small round fruit, crisp")
    v(db, "Guava", "Ruby Supreme", 730, 1095, "Red flesh, sweet, cold hardy for guava")
    v(db, "Guava", "Tropical White", 730, 1095, "White flesh, mild sweet, fragrant")
    v(db, "Loquat", "Champagne", 730, 1825, "Yellow flesh, sweet-tart, vigorous tree")
    v(db, "Loquat", "Gold Nugget", 730, 1825, "Large sweet fruit, orange flesh")
    v(db, "Loquat", "Tanaka", 730, 1825, "Japanese variety, large oval fruit, late")
    v(db, "Dragonfruit", "American Beauty", 365, 1095, "Red skin, magenta flesh, self-fertile")
    v(db, "Dragonfruit", "Physical Graffiti", 365, 1095, "Red skin, white flesh, sweet, hybrid vigor")
    v(db, "Dragonfruit", "Yellow Dragon", 365, 1095, "Yellow skin, white flesh, sweetest variety")
    v(db, "Banana", "Dwarf Cavendish", 365, 545, "Standard dessert banana, compact plant, 6-8 ft")
    v(db, "Banana", "Ice Cream (Blue Java)", 365, 545, "Blue-silver peel, creamy vanilla flavor")
    v(db, "Banana", "Lady Finger", 365, 545, "Small sweet fruit, slender plant")

    // ── Citrus ──
    v(db, "Lemon", "Eureka", 365, 1095, "Standard supermarket lemon, everbearing")
    v(db, "Lemon", "Lisbon", 365, 1095, "Very thorny, cold tolerant, juicy")
    v(db, "Lime", "Persian (Bearss)", 365, 1095, "Seedless, large fruit, main commercial lime")
    v(db, "Lime", "Key Lime", 365, 1095, "Small aromatic fruit, essential for Key lime pie")
    v(db, "Orange", "Washington Navel", 365, 1095, "Seedless, easy peel, classic eating orange")
    v(db, "Orange", "Valencia", 365, 1095, "Juice orange, late season, few seeds")
    v(db, "Orange", "Cara Cara", 365, 1095, "Pink-red flesh navel, sweet, low acid")
    v(db, "Grapefruit", "Ruby Red", 1095, 1825, "Red flesh, sweet-tart, Texas origin")
    v(db, "Grapefruit", "Marsh", 1095, 1825, "White flesh, seedless, classic flavor")
    v(db, "Kumquat", "Nagami", 365, 730, "Oval fruit, sweet rind, tart flesh")
    v(db, "Kumquat", "Meiwa", 365, 730, "Round fruit, sweeter overall, eat whole")
    v(db, "Meyer Lemon", "Improved Meyer", 365, 730, "Virus-free selection, sweet lemon-orange hybrid")
    v(db, "Satsuma", "Owari", 365, 1095, "Seedless, easy peel, very cold hardy citrus")
    v(db, "Satsuma", "Brown Select", 365, 1095, "Early ripening, sweet, Louisiana selection")
}

private fun seedExpandedVarietiesBatch4(db: SupportSQLiteDatabase) {
    seedExpandedVarietiesBatch4a(db)
    seedExpandedVarietiesBatch4b(db)
}

private fun seedExpandedVarietiesBatch4a(db: SupportSQLiteDatabase) {
    // ── Herbs 1 ──
    v(db, "Lemon Balm", "Citronella", 60, 70, "Strong lemon scent, compact growth")
    v(db, "Lemon Balm", "Aurea", 60, 70, "Golden-green variegated leaves, ornamental")
    v(db, "Lemon Balm", "Compacta", 60, 70, "Dwarf form, dense habit, ideal containers")
    v(db, "Chamomile", "German (Matricaria)", 60, 65, "Annual, apple-scented, best for tea")
    v(db, "Chamomile", "Roman", 60, 65, "Perennial, creeping groundcover, bitter tea")
    v(db, "Stevia", "Sweetie", 60, 90, "High rebaudioside A, very sweet leaves")
    v(db, "Stevia", "Sugar In The Raw", 60, 90, "Compact, heavy leaf producer")
    v(db, "Catnip", "Common", 60, 80, "Classic catnip, very attractive to cats")
    v(db, "Catnip", "Citriodora", 60, 80, "Lemon-scented, less attractive to cats")
    v(db, "Hyssop", "Official", 75, 90, "Blue flowers, classic medicinal herb")
    v(db, "Hyssop", "Anise Hyssop", 75, 90, "Licorice-mint flavor, pollinator magnet")
    v(db, "Borage", "Common Blue", 50, 60, "Blue star flowers, cucumber-flavored leaves")
    v(db, "Borage", "Alba", 50, 60, "White flowers, same cucumber flavor")
    v(db, "Comfrey", "Bocking 14", 365, 730, "Sterile, non-invasive, best for mulch/compost")
    v(db, "Comfrey", "Common", 365, 730, "Self-seeding, spreads readily, medicinal")
    v(db, "Feverfew", "Single Flower", 80, 110, "Classic daisy-like flower, medicinal")
    v(db, "Feverfew", "Aureum", 80, 110, "Golden foliage, ornamental, same properties")
    v(db, "Valerian", "Common", 120, 180, "Pink-white flowers, used for sleep aid root")
    v(db, "Valerian", "Red", 120, 180, "Centranthus ruber, ornamental, not medicinal")
    v(db, "Echinacea", "Purple Coneflower", 120, 180, "Classic purple petals, immune support root")
    v(db, "Echinacea", "White Swan", 120, 180, "White petals, same medicinal properties")
    v(db, "Echinacea", "Magnus", 120, 180, "Large flat flowers, won Perennial of Year")
    v(db, "Bee Balm", "Jacob Cline", 90, 120, "Deep red flowers, mildew resistant")
    v(db, "Bee Balm", "Marshall's Delight", 90, 120, "Pink flowers, excellent mildew resistance")
    v(db, "Bee Balm", "Wild Bergamot", 90, 120, "Lavender, native species, drought tolerant")
    v(db, "Anise", "Common", 75, 130, "Standard culinary anise, licorice seed")
    v(db, "Caraway", "Common", 60, 75, "Biennial, rye bread spice, first-year roots edible")
    v(db, "Herb Fennel", "Bronze", 60, 90, "Copper-bronze foliage, ornamental, anise flavor")
    v(db, "Herb Fennel", "Sweet", 60, 90, "Finely cut green leaves, stronger flavor")
    v(db, "Epazote", "Common", 50, 55, "Traditional Mexican herb, bean seasoning")
    v(db, "Shiso", "Red (Aka)", 55, 70, "Purple-red leaves, used in umeboshi")
    v(db, "Shiso", "Green (Ao)", 55, 70, "Green leaves, milder, sashimi garnish")
    v(db, "Summer Savory", "Common", 60, 70, "Annual, peppery bean herb, delicate")
    v(db, "Winter Savory", "Common", 80, 90, "Perennial, stronger flavor, compact shrub")
    v(db, "French Tarragon", "French", 42, 60, "True tarragon, anise flavor, does not set seed")
    v(db, "Marjoram", "Sweet", 60, 80, "Classic sweet marjoram, knot-like flower buds")
    v(db, "Marjoram", "Italian", 60, 80, "Oregano-marjoram hybrid, robust flavor")
}

private fun seedExpandedVarietiesBatch4b(db: SupportSQLiteDatabase) {
    // ── Herbs 2 ──
    v(db, "Lovage", "Common", 80, 90, "Celery-like flavor, tall perennial")
    v(db, "Angelica", "Garden", 60, 90, "Biennial, candied stems, licorice flavor")
    v(db, "Rue", "Common", 75, 90, "Blue-grey foliage, bitter, companion plant")
    v(db, "Rue", "Jackman's Blue", 75, 90, "Intense blue-grey foliage, compact")
    v(db, "Sweet Woodruff", "Common", 60, 90, "Groundcover, sweet hay scent, May wine herb")
    v(db, "Salad Burnet", "Common", 60, 75, "Cucumber-flavored leaves, rosette form")
    v(db, "Sweet Cicely", "Common", 90, 120, "Anise-flavored leaves, sweet seeds")
    v(db, "Chervil", "Brussels Winter", 40, 60, "Cold hardy, slow bolting, fine-cut leaves")
    v(db, "Chervil", "Vertissimo", 40, 55, "Dark green, slower to bolt, compact")
    v(db, "Curry Leaf", "Gamthi", 60, 90, "Dwarf, thick aromatic leaves, best for pots")
    v(db, "Curry Leaf", "Regular", 60, 90, "Standard tree form, fast growing")
    v(db, "Lemon Verbena", "Common", 90, 120, "Strongest lemon scent of any herb")
    v(db, "Pineapple Sage", "Common", 60, 90, "Red tubular flowers, pineapple-scented leaves")
    v(db, "Mexican Oregano", "Lippia graveolens", 75, 90, "Stronger than Mediterranean oregano, earthy")
    v(db, "Cuban Oregano", "Variegated", 60, 90, "White-edged leaves, ornamental, succulent herb")
    v(db, "Vietnamese Coriander", "Common", 60, 90, "V-shaped leaf markings, cilantro substitute")
    v(db, "Culantro", "Common", 75, 90, "Long serrated leaves, stronger than cilantro")
    v(db, "Thai Basil", "Siam Queen", 50, 60, "Purple stems, licorice-anise, AAS winner")
    v(db, "Thai Basil", "Sweet Thai", 50, 75, "Classic Thai cooking basil, compact")
    v(db, "Horehound", "Common White", 70, 90, "Woolly leaves, bitter, traditional cough drops")
    v(db, "Wormwood", "Common", 75, 90, "Silver-grey foliage, bitter, pest deterrent")
    v(db, "Wormwood", "Powis Castle", 75, 90, "Finely cut silver foliage, ornamental")
    v(db, "Tansy", "Common", 60, 90, "Yellow button flowers, insect repellent")
    v(db, "Tansy", "Fernleaf", 60, 90, "Finely cut foliage, less aggressive")
    v(db, "Pennyroyal", "European", 60, 90, "Creeping mint relative, strong menthol")
    v(db, "Betony", "Wood Betony", 75, 90, "Purple flower spikes, groundcover")
    v(db, "Meadowsweet", "Common", 90, 120, "Creamy flower plumes, aspirin-like compound")
    v(db, "Meadowsweet", "Aurea", 90, 120, "Golden foliage, same sweet flowers")
    v(db, "Costmary", "Common", 70, 90, "Bible leaf, minty-balsam, pressed in books")
    v(db, "Soapwort", "Common", 60, 90, "Pink flowers, lathers in water, gentle cleanser")
    v(db, "Elecampane", "Common", 365, 730, "Tall sunflower-like, medicinal root, camphor scent")

    // ── Edible Flowers ──
    v(db, "Nasturtium", "Jewel Mix", 35, 50, "Compact, mixed colors, peppery edible flowers")
    v(db, "Nasturtium", "Empress of India", 40, 55, "Dark foliage, scarlet flowers, compact")
    v(db, "Nasturtium", "Alaska", 35, 50, "Variegated leaves, mixed flower colors")
    v(db, "Calendula", "Pacific Beauty", 45, 60, "Mixed doubles, orange and yellow, large")
    v(db, "Calendula", "Resina", 45, 55, "High resin content, best medicinal variety")
    v(db, "Calendula", "Flashback", 45, 60, "Bicolor petals, unique pastel shades")
    v(db, "Viola", "Sorbet Mix", 60, 84, "Compact, winter hardy, huge color range")
    v(db, "Viola", "Johnny Jump Up", 60, 75, "Classic tricolor, self-sows freely")
    v(db, "Sunflower", "Mammoth Russian", 80, 120, "10-12 ft, huge heads, edible seeds")
    v(db, "Sunflower", "Teddy Bear", 55, 75, "Dwarf 2-3 ft, fluffy double flowers")
    v(db, "Sunflower", "Italian White", 60, 90, "Pale cream petals, dark center, branching")
    v(db, "Signet Marigold", "Tangerine Gem", 45, 56, "Citrus-scented, orange, ferny foliage")
    v(db, "Signet Marigold", "Lemon Gem", 45, 56, "Bright yellow, lemon flavor, compact")
    v(db, "Hibiscus (Roselle)", "Thai Red", 120, 150, "Deep red calyces, classic for hibiscus tea")
    v(db, "Hibiscus (Roselle)", "USVL White", 120, 150, "Green calyces, milder flavor, less tart")
    v(db, "Rose (Rugosa)", "Hansa", 365, 730, "Double mauve-pink, very fragrant, ultra cold hardy")
    v(db, "Rose (Rugosa)", "Blanc Double de Coubert", 365, 730, "Double white, intensely fragrant, disease-free")
    v(db, "Dianthus", "Cheddar Pink", 70, 120, "Compact, pink, clove-scented, heirloom")
    v(db, "Dianthus", "Firewitch", 70, 100, "Magenta, blue foliage, long bloom, Perennial of Year")
    v(db, "Edible Chrysanthemum", "Shungiku", 30, 50, "Japanese greens type, mild nutty flavor")
    v(db, "Edible Chrysanthemum", "Garland", 30, 60, "Large leaves, milder, popular in Korean cooking")
    v(db, "Daylily", "Stella de Oro", 365, 730, "Reblooming gold, most popular landscape daylily")
    v(db, "Daylily", "Happy Returns", 365, 730, "Reblooming lemon yellow, compact, fragrant")
    v(db, "Daylily", "Hyperion", 365, 730, "Tall lemon yellow, classic fragrant heirloom")
}

// ═══════════════════════════════════════════════════════════════════════
//  EXPANDED PLANTING WINDOWS
// ═══════════════════════════════════════════════════════════════════════

@Suppress("LongMethod")
private fun seedExpandedWindows(db: SupportSQLiteDatabase) {
    seedExpandedWindowsBatch1(db)
    seedExpandedWindowsBatch2(db)
    seedExpandedWindowsBatch3(db)
    seedExpandedWindowsBatch4(db)
}

private fun seedExpandedWindowsBatch1(db: SupportSQLiteDatabase) {
    seedExpandedWindowsBatch1a(db)
    seedExpandedWindowsBatch1b(db)
}

private fun seedExpandedWindowsBatch1a(db: SupportSQLiteDatabase) {
    // ── Leafy Greens ──
    w(db, "Radicchio", C, 5, 7, TP, "Start indoors early, transplant after frost")
    w(db, "Radicchio", M, 3, 4, EI, "Spring or late summer sowing for fall harvest")
    w(db, "Radicchio", W, 9, 11, DS, "Fall/winter crop, heads form in cool weather")
    w(db, "Radicchio", H, 10, 1, DS, "Winter crop only, bolts in heat")
    w(db, "Endive", C, 5, 7, TP, "Start indoors, transplant after last frost")
    w(db, "Endive", M, 3, 5, EI, "Direct sow spring or fall")
    w(db, "Endive", W, 9, 11, DS, "Fall sowing preferred, mild winters")
    w(db, "Endive", H, 10, 1, DS, "Cool season crop, winter only")
    w(db, "Escarole", C, 5, 7, TP, "Transplant after frost, harvest before heat")
    w(db, "Escarole", M, 3, 5, EI, "Spring or late summer sowing")
    w(db, "Escarole", W, 9, 11, DS, "Best as fall/winter crop")
    w(db, "Escarole", H, 10, 1, DS, "Winter planting, bolts in warm weather")
    w(db, "Mache", EC, 4, 5, DS, "Short season, cold tolerant, sow as early as possible")
    w(db, "Mache", C, 3, 5, DS, "Early spring or late fall sow")
    w(db, "Mache", M, 2, 4, DS, "Late winter/early spring, self-sows in fall")
    w(db, "Mache", W, 10, 2, DS, "Fall through winter crop")
    w(db, "Cress", EC, 5, 6, DS, "Very fast, sow after last frost")
    w(db, "Cress", C, 4, 6, DS, "Cool season, multiple successions")
    w(db, "Cress", M, 3, 5, DS, "Spring or fall, bolts in heat")
    w(db, "Cress", W, 10, 2, DS, "Winter crop in mild climates")
    w(db, "Purslane", C, 5, 7, DS, "Sow after soil warms, heat lover")
    w(db, "Purslane", M, 4, 8, DS, "Spring through summer")
    w(db, "Purslane", W, 3, 10, DS, "Long season, thrives in heat")
    w(db, "Purslane", H, 2, 11, DS, "Nearly year-round in hot zones")
    w(db, "Sorrel", EC, 5, 6, DS, "Perennial, plant once, harvest years")
    w(db, "Sorrel", C, 4, 6, DS, "Early spring sowing, cold tolerant")
    w(db, "Sorrel", M, 3, 5, DS, "Very early spring")
    w(db, "Sorrel", W, 10, 2, DS, "Fall/winter planting, perennial")
    w(db, "Lamb's Quarters", C, 5, 7, DS, "Self-sows readily, volunteer weed-crop")
    w(db, "Lamb's Quarters", M, 3, 8, DS, "Spring through summer, very easy")
    w(db, "Lamb's Quarters", W, 2, 10, DS, "Long season, harvest young leaves")
    w(db, "Watercress", C, 5, 7, DS, "Needs constant moisture, stream edges")
    w(db, "Watercress", M, 3, 9, DS, "Spring through fall in boggy conditions")
    w(db, "Watercress", W, 10, 4, DS, "Fall through spring, goes dormant in heat")
    w(db, "Watercress", H, 10, 3, DS, "Winter crop, needs shade and water")
    w(db, "Malabar Spinach", M, 5, 7, TP, "Needs warm soil, start indoors")
    w(db, "Malabar Spinach", W, 4, 8, EI, "Long warm season, trellis needed")
    w(db, "Malabar Spinach", H, 3, 9, DS, "Thrives in heat and humidity")
    w(db, "New Zealand Spinach", C, 5, 6, DS, "After last frost, heat tolerant spinach sub")
    w(db, "New Zealand Spinach", M, 4, 7, DS, "Spring through summer, self-sows")
    w(db, "New Zealand Spinach", W, 3, 9, DS, "Long season, spreads as groundcover")
    w(db, "New Zealand Spinach", H, 2, 10, DS, "Nearly year-round, true heat lover")
    w(db, "Amaranth Greens", C, 5, 7, DS, "After last frost, needs warm soil")
    w(db, "Amaranth Greens", M, 4, 8, DS, "Spring through summer")
    w(db, "Amaranth Greens", W, 3, 10, DS, "Long warm season")
    w(db, "Amaranth Greens", H, 2, 11, DS, "Nearly year-round")
    w(db, "Moringa", W, 4, 6, TP, "Transplant after all frost danger past")
    w(db, "Moringa", H, 3, 7, EI, "Thrives in extreme heat, fast growing tree")
}

private fun seedExpandedWindowsBatch1b(db: SupportSQLiteDatabase) {
    // ── Root Vegetables ──
    w(db, "Fennel", C, 5, 6, TP, "Start indoors, transplant after frost")
    w(db, "Fennel", M, 3, 5, EI, "Spring or late summer for fall bulbs")
    w(db, "Fennel", W, 9, 11, DS, "Fall sowing for winter harvest")
    w(db, "Fennel", H, 10, 1, DS, "Winter crop, bolts in heat")
    w(db, "Celeriac", EC, 5, 6, TP, "Start indoors 10 weeks early, long season")
    w(db, "Celeriac", C, 4, 5, TP, "Start indoors very early, needs long cool season")
    w(db, "Celeriac", M, 2, 4, TP, "Start indoors winter, transplant spring")
    w(db, "Rutabaga", EC, 5, 6, DS, "Spring sow for fall harvest")
    w(db, "Rutabaga", C, 5, 7, DS, "Late spring or midsummer for fall")
    w(db, "Rutabaga", M, 7, 8, DS, "Midsummer sow for fall harvest, sweetens after frost")
    w(db, "Rutabaga", W, 8, 10, DS, "Late summer/fall, grows through winter")
    w(db, "Parsnip", EC, 5, 6, DS, "Sow as early as possible, very slow to germinate")
    w(db, "Parsnip", C, 4, 6, DS, "Spring sow, harvest after frost for sweetness")
    w(db, "Parsnip", M, 3, 5, DS, "Early spring, leave in ground until fall")
    w(db, "Parsnip", W, 9, 11, DS, "Fall sow for winter harvest")
    w(db, "Daikon Radish", EC, 5, 6, DS, "Spring sow, bolt resistant varieties only")
    w(db, "Daikon Radish", C, 4, 5, DS, "Spring or late summer for fall harvest")
    w(db, "Daikon Radish", M, 8, 9, DS, "Late summer sowing, prefers fall harvest")
    w(db, "Daikon Radish", W, 9, 11, DS, "Fall/winter crop, cool weather needed")
    w(db, "Daikon Radish", H, 10, 12, DS, "Winter only, fast growing")
    w(db, "Horseradish", EC, 4, 5, DS, "Plant root cuttings in early spring")
    w(db, "Horseradish", C, 3, 5, DS, "Early spring, perennial, harvest fall/winter")
    w(db, "Horseradish", M, 2, 4, DS, "Late winter/early spring root cuttings")
    w(db, "Jicama", W, 3, 5, TP, "Start indoors, needs 9+ month season")
    w(db, "Jicama", H, 2, 4, DS, "Direct sow in warm soil, long season needed")
    w(db, "Taro", W, 3, 5, TP, "Plant corms after soil warms, needs moisture")
    w(db, "Taro", H, 2, 5, DS, "Plant corms, needs 7-12 months frost-free")
    w(db, "Ginger", W, 3, 5, TP, "Plant rhizomes in spring, harvest late fall")
    w(db, "Ginger", H, 2, 5, DS, "Plant rhizomes, needs 8-10 months warm")
    w(db, "Turmeric", W, 3, 5, TP, "Plant rhizomes after last frost")
    w(db, "Turmeric", H, 2, 5, DS, "Plant rhizomes, needs 8-10 months")
    w(db, "Rhubarb", EC, 4, 5, TP, "Plant crowns in early spring, perennial")
    w(db, "Rhubarb", C, 3, 5, TP, "Plant crowns early spring, needs winter chill")
    w(db, "Rhubarb", M, 2, 3, TP, "Late winter planting, needs cold dormancy")
    w(db, "Cassava", H, 3, 5, DS, "Plant stem cuttings, needs 8-12 months frost-free")
    w(db, "Cassava", W, 4, 5, DS, "Plant cuttings after last frost, long season")
}

private fun seedExpandedWindowsBatch2(db: SupportSQLiteDatabase) {
    // ── Legumes ──
    w(db, "Edamame", EC, 5, 6, DS, "Short season varieties only, after last frost")
    w(db, "Edamame", C, 5, 7, DS, "After last frost, needs warm soil")
    w(db, "Edamame", M, 4, 7, DS, "Spring/summer, 60°F+ soil")
    w(db, "Edamame", W, 3, 8, DS, "Long window, successive plantings")
    w(db, "Peanut", C, 5, 6, DS, "Short season varieties, plastic mulch helps")
    w(db, "Peanut", M, 4, 6, DS, "After frost, needs 120+ frost-free days")
    w(db, "Peanut", W, 3, 6, DS, "Long warm season ideal")
    w(db, "Peanut", H, 2, 7, DS, "Very long season, thrives in heat")
    w(db, "Fava Bean", EC, 5, 6, DS, "Spring sow, prefers cool weather")
    w(db, "Fava Bean", C, 3, 5, DS, "Early spring, tolerates light frost")
    w(db, "Fava Bean", M, 2, 4, DS, "Late winter/early spring, or fall sow")
    w(db, "Fava Bean", W, 10, 2, DS, "Fall/winter crop, doesn't like heat")
    w(db, "Fava Bean", H, 10, 1, DS, "Cool season only, winter planting")
    w(db, "Chickpea", C, 5, 6, DS, "After last frost, desi types more hardy")
    w(db, "Chickpea", M, 3, 5, DS, "Spring sowing, needs dry harvest period")
    w(db, "Chickpea", W, 2, 4, DS, "Early spring, harvest before summer heat")
    w(db, "Lentil", C, 4, 5, DS, "Early spring, cool season crop")
    w(db, "Lentil", M, 2, 4, DS, "Late winter/spring, tolerates light frost")
    w(db, "Lentil", W, 10, 2, DS, "Fall/winter crop")
    w(db, "Lentil", H, 10, 1, DS, "Cool season only")
    w(db, "Winged Bean", W, 4, 6, TP, "After frost, needs long warm season")
    w(db, "Winged Bean", H, 3, 7, DS, "Tropical, all parts edible")
    w(db, "Yard Long Bean", C, 5, 7, DS, "After last frost, needs trellis and heat")
    w(db, "Yard Long Bean", M, 4, 7, DS, "Spring/summer, warm soil required")
    w(db, "Yard Long Bean", W, 3, 8, DS, "Long season, successive plantings")
    w(db, "Yard Long Bean", H, 2, 9, DS, "Thrives in heat and humidity")

    // ── Asian Greens ──
    w(db, "Bok Choy", EC, 5, 6, TP, "Start indoors, short season, bolt resistant types")
    w(db, "Bok Choy", C, 4, 5, EI, "Early spring or late summer for fall")
    w(db, "Bok Choy", M, 3, 4, EI, "Spring or fall, bolts in heat")
    w(db, "Bok Choy", W, 9, 11, DS, "Fall/winter crop")
    w(db, "Bok Choy", H, 10, 2, DS, "Cool season only, winter planting")
    w(db, "Napa Cabbage", C, 5, 6, TP, "Start indoors, transplant, bolt resistant types")
    w(db, "Napa Cabbage", M, 7, 8, DS, "Midsummer sow for fall harvest best")
    w(db, "Napa Cabbage", W, 8, 10, DS, "Late summer through fall")
    w(db, "Mizuna", EC, 5, 6, DS, "Fast crop, cold tolerant")
    w(db, "Mizuna", C, 4, 6, DS, "Spring or fall, very cold hardy")
    w(db, "Mizuna", M, 3, 5, DS, "Spring or fall successions")
    w(db, "Mizuna", W, 9, 2, DS, "Fall through winter, nearly year-round")
    w(db, "Tatsoi", C, 4, 6, DS, "Spring or late summer for fall")
    w(db, "Tatsoi", M, 3, 5, DS, "Spring or fall, cold hardy rosettes")
    w(db, "Komatsuna", C, 4, 7, DS, "Spring through midsummer, heat tolerant")
    w(db, "Komatsuna", M, 3, 9, DS, "Spring through fall, versatile")
    w(db, "Komatsuna", W, 9, 3, DS, "Fall through early spring")
    w(db, "Chinese Broccoli", C, 5, 7, TP, "Spring through summer")
    w(db, "Chinese Broccoli", M, 3, 5, EI, "Spring or fall")
    w(db, "Chinese Broccoli", W, 9, 11, DS, "Fall/winter crop, prefers cool")
    w(db, "Chinese Broccoli", H, 10, 2, DS, "Winter crop")
    w(db, "Broccoli Rabe", C, 4, 6, DS, "Spring or fall sowing, fast crop")
    w(db, "Broccoli Rabe", M, 3, 5, DS, "Spring or fall, bolts in heat")
    w(db, "Broccoli Rabe", W, 9, 11, DS, "Fall/winter crop")
    w(db, "Romanesco", C, 5, 6, TP, "Start indoors, transplant, needs long cool season")
    w(db, "Romanesco", M, 7, 8, TP, "Midsummer transplant for fall harvest")
    w(db, "Romanesco", W, 8, 10, TP, "Late summer transplant for winter")

    // ── Cucurbits & Gourds ──
    w(db, "Bitter Melon", W, 4, 6, TP, "After frost, needs long hot season")
    w(db, "Bitter Melon", H, 3, 7, DS, "Thrives in heat and humidity")
    w(db, "Luffa", M, 5, 6, TP, "Start indoors, needs 150+ frost-free days")
    w(db, "Luffa", W, 3, 6, EI, "Long warm season needed for sponge maturity")
    w(db, "Luffa", H, 2, 7, DS, "Thrives in tropical heat")
    w(db, "Chayote", M, 5, 6, TP, "Start indoors, plant whole fruit")
    w(db, "Chayote", W, 3, 5, DS, "Plant sprouted fruit, perennial in warm zones")
    w(db, "Chayote", H, 2, 5, DS, "Year-round in frost-free areas")
    w(db, "Calabash", C, 5, 6, TP, "Start indoors, transplant after frost")
    w(db, "Calabash", M, 4, 6, EI, "Spring planting, needs warm season")
    w(db, "Calabash", W, 3, 7, DS, "Long season for mature gourds")
    w(db, "Calabash", H, 2, 8, DS, "Very long season, full maturity for crafts")
    w(db, "Kabocha Squash", C, 5, 6, TP, "Start indoors, transplant after frost")
    w(db, "Kabocha Squash", M, 4, 6, DS, "After frost, needs 90+ warm days")
    w(db, "Kabocha Squash", W, 3, 7, DS, "Spring planting, long growing season")
    w(db, "Delicata Squash", C, 5, 6, TP, "Start indoors or direct sow after frost")
    w(db, "Delicata Squash", M, 4, 6, DS, "After frost, relatively short season for squash")
    w(db, "Delicata Squash", W, 3, 7, DS, "Spring, stores well into winter")
    w(db, "Hubbard Squash", C, 5, 6, TP, "Start indoors, needs long season")
    w(db, "Hubbard Squash", M, 4, 6, DS, "Spring planting, 100+ days to maturity")
    w(db, "Hubbard Squash", W, 3, 6, DS, "Early spring, harvest before first frost")
    w(db, "Patty Pan Squash", C, 5, 7, DS, "After frost, fast summer squash")
    w(db, "Patty Pan Squash", M, 4, 7, DS, "Spring through summer, successive plantings")
    w(db, "Patty Pan Squash", W, 3, 9, DS, "Very long season, prolific")
    w(db, "Patty Pan Squash", H, 2, 10, DS, "Nearly year-round")
    w(db, "Snake Gourd", W, 4, 6, TP, "After frost, needs trellis and heat")
    w(db, "Snake Gourd", H, 3, 7, DS, "Tropical, needs long hot season")

    // ── Warm Season Vegetables ──
    w(db, "Tomatillo", C, 5, 6, TP, "Start indoors, transplant after frost")
    w(db, "Tomatillo", M, 4, 6, EI, "After frost, plant 2+ for pollination")
    w(db, "Tomatillo", W, 3, 7, EI, "Long season, very productive")
    w(db, "Tomatillo", H, 2, 8, DS, "Nearly year-round, self-sows")
    w(db, "Ground Cherry", C, 5, 6, TP, "Start indoors 6-8 weeks early")
    w(db, "Ground Cherry", M, 4, 6, TP, "Start indoors, transplant after frost")
}

private fun seedExpandedWindowsBatch3(db: SupportSQLiteDatabase) {
    seedExpandedWindowsBatch3a(db)
    seedExpandedWindowsBatch3b(db)
}

private fun seedExpandedWindowsBatch3a(db: SupportSQLiteDatabase) {
    // ── Berries 1 ──
    w(db, "Gooseberry", EC, 4, 5, TP, "Plant bare root in early spring")
    w(db, "Gooseberry", C, 3, 5, TP, "Early spring planting, partial shade OK")
    w(db, "Gooseberry", M, 2, 4, TP, "Late winter/early spring, needs some chill")
    w(db, "Currant", EC, 4, 5, TP, "Plant bare root in early spring")
    w(db, "Currant", C, 3, 5, TP, "Early spring, tolerates partial shade")
    w(db, "Currant", M, 2, 4, TP, "Late winter planting")
    w(db, "Elderberry", EC, 4, 5, TP, "Plant in spring after ground thaws")
    w(db, "Elderberry", C, 3, 5, TP, "Early spring, needs 2 varieties for pollination")
    w(db, "Elderberry", M, 2, 4, TP, "Late winter/spring planting")
    w(db, "Elderberry", W, 1, 3, TP, "Winter planting, adapts to heat")
    w(db, "Mulberry", C, 3, 5, TP, "Spring planting, bare root or container")
    w(db, "Mulberry", M, 2, 4, TP, "Late winter/early spring")
    w(db, "Pawpaw", C, 4, 5, TP, "Spring, needs shade when young, 2 varieties")
    w(db, "Pawpaw", M, 3, 4, TP, "Early spring, plant in partial shade first years")
    w(db, "Pawpaw", W, 2, 3, TP, "Late winter planting, protect from full sun")
    w(db, "American Persimmon", C, 3, 5, TP, "Spring bare root planting")
    w(db, "American Persimmon", M, 2, 4, TP, "Late winter/spring, deep taproot")
    w(db, "American Persimmon", W, 1, 3, TP, "Winter planting, very adaptable")
    w(db, "Hardy Kiwi", EC, 4, 5, TP, "Spring, need male + female plants")
    w(db, "Hardy Kiwi", C, 3, 5, TP, "Early spring planting, strong trellis needed")
    w(db, "Hardy Kiwi", M, 2, 4, TP, "Late winter/spring, vigorous vine")
    w(db, "Honeyberry", EC, 4, 5, TP, "Very early spring, extremely cold hardy")
    w(db, "Honeyberry", C, 3, 4, TP, "Early spring, needs 2 varieties, very early bloom")
    w(db, "Serviceberry", EC, 4, 5, TP, "Spring planting, native shrub/tree")
    w(db, "Serviceberry", C, 3, 5, TP, "Early spring, adaptable native")
    w(db, "Serviceberry", M, 2, 4, TP, "Late winter/spring planting")
    w(db, "Serviceberry", W, 1, 3, TP, "Winter planting, some species heat tolerant")
    w(db, "Goji Berry", C, 4, 6, TP, "Spring planting, full sun, drought tolerant once established")
    w(db, "Goji Berry", M, 3, 5, TP, "Early spring, very adaptable")
    w(db, "Goji Berry", W, 2, 4, TP, "Late winter/spring")
    w(db, "Cranberry", EC, 4, 5, TP, "Spring, needs acidic bog conditions")
    w(db, "Cranberry", C, 4, 5, TP, "Spring, acidic moist soil required")
    w(db, "Lingonberry", EC, 4, 5, TP, "Spring, acidic soil, partial shade OK")
    w(db, "Lingonberry", C, 3, 5, TP, "Early spring, evergreen groundcover")
    w(db, "Lingonberry", M, 2, 4, TP, "Late winter/spring, needs cool conditions")
}

private fun seedExpandedWindowsBatch3b(db: SupportSQLiteDatabase) {
    // ── Berries 2 ──
    w(db, "Boysenberry", M, 2, 4, TP, "Late winter/spring, trellis needed")
    w(db, "Boysenberry", W, 1, 3, TP, "Winter planting, mild climates ideal")
    w(db, "Loganberry", M, 2, 4, TP, "Late winter/spring dormant planting")
    w(db, "Loganberry", W, 1, 3, TP, "Winter planting")
    w(db, "Marionberry", M, 2, 4, TP, "Late winter/spring, needs trellis")
    w(db, "Marionberry", W, 1, 3, TP, "Winter planting, Pacific NW specialty")
    w(db, "Tayberry", C, 3, 5, TP, "Early spring, trailing habit, trellis needed")
    w(db, "Tayberry", M, 2, 4, TP, "Late winter/spring")
    w(db, "Dewberry", C, 3, 5, TP, "Spring, trailing wild blackberry type")
    w(db, "Dewberry", M, 2, 4, TP, "Late winter/spring")
    w(db, "Dewberry", W, 1, 3, TP, "Winter planting, spreading groundcover")
    w(db, "Huckleberry", EC, 4, 5, TP, "Spring, acidic forest soil conditions")
    w(db, "Huckleberry", C, 3, 5, TP, "Early spring, partial shade preferred")
    w(db, "Huckleberry", M, 2, 4, TP, "Late winter/spring, mountain conditions")
    w(db, "Muscadine Grape", M, 3, 5, TP, "Spring, strong trellis, warm summers")
    w(db, "Muscadine Grape", W, 2, 4, TP, "Late winter/spring, Southern specialty")
    w(db, "Muscadine Grape", H, 1, 3, TP, "Winter planting, heat and humidity loving")
    w(db, "Quince", C, 3, 5, TP, "Spring bare root, needs winter chill")
    w(db, "Quince", M, 2, 4, TP, "Late winter/spring")
    w(db, "Quince", W, 1, 3, TP, "Winter planting")
    w(db, "Cape Gooseberry", C, 5, 6, TP, "Start indoors, transplant after frost, annual")
    w(db, "Cape Gooseberry", M, 4, 6, EI, "Spring, self-sows in mild areas")
    w(db, "Cape Gooseberry", W, 3, 7, EI, "Long season, may perennialize")
    w(db, "Cape Gooseberry", H, 2, 9, DS, "Nearly year-round, perennial")
    w(db, "Prickly Pear", C, 5, 6, TP, "Plant pads after frost, drought tolerant")
    w(db, "Prickly Pear", M, 3, 6, TP, "Spring, very drought tolerant once established")
    w(db, "Prickly Pear", W, 2, 9, TP, "Nearly year-round planting")
    w(db, "Prickly Pear", H, 1, 11, TP, "Year-round, native desert plant")
    w(db, "Che Fruit", C, 3, 5, TP, "Spring planting, Osage orange relative")
    w(db, "Che Fruit", M, 2, 4, TP, "Late winter/spring")
    w(db, "Che Fruit", W, 1, 3, TP, "Winter planting")

    // ── Warm Zone Fruits ──
    w(db, "Pomegranate", M, 3, 5, TP, "Spring, needs long hot summer")
    w(db, "Pomegranate", W, 2, 4, TP, "Late winter/spring, ideal climate")
    w(db, "Pomegranate", H, 1, 3, TP, "Winter planting, thrives in dry heat")
    w(db, "Jujube", M, 3, 5, TP, "Spring, tolerates poor soil and drought")
    w(db, "Jujube", W, 2, 4, TP, "Late winter/spring, very adaptable")
    w(db, "Jujube", H, 1, 3, TP, "Winter planting, loves heat")
    w(db, "Guava", W, 3, 5, TP, "Spring, protect from cold first years")
    w(db, "Guava", H, 2, 6, TP, "Late winter/spring, tropical")
    w(db, "Loquat", W, 2, 4, TP, "Late winter/spring, evergreen fruit tree")
    w(db, "Loquat", H, 1, 3, TP, "Winter planting, fruits in winter/spring")
    w(db, "Dragonfruit", H, 3, 6, TP, "Spring, needs support structure, frost kills")
    w(db, "Banana", W, 4, 6, TP, "After frost danger, protect in winter")
    w(db, "Banana", H, 3, 7, TP, "Spring/summer, year-round production once established")

    // ── Citrus ──
    w(db, "Lemon", W, 3, 5, TP, "Spring planting, protect from frost")
    w(db, "Lemon", H, 2, 6, TP, "Late winter/spring, everbearing once established")
    w(db, "Lime", W, 3, 5, TP, "Spring, most cold-sensitive citrus")
    w(db, "Lime", H, 2, 6, TP, "Late winter/spring, needs frost-free")
    w(db, "Orange", W, 3, 5, TP, "Spring planting, needs winter protection zone 8-9")
    w(db, "Orange", H, 2, 6, TP, "Late winter/spring, ideal citrus climate")
    w(db, "Grapefruit", W, 3, 5, TP, "Spring, needs most heat of any citrus")
    w(db, "Grapefruit", H, 2, 6, TP, "Late winter/spring, needs long hot summers")
    w(db, "Kumquat", W, 3, 5, TP, "Spring, most cold-hardy citrus")
    w(db, "Kumquat", H, 2, 6, TP, "Late winter/spring")
    w(db, "Meyer Lemon", W, 3, 5, TP, "Spring, hardier than true lemons")
    w(db, "Meyer Lemon", H, 2, 6, TP, "Late winter/spring, great container citrus")
    w(db, "Satsuma", W, 3, 5, TP, "Spring, most cold-hardy mandarin")
    w(db, "Satsuma", H, 2, 6, TP, "Late winter/spring, early ripening")
}

private fun seedExpandedWindowsBatch4(db: SupportSQLiteDatabase) {
    seedExpandedWindowsBatch4a(db)
    seedExpandedWindowsBatch4b(db)
    seedExpandedWindowsBatch4c(db)
}

private fun seedExpandedWindowsBatch4a(db: SupportSQLiteDatabase) {
    // ── Herbs 1 ──
    w(db, "Lemon Balm", C, 4, 6, EI, "Spring sow or transplant, spreads freely")
    w(db, "Lemon Balm", M, 3, 5, EI, "Early spring, perennial, self-sows")
    w(db, "Lemon Balm", W, 2, 4, EI, "Late winter/spring")
    w(db, "Chamomile", C, 4, 6, DS, "After frost, self-sows for annual type")
    w(db, "Chamomile", M, 3, 5, DS, "Spring or fall sowing")
    w(db, "Chamomile", W, 9, 2, DS, "Fall through winter, cool season")
    w(db, "Stevia", W, 3, 5, TP, "After frost, tropical perennial")
    w(db, "Stevia", H, 2, 6, TP, "Late winter/spring, year-round in frost-free")
    w(db, "Catnip", C, 4, 6, EI, "Spring, very hardy perennial")
    w(db, "Catnip", M, 3, 5, EI, "Early spring, spreads readily")
    w(db, "Catnip", W, 2, 4, EI, "Late winter/spring")
    w(db, "Hyssop", C, 4, 6, EI, "Spring, perennial, pollinator plant")
    w(db, "Hyssop", M, 3, 5, EI, "Early spring, drought tolerant")
    w(db, "Hyssop", W, 2, 4, EI, "Late winter/spring")
    w(db, "Borage", C, 5, 7, DS, "After frost, self-sows prolifically")
    w(db, "Borage", M, 3, 6, DS, "Spring/summer, attracts pollinators")
    w(db, "Borage", W, 2, 5, DS, "Late winter through spring")
    w(db, "Borage", H, 10, 3, DS, "Fall through early spring")
    w(db, "Comfrey", EC, 4, 5, TP, "Spring root cuttings, extremely hardy")
    w(db, "Comfrey", C, 3, 6, TP, "Early spring, plant root divisions")
    w(db, "Comfrey", M, 2, 5, TP, "Late winter through spring")
    w(db, "Comfrey", W, 1, 4, TP, "Winter/spring planting")
    w(db, "Feverfew", C, 5, 6, TP, "Start indoors, transplant after frost")
    w(db, "Feverfew", M, 3, 5, EI, "Spring, self-sows once established")
    w(db, "Feverfew", W, 2, 4, EI, "Late winter/spring")
    w(db, "Valerian", C, 4, 6, TP, "Spring transplant, harvest root year 2")
    w(db, "Valerian", M, 3, 5, TP, "Early spring, moist partial shade")
    w(db, "Valerian", W, 2, 4, TP, "Late winter/spring")
    w(db, "Echinacea", EC, 5, 6, DS, "Spring sow, needs cold stratification")
    w(db, "Echinacea", C, 4, 6, DS, "Spring, native prairie plant")
    w(db, "Echinacea", M, 3, 5, EI, "Early spring, drought tolerant perennial")
    w(db, "Echinacea", W, 2, 4, EI, "Late winter/spring")
    w(db, "Bee Balm", C, 4, 6, TP, "Spring, perennial, hummingbird magnet")
    w(db, "Bee Balm", M, 3, 5, TP, "Early spring transplant")
    w(db, "Bee Balm", W, 2, 4, TP, "Late winter/spring")
    w(db, "Anise", C, 5, 6, DS, "After frost, needs long warm season for seed")
    w(db, "Anise", M, 3, 5, DS, "Spring, full sun, well-drained soil")
    w(db, "Anise", W, 2, 4, DS, "Late winter/spring")
    w(db, "Caraway", C, 4, 6, DS, "Spring or fall sow, biennial, seeds year 2")
    w(db, "Caraway", M, 3, 5, DS, "Early spring or fall")
    w(db, "Caraway", W, 9, 11, DS, "Fall sowing, overwinters")
    w(db, "Herb Fennel", C, 5, 6, DS, "After frost, perennial herb form")
    w(db, "Herb Fennel", M, 3, 5, DS, "Spring, self-sows")
    w(db, "Herb Fennel", W, 2, 4, DS, "Late winter/spring")
    w(db, "Epazote", M, 4, 6, DS, "After frost, self-sows aggressively")
    w(db, "Epazote", W, 3, 7, DS, "Spring through summer")
    w(db, "Epazote", H, 2, 9, DS, "Nearly year-round")
    w(db, "Shiso", C, 5, 6, TP, "Start indoors, transplant after frost")
    w(db, "Shiso", M, 4, 6, EI, "Spring, self-sows freely")
    w(db, "Shiso", W, 3, 7, DS, "Spring through summer")
    w(db, "Shiso", H, 2, 9, DS, "Nearly year-round")
    w(db, "Summer Savory", EC, 5, 6, DS, "After last frost, annual")
    w(db, "Summer Savory", C, 5, 7, DS, "After frost, warm soil needed")
    w(db, "Summer Savory", M, 4, 7, DS, "Spring through summer")
    w(db, "Summer Savory", W, 3, 9, DS, "Long season")
    w(db, "Summer Savory", H, 2, 10, DS, "Nearly year-round")
    w(db, "Winter Savory", C, 4, 6, EI, "Spring, perennial subshrub")
    w(db, "Winter Savory", M, 3, 5, EI, "Early spring, drought tolerant")
    w(db, "Winter Savory", W, 2, 4, EI, "Late winter/spring")
    w(db, "French Tarragon", C, 4, 6, TP, "Spring divisions only, does not set seed")
    w(db, "French Tarragon", M, 3, 5, TP, "Early spring, propagate by division")
    w(db, "French Tarragon", W, 2, 4, TP, "Late winter/spring")
    w(db, "Marjoram", W, 3, 5, TP, "Spring, tender perennial, frost sensitive")
    w(db, "Marjoram", H, 2, 6, EI, "Late winter/spring, year-round in frost-free")
}

private fun seedExpandedWindowsBatch4b(db: SupportSQLiteDatabase) {
    // ── Herbs 2 ──
    w(db, "Lovage", EC, 4, 5, TP, "Spring, very cold hardy perennial")
    w(db, "Lovage", C, 3, 5, EI, "Early spring, celery-like flavor")
    w(db, "Lovage", M, 2, 4, EI, "Late winter/spring")
    w(db, "Angelica", C, 4, 6, DS, "Spring or fall sow, biennial, needs fresh seed")
    w(db, "Angelica", M, 3, 5, DS, "Early spring, partial shade preferred")
    w(db, "Angelica", W, 9, 11, DS, "Fall sow for spring growth")
    w(db, "Rue", C, 4, 6, EI, "Spring, drought tolerant perennial")
    w(db, "Rue", M, 3, 5, EI, "Early spring")
    w(db, "Rue", W, 2, 4, EI, "Late winter/spring")
    w(db, "Sweet Woodruff", C, 4, 5, TP, "Spring, shade-loving groundcover")
    w(db, "Sweet Woodruff", M, 3, 4, TP, "Early spring, needs shade and moisture")
    w(db, "Salad Burnet", C, 4, 6, DS, "Spring, evergreen in mild winters")
    w(db, "Salad Burnet", M, 3, 5, DS, "Early spring or fall")
    w(db, "Sweet Cicely", EC, 4, 5, DS, "Spring, needs cold stratification")
    w(db, "Sweet Cicely", C, 3, 5, DS, "Early spring, shade tolerant")
    w(db, "Chervil", C, 4, 5, DS, "Early spring, bolts in heat")
    w(db, "Chervil", M, 3, 4, DS, "Late winter/early spring, or fall sow")
    w(db, "Chervil", W, 9, 11, DS, "Fall/winter crop, cool season")
    w(db, "Curry Leaf", W, 4, 6, TP, "After frost, tropical tree")
    w(db, "Curry Leaf", H, 3, 7, TP, "Spring, year-round in frost-free")
    w(db, "Lemon Verbena", W, 4, 6, TP, "After frost, deciduous in cool areas")
    w(db, "Lemon Verbena", H, 3, 7, TP, "Spring, evergreen in frost-free zones")
    w(db, "Pineapple Sage", W, 4, 6, TP, "After frost, blooms in fall short days")
    w(db, "Pineapple Sage", H, 3, 7, TP, "Spring, perennial in frost-free")
    w(db, "Mexican Oregano", W, 4, 6, TP, "After frost, drought tolerant shrub")
    w(db, "Mexican Oregano", H, 3, 7, TP, "Spring, native to arid regions")
    w(db, "Cuban Oregano", W, 4, 6, TP, "After frost, succulent herb")
    w(db, "Cuban Oregano", H, 3, 8, TP, "Spring/summer, frost-free zones")
    w(db, "Vietnamese Coriander", W, 4, 6, TP, "After frost, needs moisture")
    w(db, "Vietnamese Coriander", H, 3, 8, TP, "Spring, thrives in heat and humidity")
    w(db, "Culantro", W, 4, 6, TP, "After frost, partial shade, tropical")
    w(db, "Culantro", H, 3, 8, TP, "Spring, biennial in frost-free")
    w(db, "Thai Basil", C, 5, 6, TP, "Start indoors, transplant after frost")
    w(db, "Thai Basil", M, 4, 7, EI, "After frost, heat-loving annual")
    w(db, "Thai Basil", W, 3, 9, EI, "Spring through fall")
    w(db, "Thai Basil", H, 2, 10, DS, "Nearly year-round, may perennialize")
    w(db, "Horehound", C, 4, 6, EI, "Spring, very drought tolerant")
    w(db, "Horehound", M, 3, 5, EI, "Early spring, spreads by seed")
    w(db, "Horehound", W, 2, 4, EI, "Late winter/spring")
    w(db, "Wormwood", C, 4, 6, EI, "Spring, drought tolerant perennial")
    w(db, "Wormwood", M, 3, 5, EI, "Early spring")
    w(db, "Wormwood", W, 2, 4, EI, "Late winter/spring")
    w(db, "Tansy", EC, 4, 5, TP, "Spring, very cold hardy, can be invasive")
    w(db, "Tansy", C, 3, 6, EI, "Early spring, spreads aggressively")
    w(db, "Tansy", M, 2, 5, EI, "Late winter/spring")
    w(db, "Pennyroyal", C, 4, 6, TP, "Spring, creeping mint relative")
    w(db, "Pennyroyal", M, 3, 5, TP, "Early spring, groundcover")
    w(db, "Pennyroyal", W, 2, 4, TP, "Late winter/spring")
    w(db, "Betony", C, 4, 6, EI, "Spring, woodland perennial")
    w(db, "Betony", M, 3, 5, EI, "Early spring, partial shade OK")
    w(db, "Meadowsweet", EC, 4, 5, TP, "Spring, moist meadow conditions")
    w(db, "Meadowsweet", C, 3, 6, TP, "Early spring, needs moisture")
    w(db, "Meadowsweet", M, 2, 5, TP, "Late winter/spring")
    w(db, "Costmary", C, 4, 6, TP, "Spring divisions, perennial")
    w(db, "Costmary", M, 3, 5, TP, "Early spring")
    w(db, "Soapwort", EC, 4, 5, EI, "Spring, very hardy, can spread")
    w(db, "Soapwort", C, 3, 6, EI, "Early spring, easy to grow")
    w(db, "Soapwort", M, 2, 5, EI, "Late winter/spring")
    w(db, "Soapwort", W, 1, 4, EI, "Winter/spring planting")
    w(db, "Elecampane", EC, 4, 5, TP, "Spring, tall medicinal perennial")
    w(db, "Elecampane", C, 3, 5, TP, "Early spring, harvest root year 2-3")
    w(db, "Elecampane", M, 2, 4, TP, "Late winter/spring")
}

private fun seedExpandedWindowsBatch4c(db: SupportSQLiteDatabase) {
    // ── Edible Flowers ──
    w(db, "Nasturtium", EC, 5, 6, DS, "After last frost, annual, easy")
    w(db, "Nasturtium", C, 5, 7, DS, "After frost, poor soil is fine")
    w(db, "Nasturtium", M, 3, 8, DS, "Spring through summer, self-sows")
    w(db, "Nasturtium", W, 2, 10, DS, "Long season, may go year-round")
    w(db, "Nasturtium", H, 10, 4, DS, "Fall through spring, heat dormancy")
    w(db, "Calendula", EC, 5, 6, DS, "After last frost, cool season annual")
    w(db, "Calendula", C, 4, 6, DS, "Spring, self-sows, likes cool weather")
    w(db, "Calendula", M, 3, 5, DS, "Early spring or fall")
    w(db, "Calendula", W, 9, 3, DS, "Fall through spring, winter flower")
    w(db, "Calendula", H, 10, 2, DS, "Cool season only, winter bloom")
    w(db, "Viola", C, 4, 6, TP, "Spring transplant, cold hardy")
    w(db, "Viola", M, 3, 5, EI, "Early spring or fall")
    w(db, "Viola", W, 9, 3, EI, "Fall through spring, winter color")
    w(db, "Viola", H, 10, 2, TP, "Cool season bedding plant")
    w(db, "Sunflower", EC, 5, 6, DS, "After last frost, direct sow only")
    w(db, "Sunflower", C, 5, 7, DS, "After frost, full sun")
    w(db, "Sunflower", M, 3, 7, DS, "Spring through early summer")
    w(db, "Sunflower", W, 2, 8, DS, "Late winter through summer")
    w(db, "Sunflower", H, 2, 9, DS, "Long season, multiple successions")
    w(db, "Signet Marigold", EC, 5, 6, TP, "Start indoors, transplant after frost")
    w(db, "Signet Marigold", C, 5, 7, EI, "After frost, continuous bloom")
    w(db, "Signet Marigold", M, 3, 8, DS, "Spring through summer")
    w(db, "Signet Marigold", W, 2, 10, DS, "Long season, nearly year-round")
    w(db, "Signet Marigold", H, 1, 11, DS, "Year-round in frost-free")
    w(db, "Hibiscus (Roselle)", C, 5, 6, TP, "Start indoors early, needs 150+ days")
    w(db, "Hibiscus (Roselle)", M, 4, 6, TP, "Start indoors, transplant after frost")
    w(db, "Hibiscus (Roselle)", W, 3, 6, EI, "Long warm season for calyx harvest")
    w(db, "Hibiscus (Roselle)", H, 2, 7, DS, "Thrives in tropical heat")
    w(db, "Rose (Rugosa)", EC, 4, 5, TP, "Spring, extremely cold hardy")
    w(db, "Rose (Rugosa)", C, 3, 5, TP, "Early spring, disease resistant")
    w(db, "Rose (Rugosa)", M, 2, 4, TP, "Late winter/spring")
    w(db, "Rose (Rugosa)", W, 1, 3, TP, "Winter planting")
    w(db, "Dianthus", C, 4, 6, TP, "Spring, perennial, needs good drainage")
    w(db, "Dianthus", M, 3, 5, EI, "Early spring or fall")
    w(db, "Dianthus", W, 9, 3, EI, "Fall through spring")
    w(db, "Edible Chrysanthemum", C, 4, 6, DS, "Spring or fall, cool season green")
    w(db, "Edible Chrysanthemum", M, 3, 5, DS, "Early spring or fall successions")
    w(db, "Edible Chrysanthemum", W, 9, 2, DS, "Fall through winter")
    w(db, "Edible Chrysanthemum", H, 10, 2, DS, "Cool season only")
    w(db, "Daylily", EC, 4, 5, TP, "Spring, extremely cold hardy perennial")
    w(db, "Daylily", C, 3, 6, TP, "Early spring or fall divisions")
    w(db, "Daylily", M, 2, 5, TP, "Late winter through spring")
    w(db, "Daylily", W, 1, 4, TP, "Winter/spring, nearly evergreen")
}
