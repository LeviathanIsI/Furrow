package com.furrow.app.ui.orchard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.FilterVintage
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.BloomRecord
import com.furrow.app.data.local.entity.GraftingLog
import com.furrow.app.data.local.entity.OrchardHarvest
import com.furrow.app.data.local.entity.PruningLog
import com.furrow.app.data.local.entity.SprayLog
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.Tag
import com.furrow.app.ui.theme.AppRadius
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.OrchardGlow
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val orchardDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

internal fun formatDate(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(orchardDateFormatter)
}

private enum class DetailTab(val label: String) {
    HARVESTS("Harvests"),
    PRUNING("Pruning"),
    SPRAYS("Sprays"),
    BLOOMS("Blooms"),
    GRAFTS("Grafts"),
}

private fun DetailTab.toLogType(): String = when (this) {
    DetailTab.HARVESTS -> "harvest"
    DetailTab.PRUNING -> "pruning"
    DetailTab.SPRAYS -> "spray"
    DetailTab.BLOOMS -> "bloom"
    DetailTab.GRAFTS -> "grafting"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OrchardPlantDetailScreen(
    onBack: () -> Unit,
    onAddLog: (Long, String) -> Unit,
    onEditLog: (Long, String, Long) -> Unit,
    viewModel: OrchardViewModel = hiltViewModel(),
) {
    val plant by viewModel.selectedPlant.collectAsState()
    val harvests by viewModel.harvests.collectAsState()
    val pruningLogs by viewModel.pruningLogs.collectAsState()
    val sprayLogs by viewModel.sprayLogs.collectAsState()
    val bloomRecords by viewModel.bloomRecords.collectAsState()
    val graftingLogs by viewModel.graftingLogs.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = DetailTab.entries

    val view = LocalView.current

    // Action/delete state for each tab type
    var harvestForAction by remember { mutableStateOf<OrchardHarvest?>(null) }
    var harvestToDelete by remember { mutableStateOf<OrchardHarvest?>(null) }
    var pruningForAction by remember { mutableStateOf<PruningLog?>(null) }
    var pruningToDelete by remember { mutableStateOf<PruningLog?>(null) }
    var sprayForAction by remember { mutableStateOf<SprayLog?>(null) }
    var sprayToDelete by remember { mutableStateOf<SprayLog?>(null) }
    var bloomForAction by remember { mutableStateOf<BloomRecord?>(null) }
    var bloomToDelete by remember { mutableStateOf<BloomRecord?>(null) }
    var graftForAction by remember { mutableStateOf<GraftingLog?>(null) }
    var graftToDelete by remember { mutableStateOf<GraftingLog?>(null) }

    val currentPlant = plant
    val plantTitle = if (currentPlant != null) {
        buildString {
            append(currentPlant.species)
            currentPlant.variety?.let { append(" - $it") }
        }
    } else {
        "Plant"
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = plantTitle,
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
        floatingActionButton = {
            currentPlant?.let { p ->
                FloatingActionButton(
                    onClick = { onAddLog(p.id, tabs[selectedTab].toLogType()) },
                    containerColor = OrchardGlow,
                    contentColor = Void,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add log")
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            // Plant info header
            currentPlant?.let { p ->
                Panel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = AppSpacing.md, end = AppSpacing.md, top = AppSpacing.md),
                ) {
                    InlineStat(label = "Species", value = p.species)
                    p.variety?.let { InlineStat(label = "Variety", value = it) }
                    InlineStat(
                        label = "Category",
                        value = p.category.replaceFirstChar { it.uppercase() }.replace('_', ' '),
                    )
                    p.rootstock?.let { InlineStat(label = "Rootstock", value = it) }
                    p.plantingYear?.let { InlineStat(label = "Planting Year", value = "$it") }
                    InlineStat(
                        label = "Status",
                        value = p.status.replaceFirstChar { it.uppercase() },
                    )
                    p.chillHoursRequired?.let {
                        InlineStat(label = "Chill Hours Required", value = "$it")
                    }
                }
            }

            // Tab row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Charcoal,
                contentColor = TextPrimary,
                modifier = Modifier.padding(horizontal = AppSpacing.md),
                divider = { HorizontalDivider(thickness = 1.dp, color = BorderSubtle) },
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = OrchardGlow,
                        )
                    }
                },
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                tab.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    letterSpacing = 1.2.sp,
                                ),
                                color = if (selectedTab == index) TextPrimary else TextTertiary,
                            )
                        },
                    )
                }
            }

            // Tab content
            when (tabs[selectedTab]) {
                DetailTab.HARVESTS -> HarvestsTab(
                    harvests = harvests,
                    modifier = Modifier.weight(1f),
                    onLongPress = { harvestForAction = it },
                )
                DetailTab.PRUNING -> PruningTab(
                    logs = pruningLogs,
                    modifier = Modifier.weight(1f),
                    onLongPress = { pruningForAction = it },
                )
                DetailTab.SPRAYS -> SpraysTab(
                    logs = sprayLogs,
                    modifier = Modifier.weight(1f),
                    onLongPress = { sprayForAction = it },
                )
                DetailTab.BLOOMS -> BloomsTab(
                    records = bloomRecords,
                    modifier = Modifier.weight(1f),
                    onLongPress = { bloomForAction = it },
                )
                DetailTab.GRAFTS -> GraftsTab(
                    logs = graftingLogs,
                    modifier = Modifier.weight(1f),
                    onLongPress = { graftForAction = it },
                )
            }
        }
    }

    // ── Action sheets and delete dialogs ──

    harvestForAction?.let { harvest ->
        ItemActionSheet(
            onDismiss = { harvestForAction = null },
            onEdit = {
                currentPlant?.let { p -> onEditLog(p.id, "harvest", harvest.id) }
            },
            onDelete = { harvestToDelete = harvest },
        )
    }
    harvestToDelete?.let { harvest ->
        DeleteConfirmationDialog(
            itemName = "harvest of ${harvest.yieldLbs ?: 0.0} lbs",
            onConfirm = { viewModel.deleteHarvest(harvest); harvestToDelete = null },
            onDismiss = { harvestToDelete = null },
        )
    }

    pruningForAction?.let { log ->
        ItemActionSheet(
            onDismiss = { pruningForAction = null },
            onEdit = {
                currentPlant?.let { p -> onEditLog(p.id, "pruning", log.id) }
            },
            onDelete = { pruningToDelete = log },
        )
    }
    pruningToDelete?.let { log ->
        DeleteConfirmationDialog(
            itemName = log.pruningType ?: "pruning log",
            onConfirm = { viewModel.deletePruningLog(log); pruningToDelete = null },
            onDismiss = { pruningToDelete = null },
        )
    }

    sprayForAction?.let { log ->
        ItemActionSheet(
            onDismiss = { sprayForAction = null },
            onEdit = {
                currentPlant?.let { p -> onEditLog(p.id, "spray", log.id) }
            },
            onDelete = { sprayToDelete = log },
        )
    }
    sprayToDelete?.let { log ->
        DeleteConfirmationDialog(
            itemName = log.product ?: "spray log",
            onConfirm = { viewModel.deleteSprayLog(log); sprayToDelete = null },
            onDismiss = { sprayToDelete = null },
        )
    }

    bloomForAction?.let { record ->
        ItemActionSheet(
            onDismiss = { bloomForAction = null },
            onEdit = {
                currentPlant?.let { p -> onEditLog(p.id, "bloom", record.id) }
            },
            onDelete = { bloomToDelete = record },
        )
    }
    bloomToDelete?.let { record ->
        DeleteConfirmationDialog(
            itemName = "bloom record ${record.year}",
            onConfirm = { viewModel.deleteBloomRecord(record); bloomToDelete = null },
            onDismiss = { bloomToDelete = null },
        )
    }

    graftForAction?.let { log ->
        ItemActionSheet(
            onDismiss = { graftForAction = null },
            onEdit = {
                currentPlant?.let { p -> onEditLog(p.id, "grafting", log.id) }
            },
            onDelete = { graftToDelete = log },
        )
    }
    graftToDelete?.let { log ->
        DeleteConfirmationDialog(
            itemName = log.graftType ?: "grafting log",
            onConfirm = { viewModel.deleteGraftingLog(log); graftToDelete = null },
            onDismiss = { graftToDelete = null },
        )
    }
}

