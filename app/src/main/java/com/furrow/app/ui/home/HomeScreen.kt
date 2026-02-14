package com.furrow.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.ui.theme.BeeGradientEnd
import com.furrow.app.ui.theme.BeeGradientStart
import com.furrow.app.ui.theme.CardBorderDark
import com.furrow.app.ui.theme.FurrowBackground
import com.furrow.app.ui.theme.GardenGradientEnd
import com.furrow.app.ui.theme.GardenGradientStart
import com.furrow.app.ui.theme.GradientCard
import com.furrow.app.ui.theme.LocalFurrowColors
import com.furrow.app.ui.theme.PoultryGradientEnd
import com.furrow.app.ui.theme.PoultryGradientStart
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit = {},
    onNavigateToBees: () -> Unit = {},
    onNavigateToPoultry: () -> Unit = {},
    onNavigateToGarden: () -> Unit = {},
    onNavigateToEggLog: () -> Unit = {},
    onNavigateToInspection: (() -> Unit)? = null,
    onNavigateToHarvest: (() -> Unit)? = null,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val bees by viewModel.beeInsights.collectAsState()
    val poultry by viewModel.poultryInsights.collectAsState()
    val garden by viewModel.gardenInsights.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val seasonalTip by viewModel.seasonalTip.collectAsState()
    val whatToPlant by viewModel.whatToPlantNow.collectAsState()

    val furrowColors = LocalFurrowColors.current

    val hasAnyData = bees.hiveCount > 0 || poultry.flockSize > 0 || garden.activePlantings > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    profile?.let { p ->
                        ZoneBadge(p)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        }
    ) { padding ->
        FurrowBackground(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                GreetingHeader()

                Spacer(modifier = Modifier.height(24.dp))

                // Seasonal tip
                if (!seasonalTip.isNullOrBlank()) {
                    SeasonalTipCard(tip = seasonalTip!!)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Quick Actions row
                if (hasAnyData) {
                    QuickActionsRow(
                        onLogEggs = onNavigateToEggLog,
                        onInspect = onNavigateToInspection,
                        onHarvest = onNavigateToHarvest,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ── Bees Card ──
                GradientCard(
                    gradientStart = BeeGradientStart,
                    gradientEnd = BeeGradientEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToBees() },
                    backgroundIcon = Icons.Filled.BugReport,
                    backgroundIconTint = furrowColors.beeAccent.copy(alpha = 0.06f),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Filled.BugReport,
                            contentDescription = null,
                            tint = furrowColors.beeAccent,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Bees",
                            style = MaterialTheme.typography.titleMedium,
                            color = furrowColors.beeAccent,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = furrowColors.beeAccent.copy(alpha = 0.5f),
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (bees.hiveCount > 0) {
                        val inspDays = bees.daysSinceLastInspection
                        val inspColor = when {
                            inspDays == null -> MaterialTheme.colorScheme.onSurfaceVariant
                            inspDays <= 7 -> MaterialTheme.colorScheme.primary
                            inspDays <= 13 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.error
                        }
                        val queenOk = bees.hivesNeedingInspection < bees.hiveCount
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            StatPill(
                                value = "${bees.hiveCount}",
                                label = "Active Hives",
                                accentColor = furrowColors.beeAccent,
                                modifier = Modifier.weight(1f),
                            )
                            StatPill(
                                value = if (inspDays != null) "$inspDays" else "--",
                                label = "Days Since\nInspection",
                                accentColor = inspColor,
                                modifier = Modifier.weight(1f),
                            )
                            StatPill(
                                value = null,
                                label = "Queen Status",
                                accentColor = furrowColors.beeAccent,
                                modifier = Modifier.weight(1f),
                                icon = if (queenOk) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                iconColor = if (queenOk) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            )
                        }
                    } else {
                        Text(
                            "No hives yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        TextButton(onClick = onNavigateToBees) {
                            Text(
                                "Add Hive \u2192",
                                color = furrowColors.beeAccent,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Poultry Card ──
                GradientCard(
                    gradientStart = PoultryGradientStart,
                    gradientEnd = PoultryGradientEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPoultry() },
                    backgroundIcon = Icons.Filled.Egg,
                    backgroundIconTint = furrowColors.poultryAccent.copy(alpha = 0.06f),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Filled.Egg,
                            contentDescription = null,
                            tint = furrowColors.poultryAccent,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Poultry",
                            style = MaterialTheme.typography.titleMedium,
                            color = furrowColors.poultryAccent,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = furrowColors.poultryAccent.copy(alpha = 0.5f),
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (poultry.flockSize > 0) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            StatPill(
                                value = "${poultry.todayEggs}",
                                label = "Today",
                                accentColor = furrowColors.poultryAccent,
                                modifier = Modifier.weight(1f),
                            )
                            StatPill(
                                value = "${poultry.thisWeekTotal}",
                                label = "This Week",
                                accentColor = furrowColors.poultryAccent,
                                modifier = Modifier.weight(1f),
                            )
                            StatPill(
                                value = if (poultry.layRatePercent != null) "${poultry.layRatePercent}%" else "--",
                                label = "Lay Rate",
                                accentColor = furrowColors.poultryAccent,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        Text(
                            "Start tracking your flock",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        TextButton(onClick = onNavigateToPoultry) {
                            Text(
                                "Add Chicken \u2192",
                                color = furrowColors.poultryAccent,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Garden Card ──
                GradientCard(
                    gradientStart = GardenGradientStart,
                    gradientEnd = GardenGradientEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToGarden() },
                    backgroundIcon = Icons.Filled.Grass,
                    backgroundIconTint = furrowColors.gardenAccent.copy(alpha = 0.06f),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Filled.Grass,
                            contentDescription = null,
                            tint = furrowColors.gardenAccent,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Garden",
                            style = MaterialTheme.typography.titleMedium,
                            color = furrowColors.gardenAccent,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = furrowColors.gardenAccent.copy(alpha = 0.5f),
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (garden.activePlantings > 0) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            StatPill(
                                value = "${garden.activePlantings}",
                                label = "Active\nPlantings",
                                accentColor = furrowColors.gardenAccent,
                                modifier = Modifier.weight(1f),
                            )
                            StatPill(
                                value = "${garden.producingCount}",
                                label = "Ready to\nHarvest",
                                accentColor = furrowColors.gardenAccent,
                                modifier = Modifier.weight(1f),
                            )
                            StatPill(
                                value = "${garden.growingCount}",
                                label = "Growing",
                                accentColor = furrowColors.gardenAccent,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        Text(
                            "Plant your first seed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        TextButton(onClick = onNavigateToGarden) {
                            Text(
                                "Add Bed \u2192",
                                color = furrowColors.gardenAccent,
                            )
                        }
                    }
                }

                // What to Plant Now chips
                if (whatToPlant.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "PLANT NOW",
                        style = MaterialTheme.typography.labelSmall,
                        color = furrowColors.gardenAccent,
                        letterSpacing = 1.5.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        whatToPlant.forEach { suggestion ->
                            val methodLabel = when (suggestion.method) {
                                "direct_sow" -> "sow"
                                "transplant" -> "transplant"
                                else -> "sow/transplant"
                            }
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = furrowColors.gardenAccent.copy(alpha = 0.12f),
                                border = BorderStroke(0.5.dp, CardBorderDark.copy(alpha = 0.4f)),
                            ) {
                                Text(
                                    "${suggestion.name} \u00b7 $methodLabel \u00b7 ~${suggestion.daysToHarvest}d",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = furrowColors.gardenAccent,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// ── Components ──

@Composable
private fun StatPill(
    value: String?,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconColor: Color = accentColor,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = 0.1f),
        border = BorderStroke(0.5.dp, accentColor.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp),
                )
            } else if (value != null) {
                Text(
                    value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                )
            }
            Text(
                label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 13.sp,
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    onLogEggs: () -> Unit,
    onInspect: (() -> Unit)?,
    onHarvest: (() -> Unit)?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        QuickActionButton(
            icon = Icons.Filled.Egg,
            label = "Log Eggs",
            onClick = onLogEggs,
        )
        if (onInspect != null) {
            QuickActionButton(
                icon = Icons.Filled.Search,
                label = "Inspect",
                onClick = onInspect,
            )
        }
        if (onHarvest != null) {
            QuickActionButton(
                icon = Icons.Outlined.Eco,
                label = "Harvest",
                onClick = onHarvest,
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clickable(onClick = onClick),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            border = BorderStroke(0.5.dp, CardBorderDark),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SeasonalTipCard(tip: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        border = BorderStroke(0.5.dp, CardBorderDark.copy(alpha = 0.4f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "THIS MONTH",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.5.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                tip,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ZoneBadge(profile: UserProfile) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        border = BorderStroke(0.5.dp, CardBorderDark.copy(alpha = 0.4f)),
    ) {
        Text(
            text = "Zone ${profile.hardinessZone} \u2022 ${profile.state}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun GreetingHeader() {
    val greeting = when (LocalTime.now().hour) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
    val dateText = LocalDate.now().format(
        DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
    )

    Column {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = dateText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
