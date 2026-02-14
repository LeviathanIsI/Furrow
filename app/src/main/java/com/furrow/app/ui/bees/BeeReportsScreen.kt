package com.furrow.app.ui.bees

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.BeeCalendar
import com.furrow.app.data.local.entity.Hive
import com.furrow.app.ui.components.AccentStatCard
import com.furrow.app.ui.components.DecoratedSectionHeader
import com.furrow.app.ui.theme.CardBorderDark
import com.furrow.app.ui.theme.FurrowBackground
import com.furrow.app.ui.theme.LocalFurrowColors
import com.furrow.app.ui.theme.glowBorder
import java.time.Instant
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeeReportsScreen(
    onBack: () -> Unit,
    viewModel: BeeViewModel = hiltViewModel()
) {
    val currentMonthCalendar by viewModel.currentMonthCalendar.collectAsState()
    val activeHives by viewModel.activeHives.collectAsState()
    val lastInspectionDates by viewModel.lastInspectionDates.collectAsState()
    val activeTreatmentsPerHive by viewModel.activeTreatmentsPerHive.collectAsState()
    val beeAccent = LocalFurrowColors.current.beeAccent

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bee Reports") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    ) { padding ->
        FurrowBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
            ) {
                // Monthly Calendar Card
                currentMonthCalendar?.let { calendar ->
                    item {
                        ThisMonthCard(calendar, accentColor = beeAccent)
                    }
                }

                // Inspection Overview Card
                item {
                    InspectionOverviewCard(
                        activeHives = activeHives,
                        lastInspectionDates = lastInspectionDates,
                        accentColor = beeAccent,
                    )
                }

                // Treatment Summary Card
                item {
                    TreatmentSummaryCard(
                        activeTreatmentsPerHive = activeTreatmentsPerHive,
                        accentColor = beeAccent,
                    )
                }
            }
        }
    }
}

@Composable
private fun ThisMonthCard(calendar: BeeCalendar.MonthlyInfo, accentColor: Color) {
    val nectarColor = when (calendar.nectarStatus) {
        BeeCalendar.NectarStatus.FLOW_ACTIVE -> MaterialTheme.colorScheme.primary
        BeeCalendar.NectarStatus.FLOW_STARTING -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        BeeCalendar.NectarStatus.FLOW_ENDING -> MaterialTheme.colorScheme.tertiary
        BeeCalendar.NectarStatus.SECONDARY_FLOW -> MaterialTheme.colorScheme.tertiary
        BeeCalendar.NectarStatus.DEARTH -> MaterialTheme.colorScheme.error
        BeeCalendar.NectarStatus.DORMANT -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(0.5.dp, CardBorderDark.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .glowBorder(accentColor),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            DecoratedSectionHeader(title = "This Month", accentColor = accentColor)
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = MaterialTheme.shapes.small,
                color = nectarColor.copy(alpha = 0.12f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = nectarColor,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        calendar.nectarStatus.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = nectarColor,
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            calendar.tasks.forEach { task ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        Icons.Filled.Circle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(6.dp)
                            .padding(top = 2.dp),
                    )
                    Text(
                        task,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun InspectionOverviewCard(
    activeHives: List<Hive>,
    lastInspectionDates: Map<Long, Long>,
    accentColor: Color,
) {
    val now = Instant.now()
    val totalInspections = lastInspectionDates.size

    val daysBetween = if (lastInspectionDates.isNotEmpty()) {
        lastInspectionDates.values.map { inspectionMillis ->
            ChronoUnit.DAYS.between(
                Instant.ofEpochMilli(inspectionMillis),
                now
            )
        }.average().toLong()
    } else {
        0L
    }

    val hivesNeedingInspection = activeHives.count { hive ->
        val lastInspection = lastInspectionDates[hive.id]
        if (lastInspection == null) {
            true
        } else {
            val daysSince = ChronoUnit.DAYS.between(
                Instant.ofEpochMilli(lastInspection),
                now
            )
            daysSince > 7
        }
    }

    AccentStatCard(accentColor = accentColor) {
        DecoratedSectionHeader(title = "Inspection Overview", accentColor = accentColor)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(
                label = "Total Inspections",
                value = totalInspections.toString()
            )
            StatItem(
                label = "Avg Days Between",
                value = daysBetween.toString()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        StatItem(
            label = "Hives Needing Inspection",
            value = hivesNeedingInspection.toString(),
            highlighted = hivesNeedingInspection > 0
        )
    }
}

@Composable
private fun TreatmentSummaryCard(
    activeTreatmentsPerHive: Map<Long, String>,
    accentColor: Color,
) {
    val totalActiveTreatments = activeTreatmentsPerHive.size
    val hivesWithTreatments = activeTreatmentsPerHive.count { it.value.isNotBlank() }

    AccentStatCard(accentColor = accentColor) {
        DecoratedSectionHeader(title = "Treatment Summary", accentColor = accentColor)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(
                label = "Active Treatments",
                value = totalActiveTreatments.toString()
            )
            StatItem(
                label = "Hives with Treatments",
                value = hivesWithTreatments.toString()
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    highlighted: Boolean = false
) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = if (highlighted) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp,
        )
    }
}
