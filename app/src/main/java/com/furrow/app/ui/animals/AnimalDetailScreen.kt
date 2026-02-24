package com.furrow.app.ui.animals

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.BreedingRecord
import com.furrow.app.data.local.entity.FeedLog
import com.furrow.app.data.local.entity.HealthRecord
import com.furrow.app.data.local.entity.WeightLog
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.StatusPill
import com.furrow.app.ui.theme.AnimalsGlow
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.util.DateUtil
import com.furrow.app.util.displayFormat
import java.time.ZoneId

private enum class AnimalTab(val label: String, val type: String) {
    HEALTH("Health", "health"),
    BREEDING("Breeding", "breeding"),
    WEIGHT("Weight", "weight"),
    FEED("Feed", "feed"),
}

@Composable
fun AnimalDetailScreen(
    onBack: () -> Unit,
    onAddLog: (Long, String) -> Unit,
    onEditLog: (Long, String, Long) -> Unit,
    viewModel: AnimalViewModel = hiltViewModel(),
) {
    val zone = viewModel.zone
    val animal by viewModel.selectedAnimal.collectAsState()
    val healthRecords by viewModel.healthRecords.collectAsState()
    val breedingRecords by viewModel.breedingRecords.collectAsState()
    val weightLogs by viewModel.weightLogs.collectAsState()
    val feedLogs by viewModel.feedLogs.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = AnimalTab.entries

    var healthForAction by remember { mutableStateOf<HealthRecord?>(null) }
    var breedingForAction by remember { mutableStateOf<BreedingRecord?>(null) }
    var weightForAction by remember { mutableStateOf<WeightLog?>(null) }
    var feedForAction by remember { mutableStateOf<FeedLog?>(null) }

    var healthToDelete by remember { mutableStateOf<HealthRecord?>(null) }
    var breedingToDelete by remember { mutableStateOf<BreedingRecord?>(null) }
    var weightToDelete by remember { mutableStateOf<WeightLog?>(null) }
    var feedToDelete by remember { mutableStateOf<FeedLog?>(null) }

    val view = LocalView.current
    val now = remember { System.currentTimeMillis() }

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(
                title = animal?.name ?: animal?.tagId ?: "Animal",
                subtitle = animal?.let { "${it.species.displayFormat()} - ${it.breed}" },
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
            animal?.let { a ->
                FloatingActionButton(
                    onClick = { onAddLog(a.id, tabs[selectedTab].type) },
                    containerColor = AnimalsGlow,
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add ${tabs[selectedTab].label}",
                        tint = TextPrimary,
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // -- Info Header --
            animal?.let { a ->
                Panel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                ) {
                    InlineStat(label = "Species", value = a.species.displayFormat())
                    InlineStat(label = "Breed", value = a.breed)
                    InlineStat(label = "Sex", value = a.sex.displayFormat())
                    a.tagId?.let { InlineStat(label = "Tag ID", value = it) }
                    InlineStat(label = "Status", value = a.status.displayFormat())
                    a.dob?.let { InlineStat(label = "Date of Birth", value = DateUtil.formatDate(it, zone)) }
                    InlineStat(label = "Acquired", value = DateUtil.formatDate(a.acquisitionDate, zone))
                    a.source?.let { InlineStat(label = "Source", value = it) }
                    a.notes?.takeIf { it.isNotBlank() }?.let { InlineStat(label = "Notes", value = it) }
                }
            }

            // -- Tab Row --
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Charcoal,
                contentColor = TextPrimary,
                edgePadding = 16.dp,
                divider = { HorizontalDivider(thickness = 1.dp, color = BorderSubtle) },
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AnimalsGlow,
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
                                    fontSize = 11.sp,
                                ),
                                color = if (selectedTab == index) TextPrimary else TextTertiary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            // -- Tab Content --
            when (tabs[selectedTab]) {
                AnimalTab.HEALTH -> HealthTab(
                    records = healthRecords,
                    now = now,
                    zone = zone,
                    modifier = Modifier.weight(1f),
                    onLongPress = { record ->
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        healthForAction = record
                    },
                )
                AnimalTab.BREEDING -> BreedingTab(
                    records = breedingRecords,
                    zone = zone,
                    modifier = Modifier.weight(1f),
                    onLongPress = { record ->
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        breedingForAction = record
                    },
                )
                AnimalTab.WEIGHT -> WeightTab(
                    logs = weightLogs,
                    zone = zone,
                    modifier = Modifier.weight(1f),
                    onLongPress = { log ->
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        weightForAction = log
                    },
                )
                AnimalTab.FEED -> FeedTab(
                    logs = feedLogs,
                    zone = zone,
                    modifier = Modifier.weight(1f),
                    onLongPress = { log ->
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        feedForAction = log
                    },
                )
            }
        }
    }

    // -- Action Sheets --

    healthForAction?.let { record ->
        ItemActionSheet(
            onDismiss = { healthForAction = null },
            onEdit = {
                animal?.let { a -> onEditLog(a.id, "health", record.id) }
            },
            onDelete = { healthToDelete = record },
        )
    }

    breedingForAction?.let { record ->
        ItemActionSheet(
            onDismiss = { breedingForAction = null },
            onEdit = {
                animal?.let { a -> onEditLog(a.id, "breeding", record.id) }
            },
            onDelete = { breedingToDelete = record },
        )
    }

    weightForAction?.let { log ->
        ItemActionSheet(
            onDismiss = { weightForAction = null },
            onEdit = {
                animal?.let { a -> onEditLog(a.id, "weight", log.id) }
            },
            onDelete = { weightToDelete = log },
        )
    }

    feedForAction?.let { log ->
        ItemActionSheet(
            onDismiss = { feedForAction = null },
            onEdit = {
                animal?.let { a -> onEditLog(a.id, "feed", log.id) }
            },
            onDelete = { feedToDelete = log },
        )
    }

    // -- Delete Confirmation Dialogs --

    healthToDelete?.let { record ->
        DeleteConfirmationDialog(
            itemName = "${record.type} record",
            onConfirm = {
                viewModel.deleteHealthRecord(record)
                healthToDelete = null
            },
            onDismiss = { healthToDelete = null },
        )
    }

    breedingToDelete?.let { record ->
        DeleteConfirmationDialog(
            itemName = "breeding record",
            onConfirm = {
                viewModel.deleteBreedingRecord(record)
                breedingToDelete = null
            },
            onDismiss = { breedingToDelete = null },
        )
    }

    weightToDelete?.let { log ->
        DeleteConfirmationDialog(
            itemName = "weight log",
            onConfirm = {
                viewModel.deleteWeightLog(log)
                weightToDelete = null
            },
            onDismiss = { weightToDelete = null },
        )
    }

    feedToDelete?.let { log ->
        DeleteConfirmationDialog(
            itemName = "feed log",
            onConfirm = {
                viewModel.deleteFeedLog(log)
                feedToDelete = null
            },
            onDismiss = { feedToDelete = null },
        )
    }
}

