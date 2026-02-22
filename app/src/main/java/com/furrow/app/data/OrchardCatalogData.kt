package com.furrow.app.data

/**
 * Reference data for orchard species: chill hours, rootstocks, pollination notes, etc.
 * Similar to [GestationLookup] but for fruit/nut trees and berries.
 */
object OrchardCatalogData {

    data class OrchardSpeciesDefaults(
        val category: String,
        val chillHoursLow: Int,
        val chillHoursHigh: Int,
        val yearsToBearing: Int,
        val commonRootstocks: List<String>,
        val commonVarieties: List<String>,
        val pollinationNotes: String,
        val harvestMonths: String,
    )

    private val data: Map<String, OrchardSpeciesDefaults> = mapOf(
        // ── Pome fruit ──
        "Apple" to OrchardSpeciesDefaults(
            category = "Pome Fruit", chillHoursLow = 800, chillHoursHigh = 1200, yearsToBearing = 4,
            commonRootstocks = listOf("M.9", "M.26", "M.7", "MM.106", "MM.111", "B.9", "G.11", "G.41"),
            commonVarieties = listOf(
                "Honeycrisp", "Fuji", "Gala", "Granny Smith", "Golden Delicious",
                "Red Delicious", "Braeburn", "Pink Lady", "Jonagold", "McIntosh",
                "Cortland", "Empire", "Gravenstein", "Arkansas Black", "Liberty",
                "Enterprise", "Winesap", "Rome", "Northern Spy", "Cox's Orange Pippin",
            ),
            pollinationNotes = "Requires cross-pollination; plant 2+ varieties with overlapping bloom",
            harvestMonths = "Aug-Oct",
        ),
        "Pear" to OrchardSpeciesDefaults(
            category = "Pome Fruit", chillHoursLow = 600, chillHoursHigh = 900, yearsToBearing = 4,
            commonRootstocks = listOf("OHxF 97", "OHxF 87", "OHxF 333", "Pyrus betulifolia"),
            commonVarieties = listOf(
                "Bartlett", "D'Anjou", "Bosc", "Comice", "Seckel",
                "Asian (Hosui)", "Asian (Shinseiki)", "Asian (Chojuro)",
                "Kieffer", "Moonglow", "Harrow Sweet", "Conference",
            ),
            pollinationNotes = "Requires cross-pollination; Bartlett and Seckel are poor pollinators for each other",
            harvestMonths = "Aug-Oct",
        ),
        "Quince" to OrchardSpeciesDefaults(
            category = "Pome Fruit", chillHoursLow = 300, chillHoursHigh = 500, yearsToBearing = 3,
            commonRootstocks = listOf("Quince A", "Quince C", "own root"),
            commonVarieties = listOf(
                "Orange", "Champion", "Pineapple", "Smyrna", "Aromatnaya",
            ),
            pollinationNotes = "Self-fertile but benefits from cross-pollination",
            harvestMonths = "Sep-Nov",
        ),
        // ── Stone fruit ──
        "Peach" to OrchardSpeciesDefaults(
            category = "Stone Fruit", chillHoursLow = 600, chillHoursHigh = 1000, yearsToBearing = 3,
            commonRootstocks = listOf("Lovell", "Halford", "Guardian", "Citation", "Nemaguard"),
            commonVarieties = listOf(
                "Elberta", "Redhaven", "Belle of Georgia", "Contender", "Reliance",
                "Cresthaven", "Hale Haven", "Madison", "Rio Oso Gem", "Flordaprince",
                "Indian Blood", "Saturn (Donut)", "White Lady",
            ),
            pollinationNotes = "Most varieties self-fertile",
            harvestMonths = "Jun-Sep",
        ),
        "Nectarine" to OrchardSpeciesDefaults(
            category = "Stone Fruit", chillHoursLow = 600, chillHoursHigh = 1000, yearsToBearing = 3,
            commonRootstocks = listOf("Lovell", "Halford", "Guardian", "Nemaguard"),
            commonVarieties = listOf(
                "Fantasia", "Goldmine", "Harko", "Mericrest", "Sunglo",
                "Sunraycer", "Arctic Star", "Independence",
            ),
            pollinationNotes = "Most varieties self-fertile",
            harvestMonths = "Jun-Sep",
        ),
        "Plum" to OrchardSpeciesDefaults(
            category = "Stone Fruit", chillHoursLow = 500, chillHoursHigh = 900, yearsToBearing = 4,
            commonRootstocks = listOf("Myrobalan", "St. Julien", "Mariana 2624", "Citation"),
            commonVarieties = listOf(
                "Santa Rosa", "Methley", "Italian Prune", "Stanley", "Shiro",
                "Green Gage", "Damson", "Satsuma", "Ozark Premier", "Toka",
            ),
            pollinationNotes = "European plums mostly self-fertile; Japanese plums need cross-pollination",
            harvestMonths = "Jun-Sep",
        ),
        "Apricot" to OrchardSpeciesDefaults(
            category = "Stone Fruit", chillHoursLow = 500, chillHoursHigh = 900, yearsToBearing = 3,
            commonRootstocks = listOf("Myrobalan", "Manchurian", "Lovell", "Citation"),
            commonVarieties = listOf(
                "Goldcot", "Moorpark", "Tilton", "Harglow", "Tomcot",
                "Puget Gold", "Chinese/Mormon", "Blenheim",
            ),
            pollinationNotes = "Most varieties self-fertile; early bloom prone to frost damage",
            harvestMonths = "Jun-Jul",
        ),
        "Cherry (sweet)" to OrchardSpeciesDefaults(
            category = "Stone Fruit", chillHoursLow = 800, chillHoursHigh = 1200, yearsToBearing = 5,
            commonRootstocks = listOf("Mazzard", "Gisela 5", "Gisela 6", "Krymsk 5"),
            commonVarieties = listOf(
                "Bing", "Rainier", "Stella", "Lapins", "Sweetheart",
                "Black Tartarian", "Van", "Sam", "Hedelfingen", "Montmorency",
            ),
            pollinationNotes = "Most need cross-pollination; Stella, Lapins are self-fertile",
            harvestMonths = "Jun-Jul",
        ),
        "Cherry (tart)" to OrchardSpeciesDefaults(
            category = "Stone Fruit", chillHoursLow = 800, chillHoursHigh = 1200, yearsToBearing = 4,
            commonRootstocks = listOf("Mazzard", "Mahaleb", "Gisela 5"),
            commonVarieties = listOf(
                "Montmorency", "North Star", "Meteor", "Balaton", "Danube",
            ),
            pollinationNotes = "Self-fertile; Montmorency is the standard variety",
            harvestMonths = "Jun-Jul",
        ),
        // ── Vine ──
        "Grape" to OrchardSpeciesDefaults(
            category = "Vine", chillHoursLow = 100, chillHoursHigh = 500, yearsToBearing = 3,
            commonRootstocks = listOf("own root", "SO4", "3309C", "101-14", "110R", "1103P"),
            commonVarieties = listOf(
                "Concord", "Niagara", "Catawba", "Muscadine", "Thompson Seedless",
                "Flame Seedless", "Reliance", "Mars", "Marquette", "Frontenac",
                "Chambourcin", "Norton/Cynthiana", "Cabernet Sauvignon", "Merlot",
            ),
            pollinationNotes = "Self-fertile; wind pollinated",
            harvestMonths = "Aug-Oct",
        ),
        "Hardy Kiwi" to OrchardSpeciesDefaults(
            category = "Vine", chillHoursLow = 800, chillHoursHigh = 1200, yearsToBearing = 4,
            commonRootstocks = listOf("own root"),
            commonVarieties = listOf(
                "Issai", "Ananasnaya", "Ken's Red", "Geneva", "Dumbarton Oaks",
            ),
            pollinationNotes = "Dioecious: need male and female plants; 1 male per 6-8 females",
            harvestMonths = "Sep-Oct",
        ),
        // ── Bramble ──
        "Raspberry" to OrchardSpeciesDefaults(
            category = "Bramble", chillHoursLow = 800, chillHoursHigh = 1500, yearsToBearing = 2,
            commonRootstocks = listOf("own root"),
            commonVarieties = listOf(
                "Heritage", "Autumn Bliss", "Caroline", "Joan J", "Tulameen",
                "Meeker", "Latham", "Polka", "Fall Gold", "Anne",
            ),
            pollinationNotes = "Self-fertile; attracts many pollinators",
            harvestMonths = "Jun-Oct",
        ),
        "Blackberry" to OrchardSpeciesDefaults(
            category = "Bramble", chillHoursLow = 300, chillHoursHigh = 900, yearsToBearing = 2,
            commonRootstocks = listOf("own root"),
            commonVarieties = listOf(
                "Triple Crown", "Chester", "Navaho", "Ouachita", "Apache",
                "Prime-Ark Freedom", "Arapaho", "Natchez", "Osage",
            ),
            pollinationNotes = "Most varieties self-fertile",
            harvestMonths = "Jun-Aug",
        ),
        // ── Berry ──
        "Blueberry" to OrchardSpeciesDefaults(
            category = "Berry", chillHoursLow = 400, chillHoursHigh = 1000, yearsToBearing = 3,
            commonRootstocks = listOf("own root"),
            commonVarieties = listOf(
                "Bluecrop", "Jersey", "Duke", "Elliott", "Patriot",
                "Northland", "Pink Lemonade", "Sunshine Blue", "Rabbiteye",
                "Premier", "Tifblue", "Brightwell",
            ),
            pollinationNotes = "Cross-pollination improves yield; plant 2+ varieties",
            harvestMonths = "Jun-Aug",
        ),
        "Strawberry" to OrchardSpeciesDefaults(
            category = "Berry", chillHoursLow = 200, chillHoursHigh = 400, yearsToBearing = 1,
            commonRootstocks = listOf("own root"),
            commonVarieties = listOf(
                "Chandler", "Albion", "Seascape", "Jewel", "Earliglow",
                "Honeoye", "Allstar", "Ozark Beauty", "Tristar", "Cabot",
            ),
            pollinationNotes = "Self-fertile; insect pollinated",
            harvestMonths = "May-Oct",
        ),
        "Cranberry" to OrchardSpeciesDefaults(
            category = "Berry", chillHoursLow = 800, chillHoursHigh = 1200, yearsToBearing = 3,
            commonRootstocks = listOf("own root"),
            commonVarieties = listOf(
                "Stevens", "Ben Lear", "Pilgrim", "Howes", "Early Black",
            ),
            pollinationNotes = "Self-fertile; needs bees for best yield",
            harvestMonths = "Sep-Nov",
        ),
        // ── Warm climate ──
        "Fig" to OrchardSpeciesDefaults(
            category = "Warm Climate", chillHoursLow = 100, chillHoursHigh = 300, yearsToBearing = 2,
            commonRootstocks = listOf("own root"),
            commonVarieties = listOf(
                "Brown Turkey", "Celeste", "Chicago Hardy", "Kadota",
                "Black Mission", "Alma", "LSU Purple", "Desert King",
            ),
            pollinationNotes = "Common figs are self-fertile; no pollinator needed",
            harvestMonths = "Jul-Oct",
        ),
        "Pomegranate" to OrchardSpeciesDefaults(
            category = "Warm Climate", chillHoursLow = 100, chillHoursHigh = 400, yearsToBearing = 3,
            commonRootstocks = listOf("own root"),
            commonVarieties = listOf(
                "Wonderful", "Eversweet", "Parfianka", "Angel Red",
                "Salavatski", "Russian 26", "Surh Anor",
            ),
            pollinationNotes = "Self-fertile but cross-pollination improves yield",
            harvestMonths = "Sep-Nov",
        ),
        "Persimmon" to OrchardSpeciesDefaults(
            category = "Warm Climate", chillHoursLow = 100, chillHoursHigh = 500, yearsToBearing = 4,
            commonRootstocks = listOf("D. virginiana", "D. lotus"),
            commonVarieties = listOf(
                "Fuyu", "Hachiya", "Jiro", "Saijo", "Prok",
                "Yates", "Meader", "Nikita's Gift",
            ),
            pollinationNotes = "American self-fertile; Asian varies by variety",
            harvestMonths = "Sep-Dec",
        ),
        "Citrus (general)" to OrchardSpeciesDefaults(
            category = "Warm Climate", chillHoursLow = 0, chillHoursHigh = 100, yearsToBearing = 3,
            commonRootstocks = listOf("Trifoliate", "Carrizo citrange", "C-35", "Flying Dragon"),
            commonVarieties = listOf(
                "Meyer Lemon", "Eureka Lemon", "Washington Navel", "Valencia Orange",
                "Satsuma Mandarin", "Clementine", "Ruby Red Grapefruit", "Key Lime",
                "Persian Lime", "Kumquat (Nagami)", "Blood Orange (Moro)",
            ),
            pollinationNotes = "Most varieties self-fertile",
            harvestMonths = "Nov-Apr",
        ),
        // ── Tree nut ──
        "Hazelnut" to OrchardSpeciesDefaults(
            category = "Tree Nut", chillHoursLow = 800, chillHoursHigh = 1500, yearsToBearing = 4,
            commonRootstocks = listOf("own root"),
            commonVarieties = listOf(
                "Barcelona", "Jefferson", "Yamhill", "Theta", "Wepster",
            ),
            pollinationNotes = "Wind pollinated; needs compatible pollinizer variety",
            harvestMonths = "Sep-Oct",
        ),
        "Walnut" to OrchardSpeciesDefaults(
            category = "Tree Nut", chillHoursLow = 400, chillHoursHigh = 1000, yearsToBearing = 5,
            commonRootstocks = listOf("Paradox", "J. hindsii", "J. regia seedling"),
            commonVarieties = listOf(
                "Chandler", "Franquette", "Hartley", "Howard", "Carpathian",
            ),
            pollinationNotes = "Monoecious but protandrous; plant 2+ varieties for best pollination",
            harvestMonths = "Sep-Nov",
        ),
        "Pecan" to OrchardSpeciesDefaults(
            category = "Tree Nut", chillHoursLow = 400, chillHoursHigh = 800, yearsToBearing = 7,
            commonRootstocks = listOf("seedling pecan"),
            commonVarieties = listOf(
                "Desirable", "Pawnee", "Stuart", "Elliot", "Cape Fear",
                "Kanza", "Lakota", "Oconee",
            ),
            pollinationNotes = "Requires cross-pollination; plant Type I and Type II varieties",
            harvestMonths = "Oct-Nov",
        ),
        "Chestnut" to OrchardSpeciesDefaults(
            category = "Tree Nut", chillHoursLow = 400, chillHoursHigh = 800, yearsToBearing = 4,
            commonRootstocks = listOf("seedling", "Dunstan"),
            commonVarieties = listOf(
                "Colossal", "Dunstan", "Chinese (AU Homestead)", "Bouche de Betizac",
            ),
            pollinationNotes = "Requires cross-pollination; plant 2+ varieties",
            harvestMonths = "Sep-Oct",
        ),
        "Almond" to OrchardSpeciesDefaults(
            category = "Tree Nut", chillHoursLow = 300, chillHoursHigh = 600, yearsToBearing = 4,
            commonRootstocks = listOf("Nemaguard", "Lovell", "Krymsk 86", "Viking"),
            commonVarieties = listOf(
                "Nonpareil", "Monterey", "Carmel", "All-in-One", "Hall's Hardy",
            ),
            pollinationNotes = "Most need cross-pollination; early bloom prone to frost",
            harvestMonths = "Aug-Oct",
        ),
    )

    /** All species names in display order. */
    val SPECIES_NAMES: List<String> = data.keys.toList()

    /** Look up defaults for a species by name. */
    fun forSpecies(species: String): OrchardSpeciesDefaults? = data[species]

    /** Common varieties for a given species name, or empty if unknown. */
    fun varietiesForSpecies(species: String): List<String> =
        data[species]?.commonVarieties ?: emptyList()

    /** All species data as a list of pairs. */
    fun allEntries(): Map<String, OrchardSpeciesDefaults> = data
}
