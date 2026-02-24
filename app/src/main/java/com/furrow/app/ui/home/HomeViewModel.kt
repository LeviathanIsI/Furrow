package com.furrow.app.ui.home

import androidx.lifecycle.viewModelScope
import com.furrow.app.ui.FurrowViewModel
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.PlantVariety
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.data.repository.AnimalRepository
import com.furrow.app.data.repository.BeeRepository
import com.furrow.app.data.repository.ComplianceRepository
import com.furrow.app.data.repository.FinanceRepository
import com.furrow.app.data.repository.GardenRepository
import com.furrow.app.data.repository.LandRepository
import com.furrow.app.data.repository.OrchardRepository
import com.furrow.app.data.repository.PlantRepository
import com.furrow.app.data.repository.PreservationRepository
import com.furrow.app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.furrow.app.util.DateUtil
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    beeRepository: BeeRepository,
    gardenRepository: GardenRepository,
    private val plantRepository: PlantRepository,
    userProfileRepository: UserProfileRepository,
    animalRepository: AnimalRepository,
    orchardRepository: OrchardRepository,
    preservationRepository: PreservationRepository,
    landRepository: LandRepository,
    financeRepository: FinanceRepository,
    complianceRepository: ComplianceRepository,
) : FurrowViewModel() {

    internal val zone: ZoneId = runBlocking {
        userProfileRepository.getProfile().firstOrNull()
            ?.let { DateUtil.profileZone(it) } ?: ZoneId.systemDefault()
    }
    private val today = LocalDate.now(zone)
    private val currentMonth = today.monthValue
    private val todayStartMillis = today.atStartOfDay(zone).toInstant().toEpochMilli()
    private val todayEndMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
    private val weekStart = today.minusDays(6).atStartOfDay(zone).toInstant().toEpochMilli()

    // ── User Profile ──

    val userProfile: StateFlow<UserProfile?> = userProfileRepository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ── What to Plant Now (top 3 for current month + zone) ──

    val whatToPlantNow: StateFlow<List<PlantSuggestion>> = userProfile.flatMapLatest { profile ->
        if (profile == null) flowOf(emptyList())
        else combine(
            plantRepository.getActiveWindowsForMonth(profile.zoneGroup, currentMonth),
            plantRepository.getAllPlants(),
        ) { windows, plants ->
            val plantMap = plants.associateBy { it.name }
            windows.mapNotNull { window ->
                plantMap[window.plantName]?.let { plant ->
                    PlantSuggestion(
                        name = plant.name,
                        daysToHarvest = (plant.daysToHarvestMin + plant.daysToHarvestMax) / 2,
                        method = window.method,
                    )
                }
            }.distinctBy { it.name }.take(3)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    data class PlantSuggestion(
        val name: String,
        val daysToHarvest: Int,
        val method: String,
    )

    // ── Seasonal Tip ──

    val seasonalTip: StateFlow<String?> = userProfile.map { profile ->
        if (profile == null) null
        else getSeasonalTip(currentMonth, profile.zoneGroup)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ── Bees ──

    data class BeeInsights(
        val hiveCount: Int,
        val inspectionDueDays: Int?,
        val daysSinceLastInspection: Long?,
        val hivesNeedingInspection: Int,
    )

    val beeInsights: StateFlow<BeeInsights> = combine(
        beeRepository.getActiveHives(),
        beeRepository.getLastInspectionDatePerHive()
            .map { list -> list.associate { it.hiveId to it.date } },
    ) { hives, lastDates ->
        val mostRecentInspection = if (lastDates.isEmpty()) null else lastDates.values.max()
        val daysSince = mostRecentInspection?.let {
            ChronoUnit.DAYS.between(
                Instant.ofEpochMilli(it).atZone(zone).toLocalDate(),
                today,
            )
        }
        val dueDays = daysSince?.let { (INSPECTION_CYCLE_DAYS - it).toInt() }
        val hivesOverdue = hives.count { hive ->
            val lastDate = lastDates[hive.id]
            if (lastDate == null) true
            else ChronoUnit.DAYS.between(
                Instant.ofEpochMilli(lastDate).atZone(zone).toLocalDate(),
                today,
            ) >= INSPECTION_CYCLE_DAYS
        }
        BeeInsights(
            hiveCount = hives.size,
            inspectionDueDays = dueDays,
            daysSinceLastInspection = daysSince,
            hivesNeedingInspection = hivesOverdue,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BeeInsights(0, null, null, 0))

    // ── Garden ──

    data class GardenInsights(
        val activePlantings: Int,
        val producingCount: Int,
        val growingCount: Int,
        val oldestGrowingName: String?,
        val oldestGrowingDays: Long?,
        val bedCount: Int,
        val readyToHarvestCount: Int,
    )

    val gardenInsights: StateFlow<GardenInsights> = combine(
        gardenRepository.getActivePlantings(),
        gardenRepository.getActiveBeds(),
        plantRepository.getAllPlants(),
        plantRepository.getAllVarieties(),
    ) { plantings, beds, plants, varieties ->
        val producing = plantings.count { it.status.equals("producing", ignoreCase = true) }
        val growing = plantings.count { it.status.equals("growing", ignoreCase = true) }

        val oldestGrowing = plantings
            .filter { it.status.equals("growing", ignoreCase = true) }
            .minByOrNull { it.datePlanted }

        val oldestDays = oldestGrowing?.let {
            ChronoUnit.DAYS.between(
                Instant.ofEpochMilli(it.datePlanted).atZone(zone).toLocalDate(),
                today,
            )
        }

        // Compute ready-to-harvest count
        val plantMap = plants.associateBy { it.name }
        val varietyMap = varieties.associateBy { it.id }
        val readyCount = plantings.count { planting ->
            if (planting.status.lowercase() !in listOf("growing", "producing")) return@count false
            val info = plantMap[planting.plantName] ?: return@count false
            val variety = planting.varietyId?.let { varietyMap[it] }
            val daysMin = variety?.daysToHarvestMin ?: info.daysToHarvestMin
            val plantedDate = Instant.ofEpochMilli(planting.datePlanted).atZone(zone).toLocalDate()
            val earliest = plantedDate.plusDays(daysMin.toLong())
            !today.isBefore(earliest)
        }

        GardenInsights(
            activePlantings = plantings.size,
            producingCount = producing,
            growingCount = growing,
            oldestGrowingName = oldestGrowing?.plantName,
            oldestGrowingDays = oldestDays,
            bedCount = beds.size,
            readyToHarvestCount = readyCount,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        GardenInsights(0, 0, 0, null, null, 0, 0),
    )

    // ── Animals (includes egg stats from former Poultry) ──

    data class AnimalInsights(
        val totalActive: Int,
        val speciesCount: Int,
        val upcomingBirths: Int,
        val activeWithdrawals: Int,
        val chickenCount: Int,
        val todayEggs: Int,
        val thisWeekTotal: Int,
        val layRatePercent: Int?,
    )

    private val nowMillis = Instant.now().toEpochMilli()

    val animalInsights: StateFlow<AnimalInsights> = combine(
        combine(
            animalRepository.getActiveAnimals(),
            animalRepository.getUpcomingBirths(nowMillis),
            animalRepository.getActiveWithdrawals(nowMillis),
        ) { animals, births, withdrawals -> Triple(animals, births, withdrawals) },
        combine(
            animalRepository.getActiveChickens(),
            animalRepository.getEggCountForDateRange(todayStartMillis, todayEndMillis),
            animalRepository.getEggCountForDateRange(weekStart, todayEndMillis),
        ) { chickens, todayEggs, weekTotal -> Triple(chickens, todayEggs, weekTotal) },
    ) { (animals, births, withdrawals), (chickens, todayEggs, weekTotal) ->
        val chickenCount = chickens.size
        val layRate = if (chickenCount > 0) ((todayEggs.toDouble() / chickenCount) * 100).toInt() else null

        AnimalInsights(
            totalActive = animals.size,
            speciesCount = animals.map { it.species }.distinct().size,
            upcomingBirths = births.size,
            activeWithdrawals = withdrawals.size,
            chickenCount = chickenCount,
            todayEggs = todayEggs,
            thisWeekTotal = weekTotal,
            layRatePercent = layRate,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnimalInsights(0, 0, 0, 0, 0, 0, 0, null))

    // ── Orchard ──

    data class OrchardInsights(
        val plantCount: Int,
        val totalHarvestLbs: Double,
    )

    val orchardInsights: StateFlow<OrchardInsights> = combine(
        orchardRepository.getAllPlants(),
        orchardRepository.getAllHarvests(),
    ) { plants, harvests ->
        OrchardInsights(
            plantCount = plants.size,
            totalHarvestLbs = harvests.sumOf { it.yieldLbs ?: 0.0 },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OrchardInsights(0, 0.0))

    // ── Preservation ──

    data class PreservationInsights(
        val totalBatches: Int,
        val pantryInStock: Int,
        val expiringSoon: Int,
    )

    private val thirtyDaysFromNow = today.plusDays(30).atStartOfDay(zone).toInstant().toEpochMilli()

    val preservationInsights: StateFlow<PreservationInsights> = combine(
        preservationRepository.getAllCanningBatches().map { it.size },
        preservationRepository.getAllDehydratingBatches().map { it.size },
        preservationRepository.getAllFermentingBatches().map { it.size },
        preservationRepository.getAllFreezingBatches().map { it.size },
        combine(
            preservationRepository.getAllSmokingCuringBatches().map { it.size },
            preservationRepository.getInStockPantryItems(),
            preservationRepository.getExpiringSoonPantryItems(thirtyDaysFromNow),
        ) { smoking, inStock, expiring -> Triple(smoking, inStock.size, expiring.size) },
    ) { canning, dehydrating, fermenting, freezing, (smoking, inStock, expiring) ->
        PreservationInsights(
            totalBatches = canning + dehydrating + fermenting + freezing + smoking,
            pantryInStock = inStock,
            expiringSoon = expiring,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PreservationInsights(0, 0, 0))

    // ── Land ──

    data class LandInsights(
        val propertyCount: Int,
        val totalAcreage: Double,
        val structureCount: Int,
    )

    val landInsights: StateFlow<LandInsights> = combine(
        landRepository.getAllProperties(),
        landRepository.getAllStructures(),
    ) { properties, structures ->
        LandInsights(
            propertyCount = properties.size,
            totalAcreage = properties.sumOf { it.totalAcreage ?: 0.0 },
            structureCount = structures.size,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LandInsights(0, 0.0, 0))

    // ── Finances ──

    data class FinanceInsights(
        val monthExpenses: Double,
        val monthRevenue: Double,
        val netIncome: Double,
    )

    private val monthStartMillis = today.withDayOfMonth(1).atStartOfDay(zone).toInstant().toEpochMilli()
    private val monthEndMillis = today.plusMonths(1).withDayOfMonth(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1

    val financeInsights: StateFlow<FinanceInsights> = combine(
        financeRepository.getExpenseTotalForDateRange(monthStartMillis, monthEndMillis),
        financeRepository.getRevenueTotalForDateRange(monthStartMillis, monthEndMillis),
    ) { expenses, revenue ->
        val exp = expenses ?: 0.0
        val rev = revenue ?: 0.0
        FinanceInsights(
            monthExpenses = exp,
            monthRevenue = rev,
            netIncome = rev - exp,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinanceInsights(0.0, 0.0, 0.0))

    // ── Compliance ──

    data class ComplianceInsights(
        val totalPermits: Int,
        val expiringSoon: Int,
    )

    private val ninetyDaysFromNow = today.plusDays(90).atStartOfDay(zone).toInstant().toEpochMilli()

    val complianceInsights: StateFlow<ComplianceInsights> = combine(
        complianceRepository.getAllLicensePermits(),
        complianceRepository.getLicensePermitsExpiringSoon(ninetyDaysFromNow),
    ) { permits, expiring ->
        ComplianceInsights(
            totalPermits = permits.size,
            expiringSoon = expiring.size,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ComplianceInsights(0, 0))
}

private const val INSPECTION_CYCLE_DAYS = 7L

// ═══════════════════════════════════════════════════════════════
//  SEASONAL TIPS — month × zone group → actionable tip
// ═══════════════════════════════════════════════════════════════

private fun getSeasonalTip(month: Int, zoneGroup: String): String = when (zoneGroup) {
    "extreme_cold" -> extremeColdTips[month] ?: ""
    "cold" -> coldTips[month] ?: ""
    "moderate" -> moderateTips[month] ?: ""
    "warm" -> warmTips[month] ?: ""
    "hot" -> hotTips[month] ?: ""
    else -> ""
}

private val extremeColdTips = mapOf(
    1 to "Order seeds and plan garden layout. Check hive entrance for ice blockage. Review flock health \u2014 supplement light for egg production.",
    2 to "Start seeds indoors under lights. Monitor hive stores \u2014 emergency feed if light. Egg production may start increasing with daylight.",
    3 to "Harden off cool-season starts. First hive inspection when temps hit 50\u00b0F. Prepare brooder if hatching spring chicks.",
    4 to "Direct sow peas, lettuce, spinach. Spring buildup accelerating \u2014 check for queen. Deep clean coop for spring.",
    5 to "Transplant after last frost. Add supers as nectar flow begins. Chicks can move outdoors if feathered.",
    6 to "Main planting month \u2014 warm-season crops go in. Peak nectar flow \u2014 monitor supers weekly. Free-range flock for pest control.",
    7 to "Succession plant beans and greens. Check for swarming signs. Ensure fresh water and shade for flock.",
    8 to "Plant fall crops: kale, broccoli, turnips. Begin mite treatment after honey harvest. Watch for molting starting.",
    9 to "Harvest and preserve. Remove supers, treat for mites. Reduce flock if needed before winter.",
    10 to "Plant garlic, mulch beds heavily. Feed bees 2:1 syrup for winter stores. Winterize coop \u2014 add insulation.",
    11 to "Put garden to bed. Wrap hives, add mouse guards. Switch to high-protein feed as molting peaks.",
    12 to "Plan next year. Check hive weight monthly. Supplement light if egg production drops too low.",
)

private val coldTips = mapOf(
    1 to "Order seeds, review crop rotation plan. Quick hive entrance check on mild days. Supplement flock lighting for 14-hr days.",
    2 to "Start tomatoes and peppers indoors. Check hive stores \u2014 spring buildup starting. Egg production increasing with daylight.",
    3 to "Sow cool-season crops: peas, lettuce, radishes. First inspection when consistently above 50\u00b0F. Prepare for spring chicks.",
    4 to "Transplant cool-season starts. Reverse hive bodies if needed. Start chicks in brooder.",
    5 to "Plant warm-season crops after frost date. Add supers as nectar flow starts. Move pullets to coop.",
    6 to "Succession plant beans, squash, cucumbers. Monitor honey supers weekly. Peak egg production season.",
    7 to "Mulch heavily, water deeply. Check for swarming and mites. Provide shade and cool water for flock.",
    8 to "Start fall brassicas. Harvest honey, begin mite treatment. Watch for early molting signs.",
    9 to "Plant garlic, sow cover crops. Finish mite treatment. Ensure flock gets adequate protein.",
    10 to "Harvest remaining crops, mulch beds. Feed bees 2:1 syrup. Winterize coop before first freeze.",
    11 to "Clean and store tools. Add mouse guards, reduce hive entrances. Switch to winter feed ration.",
    12 to "Plan next season. Monitor hive weight. Supplement light for egg production.",
)

private val moderateTips = mapOf(
    1 to "Sow cool-season crops: peas, lettuce, carrots. First hive inspection of the year. Flock should be laying well with lengthening days.",
    2 to "Direct sow beans and brassicas. Spring buildup active \u2014 check queen and brood. Egg production ramping up.",
    3 to "Plant tomatoes and peppers outdoors. Add supers as nectar flow begins. Peak hatching season if breeding.",
    4 to "Main spring planting \u2014 all warm-season crops. Monitor for swarming. Free-range for spring pest control.",
    5 to "Succession plant summer crops. Honey supers filling fast. Peak egg production month.",
    6 to "Harvest early crops, plant fall succession. Check mite levels. Ensure shade and water for heat.",
    7 to "Water deeply, mulch everything. Split strong hives if needed. Watch for heat stress in flock.",
    8 to "Start fall garden: brassicas, root crops. Harvest honey, treat for mites. Molting may begin.",
    9 to "Plant cool-season crops for fall/winter harvest. Feed bees if flow has ended. Adjust feed for molting birds.",
    10 to "Plant garlic, cover crop empty beds. Prepare hives for winter. Good time to add young pullets.",
    11 to "Harvest cool-season crops. Final hive inspection, reduce entrances. Ensure draft-free coop.",
    12 to "Protect tender perennials. Monitor hive stores. Maintain 14-hr light for winter laying.",
)

private val warmTips = mapOf(
    1 to "Plant cool-season crops and early tomatoes. Bees active on warm days \u2014 check stores. Good egg production continues.",
    2 to "Direct sow beans, squash, cucumbers. Spring nectar flow starting \u2014 add supers. Excellent laying month.",
    3 to "Plant everything \u2014 main spring window. Strong nectar flow, watch for swarming. Peak production season.",
    4 to "Succession plant warm crops. Monitor honey supers. Chicks started now will lay by fall.",
    5 to "Harvest spring crops, plant heat-tolerant varieties. Honey harvest possible. Ensure plenty of water.",
    6 to "Focus on heat-tolerant crops: okra, peppers, sweet potatoes. Check mite loads. Shade and ventilation critical.",
    7 to "Minimal planting \u2014 maintain mulch and water. Summer dearth may require feeding bees. Watch for heat stress.",
    8 to "Start fall garden: tomatoes, beans, squash. Late-summer nectar flow possible. Egg production may dip in heat.",
    9 to "Main fall planting month. Fall nectar flow \u2014 bees rebuilding stores. Production recovering as temps cool.",
    10 to "Plant cool-season crops: lettuce, peas, brassicas. Treat mites, feed if needed. Good laying resumes.",
    11 to "Continue cool-season garden. Prepare hives for mild winter. Consistent egg production.",
    12 to "Harvest winter garden. Quick hive check. Flock laying well through mild winter.",
)

private val hotTips = mapOf(
    1 to "Plant cool-season crops now \u2014 peak winter growing. Bees foraging actively. Excellent egg production.",
    2 to "Direct sow beans, squash, melons. Strong nectar flow beginning. Peak laying season.",
    3 to "Last chance for cool-season crops. Nectar flow in full swing \u2014 add supers. Maximize egg collection.",
    4 to "Transition to heat-tolerant crops. Honey harvest from spring flow. Start heat management for flock.",
    5 to "Plant okra, sweet potatoes, southern peas. Monitor for swarming. Shade and misters for flock.",
    6 to "Minimal planting \u2014 too hot for most crops. Summer dearth \u2014 may need to feed bees. Reduce heat stress.",
    7 to "Maintain garden with deep mulch and shade cloth. Feed bees through dearth. Egg production lowest of year.",
    8 to "Start planning fall garden. Continue feeding bees if needed. Production slowly recovering.",
    9 to "Major fall planting: tomatoes, peppers, beans, squash. Fall nectar flow starting. Egg production increasing.",
    10 to "Plant cool-season crops: lettuce, brassicas, root crops. Bees rebuilding stores. Good laying month.",
    11 to "Continue planting cool-season garden. Treat mites if needed. Strong egg production.",
    12 to "Harvest and enjoy winter garden. Bees overwintering with minimal intervention. Consistent winter laying.",
)
