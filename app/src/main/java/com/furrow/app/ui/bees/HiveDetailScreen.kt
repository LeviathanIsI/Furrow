package com.furrow.app.ui.bees

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import com.furrow.app.data.local.entity.Treatment
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.GlowCard
import com.furrow.app.ui.components.ItemActionSheet
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
    val tabs = listOf("INSPECTIONS", "TREATMENTS")
    var inspectionToDelete by remember { mutableStateOf<Inspection?>(null) }
    var treatmentToDelete by remember { mutableStateOf<Treatment?>(null) }
    var inspectionForAction by remember { mutableStateOf<Inspection?>(null) }
    var treatmentForAction by remember { mutableStateOf<Treatment?>(null) }

    Scaffold(
        containerColor = Void,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Void),
            )
        },
        floatingActionButton = {
            hive?.let { h ->
                FloatingActionButton(
                    onClick = {
                        if (selectedTab == 0) onAddInspection(h.id) else onAddTreatment(h.id)
                    },
                    containerColor = BeeGlow,
                    contentColor = Void,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            hive?.let { h ->
                // ── Header Card ──
                GlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    glowColor = BeeGlow,
                    glowIntensity = 0.12f,
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            h.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            buildString {
                                append("Queen: ${h.queenStatus.replaceFirstChar { it.uppercase() }}")
                                append(" \u00b7 Source: ${h.source.replaceFirstChar { it.uppercase() }}")
                                h.beeRace?.let { append(" \u00b7 Race: $it") }
                            },
                            fontSize = 14.sp,
                            color = TextSecondary,
                        )
                        Text(
                            "Installed ${formatDate(h.installDate)}",
                            fontSize = 12.sp,
                            color = TextTertiary,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Tabs ──
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Charcoal,
                contentColor = TextPrimary,
                divider = { HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle) },
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
                            )
                        },
                    )
                }
            }

            // ── Tab Content ──
            when (selectedTab) {
                0 -> InspectionList(
                    inspections = inspections,
                    modifier = Modifier.weight(1f),
                    onLongPress = { inspectionForAction = it },
                )
                1 -> TreatmentList(
                    treatments = treatments,
                    modifier = Modifier.weight(1f),
                    onLongPress = { treatmentForAction = it },
                )
            }
        }
    }

    // ── Dialogs ──

    inspectionToDelete?.let { inspection ->
        DeleteConfirmationDialog(
            itemName = "inspection from ${formatDate(inspection.date)}",
            onConfirm = { viewModel.deleteInspection(inspection); inspectionToDelete = null },
            onDismiss = { inspectionToDelete = null },
        )
    }

    treatmentToDelete?.let { treatment ->
        DeleteConfirmationDialog(
            itemName = treatment.type.replaceFirstChar { it.uppercase() },
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InspectionList(
    inspections: List<Inspection>,
    modifier: Modifier = Modifier,
    onLongPress: (Inspection) -> Unit,
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
                subtitle = "Tap + to log your first inspection",
                actionLabel = "Add Inspection",
                glowColor = BeeGlow,
                onAction = {},
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(inspections, key = { it.id }) { inspection ->
                val findings = buildInspectionFindings(inspection)

                GlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                onLongPress(inspection)
                            },
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() },
                        ),
                    glowColor = Color.Transparent,
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            formatDate(inspection.date),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                        )
                        if (findings.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                findings.joinToString(" \u00b7 "),
                                fontSize = 12.sp,
                                color = TextSecondary,
                            )
                        }
                        inspection.notes?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                it,
                                fontSize = 12.sp,
                                color = TextTertiary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Treatment List ──

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TreatmentList(
    treatments: List<Treatment>,
    modifier: Modifier = Modifier,
    onLongPress: (Treatment) -> Unit,
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
                subtitle = "Tap + to log a treatment",
                actionLabel = "Add Treatment",
                glowColor = BeeGlow,
                onAction = {},
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(treatments, key = { it.id }) { treatment ->
                GlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                onLongPress(treatment)
                            },
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() },
                        ),
                    glowColor = Color.Transparent,
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            formatDate(treatment.date),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            treatment.type.replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp,
                            color = TextSecondary,
                        )
                        treatment.method?.let {
                            Text(
                                "Method: $it",
                                fontSize = 12.sp,
                                color = TextSecondary,
                            )
                        }
                        treatment.dose?.let {
                            Text(
                                "Dose: $it",
                                fontSize = 12.sp,
                                color = TextSecondary,
                            )
                        }
                        treatment.endDate?.let {
                            Text(
                                "Until ${formatDate(it)}",
                                fontSize = 12.sp,
                                color = TextTertiary,
                            )
                        }
                        treatment.notes?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                it,
                                fontSize = 12.sp,
                                color = TextTertiary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
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
