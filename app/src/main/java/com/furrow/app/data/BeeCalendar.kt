package com.furrow.app.data

/**
 * Hardcoded bee calendar: month × zone group → tasks and nectar flow status.
 * Data from furrow-research.md regional beekeeping calendars.
 *
 * Zone groups mapped to research regions:
 * - extreme_cold/cold → Cold regions (zones 3-6)
 * - moderate → Moderate regions (zones 6-7)
 * - warm/hot → Warm regions (zones 8-10+)
 */
object BeeCalendar {

    data class MonthlyInfo(
        val tasks: List<String>,
        val nectarStatus: NectarStatus,
    )

    enum class NectarStatus(val label: String) {
        FLOW_ACTIVE("Nectar flow active"),
        FLOW_STARTING("Nectar flow starting"),
        FLOW_ENDING("Nectar flow ending"),
        SECONDARY_FLOW("Secondary flow (goldenrod/aster)"),
        DEARTH("Nectar dearth \u2014 check stores"),
        DORMANT("Winter dormant"),
    }

    fun getMonthlyInfo(month: Int, zoneGroup: String): MonthlyInfo {
        val region = when (zoneGroup) {
            "extreme_cold", "cold" -> coldCalendar
            "moderate" -> moderateCalendar
            "warm", "hot" -> warmCalendar
            else -> moderateCalendar
        }
        return region[month] ?: moderateCalendar[month]!!
    }

    // ═══════════════════════════════════════════════════════════════
    //  COLD REGIONS — Zones 3-6
    // ═══════════════════════════════════════════════════════════════

    private val coldCalendar = mapOf(
        1 to MonthlyInfo(
            tasks = listOf(
                "Don't open hives \u2014 too cold",
                "Heft hives to check weight",
                "Emergency feed with sugar board if light",
                "Order bees and equipment for spring",
            ),
            nectarStatus = NectarStatus.DORMANT,
        ),
        2 to MonthlyInfo(
            tasks = listOf(
                "Most starvation deaths happen now \u2014 check weight",
                "Feed pollen patties if needed",
                "Queen may start laying late in month",
            ),
            nectarStatus = NectarStatus.DORMANT,
        ),
        3 to MonthlyInfo(
            tasks = listOf(
                "Feed 1:1 syrup if stores are low",
                "Do NOT open until temps reach 50\u00b0F+",
                "First pollen arriving: pussy willow, maple",
            ),
            nectarStatus = NectarStatus.FLOW_STARTING,
        ),
        4 to MonthlyInfo(
            tasks = listOf(
                "First full inspection when temps allow",
                "Check queen, brood pattern, disease signs",
                "Varroa mite test",
                "Reverse brood boxes if needed",
            ),
            nectarStatus = NectarStatus.FLOW_STARTING,
        ),
        5 to MonthlyInfo(
            tasks = listOf(
                "Inspect every 7-10 days for swarm cells",
                "Add supers as nectar flow begins",
                "Make splits if desired",
                "Swarm season \u2014 watch for queen cells",
            ),
            nectarStatus = NectarStatus.FLOW_ACTIVE,
        ),
        6 to MonthlyInfo(
            tasks = listOf(
                "Peak nectar flow: clover, basswood",
                "Add supers as needed",
                "Monitor for swarming",
                "Peak honey production month",
            ),
            nectarStatus = NectarStatus.FLOW_ACTIVE,
        ),
        7 to MonthlyInfo(
            tasks = listOf(
                "Harvest spring honey",
                "Varroa mite test",
                "Continue swarm management",
                "Nectar flow may be tapering",
            ),
            nectarStatus = NectarStatus.FLOW_ENDING,
        ),
        8 to MonthlyInfo(
            tasks = listOf(
                "CRITICAL: Treat for varroa NOW",
                "Winter bees being raised this month",
                "This is the most important treatment window",
                "Population naturally declining",
            ),
            nectarStatus = NectarStatus.DEARTH,
        ),
        9 to MonthlyInfo(
            tasks = listOf(
                "Final varroa treatment",
                "Remove empty supers",
                "Feed 2:1 syrup for winter stores",
                "Watch for robbing behavior",
            ),
            nectarStatus = NectarStatus.SECONDARY_FLOW,
        ),
        10 to MonthlyInfo(
            tasks = listOf(
                "Install mouse guards",
                "Ensure 80-100 lbs stores (cold) or 60-80 lbs",
                "Wrap hives if in zones 3-5",
                "Final foraging on asters and goldenrod",
            ),
            nectarStatus = NectarStatus.FLOW_ENDING,
        ),
        11 to MonthlyInfo(
            tasks = listOf(
                "Ensure top ventilation is working",
                "Set up windbreak if hives are exposed",
                "No inspections \u2014 bees fully clustered",
            ),
            nectarStatus = NectarStatus.DORMANT,
        ),
        12 to MonthlyInfo(
            tasks = listOf(
                "Leave hives alone",
                "Plan for next year",
                "Order equipment and supplies",
            ),
            nectarStatus = NectarStatus.DORMANT,
        ),
    )

