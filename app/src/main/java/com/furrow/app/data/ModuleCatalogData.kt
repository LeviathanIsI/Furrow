package com.furrow.app.data

/**
 * Centralized controlled vocabularies for dropdown selectors across all modules.
 * Each list provides curated options for form fields that were previously free-text.
 */
object ModuleCatalogData {

    // ═══════════════════════════════════════════════════════════════
    //  ANIMALS
    // ═══════════════════════════════════════════════════════════════

    val SPECIES = listOf(
        "chicken", "duck", "turkey", "goose", "quail",
        "goat", "sheep", "pig", "cow", "rabbit",
        "horse", "alpaca", "llama", "donkey",
    )

    val HEALTH_RECORD_TYPES = listOf(
        "vaccination", "deworming", "antibiotic", "anti-inflammatory",
        "vitamin/mineral", "injury", "illness", "hoof care", "dental",
        "reproductive exam", "other",
    )

    val ADMINISTRATION_ROUTES = listOf(
        "oral", "SQ", "IM", "topical", "intramammary", "pour-on", "other",
    )

    val BREEDING_METHODS = listOf(
        "natural", "AI", "embryo transfer", "other",
    )

    val FEED_TYPES = listOf(
        "hay", "grain", "pellet", "mineral", "supplement",
        "pasture", "silage", "browse", "other",
    )

    // ═══════════════════════════════════════════════════════════════
    //  ORCHARD
    // ═══════════════════════════════════════════════════════════════

    val ORCHARD_CATEGORIES = listOf(
        "pome fruit", "stone fruit", "vine", "bramble",
        "berry", "tree nut", "warm climate",
    )

    val PRUNING_TYPES = listOf(
        "dormant", "summer", "training", "renewal", "thinning", "corrective",
    )

    val PRUNING_METHODS = listOf(
        "hand pruners", "loppers", "pruning saw", "hedge shears", "mechanical",
    )

    val SPRAY_TIMINGS = listOf(
        "dormant", "delayed dormant", "green tip", "pink", "bloom",
        "petal fall", "cover spray", "pre-harvest", "post-harvest",
    )

    val FRUIT_QUALITIES = listOf(
        "excellent", "good", "fair", "poor",
    )

    val GRAFT_TYPES = listOf(
        "whip and tongue", "cleft", "bark", "bud", "bridge", "side veneer",
    )

    // ═══════════════════════════════════════════════════════════════
    //  PRESERVATION
    // ═══════════════════════════════════════════════════════════════

    val CANNING_METHODS = listOf(
        "water bath", "pressure", "steam",
    )

    val JAR_SIZES = listOf(
        "4 oz", "half-pint", "pint", "quart", "half-gallon",
    )

    val FERMENTATION_METHODS = listOf(
        "lacto-ferment", "vinegar pickle", "alcohol", "mixed",
    )

    val VESSEL_TYPES = listOf(
        "mason jar", "crock", "airlock jar", "bucket", "carboy",
    )

    val PACKAGING_METHODS = listOf(
        "vacuum seal", "freezer bag", "freezer paper", "rigid container", "ice cube tray",
    )

    val CURE_TYPES = listOf(
        "dry cure", "wet brine", "equilibrium cure",
    )

    val SMOKE_WOODS = listOf(
        "hickory", "mesquite", "apple", "cherry", "oak", "maple", "pecan", "alder",
    )

    val SMOKING_METHODS = listOf(
        "hot smoke", "cold smoke", "smoke roast",
    )

    val PANTRY_CATEGORIES = listOf(
        "canned goods", "dried goods", "fermented", "frozen",
        "smoked/cured", "fresh", "condiments", "beverages", "other",
    )

    val PANTRY_UNITS = listOf(
        "jar", "can", "bag", "lb", "oz", "each", "quart", "pint", "gallon", "bunch",
    )

    val STORAGE_LOCATIONS = listOf(
        "pantry", "cellar", "refrigerator", "root cellar", "freezer",
    )

