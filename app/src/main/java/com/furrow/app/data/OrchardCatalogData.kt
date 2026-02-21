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
        val pollinationNotes: String,
        val harvestMonths: String,
    )

    private val data: Map<String, OrchardSpeciesDefaults> = mapOf(
        // ── Pome fruit ──
        "Apple" to OrchardSpeciesDefaults(
            category = "pome fruit", chillHoursLow = 800, chillHoursHigh = 1200, yearsToBearing = 4,
            commonRootstocks = listOf("M.9", "M.26", "M.7", "MM.106", "MM.111", "B.9", "G.11", "G.41"),
            pollinationNotes = "Requires cross-pollination; plant 2+ varieties with overlapping bloom",
            harvestMonths = "Aug-Oct",
        ),
        "Pear" to OrchardSpeciesDefaults(
            category = "pome fruit", chillHoursLow = 600, chillHoursHigh = 900, yearsToBearing = 4,
            commonRootstocks = listOf("OHxF 97", "OHxF 87", "OHxF 333", "Pyrus betulifolia"),
            pollinationNotes = "Requires cross-pollination; Bartlett and Seckel are poor pollinators for each other",
            harvestMonths = "Aug-Oct",
        ),
        "Quince" to OrchardSpeciesDefaults(
            category = "pome fruit", chillHoursLow = 300, chillHoursHigh = 500, yearsToBearing = 3,
            commonRootstocks = listOf("Quince A", "Quince C", "own root"),
            pollinationNotes = "Self-fertile but benefits from cross-pollination",
            harvestMonths = "Sep-Nov",
        ),
        // ── Stone fruit ──
        "Peach" to OrchardSpeciesDefaults(
            category = "stone fruit", chillHoursLow = 600, chillHoursHigh = 1000, yearsToBearing = 3,
            commonRootstocks = listOf("Lovell", "Halford", "Guardian", "Citation", "Nemaguard"),
            pollinationNotes = "Most varieties self-fertile",
            harvestMonths = "Jun-Sep",
        ),
        "Nectarine" to OrchardSpeciesDefaults(
            category = "stone fruit", chillHoursLow = 600, chillHoursHigh = 1000, yearsToBearing = 3,
            commonRootstocks = listOf("Lovell", "Halford", "Guardian", "Nemaguard"),
            pollinationNotes = "Most varieties self-fertile",
            harvestMonths = "Jun-Sep",
        ),
        "Plum" to OrchardSpeciesDefaults(
            category = "stone fruit", chillHoursLow = 500, chillHoursHigh = 900, yearsToBearing = 4,
            commonRootstocks = listOf("Myrobalan", "St. Julien", "Mariana 2624", "Citation"),
            pollinationNotes = "European plums mostly self-fertile; Japanese plums need cross-pollination",
            harvestMonths = "Jun-Sep",
        ),
        "Apricot" to OrchardSpeciesDefaults(
            category = "stone fruit", chillHoursLow = 500, chillHoursHigh = 900, yearsToBearing = 3,
            commonRootstocks = listOf("Myrobalan", "Manchurian", "Lovell", "Citation"),
            pollinationNotes = "Most varieties self-fertile; early bloom prone to frost damage",
            harvestMonths = "Jun-Jul",
        ),
        "Cherry (sweet)" to OrchardSpeciesDefaults(
            category = "stone fruit", chillHoursLow = 800, chillHoursHigh = 1200, yearsToBearing = 5,
            commonRootstocks = listOf("Mazzard", "Gisela 5", "Gisela 6", "Krymsk 5"),
            pollinationNotes = "Most need cross-pollination; Stella, Lapins are self-fertile",
            harvestMonths = "Jun-Jul",
        ),
        "Cherry (tart)" to OrchardSpeciesDefaults(
            category = "stone fruit", chillHoursLow = 800, chillHoursHigh = 1200, yearsToBearing = 4,
            commonRootstocks = listOf("Mazzard", "Mahaleb", "Gisela 5"),
            pollinationNotes = "Self-fertile; Montmorency is the standard variety",
            harvestMonths = "Jun-Jul",
        ),
        // ── Vine ──
        "Grape" to OrchardSpeciesDefaults(
            category = "vine", chillHoursLow = 100, chillHoursHigh = 500, yearsToBearing = 3,
            commonRootstocks = listOf("own root", "SO4", "3309C", "101-14", "110R", "1103P"),
            pollinationNotes = "Self-fertile; wind pollinated",
            harvestMonths = "Aug-Oct",
        ),
        "Hardy Kiwi" to OrchardSpeciesDefaults(
            category = "vine", chillHoursLow = 800, chillHoursHigh = 1200, yearsToBearing = 4,
            commonRootstocks = listOf("own root"),
            pollinationNotes = "Dioecious: need male and female plants; 1 male per 6-8 females",
            harvestMonths = "Sep-Oct",
        ),
        // ── Bramble ──
        "Raspberry" to OrchardSpeciesDefaults(
            category = "bramble", chillHoursLow = 800, chillHoursHigh = 1500, yearsToBearing = 2,
            commonRootstocks = listOf("own root"),
            pollinationNotes = "Self-fertile; attracts many pollinators",
            harvestMonths = "Jun-Oct",
        ),
        "Blackberry" to OrchardSpeciesDefaults(
            category = "bramble", chillHoursLow = 300, chillHoursHigh = 900, yearsToBearing = 2,
            commonRootstocks = listOf("own root"),
            pollinationNotes = "Most varieties self-fertile",
            harvestMonths = "Jun-Aug",
        ),
        // ── Berry ──
        "Blueberry" to OrchardSpeciesDefaults(
            category = "berry", chillHoursLow = 400, chillHoursHigh = 1000, yearsToBearing = 3,
            commonRootstocks = listOf("own root"),
            pollinationNotes = "Cross-pollination improves yield; plant 2+ varieties",
            harvestMonths = "Jun-Aug",
        ),
        "Strawberry" to OrchardSpeciesDefaults(
            category = "berry", chillHoursLow = 200, chillHoursHigh = 400, yearsToBearing = 1,
            commonRootstocks = listOf("own root"),
            pollinationNotes = "Self-fertile; insect pollinated",
            harvestMonths = "May-Oct",
        ),
        "Cranberry" to OrchardSpeciesDefaults(
            category = "berry", chillHoursLow = 800, chillHoursHigh = 1200, yearsToBearing = 3,
            commonRootstocks = listOf("own root"),
            pollinationNotes = "Self-fertile; needs bees for best yield",
            harvestMonths = "Sep-Nov",
        ),
        // ── Warm climate ──
        "Fig" to OrchardSpeciesDefaults(
            category = "warm climate", chillHoursLow = 100, chillHoursHigh = 300, yearsToBearing = 2,
            commonRootstocks = listOf("own root"),
            pollinationNotes = "Common figs are self-fertile; no pollinator needed",
            harvestMonths = "Jul-Oct",
        ),
        "Pomegranate" to OrchardSpeciesDefaults(
            category = "warm climate", chillHoursLow = 100, chillHoursHigh = 400, yearsToBearing = 3,
            commonRootstocks = listOf("own root"),
            pollinationNotes = "Self-fertile but cross-pollination improves yield",
            harvestMonths = "Sep-Nov",
        ),
        "Persimmon" to OrchardSpeciesDefaults(
            category = "warm climate", chillHoursLow = 100, chillHoursHigh = 500, yearsToBearing = 4,
            commonRootstocks = listOf("D. virginiana", "D. lotus"),
            pollinationNotes = "American self-fertile; Asian varies by variety",
            harvestMonths = "Sep-Dec",
        ),
        "Citrus (general)" to OrchardSpeciesDefaults(
            category = "warm climate", chillHoursLow = 0, chillHoursHigh = 100, yearsToBearing = 3,
            commonRootstocks = listOf("Trifoliate", "Carrizo citrange", "C-35", "Flying Dragon"),
            pollinationNotes = "Most varieties self-fertile",
            harvestMonths = "Nov-Apr",
        ),
        // ── Tree nut ──
        "Hazelnut" to OrchardSpeciesDefaults(
            category = "tree nut", chillHoursLow = 800, chillHoursHigh = 1500, yearsToBearing = 4,
            commonRootstocks = listOf("own root"),
            pollinationNotes = "Wind pollinated; needs compatible pollinizer variety",
            harvestMonths = "Sep-Oct",
        ),
        "Walnut" to OrchardSpeciesDefaults(
            category = "tree nut", chillHoursLow = 400, chillHoursHigh = 1000, yearsToBearing = 5,
            commonRootstocks = listOf("Paradox", "J. hindsii", "J. regia seedling"),
            pollinationNotes = "Monoecious but protandrous; plant 2+ varieties for best pollination",
            harvestMonths = "Sep-Nov",
        ),
        "Pecan" to OrchardSpeciesDefaults(
            category = "tree nut", chillHoursLow = 400, chillHoursHigh = 800, yearsToBearing = 7,
            commonRootstocks = listOf("seedling pecan"),
            pollinationNotes = "Requires cross-pollination; plant Type I and Type II varieties",
            harvestMonths = "Oct-Nov",
        ),
        "Chestnut" to OrchardSpeciesDefaults(
            category = "tree nut", chillHoursLow = 400, chillHoursHigh = 800, yearsToBearing = 4,
            commonRootstocks = listOf("seedling", "Dunstan"),
            pollinationNotes = "Requires cross-pollination; plant 2+ varieties",
            harvestMonths = "Sep-Oct",
        ),
        "Almond" to OrchardSpeciesDefaults(
            category = "tree nut", chillHoursLow = 300, chillHoursHigh = 600, yearsToBearing = 4,
            commonRootstocks = listOf("Nemaguard", "Lovell", "Krymsk 86", "Viking"),
            pollinationNotes = "Most need cross-pollination; early bloom prone to frost",
            harvestMonths = "Aug-Oct",
        ),
    )

    /** All species names in display order. */
    val SPECIES_NAMES: List<String> = data.keys.toList()

    /** Look up defaults for a species by name. */
    fun forSpecies(species: String): OrchardSpeciesDefaults? = data[species]

    /** All species data as a list of pairs. */
    fun allEntries(): Map<String, OrchardSpeciesDefaults> = data
}
