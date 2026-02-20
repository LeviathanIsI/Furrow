package com.furrow.app.ui.garden.tabs

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.ui.components.AppButtonSecondary
import com.furrow.app.ui.components.AppCard
import com.furrow.app.ui.components.AppChip
import com.furrow.app.ui.components.StatusPill
import com.furrow.app.ui.components.TimelineBar
import com.furrow.app.ui.garden.HarvestPrediction
import com.furrow.app.ui.theme.AppRadius
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.Obsidian
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private val zone: ZoneId = ZoneId.systemDefault()
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d")

private const val DEFAULT_GROWING_DAYS = 120L

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
internal fun PlantingsTab(
    plantings: List<Planting>,
    plantInfoMap: Map<String, PlantInfo>,
    zoneWindows: List<PlantingWindow>,
    harvestPredictions: Map<Long, HarvestPrediction>,
    onLongPress: (Planting) -> Unit,
    onPlantInfoClick: (PlantInfo) -> Unit,
    onMarkSprouted: (Planting) -> Unit = {},
) {
    val view = LocalView.current

    if (plantings.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Outlined.Eco,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = GardenGlow.copy(alpha = 0.4f),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No plantings yet",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary,
                )
                Text(
                    "Tap + to add a planting",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                )
            }
        }
    } else {
        val today = remember { LocalDate.now(zone) }
        val currentMonth = today.monthValue

        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = AppSpacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(plantings, key = { it.id }) { planting ->
                val plantedDate = remember(planting.datePlanted) {
                    Instant.ofEpochMilli(planting.datePlanted).atZone(zone).toLocalDate()
                }
                val daysSincePlanting = ChronoUnit.DAYS.between(plantedDate, today)
                val plantInfo = plantInfoMap[planting.plantName]

                val avgDtm = if (plantInfo != null) {
                    ((plantInfo.daysToHarvestMin + plantInfo.daysToHarvestMax) / 2).toLong()
                } else {
                    DEFAULT_GROWING_DAYS
                }
                val progress = (daysSincePlanting.toFloat() / avgDtm).coerceIn(0f, 1f)
                val growthStage = growthStageForProgress(progress)
                val prediction = harvestPredictions[planting.id]

                PlantingCard(
                    planting = planting,
                    plantInfo = plantInfo,
                    plantedDate = plantedDate,
                    daysSincePlanting = daysSincePlanting,
                    avgDtm = avgDtm,
                    progress = progress,
                    growthStage = growthStage,
                    prediction = prediction,
                    today = today,
                    currentMonth = currentMonth,
                    zoneWindows = zoneWindows,
                    onLongPress = {
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        onLongPress(planting)
                    },
                    onPlantInfoClick = { plantInfo?.let { onPlantInfoClick(it) } },
                    onMarkSprouted = { onMarkSprouted(planting) },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun PlantingCard(
    planting: Planting,
    plantInfo: PlantInfo?,
    plantedDate: LocalDate,
    daysSincePlanting: Long,
    avgDtm: Long,
    progress: Float,
    growthStage: GrowthStage,
    prediction: HarvestPrediction?,
    today: LocalDate,
    currentMonth: Int,
    zoneWindows: List<PlantingWindow>,
    onLongPress: () -> Unit,
    onPlantInfoClick: () -> Unit,
    onMarkSprouted: () -> Unit,
) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongPress,
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() },
            ),
    ) {
        // Row 1: Plant name + status badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val displayName = if (planting.variety != null) {
                "${planting.plantName} \u2014 ${planting.variety}"
            } else {
                planting.plantName
            }
            Text(
                displayName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                modifier = if (plantInfo != null) Modifier.clickable { onPlantInfoClick() }
                else Modifier,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            PlantingStatusBadge(planting.status)
        }

        // Row 2: Growth stage + day counter
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GrowthStagePill(growthStage)
            Text(
                if (plantInfo != null) "Day $daysSincePlanting / $avgDtm"
                else "Day $daysSincePlanting",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }

        // Growth progress + harvest timeline
        if (planting.status == "growing" || planting.status == "producing") {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = GardenGlow,
                trackColor = Obsidian,
                gapSize = 0.dp,
            )

            if (prediction != null) {
                Spacer(modifier = Modifier.height(6.dp))
                TimelineBar(
                    startDate = plantedDate,
                    endDate = prediction.latestHarvest,
                    windowStart = prediction.earliestHarvest,
                    windowEnd = prediction.latestHarvest,
                    today = today,
                    glowColor = GardenGlow,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(4.dp))
                when {
                    prediction.isOverdue -> {
                        Text(
                            "Overdue \u2014 harvest now",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusBad,
                        )
                    }
                    prediction.isReady -> {
                        Text(
                            "Ready to harvest! Window closes ${prediction.latestHarvest.format(dateFormatter)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusGood,
                        )
                    }
                    else -> {
                        val daysLeft = prediction.daysUntilEarliest
                        Text(
                            "Harvest: ${prediction.earliestHarvest.format(dateFormatter)} \u2013 ${prediction.latestHarvest.format(dateFormatter)} (~${daysLeft}d)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                        )
                    }
                }
            }
        }

        // Planted date + source
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Planted ${plantedDate.format(dateFormatter)} \u00b7 ${planting.source.replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
        )

        // Quick-reference row (water, sun, temp)
        if (plantInfo != null) {
            val hasQuickRef = plantInfo.waterInchesPerWeek != null ||
                plantInfo.sunRequirement.isNotBlank() ||
                (plantInfo.minTempF != null && plantInfo.maxTempF != null)
            if (hasQuickRef) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(AppRadius.input),
                    color = Obsidian,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        plantInfo.waterInchesPerWeek?.let { water ->
                            Text(
                                "\uD83D\uDCA7 ${water}\"/wk",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary,
                            )
                        }
                        if (plantInfo.sunRequirement.isNotBlank()) {
                            Text(
                                "\u2600\uFE0F ${plantInfo.sunRequirement.replaceFirstChar { it.uppercase() }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary,
                            )
                        }
                        if (plantInfo.minTempF != null && plantInfo.maxTempF != null) {
                            Text(
                                "\uD83C\uDF21\uFE0F ${plantInfo.minTempF}-${plantInfo.maxTempF}\u00b0F",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary,
                            )
                        }
                    }
                }
            }
        }

        // Companions
        if (plantInfo != null && plantInfo.companionPlants.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Companions:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                )
                Spacer(modifier = Modifier.width(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    plantInfo.companionPlants.split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .forEach { companion ->
                            Surface(
                                shape = RoundedCornerShape(AppRadius.chip),
                                color = Obsidian,
                            ) {
                                AppChip(
                                    text = companion,
                                    selected = false,
                                    accentColor = GardenGlow,
                                )
                            }
                        }
                }
            }
        }

        // Germination tracking
        if (planting.source == "seed" && planting.status == "growing") {
            if (planting.germinationDate != null) {
                val sproutDate = remember(planting.germinationDate) {
                    Instant.ofEpochMilli(planting.germinationDate).atZone(zone).toLocalDate()
                }
                val sproutInfo = buildString {
                    append("Sprouted ${sproutDate.format(dateFormatter)}")
                    if (planting.seedsSprouted != null && planting.seedsPlanted != null) {
                        append(" (${planting.seedsSprouted} of ${planting.seedsPlanted} seeds)")
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    sproutInfo,
                    style = MaterialTheme.typography.labelSmall,
                    color = GardenGlow,
                )
            } else {
                val germMin = plantInfo?.germinationDaysMin
                val germMax = plantInfo?.germinationDaysMax
                if (germMin != null && germMax != null) {
                    val expectedStart = plantedDate.plusDays(germMin.toLong())
                    val expectedEnd = plantedDate.plusDays(germMax.toLong())
                    val isOverdue = today.isAfter(expectedEnd)
                    Spacer(modifier = Modifier.height(4.dp))
                    if (isOverdue) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                Icons.Outlined.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = StatusBad,
                            )
                            Text(
                                "Overdue \u2014 check seeds",
                                style = MaterialTheme.typography.labelSmall,
                                color = StatusBad,
                            )
                        }
                    } else {
                        Text(
                            "Expected sprouts: ~${expectedStart.format(dateFormatter)} \u2013 ${expectedEnd.format(dateFormatter)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                AppButtonSecondary(
                    text = "Mark Sprouted",
                    onClick = onMarkSprouted,
                    height = 32.dp,
                )
            }
        }

        // Notes
        planting.notes?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = TextTertiary,
            )
        }
    }
}