    // ═══════════════════════════════════════════════════════════════
    //  LAND
    // ═══════════════════════════════════════════════════════════════

    val STRUCTURE_TYPES = listOf(
        "barn", "shed", "coop", "greenhouse", "high tunnel", "cold frame",
        "pump house", "equipment storage", "workshop", "roadside stand",
        "root cellar", "smokehouse",
    )

    val FENCE_TYPES = listOf(
        "woven wire", "electric net", "high-tensile electric", "board",
        "welded wire", "barbed wire", "chain link", "split rail", "post and rail",
    )

    val WATER_SOURCE_TYPES = listOf(
        "well", "municipal", "pond", "creek", "rain catchment",
        "cistern", "spring", "irrigation ditch",
    )

    val PUMP_TYPES = listOf(
        "submersible", "jet", "solar", "hand", "none",
    )

    val COMPOST_TYPES = listOf(
        "hot", "cold", "vermicompost", "bokashi", "sheet/lasagna", "tumbler",
    )

    val COMPOST_STAGES = listOf(
        "fresh", "active", "curing", "finished",
    )

    val SOIL_TEXTURES = listOf(
        "sand", "loamy sand", "sandy loam", "loam", "silt loam", "silt",
        "sandy clay loam", "clay loam", "silty clay loam",
        "sandy clay", "silty clay", "clay",
    )

    val FORAGE_TYPES = listOf(
        "grass", "legume", "mixed", "browse", "annual",
    )

    val ZONING_TYPES = listOf(
        "agricultural", "residential", "rural", "commercial",
        "mixed use", "conservation", "other",
    )

    // ═══════════════════════════════════════════════════════════════
    //  FINANCES
    // ═══════════════════════════════════════════════════════════════

    val EXPENSE_CATEGORIES = listOf(
        "seeds & plants", "feed", "fertilizer & lime", "machine hire",
        "repairs & maintenance", "supplies", "utilities",
        "veterinary & medicine", "fuel & oil", "labor", "insurance",
        "rent & lease", "taxes", "interest", "depreciation",
        "storage & warehousing", "marketing", "custom hire",
        "freight & trucking", "other",
    )

    val PAYMENT_METHODS = listOf(
        "cash", "check", "credit card", "debit card", "ACH/wire", "barter", "other",
    )

    val PRODUCT_CATEGORIES = listOf(
        "produce", "eggs", "meat", "dairy", "honey", "fiber",
        "value-added", "plants/nursery", "hay/feed", "firewood", "other",
    )

    val SALES_CHANNELS = listOf(
        "farm gate", "farmers market", "CSA", "wholesale", "online",
        "restaurant", "co-op", "roadside stand", "other",
    )

    val UNITS = listOf(
        "lb", "oz", "kg", "dozen", "each", "bushel",
        "gallon", "quart", "pint", "head", "flat", "bunch", "cord", "other",
    )

    val MILEAGE_PURPOSES = listOf(
        "farm supply run", "market/delivery", "veterinary", "equipment repair",
        "banking", "meeting/training", "inspection", "other",
    )

    // ═══════════════════════════════════════════════════════════════
    //  COMPLIANCE
    // ═══════════════════════════════════════════════════════════════

    val PERMIT_TYPES = listOf(
        "food sales", "meat processing", "egg handling", "dairy", "water use",
        "pesticide applicator", "zoning/land use", "farmers market",
        "organic certification", "business license", "cottage food",
        "food handler", "other",
    )

    val INSPECTION_OUTCOMES = listOf(
        "passed", "failed", "conditional", "pending",
        "corrective action required", "reinspection scheduled",
    )

    val DOCUMENT_TYPES = listOf(
        "permit", "license", "certificate", "insurance", "test result",
        "training record", "plan/protocol", "receipt/invoice", "other",
    )

    val SALES_PRODUCT_TYPES = listOf(
        "produce", "eggs", "meat", "dairy", "honey",
        "value-added", "baked goods", "other",
    )
}
