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
        seedPlantingWindows(db)
        seedBreedInfo(db)
        seedBeeRaceInfo(db)
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
    ) {
        db.execSQL(
            """INSERT INTO plant_info (name, category, minZone, maxZone, daysToHarvestMin, daysToHarvestMax,
               sunRequirement, waterFrequency, containerSuitable, containerMinGallons,
               companionPlants, incompatiblePlants, notes, isCustom) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,0)""",
            arrayOf<Any?>(
                name, cat, minZ, maxZ, dtmMin, dtmMax, sun, water,
                if (container) 1 else 0, gallon, companions, incompatible, notes,
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

    // ── Chicken Breed Seeding ──

    private fun b(
        db: SupportSQLiteDatabase,
        name: String, eggsPerYear: Int, eggColor: String, eggSize: String,
        weight: String, purpose: String, temperament: String, combType: String,
        heat: Int, cold: Int, broodiness: String, climateNotes: String? = null,
    ) {
        db.execSQL(
            """INSERT INTO chicken_breed_info (name, eggsPerYear, eggColor, eggSize, weight, purpose,
               temperament, combType, heatTolerance, coldTolerance, broodiness, climateNotes, isCustom)
               VALUES (?,?,?,?,?,?,?,?,?,?,?,?,0)""",
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