// ── Helper types ──

internal enum class GrowthStage(val label: String, val emoji: String) {
    SEEDLING("Seedling", "\uD83C\uDF31"),
    VEGETATIVE("Growing", "\uD83C\uDF3F"),
    FLOWERING("Flowering", "\uD83C\uDF38"),
    FRUITING("Fruiting", "\uD83C\uDF45"),
    READY("Ready", "\u2705"),
}

internal fun growthStageForProgress(progress: Float): GrowthStage = when {
    progress < 0.2f -> GrowthStage.SEEDLING
    progress < 0.5f -> GrowthStage.VEGETATIVE
    progress < 0.7f -> GrowthStage.FLOWERING
    progress < 0.9f -> GrowthStage.FRUITING
    else -> GrowthStage.READY
}

@Composable
private fun GrowthStagePill(stage: GrowthStage) {
    val stageColor = when (stage) {
        GrowthStage.SEEDLING -> GardenGlow.copy(alpha = 0.7f)
        GrowthStage.VEGETATIVE -> GardenGlow
        GrowthStage.FLOWERING -> GardenGlow
        GrowthStage.FRUITING -> GardenGlow
        GrowthStage.READY -> StatusGood
    }
    AppChip(
        text = "${stage.emoji} ${stage.label}",
        selected = stage == GrowthStage.READY,
        accentColor = stageColor,
    )
}

