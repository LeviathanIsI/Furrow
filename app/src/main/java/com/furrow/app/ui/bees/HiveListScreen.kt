package com.furrow.app.ui.bees

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.BeeRaceInfo
import com.furrow.app.data.local.entity.Hive
import com.furrow.app.data.local.entity.Inspection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalView
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.FurrowBottomSheet
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.SearchableSelector
import androidx.compose.foundation.BorderStroke
import com.furrow.app.ui.theme.CardBorderDark
import com.furrow.app.ui.theme.FurrowBackground
import com.furrow.app.ui.theme.LocalFurrowColors
import com.furrow.app.ui.theme.shimmer
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// ── Climate badge logic (reused from poultry pattern) ──

internal enum class ClimateBadge(val label: String) {
    GREAT("Great for your climate"),
    MANAGEABLE("Manageable"),
    NOT_IDEAL("Not ideal"),
}

@Composable
internal fun climateBadgeColor(badge: ClimateBadge): Color = when (badge) {
    ClimateBadge.GREAT -> MaterialTheme.colorScheme.primary
    ClimateBadge.MANAGEABLE -> MaterialTheme.colorScheme.tertiary
    ClimateBadge.NOT_IDEAL -> MaterialTheme.colorScheme.error
}

/**
 * Map bee race overwintering (cold fitness) and honey production in warm climates
 * to a badge based on the user's zone group.
 *
 * For bees the key climate factors are:
 * - Cold zones: overwinteringAbility is critical
 * - Hot zones: races that tolerate heat (Italian, Saskatraz) do best
 * - We use a simple heuristic: cold zones check overwintering, hot/warm check
 *   a "heat fitness" derived from the race's known heat suitability,
 *   moderate checks the average.
 */
