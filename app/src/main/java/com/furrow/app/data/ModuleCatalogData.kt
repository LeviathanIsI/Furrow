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
        "Chicken", "Duck", "Turkey", "Goose", "Quail",
        "Goat", "Sheep", "Pig", "Cow", "Rabbit",
        "Horse", "Alpaca", "Llama", "Donkey",
    )

    val HEALTH_RECORD_TYPES = listOf(
        "Vaccination", "Deworming", "Antibiotic", "Anti-Inflammatory",
        "Vitamin/Mineral", "Injury", "Illness", "Hoof Care", "Dental",
        "Reproductive Exam", "Other",
    )

    val ADMINISTRATION_ROUTES = listOf(
        "Oral", "SQ", "IM", "Topical", "Intramammary", "Pour-On", "Other",
    )

    val BREEDING_METHODS = listOf(
        "Natural", "AI", "Embryo Transfer", "Other",
    )

    val FEED_TYPES = listOf(
        "Hay", "Grain", "Pellet", "Mineral", "Supplement",
        "Pasture", "Silage", "Browse", "Other",
    )

    // ═══════════════════════════════════════════════════════════════
    //  ORCHARD
    // ═══════════════════════════════════════════════════════════════

    val ORCHARD_CATEGORIES = listOf(
        "Pome Fruit", "Stone Fruit", "Vine", "Bramble",
        "Berry", "Tree Nut", "Warm Climate",
    )

    val PRUNING_TYPES = listOf(
        "Dormant", "Summer", "Training", "Renewal", "Thinning", "Corrective",
    )

    val PRUNING_METHODS = listOf(
        "Hand Pruners", "Loppers", "Pruning Saw", "Hedge Shears", "Mechanical",
    )

    val SPRAY_TIMINGS = listOf(
        "Dormant", "Delayed Dormant", "Green Tip", "Pink", "Bloom",
        "Petal Fall", "Cover Spray", "Pre-Harvest", "Post-Harvest",
    )

    val FRUIT_QUALITIES = listOf(
        "Excellent", "Good", "Fair", "Poor",
    )

    val GRAFT_TYPES = listOf(
        "Whip and Tongue", "Cleft", "Bark", "Bud", "Bridge", "Side Veneer",
    )

    // ═══════════════════════════════════════════════════════════════
    //  PRESERVATION
    // ═══════════════════════════════════════════════════════════════

    val CANNING_METHODS = listOf(
        "Water Bath", "Pressure Canner", "Steam Canner",
    )

    val JAR_SIZES = listOf(
        "4 Oz (Quarter Pint)",
        "8 Oz (Half Pint)",
        "8 Oz (Half Pint, Wide Mouth)",
        "12 Oz (Three-Quarter Pint)",
        "16 Oz (Pint, Regular Mouth)",
        "16 Oz (Pint, Wide Mouth)",
        "24 Oz (Pint and a Half)",
        "32 Oz (Quart, Regular Mouth)",
        "32 Oz (Quart, Wide Mouth)",
        "64 Oz (Half Gallon)",
    )

    val CANNING_RECIPE_CATEGORIES = listOf(
        "Jam", "Jelly", "Preserves", "Marmalade", "Fruit Butter",
        "Fruit in Syrup", "Pie Filling", "Applesauce",
        "Pickles (Fermented)", "Pickles (Fresh Pack/Quick)",
        "Relish", "Chutney", "Salsa",
        "Tomato Sauce", "Tomatoes (Whole/Crushed/Diced)",
        "Pasta/Pizza Sauce", "Hot Sauce", "BBQ Sauce", "Ketchup",
        "Fruit Juice",
        "Soup (Pressure Only)", "Broth/Stock (Pressure Only)",
        "Vegetables (Pressure Only)", "Beans (Pressure Only)",
        "Meat - Raw Pack (Pressure Only)", "Meat - Hot Pack (Pressure Only)",
        "Poultry (Pressure Only)", "Fish/Seafood (Pressure Only)",
        "Stew/Chili (Pressure Only)", "Spaghetti Sauce with Meat (Pressure Only)",
    )

    val STORAGE_LOCATIONS = listOf(
        "Pantry Shelf", "Root Cellar", "Basement Shelf",
        "Kitchen Cabinet", "Climate-Controlled Closet",
        "Garage Shelf (Climate-Controlled)", "Dedicated Pantry Room",
        "Barn/Outbuilding", "Refrigerator", "Freezer",
    )

    val DEHYDRATING_PRODUCT_CATEGORIES = listOf(
        "Fruit Leather", "Dried Fruit (Sliced)", "Dried Berries",
        "Dried Vegetables", "Dried Herbs",
        "Dried Peppers (Hot)", "Dried Peppers (Sweet)",
        "Sun-Dried Tomatoes", "Dried Mushrooms",
        "Dried Garlic", "Dried Onion",
        "Jerky (Beef)", "Jerky (Poultry)", "Jerky (Venison/Game)",
        "Dried Citrus Wheels", "Fruit/Vegetable Powder",
        "Backpacking/Camp Meals", "Pet Treats",
        "Dried Flowers (Edible)", "Dried Beans/Legumes",
        "Crackers/Flatbread",
    )

    val FERMENTATION_METHODS = listOf(
        "Lacto-Fermentation (Salt Brine)", "Lacto-Fermentation (Dry Salt)",
        "Vinegar Pickling", "Alcohol Fermentation",
        "Acetic Acid Fermentation",
        "Kombucha (1st Ferment)", "Kombucha (2nd Ferment)",
        "Water Kefir", "Milk Kefir", "Yogurt Culturing", "Cheese Making",
        "Sourdough", "Tepache", "Mead", "Fruit Wine", "Hard Cider",
        "Beer/Ale Brewing", "Hot Sauce Fermentation",
        "Fermented Honey Garlic", "Ginger Bug", "Koji (Aspergillus)",
    )

    val VESSEL_TYPES = listOf(
        "Mason Jar", "Mason Jar w/ Airlock Lid",
        "Open Crock", "Water-Sealed Crock",
        "Food-Grade Bucket", "Glass Carboy", "PET Carboy",
        "Wooden Barrel", "Fido-Style Bail Jar",
        "Continuous Brew Vessel", "SS Conical Fermenter",
        "Ceramic Kimchi Pot (Onggi)", "Fermentation Weight + Jar",
    )

    val PACKAGING_METHODS = listOf(
        "Vacuum Sealed Bag", "Freezer Bag (Ziplock)",
        "Freezer Paper", "Plastic Wrap + Aluminum Foil",
        "Mason Jar (Wide Mouth)", "Rigid Plastic Container",
        "Ice Cube Tray Then Bag", "Silicone Mold Then Bag",
        "Butcher Paper", "Mylar Bag (Heat Sealed)",
        "Tray Pack / Flash Freeze", "Cryovac / Shrink Wrap",
        "Wax-Coated Box",
    )

    val CURE_TYPES = listOf(
        "Dry Cure (Rub)", "Wet Cure (Brine)", "Injection Cure",
        "Combination (Inject + Rub)", "Equilibrium Cure",
        "No Cure (Fresh Smoke)", "Sugar Cure", "Salt Box Cure",
    )

    val SMOKING_METHODS = listOf(
        "Cold Smoke", "Hot Smoke", "Warm Smoke",
        "Smoke Roast", "Low and Slow BBQ",
    )

    val SMOKE_WOODS = listOf(
        "Hickory", "Mesquite", "Oak (Red or White)",
        "Apple", "Cherry", "Pecan", "Maple", "Alder",
        "Peach", "Plum", "Mulberry",
        "Citrus (Orange/Lemon/Grapefruit)", "Grapevine",
        "Olive", "Walnut", "Whiskey Barrel Chips", "Competition Blend",
    )

    val MEAT_TYPES = listOf(
        "Beef - Brisket", "Beef - Chuck Roast", "Beef - Short Ribs", "Beef - Tri-Tip",
        "Pork - Shoulder/Butt", "Pork - Spare Ribs", "Pork - Baby Back Ribs",
        "Pork - Belly/Bacon", "Pork - Loin/Tenderloin", "Pork - Ham (Whole Leg)",
        "Chicken - Whole", "Chicken - Leg Quarters", "Chicken - Wings",
        "Turkey - Whole", "Turkey - Breast",
        "Duck - Whole",
        "Lamb - Leg", "Lamb - Shoulder",
        "Venison - Roast", "Venison - Jerky",
        "Wild Hog/Boar", "Goat", "Rabbit",
        "Fish - Salmon", "Fish - Trout", "Fish - Mullet", "Fish - Snapper/Grouper",
        "Sausage - Fresh", "Sausage - Cured",
        "Cheese", "Nuts (Pecans, Almonds)",
        "Vegetables", "Salt", "Hard-Boiled Eggs",
    )

    val PANTRY_CATEGORIES = listOf(
        "Canned Goods", "Dried Goods", "Fermented", "Frozen",
        "Smoked/Cured", "Fresh", "Condiments", "Beverages",
        "Baked Goods", "Grains/Flour", "Oils/Vinegars",
        "Herbs/Spices", "Dairy", "Eggs", "Honey", "Other",
    )

    val PANTRY_UNITS = listOf(
        "Jar", "Can", "Bag", "Lb", "Oz", "Kg", "Each",
        "Quart", "Pint", "Gallon", "Half-Gallon",
        "Bunch", "Dozen", "Bushel", "Flat",
    )

    val PANTRY_STATUSES = listOf(
        "In Stock", "Consumed", "Expired", "Donated",
    )

    // ═══════════════════════════════════════════════════════════════
    //  LAND
    // ═══════════════════════════════════════════════════════════════

    val STRUCTURE_TYPES = listOf(
        "Barn", "Shed", "Coop", "Greenhouse", "High Tunnel", "Cold Frame",
        "Pump House", "Equipment Storage", "Workshop", "Roadside Stand",
        "Root Cellar", "Smokehouse",
    )

    val FENCE_TYPES = listOf(
        "Woven Wire", "Electric Net", "High-Tensile Electric", "Board",
        "Welded Wire", "Barbed Wire", "Chain Link", "Split Rail", "Post and Rail",
    )

    val WATER_SOURCE_TYPES = listOf(
        "Well", "Municipal", "Pond", "Creek", "Rain Catchment",
        "Cistern", "Spring", "Irrigation Ditch",
    )

    val PUMP_TYPES = listOf(
        "Submersible", "Jet", "Solar", "Hand", "None",
    )

    val COMPOST_TYPES = listOf(
        "Hot", "Cold", "Vermicompost", "Bokashi", "Sheet/Lasagna", "Tumbler",
    )

    val COMPOST_STAGES = listOf(
        "Fresh", "Active", "Curing", "Finished",
    )

    val SOIL_TEXTURES = listOf(
        "Sand", "Loamy Sand", "Sandy Loam", "Loam", "Silt Loam", "Silt",
        "Sandy Clay Loam", "Clay Loam", "Silty Clay Loam",
        "Sandy Clay", "Silty Clay", "Clay",
    )

    val FORAGE_TYPES = listOf(
        "Grass", "Legume", "Mixed", "Browse", "Annual",
    )

    val ZONING_TYPES = listOf(
        "Agricultural", "Residential", "Rural", "Commercial",
        "Mixed Use", "Conservation", "Other",
    )

    // ═══════════════════════════════════════════════════════════════
    //  FINANCES
    // ═══════════════════════════════════════════════════════════════

    val EXPENSE_CATEGORIES = listOf(
        "Seeds & Plants", "Feed", "Fertilizer & Lime", "Machine Hire",
        "Repairs & Maintenance", "Supplies", "Utilities",
        "Veterinary & Medicine", "Fuel & Oil", "Labor", "Insurance",
        "Rent & Lease", "Taxes", "Interest", "Depreciation",
        "Storage & Warehousing", "Marketing", "Custom Hire",
        "Freight & Trucking", "Other",
    )

    val PAYMENT_METHODS = listOf(
        "Cash", "Check", "Credit Card", "Debit Card", "ACH/Wire", "Barter", "Other",
    )

    val PRODUCT_CATEGORIES = listOf(
        "Produce", "Eggs", "Meat", "Dairy", "Honey", "Fiber",
        "Value-Added", "Plants/Nursery", "Hay/Feed", "Firewood", "Other",
    )

    val SALES_CHANNELS = listOf(
        "Farm Gate", "Farmers Market", "CSA", "Wholesale", "Online",
        "Restaurant", "Co-Op", "Roadside Stand", "Other",
    )

    val UNITS = listOf(
        "Lb", "Oz", "Kg", "Dozen", "Each", "Bushel",
        "Gallon", "Quart", "Pint", "Head", "Flat", "Bunch", "Cord", "Other",
    )

    val MILEAGE_PURPOSES = listOf(
        "Farm Supply Run", "Market/Delivery", "Veterinary", "Equipment Repair",
        "Banking", "Meeting/Training", "Inspection", "Other",
    )

    // ═══════════════════════════════════════════════════════════════
    //  COMPLIANCE
    // ═══════════════════════════════════════════════════════════════

    val PERMIT_TYPES = listOf(
        "Food Sales", "Meat Processing", "Egg Handling", "Dairy", "Water Use",
        "Pesticide Applicator", "Zoning/Land Use", "Farmers Market",
        "Organic Certification", "Business License", "Cottage Food",
        "Food Handler", "Other",
    )

    val INSPECTION_OUTCOMES = listOf(
        "Passed", "Failed", "Conditional", "Pending",
        "Corrective Action Required", "Reinspection Scheduled",
    )

    val DOCUMENT_TYPES = listOf(
        "Permit", "License", "Certificate", "Insurance", "Test Result",
        "Training Record", "Plan/Protocol", "Receipt/Invoice", "Other",
    )

    val SALES_PRODUCT_TYPES = listOf(
        "Produce", "Eggs", "Meat", "Dairy", "Honey",
        "Value-Added", "Baked Goods", "Other",
    )
}
