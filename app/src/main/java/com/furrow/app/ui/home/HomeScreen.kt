package com.furrow.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.EggAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.FurrowModule
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit = {},
    onNavigateToBees: () -> Unit = {},
    onNavigateToPoultry: () -> Unit = {},
    onNavigateToGarden: () -> Unit = {},
    onNavigateToEggLog: () -> Unit = {},
    onNavigateToInspection: (() -> Unit)? = null,
    onNavigateToHarvest: (() -> Unit)? = null,
    enabledModules: Set<String> = emptySet(),
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val bees by viewModel.beeInsights.collectAsState()
    val poultry by viewModel.poultryInsights.collectAsState()
    val garden by viewModel.gardenInsights.collectAsState()
    val seasonalTip by viewModel.seasonalTip.collectAsState()

    val beesEnabled = "bees" in enabledModules
    val poultryEnabled = "poultry" in enabledModules
    val gardenEnabled = "garden" in enabledModules

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = AppSpacing.md),
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
        if (beesEnabled || poultryEnabled || gardenEnabled) {
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
                    if (beesEnabled && poultryEnabled) VerticalDividerThin()
                    if (poultryEnabled) {
                        DashboardStat(
                            modifier = Modifier.weight(1f),
                            value = poultry.flockSize.toString(),
                            label = "Flock",
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
                }
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

        // Activity summary — only show enabled modules
        if (beesEnabled || poultryEnabled || gardenEnabled) {
            Panel(
                modifier = Modifier.padding(horizontal = AppSpacing.md),
                contentPadding = PaddingValues(0.dp),
            ) {
                ListRow(
                    title = "Activity summary",
                    subtitle = "Priority items for today",
                    showDivider = true,
                )
                if (beesEnabled) {
                    ListRow(
                        title = "Bees",
                        subtitle = if (bees.hivesNeedingInspection > 0) {
                            "${bees.hivesNeedingInspection} hives need inspection"
                        } else {
                            "Inspection cycle is on track"
                        },
                        metadata = if (bees.hivesNeedingInspection > 0) "Due" else "OK",
                        showDivider = gardenEnabled || poultryEnabled,
                    )
                }
                if (gardenEnabled) {
                    ListRow(
                        title = "Garden",
                        subtitle = if (garden.readyToHarvestCount > 0) {
                            "${garden.readyToHarvestCount} plantings are ready to harvest"
                        } else {
                            "No immediate harvest actions"
                        },
                        metadata = if (garden.readyToHarvestCount > 0) "Ready" else "Queue",
                        showDivider = poultryEnabled,
                    )
                }
                if (poultryEnabled) {
                    ListRow(
                        title = "Poultry",
                        subtitle = "${poultry.todayEggs} eggs logged today",
                        metadata = if (poultry.todayEggs > 0) "Active" else "Log",
                        showDivider = false,
                    )
                }
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
                            "${bees.hiveCount} hives • ${bees.hivesNeedingInspection} need inspection"
                        } else {
                            "No hives tracked yet"
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.BugReport,
                                contentDescription = null,
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
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = TextTertiary,
                                )
                            }
                        },
                        onClick = onNavigateToBees,
                    )
                }
            }

            if (poultryEnabled) {
                moduleRows.add {
                    ListRow(
                        title = "Poultry",
                        subtitle = if (poultry.flockSize > 0) {
                            "${poultry.todayEggs} eggs today • ${poultry.thisWeekTotal} this week"
                        } else {
                            "No flock tracked yet"
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.EggAlt,
                                contentDescription = null,
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
                                    onClick = onNavigateToEggLog,
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = TextTertiary,
                                )
                            }
                        },
                        onClick = onNavigateToPoultry,
                    )
                }
            }

            if (gardenEnabled) {
                moduleRows.add {
                    ListRow(
                        title = "Garden",
                        subtitle = if (garden.activePlantings > 0) {
                            "${garden.activePlantings} active • ${garden.readyToHarvestCount} ready to harvest"
                        } else {
                            "No active plantings yet"
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Agriculture,
                                contentDescription = null,
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
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = TextTertiary,
                                )
                            }
                        },
                        onClick = onNavigateToGarden,
                    )
                }
            }

            // New modules that are enabled but not yet implemented
            val newModuleKeys = listOf("animals", "orchard", "preservation", "land", "finances", "compliance")
            newModuleKeys.filter { it in enabledModules }.forEach { key ->
                val module = FurrowModule.fromKey(key) ?: return@forEach
                moduleRows.add {
                    ListRow(
                        title = module.displayName,
                        subtitle = "Coming soon",
                        leadingIcon = {
                            Icon(
                                imageVector = module.unselectedIcon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = TextSecondary,
                            )
                        },
                    )
                }
            }

            moduleRows.forEachIndexed { index, row ->
                row()
            }
        }
        Spacer(modifier = Modifier.height(AppSpacing.xs))
    }
}

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
