package com.furrow.app.ui.garden

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.foundation.Canvas
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.material3.ripple
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.theme.FurrowBackground
import com.furrow.app.ui.theme.FurrowCardDefaults
import com.furrow.app.ui.theme.shimmer
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private val zone: ZoneId = ZoneId.systemDefault()
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

private const val DEFAULT_GROWING_DAYS = 120L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BedDetailScreen(
    onBack: () -> Unit,
    onAddPlanting: (Long) -> Unit,
    onAddHarvest: (Long) -> Unit,
    onEditPlanting: (Long, Long) -> Unit,
    onEditHarvest: (Long, Long) -> Unit,
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val bed by viewModel.selectedBed.collectAsState()
    val plantings by viewModel.plantings.collectAsState()
    val harvests by viewModel.harvests.collectAsState()
    val plantInfoMap by viewModel.plantInfoMap.collectAsState()
    val zoneWindows by viewModel.zoneWindows.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("PLANTINGS", "HARVESTS")
    var plantingToDelete by remember { mutableStateOf<Planting?>(null) }
    var harvestToDelete by remember { mutableStateOf<HarvestLog?>(null) }
    var selectedPlantDetail by remember { mutableStateOf<PlantInfo?>(null) }
    var plantingForAction by remember { mutableStateOf<Planting?>(null) }
    var harvestForAction by remember { mutableStateOf<HarvestLog?>(null) }

    val fabInteraction = remember { MutableInteractionSource() }
    val isFabPressed by fabInteraction.collectIsPressedAsState()
    val fabScale by animateFloatAsState(
        targetValue = if (isFabPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "fabScale",
    )

    val plantingNames = remember(plantings) {
        plantings.associate { it.id to it.plantName }
    }

    // Compute harvest totals per planting
    val harvestTotals = remember(harvests) {
        harvests.groupBy { it.plantingId }.mapValues { (_, logs) ->
            val totalOz = logs.sumOf { it.amountOz ?: 0.0 }
            val totalCount = logs.sumOf { it.count ?: 0 }
            HarvestTotal(totalOz, totalCount, logs.size)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(bed?.name ?: "Bed") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        floatingActionButton = {
            bed?.let { b ->
                FloatingActionButton(
                    onClick = {
                        if (selectedTab == 0) onAddPlanting(b.id) else onAddHarvest(b.id)
                    },
                    modifier = Modifier.graphicsLayer {
                        scaleX = fabScale
                        scaleY = fabScale
                    },
                    interactionSource = fabInteraction,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { padding ->
        FurrowBackground(modifier = Modifier.fillMaxSize().padding(padding)) {
        Column(modifier = Modifier.fillMaxSize()) {
            bed?.let { b ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = FurrowCardDefaults.elevatedCardColors,
                    elevation = FurrowCardDefaults.elevatedCardElevation,
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(b.name, style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                b.type.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            b.sunExposure?.let {
                                Text(
                                    it.replaceFirstChar { c -> c.uppercase() },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        val sizeText = when {
                            b.sizeGallons != null -> "${b.sizeGallons} gal"
                            b.lengthFt != null && b.widthFt != null -> "${b.lengthFt} x ${b.widthFt} ft"
                            else -> null
                        }
                        sizeText?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        b.soilType?.let {
                            Text(
                                "Soil: $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        b.notes?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            val count = if (index == 0) plantings.size else harvests.size
                            Text(
                                "$title ($count)",
                                letterSpacing = 1.2.sp,
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> PlantingList(
                    plantings, plantInfoMap, zoneWindows,
                    onLongPress = { plantingForAction = it },
                    onPlantInfoClick = { selectedPlantDetail = it },
                )
                1 -> HarvestList(harvests, plantingNames, harvestTotals, onLongPress = { harvestForAction = it })
            }
        }
        }
    }

    plantingToDelete?.let { planting ->
        DeleteConfirmationDialog(
            itemName = planting.plantName,
            onConfirm = { viewModel.deletePlanting(planting); plantingToDelete = null },
            onDismiss = { plantingToDelete = null },
        )
    }

    harvestToDelete?.let { harvest ->
        val name = plantingNames[harvest.plantingId] ?: "harvest"
        DeleteConfirmationDialog(
            itemName = "$name harvest",
            onConfirm = { viewModel.deleteHarvest(harvest); harvestToDelete = null },
            onDismiss = { harvestToDelete = null },
        )
    }

    selectedPlantDetail?.let { plant ->
        PlantDetailSheet(
            plant = plant,
            onDismiss = { selectedPlantDetail = null },
        )
    }

    plantingForAction?.let { planting ->
        ItemActionSheet(
            onDismiss = { plantingForAction = null },
            onEdit = { bed?.let { b -> onEditPlanting(b.id, planting.id) } },
            onDelete = { plantingToDelete = planting },
        )
    }

    harvestForAction?.let { harvest ->
        ItemActionSheet(
            onDismiss = { harvestForAction = null },
            onEdit = { bed?.let { b -> onEditHarvest(b.id, harvest.id) } },
            onDelete = { harvestToDelete = harvest },
        )
    }
}

private data class HarvestTotal(
    val totalOz: Double,
    val totalCount: Int,
    val entryCount: Int,
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun PlantingList(
    plantings: List<Planting>,
    plantInfoMap: Map<String, PlantInfo>,
    zoneWindows: List<PlantingWindow>,
    onLongPress: (Planting) -> Unit,
    onPlantInfoClick: (PlantInfo) -> Unit,
) {
    val view = LocalView.current
    if (plantings.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .shimmer(),
            ) {
                Icon(
                    Icons.Outlined.Eco,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No plantings yet",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Tap + to add a planting",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        val today = remember { LocalDate.now(zone) }
        val currentMonth = today.monthValue

        LazyColumn(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(plantings, key = { it.id }) { planting ->
                val plantedDate = remember(planting.datePlanted) {
                    Instant.ofEpochMilli(planting.datePlanted).atZone(zone).toLocalDate()
                }
                val daysSincePlanting = ChronoUnit.DAYS.between(plantedDate, today)
                val plantInfo = plantInfoMap[planting.plantName]

                // Use PlantInfo DTM if available, otherwise fallback
                val avgDtm = if (plantInfo != null) {
                    ((plantInfo.daysToHarvestMin + plantInfo.daysToHarvestMax) / 2).toLong()
                } else {
                    DEFAULT_GROWING_DAYS
                }
                val progress = (daysSincePlanting.toFloat() / avgDtm).coerceIn(0f, 1f)
                val growthStage = growthStageForProgress(progress)

                // Estimated harvest date
                val estHarvestDate = plantedDate.plusDays(avgDtm)

                // Season status
                val seasonStatus = seasonStatusFor(planting.plantName, zoneWindows, currentMonth)

                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                onLongPress(planting)
                            },
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() },
                        ),
                    colors = FurrowCardDefaults.outlinedCardColors,
                    border = FurrowCardDefaults.outlinedCardBorder,
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    planting.plantName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (plantInfo != null) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface,
                                    modifier = if (plantInfo != null) Modifier.clickable {
                                        onPlantInfoClick(plantInfo)
                                    } else Modifier,
                                )
                                planting.variety?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            PlantingStatusBadge(planting.status)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        // Growth stage + day count
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            GrowthStagePill(growthStage)
                            Text(
                                if (plantInfo != null) "Day $daysSincePlanting of $avgDtm"
                                else "Day $daysSincePlanting",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        // Progress bar
                        if (planting.status == "growing" || planting.status == "producing") {
                            Spacer(modifier = Modifier.height(6.dp))
                            GrowthProgressBar(
                                progress = progress,
                                stage = growthStage,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        // Estimated harvest + season status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (plantInfo != null) {
                                Text(
                                    "Est. harvest ${estHarvestDate.format(dateFormatter)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } else {
                                Text(
                                    "Planted ${plantedDate.format(dateFormatter)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            SeasonStatusPill(seasonStatus)
                        }
                        if (plantInfo != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Planted ${plantedDate.format(dateFormatter)} \u2022 ${planting.source.replaceFirstChar { it.uppercase() }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Source: ${planting.source.replaceFirstChar { it.uppercase() }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        planting.notes?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Companion plants
                        if (plantInfo != null && plantInfo.companionPlants.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    "Companions:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                                shape = MaterialTheme.shapes.small,
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                            ) {
                                                Text(
                                                    companion,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                )
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class GrowthStage(val label: String) {
    SEEDLING("Seedling"),
    VEGETATIVE("Vegetative"),
    FLOWERING("Flowering"),
    FRUITING("Fruiting"),
    READY("Ready to Harvest"),
}

@Composable
private fun GrowthStage.color() = when (this) {
    GrowthStage.SEEDLING -> MaterialTheme.colorScheme.tertiary
    GrowthStage.VEGETATIVE -> MaterialTheme.colorScheme.primary
    GrowthStage.FLOWERING -> MaterialTheme.colorScheme.secondary
    GrowthStage.FRUITING -> MaterialTheme.colorScheme.tertiary
    GrowthStage.READY -> MaterialTheme.colorScheme.primary
}

private fun growthStageForProgress(progress: Float): GrowthStage = when {
    progress < 0.2f -> GrowthStage.SEEDLING
    progress < 0.5f -> GrowthStage.VEGETATIVE
    progress < 0.7f -> GrowthStage.FLOWERING
    progress < 0.9f -> GrowthStage.FRUITING
    else -> GrowthStage.READY
}

@Composable
private fun GrowthStagePill(stage: GrowthStage) {
    val stageColor = stage.color()
    Surface(
        shape = MaterialTheme.shapes.small,
        color = stageColor.copy(alpha = 0.12f),
    ) {
        Text(
            stage.label,
            style = MaterialTheme.typography.labelSmall,
            color = stageColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

private sealed class SeasonStatus(val label: String) {
    data object InSeason : SeasonStatus("In season now")
    data class PlantIn(val monthName: String) : SeasonStatus("Plant in $monthName")
    data object OutOfSeason : SeasonStatus("Out of season")
}

private fun seasonStatusFor(
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

    // Find next upcoming window
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
private fun SeasonStatusPill(status: SeasonStatus) {
    val color = when (status) {
        is SeasonStatus.InSeason -> MaterialTheme.colorScheme.primary
        is SeasonStatus.PlantIn -> MaterialTheme.colorScheme.tertiary
        is SeasonStatus.OutOfSeason -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.12f),
    ) {
        Text(
            status.label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun PlantingStatusBadge(status: String) {
    val (label, color) = when (status) {
        "growing" -> "Growing" to MaterialTheme.colorScheme.primary
        "producing" -> "Producing" to MaterialTheme.colorScheme.tertiary
        "finished" -> "Finished" to MaterialTheme.colorScheme.onSurfaceVariant
        "failed" -> "Failed" to MaterialTheme.colorScheme.error
        else -> status.replaceFirstChar { it.uppercase() } to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.12f),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun GrowthProgressBar(
    progress: Float,
    stage: GrowthStage,
    modifier: Modifier = Modifier,
) {
    val stageColor = stage.color()
    val trackColor = stageColor.copy(alpha = 0.12f)
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(stageColor.copy(alpha = 0.4f), stageColor),
    )

    Canvas(modifier = modifier.height(8.dp).fillMaxWidth()) {
        val cornerPx = CornerRadius(4.dp.toPx())
        // Track
        drawRoundRect(
            color = trackColor,
            cornerRadius = cornerPx,
        )
        // Fill
        if (progress > 0f) {
            drawRoundRect(
                brush = gradientBrush,
                size = Size(size.width * progress, size.height),
                cornerRadius = cornerPx,
            )
        }
        // Thumb circle
        if (progress > 0.02f) {
            val thumbX = (size.width * progress).coerceIn(size.height / 2, size.width - size.height / 2)
            drawCircle(
                color = stageColor,
                radius = 6.dp.toPx(),
                center = Offset(thumbX, size.height / 2),
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = Offset(thumbX, size.height / 2),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HarvestList(
    harvests: List<HarvestLog>,
    plantingNames: Map<Long, String>,
    harvestTotals: Map<Long, HarvestTotal>,
    onLongPress: (HarvestLog) -> Unit,
) {
    val view = LocalView.current
    if (harvests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .shimmer(),
            ) {
                Icon(
                    Icons.Outlined.Scale,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No harvests yet",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Tap + to log a harvest",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        // Group harvests by planting
        val grouped = remember(harvests) {
            harvests.groupBy { it.plantingId }
        }

        LazyColumn(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            grouped.forEach { (plantingId, logs) ->
                val plantName = plantingNames[plantingId] ?: "Unknown"
                val total = harvestTotals[plantingId]

                // Planting header with total
                item(key = "header_$plantingId") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            plantName,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        total?.let {
                            val yieldText = buildString {
                                if (it.totalOz > 0) append(String.format(Locale.US, "%.1f oz", it.totalOz))
                                if (it.totalOz > 0 && it.totalCount > 0) append(" \u2022 ")
                                if (it.totalCount > 0) append("${it.totalCount} items")
                                append(" (${it.entryCount} harvests)")
                            }
                            Text(
                                yieldText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // Individual harvest entries
                items(logs, key = { it.id }) { harvest ->
                    val harvestDate = remember(harvest.date) {
                        Instant.ofEpochMilli(harvest.date).atZone(zone).toLocalDate()
                    }
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(harvest)
                                },
                                indication = ripple(),
                                interactionSource = remember { MutableInteractionSource() },
                            ),
                        colors = FurrowCardDefaults.outlinedCardColors,
                        border = FurrowCardDefaults.outlinedCardBorder,
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    harvestDate.format(dateFormatter),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    harvest.amountOz?.let {
                                        Text(
                                            "${it}oz",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                    harvest.count?.let {
                                        Text(
                                            "$it items",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }
                            harvest.notes?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun PlantDetailSheet(
    plant: PlantInfo,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                plant.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                plant.category.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            // Growing details
            PlantDetailRow("Days to Harvest", "${plant.daysToHarvestMin}-${plant.daysToHarvestMax} days")
            PlantDetailRow("Sun", plant.sunRequirement.replaceFirstChar { it.uppercase() })
            PlantDetailRow("Water", plant.waterFrequency.replaceFirstChar { it.uppercase() })
            PlantDetailRow("Hardiness Zone", "${plant.minZone}-${plant.maxZone}")
            if (plant.containerSuitable) {
                PlantDetailRow("Container", "Yes (min ${plant.containerMinGallons} gal)")
            }

            // Companion plants
            if (plant.companionPlants.isNotBlank()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                Text(
                    "COMPANION PLANTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    plant.companionPlants.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { name ->
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        ) {
                            Text(
                                name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }

            // Incompatible plants
            if (plant.incompatiblePlants.isNotBlank()) {
                Text(
                    "AVOID PLANTING WITH",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    letterSpacing = 1.sp,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    plant.incompatiblePlants.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { name ->
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                        ) {
                            Text(
                                name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }

            // Notes
            plant.notes?.let {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PlantDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
