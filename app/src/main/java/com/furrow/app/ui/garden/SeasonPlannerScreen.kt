package com.furrow.app.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTextField
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.SecondaryButton
import com.furrow.app.ui.components.Tag
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.util.DateUtil
import com.furrow.app.util.displayFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun SeasonPlannerScreen(
    onBack: () -> Unit,
    onFinished: () -> Unit,
    viewModel: SeasonPlannerViewModel = hiltViewModel(),
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val selections by viewModel.selections.collectAsState()
    val allPlants by viewModel.allPlants.collectAsState()
    val activeBeds by viewModel.activeBeds.collectAsState()
    val activeWindows by viewModel.activeWindows.collectAsState()
    val alreadyPlanted by viewModel.alreadyPlantedNames.collectAsState()
    val rotationWarnings by viewModel.rotationWarnings.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    val fieldColors = AppTextFieldDefaults.colors(accentColor = GardenGlow)

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(
                title = "Plan Season",
                subtitle = "Step $currentStep of 4",
                navigationIcon = {
                    IconButton(onClick = { if (currentStep > 1) viewModel.prevStep() else onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextSecondary,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Step indicator
            StepIndicator(
                currentStep = currentStep,
                modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
            )

            // Step content
            Box(modifier = Modifier.weight(1f)) {
                when (currentStep) {
                    1 -> PlantSelectionStep(
                        allPlants = allPlants,
                        activeWindows = activeWindows,
                        alreadyPlanted = alreadyPlanted,
                        selections = selections,
                        onToggle = viewModel::togglePlant,
                        fieldColors = fieldColors,
                    )
                    2 -> BedAssignmentStep(
                        selections = selections,
                        activeBeds = activeBeds,
                        rotationWarnings = rotationWarnings,
                        onAssignBed = viewModel::assignBed,
                        fieldColors = fieldColors,
                    )
                    3 -> TimelineStep(
                        selections = selections,
                        allPlants = allPlants,
                    )
                    4 -> SummaryStep(
                        selections = selections,
                        activeBeds = activeBeds,
                    )
                }
            }

            // Fixed bottom bar
            HorizontalDivider(color = BorderSubtle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                if (currentStep > 1) {
                    SecondaryButton(
                        text = "Back",
                        onClick = { viewModel.prevStep() },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (currentStep < 4) {
                    PrimaryButton(
                        text = "Next",
                        onClick = { viewModel.nextStep() },
                        enabled = when (currentStep) {
                            1 -> selections.isNotEmpty()
                            2 -> selections.any { it.assignedBedId != null }
                            else -> true
                        },
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    PrimaryButton(
                        text = "Save Plan",
                        onClick = {
                            viewModel.savePlan()
                            onFinished()
                        },
                        enabled = selections.any { it.assignedBedId != null },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  STEP INDICATOR
// ═══════════════════════════════════════════════════════════════

@Composable
private fun StepIndicator(currentStep: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (step in 1..4) {
            val isActive = step <= currentStep
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isActive) GardenGlow else BorderSubtle),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$step",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) TextPrimary else TextTertiary,
                )
            }
            if (step < 4) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(2.dp)
                        .background(if (step < currentStep) GardenGlow else BorderSubtle),
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  STEP 1: PLANT SELECTION
// ═══════════════════════════════════════════════════════════════

@Composable
private fun PlantSelectionStep(
    allPlants: List<PlantInfo>,
    activeWindows: List<PlantingWindow>,
    alreadyPlanted: Set<String>,
    selections: List<PlantSelection>,
    onToggle: (PlantInfo, PlantingWindow?) -> Unit,
    fieldColors: androidx.compose.material3.TextFieldColors,
) {
    var searchQuery by remember { mutableStateOf("") }
    var showInWindowOnly by remember { mutableStateOf(true) }
    val windowNames = activeWindows.map { it.plantName }.toSet()
    val selectedNames = selections.map { it.plantInfo.name }.toSet()
    val windowMap = activeWindows.associateBy { it.plantName }

    val baseList = if (showInWindowOnly) {
        allPlants.filter { it.name in windowNames }
    } else {
        allPlants
    }

    val filtered = if (searchQuery.isBlank()) baseList
    else baseList.filter { it.name.contains(searchQuery, ignoreCase = true) }

    val grouped = filtered.groupBy { it.category }
        .toSortedMap()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppSpacing.md,
            end = AppSpacing.md,
            top = AppSpacing.xs,
            bottom = AppSpacing.sm,
        ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        item(key = "filter") {
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                Tag(
                    text = "In Window (${windowNames.size})",
                    selected = showInWindowOnly,
                    onClick = { showInWindowOnly = true },
                    accentColor = GardenGlow,
                )
                Tag(
                    text = "All Plants",
                    selected = !showInWindowOnly,
                    onClick = { showInWindowOnly = false },
                    accentColor = GardenGlow,
                )
            }
        }

        item(key = "search") {
            AppTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search plants") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
            )
        }

        grouped.forEach { (category, plants) ->
            item(key = "header_$category") {
                Text(
                    text = category.displayFormat(),
                    style = MaterialTheme.typography.titleSmall,
                    color = GardenGlow,
                    modifier = Modifier.padding(top = AppSpacing.sm),
                )
            }

            item(key = "panel_$category") {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    plants.forEachIndexed { index, plant ->
                        val isSelected = plant.name in selectedNames
                        val inWindow = plant.name in windowNames
                        val grewLastSeason = plant.name in alreadyPlanted

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggle(plant, windowMap[plant.name]) }
                                .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Outlined.CheckCircle
                                else Icons.Outlined.RadioButtonUnchecked,
                                contentDescription = if (isSelected) "Selected" else "Not selected",
                                tint = if (isSelected) GardenGlow else TextTertiary,
                                modifier = Modifier.size(22.dp),
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = plant.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                )
                                Text(
                                    text = "${plant.daysToHarvestMin}-${plant.daysToHarvestMax}d  ·  ${plant.sunRequirement.displayFormat()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                            if (inWindow) {
                                Tag(text = "In Window", selected = true, accentColor = GardenGlow)
                            }
                            if (grewLastSeason) {
                                Tag(text = "Growing", selected = false, accentColor = GardenGlow)
                            }
                        }
                        if (index != plants.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = AppSpacing.md),
                                color = BorderSubtle,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  STEP 2: BED ASSIGNMENT
// ═══════════════════════════════════════════════════════════════

@Composable
private fun BedAssignmentStep(
    selections: List<PlantSelection>,
    activeBeds: List<com.furrow.app.data.local.entity.GardenBed>,
    rotationWarnings: List<RotationWarning>,
    onAssignBed: (String, Long) -> Unit,
    fieldColors: androidx.compose.material3.TextFieldColors,
) {
    val bedNames = activeBeds.map { it.name }
    val bedMap = activeBeds.associateBy { it.name }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppSpacing.md,
            end = AppSpacing.md,
            top = AppSpacing.xs,
            bottom = AppSpacing.sm,
        ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        items(selections, key = { it.plantInfo.name }) { sel ->
            val warning = rotationWarnings.firstOrNull { it.plantName == sel.plantInfo.name }
            val currentBedName = activeBeds.firstOrNull { it.id == sel.assignedBedId }?.name

            Panel {
                Text(
                    text = sel.plantInfo.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                )
                Text(
                    text = "${sel.plantInfo.category.displayFormat()}  ·  ${sel.plantInfo.daysToHarvestMin}-${sel.plantInfo.daysToHarvestMax}d",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )

                Spacer(modifier = Modifier.height(AppSpacing.xs))

                DropdownSelector(
                    label = "Assign to Bed",
                    options = bedNames,
                    selected = currentBedName ?: "",
                    onSelect = { name ->
                        bedMap[name]?.let { bed -> onAssignBed(sel.plantInfo.name, bed.id) }
                    },
                    accentColor = GardenGlow,
                )

                if (warning != null) {
                    Row(
                        modifier = Modifier.padding(top = AppSpacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = "Rotation warning",
                            tint = StatusWarn,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "Same crop family (${warning.category.displayFormat()}) was in ${warning.bedName} last year",
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusWarn,
                        )
                    }
                }

                // Companion/incompatible warnings
                val assignedBed = activeBeds.firstOrNull { it.id == sel.assignedBedId }
                val checkCompanion = assignedBed?.checkCompanionPlanting ?: true
                val sameBedSelections = selections.filter {
                    it.assignedBedId == sel.assignedBedId && it.assignedBedId != null && it.plantInfo.name != sel.plantInfo.name
                }
                val incompatible = sel.plantInfo.incompatiblePlants.split(",").map { it.trim().lowercase() }.filter { it.isNotBlank() }
                sameBedSelections.forEach { other ->
                    if (checkCompanion && other.plantInfo.name.lowercase() in incompatible) {
                        Row(
                            modifier = Modifier.padding(top = AppSpacing.xxs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = "Incompatible",
                                tint = StatusBad,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = "Incompatible with ${other.plantInfo.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = StatusBad,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  STEP 3: TIMELINE
// ═══════════════════════════════════════════════════════════════

@Composable
private fun TimelineStep(
    selections: List<PlantSelection>,
    allPlants: List<PlantInfo>,
) {
    val today = LocalDate.now()
    val monthStart = today.withDayOfMonth(1)
    val rangeEnd = monthStart.plusMonths(6)
    val totalDays = ChronoUnit.DAYS.between(monthStart, rangeEnd).toFloat()

    val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault())
    val months = (0L until 6L).map { monthStart.plusMonths(it) }

    val assigned = selections.filter { it.assignedBedId != null && it.targetDate != null }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppSpacing.md,
            end = AppSpacing.md,
            top = AppSpacing.xs,
            bottom = AppSpacing.sm,
        ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        // Month headers
        item(key = "months") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                months.forEach { month ->
                    Text(
                        text = month.format(monthFormatter),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        items(assigned, key = { "timeline_${it.plantInfo.name}" }) { sel ->
            val targetDate = Instant.ofEpochMilli(sel.targetDate!!)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val harvestDate = targetDate.plusDays(sel.plantInfo.daysToHarvestMin.toLong())

            val plantOffset = ChronoUnit.DAYS.between(monthStart, targetDate)
                .coerceAtLeast(0).toFloat() / totalDays
            val harvestEnd = ChronoUnit.DAYS.between(monthStart, harvestDate)
                .coerceAtMost(totalDays.toLong()).toFloat() / totalDays
            val barWidth = (harvestEnd - plantOffset).coerceAtLeast(0.02f)

            Column {
                Text(
                    text = sel.plantInfo.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                ) {
                    // Growing bar
                    TimelineBar(
                        startFraction = plantOffset,
                        widthFraction = barWidth,
                        color = GardenGlow,
                    )
                }

                if (sel.plantInfo.canSuccessionPlant == true) {
                    Text(
                        text = "Can succession plant every ${sel.plantInfo.successionPlantingDays ?: sel.plantInfo.daysToHarvestMin}d",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineBar(
    startFraction: Float,
    widthFraction: Float,
    color: androidx.compose.ui.graphics.Color,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .height(14.dp)
                .align(Alignment.CenterStart)
                .padding(start = (startFraction * 300).dp.coerceAtMost(280.dp))
                .clip(MaterialTheme.shapes.extraSmall)
                .background(color.copy(alpha = 0.6f)),
        )
    }
}

// ═══════════════════════════════════════════════════════════════
//  STEP 4: SUMMARY
// ═══════════════════════════════════════════════════════════════

@Composable
private fun SummaryStep(
    selections: List<PlantSelection>,
    activeBeds: List<com.furrow.app.data.local.entity.GardenBed>,
) {
    val bedMap = activeBeds.associateBy { it.id }
    val assigned = selections.filter { it.assignedBedId != null }
    val byBed = assigned.groupBy { it.assignedBedId!! }

    val dateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppSpacing.md,
            end = AppSpacing.md,
            top = AppSpacing.xs,
            bottom = AppSpacing.sm,
        ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        item(key = "total") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Season Plan",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Tag(
                    text = "${assigned.size} plants",
                    selected = true,
                    accentColor = GardenGlow,
                )
            }
        }

        byBed.forEach { (bedId, sels) ->
            val bedName = bedMap[bedId]?.name ?: "Bed $bedId"

            item(key = "bed_$bedId") {
                Panel {
                    Text(
                        text = bedName,
                        style = MaterialTheme.typography.titleSmall,
                        color = GardenGlow,
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.xs))

                    sels.forEach { sel ->
                        val targetDate = sel.targetDate?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        val harvestDate = targetDate?.plusDays(sel.plantInfo.daysToHarvestMin.toLong())

                        Column(modifier = Modifier.padding(vertical = AppSpacing.xxs)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = sel.plantInfo.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                )
                                Text(
                                    text = sel.window?.method?.displayFormat() ?: "Transplant",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary,
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            ) {
                                targetDate?.let {
                                    Text(
                                        text = "Plant: ${it.format(dateFormatter)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                    )
                                }
                                harvestDate?.let {
                                    Text(
                                        text = "Harvest: ~${it.format(dateFormatter)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
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
