package com.furrow.app.ui.bees

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.WaterDrop
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
import com.furrow.app.ui.components.GlowCard
import com.furrow.app.ui.theme.BeeGlow
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void
import java.time.Instant
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeeReportsScreen(
    onBack: () -> Unit,
    viewModel: BeeViewModel = hiltViewModel(),
) {
    val currentMonthCalendar by viewModel.currentMonthCalendar.collectAsState()
    val activeHives by viewModel.activeHives.collectAsState()
    val lastInspectionDates by viewModel.lastInspectionDates.collectAsState()
    val activeTreatmentsPerHive by viewModel.activeTreatmentsPerHive.collectAsState()

    Scaffold(
        containerColor = Void,
        topBar = {
            TopAppBar(
                title = { Text("Bee Reports", color = TextPrimary) },
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        ) {
            currentMonthCalendar?.let { calendar ->
                item {
                    ThisMonthCard(calendar)
                }
            }

            item {
                InspectionOverviewCard(
                    activeHives = activeHives,
                    lastInspectionDates = lastInspectionDates,
                )
            }

            item {
                TreatmentSummaryCard(
                    activeTreatmentsPerHive = activeTreatmentsPerHive,
                )
            }
        }
    }
}

@Composable
private fun ThisMonthCard(calendar: BeeCalendar.MonthlyInfo) {
    val nectarColor = when (calendar.nectarStatus) {
        BeeCalendar.NectarStatus.FLOW_ACTIVE -> StatusGood
        BeeCalendar.NectarStatus.FLOW_STARTING -> StatusGood.copy(alpha = 0.7f)
        BeeCalendar.NectarStatus.FLOW_ENDING -> StatusWarn
        BeeCalendar.NectarStatus.SECONDARY_FLOW -> StatusWarn
        BeeCalendar.NectarStatus.DEARTH -> StatusBad
        BeeCalendar.NectarStatus.DORMANT -> TextTertiary
    }

    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = BeeGlow,
        glowIntensity = 0.08f,
    ) {
        ReportSectionHeader("THIS MONTH")
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
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
                    tint = TextTertiary.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(6.dp)
                        .padding(top = 2.dp),
                )
                Text(
                    task,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                )
            }
        }
    }
}

@Composable
private fun InspectionOverviewCard(
    activeHives: List<Hive>,
    lastInspectionDates: Map<Long, Long>,
) {
    val now = Instant.now()
    val totalInspections = lastInspectionDates.size

    val daysBetween = if (lastInspectionDates.isNotEmpty()) {
        lastInspectionDates.values.map { inspectionMillis ->
            ChronoUnit.DAYS.between(Instant.ofEpochMilli(inspectionMillis), now)
        }.average().toLong()
    } else {
        0L
    }

    val hivesNeedingInspection = activeHives.count { hive ->
        val lastInspection = lastInspectionDates[hive.id]
        if (lastInspection == null) {
            true
        } else {
            val daysSince = ChronoUnit.DAYS.between(Instant.ofEpochMilli(lastInspection), now)
            daysSince > 7
        }
    }

    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = BeeGlow,
        glowIntensity = 0.06f,
    ) {
        ReportSectionHeader("INSPECTION OVERVIEW")
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StatItem(label = "Total Inspections", value = totalInspections.toString())
            StatItem(label = "Avg Days Between", value = daysBetween.toString())
        }

        Spacer(modifier = Modifier.height(12.dp))

        StatItem(
            label = "Hives Needing Inspection",
            value = hivesNeedingInspection.toString(),
            highlighted = hivesNeedingInspection > 0,
        )
    }
}

@Composable
private fun TreatmentSummaryCard(
    activeTreatmentsPerHive: Map<Long, String>,
) {
    val totalActiveTreatments = activeTreatmentsPerHive.size
    val hivesWithTreatments = activeTreatmentsPerHive.count { it.value.isNotBlank() }

    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = BeeGlow,
        glowIntensity = 0.06f,
    ) {
        ReportSectionHeader("TREATMENT SUMMARY")
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StatItem(label = "Active Treatments", value = totalActiveTreatments.toString())
            StatItem(label = "Hives with Treatments", value = hivesWithTreatments.toString())
        }
    }
}

@Composable
private fun ReportSectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(3.dp, 16.dp)
                .background(BeeGlow, RoundedCornerShape(2.dp)),
        )
        Text(
            title,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
            color = TextTertiary,
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    highlighted: Boolean = false,
) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = if (highlighted) StatusBad else TextPrimary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary,
            letterSpacing = 1.sp,
        )
    }
}
