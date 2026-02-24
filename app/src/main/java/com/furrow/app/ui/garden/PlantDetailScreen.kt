package com.furrow.app.ui.garden

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.util.DateUtil
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.PlantVariety
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppSectionHeader
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.Tag
import com.furrow.app.ui.theme.AppRadius
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderVisible
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.Slate
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextMuted
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.util.displayFormat
import com.furrow.app.util.formatFertilizer
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

// ── Main Screen ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlantDetailScreen(
    onBack: () -> Unit,
    viewModel: PlantDetailViewModel = hiltViewModel(),
) {
    val planting by viewModel.planting.collectAsState()
    val plant by viewModel.plant.collectAsState()
    val varieties by viewModel.varieties.collectAsState()
    val plantingWindows by viewModel.plantingWindows.collectAsState()
    val zone = viewModel.zone

    val currentPlant = plant ?: return
    val currentPlanting = planting

    val matchedVariety = currentPlanting?.variety?.let { varName ->
        varieties.find { it.name.equals(varName, ignoreCase = true) }
    }
    val dthMin = matchedVariety?.daysToHarvestMin ?: currentPlant.daysToHarvestMin
    val dthMax = matchedVariety?.daysToHarvestMax ?: currentPlant.daysToHarvestMax

    val headerName = if (currentPlanting?.variety != null) {
        "${currentPlant.name} • ${currentPlanting.variety}"
    } else {
        currentPlant.name
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = headerName,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            // ── Header ──
            item {
                Text(
                    text = "Zone ${currentPlant.minZone}–${currentPlant.maxZone} · $dthMin–$dthMax days to harvest",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                ) {
                    Tag(text = currentPlant.category.displayFormat(), selected = true)
                    Tag(text = currentPlant.sunRequirement.displayFormat())
                    if (currentPlant.perennial) Tag(text = "Perennial")
                    if (currentPlant.frostTolerant) Tag(text = "Frost Tolerant")
                    if (currentPlant.containerSuitable) {
                        val gallons = if (currentPlant.containerMinGallons > 0) " ${currentPlant.containerMinGallons}gal+" else ""
                        Tag(text = "Container$gallons")
                    }
                }
            }

            // ── This Planting (contextual) ──
            if (currentPlanting != null) {
                item { PlantingContextPanel(currentPlanting, dthMin, dthMax, zone) }
                item { AppSectionHeader("Plant Reference") }
            }

            // ── Quick Stats ──
            item { QuickStatsRow(currentPlant, dthMin, dthMax) }

            // ── Planting Windows ──
            if (plantingWindows.isNotEmpty()) {
                item { PlantingTimeline(plantingWindows) }
            }

            // ── Growing Requirements ──
            item { GrowingRequirementsPanel(currentPlant) }

            // ── Germination Tips ──
            item { GerminationTipCard(currentPlant) }

            // ── Companion Plants ──
            item { CompanionPlantsSection(currentPlant) }

            // ── Harvest Tips ──
            item { HarvestTipsPanel(currentPlant) }

            // ── Pest & Disease ──
            item { PestDiseasePanel(currentPlant) }

            // ── Care Notes ──
            item { CareNotesPanel(currentPlant) }

            // ── Climate Tips ──
            item { ClimateTipsPanel(currentPlant) }

            // ── Succession Planting ──
            if (currentPlant.canSuccessionPlant == true && currentPlant.successionPlantingDays != null) {
                item {
                    Panel {
                        PanelTitle(Icons.Outlined.CalendarMonth, "Succession Planting")
                        InlineStat("Replant every", "${currentPlant.successionPlantingDays} days")
                    }
                }
            }

            // ── Rotation & Storage ──
            item { RotationStoragePanel(currentPlant) }

            // ── Varieties ──
            if (varieties.isNotEmpty()) {
                item { VarietiesSection(varieties) }
            }

            // ── Notes ──
            if (currentPlant.notes?.isNotBlank() == true) {
                item {
                    AppSectionHeader("Notes")
                    Panel {
                        Text(currentPlant.notes, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(AppSpacing.md)) }
        }
    }
}

// ── Panel Title Helper ──────────────────────────────────────────────────────────

@Composable
private fun PanelTitle(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        Icon(icon, contentDescription = null, tint = GardenGlow, modifier = Modifier.size(18.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
    }
}

// ── Planting Context ────────────────────────────────────────────────────────────

@Composable
private fun PlantingContextPanel(
    planting: Planting,
    dthMin: Int,
    dthMax: Int,
    zone: ZoneId,
) {
    val today = LocalDate.now(zone)
    val plantedDate = Instant.ofEpochMilli(planting.datePlanted).atZone(zone).toLocalDate()
    val age = ChronoUnit.DAYS.between(plantedDate, today)

    fun fmtDate(date: LocalDate): String = DateUtil.formatShortDate(date, today)

    val earliestHarvest = plantedDate.plusDays(dthMin.toLong())
    val latestHarvest = plantedDate.plusDays(dthMax.toLong())

    Panel {
        PanelTitle(Icons.Outlined.Spa, "This Planting")

        InlineStat("Planted", fmtDate(plantedDate))

        planting.germinationDate?.let { millis ->
            val sproutDate = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
            InlineStat("Sprouted", fmtDate(sproutDate))
        }

        InlineStat("Age", "$age days")
        InlineStat("Source", planting.source.displayFormat())
        InlineStat("Status", planting.status.displayFormat())

        InlineStat("Est. Harvest", "${fmtDate(earliestHarvest)} – ${fmtDate(latestHarvest)}")

        when {
            today > latestHarvest -> {
                val daysPast = ChronoUnit.DAYS.between(latestHarvest, today)
                InlineStat("Harvest", "$daysPast days past expected harvest")
            }
            today >= earliestHarvest -> {
                InlineStat("Harvest", "Ready to harvest")
            }
            else -> {
                val daysRemaining = ChronoUnit.DAYS.between(today, earliestHarvest)
                InlineStat("Harvest", "~$daysRemaining days to harvest")
            }
        }

        planting.notes?.takeIf { it.isNotBlank() }?.let {
            Text("Notes", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(it, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        }
    }
}

// ── Quick Stats ─────────────────────────────────────────────────────────────────

@Composable
private fun QuickStatsRow(
    plant: PlantInfo,
    dthMin: Int = plant.daysToHarvestMin,
    dthMax: Int = plant.daysToHarvestMax,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        plant.waterInchesPerWeek?.let {
            val display = if (it % 1 == 0f) "${it.toInt()}" else "$it"
            QuickStatCard(Icons.Outlined.WaterDrop, "$display\"/wk", "Water")
        }
        QuickStatCard(Icons.Outlined.WbSunny, plant.sunRequirement.displayFormat(), "Sun")
        plant.spacingInches?.let {
            QuickStatCard(Icons.Outlined.Straighten, "$it\"", "Spacing")
        }
        plant.plantingDepthInches?.let {
            val display = if (it % 1 == 0f) "${it.toInt()}" else "$it"
            QuickStatCard(Icons.Outlined.Grass, "$display\"", "Depth")
        }
        QuickStatCard(
            Icons.Outlined.CalendarMonth,
            "$dthMin–${dthMax}d",
            "Harvest",
        )
    }
}

@Composable
private fun QuickStatCard(icon: ImageVector, value: String, label: String) {
    Box(
        modifier = Modifier
            .background(Charcoal, RoundedCornerShape(AppRadius.card))
            .border(1.dp, BorderVisible, RoundedCornerShape(AppRadius.card)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs),
        ) {
            Icon(icon, contentDescription = null, tint = GardenGlow, modifier = Modifier.size(20.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

// ── Planting Timeline ───────────────────────────────────────────────────────────

@Composable
private fun PlantingTimeline(windows: List<PlantingWindow>) {
    AppSectionHeader("Planting Windows")
    Panel {
        windows.forEachIndexed { index, window ->
            if (index > 0) Spacer(Modifier.height(AppSpacing.xs))
            Tag(text = window.method.displayFormat(), accentColor = GardenGlow, selected = true)
            Spacer(Modifier.height(AppSpacing.xxs))
            PlantingTimelineBar(window.startMonth, window.endMonth)
            MonthLabelsRow()
            window.notes?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun PlantingTimelineBar(startMonth: Int, endMonth: Int) {
    val activeMonths = if (startMonth <= endMonth) {
        (startMonth..endMonth).toSet()
    } else {
        ((startMonth..12) + (1..endMonth)).toSet()
    }

    Canvas(modifier = Modifier.fillMaxWidth().height(20.dp)) {
        val cellWidth = size.width / 12f
        val gap = 2.dp.toPx()
        val radius = 3.dp.toPx()

        for (month in 1..12) {
            val x = (month - 1) * cellWidth + gap / 2
            val w = cellWidth - gap
            drawRoundRect(
                color = if (month in activeMonths) GardenGlow else Slate,
                topLeft = Offset(x, 0f),
                size = Size(w, size.height),
                cornerRadius = CornerRadius(radius),
            )
        }
    }
}

@Composable
private fun MonthLabelsRow() {
    val labels = listOf("J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")
    Row(modifier = Modifier.fillMaxWidth()) {
        labels.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
            )
        }
    }
}

// ── Growing Requirements ────────────────────────────────────────────────────────

@Composable
private fun GrowingRequirementsPanel(plant: PlantInfo) {
    val hasData = plant.minSoilTempF != null || plant.soilPH != null ||
        plant.fertilizerType != null || plant.fertilizerFrequency != null ||
        plant.stakingRequired != null || plant.mulchRecommended != null ||
        plant.plantHeight != null || plant.rowSpacingInches != null
    if (!hasData) return

    Panel {
        PanelTitle(Icons.Outlined.Thermostat, "Growing Requirements")
        plant.minSoilTempF?.let { InlineStat("Min Soil Temp", "${it}°F") }
        plant.soilPH?.let { InlineStat("Soil pH", it) }
        plant.fertilizerType?.let { InlineStat("Fertilizer", it.formatFertilizer()) }
        plant.fertilizerFrequency?.let { InlineStat("Feed Schedule", it.displayFormat()) }
        plant.stakingRequired?.let { InlineStat("Staking", if (it) "Required" else "Not needed") }
        plant.mulchRecommended?.let { InlineStat("Mulch", if (it) "Recommended" else "Optional") }
        plant.plantHeight?.let { InlineStat("Height", it) }
        plant.rowSpacingInches?.let { InlineStat("Row Spacing", "$it\"") }
    }
}

// ── Germination Tips ────────────────────────────────────────────────────────────

@Composable
private fun GerminationTipCard(plant: PlantInfo) {
    val hasData = plant.germinationDaysMin != null || plant.seedSoakHours != null ||
        plant.scarification == true || plant.indoorStartWeeksBefore != null ||
        plant.startNotes?.isNotBlank() == true
    if (!hasData) return

    Panel {
        PanelTitle(Icons.Outlined.Lightbulb, "Germination")
        plant.germinationDaysMin?.let { min ->
            val range = plant.germinationDaysMax?.let { "$min–$it days" } ?: "$min days"
            InlineStat("Days to Germinate", range)
        }
        plant.seedSoakHours?.let { InlineStat("Seed Soak", "$it hrs") }
        if (plant.scarification == true) InlineStat("Scarification", "Needed")
        plant.indoorStartWeeksBefore?.let { InlineStat("Indoor Start", "$it wks before last frost") }
        plant.startNotes?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

// ── Companion Plants ────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompanionPlantsSection(plant: PlantInfo) {
    val good = plant.companionPlants.split(",").map { it.trim() }.filter { it.isNotBlank() }
    val bad = plant.incompatiblePlants.split(",").map { it.trim() }.filter { it.isNotBlank() }
    if (good.isEmpty() && bad.isEmpty()) return

    AppSectionHeader("Companion Plants")
    Panel {
        if (good.isNotEmpty()) {
            Text("Good Companions", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                good.forEach { Tag(text = it, accentColor = GardenGlow, selected = true) }
            }
        }
        if (bad.isNotEmpty()) {
            Text("Avoid With", style = MaterialTheme.typography.labelSmall, color = StatusBad)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                bad.forEach {
                    Tag(
                        text = it,
                        accentColor = StatusBad,
                        selected = true,
                        leadingIcon = {
                            Icon(Icons.Outlined.Block, null, modifier = Modifier.size(14.dp), tint = StatusBad)
                        },
                    )
                }
            }
        }
    }
}

// ── Harvest Tips ────────────────────────────────────────────────────────────────

@Composable
private fun HarvestTipsPanel(plant: PlantInfo) {
    val hasData = plant.harvestIndicators != null || plant.harvestFrequency != null ||
        plant.yieldPerPlant != null || plant.harvestMethod != null
    if (!hasData) return

    Panel {
        PanelTitle(Icons.Outlined.CalendarMonth, "Harvest")
        plant.harvestIndicators?.takeIf { it.isNotBlank() }?.let {
            Text("Ready When", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(it, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        }
        plant.harvestFrequency?.let { InlineStat("Frequency", it.displayFormat()) }
        plant.yieldPerPlant?.let { InlineStat("Yield / Plant", it) }
        plant.harvestMethod?.let { InlineStat("Method", it.displayFormat()) }
    }
}

// ── Pest & Disease ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PestDiseasePanel(plant: PlantInfo) {
    val pests = plant.commonPests?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
    val diseases = plant.commonDiseases?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
    if (pests.isEmpty() && diseases.isEmpty()) return

    Panel {
        PanelTitle(Icons.Outlined.BugReport, "Pests & Disease")
        if (pests.isNotEmpty()) {
            Text("Common Pests", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                pests.forEach { Tag(text = it, accentColor = StatusWarn, selected = true) }
            }
        }
        if (diseases.isNotEmpty()) {
            Text("Common Diseases", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                diseases.forEach { Tag(text = it, accentColor = StatusBad, selected = true) }
            }
        }
        plant.pestNotes?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

// ── Care Notes ──────────────────────────────────────────────────────────────────

@Composable
private fun CareNotesPanel(plant: PlantInfo) {
    val hasData = plant.pruningNotes?.isNotBlank() == true ||
        plant.thinningNotes?.isNotBlank() == true
    if (!hasData) return

    Panel {
        PanelTitle(Icons.Outlined.Grass, "Care Notes")
        plant.pruningNotes?.takeIf { it.isNotBlank() }?.let {
            Text("Pruning", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(it, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        }
        plant.thinningNotes?.takeIf { it.isNotBlank() }?.let {
            Text("Thinning", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(it, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        }
    }
}

// ── Climate Tips ────────────────────────────────────────────────────────────────

@Composable
private fun ClimateTipsPanel(plant: PlantInfo) {
    val hasData = plant.heatTips?.isNotBlank() == true ||
        plant.coldTips?.isNotBlank() == true
    if (!hasData) return

    Panel {
        PanelTitle(Icons.Outlined.Thermostat, "Climate Tips")
        plant.heatTips?.takeIf { it.isNotBlank() }?.let {
            Text("Heat", style = MaterialTheme.typography.labelSmall, color = StatusWarn)
            Text(it, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        }
        plant.coldTips?.takeIf { it.isNotBlank() }?.let {
            Text("Cold", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(it, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        }
    }
}

// ── Rotation & Storage ──────────────────────────────────────────────────────────

@Composable
private fun RotationStoragePanel(plant: PlantInfo) {
    val hasData = plant.rotationGroup != null ||
        plant.rotationNotes?.isNotBlank() == true ||
        plant.storageNotes?.isNotBlank() == true
    if (!hasData) return

    Panel {
        PanelTitle(Icons.Outlined.CalendarMonth, "Rotation & Storage")
        plant.rotationGroup?.let { InlineStat("Rotation Group", it.displayFormat()) }
        plant.rotationNotes?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        plant.storageNotes?.takeIf { it.isNotBlank() }?.let {
            Text("Storage", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(it, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        }
    }
}

// ── Varieties ───────────────────────────────────────────────────────────────────

@Composable
private fun VarietiesSection(varieties: List<PlantVariety>) {
    AppSectionHeader("Varieties")
    Panel(contentPadding = PaddingValues(0.dp)) {
        varieties.forEachIndexed { index, variety ->
            val days = listOfNotNull(variety.daysToHarvestMin, variety.daysToHarvestMax)
                .joinToString("–")
            ListRow(
                title = variety.name,
                subtitle = variety.description,
                trailingText = if (days.isNotBlank()) "${days}d" else null,
                showDivider = index != varieties.lastIndex,
            )
        }
    }
}
