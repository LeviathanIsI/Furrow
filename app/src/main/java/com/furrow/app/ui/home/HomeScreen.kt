package com.furrow.app.ui.home

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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.FurrowModule
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.FurrowBottomSheet
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.NumberStepper
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.util.filterDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit = {},
    onNavigateToBees: () -> Unit = {},
    onNavigateToGarden: () -> Unit = {},
    onNavigateToEggLog: () -> Unit = {},
    onNavigateToInspection: (() -> Unit)? = null,
    onNavigateToHarvest: (() -> Unit)? = null,
    onNavigateToAnimals: () -> Unit = {},
    onNavigateToOrchard: () -> Unit = {},
    onNavigateToPreservation: () -> Unit = {},
    onNavigateToLand: () -> Unit = {},
    onNavigateToFinances: () -> Unit = {},
    onNavigateToCompliance: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {},
    enabledModules: Set<String> = emptySet(),
    deepLinkAction: String? = null,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val beesOrNull by viewModel.beeInsights.collectAsState()
    val garden by viewModel.gardenInsights.collectAsState()
    val animals by viewModel.animalInsights.collectAsState()
    val orchard by viewModel.orchardInsights.collectAsState()
    val preservation by viewModel.preservationInsights.collectAsState()
    val land by viewModel.landInsights.collectAsState()
    val finances by viewModel.financeInsights.collectAsState()
    val compliance by viewModel.complianceInsights.collectAsState()
    val seasonalTip by viewModel.seasonalTip.collectAsState()
    val todayTasks by viewModel.todayTasks.collectAsState()
    val activeBeds by viewModel.activeBeds.collectAsState()
    val activeHives by viewModel.activeHives.collectAsState()
    val activePlantings by viewModel.activePlantingsForLog.collectAsState()
    val crossModuleInsights by viewModel.crossModuleInsights.collectAsState()
    val activeWindowCount by viewModel.activeWindowCount.collectAsState()

    if (beesOrNull == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val bees = beesOrNull ?: return

    val beesEnabled = "bees" in enabledModules
    val gardenEnabled = "garden" in enabledModules
    val animalsEnabled = "animals" in enabledModules
    val orchardEnabled = "orchard" in enabledModules
    val preservationEnabled = "preservation" in enabledModules
    val landEnabled = "land" in enabledModules
    val financesEnabled = "finances" in enabledModules
    val complianceEnabled = "compliance" in enabledModules

    // Quick-log sheet state
    var showEggsSheet by remember { mutableStateOf(false) }
    var showWaterSheet by remember { mutableStateOf(false) }
    var showHarvestSheet by remember { mutableStateOf(false) }
    var showInspectSheet by remember { mutableStateOf(false) }

    // Deep link handling from widget
    LaunchedEffect(deepLinkAction) {
        when (deepLinkAction) {
            "eggs" -> showEggsSheet = true
            "water" -> showWaterSheet = true
            "harvest" -> showHarvestSheet = true
            "inspect" -> showInspectSheet = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = AppSpacing.bottomListPadding),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        AppTopBar(
            title = "Furrow",
            subtitle = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()),
            ),
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary,
                    )
                }
            },
        )

        // Dashboard stats — only show stats for enabled modules
        if (beesEnabled || animalsEnabled || gardenEnabled) {
            Panel(
                modifier = Modifier.padding(horizontal = AppSpacing.md),
                contentPadding = PaddingValues(0.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.md, vertical = AppSpacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (gardenEnabled) {
                        DashboardStat(
                            modifier = Modifier.weight(1f),
                            value = garden.activePlantings.toString(),
                            label = "Active plantings",
                        )
                    }
                    if (gardenEnabled && beesEnabled) VerticalDividerThin()
                    if (beesEnabled) {
                        DashboardStat(
                            modifier = Modifier.weight(1f),
                            value = bees.hiveCount.toString(),
                            label = "Hives",
                        )
                    }
                    if ((gardenEnabled || beesEnabled) && animalsEnabled) VerticalDividerThin()
                    if (animalsEnabled) {
                        DashboardStat(
                            modifier = Modifier.weight(1f),
                            value = animals.totalActive.toString(),
                            label = "Animals",
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = BorderSubtle)
                Column(
                    modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs),
                ) {
                    if (gardenEnabled) {
                        InlineStat(
                            label = "Ready to harvest",
                            value = garden.readyToHarvestCount.toString(),
                        )
                    }
                    if (beesEnabled) {
                        InlineStat(
                            label = "Hives due for inspection",
                            value = bees.hivesNeedingInspection.toString(),
                        )
                    }
                    if (animalsEnabled && animals.chickenCount > 0) {
                        InlineStat(
                            label = "Eggs today",
                            value = animals.todayEggs.toString(),
                        )
                    }
                }
            }
        }

        // ── Quick Actions ──
        QuickActionsRow(
            animalsEnabled = animalsEnabled && animals.chickenCount > 0,
            gardenEnabled = gardenEnabled,
            beesEnabled = beesEnabled,
            onEggsClick = { showEggsSheet = true },
            onWaterClick = { showWaterSheet = true },
            onHarvestClick = { showHarvestSheet = true },
            onInspectClick = { showInspectSheet = true },
        )

        // ── Today Section ──
        TodaySection(
            tasks = todayTasks,
            onTaskAction = { task -> onNavigateToRoute(task.route) },
        )

        // ── Cross-Module Insights ──
        if (crossModuleInsights.isNotEmpty()) {
            InsightsSection(insights = crossModuleInsights)
        }

        // ── Season Planner Card ──
        if (gardenEnabled && activeWindowCount > 0) {
            Panel(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                Text(
                    text = "Plan Your Season",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Text(
                    text = "$activeWindowCount plants in window now",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                PrimaryButton(
                    text = "Start Planner",
                    onClick = { onNavigateToRoute("garden/season-planner") },
                )
            }
        }

        seasonalTip?.takeIf { it.isNotBlank() }?.let { tip ->
            Panel(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                Text(
                    text = "This month",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        }

        // Module rows
        Panel(
            modifier = Modifier.padding(horizontal = AppSpacing.md),
            contentPadding = PaddingValues(0.dp),
        ) {
            ListRow(
                title = "Modules",
                subtitle = "Open records and log actions",
                showDivider = true,
            )

            // Track which items we show to manage dividers
            val moduleRows = mutableListOf<@Composable () -> Unit>()

            if (beesEnabled) {
                moduleRows.add {
                    ListRow(
                        title = "Bees",
                        subtitle = if (bees.hiveCount > 0) {
                            "${bees.hiveCount} hives \u2022 ${bees.hivesNeedingInspection} need inspection"
                        } else {
                            "No hives tracked yet"
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.BugReport,
                                contentDescription = "Bees",
                                modifier = Modifier.size(18.dp),
                                tint = TextSecondary,
                            )
                        },
                        trailing = {
                            Row(
                                modifier = Modifier.wrapContentWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                            ) {
                                PrimaryButton(
                                    text = "Log",
                                    onClick = { onNavigateToInspection?.invoke() ?: onNavigateToBees() },
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "View details",
                                    modifier = Modifier.size(20.dp),
                                    tint = TextTertiary,
                                )
                            }
                        },
                        onClick = onNavigateToBees,
                    )
                }
            }

            if (animalsEnabled) {
                moduleRows.add {
                    val subtitle = if (animals.totalActive > 0) {
                        if (animals.chickenCount > 0) {
                            "${animals.totalActive} active \u2022 ${animals.todayEggs} eggs today"
                        } else {
                            "${animals.totalActive} active \u2022 ${animals.speciesCount} species"
                        }
                    } else {
                        "No animals tracked yet"
                    }
                    val buttonText = if (animals.chickenCount > 0) "Log" else "Add"
                    val buttonAction = if (animals.chickenCount > 0) onNavigateToEggLog else onNavigateToAnimals
                    ModuleRow(
                        title = "Animals",
                        subtitle = subtitle,
                        icon = FurrowModule.ANIMALS.unselectedIcon,
                        buttonText = buttonText,
                        onClick = onNavigateToAnimals,
                        onButtonClick = buttonAction,
                    )
                }
            }

            if (gardenEnabled) {
                moduleRows.add {
                    ListRow(
                        title = "Garden",
                        subtitle = if (garden.activePlantings > 0) {
                            "${garden.activePlantings} active \u2022 ${garden.readyToHarvestCount} ready to harvest"
                        } else {
                            "No active plantings yet"
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Agriculture,
                                contentDescription = "Garden",
                                modifier = Modifier.size(18.dp),
                                tint = TextSecondary,
                            )
                        },
                        trailing = {
                            Row(
                                modifier = Modifier.wrapContentWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                            ) {
                                PrimaryButton(
                                    text = "Add",
                                    onClick = { onNavigateToHarvest?.invoke() ?: onNavigateToGarden() },
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "View details",
                                    modifier = Modifier.size(20.dp),
                                    tint = TextTertiary,
                                )
                            }
                        },
                        onClick = onNavigateToGarden,
                    )
                }
            }

            if (orchardEnabled) {
                moduleRows.add {
                    ModuleRow(
                        title = "Orchard",
                        subtitle = if (orchard.plantCount > 0) {
                            "${orchard.plantCount} plants \u2022 ${"%.1f".format(orchard.totalHarvestLbs)} lbs harvested"
                        } else {
                            "No orchard plants yet"
                        },
                        icon = FurrowModule.ORCHARD.unselectedIcon,
                        buttonText = "Add",
                        onClick = onNavigateToOrchard,
                        onButtonClick = onNavigateToOrchard,
                    )
                }
            }

            if (preservationEnabled) {
                moduleRows.add {
                    ModuleRow(
                        title = "Preservation",
                        subtitle = if (preservation.totalBatches > 0) {
                            "${preservation.totalBatches} batches \u2022 ${preservation.pantryInStock} in pantry"
                        } else {
                            "No preservation batches yet"
                        },
                        icon = FurrowModule.PRESERVATION.unselectedIcon,
                        buttonText = "Add",
                        onClick = onNavigateToPreservation,
                        onButtonClick = onNavigateToPreservation,
                    )
                }
            }

            if (landEnabled) {
                moduleRows.add {
                    ModuleRow(
                        title = "Land",
                        subtitle = if (land.propertyCount > 0) {
                            "${land.propertyCount} properties \u2022 ${"%.1f".format(land.totalAcreage)} acres"
                        } else {
                            "No properties tracked yet"
                        },
                        icon = FurrowModule.LAND.unselectedIcon,
                        buttonText = "Add",
                        onClick = onNavigateToLand,
                        onButtonClick = onNavigateToLand,
                    )
                }
            }

            if (financesEnabled) {
                moduleRows.add {
                    ModuleRow(
                        title = "Finances",
                        subtitle = if (finances.monthExpenses > 0 || finances.monthRevenue > 0) {
                            "Net: ${"$%.0f".format(finances.netIncome)} this month"
                        } else {
                            "No financial records yet"
                        },
                        icon = FurrowModule.FINANCES.unselectedIcon,
                        buttonText = "Log",
                        onClick = onNavigateToFinances,
                        onButtonClick = onNavigateToFinances,
                    )
                }
            }

            if (complianceEnabled) {
                moduleRows.add {
                    ModuleRow(
                        title = "Compliance",
                        subtitle = if (compliance.totalPermits > 0) {
                            "${compliance.totalPermits} permits" + if (compliance.expiringSoon > 0) " \u2022 ${compliance.expiringSoon} expiring" else ""
                        } else {
                            "No permits tracked yet"
                        },
                        icon = FurrowModule.COMPLIANCE.unselectedIcon,
                        buttonText = "Add",
                        onClick = onNavigateToCompliance,
                        onButtonClick = onNavigateToCompliance,
                    )
                }
            }

            moduleRows.forEachIndexed { index, row ->
                row()
            }
        }
        Spacer(modifier = Modifier.height(AppSpacing.xs))
    }

    // ── Bottom Sheets ──

    if (showEggsSheet) {
        LogEggsSheet(
            todayMillis = Instant.now().toEpochMilli(),
            onDismiss = { showEggsSheet = false },
            onSave = { count, dateMillis ->
                viewModel.logEggs(count, dateMillis)
                showEggsSheet = false
            },
        )
    }

    if (showWaterSheet) {
        LogWaterSheet(
            beds = activeBeds,
            todayMillis = Instant.now().toEpochMilli(),
            onDismiss = { showWaterSheet = false },
            onSave = { bedId, dateMillis ->
                viewModel.logWatering(bedId, dateMillis)
                showWaterSheet = false
            },
        )
    }

    if (showHarvestSheet) {
        LogHarvestSheet(
            plantings = activePlantings,
            beds = activeBeds,
            onDismiss = { showHarvestSheet = false },
            onSave = { plantingId, amountOz, dateMillis ->
                viewModel.logHarvest(plantingId, amountOz, dateMillis)
                showHarvestSheet = false
            },
        )
    }

    if (showInspectSheet) {
        QuickInspectSheet(
            hives = activeHives,
            onDismiss = { showInspectSheet = false },
            onNavigate = { hiveId ->
                showInspectSheet = false
                onNavigateToRoute("bees/$hiveId/add-inspection?editId=0")
            },
        )
    }
}

// ═══════════════════════════════════════════════════════════════
//  TODAY SECTION
// ═══════════════════════════════════════════════════════════════

@Composable
private fun TodaySection(
    tasks: List<HomeViewModel.TodayTask>,
    onTaskAction: (HomeViewModel.TodayTask) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val visibleTasks = if (expanded) tasks else tasks.take(5)

    Panel(
        modifier = Modifier.padding(horizontal = AppSpacing.md),
        contentPadding = PaddingValues(0.dp),
    ) {
        ListRow(
            title = "Today",
            metadata = if (tasks.isNotEmpty()) "${tasks.size}" else null,
            showDivider = tasks.isNotEmpty(),
        )

        if (tasks.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.md),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = GardenGlow,
                )
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Text(
                    text = "All caught up! Nothing needs attention today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        } else {
            visibleTasks.forEachIndexed { index, task ->
                TaskRow(
                    task = task,
                    onAction = { onTaskAction(task) },
                    showDivider = index < visibleTasks.lastIndex || (!expanded && tasks.size > 5),
                )
            }
            if (!expanded && tasks.size > 5) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    TextButton(onClick = { expanded = true }) {
                        Text(
                            text = "Show all (${tasks.size})",
                            style = MaterialTheme.typography.labelMedium,
                            color = GardenGlow,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(
    task: HomeViewModel.TodayTask,
    onAction: () -> Unit,
    showDivider: Boolean,
) {
    val dotColor = when (task.priority) {
        0 -> StatusBad
        1 -> StatusWarn
        else -> TextTertiary
    }

    ListRow(
        title = task.title,
        subtitle = task.subtitle,
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
        },
        trailing = {
            PrimaryButton(
                text = task.actionLabel,
                onClick = onAction,
            )
        },
        showDivider = showDivider,
    )
}

// ═══════════════════════════════════════════════════════════════
//  INSIGHTS SECTION
// ═══════════════════════════════════════════════════════════════

@Composable
private fun InsightsSection(
    insights: List<HomeViewModel.CrossModuleInsight>,
) {
    Panel(
        modifier = Modifier.padding(horizontal = AppSpacing.md),
    ) {
        Text(
            "Insights",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
        )
        insights.forEach { insight ->
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                insight.title,
                style = MaterialTheme.typography.labelMedium,
                color = GardenGlow,
            )
            Text(
                insight.body,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  QUICK ACTIONS ROW
// ═══════════════════════════════════════════════════════════════

@Composable
private fun QuickActionsRow(
    animalsEnabled: Boolean,
    gardenEnabled: Boolean,
    beesEnabled: Boolean,
    onEggsClick: () -> Unit,
    onWaterClick: () -> Unit,
    onHarvestClick: () -> Unit,
    onInspectClick: () -> Unit,
) {
    val actions = mutableListOf<Triple<String, ImageVector, () -> Unit>>()
    if (animalsEnabled) actions.add(Triple("Eggs", Icons.Outlined.Egg, onEggsClick))
    if (gardenEnabled) {
        actions.add(Triple("Water", Icons.Outlined.WaterDrop, onWaterClick))
        actions.add(Triple("Harvest", Icons.Outlined.Agriculture, onHarvestClick))
    }
    if (beesEnabled) actions.add(Triple("Inspect", Icons.Outlined.Search, onInspectClick))

    if (actions.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        actions.forEach { (label, icon, onClick) ->
            QuickActionCard(
                modifier = Modifier.weight(1f),
                label = label,
                icon = icon,
                onClick = onClick,
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Charcoal)
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = GardenGlow,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
        )
    }
}

// ═══════════════════════════════════════════════════════════════
//  QUICK LOG BOTTOM SHEETS
// ═══════════════════════════════════════════════════════════════

@Composable
private fun LogEggsSheet(
    todayMillis: Long,
    onDismiss: () -> Unit,
    onSave: (count: Int, dateMillis: Long) -> Unit,
) {
    var count by remember { mutableIntStateOf(0) }

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = "Log Eggs",
        content = {
            NumberStepper(
                value = count,
                onValueChange = { count = it },
                minValue = 0,
                maxValue = 99,
                label = "Egg count",
            )
        },
        confirmText = "Save",
        confirmEnabled = count > 0,
        onConfirm = { onSave(count, todayMillis) },
    )
}

@Composable
private fun LogWaterSheet(
    beds: List<com.furrow.app.data.local.entity.GardenBed>,
    todayMillis: Long,
    onDismiss: () -> Unit,
    onSave: (bedId: Long, dateMillis: Long) -> Unit,
) {
    var selectedBedName by remember { mutableStateOf("") }
    val bedMap = remember(beds) { beds.associateBy { it.name } }

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = "Log Watering",
        content = {
            DropdownSelector(
                label = "Bed",
                options = beds.map { it.name },
                selected = selectedBedName,
                onSelect = { selectedBedName = it },
            )
        },
        confirmText = "Save",
        confirmEnabled = bedMap.containsKey(selectedBedName),
        onConfirm = {
            bedMap[selectedBedName]?.let { onSave(it.id, todayMillis) }
        },
    )
}

@Composable
private fun LogHarvestSheet(
    plantings: List<com.furrow.app.data.local.entity.Planting>,
    beds: List<com.furrow.app.data.local.entity.GardenBed>,
    onDismiss: () -> Unit,
    onSave: (plantingId: Long, amountOz: Double, dateMillis: Long) -> Unit,
) {
    val bedMap = remember(beds) { beds.associateBy { it.id } }
    val displayOptions = remember(plantings, bedMap) {
        plantings.map { p ->
            val bedName = bedMap[p.bedId]?.name ?: "Bed"
            "${p.plantName} ($bedName)" to p.id
        }
    }
    var selectedLabel by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }

    val selectedId = displayOptions.firstOrNull { it.first == selectedLabel }?.second

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = "Log Harvest",
        content = {
            DropdownSelector(
                label = "Planting",
                options = displayOptions.map { it.first },
                selected = selectedLabel,
                onSelect = { selectedLabel = it },
            )
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filterDecimal() },
                label = { Text("Amount (oz)") },
                modifier = Modifier.fillMaxWidth(),
                colors = AppTextFieldDefaults.colors(bordered = true),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
            )
        },
        confirmText = "Save",
        confirmEnabled = selectedId != null && amountText.toDoubleOrNull() != null && amountText.toDouble() > 0,
        onConfirm = {
            if (selectedId != null) {
                onSave(selectedId, amountText.toDouble(), Instant.now().toEpochMilli())
            }
        },
    )
}

@Composable
private fun QuickInspectSheet(
    hives: List<com.furrow.app.data.local.entity.Hive>,
    onDismiss: () -> Unit,
    onNavigate: (hiveId: Long) -> Unit,
) {
    var selectedHiveName by remember { mutableStateOf("") }
    val hiveMap = remember(hives) { hives.associateBy { it.name } }

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = "Quick Inspection",
        content = {
            DropdownSelector(
                label = "Hive",
                options = hives.map { it.name },
                selected = selectedHiveName,
                onSelect = { selectedHiveName = it },
            )
        },
        confirmText = "Start",
        confirmEnabled = hiveMap.containsKey(selectedHiveName),
        onConfirm = {
            hiveMap[selectedHiveName]?.let { onNavigate(it.id) }
        },
    )
}

// ═══════════════════════════════════════════════════════════════
//  HELPER COMPOSABLES
// ═══════════════════════════════════════════════════════════════

@Composable
private fun DashboardStat(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
) {
    Column(
        modifier = modifier
            .padding(horizontal = AppSpacing.xs),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = GardenGlow,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
        )
    }
}

@Composable
private fun VerticalDividerThin() {
    Spacer(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(BorderSubtle),
    )
}

@Composable
private fun ModuleRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    buttonText: String,
    onClick: () -> Unit,
    onButtonClick: () -> Unit,
) {
    ListRow(
        title = title,
        subtitle = subtitle,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "Module",
                modifier = Modifier.size(18.dp),
                tint = TextSecondary,
            )
        },
        trailing = {
            Row(
                modifier = Modifier.wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                PrimaryButton(
                    text = buttonText,
                    onClick = onButtonClick,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View details",
                    modifier = Modifier.size(20.dp),
                    tint = TextTertiary,
                )
            }
        },
        onClick = onClick,
    )
}