    // ═══════════════════════════════════════════════════════════════
    //  MODERATE REGIONS — Zones 6-7
    // ═══════════════════════════════════════════════════════════════

    private val moderateCalendar = mapOf(
        1 to MonthlyInfo(
            tasks = listOf(
                "Check stores on warm days",
                "Emergency feed if needed",
                "Order bees and equipment for spring",
            ),
            nectarStatus = NectarStatus.DORMANT,
        ),
        2 to MonthlyInfo(
            tasks = listOf(
                "Pollen patties if needed",
                "Quick check on warm days (50\u00b0F+)",
                "First pollen: maple, crocus",
                "Brood rearing starting",
            ),
            nectarStatus = NectarStatus.FLOW_STARTING,
        ),
        3 to MonthlyInfo(
            tasks = listOf(
                "Full inspection \u2014 check queen and brood",
                "Reverse brood boxes if needed",
                "Feed 1:1 syrup if stores are light",
                "Varroa mite test",
            ),
            nectarStatus = NectarStatus.FLOW_STARTING,
        ),
        4 to MonthlyInfo(
            tasks = listOf(
                "Inspect weekly for swarm cells",
                "Add first supers",
                "Make nucs or splits if desired",
                "Swarm season begins!",
            ),
            nectarStatus = NectarStatus.FLOW_ACTIVE,
        ),
        5 to MonthlyInfo(
            tasks = listOf(
                "Major nectar flow: locust, clover, tulip poplar",
                "Supers on \u2014 this is your honey month",
                "Monitor for crowding",
                "Peak buildup",
            ),
            nectarStatus = NectarStatus.FLOW_ACTIVE,
        ),
        6 to MonthlyInfo(
            tasks = listOf(
                "Nectar flow continues or peaks",
                "Add supers as needed",
                "May harvest spring honey mid-month",
                "High honey production",
            ),
            nectarStatus = NectarStatus.FLOW_ACTIVE,
        ),
        7 to MonthlyInfo(
            tasks = listOf(
                "Nectar flow ending in most areas",
                "Harvest honey",
                "Begin varroa treatment plan",
                "Provide water source for bees",
            ),
            nectarStatus = NectarStatus.DEARTH,
        ),
        8 to MonthlyInfo(
            tasks = listOf(
                "TREAT FOR VARROA \u2014 critical window",
                "Requeen if needed",
                "Feed if stores are low",
                "Winter bees being raised",
            ),
            nectarStatus = NectarStatus.DEARTH,
        ),
        9 to MonthlyInfo(
            tasks = listOf(
                "Complete varroa treatment",
                "Assess winter stores",
                "Begin fall feeding with 2:1 syrup",
                "Fall nectar flow possible: goldenrod, aster",
            ),
            nectarStatus = NectarStatus.SECONDARY_FLOW,
        ),
        10 to MonthlyInfo(
            tasks = listOf(
                "Install mouse guards",
                "Ensure 60-80 lbs stores",
                "Reduce entrances",
                "Drones being expelled",
            ),
            nectarStatus = NectarStatus.FLOW_ENDING,
        ),
        11 to MonthlyInfo(
            tasks = listOf(
                "Ensure ventilation",
                "Leave hives alone \u2014 bees clustered",
                "Minimal activity expected",
            ),
            nectarStatus = NectarStatus.DORMANT,
        ),
        12 to MonthlyInfo(
            tasks = listOf(
                "Plan for next year",
                "Colony is dormant",
                "Order supplies for spring",
            ),
            nectarStatus = NectarStatus.DORMANT,
        ),
    )