// -- Health Tab --

@Composable
private fun HealthTab(
    records: List<HealthRecord>,
    now: Long,
    zone: ZoneId,
    modifier: Modifier = Modifier,
    onLongPress: (HealthRecord) -> Unit,
) {
    if (records.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No health records",
                subtitle = "Tap + to log a vaccination, treatment, or checkup.",
                icon = {
                    Icon(
                        Icons.Outlined.Healing,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = AnimalsGlow,
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
                bottom = AppSpacing.bottomListPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    records.forEachIndexed { index, record ->
                        val subtitle = buildString {
                            record.productName?.let { append(it) }
                            record.dosage?.let {
                                if (isNotEmpty()) append(" - ")
                                append(it)
                            }
                        }.ifEmpty { null }

                        val isWithdrawal = record.withdrawalEndDate != null && record.withdrawalEndDate > now

                        ListRow(
                            modifier = Modifier.pointerInput(record) {
                                detectTapGestures(onLongPress = { onLongPress(record) })
                            },
                            title = record.type.displayFormat(),
                            subtitle = subtitle,
                            metadata = DateUtil.formatDate(record.date, zone),
                            trailing = if (isWithdrawal) {
                                {
                                    StatusPill(
                                        text = "Withdrawal",
                                        error = true,
                                    )
                                }
                            } else {
                                null
                            },
                            showDivider = index != records.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// -- Breeding Tab --

@Composable
private fun BreedingTab(
    records: List<BreedingRecord>,
    zone: ZoneId,
    modifier: Modifier = Modifier,
    onLongPress: (BreedingRecord) -> Unit,
) {
    if (records.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No breeding records",
                subtitle = "Tap + to log a breeding event.",
                icon = {
                    Icon(
                        Icons.Outlined.Pets,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = AnimalsGlow,
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
                bottom = AppSpacing.bottomListPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    records.forEachIndexed { index, record ->
                        val subtitle = buildString {
                            record.method?.let { append(it.displayFormat()) }
                            if (record.offspringCount != null && record.birthDate != null) {
                                if (isNotEmpty()) append(" - ")
                                append("${record.offspringCount} born")
                            }
                        }.ifEmpty { null }

                        ListRow(
                            modifier = Modifier.pointerInput(record) {
                                detectTapGestures(onLongPress = { onLongPress(record) })
                            },
                            title = "Breeding ${DateUtil.formatDate(record.breedingDate, zone)}",
                            subtitle = subtitle,
                            metadata = record.dueDate?.let { "Due ${DateUtil.formatDate(it, zone)}" },
                            showDivider = index != records.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// -- Weight Tab --

@Composable
private fun WeightTab(
    logs: List<WeightLog>,
    zone: ZoneId,
    modifier: Modifier = Modifier,
    onLongPress: (WeightLog) -> Unit,
) {
    if (logs.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No weight records",
                subtitle = "Tap + to log a weight measurement.",
                icon = {
                    Icon(
                        Icons.Outlined.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = AnimalsGlow,
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
                bottom = AppSpacing.bottomListPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    logs.forEachIndexed { index, log ->
                        val subtitle = log.adg?.let {
                            "ADG: ${"%.2f".format(it)} lbs/day"
                        }

                        ListRow(
                            modifier = Modifier.pointerInput(log) {
                                detectTapGestures(onLongPress = { onLongPress(log) })
                            },
                            title = "${"%.1f".format(log.weight)} lbs",
                            subtitle = subtitle,
                            metadata = DateUtil.formatDate(log.date, zone),
                            showDivider = index != logs.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// -- Feed Tab --

@Composable
private fun FeedTab(
    logs: List<FeedLog>,
    zone: ZoneId,
    modifier: Modifier = Modifier,
    onLongPress: (FeedLog) -> Unit,
) {
    if (logs.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No feed records",
                subtitle = "Tap + to log a feeding.",
                icon = {
                    Icon(
                        Icons.Outlined.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = AnimalsGlow,
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
                bottom = AppSpacing.bottomListPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    logs.forEachIndexed { index, log ->
                        val subtitle = buildString {
                            log.quantityLbs?.let { append("${"%.1f".format(it)} lbs") }
                            log.cost?.let {
                                if (isNotEmpty()) append(" - ")
                                append("$${"%,.2f".format(it)}")
                            }
                        }.ifEmpty { null }

                        ListRow(
                            modifier = Modifier.pointerInput(log) {
                                detectTapGestures(onLongPress = { onLongPress(log) })
                            },
                            title = log.feedType.displayFormat(),
                            subtitle = subtitle,
                            metadata = DateUtil.formatDate(log.date, zone),
                            showDivider = index != logs.lastIndex,
                        )
                    }
                }
            }
        }
    }
}
