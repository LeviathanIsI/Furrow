package com.furrow.app.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

object PlantDatabaseSeeder : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        seedAllReferenceTables(db)
    }

    /** Called from both onCreate (fresh install) and Migration (upgrade). */
    fun seedAllReferenceTables(db: SupportSQLiteDatabase) {
        seedPlantInfo(db)
        seedPlantVarieties(db)
        seedPlantingWindows(db)
        seedExpandedPlants(db)
        PlantGrowingDataSeeder.seedGrowingData(db)
        seedBreedInfo(db)
        AnimalBreedInfoSeeder.seedBreeds(db)
        seedBeeRaceInfo(db)
        val naCatalogStats = NACatalogImporter.seed(db)
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

    // ═══════════════════════════════════════════════════════════════
    //  PLANT INFO — 48 plants from research
    // ═══════════════════════════════════════════════════════════════

    private fun seedPlantInfo(db: SupportSQLiteDatabase) {

        // ── Warm-Season Vegetables ──

        p(db, "Tomato (determinate)", "vegetable", 3, 11, 60, 80,
            "Full", "Regular", true, 5,
            "Basil,Carrot,Parsley", "Broccoli,Cauliflower,Cabbage,Kale,Brussels Sprouts",
            "Bush type, good for containers and short seasons")

        p(db, "Tomato (indeterminate)", "vegetable", 4, 11, 70, 90,
            "Full", "Regular", true, 10,
            "Basil,Carrot,Parsley", "Broccoli,Cauliflower,Cabbage,Kale,Brussels Sprouts",
            "Vining type, needs stake or cage, longer harvest")

        p(db, "Pepper (sweet)", "vegetable", 4, 11, 65, 80,
            "Full", "Regular", true, 5,
            "Basil,Carrot,Onion,Spinach,Tomato", "Kohlrabi")

        p(db, "Pepper (hot)", "vegetable", 4, 11, 70, 90,
            "Full", "Moderate", true, 5,
            "Basil,Carrot,Onion,Spinach,Tomato", "Kohlrabi")

        p(db, "Cucumber", "vegetable", 3, 11, 55, 65,
            "Full", "Regular", true, 10,
            "Bush Beans,Pole Beans,Corn,Peas,Radish", "Potato")

        p(db, "Zucchini", "vegetable", 3, 11, 40, 55,
            "Full", "Regular", true, 10,
            "Bush Beans,Pole Beans,Corn,Peas,Radish", "Potato",
            "Also known as summer squash")

        p(db, "Winter Squash", "vegetable", 3, 10, 85, 100,
            "Full", "Regular", false, 15,
            "Bush Beans,Pole Beans,Corn,Peas,Radish", "Potato",
            "Includes butternut, acorn, spaghetti squash. Marginal in containers (15 gal min)")

        p(db, "Pumpkin", "vegetable", 3, 10, 100, 120,
            "Full", "Regular", false, 0,
            "Bush Beans,Pole Beans,Corn,Peas,Radish", "Potato",
            "Needs lots of space, not suited for containers")

        p(db, "Watermelon", "vegetable", 4, 11, 80, 90,
            "Full", "Regular", false, 15,
            "Corn,Radish", "Potato",
            "Marginal in containers (15 gal min). Short-season varieties for cold zones")

        p(db, "Cantaloupe", "vegetable", 4, 10, 70, 80,
            "Full", "Regular", false, 10,
            "Corn,Radish", "Potato",
            "Also known as muskmelon. Marginal in containers (10 gal min)")

        p(db, "Eggplant", "vegetable", 5, 11, 75, 90,
            "Full", "Regular", true, 5,
            "Basil,Pepper,Bush Beans", "")

        p(db, "Okra", "vegetable", 5, 11, 50, 65,
            "Full", "Low", true, 10,
            "", "",
            "Heat-loving crop, thrives in southern summers")

        p(db, "Sweet Potato", "vegetable", 5, 11, 90, 150,
            "Full", "Moderate", false, 15,
            "", "",
            "Planted from slips. Marginal in containers (15 gal min). Needs long warm season")

        p(db, "Corn", "vegetable", 3, 10, 60, 90,
            "Full", "Regular", false, 15,
            "Bush Beans,Pole Beans,Zucchini,Cucumber,Peas", "Tomato",
            "Marginal in containers. Plant in blocks of 3+ for pollination")

        p(db, "Bush Beans", "vegetable", 3, 11, 50, 60,
            "Full", "Regular", true, 5,
            "Corn,Zucchini,Cucumber,Carrot,Beet", "Onion,Garlic,Chives")

        p(db, "Pole Beans", "vegetable", 3, 11, 60, 70,
            "Full", "Regular", true, 7,
            "Corn,Zucchini,Cucumber,Carrot,Beet", "Onion,Garlic,Chives",
            "Needs trellis or pole support")

        p(db, "Lima Beans", "vegetable", 5, 11, 75, 85,
            "Full", "Regular", true, 5,
            "Corn,Zucchini,Cucumber,Carrot,Beet", "Onion,Garlic,Chives")

        p(db, "Southern Peas", "vegetable", 7, 11, 60, 70,
            "Full", "Low", true, 5,
            "Corn,Zucchini,Cucumber", "Onion,Garlic",
            "Also called cowpeas or black-eyed peas. Heat-loving")

        // ── Cool-Season Vegetables ──

        p(db, "Lettuce (leaf)", "vegetable", 3, 11, 40, 60,
            "Partial-Full", "Regular", true, 3,
            "Carrot,Radish,Chives,Mint", "")

        p(db, "Lettuce (head)", "vegetable", 3, 10, 60, 85,
            "Partial-Full", "Regular", true, 5,
            "Carrot,Radish,Chives,Mint", "",
            "More space needed than leaf lettuce, bolts faster in heat")

        p(db, "Spinach", "vegetable", 3, 10, 40, 50,
            "Partial-Full", "Regular", true, 3,
            "Radish,Peas,Bush Beans", "",
            "Bolts quickly in heat, best as spring/fall crop")

        p(db, "Kale", "vegetable", 3, 11, 55, 75,
            "Full", "Regular", true, 5,
            "Onion,Garlic,Beet,Sage,Rosemary", "Tomato,Pole Beans",
            "Very cold-hardy, flavor improves after frost")

        p(db, "Swiss Chard", "vegetable", 3, 11, 55, 65,
            "Partial-Full", "Regular", true, 5,
            "Onion,Garlic,Beet", "",
            "Heat-tolerant for a cool-season green")

        p(db, "Collard Greens", "vegetable", 5, 11, 60, 80,
            "Full", "Regular", true, 5,
            "Onion,Garlic,Beet,Sage,Rosemary", "Tomato,Pole Beans",
            "Very heat-tolerant. Southern staple")

        p(db, "Mustard Greens", "vegetable", 3, 11, 40, 50,
            "Full", "Regular", true, 3,
            "Onion", "Pole Beans",
            "Fast-growing, spicy greens")

        p(db, "Broccoli", "vegetable", 3, 10, 60, 80,
            "Full", "Regular", true, 5,
            "Onion,Garlic,Beet,Sage,Rosemary", "Tomato,Pole Beans")

        p(db, "Cauliflower", "vegetable", 3, 9, 55, 80,
            "Full", "Regular", true, 5,
            "Onion,Garlic,Beet,Sage,Rosemary", "Tomato,Pole Beans",
            "Finicky — needs consistent cool temps")

        p(db, "Cabbage", "vegetable", 3, 10, 65, 80,
            "Full", "Regular", true, 5,
            "Onion,Garlic,Beet,Sage,Rosemary", "Tomato,Pole Beans")

        p(db, "Brussels Sprouts", "vegetable", 3, 8, 90, 110,
            "Full", "Regular", false, 7,
            "Onion,Garlic,Beet,Sage,Rosemary", "Tomato,Pole Beans",
            "Marginal in containers (7 gal min). Needs long cool season")

        p(db, "Peas", "vegetable", 3, 9, 58, 72,
            "Full", "Regular", true, 5,
            "Carrot,Turnip,Radish,Cucumber,Corn", "Onion,Garlic",
            "Includes snap and snow peas. Plant early, heat ends production")

        p(db, "Radish", "vegetable", 3, 11, 25, 30,
            "Full-Partial", "Regular", true, 2,
            "Lettuce,Peas,Bush Beans,Cucumber,Spinach", "",
            "Fastest vegetable from seed to harvest")

        p(db, "Carrot", "vegetable", 3, 10, 70, 80,
            "Full", "Regular", true, 7,
            "Tomato,Onion,Rosemary,Sage", "Dill",
            "Needs deep container (7 gal min). Loose soil essential")

        p(db, "Beet", "vegetable", 3, 10, 55, 65,
            "Full", "Regular", true, 5,
            "Onion,Cabbage,Lettuce,Garlic", "Pole Beans,Mustard Greens")

        p(db, "Turnip", "vegetable", 3, 10, 40, 60,
            "Full", "Regular", true, 5,
            "Peas,Onion", "",
            "Both roots and greens are edible")

        p(db, "Onion", "vegetable", 3, 10, 90, 120,
            "Full", "Moderate", true, 5,
            "Carrot,Beet,Lettuce,Tomato,Cabbage", "Bush Beans,Pole Beans,Lima Beans,Peas",
            "Day-length sensitive — use short-day varieties in South, long-day in North")

        p(db, "Garlic", "vegetable", 3, 10, 90, 120,
            "Full", "Moderate", true, 5,
            "Carrot,Beet,Lettuce,Tomato,Cabbage", "Bush Beans,Pole Beans,Lima Beans,Peas",
            "Plant in fall for summer harvest. Hardneck for cold zones, softneck for warm")

        p(db, "Potato", "vegetable", 3, 10, 70, 120,
            "Full", "Regular", true, 10,
            "Bush Beans,Corn,Cabbage", "Tomato,Zucchini,Cucumber",
            "Hill soil as plants grow. 10 gal grow bags work well")

        p(db, "Kohlrabi", "vegetable", 3, 9, 50, 70,
            "Full", "Regular", true, 3,
            "Onion,Garlic,Beet", "Tomato,Pole Beans,Pepper",
            "Underrated vegetable, tastes like mild broccoli stem")

        // ── Herbs ──

        p(db, "Basil", "herb", 4, 11, 50, 75,
            "Full", "Regular", true, 1,
            "Tomato,Pepper", "Sage",
            "Pinch flowers for bushier growth. Many varieties available")

        p(db, "Cilantro", "herb", 3, 10, 45, 70,
            "Partial", "Regular", true, 1,
            "", "",
            "Bolts fast in heat. Succession plant every 2-3 weeks")

        p(db, "Dill", "herb", 3, 10, 40, 60,
            "Full", "Moderate", true, 3,
            "", "Carrot",
            "Self-seeds readily. Attracts beneficial insects")

        p(db, "Parsley", "herb", 3, 11, 70, 90,
            "Partial-Full", "Regular", true, 1,
            "Tomato", "",
            "Biennial — produces for two years. Slow to germinate")

        p(db, "Oregano", "herb", 4, 10, 80, 90,
            "Full", "Low", true, 1,
            "", "",
            "Perennial in zones 5+. Drought-tolerant once established")

        p(db, "Thyme", "herb", 4, 10, 85, 95,
            "Full", "Low", true, 1,
            "", "",
            "Perennial in zones 5+. Excellent ground cover")

        p(db, "Rosemary", "herb", 7, 11, 80, 100,
            "Full", "Low", true, 2,
            "Cabbage,Broccoli,Carrot", "",
            "Perennial in zones 7+. Bring indoors in cold climates")

        p(db, "Sage", "herb", 4, 10, 75, 85,
            "Full", "Low", true, 2,
            "Cabbage,Broccoli,Carrot", "Basil",
            "Perennial in zones 5+. Repels cabbage moth")

        p(db, "Mint", "herb", 3, 11, 60, 90,
            "Partial", "Regular", true, 2,
            "Lettuce,Cabbage", "",
            "Extremely invasive — always grow in containers. Perennial")

        p(db, "Chives", "herb", 3, 10, 60, 90,
            "Full-Partial", "Moderate", true, 1,
            "Carrot,Tomato", "",
            "Perennial. Edible flowers. Repels aphids")

        // ── Expanded Plants ──

        p(db, "Cherry Tomato", "vegetable", 3, 11, 55, 70,
            "Full", "Regular", true, 5,
            "Basil,Carrot,Parsley", "Broccoli,Cauliflower,Cabbage",
            "Prolific producer, great for containers and snacking")

        p(db, "Roma Tomato", "vegetable", 4, 11, 70, 80,
            "Full", "Regular", true, 5,
            "Basil,Carrot,Parsley", "Broccoli,Cauliflower,Cabbage",
            "Paste-type, meaty flesh, great for sauces and canning")

        p(db, "Jalapeno", "vegetable", 4, 11, 65, 80,
            "Full", "Moderate", true, 3,
            "Basil,Carrot,Onion,Tomato", "Kohlrabi",
            "Medium heat pepper (2500-8000 SHU), versatile in cooking")

        p(db, "Habanero", "vegetable", 5, 11, 90, 120,
            "Full", "Moderate", true, 3,
            "Basil,Carrot,Onion,Tomato", "Kohlrabi",
            "Very hot pepper (100k-350k SHU), needs long warm season")

        p(db, "Cayenne", "vegetable", 4, 11, 70, 85,
            "Full", "Moderate", true, 3,
            "Basil,Carrot,Onion,Tomato", "Kohlrabi",
            "Hot pepper (30k-50k SHU), great for drying")

        p(db, "Banana Pepper", "vegetable", 4, 11, 60, 75,
            "Full", "Regular", true, 3,
            "Basil,Carrot,Onion,Tomato", "Kohlrabi",
            "Mild sweet pepper, great for pickling and salads")

        p(db, "Pickling Cucumber", "vegetable", 3, 11, 50, 60,
            "Full", "Regular", true, 10,
            "Bush Beans,Pole Beans,Corn,Peas,Radish", "Potato",
            "Compact, prolific, bred specifically for pickling")

        p(db, "Yellow Squash", "vegetable", 3, 11, 40, 55,
            "Full", "Regular", true, 10,
            "Bush Beans,Pole Beans,Corn,Peas,Radish", "Potato",
            "Crookneck or straightneck summer squash, very productive")

        p(db, "Butternut Squash", "vegetable", 3, 10, 85, 100,
            "Full", "Regular", false, 15,
            "Bush Beans,Pole Beans,Corn", "Potato",
            "Sweet winter squash, stores well for months")

        p(db, "Acorn Squash", "vegetable", 3, 10, 80, 100,
            "Full", "Regular", false, 15,
            "Bush Beans,Pole Beans,Corn", "Potato",
            "Small single-serving winter squash, stores well")

        p(db, "Spaghetti Squash", "vegetable", 3, 10, 90, 100,
            "Full", "Regular", false, 15,
            "Bush Beans,Pole Beans,Corn", "Potato",
            "Unique stringy flesh resembles pasta when cooked")

        p(db, "Honeydew", "vegetable", 4, 11, 80, 100,
            "Full", "Regular", false, 15,
            "Corn,Radish", "Potato",
            "Sweet green-fleshed melon, needs long warm season")

        p(db, "Snap Peas", "vegetable", 3, 9, 55, 70,
            "Full", "Regular", true, 5,
            "Carrot,Turnip,Radish,Cucumber,Corn", "Onion,Garlic",
            "Edible pods, sweet flavor, eat whole")

        p(db, "Snow Peas", "vegetable", 3, 9, 55, 65,
            "Full", "Regular", true, 5,
            "Carrot,Turnip,Radish,Cucumber,Corn", "Onion,Garlic",
            "Flat edible pods, popular in stir-fry")

        p(db, "Romaine Lettuce", "vegetable", 3, 10, 60, 75,
            "Partial-Full", "Regular", true, 5,
            "Carrot,Radish,Chives,Mint", "",
            "Upright head lettuce, heat tolerant, crisp texture")

        p(db, "Butter Lettuce", "vegetable", 3, 10, 55, 70,
            "Partial-Full", "Regular", true, 3,
            "Carrot,Radish,Chives,Mint", "",
            "Soft, tender leaves, forms loose heads")

        p(db, "Arugula", "vegetable", 3, 11, 25, 40,
            "Partial-Full", "Regular", true, 2,
            "Carrot,Lettuce", "",
            "Peppery salad green, very fast growing, bolts in heat")

        p(db, "Green Onion", "vegetable", 3, 11, 50, 65,
            "Full", "Regular", true, 2,
            "Carrot,Beet,Lettuce,Tomato", "Bush Beans,Pole Beans,Peas",
            "Also called scallions, harvest young for mild flavor")

        p(db, "Shallot", "vegetable", 3, 10, 90, 120,
            "Full", "Moderate", true, 5,
            "Carrot,Beet,Lettuce,Tomato", "Bush Beans,Pole Beans,Peas",
            "Mild, sweet onion flavor, multiplies from single bulb")

        p(db, "Leek", "vegetable", 3, 10, 100, 150,
            "Full", "Regular", true, 5,
            "Carrot,Celery", "Bush Beans,Pole Beans,Peas",
            "Mild onion family, long growing season, cold hardy")

        p(db, "Celery", "vegetable", 3, 10, 120, 140,
            "Full", "Regular", true, 5,
            "Leek,Tomato,Bush Beans", "",
            "Needs consistent moisture, long cool growing season")

        p(db, "Asparagus", "vegetable", 3, 8, 730, 1095,
            "Full", "Regular", false, 0,
            "Tomato,Parsley,Basil", "",
            "Perennial, produces for 15+ years after 2-3 year establishment")

        p(db, "Artichoke", "vegetable", 7, 11, 120, 180,
            "Full", "Regular", true, 15,
            "Peas,Cabbage", "",
            "Perennial in zones 7+, grow as annual in colder zones")

        p(db, "Lemongrass", "herb", 8, 11, 90, 120,
            "Full", "Regular", true, 5,
            "", "",
            "Tropical herb, grow as annual in cold zones, aromatic")

        p(db, "Lavender", "herb", 5, 9, 90, 200,
            "Full", "Low", true, 3,
            "Rosemary,Sage,Thyme", "",
            "Perennial in zones 5+, drought tolerant, fragrant")

        p(db, "Strawberry", "fruit", 3, 10, 60, 90,
            "Full", "Regular", true, 3,
            "Bush Beans,Lettuce,Spinach,Thyme", "Cabbage,Broccoli",
            "June-bearing or everbearing varieties available")

        p(db, "Blueberry", "fruit", 3, 8, 730, 1095,
            "Full", "Regular", true, 10,
            "", "",
            "Needs acidic soil (pH 4.5-5.5), plant 2+ varieties for pollination")

        p(db, "Raspberry", "fruit", 3, 8, 365, 730,
            "Full", "Regular", false, 0,
            "", "",
            "Spreads by runners, needs trellis, produces on 2nd year canes")

        p(db, "Blackberry", "fruit", 5, 9, 365, 730,
            "Full", "Regular", false, 0,
            "", "",
            "Vigorous grower, thornless varieties available")

        p(db, "Fig", "fruit", 7, 11, 365, 730,
            "Full", "Moderate", true, 15,
            "", "",
            "Can grow in containers in cold zones, bring indoors in winter")

        p(db, "Grape", "fruit", 4, 10, 365, 1095,
            "Full", "Moderate", false, 0,
            "", "",
            "Needs trellis, table and wine varieties available")

        p(db, "Passion Fruit", "fruit", 9, 11, 365, 545,
            "Full", "Regular", false, 0,
            "", "",
            "Tropical vine, needs frost-free climate")

        p(db, "Papaya", "fruit", 9, 11, 270, 365,
            "Full", "Regular", true, 15,
            "", "",
            "Tropical, fast growing, needs consistent warmth")

        p(db, "Mango", "fruit", 10, 11, 730, 1825,
            "Full", "Moderate", false, 0,
            "", "",
            "Large tropical tree, only for frost-free zones")

        p(db, "Iceberg Lettuce", "vegetable", 3, 10, 70, 85,
            "Partial-Full", "Regular", true, 5,
            "Carrot,Radish,Chives,Mint", "",
            "Classic crispy head lettuce, needs cool consistent temps")

        p(db, "Yam", "vegetable", 8, 11, 150, 240,
            "Full", "Regular", false, 0,
            "", "",
            "True yam, tropical crop, needs very long warm season. Not same as sweet potato")
    }

    // ═══════════════════════════════════════════════════════════════
    //  PLANTING WINDOWS — all zone groups, all plants
    //  Spring + Fall windows where applicable
    // ═══════════════════════════════════════════════════════════════

    @Suppress("LongMethod")
    private fun seedPlantingWindows(db: SupportSQLiteDatabase) {

        // ──────────────────────────────────────────
        //  TOMATO (determinate)
        // ──────────────────────────────────────────
        w(db, "Tomato (determinate)", EC, 5, 6, TP, "Short-season varieties recommended")
        w(db, "Tomato (determinate)", C, 5, 6, TP)
        w(db, "Tomato (determinate)", M, 5, 6, TP)
        w(db, "Tomato (determinate)", W, 4, 5, TP)
        w(db, "Tomato (determinate)", H, 2, 4, TP)

        // ──────────────────────────────────────────
        //  TOMATO (indeterminate)
        // ──────────────────────────────────────────
        w(db, "Tomato (indeterminate)", EC, 5, 6, TP, "Short-season varieties recommended")
        w(db, "Tomato (indeterminate)", C, 5, 6, TP)
        w(db, "Tomato (indeterminate)", M, 5, 6, TP)
        w(db, "Tomato (indeterminate)", W, 4, 5, TP)
        w(db, "Tomato (indeterminate)", H, 2, 4, TP)

        // ──────────────────────────────────────────
        //  PEPPER (sweet)
        // ──────────────────────────────────────────
        w(db, "Pepper (sweet)", EC, 5, 6, TP)
        w(db, "Pepper (sweet)", C, 5, 6, TP)
        w(db, "Pepper (sweet)", M, 5, 6, TP)
        w(db, "Pepper (sweet)", W, 4, 5, TP)
        w(db, "Pepper (sweet)", H, 2, 4, TP)

        // ──────────────────────────────────────────
        //  PEPPER (hot)
        // ──────────────────────────────────────────
        w(db, "Pepper (hot)", EC, 5, 6, TP)
        w(db, "Pepper (hot)", C, 5, 6, TP)
        w(db, "Pepper (hot)", M, 5, 6, TP)
        w(db, "Pepper (hot)", W, 4, 5, TP)
        w(db, "Pepper (hot)", H, 2, 4, TP)

        // ──────────────────────────────────────────
        //  CUCUMBER
        // ──────────────────────────────────────────
        w(db, "Cucumber", EC, 6, 6, DS)
        w(db, "Cucumber", C, 6, 6, DS)
        w(db, "Cucumber", M, 5, 6, EI)
        w(db, "Cucumber", W, 4, 5, EI)
        w(db, "Cucumber", H, 3, 4, EI)

        // ──────────────────────────────────────────
        //  ZUCCHINI
        // ──────────────────────────────────────────
        w(db, "Zucchini", EC, 6, 6, DS)
        w(db, "Zucchini", C, 6, 6, DS)
        w(db, "Zucchini", M, 5, 6, EI)
        w(db, "Zucchini", W, 4, 5, EI)
        w(db, "Zucchini", H, 3, 4, EI)

        // ──────────────────────────────────────────
        //  WINTER SQUASH
        // ──────────────────────────────────────────
        w(db, "Winter Squash", EC, 6, 6, DS, "Choose short-season varieties (85 days)")
        w(db, "Winter Squash", C, 6, 6, DS)
        w(db, "Winter Squash", M, 5, 6, EI)
        w(db, "Winter Squash", W, 4, 5, EI)
        w(db, "Winter Squash", H, 2, 3, EI, "Spring planting")
        w(db, "Winter Squash", H, 10, 2, EI, "Fall planting — primary season in hot zones")

        // ──────────────────────────────────────────
        //  PUMPKIN
        // ──────────────────────────────────────────
        w(db, "Pumpkin", EC, 6, 6, DS, "Choose 90-day varieties for short season")
        w(db, "Pumpkin", C, 6, 6, DS)
        w(db, "Pumpkin", M, 5, 6, DS)
        w(db, "Pumpkin", W, 4, 5, DS)
        w(db, "Pumpkin", H, 2, 3, DS)

        // ──────────────────────────────────────────
        //  WATERMELON
        // ──────────────────────────────────────────
        w(db, "Watermelon", EC, 6, 6, TP, "Short-season varieties only")
        w(db, "Watermelon", C, 6, 6, TP)
        w(db, "Watermelon", M, 5, 6, EI)
        w(db, "Watermelon", W, 4, 5, EI)
        w(db, "Watermelon", H, 3, 4, EI)

        // ──────────────────────────────────────────
        //  CANTALOUPE
        // ──────────────────────────────────────────
        w(db, "Cantaloupe", EC, 6, 6, TP, "Short-season varieties only")
        w(db, "Cantaloupe", C, 6, 6, TP)
        w(db, "Cantaloupe", M, 5, 6, EI)
        w(db, "Cantaloupe", W, 4, 5, EI)
        w(db, "Cantaloupe", H, 3, 4, EI)

        // ──────────────────────────────────────────
        //  EGGPLANT
        // ──────────────────────────────────────────
        w(db, "Eggplant", EC, 6, 6, TP, "Difficult in extreme cold zones — use season extenders")
        w(db, "Eggplant", C, 6, 6, TP)
        w(db, "Eggplant", M, 5, 6, TP)
        w(db, "Eggplant", W, 4, 5, TP)
        w(db, "Eggplant", H, 2, 4, TP)

        // ──────────────────────────────────────────
        //  OKRA
        // ──────────────────────────────────────────
        // Not recommended for extreme_cold or cold zones
        w(db, "Okra", M, 6, 6, DS)
        w(db, "Okra", W, 5, 6, DS)
        w(db, "Okra", H, 3, 5, DS, "Thrives in summer heat when other crops struggle")

        // ──────────────────────────────────────────
        //  SWEET POTATO
        // ──────────────────────────────────────────
        // Not recommended for extreme_cold or cold zones
        w(db, "Sweet Potato", M, 6, 6, TP, "Plant slips after soil is warm")
        w(db, "Sweet Potato", W, 5, 6, TP)
        w(db, "Sweet Potato", H, 3, 5, TP, "Survives summer heat in hot zones")

        // ──────────────────────────────────────────
        //  CORN
        // ──────────────────────────────────────────
        w(db, "Corn", EC, 6, 6, DS, "Choose 60-day varieties")
        w(db, "Corn", C, 6, 6, DS)
        w(db, "Corn", M, 5, 6, DS)
        w(db, "Corn", W, 4, 5, DS)
        w(db, "Corn", H, 3, 3, DS)

        // ──────────────────────────────────────────
        //  BUSH BEANS
        // ──────────────────────────────────────────
        w(db, "Bush Beans", EC, 6, 6, DS)
        w(db, "Bush Beans", C, 6, 6, DS)
        w(db, "Bush Beans", M, 5, 6, DS)
        w(db, "Bush Beans", W, 4, 5, DS, "Spring planting")
        w(db, "Bush Beans", H, 3, 4, DS, "Spring planting")
        w(db, "Bush Beans", H, 8, 9, DS, "Fall planting")

        // ──────────────────────────────────────────
        //  POLE BEANS
        // ──────────────────────────────────────────
        w(db, "Pole Beans", EC, 6, 6, DS)
        w(db, "Pole Beans", C, 6, 6, DS)
        w(db, "Pole Beans", M, 5, 6, DS)
        w(db, "Pole Beans", W, 4, 5, DS, "Spring planting")
        w(db, "Pole Beans", H, 3, 4, DS, "Spring planting")
        w(db, "Pole Beans", H, 8, 9, DS, "Fall planting")

        // ──────────────────────────────────────────
        //  LIMA BEANS
        // ──────────────────────────────────────────
        // Not recommended for extreme_cold or cold zones
        w(db, "Lima Beans", M, 5, 6, DS)
        w(db, "Lima Beans", W, 4, 5, DS)
        w(db, "Lima Beans", H, 3, 4, DS, "Spring planting")
        w(db, "Lima Beans", H, 8, 9, DS, "Fall planting")

        // ──────────────────────────────────────────
        //  SOUTHERN PEAS
        // ──────────────────────────────────────────
        // Only warm and hot zones
        w(db, "Southern Peas", W, 5, 6, DS)
        w(db, "Southern Peas", H, 3, 5, DS, "Thrives in summer heat")

        // ──────────────────────────────────────────
        //  LETTUCE (leaf)
        // ──────────────────────────────────────────
        w(db, "Lettuce (leaf)", EC, 5, 6, EI, "Spring planting")
        w(db, "Lettuce (leaf)", EC, 8, 8, EI, "Fall planting")
        w(db, "Lettuce (leaf)", C, 5, 6, EI, "Spring planting")
        w(db, "Lettuce (leaf)", C, 8, 8, EI, "Fall planting")
        w(db, "Lettuce (leaf)", M, 4, 5, EI, "Spring planting")
        w(db, "Lettuce (leaf)", M, 8, 9, EI, "Fall planting")
        w(db, "Lettuce (leaf)", W, 3, 4, EI, "Spring planting")
        w(db, "Lettuce (leaf)", W, 9, 10, EI, "Fall planting")
        w(db, "Lettuce (leaf)", H, 10, 3, EI, "Fall/winter — primary season in hot zones")

        // ──────────────────────────────────────────
        //  LETTUCE (head)
        // ──────────────────────────────────────────
        w(db, "Lettuce (head)", EC, 5, 6, TP, "Spring planting")
        w(db, "Lettuce (head)", EC, 8, 8, TP, "Fall planting")
        w(db, "Lettuce (head)", C, 5, 6, TP, "Spring planting")
        w(db, "Lettuce (head)", C, 8, 8, TP, "Fall planting")
        w(db, "Lettuce (head)", M, 4, 5, TP, "Spring planting")
        w(db, "Lettuce (head)", M, 8, 9, TP, "Fall planting")
        w(db, "Lettuce (head)", W, 3, 4, TP, "Spring planting")
        w(db, "Lettuce (head)", W, 9, 10, TP, "Fall planting")
        w(db, "Lettuce (head)", H, 10, 3, TP, "Fall/winter — primary season in hot zones")

        // ──────────────────────────────────────────
        //  SPINACH
        // ──────────────────────────────────────────
        w(db, "Spinach", EC, 4, 5, DS, "Spring planting")
        w(db, "Spinach", EC, 8, 8, DS, "Fall planting")
        w(db, "Spinach", C, 4, 5, DS, "Spring planting")
        w(db, "Spinach", C, 8, 8, DS, "Fall planting")
        w(db, "Spinach", M, 3, 4, DS, "Spring planting")
        w(db, "Spinach", M, 9, 9, DS, "Fall planting")
        w(db, "Spinach", W, 2, 3, DS, "Spring planting")
        w(db, "Spinach", W, 10, 11, DS, "Fall planting")
        w(db, "Spinach", H, 10, 2, DS, "Fall/winter — primary season in hot zones")

        // ──────────────────────────────────────────
        //  KALE
        // ──────────────────────────────────────────
        w(db, "Kale", EC, 4, 5, EI, "Spring planting")
        w(db, "Kale", EC, 8, 8, EI, "Fall planting")
        w(db, "Kale", C, 4, 5, EI, "Spring planting")
        w(db, "Kale", C, 8, 8, EI, "Fall planting")
        w(db, "Kale", M, 3, 5, EI, "Spring planting")
        w(db, "Kale", M, 8, 9, EI, "Fall planting")
        w(db, "Kale", W, 2, 3, EI, "Spring planting")
        w(db, "Kale", W, 9, 10, EI, "Fall planting")
        w(db, "Kale", H, 10, 3, EI, "Fall/winter — primary season in hot zones")

        // ──────────────────────────────────────────
        //  SWISS CHARD
        // ──────────────────────────────────────────
        w(db, "Swiss Chard", EC, 5, 6, DS, "Spring planting")
        w(db, "Swiss Chard", EC, 8, 8, DS, "Fall planting")
        w(db, "Swiss Chard", C, 5, 6, DS, "Spring planting")
        w(db, "Swiss Chard", C, 8, 8, DS, "Fall planting")
        w(db, "Swiss Chard", M, 4, 5, DS, "Spring planting")
        w(db, "Swiss Chard", M, 8, 9, DS, "Fall planting")
        w(db, "Swiss Chard", W, 3, 4, DS, "Spring planting")
        w(db, "Swiss Chard", W, 9, 10, DS, "Fall planting")
        w(db, "Swiss Chard", H, 10, 3, DS, "Fall/winter — more heat-tolerant than other greens")

        // ──────────────────────────────────────────
        //  COLLARD GREENS
        // ──────────────────────────────────────────
        w(db, "Collard Greens", M, 4, 5, EI, "Spring planting")
        w(db, "Collard Greens", M, 8, 9, EI, "Fall planting")
        w(db, "Collard Greens", W, 2, 3, EI, "Spring planting")
        w(db, "Collard Greens", W, 9, 10, EI, "Fall planting")
        w(db, "Collard Greens", H, 10, 3, EI, "Fall/winter — thrives in mild winters")

        // ──────────────────────────────────────────
        //  MUSTARD GREENS
        // ──────────────────────────────────────────
        w(db, "Mustard Greens", EC, 4, 5, DS, "Spring planting")
        w(db, "Mustard Greens", EC, 8, 8, DS, "Fall planting")
        w(db, "Mustard Greens", C, 4, 5, DS, "Spring planting")
        w(db, "Mustard Greens", C, 8, 8, DS, "Fall planting")
        w(db, "Mustard Greens", M, 3, 4, DS, "Spring planting")
        w(db, "Mustard Greens", M, 9, 9, DS, "Fall planting")
        w(db, "Mustard Greens", W, 2, 3, DS, "Spring planting")
        w(db, "Mustard Greens", W, 10, 11, DS, "Fall planting")
        w(db, "Mustard Greens", H, 10, 2, DS, "Fall/winter primary season")

        // ──────────────────────────────────────────
        //  BROCCOLI
        // ──────────────────────────────────────────
        w(db, "Broccoli", EC, 5, 5, TP, "Spring only — start indoors 6 weeks before transplant")
        w(db, "Broccoli", C, 5, 5, TP, "Spring only — start indoors 6 weeks before transplant")
        w(db, "Broccoli", M, 4, 5, TP, "Spring planting")
        w(db, "Broccoli", M, 8, 8, TP, "Fall planting — start indoors Jul-Aug")
        w(db, "Broccoli", W, 2, 3, TP, "Spring planting")
        w(db, "Broccoli", W, 9, 10, TP, "Fall planting")
        w(db, "Broccoli", H, 10, 1, TP, "Fall/winter — primary season in hot zones")

        // ──────────────────────────────────────────
        //  CAULIFLOWER
        // ──────────────────────────────────────────
        w(db, "Cauliflower", EC, 5, 5, TP, "Challenging — needs consistent cool temps")
        w(db, "Cauliflower", C, 5, 5, TP)
        w(db, "Cauliflower", M, 4, 5, TP, "Spring planting")
        w(db, "Cauliflower", M, 8, 8, TP, "Fall planting")
        w(db, "Cauliflower", W, 2, 3, TP, "Spring planting")
        w(db, "Cauliflower", W, 9, 10, TP, "Fall planting")
        w(db, "Cauliflower", H, 10, 1, TP, "Fall/winter only")

        // ──────────────────────────────────────────
        //  CABBAGE
        // ──────────────────────────────────────────
        w(db, "Cabbage", EC, 5, 5, TP)
        w(db, "Cabbage", C, 5, 5, TP)
        w(db, "Cabbage", M, 4, 5, TP, "Spring planting")
        w(db, "Cabbage", M, 8, 9, TP, "Fall planting")
        w(db, "Cabbage", W, 2, 3, TP, "Spring planting")
        w(db, "Cabbage", W, 9, 10, TP, "Fall planting")
        w(db, "Cabbage", H, 10, 1, TP, "Fall/winter primary season")

        // ──────────────────────────────────────────
        //  BRUSSELS SPROUTS
        // ──────────────────────────────────────────
        w(db, "Brussels Sprouts", EC, 5, 5, TP, "Challenging — may not mature before frost")
        w(db, "Brussels Sprouts", C, 5, 5, TP)
        w(db, "Brussels Sprouts", M, 5, 5, TP, "Spring planting for fall harvest")
        w(db, "Brussels Sprouts", M, 7, 8, TP, "Start indoors for fall harvest")
        w(db, "Brussels Sprouts", W, 8, 9, TP, "Fall planting for winter harvest")

        // ──────────────────────────────────────────
        //  PEAS
        // ──────────────────────────────────────────
        w(db, "Peas", EC, 4, 5, DS)
        w(db, "Peas", C, 4, 5, DS)
        w(db, "Peas", M, 3, 4, DS)
        w(db, "Peas", W, 2, 3, DS, "Spring planting")
        w(db, "Peas", W, 10, 11, DS, "Fall planting")
        w(db, "Peas", H, 10, 2, DS, "Fall/winter — primary season in hot zones")

        // ──────────────────────────────────────────
        //  RADISH
        // ──────────────────────────────────────────
        w(db, "Radish", EC, 5, 6, DS, "Spring planting")
        w(db, "Radish", EC, 8, 8, DS, "Fall planting")
        w(db, "Radish", C, 5, 6, DS, "Spring planting")
        w(db, "Radish", C, 8, 8, DS, "Fall planting")
        w(db, "Radish", M, 4, 5, DS, "Spring planting")
        w(db, "Radish", M, 8, 9, DS, "Fall planting")
        w(db, "Radish", W, 2, 4, DS, "Spring planting")
        w(db, "Radish", W, 9, 11, DS, "Fall planting")
        w(db, "Radish", H, 10, 3, DS, "Fall/winter — succession plant every 2 weeks")

        // ──────────────────────────────────────────
        //  CARROT
        // ──────────────────────────────────────────
        w(db, "Carrot", EC, 5, 6, DS)
        w(db, "Carrot", C, 5, 6, DS)
        w(db, "Carrot", M, 4, 5, DS, "Spring planting")
        w(db, "Carrot", M, 8, 8, DS, "Fall planting")
        w(db, "Carrot", W, 2, 3, DS, "Spring planting")
        w(db, "Carrot", W, 9, 10, DS, "Fall planting")
        w(db, "Carrot", H, 10, 2, DS, "Fall/winter primary season")

        // ──────────────────────────────────────────
        //  BEET
        // ──────────────────────────────────────────
        w(db, "Beet", EC, 5, 6, DS)
        w(db, "Beet", C, 5, 6, DS)
        w(db, "Beet", M, 4, 5, DS, "Spring planting")
        w(db, "Beet", M, 8, 9, DS, "Fall planting")
        w(db, "Beet", W, 2, 3, DS, "Spring planting")
        w(db, "Beet", W, 9, 10, DS, "Fall planting")
        w(db, "Beet", H, 10, 2, DS, "Fall/winter primary season")

        // ──────────────────────────────────────────
        //  TURNIP
        // ──────────────────────────────────────────
        w(db, "Turnip", EC, 5, 6, DS)
        w(db, "Turnip", C, 5, 6, DS)
        w(db, "Turnip", M, 4, 5, DS, "Spring planting")
        w(db, "Turnip", M, 8, 9, DS, "Fall planting")
        w(db, "Turnip", W, 2, 3, DS, "Spring planting")
        w(db, "Turnip", W, 9, 10, DS, "Fall planting")
        w(db, "Turnip", H, 10, 2, DS, "Fall/winter primary season")

        // ──────────────────────────────────────────
        //  ONION
        // ──────────────────────────────────────────
        w(db, "Onion", EC, 5, 5, EI, "Plant sets or transplants")
        w(db, "Onion", C, 5, 5, EI, "Plant sets or transplants")
        w(db, "Onion", M, 4, 4, EI, "Sets or transplants")
        w(db, "Onion", W, 2, 3, EI)
        w(db, "Onion", H, 10, 12, EI, "Fall planting for spring harvest. Use short-day varieties")

        // ──────────────────────────────────────────
        //  GARLIC (all fall-planted)
        // ──────────────────────────────────────────
        w(db, "Garlic", EC, 9, 10, DS, "Fall plant. Hardneck varieties best for cold zones")
        w(db, "Garlic", C, 9, 10, DS, "Fall plant. Hardneck varieties")
        w(db, "Garlic", M, 10, 11, DS, "Fall plant")
        w(db, "Garlic", W, 10, 11, DS, "Fall plant")
        w(db, "Garlic", H, 11, 12, DS, "Fall plant. Use softneck varieties for mild winters")

        // ──────────────────────────────────────────
        //  POTATO
        // ──────────────────────────────────────────
        w(db, "Potato", EC, 5, 5, DS, "Plant seed potatoes after last frost")
        w(db, "Potato", C, 5, 5, DS)
        w(db, "Potato", M, 4, 5, DS)
        w(db, "Potato", W, 2, 3, DS)
        w(db, "Potato", H, 1, 2, DS, "Plant early for spring harvest before heat")

        // ──────────────────────────────────────────
        //  KOHLRABI
        // ──────────────────────────────────────────
        w(db, "Kohlrabi", EC, 5, 5, EI)
        w(db, "Kohlrabi", C, 5, 5, EI)
        w(db, "Kohlrabi", M, 4, 5, EI, "Spring planting")
        w(db, "Kohlrabi", M, 8, 9, EI, "Fall planting")
        w(db, "Kohlrabi", W, 2, 3, EI, "Spring planting")
        w(db, "Kohlrabi", W, 9, 10, EI, "Fall planting")
        w(db, "Kohlrabi", H, 10, 1, EI, "Fall/winter only")

        // ══════════════════════════════════════════
        //  HERBS
        // ══════════════════════════════════════════

        // ──────────────────────────────────────────
        //  BASIL
        // ──────────────────────────────────────────
        w(db, "Basil", EC, 6, 6, TP, "Very frost-sensitive")
        w(db, "Basil", C, 6, 6, TP)
        w(db, "Basil", M, 5, 6, EI)
        w(db, "Basil", W, 4, 5, EI)
        w(db, "Basil", H, 3, 4, EI)

        // ──────────────────────────────────────────
        //  CILANTRO
        // ──────────────────────────────────────────
        w(db, "Cilantro", EC, 5, 6, DS)
        w(db, "Cilantro", C, 5, 6, DS)
        w(db, "Cilantro", M, 4, 5, DS, "Spring planting")
        w(db, "Cilantro", M, 9, 9, DS, "Fall planting")
        w(db, "Cilantro", W, 2, 3, DS, "Spring planting")
        w(db, "Cilantro", W, 10, 11, DS, "Fall planting")
        w(db, "Cilantro", H, 10, 2, DS, "Fall/winter — bolts fast in heat")

        // ──────────────────────────────────────────
        //  DILL
        // ──────────────────────────────────────────
        w(db, "Dill", EC, 5, 6, DS)
        w(db, "Dill", C, 5, 6, DS)
        w(db, "Dill", M, 4, 5, DS, "Spring planting")
        w(db, "Dill", M, 9, 9, DS, "Fall planting")
        w(db, "Dill", W, 3, 4, DS, "Spring planting")
        w(db, "Dill", W, 10, 11, DS, "Fall planting")
        w(db, "Dill", H, 10, 2, DS, "Fall/winter — bolts in heat")

        // ──────────────────────────────────────────
        //  PARSLEY
        // ──────────────────────────────────────────
        w(db, "Parsley", EC, 5, 6, EI, "Slow to germinate — be patient")
        w(db, "Parsley", C, 5, 6, EI)
        w(db, "Parsley", M, 4, 5, EI, "Spring planting")
        w(db, "Parsley", M, 9, 9, EI, "Fall planting")
        w(db, "Parsley", W, 3, 4, EI, "Spring planting")
        w(db, "Parsley", W, 10, 11, EI, "Fall planting")
        w(db, "Parsley", H, 10, 2, EI, "Fall/winter primary season")

        // ──────────────────────────────────────────
        //  OREGANO
        // ──────────────────────────────────────────
        w(db, "Oregano", EC, 5, 6, TP, "Perennial in zones 5+. Bring indoors in winter")
        w(db, "Oregano", C, 5, 6, TP, "Perennial in zones 5+")
        w(db, "Oregano", M, 4, 6, TP)
        w(db, "Oregano", W, 3, 5, TP)
        w(db, "Oregano", H, 2, 4, TP, "May go semi-dormant in extreme summer heat")

        // ──────────────────────────────────────────
        //  THYME
        // ──────────────────────────────────────────
        w(db, "Thyme", EC, 5, 6, TP, "Perennial in zones 5+. Mulch heavily in winter")
        w(db, "Thyme", C, 5, 6, TP, "Perennial in zones 5+")
        w(db, "Thyme", M, 4, 6, TP)
        w(db, "Thyme", W, 3, 5, TP)
        w(db, "Thyme", H, 2, 4, TP)

        // ──────────────────────────────────────────
        //  ROSEMARY
        // ──────────────────────────────────────────
        // Only perennial in zones 7+, others grow as annual or bring indoors
        w(db, "Rosemary", W, 3, 5, TP, "Perennial in zones 7+")
        w(db, "Rosemary", H, 2, 4, TP, "Evergreen perennial in hot zones")

        // ──────────────────────────────────────────
        //  SAGE
        // ──────────────────────────────────────────
        w(db, "Sage", EC, 5, 6, TP, "Perennial in zones 5+. Protect in extreme cold")
        w(db, "Sage", C, 5, 6, TP, "Perennial in zones 5+")
        w(db, "Sage", M, 4, 6, TP)
        w(db, "Sage", W, 3, 5, TP)
        w(db, "Sage", H, 2, 4, TP)

        // ──────────────────────────────────────────
        //  MINT
        // ──────────────────────────────────────────
        w(db, "Mint", EC, 5, 6, TP, "Perennial. ALWAYS grow in containers — very invasive")
        w(db, "Mint", C, 5, 6, TP)
        w(db, "Mint", M, 4, 6, TP)
        w(db, "Mint", W, 3, 5, TP)
        w(db, "Mint", H, 2, 4, TP)

        // ──────────────────────────────────────────
        //  CHIVES
        // ──────────────────────────────────────────
        w(db, "Chives", EC, 4, 6, EI, "Perennial. Very cold-hardy")
        w(db, "Chives", C, 4, 6, EI)
        w(db, "Chives", M, 3, 6, EI)
        w(db, "Chives", W, 2, 5, EI)
        w(db, "Chives", H, 10, 4, EI, "Fall through spring in hot zones")

        // ── Expanded Plant Windows ──

        // Cherry Tomato
        w(db, "Cherry Tomato", EC, 5, 6, TP, "Short-season varieties recommended")
        w(db, "Cherry Tomato", C, 5, 6, TP)
        w(db, "Cherry Tomato", M, 5, 6, TP)
        w(db, "Cherry Tomato", W, 4, 5, TP)
        w(db, "Cherry Tomato", H, 2, 4, TP)

        // Roma Tomato
        w(db, "Roma Tomato", EC, 5, 6, TP)
        w(db, "Roma Tomato", C, 5, 6, TP)
        w(db, "Roma Tomato", M, 5, 6, TP)
        w(db, "Roma Tomato", W, 4, 5, TP)
        w(db, "Roma Tomato", H, 2, 4, TP)

        // Jalapeno
        w(db, "Jalapeno", EC, 5, 6, TP)
        w(db, "Jalapeno", C, 5, 6, TP)
        w(db, "Jalapeno", M, 5, 6, TP)
        w(db, "Jalapeno", W, 4, 5, TP)
        w(db, "Jalapeno", H, 2, 4, TP)

        // Habanero
        w(db, "Habanero", M, 5, 6, TP, "Needs long warm season")
        w(db, "Habanero", W, 4, 5, TP)
        w(db, "Habanero", H, 2, 4, TP)

        // Cayenne
        w(db, "Cayenne", EC, 5, 6, TP)
        w(db, "Cayenne", C, 5, 6, TP)
        w(db, "Cayenne", M, 5, 6, TP)
        w(db, "Cayenne", W, 4, 5, TP)
        w(db, "Cayenne", H, 2, 4, TP)

        // Banana Pepper
        w(db, "Banana Pepper", EC, 5, 6, TP)
        w(db, "Banana Pepper", C, 5, 6, TP)
        w(db, "Banana Pepper", M, 5, 6, TP)
        w(db, "Banana Pepper", W, 4, 5, TP)
        w(db, "Banana Pepper", H, 2, 4, TP)

        // Pickling Cucumber
        w(db, "Pickling Cucumber", EC, 6, 6, DS)
        w(db, "Pickling Cucumber", C, 6, 6, DS)
        w(db, "Pickling Cucumber", M, 5, 6, EI)
        w(db, "Pickling Cucumber", W, 4, 5, EI)
        w(db, "Pickling Cucumber", H, 3, 4, EI)

        // Yellow Squash
        w(db, "Yellow Squash", EC, 6, 6, DS)
        w(db, "Yellow Squash", C, 6, 6, DS)
        w(db, "Yellow Squash", M, 5, 6, EI)
        w(db, "Yellow Squash", W, 4, 5, EI)
        w(db, "Yellow Squash", H, 3, 4, EI)

        // Butternut Squash
        w(db, "Butternut Squash", EC, 6, 6, DS, "Choose short-season varieties")
        w(db, "Butternut Squash", C, 6, 6, DS)
        w(db, "Butternut Squash", M, 5, 6, EI)
        w(db, "Butternut Squash", W, 4, 5, EI)
        w(db, "Butternut Squash", H, 2, 3, EI)

        // Acorn Squash
        w(db, "Acorn Squash", EC, 6, 6, DS)
        w(db, "Acorn Squash", C, 6, 6, DS)
        w(db, "Acorn Squash", M, 5, 6, EI)
        w(db, "Acorn Squash", W, 4, 5, EI)
        w(db, "Acorn Squash", H, 2, 3, EI)

        // Spaghetti Squash
        w(db, "Spaghetti Squash", EC, 6, 6, DS)
        w(db, "Spaghetti Squash", C, 6, 6, DS)
        w(db, "Spaghetti Squash", M, 5, 6, EI)
        w(db, "Spaghetti Squash", W, 4, 5, EI)
        w(db, "Spaghetti Squash", H, 2, 3, EI)

        // Honeydew
        w(db, "Honeydew", EC, 6, 6, TP, "Short-season varieties only")
        w(db, "Honeydew", C, 6, 6, TP)
        w(db, "Honeydew", M, 5, 6, EI)
        w(db, "Honeydew", W, 4, 5, EI)
        w(db, "Honeydew", H, 3, 4, EI)

        // Snap Peas
        w(db, "Snap Peas", EC, 4, 5, DS)
        w(db, "Snap Peas", C, 4, 5, DS)
        w(db, "Snap Peas", M, 3, 4, DS)
        w(db, "Snap Peas", W, 2, 3, DS)
        w(db, "Snap Peas", H, 10, 2, DS, "Fall/winter primary season")

        // Snow Peas
        w(db, "Snow Peas", EC, 4, 5, DS)
        w(db, "Snow Peas", C, 4, 5, DS)
        w(db, "Snow Peas", M, 3, 4, DS)
        w(db, "Snow Peas", W, 2, 3, DS)
        w(db, "Snow Peas", H, 10, 2, DS, "Fall/winter primary season")

        // Romaine Lettuce
        w(db, "Romaine Lettuce", EC, 5, 6, EI, "Spring planting")
        w(db, "Romaine Lettuce", EC, 8, 8, EI, "Fall planting")
        w(db, "Romaine Lettuce", C, 5, 6, EI, "Spring planting")
        w(db, "Romaine Lettuce", C, 8, 8, EI, "Fall planting")
        w(db, "Romaine Lettuce", M, 4, 5, EI, "Spring planting")
        w(db, "Romaine Lettuce", M, 8, 9, EI, "Fall planting")
        w(db, "Romaine Lettuce", W, 3, 4, EI, "Spring planting")
        w(db, "Romaine Lettuce", W, 9, 10, EI, "Fall planting")
        w(db, "Romaine Lettuce", H, 10, 3, EI, "Fall/winter primary season")

        // Butter Lettuce
        w(db, "Butter Lettuce", EC, 5, 6, EI, "Spring planting")
        w(db, "Butter Lettuce", C, 5, 6, EI, "Spring planting")
        w(db, "Butter Lettuce", M, 4, 5, EI, "Spring planting")
        w(db, "Butter Lettuce", M, 8, 9, EI, "Fall planting")
        w(db, "Butter Lettuce", W, 3, 4, EI, "Spring planting")
        w(db, "Butter Lettuce", W, 9, 10, EI, "Fall planting")
        w(db, "Butter Lettuce", H, 10, 3, EI, "Fall/winter primary season")

        // Arugula
        w(db, "Arugula", EC, 5, 6, DS, "Spring planting")
        w(db, "Arugula", EC, 8, 8, DS, "Fall planting")
        w(db, "Arugula", C, 5, 6, DS, "Spring planting")
        w(db, "Arugula", C, 8, 8, DS, "Fall planting")
        w(db, "Arugula", M, 4, 5, DS, "Spring planting")
        w(db, "Arugula", M, 9, 9, DS, "Fall planting")
        w(db, "Arugula", W, 2, 3, DS, "Spring planting")
        w(db, "Arugula", W, 10, 11, DS, "Fall planting")
        w(db, "Arugula", H, 10, 2, DS, "Fall/winter — bolts fast in heat")

        // Green Onion
        w(db, "Green Onion", EC, 5, 6, DS)
        w(db, "Green Onion", C, 5, 6, DS)
        w(db, "Green Onion", M, 3, 6, DS)
        w(db, "Green Onion", W, 2, 5, DS)
        w(db, "Green Onion", H, 10, 4, DS, "Year-round in mild areas")

        // Shallot
        w(db, "Shallot", EC, 5, 5, DS)
        w(db, "Shallot", C, 5, 5, DS)
        w(db, "Shallot", M, 4, 4, DS)
        w(db, "Shallot", W, 2, 3, DS)
        w(db, "Shallot", H, 10, 12, DS, "Fall planting for spring harvest")

        // Leek
        w(db, "Leek", EC, 5, 5, TP)
        w(db, "Leek", C, 5, 5, TP)
        w(db, "Leek", M, 4, 5, TP)
        w(db, "Leek", W, 2, 3, TP)
        w(db, "Leek", H, 10, 12, TP, "Fall planting")

        // Celery
        w(db, "Celery", EC, 5, 6, TP, "Start indoors early")
        w(db, "Celery", C, 5, 6, TP)
        w(db, "Celery", M, 4, 5, TP)
        w(db, "Celery", W, 2, 3, TP)
        w(db, "Celery", H, 10, 1, TP, "Fall/winter only")

        // Asparagus (perennial)
        w(db, "Asparagus", EC, 4, 5, TP, "Plant crowns. 2-3 year wait before harvest")
        w(db, "Asparagus", C, 4, 5, TP)
        w(db, "Asparagus", M, 3, 4, TP)
        w(db, "Asparagus", W, 2, 3, TP)

        // Artichoke
        w(db, "Artichoke", W, 3, 5, TP, "Perennial in zones 7+")
        w(db, "Artichoke", H, 2, 4, TP, "Perennial, produces year-round in mild climates")

        // Lemongrass
        w(db, "Lemongrass", W, 4, 6, TP, "Grow as annual in cooler areas")
        w(db, "Lemongrass", H, 3, 5, TP, "Perennial in tropical zones")

        // Lavender
        w(db, "Lavender", EC, 5, 6, TP, "Perennial in zones 5+, mulch heavily")
        w(db, "Lavender", C, 5, 6, TP)
        w(db, "Lavender", M, 4, 5, TP)
        w(db, "Lavender", W, 3, 4, TP)

        // Strawberry
        w(db, "Strawberry", EC, 5, 5, TP, "Spring plant for next year harvest")
        w(db, "Strawberry", C, 5, 5, TP)
        w(db, "Strawberry", M, 3, 4, TP)
        w(db, "Strawberry", W, 2, 3, TP)
        w(db, "Strawberry", H, 10, 12, TP, "Fall planting for spring harvest")

        // Blueberry
        w(db, "Blueberry", EC, 4, 5, TP, "Need acidic soil, 2+ varieties for cross-pollination")
        w(db, "Blueberry", C, 4, 5, TP)
        w(db, "Blueberry", M, 3, 4, TP)
        w(db, "Blueberry", W, 2, 3, TP)

        // Raspberry
        w(db, "Raspberry", EC, 4, 5, TP, "Plant dormant canes")
        w(db, "Raspberry", C, 4, 5, TP)
        w(db, "Raspberry", M, 3, 4, TP)
        w(db, "Raspberry", W, 2, 3, TP)

        // Blackberry
        w(db, "Blackberry", M, 3, 4, TP)
        w(db, "Blackberry", W, 2, 3, TP)
        w(db, "Blackberry", H, 1, 2, TP)

        // Fig
        w(db, "Fig", W, 3, 4, TP, "Perennial, plant in spring")
        w(db, "Fig", H, 2, 3, TP, "Evergreen in hot zones")

        // Grape
        w(db, "Grape", EC, 4, 5, TP, "Plant dormant vines")
        w(db, "Grape", C, 4, 5, TP)
        w(db, "Grape", M, 3, 4, TP)
        w(db, "Grape", W, 2, 3, TP)
        w(db, "Grape", H, 1, 2, TP)

        // Passion Fruit
        w(db, "Passion Fruit", H, 3, 5, TP, "Frost-free zones only")

        // Papaya
        w(db, "Papaya", H, 3, 5, TP, "Tropical, needs consistent warmth")

        // Mango
        w(db, "Mango", H, 3, 4, TP, "Large tree, frost-free zones only")

        // Iceberg Lettuce
        w(db, "Iceberg Lettuce", EC, 5, 6, TP, "Spring planting")
        w(db, "Iceberg Lettuce", C, 5, 6, TP, "Spring planting")
        w(db, "Iceberg Lettuce", M, 4, 5, TP, "Spring planting")
        w(db, "Iceberg Lettuce", M, 8, 9, TP, "Fall planting")
        w(db, "Iceberg Lettuce", W, 3, 4, TP, "Spring planting")
        w(db, "Iceberg Lettuce", W, 9, 10, TP, "Fall planting")
        w(db, "Iceberg Lettuce", H, 10, 3, TP, "Fall/winter primary season")

        // Yam
        w(db, "Yam", H, 3, 5, TP, "Tropical, needs very long warm season")
    }

    // ═══════════════════════════════════════════════════════════════
    //  PLANT VARIETIES — cultivar data for harvest predictions
    // ═══════════════════════════════════════════════════════════════

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

    @Suppress("LongMethod")
    private fun seedPlantVarieties(db: SupportSQLiteDatabase) {

        // ── Tomato (determinate) ──
        v(db, "Tomato (determinate)", "Celebrity", 65, 72, "All-American classic hybrid; excellent disease resistance and reliable yields")
        v(db, "Tomato (determinate)", "Mountain Pride", 74, 77, "Firm crack-resistant 10 oz fruits; great for humid regions")
        v(db, "Tomato (determinate)", "Defiant PhR", 70, 75, "Late blight resistant hybrid with 8 oz fruits; bred for organic gardens")
        v(db, "Tomato (determinate)", "Rutgers", 73, 80, "Heritage variety with classic tomato flavor; balanced acid-sweet for slicing and canning")
        v(db, "Tomato (determinate)", "Glacier", 55, 60, "Ultra-early 2-3 oz fruits on compact plants; perfect for short seasons and containers")
        v(db, "Tomato (determinate)", "Mountain Fresh Plus", 72, 78, "Large firm fruits with strong disease resistance; bred for Eastern US conditions")
        v(db, "Tomato (determinate)", "Bush Early Girl", 59, 65, "Compact bush form with 4-6 oz fruits; great for containers and small spaces")

        // ── Tomato (indeterminate) ──
        v(db, "Tomato (indeterminate)", "Better Boy", 70, 75, "Classic hybrid producing 12-16 oz fruits; one of the most popular home garden tomatoes")
        v(db, "Tomato (indeterminate)", "Big Beef", 73, 78, "AAS-winning hybrid with smooth 10-12 oz fruits; excellent disease resistance")
        v(db, "Tomato (indeterminate)", "Brandywine", 80, 90, "Famous Amish heirloom with large pink beefsteak fruits and exceptional rich flavor")
        v(db, "Tomato (indeterminate)", "Cherokee Purple", 80, 90, "Dusky purple-brown fruits with complex sweet-smoky flavor; thin skin")
        v(db, "Tomato (indeterminate)", "Mortgage Lifter", 80, 85, "Large 1-2 lb pink beefsteak heirloom with mild sweet flavor; few seeds")
        v(db, "Tomato (indeterminate)", "Early Girl", 52, 62, "Very early 4-6 oz slicer on vigorous vines; reliable in all climates")
        v(db, "Tomato (indeterminate)", "Beefsteak", 80, 90, "Classic large meaty 1-2 lb fruits; needs staking and long warm season")
        v(db, "Tomato (indeterminate)", "Black Krim", 75, 85, "Russian heirloom with dark mahogany fruits and intense sweet-salty flavor")
        v(db, "Tomato (indeterminate)", "Amish Paste", 74, 85, "Large 8 oz paste tomato with meaty flesh; versatile for fresh eating and canning")
        v(db, "Tomato (indeterminate)", "German Johnson", 76, 85, "Large pink heirloom with mild sweet flavor; parent variety of Mortgage Lifter")

        // ── Cherry Tomato ──
        v(db, "Cherry Tomato", "Sun Gold", 55, 65, "Gold-orange cherry with intensely sweet tropical flavor; the benchmark sweet cherry")
        v(db, "Cherry Tomato", "Sweet 100", 55, 65, "Prolific red cherry producing long clusters of very sweet 1-inch fruits")
        v(db, "Cherry Tomato", "Supersweet 100", 60, 65, "Improved Sweet 100 with better crack resistance; uniform sweet red cherries")
        v(db, "Cherry Tomato", "Matt's Wild Cherry", 58, 65, "Tiny half-inch Mexican heirloom; intense flavor, incredibly productive and heat tolerant")
        v(db, "Cherry Tomato", "Yellow Pear", 70, 78, "Heirloom pear-shaped yellow fruits with mild sweet flavor; vigorous and heavy yields")
        v(db, "Cherry Tomato", "Black Cherry", 65, 75, "Deep purple-brown cherry with complex rich sweet flavor; beautiful in salads")
        v(db, "Cherry Tomato", "Juliet", 60, 65, "AAS-winning grape tomato with oblong 1 oz fruits; crack resistant with good disease tolerance")
        v(db, "Cherry Tomato", "Sweetie", 55, 65, "Open-pollinated red cherry with high sugar; clusters of 15-20 fruits per truss")

        // ── Roma Tomato ──
        v(db, "Roma Tomato", "San Marzano", 78, 85, "Italian classic paste tomato with elongated fruits; the gold standard for sauce")
        v(db, "Roma Tomato", "Roma VF", 73, 80, "Compact determinate paste type; fusarium and verticillium resistant")
        v(db, "Roma Tomato", "Martino's Roma", 75, 80, "Italian heirloom with meaty pear-shaped fruits; more flavor than standard Roma")
        v(db, "Roma Tomato", "Opalka", 78, 85, "Polish heirloom with long pointed 5-inch fruits; nearly seedless with sweet rich flavor")
        v(db, "Roma Tomato", "Viva Italia", 72, 80, "Hybrid Roma with improved disease resistance; firm meaty fruits for processing")
        v(db, "Roma Tomato", "Granadero", 70, 75, "High-yielding hybrid with uniform 4 oz fruits; excellent late blight resistance")

        // ── Pepper (sweet) ──
        v(db, "Pepper (sweet)", "California Wonder", 72, 80, "Classic blocky green-to-red bell; thick sweet walls and open habit")
        v(db, "Pepper (sweet)", "Big Bertha", 72, 78, "Extra-large 7-inch green-to-red bell; thick walls and heavy yields")
        v(db, "Pepper (sweet)", "King of the North", 70, 75, "Early blocky bell for northern gardens; sets fruit in cool temps")
        v(db, "Pepper (sweet)", "Red Knight", 68, 72, "Early red bell hybrid with thick sweet walls; compact with disease resistance")
        v(db, "Pepper (sweet)", "Lunchbox Mini", 60, 75, "Snack-size 3-inch elongated sweet peppers; prolific, great for containers")
        v(db, "Pepper (sweet)", "Corno di Toro", 78, 85, "Italian 8-10 inch bull's horn pepper; sweet rich flavor when roasted")
        v(db, "Pepper (sweet)", "Lipstick", 55, 65, "Early pimiento with tapered 4-inch sweet red fruits; reliable in short seasons")
        v(db, "Pepper (sweet)", "Purple Beauty", 70, 78, "Dark purple bell with thick sweet walls; compact plants and unique color")

        // ── Pepper (hot) ──
        v(db, "Pepper (hot)", "Serrano", 75, 85, "Classic Mexican hot pepper (10-23k SHU); cylindrical 2-3 inch fruits")
        v(db, "Pepper (hot)", "Thai Hot", 75, 90, "Small fiery 1-inch peppers (50-100k SHU); prolific on compact plants")
        v(db, "Pepper (hot)", "Anaheim", 74, 80, "Mild 6-8 inch pepper (500-2500 SHU); great for roasting and stuffing")
        v(db, "Pepper (hot)", "Poblano", 70, 80, "Mild-medium heart-shaped (1-2k SHU); essential for chiles rellenos")
        v(db, "Pepper (hot)", "Ghost Pepper", 100, 120, "Extremely hot (1M+ SHU); needs long warm season and patience")
        v(db, "Pepper (hot)", "Hungarian Wax", 60, 70, "Medium-hot 6-inch waxy yellow (5-10k SHU); early, great for pickling")
        v(db, "Pepper (hot)", "Scotch Bonnet", 90, 120, "Caribbean bonnet-shaped (100-350k SHU); fruity heat for jerk seasoning")

        // ── Jalapeno ──
        v(db, "Jalapeno", "Early Jalapeno", 60, 70, "First to mature with 3-inch fruits; reliable in short-season gardens")
        v(db, "Jalapeno", "TAM Mild Jalapeno", 68, 80, "Milder version (1000-1500 SHU); same flavor with reduced heat")
        v(db, "Jalapeno", "Mucho Nacho", 68, 75, "Extra-large 4-inch thick-walled fruits; heavy yields and classic heat")
        v(db, "Jalapeno", "Purple Jalapeno", 70, 80, "Dark purple 3-inch fruits ripening to red; ornamental and edible")
        v(db, "Jalapeno", "Mammoth", 70, 75, "Jumbo 5-inch jalapenos with thick walls; great for stuffing and poppers")

        // ── Habanero ──
        v(db, "Habanero", "Orange Habanero", 90, 100, "Classic lantern-shaped orange (150-325k SHU); fruity tropical heat")
        v(db, "Habanero", "Caribbean Red", 90, 110, "Hotter cousin (400k SHU) with wrinkled red fruits")
        v(db, "Habanero", "Chocolate Habanero", 95, 110, "Dark brown fruits (300-425k SHU); rich smoky flavor with intense heat")
        v(db, "Habanero", "Red Savina", 90, 100, "Large red fruits with extreme heat (500k SHU) and fruity flavor")
        v(db, "Habanero", "Habanada", 90, 100, "Heatless habanero (0 SHU) with all the fruity flavor; unique no-heat variety")

        // ── Cayenne ──
        v(db, "Cayenne", "Long Slim", 70, 80, "Classic thin 5-6 inch red cayenne (30-50k SHU); great dried or powdered")
        v(db, "Cayenne", "Large Red Thick", 70, 85, "Meatier 6-inch cayenne with thicker walls; easier to stuff and dehydrate")
        v(db, "Cayenne", "Ring of Fire", 60, 68, "Early hybrid with 4-inch peppers (50k SHU); compact and very heavy yields")
        v(db, "Cayenne", "Golden Cayenne", 72, 82, "Golden-yellow 6-inch fruits (30-50k SHU); milder fruity flavor")
        v(db, "Cayenne", "Dragon Cayenne", 68, 75, "Asian hybrid with slim curved red fruits; productive and great for drying")

        // ── Banana Pepper ──
        v(db, "Banana Pepper", "Sweet Banana", 60, 72, "Classic mild yellow 6-7 inch tapered; sweet and tangy, great pickled")
        v(db, "Banana Pepper", "Hungarian Yellow Wax", 58, 70, "Medium-hot 5-6 inch yellow; more heat, excellent for pickling")
        v(db, "Banana Pepper", "Inferno Hot Banana", 60, 75, "Hot banana type (7k SHU) with 8-inch fruits")
        v(db, "Banana Pepper", "Bananarama", 58, 68, "Sweet 7-8 inch hybrid with thick walls; very productive and uniform")
        v(db, "Banana Pepper", "Sweet Hungarian", 58, 70, "Mild 6-inch yellow to red; traditional Hungarian type with sweet fruity flavor")

        // ── Cucumber ──
        v(db, "Cucumber", "Marketmore 76", 58, 68, "Dark green 8-9 inch slicer; downy and powdery mildew resistant")
        v(db, "Cucumber", "Straight Eight", 52, 65, "Classic heirloom slicer with uniform 8-inch fruits; AAS winner since 1935")
        v(db, "Cucumber", "Armenian", 55, 65, "Long ribbed 12-18 inch pale green; actually a melon, never bitter")
        v(db, "Cucumber", "Lemon", 55, 65, "Unique round lemon-yellow heirloom; sweet, mild, never bitter")
        v(db, "Cucumber", "Diva", 58, 65, "Seedless gynoecious hybrid with sweet 7-inch fruits; AAS winner")
        v(db, "Cucumber", "Spacemaster", 56, 62, "Compact bush type for containers; 7-8 inch slicers on short vines")

        // ── Pickling Cucumber ──
        v(db, "Pickling Cucumber", "Boston Pickling", 50, 58, "Classic American pickling cucumber since 1880; crisp 3-6 inch fruits")
        v(db, "Pickling Cucumber", "National Pickling", 48, 55, "Blocky 5-inch fruits for uniform pickles; heavy yields")
        v(db, "Pickling Cucumber", "Calypso", 50, 56, "Gynoecious hybrid with uniform fruits; multiple disease resistances")
        v(db, "Pickling Cucumber", "Homemade Pickles", 50, 55, "Compact vines with 2-5 inch fruits; white spined and crisp")
        v(db, "Pickling Cucumber", "Wisconsin SMR 58", 54, 60, "Disease-resistant heirloom; reliable Midwest favorite for canning")

        // ── Zucchini ──
        v(db, "Zucchini", "Black Beauty", 45, 55, "Classic dark green Italian zucchini; glossy straight fruits on compact plants")
        v(db, "Zucchini", "Costata Romanesco", 52, 60, "Ribbed Italian heirloom with nutty flavor; gourmet quality")
        v(db, "Zucchini", "Dark Star", 43, 50, "Early hybrid with deep green fruits; open bush habit and good virus tolerance")
        v(db, "Zucchini", "Golden Zucchini", 45, 55, "Bright yellow fruits easy to spot; same flavor, fun color variety")
        v(db, "Zucchini", "Cocozelle", 45, 55, "Italian heirloom with dark green striped 12-inch fruits; rich nutty flavor")
        v(db, "Zucchini", "Ronde de Nice", 45, 52, "French round heirloom, best picked golf-ball size; perfect for stuffing")

        // ── Yellow Squash ──
        v(db, "Yellow Squash", "Early Prolific Straightneck", 42, 50, "Standard yellow straightneck; compact bush with heavy yields")
        v(db, "Yellow Squash", "Early Summer Crookneck", 42, 53, "Classic crookneck with bumpy golden skin; buttery flavor")
        v(db, "Yellow Squash", "Dixie", 42, 50, "Improved crookneck with smooth skin; excellent Southern adaptation")
        v(db, "Yellow Squash", "Goldbar", 42, 50, "Hybrid straightneck with bright golden fruits; strong virus tolerance")
        v(db, "Yellow Squash", "Yellow Scallop", 45, 55, "Patty pan type with scalloped edges; great for stuffing and grilling")

        // ── Winter Squash ──
        v(db, "Winter Squash", "Kabocha", 90, 100, "Japanese sweet squash with dense orange flesh; dry flaky texture when baked")
        v(db, "Winter Squash", "Delicata", 80, 100, "Cream and green striped; sweet edible skin, no peeling needed")
        v(db, "Winter Squash", "Blue Hubbard", 100, 110, "Large 12-15 lb blue-grey squash; stores 6+ months")
        v(db, "Winter Squash", "Red Kuri", 85, 95, "Teardrop-shaped red-orange; smooth sweet chestnut-like flavor")
        v(db, "Winter Squash", "Turban Squash", 95, 100, "Colorful turban-shaped ornamental; also edible with sweet mild flesh")

        // ── Butternut Squash ──
        v(db, "Butternut Squash", "Waltham", 85, 100, "Classic butternut with 9-10 inch tan fruits; stores 3-6 months")
        v(db, "Butternut Squash", "Honeynut", 90, 100, "Mini butternut half the size; sweeter concentrated flavor")
        v(db, "Butternut Squash", "Butterbush", 75, 85, "Compact bush type producing 1.5 lb fruits; for small gardens")
        v(db, "Butternut Squash", "Atlas", 85, 95, "Hybrid with 6-8 lb fruits; powdery mildew resistance")
        v(db, "Butternut Squash", "Penelope", 80, 90, "Early hybrid with uniform 2-3 lb fruits; sweet smooth flesh")

        // ── Acorn Squash ──
        v(db, "Acorn Squash", "Table Queen", 80, 90, "Original acorn squash with dark green ribbed 1-2 lb fruits")
        v(db, "Acorn Squash", "Table Gold", 78, 85, "Golden-skinned; slightly sweeter than green varieties")
        v(db, "Acorn Squash", "Carnival", 85, 95, "Multicolored cream, green, orange skin; excellent flavor")
        v(db, "Acorn Squash", "Honey Bear", 85, 90, "AAS-winning mini on compact vines; 1 lb personal-size fruits")
        v(db, "Acorn Squash", "Festival", 85, 95, "Colorful striped acorn-butternut cross; sweet dry flaky texture")

        // ── Spaghetti Squash ──
        v(db, "Spaghetti Squash", "Vegetable Spaghetti", 90, 100, "The original with 8-10 inch oblong yellow fruits; mild stringy flesh")
        v(db, "Spaghetti Squash", "Orangetti", 85, 90, "Orange-fleshed hybrid with richer flavor and more beta-carotene")
        v(db, "Spaghetti Squash", "Hasta La Pasta", 85, 95, "Compact semi-bush with golden fruits; excellent noodle-like texture")
        v(db, "Spaghetti Squash", "Small Wonder", 80, 85, "Mini 2 lb personal-size; compact vines for small gardens")
        v(db, "Spaghetti Squash", "Pinnacle", 85, 90, "Hybrid with uniform cream-yellow fruits; improved disease resistance")

        // ── Pumpkin ──
        v(db, "Pumpkin", "Jack O'Lantern", 100, 115, "Classic carving pumpkin 10-18 lb; good shape for Halloween")
        v(db, "Pumpkin", "Sugar Pie", 100, 110, "Baking standard with 6-8 lb fruits; sweet dense flesh for pies")
        v(db, "Pumpkin", "Connecticut Field", 100, 120, "Large 15-25 lb field pumpkin; classic American heirloom")
        v(db, "Pumpkin", "Cinderella", 95, 110, "Flat 20-35 lb French Rouge Vif d'Etampes; beautiful display and good eating")
        v(db, "Pumpkin", "Lumina", 90, 100, "Ghostly white 10-12 lb pumpkin; great for painting and decorating")
        v(db, "Pumpkin", "Baby Bear", 90, 105, "Mini 1.5-2.5 lb with flat shape; perfect for kids and individual pies")
        v(db, "Pumpkin", "Big Max", 110, 120, "Show pumpkin reaching 50-100+ lb; fun giant variety")
        v(db, "Pumpkin", "Jarrahdale", 95, 100, "Blue-grey Australian heirloom 12-18 lb; sweet dense orange flesh")

        // ── Watermelon ──
        v(db, "Watermelon", "Crimson Sweet", 80, 90, "Classic oval 20-25 lb with light/dark green stripes; bright red sweet flesh")
        v(db, "Watermelon", "Sugar Baby", 75, 80, "Compact icebox 8-12 lb; dark green rind, great for small gardens")
        v(db, "Watermelon", "Charleston Gray", 85, 90, "Large oblong 28-35 lb; light green rind, good disease resistance")
        v(db, "Watermelon", "Moon and Stars", 90, 100, "Unique heirloom with yellow spots on dark rind; 25-40 lb")
        v(db, "Watermelon", "Yellow Doll", 65, 75, "Early 5-8 lb icebox with yellow flesh; sweet, compact vines")
        v(db, "Watermelon", "Jubilee", 90, 95, "Large oblong 30-40 lb striped; bright red sweet flesh")
        v(db, "Watermelon", "Blacktail Mountain", 70, 75, "Extra-early 8-12 lb; excellent for short-season gardens")

        // ── Cantaloupe ──
        v(db, "Cantaloupe", "Hale's Best Jumbo", 75, 85, "Classic heirloom 4-5 lb netted; thick sweet salmon flesh, drought tolerant")
        v(db, "Cantaloupe", "Ambrosia", 72, 86, "Hybrid with extra-sweet peach-colored flesh; superb flavor")
        v(db, "Cantaloupe", "Hearts of Gold", 70, 80, "Heirloom 2-3 lb with deep orange very sweet flesh")
        v(db, "Cantaloupe", "Honey Rock", 80, 85, "AAS-winning heirloom 3-4 lb; juicy sweet salmon flesh")
        v(db, "Cantaloupe", "Sugar Cube", 72, 80, "Personal-size 2 lb hybrid; extremely sweet, mildew resistant")
        v(db, "Cantaloupe", "Minnesota Midget", 60, 68, "Extra-early 4-inch mini melon; bred for short seasons, compact vines")

        // ── Honeydew ──
        v(db, "Honeydew", "Green Flesh", 80, 100, "Classic smooth white-rind with sweet green flesh; standard type")
        v(db, "Honeydew", "Earlidew", 80, 90, "Early hybrid 4-5 lb; sweet green flesh, good for shorter seasons")
        v(db, "Honeydew", "Honey Brew", 80, 95, "Orange-fleshed cross blending honeydew and cantaloupe flavors")
        v(db, "Honeydew", "Snow Mass", 85, 100, "Large 6-8 lb; very sweet pale green flesh")
        v(db, "Honeydew", "Kazakh", 85, 95, "Heirloom golden-yellow rind with spicy sweet green flesh")

        // ── Eggplant ──
        v(db, "Eggplant", "Black Beauty", 72, 80, "Classic large globe heirloom; glossy dark purple, heavy producer")
        v(db, "Eggplant", "Ichiban", 58, 70, "Japanese hybrid with slender 10-inch dark purple fruits; early and prolific")
        v(db, "Eggplant", "Rosa Bianca", 75, 90, "Italian heirloom with lavender-white globe fruits; no bitterness")
        v(db, "Eggplant", "Hansel", 55, 65, "AAS-winning mini producing dark purple clusters; compact for containers")
        v(db, "Eggplant", "Orient Express", 58, 65, "Early Asian hybrid; sets fruit in cool weather better than most")
        v(db, "Eggplant", "Ping Tung Long", 62, 75, "Taiwanese heirloom with slender 12-18 inch lavender fruits; sweet and tender")

        // ── Okra ──
        v(db, "Okra", "Clemson Spineless", 55, 64, "Most popular home garden okra; spineless plants, AAS winner since 1939")
        v(db, "Okra", "Emerald", 52, 58, "Smooth round dark green pods; stay tender at larger sizes")
        v(db, "Okra", "Burgundy", 55, 65, "Wine-red stems and crimson pods; ornamental and edible")
        v(db, "Okra", "Annie Oakley II", 48, 55, "Early hybrid on compact spineless plants; suited to northern gardens")
        v(db, "Okra", "Jambalaya", 48, 53, "Compact 3-4 ft plants with heavy yields; great for small spaces")
        v(db, "Okra", "Hill Country Red", 60, 70, "Texas heirloom since 1954; green pods tinted red at tips")

        // ── Sweet Potato ──
        v(db, "Sweet Potato", "Beauregard", 90, 105, "Most widely grown commercial variety; orange skin and flesh, reliable yields")
        v(db, "Sweet Potato", "Jewel", 100, 120, "Copper-skinned with deep orange flesh; good storage quality")
        v(db, "Sweet Potato", "Covington", 95, 110, "Rose-copper skin with bright orange flesh; excellent eating quality")
        v(db, "Sweet Potato", "Georgia Jet", 90, 100, "Red skin with deep orange flesh; extra-early for northern growing")
        v(db, "Sweet Potato", "Murasaki", 100, 120, "Purple skin with dry white flesh; Asian type with chestnut-like flavor")

        // ── Corn ──
        v(db, "Corn", "Early Sunglow", 60, 68, "Extra-early yellow; compact 5 ft stalks with 7-inch ears")
        v(db, "Corn", "Golden Bantam", 75, 82, "Classic open-pollinated heirloom with rich old-fashioned flavor")
        v(db, "Corn", "Bodacious", 73, 78, "Sugar-enhanced yellow hybrid; tender sweet kernels on 8-inch ears")
        v(db, "Corn", "Peaches and Cream", 78, 85, "Popular bicolor with sweet white and yellow kernels; widely adapted")
        v(db, "Corn", "Ambrosia", 73, 78, "Bicolor hybrid prized for exceptional tenderness and sweetness")
        v(db, "Corn", "Honey and Cream", 78, 86, "Bicolor with 7-8 inch ears; reliable classic sweet corn flavor")
        v(db, "Corn", "Kandy Korn", 84, 92, "Yellow se hybrid with extra-sweet tender kernels; large ears")
        v(db, "Corn", "Silver Queen", 88, 96, "Late-season white classic producing large 8-9 inch ears of sweet kernels")
        v(db, "Corn", "Jubilee", 85, 95, "Supersweet sh2 yellow hybrid with extended harvest window")

        // ── Bush Beans ──
        v(db, "Bush Beans", "Blue Lake 274", 50, 58, "Industry standard with straight 6-inch stringless pods; heavy yields")
        v(db, "Bush Beans", "Contender", 40, 55, "Early producer of tender 6-inch pods; tolerates cool soil")
        v(db, "Bush Beans", "Provider", 50, 54, "Reliable early bean that germinates in cool soil; round stringless pods")
        v(db, "Bush Beans", "Jade", 53, 60, "Slim straight dark green 6-7 inch pods; excellent for market gardens")
        v(db, "Bush Beans", "Royal Burgundy", 51, 60, "Purple pods turn green when cooked; tolerates cool soil")
        v(db, "Bush Beans", "Roma II", 53, 59, "Bush Italian classic with wide flat stringless pods; great fresh or frozen")
        v(db, "Bush Beans", "Tendergreen", 50, 60, "Heirloom with round meaty dark green pods; good disease resistance")

        // ── Pole Beans ──
        v(db, "Pole Beans", "Kentucky Wonder", 58, 72, "Classic heirloom since 1877 with 7-9 inch pods; vigorous climber")
        v(db, "Pole Beans", "Blue Lake Pole", 60, 68, "Gold standard for canning with straight round 6-inch stringless pods")
        v(db, "Pole Beans", "Kentucky Blue", 63, 73, "Cross of Kentucky Wonder and Blue Lake; sweet flavor and heavy yield")
        v(db, "Pole Beans", "Rattlesnake", 65, 73, "Dark green pods streaked purple; heat-tolerant for hot climates")
        v(db, "Pole Beans", "Romano Pole", 66, 75, "Italian heirloom with broad flat meaty pods; rich nutty flavor")
        v(db, "Pole Beans", "Fortex", 60, 70, "French filet type with slender stringless 11-inch pods; tender at large sizes")

        // ── Lima Beans ──
        v(db, "Lima Beans", "Henderson Bush", 60, 70, "Most popular dwarf baby lima; compact with small creamy white beans")
        v(db, "Lima Beans", "Fordhook 242", 70, 80, "AAS winner producing large plump green beans on heat-tolerant plants")
        v(db, "Lima Beans", "Jackson Wonder", 65, 75, "Heirloom bush lima with speckled beans; drought-tolerant in the South")
        v(db, "Lima Beans", "Eastland", 68, 75, "Early baby bush lima with uniform pods; downy mildew resistance")
        v(db, "Lima Beans", "Christmas Lima", 85, 100, "Pole heirloom with maroon-speckled white beans; chestnut-like flavor")

        // ── Southern Peas ──
        v(db, "Southern Peas", "California Blackeye No. 5", 70, 80, "Dominant blackeye variety with heavy yields; vigorous bush")
        v(db, "Southern Peas", "Pinkeye Purple Hull", 60, 70, "Popular purple-hull type; easy to shell, turns purple when ready")
        v(db, "Southern Peas", "Mississippi Silver", 64, 75, "Silver-hulled crowder; vigorous semi-vining with heavy yields")
        v(db, "Southern Peas", "Zipper Cream", 65, 75, "Cream pea with zipper-like seam for easy shelling; sweet mild flavor")
        v(db, "Southern Peas", "Queen Anne", 60, 68, "Compact blackeye bush producing 7-9 inch pods; earlier maturity")

        // ── Peas ──
        v(db, "Peas", "Alaska", 55, 60, "Earliest shelling pea; 24-inch vines with 2-3 inch pods; great for canning")
        v(db, "Peas", "Little Marvel", 55, 63, "Dwarf 18-inch plants with plump 3-inch pods of sweet peas; no staking")
        v(db, "Peas", "Thomas Laxton", 58, 65, "Heirloom with 4-inch pods of 7-8 large sweet peas; vigorous 30-inch vines")
        v(db, "Peas", "Green Arrow", 68, 72, "24-inch plants with 4-5 inch pods of 9-11 peas; mildew resistant")
        v(db, "Peas", "Lincoln", 60, 70, "English heirloom also called Homesteader; the sweetest shelling pea")
        v(db, "Peas", "Wando", 65, 70, "Heat-tolerant heirloom; handles warm weather better than most peas")

        // ── Snap Peas ──
        v(db, "Snap Peas", "Sugar Snap", 58, 65, "The original snap pea with tall 5-6 ft vines; crisp sweet edible pods")
        v(db, "Snap Peas", "Sugar Ann", 52, 58, "AAS-winning dwarf on 24-30 inch vines; earliest snap pea")
        v(db, "Snap Peas", "Super Sugar Snap", 60, 66, "Improved Sugar Snap with better disease resistance; shorter vines")
        v(db, "Snap Peas", "Cascadia", 58, 62, "Short 32-inch vines; plump stringless pods with sweet flavor")
        v(db, "Snap Peas", "Sugar Bob", 53, 58, "Compact 24-inch dwarf with thick-walled crunchy pods; no trellising")

        // ── Snow Peas ──
        v(db, "Snow Peas", "Oregon Sugar Pod II", 60, 68, "Bush 28-inch plants; flat tender stringless pods, disease resistant")
        v(db, "Snow Peas", "Mammoth Melting Sugar", 65, 75, "Tall 4-5 ft vines with large 4-5 inch stringless tender pods")
        v(db, "Snow Peas", "Snowbird", 55, 60, "Extra-early dwarf 18-inch plants with 3-inch pods in clusters")
        v(db, "Snow Peas", "Oregon Giant", 65, 75, "Large 4.5-inch wide sweet pods on 3 ft vines; great disease resistance")
        v(db, "Snow Peas", "Golden Sweet", 60, 70, "Unique heirloom with lemon-yellow pods and purple flowers")

        // ── Lettuce (leaf) ──
        v(db, "Lettuce (leaf)", "Salad Bowl", 40, 50, "Classic oak-leaf type; light green tender leaves, slow to bolt")
        v(db, "Lettuce (leaf)", "Red Sails", 40, 50, "AAS winner with ruffled bronze-red leaves; very slow to bolt")
        v(db, "Lettuce (leaf)", "Black-Seeded Simpson", 42, 55, "Heirloom with large crinkled light green leaves; fast growing")
        v(db, "Lettuce (leaf)", "Oak Leaf", 40, 50, "Deeply lobed oak-shaped leaves; heat tolerant with sweet mild flavor")
        v(db, "Lettuce (leaf)", "Lollo Rossa", 40, 55, "Italian frilly red-tipped leaves; beautiful in salads, slow to bolt")
        v(db, "Lettuce (leaf)", "Red Salad Bowl", 40, 50, "Red version with deeply lobed bronze leaves; heat tolerant")
        v(db, "Lettuce (leaf)", "Deer Tongue", 46, 55, "Amish heirloom with pointed leaves; heat tolerant, sweet buttery flavor")

        // ── Lettuce (head) ──
        v(db, "Lettuce (head)", "Great Lakes", 75, 85, "Large firm crisphead; heat tolerant for a head lettuce")
        v(db, "Lettuce (head)", "Ithaca", 60, 72, "Early crisphead that tolerates heat; reliable in variable weather")
        v(db, "Lettuce (head)", "Crispino", 57, 65, "Very early crisphead hybrid with excellent heat tolerance")
        v(db, "Lettuce (head)", "Nevada", 60, 70, "Summer crisphead with good bolt resistance; large dense heads")
        v(db, "Lettuce (head)", "Webb's Wonderful", 72, 85, "English heirloom crisphead with large firm heads; sweet mild flavor")

        // ── Romaine Lettuce ──
        v(db, "Romaine Lettuce", "Parris Island Cos", 60, 68, "Classic 10-inch upright romaine; mosaic virus resistant")
        v(db, "Romaine Lettuce", "Little Gem", 50, 60, "Mini romaine forming compact 6-inch heads; sweet crunchy hearts")
        v(db, "Romaine Lettuce", "Jericho", 60, 70, "Israeli variety bred for desert heat; exceptional bolt resistance")
        v(db, "Romaine Lettuce", "Winter Density", 55, 65, "Cos-butterhead cross with small dense heads; cold hardy and sweet")
        v(db, "Romaine Lettuce", "Rouge d'Hiver", 55, 65, "French heirloom with red-bronze outer leaves; cold hardy")

        // ── Butter Lettuce ──
        v(db, "Butter Lettuce", "Buttercrunch", 55, 65, "AAS-winning bibb type; heat tolerant, forms small dense sweet heads")
        v(db, "Butter Lettuce", "Tom Thumb", 48, 60, "Tiny tennis-ball size bibb heads; perfect for containers")
        v(db, "Butter Lettuce", "Bibb", 55, 70, "The original butterhead with small silky heads; classic gourmet lettuce")
        v(db, "Butter Lettuce", "Marvel of Four Seasons", 55, 65, "French heirloom with red-tinged outer leaves and creamy hearts")
        v(db, "Butter Lettuce", "Nancy", 48, 58, "European greenhouse type with bright green soft heads; excellent flavor")

        // ── Iceberg Lettuce ──
        v(db, "Iceberg Lettuce", "Ithaca", 60, 72, "Adaptable crisphead; good heat tolerance and tip burn resistance")
        v(db, "Iceberg Lettuce", "Summertime", 70, 85, "Heat-adapted with large firm heads; slower to bolt in warm weather")
        v(db, "Iceberg Lettuce", "Salinas", 70, 80, "Commercial standard with large dense heads; crisp and mild")
        v(db, "Iceberg Lettuce", "Great Lakes 118", 75, 85, "Improved strain with firmer denser heads; heat tolerant")
        v(db, "Iceberg Lettuce", "Crispino", 57, 65, "Early mini iceberg with very firm heads; bolt resistant")

        // ── Arugula ──
        v(db, "Arugula", "Astro", 25, 35, "Fast-growing smooth-leaf with milder flavor; earliest variety")
        v(db, "Arugula", "Roquette", 30, 40, "Standard Italian arugula with deeply lobed peppery leaves")
        v(db, "Arugula", "Sylvetta", 35, 50, "Wild perennial with fine narrow leaves; more intense, slower bolting")
        v(db, "Arugula", "Dragon's Tongue", 30, 40, "Purple-veined leaves with mild peppery flavor; heat tolerant")
        v(db, "Arugula", "Wasabi", 30, 40, "Spicy wasabi-like flavor with broad green leaves")

        // ── Spinach ──
        v(db, "Spinach", "Bloomsdale Long Standing", 40, 50, "Classic heirloom with thick crinkled dark green leaves; slow to bolt")
        v(db, "Spinach", "Giant Noble", 42, 48, "Large smooth leaves; great for canning and freezing with heavy yields")
        v(db, "Spinach", "Space", 38, 45, "Hybrid with smooth upright leaves; easy to clean, slow to bolt")
        v(db, "Spinach", "Tyee", 39, 45, "Semi-savoy hybrid; excellent bolt resistance and downy mildew tolerance")
        v(db, "Spinach", "Regiment", 37, 42, "Very fast smooth-leaf hybrid; baby leaf harvest in 4 weeks")

        // ── Kale ──
        v(db, "Kale", "Lacinato", 55, 65, "Italian dinosaur kale with puckered strap-like leaves; sweetens after frost")
        v(db, "Kale", "Red Russian", 50, 65, "Flat grey-green leaves with purple veins; tender, mild, cold hardy to 10F")
        v(db, "Kale", "Winterbor", 55, 65, "Curly blue-green hybrid; extremely cold hardy to -10F")
        v(db, "Kale", "Dwarf Blue Curled Scotch", 55, 65, "Compact 12-15 inch plants; great for containers and small spaces")
        v(db, "Kale", "Redbor", 55, 65, "Deep purple-red curly leaves; beautiful ornamental and very tasty")

        // ── Swiss Chard ──
        v(db, "Swiss Chard", "Fordhook Giant", 55, 60, "Classic white-stemmed with large crinkled dark green leaves; heat tolerant")
        v(db, "Swiss Chard", "Bright Lights", 55, 60, "AAS winner with rainbow stems in gold, pink, orange, red, purple")
        v(db, "Swiss Chard", "Ruby Red", 55, 60, "Deep crimson stems with dark green crinkled leaves; ornamental")
        v(db, "Swiss Chard", "Rhubarb", 55, 60, "Dark red stems with savoyed leaves; heirloom with rich color")
        v(db, "Swiss Chard", "Lucullus", 50, 60, "Light green stems with pale crinkled leaves; mild delicate flavor")

        // ── Collard Greens ──
        v(db, "Collard Greens", "Georgia Southern", 60, 80, "Classic tall heirloom with large blue-green leaves; the standard Southern collard")
        v(db, "Collard Greens", "Vates", 60, 75, "Compact plants with dark green smooth leaves; more cold hardy")
        v(db, "Collard Greens", "Morris Heading", 60, 80, "Forms a loose head like cabbage; tender wavy leaves with mild flavor")
        v(db, "Collard Greens", "Champion", 60, 75, "Compact Vates-type with darker leaves; improved bolt resistance")
        v(db, "Collard Greens", "Top Bunch", 50, 65, "Short-stemmed with clustered dark green leaves; easy to harvest")

        // ── Mustard Greens ──
        v(db, "Mustard Greens", "Southern Giant Curled", 40, 50, "Large bright green frilled leaves with mild mustard bite")
        v(db, "Mustard Greens", "Red Giant", 40, 50, "Dark red-purple leaves with spicy wasabi-like flavor; beautiful in salads")
        v(db, "Mustard Greens", "Tendergreen", 35, 45, "Smooth leaves with mild spinach-like flavor; also called mustard spinach")
        v(db, "Mustard Greens", "Florida Broadleaf", 40, 50, "Large smooth light green leaves with mild peppery taste; slow to bolt")
        v(db, "Mustard Greens", "Green Wave", 40, 50, "Frilly bright green leaves with moderately spicy bite; very cold tolerant")

        // ── Broccoli ──
        v(db, "Broccoli", "Calabrese", 60, 75, "Italian heirloom; produces abundant side shoots after main head harvest")
        v(db, "Broccoli", "De Cicco", 48, 65, "Early heirloom with prolific side shoots; great for extended harvest")
        v(db, "Broccoli", "Waltham 29", 70, 80, "Compact cold-hardy with dense blue-green heads; bred for fall crops")
        v(db, "Broccoli", "Green Magic", 60, 70, "Hybrid with uniform domed blue-green heads; heat tolerant")
        v(db, "Broccoli", "Belstar", 65, 75, "Organic favorite with smooth domed heads; good heat tolerance")
        v(db, "Broccoli", "Marathon", 68, 80, "Long-standing hybrid with dense heads; excellent cold tolerance")

        // ── Cauliflower ──
        v(db, "Cauliflower", "Snowball", 60, 75, "Self-blanching white heirloom; tight curds, reliable in cool weather")
        v(db, "Cauliflower", "Amazing", 68, 75, "Hybrid with large 2-3 lb pure white self-wrapping heads")
        v(db, "Cauliflower", "Graffiti", 70, 80, "Stunning bright purple curds that hold color when cooked lightly")
        v(db, "Cauliflower", "Cheddar", 68, 75, "Orange curds rich in beta-carotene; color deepens when cooked")
        v(db, "Cauliflower", "Romanesco", 75, 85, "Fractal lime-green spiraling curds; nutty sweet, stunning appearance")
        v(db, "Cauliflower", "Snow Crown", 50, 60, "Very early hybrid with smooth white heads; forgiving of weather swings")

        // ── Cabbage ──
        v(db, "Cabbage", "Early Jersey Wakefield", 60, 70, "Pointed-head heirloom; the earliest heading cabbage")
        v(db, "Cabbage", "Golden Acre", 58, 65, "Early round 3-4 lb heads; compact for close spacing")
        v(db, "Cabbage", "Copenhagen Market", 65, 80, "Round firm 3-4 lb heads; good for kraut and fresh eating")
        v(db, "Cabbage", "Savoy Perfection", 85, 95, "Crinkled blue-green leaves with tender sweet flavor; great for wraps")
        v(db, "Cabbage", "Red Acre", 72, 80, "Compact 3-4 lb red-purple heads; good for fresh eating and pickling")
        v(db, "Cabbage", "Mammoth Red Rock", 90, 100, "Large 5-8 lb deep red heads; excellent storage variety")

        // ── Brussels Sprouts ──
        v(db, "Brussels Sprouts", "Long Island Improved", 90, 110, "Classic heirloom with dark green sprouts on 24-inch stalks")
        v(db, "Brussels Sprouts", "Catskill", 90, 100, "Dwarf 20-inch plants with medium-large sprouts; easier harvesting")
        v(db, "Brussels Sprouts", "Jade Cross", 90, 100, "Hybrid with uniform blue-green sprouts; disease resistant")
        v(db, "Brussels Sprouts", "Nautic", 95, 105, "Tall hybrid with smooth round dark green sprouts; great flavor after frost")
        v(db, "Brussels Sprouts", "Dagan", 100, 110, "Late hybrid with large firm sprouts; holds well in cold weather")

        // ── Radish ──
        v(db, "Radish", "Cherry Belle", 22, 28, "Classic round bright red; crisp white flesh with mild peppery bite")
        v(db, "Radish", "French Breakfast", 23, 28, "Oblong red with white tip; mild crisp flesh, beautiful")
        v(db, "Radish", "Watermelon", 50, 60, "Green-white exterior with bright pink interior; mild sweet")
        v(db, "Radish", "Black Spanish Round", 55, 60, "Black-skinned winter radish; crisp white spicy flesh, stores well")
        v(db, "Radish", "Easter Egg", 25, 30, "Mix of red, purple, pink, white colors; fun with mild flavor")
        v(db, "Radish", "Champion", 25, 28, "Round bright scarlet; resists pithiness longer than most")

        // ── Carrot ──
        v(db, "Carrot", "Danvers 126", 65, 78, "Classic 6-7 inch half-long; tolerates heavy soil and stores well")
        v(db, "Carrot", "Scarlet Nantes", 65, 75, "Cylindrical 6-7 inch French type; sweet crisp and virtually coreless")
        v(db, "Carrot", "Imperator 58", 70, 80, "Long slender 8-9 inch; classic shape with sweet flavor")
        v(db, "Carrot", "Little Finger", 55, 65, "Tiny 3-4 inch baby Nantes; perfect for containers and shallow soil")
        v(db, "Carrot", "Cosmic Purple", 60, 73, "Purple exterior with orange interior; unique color, sweet flavor")
        v(db, "Carrot", "Chantenay Red Core", 65, 75, "Short thick 5-inch tapered; tolerates heavy clay soil")

        // ── Beet ──
        v(db, "Beet", "Detroit Dark Red", 55, 65, "Classic round deep red; sweet fine-grained flesh, the standard beet")
        v(db, "Beet", "Chioggia", 50, 60, "Italian heirloom with candy-stripe pink and white rings; mild sweet")
        v(db, "Beet", "Golden", 50, 65, "Yellow-orange flesh that doesn't stain; sweet mild flavor")
        v(db, "Beet", "Bull's Blood", 50, 60, "Deep burgundy foliage for baby greens; dark red roots with good flavor")
        v(db, "Beet", "Cylindra", 55, 65, "Unique long cylindrical shape; Danish heirloom, dark red sweet flesh")
        v(db, "Beet", "Early Wonder", 48, 58, "Fast growing flattened globe; dual-purpose for roots and greens")

        // ── Turnip ──
        v(db, "Turnip", "Purple Top White Globe", 45, 60, "Classic purple and white; mild sweet 3-4 inch roots")
        v(db, "Turnip", "Hakurei", 35, 45, "Japanese salad turnip; sweet mild, can be eaten raw like an apple")
        v(db, "Turnip", "Golden Globe", 45, 60, "Yellow-fleshed mild sweet; great for turnip skeptics")
        v(db, "Turnip", "Shogoin", 40, 50, "Japanese variety grown primarily for large mild greens")
        v(db, "Turnip", "Scarlet Queen", 40, 50, "Bright red-skinned with white flesh; beautiful with mild sweet flavor")

        // ── Onion ──
        v(db, "Onion", "Yellow Sweet Spanish", 100, 120, "Large mild sweet yellow globe; classic all-purpose onion")
        v(db, "Onion", "Walla Walla", 90, 115, "Famous Pacific Northwest sweet; very mild jumbo bulbs")
        v(db, "Onion", "Red Wing", 100, 118, "Large dark red globe; good storage onion with mild sweet flavor")
        v(db, "Onion", "Candy", 85, 100, "Mid-size sweet yellow hybrid; intermediate day length, works in more zones")
        v(db, "Onion", "Copra", 104, 118, "Best storage onion with firm yellow globes; keeps 10-12 months")
        v(db, "Onion", "Texas 1015", 90, 110, "Texas supersweet short-day jumbo; very mild for Southern zones")

        // ── Garlic ──
        v(db, "Garlic", "Music", 90, 120, "Large hardneck with 4-5 fat easy-peel cloves; robust spicy, cold hardy")
        v(db, "Garlic", "California Early", 90, 120, "Classic softneck with 12-16 cloves; mild flavor, braids well")
        v(db, "Garlic", "Georgian Fire", 90, 120, "Porcelain hardneck with 5-6 large cloves; very hot raw, mellows cooked")
        v(db, "Garlic", "Elephant", 90, 120, "Mild giant cloves actually a leek relative; very mild garlic flavor")
        v(db, "Garlic", "Inchelium Red", 90, 120, "Softneck with complex mild-to-hot flavor; stores 6-9 months")
        v(db, "Garlic", "Spanish Roja", 90, 120, "Heirloom rocambole hardneck; true rich garlic flavor, easy-peel cloves")

        // ── Potato ──
        v(db, "Potato", "Yukon Gold", 70, 90, "Golden flesh all-purpose; buttery flavor, great mashed or roasted")
        v(db, "Potato", "Russet Burbank", 80, 120, "Classic large baking potato; dry fluffy flesh, the Idaho standard")
        v(db, "Potato", "Red Pontiac", 80, 100, "Red-skinned waxy with white flesh; great boiled or in salads")
        v(db, "Potato", "Kennebec", 80, 100, "Large white all-purpose; excellent yields, great for frying and baking")
        v(db, "Potato", "Purple Majesty", 75, 90, "Deep purple skin and flesh; high in antioxidants, holds color when cooked")
        v(db, "Potato", "German Butterball", 90, 120, "Golden-skinned with rich buttery yellow flesh; good storage")

        // ── Kohlrabi ──
        v(db, "Kohlrabi", "Early White Vienna", 50, 60, "Classic pale green bulb; mild broccoli-stem flavor, best at 2-3 inches")
        v(db, "Kohlrabi", "Early Purple Vienna", 50, 60, "Purple-skinned with white flesh; same flavor, beautiful color")
        v(db, "Kohlrabi", "Winner", 45, 55, "Hybrid that stays tender at large size; very slow to get woody")
        v(db, "Kohlrabi", "Gigante", 70, 80, "Czech heirloom growing to 10 inches without getting woody")
        v(db, "Kohlrabi", "Kolibri", 45, 55, "Purple hybrid with crisp sweet white flesh; uniform and slow to bolt")

        // ── Green Onion ──
        v(db, "Green Onion", "Evergreen White Bunching", 50, 65, "Hardy perennial bunching type; divides into clusters, cold hardy")
        v(db, "Green Onion", "Tokyo Long White", 50, 65, "Classic Asian green onion with long white single stems; mild sweet")
        v(db, "Green Onion", "Red Beard", 55, 65, "Stunning burgundy-red stalks with mild flavor; beautiful garnish")
        v(db, "Green Onion", "Parade", 50, 60, "Dutch hybrid with tall straight single-stem stalks; uniform")
        v(db, "Green Onion", "Guardsman", 50, 60, "Upright uniform bunching type; good heat and bolt tolerance")

        // ── Shallot ──
        v(db, "Shallot", "French Red", 90, 120, "Classic copper-skinned with pink-tinged flesh; the gourmet standard")
        v(db, "Shallot", "Ambition", 90, 100, "Hybrid seed-grown with uniform copper-red bulbs; easier to grow")
        v(db, "Shallot", "Dutch Yellow", 90, 120, "Golden-skinned with yellow flesh; mild sweet, multiplies well")
        v(db, "Shallot", "Conservor", 90, 100, "Long teardrop-shaped French type; reddish skin with subtle flavor")
        v(db, "Shallot", "Prisma", 90, 100, "Deep red hybrid with strong flavor; uniform oval shape")

        // ── Leek ──
        v(db, "Leek", "American Flag", 100, 130, "Classic heirloom with long white shanks; mild onion flavor")
        v(db, "Leek", "King Richard", 75, 90, "Early variety with extra-long slender shanks; fast growing")
        v(db, "Leek", "Giant Musselburgh", 100, 150, "Scottish heirloom with thick shanks; very cold hardy")
        v(db, "Leek", "Lancelot", 75, 95, "Early hybrid with uniform white shanks; good disease resistance")
        v(db, "Leek", "Bandit", 100, 120, "Very cold hardy winter leek; thick short blue-green shanks")

        // ── Celery ──
        v(db, "Celery", "Tall Utah", 120, 140, "Classic crisp stalks; the standard green celery for home gardens")
        v(db, "Celery", "Golden Self-Blanching", 80, 100, "Naturally pale golden stalks needing no blanching; mild sweet")
        v(db, "Celery", "Ventura", 80, 100, "Long thick smooth dark green stalks; good bolt resistance")
        v(db, "Celery", "Conquistador", 80, 95, "Early hybrid with thick dark green stalks; good for variable conditions")
        v(db, "Celery", "Tango", 80, 90, "Compact hybrid; earliest celery for short-season gardens")

        // ── Asparagus ──
        v(db, "Asparagus", "Mary Washington", 730, 1095, "Classic heirloom with green spears and purple tips; rust resistant")
        v(db, "Asparagus", "Jersey Knight", 730, 1095, "All-male hybrid with thick green spears; higher yields")
        v(db, "Asparagus", "Purple Passion", 730, 1095, "Large purple spears sweeter than green; 20% more sugar")
        v(db, "Asparagus", "Millennium", 730, 1095, "Canadian bred for cold climates; vigorous large green spears")
        v(db, "Asparagus", "Atlas", 730, 1095, "All-male hybrid adapted to warm climates; thick green spears")

        // ── Artichoke ──
        v(db, "Artichoke", "Green Globe", 120, 180, "Classic large globe; perennial in mild climates")
        v(db, "Artichoke", "Imperial Star", 85, 100, "Bred to produce first year from seed; reliable annual production")
        v(db, "Artichoke", "Purple of Romagna", 120, 150, "Italian heirloom with deep purple buds; rich nutty flavor")
        v(db, "Artichoke", "Violetto", 120, 160, "Purple Italian with elongated buds; ornamental and edible")
        v(db, "Artichoke", "Big Heart", 100, 120, "Thornless with large meaty hearts; mild sweet flavor")

        // ── Yam ──
        v(db, "Yam", "Water Yam", 150, 240, "Purple-fleshed variety; moist starchy tubers popular in tropics")
        v(db, "Yam", "White Yam", 150, 240, "Most common true yam with white starchy flesh; West African staple")
        v(db, "Yam", "Yellow Guinea", 180, 240, "Yellow-fleshed tropical; firm texture, popular in Caribbean cooking")
        v(db, "Yam", "Purple Yam", 150, 210, "Vibrant purple flesh (ube); used in Filipino desserts, sweet flavor")
        v(db, "Yam", "Chinese Yam", 150, 200, "Long cylindrical with crisp white flesh; can be eaten raw, cold hardy")

        // ═══════════════════════════════════════════════════
        //  HERBS
        // ═══════════════════════════════════════════════════

        // ── Basil ──
        v(db, "Basil", "Genovese", 50, 68, "Classic Italian large-leaf; the standard for pesto with sweet intense aroma")
        v(db, "Basil", "Sweet Basil", 50, 75, "All-purpose medium-leaf; versatile for cooking")
        v(db, "Basil", "Thai", 55, 75, "Narrow pointed leaves with spicy anise-clove flavor; heat tolerant")
        v(db, "Basil", "Purple Ruffles", 60, 75, "AAS winner with ruffled deep purple leaves; stunning garnish")
        v(db, "Basil", "Lemon", 50, 65, "Bright citrus aroma; great in teas, fish dishes, and desserts")
        v(db, "Basil", "Cinnamon", 50, 68, "Spicy cinnamon-scented; unique flavor for Mexican and Asian dishes")
        v(db, "Basil", "Holy (Tulsi)", 55, 75, "Sacred Indian basil with peppery clove flavor; medicinal tea herb")
        v(db, "Basil", "Lettuce Leaf", 50, 75, "Huge 4-5 inch crinkled leaves; mild sweet flavor, great for wraps")

        // ── Cilantro ──
        v(db, "Cilantro", "Santo", 45, 55, "Slow bolt with large aromatic leaves; one of the best for leaf production")
        v(db, "Cilantro", "Slow Bolt", 45, 60, "Bred for extended leaf harvest; goes to seed later than standard")
        v(db, "Cilantro", "Calypso", 45, 55, "Slowest bolting variety; large leaves with maximum harvest window")
        v(db, "Cilantro", "Long Standing", 50, 65, "Extended leaf production; good for warm-season growing")
        v(db, "Cilantro", "Delfino", 45, 60, "Feathery fern-like leaves; ornamental look, very slow to bolt")

        // ── Dill ──
        v(db, "Dill", "Bouquet", 40, 55, "Classic large-headed dill for pickling; big seed heads")
        v(db, "Dill", "Fernleaf", 40, 60, "Dwarf 18-inch AAS winner; perfect for containers, slow bolting")
        v(db, "Dill", "Mammoth Long Island", 40, 55, "Tall plants with large seed heads; the traditional pickling dill")
        v(db, "Dill", "Hera", 40, 55, "Compact multi-branching with extended leaf harvest; slow to bolt")
        v(db, "Dill", "Dukat", 40, 55, "Scandinavian variety with extra-fine sweet foliage; high oil content")

        // ── Parsley ──
        v(db, "Parsley", "Italian Flat Leaf", 70, 90, "Flat dark green leaves with bold flavor; preferred for cooking")
        v(db, "Parsley", "Moss Curled", 70, 85, "Tightly curled decorative dark green leaves; classic garnish")
        v(db, "Parsley", "Giant of Italy", 70, 90, "Large flat leaves on tall stems; robust flavor and big harvests")
        v(db, "Parsley", "Forest Green", 70, 85, "Dark green densely curled compact variety; cold hardy")
        v(db, "Parsley", "Titan", 70, 85, "Vigorous flat-leaf with large dark green leaves; strong bolt resistance")

        // ── Oregano ──
        v(db, "Oregano", "Greek", 80, 90, "Compact with small pungent leaves; the most flavorful variety")
        v(db, "Oregano", "Italian", 80, 90, "Larger leaves with milder flavor; good all-purpose cooking oregano")
        v(db, "Oregano", "Hot and Spicy", 80, 90, "Extra-pungent with intense spicy flavor; small leaves packed with oil")
        v(db, "Oregano", "Kent Beauty", 80, 90, "Ornamental trailing with hop-like pink bracts; edible but mainly decorative")
        v(db, "Oregano", "Syrian", 80, 90, "Broader leaves with sweet earthy aroma; key ingredient in za'atar")

        // ── Thyme ──
        v(db, "Thyme", "English", 85, 95, "Classic culinary with small grey-green leaves; strong flavor")
        v(db, "Thyme", "French", 85, 95, "Narrow grey-green leaves with sweeter more subtle flavor")
        v(db, "Thyme", "Lemon", 85, 95, "Bright citrus-scented; wonderful with fish, chicken, and in teas")
        v(db, "Thyme", "Creeping Red", 85, 95, "Low-growing ground cover with tiny pink flowers; fragrant")
        v(db, "Thyme", "Silver", 85, 95, "Variegated white and green leaves; beautiful ornamental edible")

        // ── Rosemary ──
        v(db, "Rosemary", "Tuscan Blue", 80, 100, "Tall upright 4-6 ft with broad dark leaves; strong pine flavor")
        v(db, "Rosemary", "Arp", 80, 100, "Most cold-hardy variety surviving to zone 6; lemon-pine flavor")
        v(db, "Rosemary", "Hill Hardy", 80, 100, "Cold hardy to zone 7 with upright bushy habit; strong flavor")
        v(db, "Rosemary", "Prostrate", 80, 100, "Trailing ground-cover type; great for walls and containers")
        v(db, "Rosemary", "Salem", 80, 100, "Hardy upright with dark green narrow leaves; vigorous grower")

        // ── Sage ──
        v(db, "Sage", "Common Garden", 75, 85, "Classic grey-green fuzzy leaves; the standard culinary sage")
        v(db, "Sage", "Purple", 75, 85, "Purple-tinged leaves with same flavor; beautiful ornamental edible")
        v(db, "Sage", "Berggarten", 75, 85, "Broad round leaves that rarely flower; stays compact and bushy")
        v(db, "Sage", "Pineapple", 75, 85, "Tender perennial with fruity pineapple scent; not cold hardy")
        v(db, "Sage", "Tricolor", 75, 85, "Variegated green, white, and pink leaves; less hardy than common")

        // ── Mint ──
        v(db, "Mint", "Spearmint", 60, 90, "Classic sweet mint for mojitos and teas; the most widely grown mint")
        v(db, "Mint", "Peppermint", 60, 90, "Strong menthol-rich; darker leaves, best for teas and medicinal use")
        v(db, "Mint", "Chocolate", 60, 90, "Dark stems with chocolate-mint aroma; popular for desserts")
        v(db, "Mint", "Apple", 60, 90, "Fuzzy rounded leaves with fruity apple-mint scent; mild flavor")
        v(db, "Mint", "Mojito", 60, 90, "Cuban mint with large soft leaves; mild sweet, the authentic mojito mint")

        // ── Chives ──
        v(db, "Chives", "Common", 60, 90, "Standard onion chives; purple pom-pom flowers are also edible")
        v(db, "Chives", "Garlic (Chinese)", 60, 90, "Flat broad leaves with mild garlic flavor; white star-shaped flowers")
        v(db, "Chives", "Fine Leaf", 60, 90, "Extra-thin delicate leaves; refined appearance for garnishing")
        v(db, "Chives", "Staro", 60, 90, "Vigorous thick-leaved variety; upright dark green, good for production")
        v(db, "Chives", "Purly", 60, 90, "Compact with abundant purple flowers; ornamental and edible")

        // ── Lemongrass ──
        v(db, "Lemongrass", "East Indian", 90, 120, "Most common culinary type; strong lemon flavor for Thai cooking")
        v(db, "Lemongrass", "West Indian", 90, 120, "Broader leaves with milder citrus; used in Caribbean dishes")
        v(db, "Lemongrass", "Malabar", 90, 120, "Compact with intense lemon-ginger aroma; good for containers")

        // ── Lavender ──
        v(db, "Lavender", "Munstead", 90, 200, "Compact English lavender 12-18 inch; very cold hardy and fragrant")
        v(db, "Lavender", "Hidcote", 90, 200, "Dark purple flowers on compact plants; the most popular English lavender")
        v(db, "Lavender", "Grosso", 90, 200, "Lavandin hybrid with long purple spikes; highest oil content")
        v(db, "Lavender", "Provence", 90, 200, "French lavandin with light purple flowers; classic fragrance")
        v(db, "Lavender", "Lady", 90, 200, "AAS winner that blooms first year from seed; compact 10-inch plants")

        // ═══════════════════════════════════════════════════
        //  FRUITS
        // ═══════════════════════════════════════════════════

        // ── Strawberry ──
        v(db, "Strawberry", "Chandler", 60, 90, "Large sweet June-bearing; excellent flavor, warm climate standard")
        v(db, "Strawberry", "Ozark Beauty", 60, 90, "Everbearing with large sweet red berries; spring through fall")
        v(db, "Strawberry", "Seascape", 60, 90, "Day-neutral producing all season; large sweet with high yields")
        v(db, "Strawberry", "Earliglow", 60, 75, "Earliest June-bearer; intense sweet flavor, outstanding taste")
        v(db, "Strawberry", "Jewel", 60, 90, "Late June-bearing with large firm sweet berries; great for freezing")
        v(db, "Strawberry", "Albion", 60, 90, "Day-neutral with long conical firm berries; heavy spring through fall")
        v(db, "Strawberry", "Honeoye", 60, 80, "Early June-bearing with large bright red berries; very productive")

        // ── Blueberry ──
        v(db, "Blueberry", "Bluecrop", 730, 1095, "Most widely planted highbush; reliable heavy yields")
        v(db, "Blueberry", "Patriot", 730, 1095, "Very cold hardy to -30F; large sweet berries, tolerates wet feet")
        v(db, "Blueberry", "Duke", 730, 1095, "Early-season with large firm mild sweet berries; consistent yields")
        v(db, "Blueberry", "Jersey", 730, 1095, "Late-season heirloom with small very sweet berries; extremely productive")
        v(db, "Blueberry", "Sunshine Blue", 730, 1095, "Compact 3-4 ft Southern highbush for containers; self-fertile")
        v(db, "Blueberry", "Blueray", 730, 1095, "Mid-season with very large sweet berries; outstanding flavor")

        // ── Raspberry ──
        v(db, "Raspberry", "Heritage", 365, 730, "Most popular fall-bearing red; two crops per year, widely adapted")
        v(db, "Raspberry", "Caroline", 365, 730, "Large sweet fall-bearing; excellent flavor and disease resistance")
        v(db, "Raspberry", "Jewel", 365, 730, "Premier black raspberry; large sweet glossy berries, productive")
        v(db, "Raspberry", "Boyne", 365, 730, "Extra cold-hardy summer-bearing; survives to -40F")
        v(db, "Raspberry", "Latham", 365, 730, "Classic summer-bearing red; very cold hardy, reliable heirloom")
        v(db, "Raspberry", "Anne", 365, 730, "Fall-bearing yellow; large pale gold sweet berries with mild flavor")

        // ── Blackberry ──
        v(db, "Blackberry", "Triple Crown", 365, 730, "Thornless semi-erect with very large sweet berries; up to 30 lbs per plant")
        v(db, "Blackberry", "Navaho", 365, 730, "Thornless erect with medium sweet firm berries")
        v(db, "Blackberry", "Chester", 365, 730, "Thornless trailing with large sweet-tart berries; very cold hardy")
        v(db, "Blackberry", "Apache", 365, 730, "Thornless erect with very large firm sweet berries; easy to harvest")
        v(db, "Blackberry", "Ouachita", 365, 730, "Thornless erect with excellent sweet flavor; heat tolerant")
        v(db, "Blackberry", "Prime-Ark Freedom", 365, 730, "Thornless primocane-bearing; produces on first-year canes")

        // ── Fig ──
        v(db, "Fig", "Brown Turkey", 365, 730, "Most widely adapted; sweet brown-purple fruits, cold hardiest fig")
        v(db, "Fig", "Celeste", 365, 730, "Small very sweet purple-brown figs; cold hardy and disease resistant")
        v(db, "Fig", "Chicago Hardy", 365, 730, "Cold hardy to zone 6; dies back and regrows in cold winters")
        v(db, "Fig", "Black Mission", 365, 730, "Classic dark purple-black figs with sweet jammy flesh")
        v(db, "Fig", "Kadota", 365, 730, "Light green skin with sweet amber flesh; excellent for drying")
        v(db, "Fig", "LSU Purple", 365, 730, "Louisiana-bred; good disease resistance for humid climates")

        // ── Grape ──
        v(db, "Grape", "Concord", 365, 1095, "Classic American blue-black; bold sweet flavor for jelly and juice")
        v(db, "Grape", "Thompson Seedless", 365, 1095, "Seedless green table and raisin grape; needs hot summers")
        v(db, "Grape", "Niagara", 365, 1095, "White American; sweet foxy flavor, cold hardy companion to Concord")
        v(db, "Grape", "Catawba", 365, 1095, "Pink-red American for wine and juice; sweet spicy, late ripening")
        v(db, "Grape", "Mars", 365, 1095, "Seedless blue-black table grape; cold hardy and disease resistant")
        v(db, "Grape", "Reliance", 365, 1095, "Seedless red table grape; very sweet, cold hardy to -25F")

        // ── Passion Fruit ──
        v(db, "Passion Fruit", "Frederick", 365, 545, "Purple-fruited with sweet-tart aromatic pulp; most common type")
        v(db, "Passion Fruit", "Possum Purple", 365, 545, "Cold-tolerant purple variety hardy to 25F")
        v(db, "Passion Fruit", "Panama Red", 365, 545, "Red-purple fruits larger than standard; sweet aromatic pulp")
        v(db, "Passion Fruit", "Yellow Giant", 365, 545, "Large yellow fruits with tangy juice; great for drinks")

        // ── Papaya ──
        v(db, "Papaya", "Red Lady", 270, 365, "Bisexual dwarf producing sweet red-fleshed fruits; disease resistant")
        v(db, "Papaya", "Maradol", 270, 365, "Large 3-5 lb fruits with sweet salmon-red flesh; Cuban variety")
        v(db, "Papaya", "Solo", 270, 330, "Hawaiian type with small sweet yellow-orange flesh; pear-shaped")
        v(db, "Papaya", "Tainung No. 2", 270, 365, "Taiwanese hybrid with large red-orange flesh; high yielding")
        v(db, "Papaya", "Sunrise", 270, 330, "Hawaiian solo type with sweet reddish-orange flesh; compact plants")

        // ── Mango ──
        v(db, "Mango", "Tommy Atkins", 730, 1825, "Most widely grown commercial variety; colorful red-green skin")
        v(db, "Mango", "Alphonso", 730, 1825, "Considered the world's best mango; rich sweet creamy flesh")
        v(db, "Mango", "Kent", 730, 1825, "Large green-red fruit with sweet juicy fiberless flesh")
        v(db, "Mango", "Nam Doc Mai", 730, 1825, "Thai variety with elongated yellow fruits; sweet aromatic fiberless")
        v(db, "Mango", "Glenn", 730, 1825, "Florida variety with sweet mild fiberless flesh; compact tree")
    }

    // ── Chicken Breed Seeding ──

    private fun b(
        db: SupportSQLiteDatabase,
        name: String, eggsPerYear: Int, eggColor: String, eggSize: String,
        weight: String, purpose: String, temperament: String, combType: String,
        heat: Int, cold: Int, broodiness: String, climateNotes: String? = null,
    ) {
        db.execSQL(
            """INSERT INTO animal_breed_info (species, name, eggs_per_year, egg_color, egg_size, weight, purpose,
               temperament, comb_type, heat_tolerance, cold_tolerance, broodiness, notes, is_custom)
               VALUES ('chicken',?,?,?,?,?,?,?,?,?,?,?,?,0)""",
            arrayOf<Any?>(
                name, eggsPerYear, eggColor, eggSize, weight, purpose,
                temperament, combType, heat, cold, broodiness, climateNotes,
            )
        )
    }

    private fun seedBreedInfo(db: SupportSQLiteDatabase) {
        // ── Layer breeds ──
        b(db, "Leghorn (White)", 300, "White", "Large-XL", "4.5 lb",
            "layer", "Active, flighty", "Single (large)", 5, 1, "low",
            "Excellent layers, heat tolerant but frostbite-prone large comb")
        b(db, "Ancona", 260, "White", "Medium-XL", "4.5 lb",
            "layer", "Flighty, active", "Single", 4, 3, "low",
            "Good forager, active breed")
        b(db, "Easter Egger", 225, "Blue/Green/Pink", "Medium-Large", "5-6 lb",
            "layer", "Variable", "Pea/Various", 4, 3, "low",
            "Colorful eggs, friendly mixed breed")
        b(db, "Ameraucana", 190, "Blue", "Medium-XL", "5.5 lb",
            "layer", "Calm", "Pea", 4, 4, "moderate",
            "True blue eggs, pea comb resists frostbite")
        b(db, "Egyptian Fayoumi", 175, "White/Cream", "Small", "3.5 lb",
            "layer", "Wild, flighty", "Single (large)", 5, 1, "low",
            "Ancient breed, exceptional heat and disease resistance")

        // ── Dual-purpose breeds ──
        b(db, "Rhode Island Red", 275, "Brown", "Large", "6.5 lb",
            "dual", "Hardy, friendly", "Single", 4, 3, "moderate",
            "All-around hardy heritage breed")
        b(db, "Australorp", 265, "Brown", "Medium-Large", "6.5 lb",
            "dual", "Calm, docile", "Single", 4, 4, "moderate",
            "Record-setting layer, handles most climates well")
        b(db, "Plymouth Rock (Barred)", 240, "Brown", "Large", "7 lb",
            "dual", "Friendly, calm", "Single", 4, 3, "moderate",
            "Classic homestead breed, good all-rounder")
        b(db, "Sussex (Speckled)", 275, "Cream-Brown", "Medium-Large", "7 lb",
            "dual", "Curious, gentle", "Single", 3, 3, "moderate",
            "Good forager, alert to predators")
        b(db, "Wyandotte", 200, "Brown", "Large", "6.5 lb",
            "dual", "Calm, vocal", "Rose", 3, 4, "moderate",
            "Rose comb excellent for cold, many color varieties")
        b(db, "Orpington (Buff)", 190, "Brown", "Large", "8 lb",
            "dual", "Very gentle", "Single", 2, 4, "high",
            "Great personality, needs shade in heat")
        b(db, "Brahma", 175, "Brown", "Medium-Large", "9 lb",
            "dual", "Gentle, calm", "Pea", 2, 5, "high",
            "Feathered feet, pea comb, cold weather champion")
        b(db, "New Hampshire Red", 225, "Brown", "Large", "6.5 lb",
            "dual", "Competitive", "Single", 4, 3, "moderate",
            "Fast growing, good production")
        b(db, "Delaware", 240, "Brown", "Large-Jumbo", "6.5 lb",
            "dual", "Calm, docile", "Single", 4, 3, "moderate",
            "Heritage breed, jumbo eggs")
        b(db, "Dominique", 252, "Brown", "Medium", "5 lb",
            "dual", "Calm", "Rose", 3, 4, "high",
            "Oldest American breed, rose comb for cold hardiness")
        b(db, "Buckeye", 200, "Brown", "Medium", "6.5 lb",
            "dual", "Calm, curious", "Pea", 3, 5, "moderate",
            "Developed for cold Ohio winters, pea comb")
        b(db, "Chantecler", 210, "Brown", "Small-Large", "6.5 lb",
            "dual", "Calm", "Cushion", 3, 5, "high",
            "Canadian breed, cushion comb for extreme cold")
        b(db, "Naked Neck (Turken)", 225, "Brown", "Large", "5 lb",
            "dual", "Curious, adaptable", "Single", 5, 3, "moderate",
            "Bare neck aids heat dissipation, surprisingly cold hardy")
        b(db, "Welsummer", 180, "Dark Brown", "Medium-Large", "6 lb",
            "dual", "Docile, friendly", "Single", 3, 3, "moderate",
            "Beautiful speckled dark brown eggs")
        b(db, "Marans", 175, "Very Dark Brown", "Medium-Large", "7 lb",
            "dual", "Gentle, quiet", "Single", 3, 3, "moderate",
            "Chocolate-colored eggs, French heritage breed")
        b(db, "Barnevelder", 200, "Dark Brown", "Large", "6.5 lb",
            "dual", "Calm", "Single", 3, 2, "moderate",
            "Dutch breed, dark eggs, not very cold hardy")

        // ── Meat breeds ──
        b(db, "Cornish Cross", 0, "N/A", "N/A", "8 lb (6-8 weeks)",
            "meat", "Sedentary", "Single", 2, 1, "low",
            "Fast growing meat bird, not suited for laying or extremes")
        b(db, "Freedom Ranger", 0, "N/A", "N/A", "5-6 lb (9-11 weeks)",
            "meat", "Active, forager", "Single", 4, 2, "low",
            "Pasture-raised meat bird, good forager")

        // ── Ornamental breeds ──
        b(db, "Cochin", 140, "Brown", "Small-Medium", "8.5 lb",
            "ornamental", "Very docile", "Single (small)", 2, 4, "high",
            "Feathered feet, excellent broody mother")
        b(db, "Silkie", 110, "Cream/Tinted", "Small", "2-3 lb",
            "ornamental", "Very gentle", "Walnut", 3, 1, "high",
            "Excellent broody mother, silky feathers don't insulate when wet")

        // ── Expanded Breeds ──

        b(db, "ISA Brown", 300, "Brown", "Large", "5 lb",
            "layer", "Friendly, docile", "Single", 4, 3, "low",
            "Commercial hybrid, top layer, friendly but shorter lifespan (2-3 years peak)")

        b(db, "Golden Comet", 300, "Brown", "Large", "5 lb",
            "layer", "Friendly, calm", "Single", 4, 3, "low",
            "Sex-link hybrid, reliable layer, great for beginners")

        b(db, "Hamburg", 200, "White", "Small-Medium", "4 lb",
            "layer", "Active, flighty", "Rose", 3, 3, "low",
            "Rose comb for cold hardiness, excellent forager, alert")

        b(db, "Polish", 150, "White", "Small-Medium", "4.5 lb",
            "ornamental", "Gentle, docile", "V-shaped", 3, 2, "low",
            "Distinctive crest feathers, limited vision from crest, needs predator protection")

        b(db, "Jersey Giant", 175, "Brown", "Large-XL", "11 lb",
            "dual", "Calm, docile", "Single", 3, 4, "moderate",
            "Largest purebred chicken, slow to mature, gentle giant")

        b(db, "California White", 290, "White", "Large", "5 lb",
            "layer", "Active, friendly", "Single", 5, 2, "low",
            "Leghorn hybrid, prolific layer, heat tolerant, commercial production")
    }

    // ═══════════════════════════════════════════════════════════════
    //  BEE RACE INFO — 7 races from research
    // ═══════════════════════════════════════════════════════════════

    private fun r(
        db: SupportSQLiteDatabase,
        name: String, temperament: String,
        honey: Int, mite: Int, swarming: Int, overwintering: Int,
        climate: String, notes: String? = null,
    ) {
        db.execSQL(
            """INSERT INTO bee_race_info (name, temperament, honeyProduction, miteResistance,
               swarmingTendency, overwinteringAbility, climateSuitability, notes, isCustom)
               VALUES (?,?,?,?,?,?,?,?,0)""",
            arrayOf<Any?>(name, temperament, honey, mite, swarming, overwintering, climate, notes),
        )
    }

    private fun seedBeeRaceInfo(db: SupportSQLiteDatabase) {
        r(db, "Italian", "Very gentle",
            5, 2, 2, 3,
            "Best for warm/hot zones (7-10). Good for all regions as first-year bees.",
            "Prone to robbing. Low propolis. High heat tolerance. Most forgiving for beginners.")
        r(db, "Carniolan", "Very gentle",
            5, 3, 5, 5,
            "Best for cold zones (3-7). Excellent in Northeast, Upper Midwest, Pacific Northwest.",
            "Rapid spring buildup. Excellent cold hardiness. Poor heat tolerance. Low propolis.")
        r(db, "Russian", "Moderate-Defensive",
            4, 5, 4, 5,
            "Best for extreme cold zones (3-5). Good for Mountain West and Great Plains.",
            "VSH trait for mite resistance. Slow spring buildup. Need 80-100 lbs stores for winter.")
        r(db, "Buckfast", "Gentle",
            5, 4, 2, 4,
            "Versatile across zones 4-9. Good for Northeast, Mountain West, Southeast.",
            "Tracheal mite resistant. Moderate to fast spring buildup. Good cold and heat tolerance.")
        r(db, "Saskatraz", "Moderate",
            5, 5, 3, 4,
            "Wide range zones 3-10. Best for Upper Midwest, Southeast, Southwest.",
            "Excellent varroa and tracheal mite resistance. Very high propolis. Good all-climate bee.")
        r(db, "Caucasian", "Gentle",
            3, 2, 2, 4,
            "Best for moderate zones (5-7) with lower mite pressure.",
            "Very high propolis which can be challenging. Slow spring buildup. Fair heat tolerance.")
        r(db, "German Dark", "Aggressive",
            3, 2, 3, 5,
            "Best for cold zones (3-5) where temperament is less concern.",
            "Excellent cold hardiness. Poor heat tolerance. Not typically recommended due to aggression.")

        r(db, "Africanized", "Very Aggressive",
            5, 4, 5, 1,
            "Only suitable for hot zones (9-11). NOT recommended for residential areas.",
            "WARNING: Extremely defensive. Requires experienced beekeepers only. High honey production. Excellent disease resistance. Illegal in some jurisdictions.")

        r(db, "VSH Italian", "Gentle",
            4, 5, 2, 3,
            "Wide range zones 4-10. Best for beekeepers dealing with varroa mites.",
            "Varroa Sensitive Hygiene trait bred into Italian stock. Best of both worlds: gentle temperament with strong mite resistance. Slightly lower honey production than standard Italian.")
    }
}