internal fun climateBadgeFor(race: BeeRaceInfo, zoneGroup: String): ClimateBadge {
    // Heat fitness heuristic: Italian/Saskatraz/Buckfast handle heat well,
    // Carniolan/German Dark handle heat poorly, Russian/Caucasian are moderate
    val heatFitness = when (race.name) {
        "Italian" -> 5
        "Saskatraz" -> 4
        "Buckfast" -> 4
        "Russian" -> 3
        "Caucasian" -> 2
        "Carniolan" -> 2
        "German Dark" -> 1
        else -> 3
    }

    val score = when (zoneGroup) {
        "hot", "warm" -> heatFitness
        "cold", "extreme_cold" -> race.overwinteringAbility
        else -> minOf(heatFitness, race.overwinteringAbility) // moderate
    }

    return when {
        score >= 4 -> ClimateBadge.GREAT
        score == 3 -> ClimateBadge.MANAGEABLE
        else -> ClimateBadge.NOT_IDEAL
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiveListScreen(
    onHiveClick: (Long) -> Unit,
    onReportsClick: () -> Unit,
    viewModel: BeeViewModel = hiltViewModel(),
) {
    val hives by viewModel.activeHives.collectAsState()
    val lastDates by viewModel.lastInspectionDates.collectAsState()
    val raceInfoMap by viewModel.raceInfoMap.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val activeTreatments by viewModel.activeTreatmentsPerHive.collectAsState()
    val recentInspections by viewModel.recentInspectionsPerHive.collectAsState()
    val latestInspections by viewModel.latestInspectionPerHive.collectAsState()
    val furrowColors = LocalFurrowColors.current
    var showAddDialog by remember { mutableStateOf(false) }
    var hiveToDelete by remember { mutableStateOf<Hive?>(null) }
    var hiveForAction by remember { mutableStateOf<Hive?>(null) }
    var hiveToEdit by remember { mutableStateOf<Hive?>(null) }

    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(500)
            isRefreshing = false
        }
    }

    val fabInteraction = remember { MutableInteractionSource() }
    val isFabPressed by fabInteraction.collectIsPressedAsState()
    val fabScale by animateFloatAsState(
        targetValue = if (isFabPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "fabScale",
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hives") },
                actions = {
                    IconButton(onClick = onReportsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Assessment,
                            contentDescription = "Reports",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Hive") },
                modifier = Modifier.graphicsLayer {
                    scaleX = fabScale
                    scaleY = fabScale
                },
                interactionSource = fabInteraction,
            )
        }
    ) { padding ->
        FurrowBackground(modifier = Modifier.fillMaxSize().padding(padding)) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { isRefreshing = true },
                modifier = Modifier.fillMaxSize(),
            ) {
                if (hives.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .shimmer(),
                        ) {
                            Icon(
                                Icons.Filled.BugReport,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No hives yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Tap \"Add Hive\" to get started",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(hives, key = { it.id }) { hive ->
                            HiveCard(
                                hive = hive,
                                lastInspectionDate = lastDates[hive.id],
                                raceInfo = hive.beeRace?.let { raceInfoMap[it] },
                                zoneGroup = userProfile?.zoneGroup,
                                activeTreatmentType = activeTreatments[hive.id],
                                recentInspectionDates = recentInspections[hive.id] ?: emptyList(),
                                latestInspection = latestInspections[hive.id],
                                accentColor = furrowColors.beeAccent,
                                onClick = { onHiveClick(hive.id) },
                                onLongClick = { hiveForAction = hive },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog || hiveToEdit != null) {
        val allRaces by viewModel.allRaces.collectAsState()
        AddHiveSheet(
            existingHive = hiveToEdit,
            onDismiss = { showAddDialog = false; hiveToEdit = null },
            onSave = { hive ->
                if (hiveToEdit != null) viewModel.updateHive(hive) else viewModel.addHive(hive)
                showAddDialog = false
                hiveToEdit = null
            },
            allRaces = allRaces,
            zoneGroup = userProfile?.zoneGroup,
            onInsertRace = { race -> viewModel.insertRace(race) },
            onUpdateRace = { race -> viewModel.updateRace(race) },
            onDeleteRace = { race -> viewModel.deleteRace(race) },
        )
    }

    hiveToDelete?.let { hive ->
        DeleteConfirmationDialog(
            itemName = hive.name,
            onConfirm = { viewModel.deleteHive(hive); hiveToDelete = null },
            onDismiss = { hiveToDelete = null },
        )
    }

    hiveForAction?.let { hive ->
        ItemActionSheet(
            onDismiss = { hiveForAction = null },
            onEdit = { hiveToEdit = hive },
            onDelete = { hiveToDelete = hive },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HiveCard(
    hive: Hive,
    lastInspectionDate: Long?,
    raceInfo: BeeRaceInfo?,
    zoneGroup: String?,
    activeTreatmentType: String?,
    recentInspectionDates: List<Long>,
    latestInspection: Inspection?,
    accentColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val view = LocalView.current
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val daysAgo = lastInspectionDate?.let {
        ChronoUnit.DAYS.between(
            Instant.ofEpochMilli(it).atZone(zone).toLocalDate(),
            today,
        )
    }
    val inspectionColor = when {
        daysAgo == null -> MaterialTheme.colorScheme.onSurfaceVariant
        daysAgo <= 7 -> MaterialTheme.colorScheme.primary
        daysAgo <= 13 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
    val inspectionLabel = when {
        daysAgo == null -> "No data"
        daysAgo <= 7 -> "On schedule"
        daysAgo <= 13 -> "Due soon"
        else -> "Overdue"
    }
    val daysText = when {
        daysAgo == null -> "\u2014"
        else -> "$daysAgo"
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(0.5.dp, CardBorderDark.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
                onLongClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onLongClick()
                },
            ),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Accent strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
            ) {
                // Name + Queen badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        hive.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ColonyHealthRing(score = hiveHealthScore(latestInspection))
                        QueenStatusBadge(hive.queenStatus)
                    }
                }

                // Race + climate badge
                if (hive.beeRace != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            hive.beeRace,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (raceInfo != null && zoneGroup != null) {
                            val badge = climateBadgeFor(raceInfo, zoneGroup)
                            val badgeColor = climateBadgeColor(badge)
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = badgeColor.copy(alpha = 0.12f),
                            ) {
                                Text(
                                    badge.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = badgeColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f),
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Inspection stat row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "LAST INSPECTION",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 1.sp,
                        )
                        Text(
                            inspectionLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = inspectionColor,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            daysText,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = inspectionColor,
                        )
                        if (daysAgo != null) {
                            Text(
                                "days",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp,
                            )
                        }
                    }
                }

                // Timeline dots
                if (recentInspectionDates.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            "5 WEEKS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        for (week in 4 downTo 0) {
                            val weekStart = today.minusWeeks((week + 1).toLong())
                            val weekEnd = today.minusWeeks(week.toLong())
                            val hasInspection = recentInspectionDates.any { dateMillis ->
                                val d = Instant.ofEpochMilli(dateMillis).atZone(zone).toLocalDate()
                                d.isAfter(weekStart.minusDays(1)) && !d.isAfter(weekEnd)
                            }
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (hasInspection) accentColor
                                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
                                        CircleShape,
                                    )
                            )
                        }
                    }
                }

                // Treatment badge
                if (activeTreatmentType != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                    ) {
                        Text(
                            "\uD83D\uDC8A ${activeTreatmentType.replaceFirstChar { it.uppercase() }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QueenStatusBadge(status: String) {
    val (label, color) = when (status) {
        "present" -> "\u265B Queen" to MaterialTheme.colorScheme.primary
        "absent" -> "\u2717 No Queen" to MaterialTheme.colorScheme.error
        else -> "\u265B Queen ?" to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.12f),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

private fun hiveHealthScore(inspection: Inspection?): Float {
    if (inspection == null) return -1f
    var score = 0
    if (inspection.queenSeen) score += 25
    if (inspection.eggsLarvae) score += 25
    if (inspection.broodPattern in listOf("solid", "good")) score += 25
    if (inspection.honeyStores in listOf("moderate", "good", "heavy")) score += 25
    return score.toFloat()
}

@Composable
private fun ColonyHealthRing(
    score: Float,
    modifier: Modifier = Modifier,
) {
    val color = when {
        score < 0 -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        score >= 75 -> MaterialTheme.colorScheme.primary
        score >= 50 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
    val sweepAngle = if (score < 0) 0f else score * 3.6f

    Canvas(modifier = modifier.size(40.dp)) {
        val strokeWidth = 4.dp.toPx()
        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
        val arcOffset = Offset(strokeWidth / 2, strokeWidth / 2)
        drawArc(
            color = color.copy(alpha = 0.12f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = arcOffset,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
        if (sweepAngle > 0f) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHiveSheet(
    existingHive: Hive? = null,
    onDismiss: () -> Unit,
    onSave: (Hive) -> Unit,
    allRaces: List<BeeRaceInfo>,
    zoneGroup: String?,
    onInsertRace: (BeeRaceInfo) -> Unit,
    onUpdateRace: (BeeRaceInfo) -> Unit,
    onDeleteRace: (BeeRaceInfo) -> Unit,
) {
    val isEditMode = existingHive != null
    var name by remember { mutableStateOf(existingHive?.name ?: "") }
    var installDate by remember { mutableLongStateOf(existingHive?.installDate ?: System.currentTimeMillis()) }
    var queenStatus by remember { mutableStateOf(existingHive?.queenStatus ?: "unknown") }
    var source by remember { mutableStateOf(existingHive?.source ?: "package") }
    var notes by remember { mutableStateOf(existingHive?.notes ?: "") }
    var selectedRace by remember { mutableStateOf(existingHive?.beeRace) }
    var raceQuery by remember { mutableStateOf(existingHive?.beeRace ?: "") }
    var showAddCustomRace by remember { mutableStateOf(false) }
    var customRaceInitialName by remember { mutableStateOf("") }
    var raceToEdit by remember { mutableStateOf<BeeRaceInfo?>(null) }
    var raceToDelete by remember { mutableStateOf<BeeRaceInfo?>(null) }

    val filteredRaces = remember(allRaces, raceQuery, zoneGroup) {
        val query = raceQuery.lowercase()
        val filtered = allRaces.filter { query.isEmpty() || it.name.lowercase().contains(query) }
        if (zoneGroup == null) filtered
        else filtered.sortedWith(
            compareBy<BeeRaceInfo> {
                when (climateBadgeFor(it, zoneGroup)) {
                    ClimateBadge.GREAT -> 0
                    ClimateBadge.MANAGEABLE -> 1
                    ClimateBadge.NOT_IDEAL -> 2
                }
            }.thenBy { it.name }
        )
    }

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = if (isEditMode) "Edit Hive" else "Add Hive",
        confirmText = "Save",
        confirmEnabled = name.isNotBlank(),
        onConfirm = {
            if (name.isNotBlank()) {
                onSave(
                    Hive(
                        id = existingHive?.id ?: 0,
                        name = name.trim(),
                        installDate = installDate,
                        queenStatus = queenStatus,
                        source = source,
                        notes = notes.ifBlank { null },
                        beeRace = selectedRace,
                    )
                )
            }
        },
        content = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            SearchableSelector(
                query = raceQuery,
                onQueryChange = {
                    raceQuery = it
                    selectedRace = it.ifBlank { null }
                },
                items = filteredRaces,
                onItemSelected = { race ->
                    selectedRace = race.name
                    raceQuery = race.name
                },
                label = "Bee Race (optional)",
                nameSelector = { it.name },
                isCustom = { it.isCustom },
                onAddCustom = { customName ->
                    customRaceInitialName = customName
                    showAddCustomRace = true
                },
                onEditCustom = { race ->
                    raceToEdit = race
                },
                onDeleteCustom = { race ->
                    raceToDelete = race
                },
                itemContent = { race ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(race.name, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                race.temperament,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (zoneGroup != null) {
                            val badge = climateBadgeFor(race, zoneGroup)
                            val badgeColor = climateBadgeColor(badge)
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = badgeColor.copy(alpha = 0.12f),
                            ) {
                                Text(
                                    badge.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = badgeColor,
                                    modifier = Modifier.padding(
                                        horizontal = 6.dp,
                                        vertical = 2.dp,
                                    ),
                                )
                            }
                        }
                    }
                },
            )

            DateFieldWithToggle(
                label = "Install Date:",
                dateMillis = installDate,
                onDateChange = { installDate = it },
                useTodayDefault = !isEditMode,
            )
            DropdownSelector(
                label = "Queen Status",
                options = listOf("present", "absent", "unknown"),
                selected = queenStatus,
                onSelect = { queenStatus = it },
            )
            DropdownSelector(
                label = "Source",
                options = listOf("package", "nuc", "swarm", "split"),
                selected = source,
                onSelect = { source = it },
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
        },
    )

    if (showAddCustomRace || raceToEdit != null) {
        AddCustomRaceSheet(
            existingRace = raceToEdit,
            initialName = if (raceToEdit != null) raceToEdit!!.name else customRaceInitialName,
            onDismiss = {
                showAddCustomRace = false
                raceToEdit = null
            },
            onSave = { race ->
                if (raceToEdit != null) {
                    onUpdateRace(race)
                } else {
                    onInsertRace(race)
                }
                selectedRace = race.name
                raceQuery = race.name
                showAddCustomRace = false
                raceToEdit = null
            },
        )
    }

    raceToDelete?.let { race ->
        DeleteConfirmationDialog(
            itemName = race.name,
            onConfirm = {
                onDeleteRace(race)
                if (selectedRace == race.name) {
                    selectedRace = null
                    raceQuery = ""
                }
                raceToDelete = null
            },
            onDismiss = { raceToDelete = null },
        )
    }
}

@Composable
private fun AddCustomRaceSheet(
    existingRace: BeeRaceInfo?,
    initialName: String,
    onDismiss: () -> Unit,
    onSave: (BeeRaceInfo) -> Unit,
) {
    var raceName by remember { mutableStateOf(existingRace?.name ?: initialName) }
    var temperament by remember { mutableStateOf(existingRace?.temperament ?: "gentle") }
    var honeyProduction by remember { mutableStateOf(existingRace?.honeyProduction?.toString() ?: "3") }
    var miteResistance by remember { mutableStateOf(existingRace?.miteResistance?.toString() ?: "3") }
    var swarmingTendency by remember { mutableStateOf(existingRace?.swarmingTendency?.toString() ?: "3") }
    var overwintering by remember { mutableStateOf(existingRace?.overwinteringAbility?.toString() ?: "3") }
    var climateSuitability by remember { mutableStateOf(existingRace?.climateSuitability ?: "moderate") }
    var raceNotes by remember { mutableStateOf(existingRace?.notes ?: "") }

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = if (existingRace != null) "Edit Custom Race" else "Add Custom Race",
        confirmText = "Save",
        confirmEnabled = raceName.isNotBlank(),
        onConfirm = {
            if (raceName.isNotBlank()) {
                onSave(
                    BeeRaceInfo(
                        id = existingRace?.id ?: 0,
                        name = raceName.trim(),
                        temperament = temperament,
                        honeyProduction = honeyProduction.toIntOrNull() ?: 3,
                        miteResistance = miteResistance.toIntOrNull() ?: 3,
                        swarmingTendency = swarmingTendency.toIntOrNull() ?: 3,
                        overwinteringAbility = overwintering.toIntOrNull() ?: 3,
                        climateSuitability = climateSuitability,
                        notes = raceNotes.ifBlank { null },
                        isCustom = true,
                    )
                )
            }
        },
        content = {
            OutlinedTextField(
                value = raceName,
                onValueChange = { raceName = it },
                label = { Text("Race Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = temperament,
                onValueChange = { temperament = it },
                label = { Text("Temperament") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = honeyProduction,
                    onValueChange = { honeyProduction = it.filter { c -> c.isDigit() } },
                    label = { Text("Honey (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = miteResistance,
                    onValueChange = { miteResistance = it.filter { c -> c.isDigit() } },
                    label = { Text("Mites (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = swarmingTendency,
                    onValueChange = { swarmingTendency = it.filter { c -> c.isDigit() } },
                    label = { Text("Swarm (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = overwintering,
                    onValueChange = { overwintering = it.filter { c -> c.isDigit() } },
                    label = { Text("Winter (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }
            DropdownSelector(
                label = "Climate Suitability",
                options = listOf("cold", "moderate", "warm", "hot", "all climates"),
                selected = climateSuitability,
                onSelect = { climateSuitability = it },
            )
            OutlinedTextField(
                value = raceNotes,
                onValueChange = { raceNotes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DropdownSelector(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.replaceFirstChar { it.uppercase() }) },
                    onClick = { onSelect(option); expanded = false }
                )
            }
        }
    }
}

private val hiveDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

internal fun formatDate(millis: Long): String {
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().format(hiveDateFormatter)
}