// ── Harvests Tab ──

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HarvestsTab(
    harvests: List<OrchardHarvest>,
    modifier: Modifier = Modifier,
    onLongPress: (OrchardHarvest) -> Unit,
) {
    val view = LocalView.current
    if (harvests.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No harvests yet",
                subtitle = "Tap + to record your first harvest.",
                icon = {
                    Icon(
                        Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = OrchardGlow,
                    )
                },
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                end = AppSpacing.md,
                top = AppSpacing.sm,
                bottom = AppSpacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    harvests.forEachIndexed { index, harvest ->
                        ListRow(
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(harvest)
                                },
                            ),
                            title = "${harvest.yieldLbs ?: 0.0} lbs",
                            subtitle = harvest.fruitQuality,
                            metadata = formatDate(harvest.date),
                            showDivider = index != harvests.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// ── Pruning Tab ──

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PruningTab(
    logs: List<PruningLog>,
    modifier: Modifier = Modifier,
    onLongPress: (PruningLog) -> Unit,
) {
    val view = LocalView.current
    if (logs.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No pruning logs yet",
                subtitle = "Tap + to record a pruning session.",
                icon = {
                    Icon(
                        Icons.Outlined.ContentCut,
                        contentDescription = null,
                        tint = OrchardGlow,
                    )
                },
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                end = AppSpacing.md,
                top = AppSpacing.sm,
                bottom = AppSpacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    logs.forEachIndexed { index, log ->
                        ListRow(
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(log)
                                },
                            ),
                            title = log.pruningType?.replaceFirstChar { it.uppercase() } ?: "Pruning",
                            subtitle = log.method,
                            metadata = formatDate(log.date),
                            showDivider = index != logs.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// ── Sprays Tab ──

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpraysTab(
    logs: List<SprayLog>,
    modifier: Modifier = Modifier,
    onLongPress: (SprayLog) -> Unit,
) {
    val view = LocalView.current
    if (logs.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No spray logs yet",
                subtitle = "Tap + to record a spray application.",
                icon = {
                    Icon(
                        Icons.Outlined.Spa,
                        contentDescription = null,
                        tint = OrchardGlow,
                    )
                },
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                end = AppSpacing.md,
                top = AppSpacing.sm,
                bottom = AppSpacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    logs.forEachIndexed { index, log ->
                        val subtitle = buildString {
                            log.timing?.let { append(it.replaceFirstChar { c -> c.uppercase() }) }
                            log.activeIngredient?.let {
                                if (isNotEmpty()) append(" | ")
                                append(it)
                            }
                        }.ifBlank { null }

                        ListRow(
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(log)
                                },
                            ),
                            title = log.product ?: "Spray",
                            subtitle = subtitle,
                            metadata = formatDate(log.date),
                            trailing = if (log.organicApproved) {
                                {
                                    Tag(
                                        text = "Organic",
                                        selected = true,
                                        accentColor = StatusGood,
                                    )
                                }
                            } else {
                                null
                            },
                            showDivider = index != logs.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// ── Blooms Tab ──

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BloomsTab(
    records: List<BloomRecord>,
    modifier: Modifier = Modifier,
    onLongPress: (BloomRecord) -> Unit,
) {
    val view = LocalView.current
    if (records.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No bloom records yet",
                subtitle = "Tap + to record bloom observations.",
                icon = {
                    Icon(
                        Icons.Outlined.FilterVintage,
                        contentDescription = null,
                        tint = OrchardGlow,
                    )
                },
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                end = AppSpacing.md,
                top = AppSpacing.sm,
                bottom = AppSpacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    records.forEachIndexed { index, record ->
                        ListRow(
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(record)
                                },
                            ),
                            title = "Bloom ${record.year}",
                            subtitle = record.fruitSet,
                            metadata = record.firstBloomDate?.let { formatDate(it) },
                            showDivider = index != records.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// ── Grafts Tab ──

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GraftsTab(
    logs: List<GraftingLog>,
    modifier: Modifier = Modifier,
    onLongPress: (GraftingLog) -> Unit,
) {
    val view = LocalView.current
    if (logs.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No grafting logs yet",
                subtitle = "Tap + to record a graft.",
                icon = {
                    Icon(
                        Icons.Outlined.Agriculture,
                        contentDescription = null,
                        tint = OrchardGlow,
                    )
                },
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                end = AppSpacing.md,
                top = AppSpacing.sm,
                bottom = AppSpacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    logs.forEachIndexed { index, log ->
                        val successText = when (log.success) {
                            "successful" -> "Successful"
                            "failed" -> "Failed"
                            "pending" -> "Pending"
                            else -> log.success?.replaceFirstChar { it.uppercase() }
                        }
                        ListRow(
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(log)
                                },
                            ),
                            title = log.graftType?.replaceFirstChar { it.uppercase() } ?: "Graft",
                            subtitle = log.scionSource,
                            metadata = successText,
                            showDivider = index != logs.lastIndex,
                        )
                    }
                }
            }
        }
    }
}
