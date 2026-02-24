package com.furrow.app.ui.bees

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.Inspection
import com.furrow.app.util.DateUtil
import com.furrow.app.util.displayFormat
import com.furrow.app.data.local.entity.Treatment
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.GlowCard
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BeeGlow
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiveDetailScreen(
    onBack: () -> Unit,
    onAddInspection: (Long) -> Unit,
    onAddTreatment: (Long) -> Unit,
    onEditInspection: (Long, Long) -> Unit,
    onEditTreatment: (Long, Long) -> Unit,
    viewModel: BeeViewModel = hiltViewModel(),
) {
    val hive by viewModel.selectedHive.collectAsState()
    val inspections by viewModel.inspections.collectAsState()
    val treatments by viewModel.treatments.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Inspections", "Treatments")
    var inspectionToDelete by remember { mutableStateOf<Inspection?>(null) }
    var treatmentToDelete by remember { mutableStateOf<Treatment?>(null) }
    var inspectionForAction by remember { mutableStateOf<Inspection?>(null) }
    var treatmentForAction by remember { mutableStateOf<Treatment?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    com.furrow.app.ui.components.AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            com.furrow.app.ui.components.AppTopBar(
                title = hive?.name ?: "Hive",
                subtitle = hive?.beeRace,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            hive?.let { h ->
                com.furrow.app.ui.components.Panel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Text(
                        "Queen ${h.queenStatus.displayFormat()} \u2022 Source ${h.source.displayFormat()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                    Text(
                        "Installed ${DateUtil.formatDate(h.installDate, viewModel.zone)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        com.furrow.app.ui.components.PrimaryButton(
                            text = if (selectedTab == 0) "Add Inspection" else "Add Treatment",
                            onClick = {
                                if (selectedTab == 0) onAddInspection(h.id) else onAddTreatment(h.id)
                            },
                        )
                    }
                }
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Charcoal,
                contentColor = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp),
                divider = { HorizontalDivider(thickness = 1.dp, color = BorderSubtle) },
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = BeeGlow,
                        )
                    }
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    letterSpacing = 1.2.sp,
                                ),
                                color = if (selectedTab == index) TextPrimary else TextTertiary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
            }

            when (selectedTab) {
                0 -> InspectionList(
                    inspections = inspections,
                    modifier = Modifier.weight(1f),
                    onLongPress = { inspectionForAction = it },
                    zone = viewModel.zone,
                )
                1 -> TreatmentList(
                    treatments = treatments,
                    modifier = Modifier.weight(1f),
                    onLongPress = { treatmentForAction = it },
                    zone = viewModel.zone,
                )
            }
        }
    }

    // ── Dialogs ──

    inspectionToDelete?.let { inspection ->
        DeleteConfirmationDialog(
            itemName = "inspection from ${DateUtil.formatDate(inspection.date, viewModel.zone)}",
            onConfirm = { viewModel.deleteInspection(inspection); inspectionToDelete = null },
            onDismiss = { inspectionToDelete = null },
        )
    }

    treatmentToDelete?.let { treatment ->
        DeleteConfirmationDialog(
            itemName = treatment.type.displayFormat(),
            onConfirm = { viewModel.deleteTreatment(treatment); treatmentToDelete = null },
            onDismiss = { treatmentToDelete = null },
        )
    }

    inspectionForAction?.let { inspection ->
        ItemActionSheet(
            onDismiss = { inspectionForAction = null },
            onEdit = { hive?.let { h -> onEditInspection(h.id, inspection.id) } },
            onDelete = { inspectionToDelete = inspection },
        )
    }

    treatmentForAction?.let { treatment ->
        ItemActionSheet(
            onDismiss = { treatmentForAction = null },
            onEdit = { hive?.let { h -> onEditTreatment(h.id, treatment.id) } },
            onDelete = { treatmentToDelete = treatment },
        )
    }
}

// ── Inspection List ──

@Composable
private fun InspectionList(
    inspections: List<Inspection>,
    modifier: Modifier = Modifier,
    onLongPress: (Inspection) -> Unit,
    zone: java.time.ZoneId,
) {
    val view = LocalView.current
    if (inspections.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                icon = {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = BeeGlow,
                    )
                },
                title = "No inspections yet",
                subtitle = "Use Add Inspection to record your first check.",
                actionLabel = "Add Inspection",
                glowColor = BeeGlow,
                onAction = {},
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = AppSpacing.bottomListPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                com.furrow.app.ui.components.Panel(contentPadding = PaddingValues(0.dp)) {
                    inspections.forEachIndexed { index, inspection ->
                        val findings = buildInspectionFindings(inspection)
                        com.furrow.app.ui.components.ListRow(
                            modifier = Modifier.pointerInput(inspection) {
                                detectTapGestures(onLongPress = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(inspection)
                                })
                            },
                            title = DateUtil.formatDate(inspection.date, zone),
                            subtitle = listOfNotNull(
                                findings.joinToString(" • ").takeIf { it.isNotBlank() },
                                inspection.notes,
                            ).joinToString(" • "),
                            leadingIcon = {
                                Icon(Icons.Outlined.Search, contentDescription = null, tint = TextSecondary)
                            },
                            showDivider = index != inspections.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// ── Treatment List ──

@Composable
private fun TreatmentList(
    treatments: List<Treatment>,
    modifier: Modifier = Modifier,
    onLongPress: (Treatment) -> Unit,
    zone: java.time.ZoneId,
) {
    val view = LocalView.current
    if (treatments.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                icon = {
                    Icon(
                        Icons.Outlined.Vaccines,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = BeeGlow,
                    )
                },
                title = "No treatments yet",
                subtitle = "Use Add Treatment to record interventions.",
                actionLabel = "Add Treatment",
                glowColor = BeeGlow,
                onAction = {},
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = AppSpacing.bottomListPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                com.furrow.app.ui.components.Panel(contentPadding = PaddingValues(0.dp)) {
                    treatments.forEachIndexed { index, treatment ->
                        val details = buildString {
                            append(treatment.type.displayFormat())
                            treatment.method?.let { append(" \u2022 Method: ${it.displayFormat()}") }
                            treatment.dose?.let { append(" • Dose: $it") }
                            treatment.endDate?.let { append(" • Until ${DateUtil.formatDate(it, zone)}") }
                            treatment.notes?.let { append(" • $it") }
                        }
                        com.furrow.app.ui.components.ListRow(
                            modifier = Modifier.pointerInput(treatment) {
                                detectTapGestures(onLongPress = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(treatment)
                                })
                            },
                            title = DateUtil.formatDate(treatment.date, zone),
                            subtitle = details,
                            leadingIcon = {
                                Icon(Icons.Outlined.Vaccines, contentDescription = null, tint = TextSecondary)
                            },
                            showDivider = index != treatments.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// ── Helpers ──

private fun buildInspectionFindings(inspection: Inspection): List<String> = buildList {
    if (inspection.queenSeen) add("Queen \u2713") else add("Queen \u2717")
    if (inspection.queenCells) add("Queen Cells")
    if (inspection.eggsLarvae) add("Eggs \u2713")
    inspection.broodPattern?.let { add("Brood: $it") }
    inspection.honeyStores?.let { add("Honey: $it") }
    if (!inspection.pestsSigns.isNullOrBlank()) add("Pests")
    if (!inspection.diseasesSigns.isNullOrBlank()) add("Disease")
}