    // ═══════════════════════════════════════════════════════════════
    //  WARM REGIONS — Zones 8-10+
    // ═══════════════════════════════════════════════════════════════

    private val warmCalendar = mapOf(
        1 to MonthlyInfo(
            tasks = listOf(
                "Inspect hives \u2014 brood rearing active",
                "Feed if stores are low",
                "Build up colonies for spring flow",
                "Pollen coming in: maple, early wildflowers",
            ),
            nectarStatus = NectarStatus.FLOW_STARTING,
        ),
        2 to MonthlyInfo(
            tasks = listOf(
                "Strong buildup underway",
                "Add supers in Gulf Coast and Florida",
                "Swarm season begins in deep South",
                "Early nectar flow starting",
            ),
            nectarStatus = NectarStatus.FLOW_ACTIVE,
        ),
        3 to MonthlyInfo(
            tasks = listOf(
                "PRIMARY nectar flow: citrus, gallberry, wildflower",
                "Supers on \u2014 weekly swarm inspections",
                "Peak honey production month (FL/Gulf)",
                "Monitor for small hive beetle",
            ),
            nectarStatus = NectarStatus.FLOW_ACTIVE,
        ),
        4 to MonthlyInfo(
            tasks = listOf(
                "Nectar flow continues",
                "Continue harvest",
                "Monitor for SHB (Small Hive Beetle)",
                "Varroa mite test",
            ),
            nectarStatus = NectarStatus.FLOW_ACTIVE,
        ),
        5 to MonthlyInfo(
            tasks = listOf(
                "Flow ending in most southern areas",
                "Harvest spring honey",
                "Begin varroa treatment",
                "Prepare for summer dearth",
            ),
            nectarStatus = NectarStatus.FLOW_ENDING,
        ),
        6 to MonthlyInfo(
            tasks = listOf(
                "Summer dearth beginning",
                "Provide shade, water, ventilation",
                "Feed if needed",
                "SHB management critical in heat",
            ),
            nectarStatus = NectarStatus.DEARTH,
        ),
        7 to MonthlyInfo(
            tasks = listOf(
                "Dearth continues \u2014 extreme heat",
                "Keep water available at all times",
                "Minimal inspections (early AM only)",
                "Bees may beard heavily \u2014 this is normal",
            ),
            nectarStatus = NectarStatus.DEARTH,
        ),
        8 to MonthlyInfo(
            tasks = listOf(
                "Late summer varroa treatment",
                "Start feeding for fall buildup",
                "Dearth continues in most areas",
                "Fall buildup starting in some regions",
            ),
            nectarStatus = NectarStatus.DEARTH,
        ),
        9 to MonthlyInfo(
            tasks = listOf(
                "Fall buildup begins",
                "Secondary nectar flow: goldenrod, aster",
                "Inspect and feed",
                "Requeen if needed",
            ),
            nectarStatus = NectarStatus.SECONDARY_FLOW,
        ),
        10 to MonthlyInfo(
            tasks = listOf(
                "Fall flow continues \u2014 good buildup month",
                "Add supers for fall honey if strong flow",
                "Monitor stores",
            ),
            nectarStatus = NectarStatus.SECONDARY_FLOW,
        ),
        11 to MonthlyInfo(
            tasks = listOf(
                "Light harvest if surplus",
                "Ensure stores: 40-60 lbs (less than northern)",
                "Activity slowing but brood rearing continues",
            ),
            nectarStatus = NectarStatus.FLOW_ENDING,
        ),
        12 to MonthlyInfo(
            tasks = listOf(
                "Light inspection",
                "Oxalic acid treatment if broodless",
                "Cluster is loose or absent in zones 9-10",
                "Plan for next year",
            ),
            nectarStatus = NectarStatus.DORMANT,
        ),
    )
}