internal sealed class SeasonStatus(val label: String) {
    data object InSeason : SeasonStatus("In season now")
    data class PlantIn(val monthName: String) : SeasonStatus("Plant in $monthName")
    data object OutOfSeason : SeasonStatus("Out of season")
}

internal fun seasonStatusFor(
    plantName: String,
    zoneWindows: List<PlantingWindow>,
    currentMonth: Int,
): SeasonStatus {
    val windows = zoneWindows.filter { it.plantName == plantName }
    if (windows.isEmpty()) return SeasonStatus.OutOfSeason

    val inSeason = windows.any { w ->
        if (w.startMonth <= w.endMonth) {
            currentMonth in w.startMonth..w.endMonth
        } else {
            currentMonth >= w.startMonth || currentMonth <= w.endMonth
        }
    }
    if (inSeason) return SeasonStatus.InSeason

    val futureWindow = windows
        .filter { it.startMonth > currentMonth }
        .minByOrNull { it.startMonth }
        ?: windows.minByOrNull { it.startMonth }

    return if (futureWindow != null) {
        val monthName = Month.of(futureWindow.startMonth)
            .getDisplayName(JavaTextStyle.SHORT, Locale.getDefault())
        SeasonStatus.PlantIn(monthName)
    } else {
        SeasonStatus.OutOfSeason
    }
}

@Composable
internal fun PlantingStatusBadge(status: String) {
    val (label, borderColor, active) = when (status) {
        "growing" -> Triple("Growing", GardenGlow, true)
        "producing" -> Triple("Producing", GardenGlow, true)
        "finished" -> Triple("Finished", TextTertiary, false)
        "failed" -> Triple("Failed", StatusBad, false)
        else -> Triple(status.replaceFirstChar { it.uppercase() }, TextTertiary, false)
    }
    StatusPill(
        text = label,
        active = active,
        error = borderColor == StatusBad,
    )
}
