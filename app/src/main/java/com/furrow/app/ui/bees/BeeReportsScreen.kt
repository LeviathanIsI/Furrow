package com.furrow.app.ui.bees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.BeeCalendar
import com.furrow.app.data.local.dao.InspectionExport
import com.furrow.app.data.local.entity.Hive
import com.furrow.app.util.CsvExporter
import com.furrow.app.util.DateUtil
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.Tag
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BeeGlow
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.TextPrimary
import java.time.Instant
import java.time.temporal.ChronoUnit

@Composable
fun BeeReportsScreen(
    onBack: () -> Unit,
    viewModel: BeeViewModel = hiltViewModel(),
) {
    val currentMonthCalendar by viewModel.currentMonthCalendar.collectAsState()
    val activeHives by viewModel.activeHives.collectAsState()
    val lastInspectionDates by viewModel.lastInspectionDates.collectAsState()
    val activeTreatmentsPerHive by viewModel.activeTreatmentsPerHive.collectAsState()
    val inspectionsForExport by viewModel.inspectionsForExport.collectAsState()
    val context = LocalContext.current

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Bee reports",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val csv = buildString {
                            appendLine("Hive Name,Date,Queen Seen,Temperament,Brood Pattern,Honey Stores,Notes")
                            inspectionsForExport.forEach { i ->
                                appendLine("${CsvExporter.escapeCsv(i.hiveName)},${DateUtil.formatDate(i.date, viewModel.zone)},${i.queenSeen},${CsvExporter.escapeCsv(i.temperament)},${CsvExporter.escapeCsv(i.broodPattern)},${CsvExporter.escapeCsv(i.honeyStores)},${CsvExporter.escapeCsv(i.notes)}")
                            }
                        }
                        CsvExporter.share(context, "furrow_inspections", csv)
                    }) {
                        Icon(Icons.Outlined.FileDownload, contentDescription = "Export CSV", tint = TextPrimary)
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            currentMonthCalendar?.let { calendar ->
                item { ThisMonthPanel(calendar) }
            }
            item {
                InspectionOverviewPanel(
                    activeHives = activeHives ?: emptyList(),
                    lastInspectionDates = lastInspectionDates,
                )
            }
            item {
                TreatmentSummaryPanel(activeTreatmentsPerHive = activeTreatmentsPerHive)
            }
        }
    }
}

@Composable
private fun ThisMonthPanel(calendar: BeeCalendar.MonthlyInfo) {
    Panel(contentPadding = PaddingValues(0.dp)) {
        ListRow(
            title = "Nectar status",
            subtitle = calendar.nectarStatus.label,
            trailing = { Tag(text = "Now", selected = true, accentColor = BeeGlow) },
        )
        calendar.tasks.forEachIndexed { index, task ->
            ListRow(
                title = task,
                subtitle = null,
                showDivider = index != calendar.tasks.lastIndex,
            )
        }
    }
}

@Composable
private fun InspectionOverviewPanel(
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
        if (lastInspection == null) true
        else ChronoUnit.DAYS.between(Instant.ofEpochMilli(lastInspection), now) > 7
    }

    Panel {
        Text("Inspection overview", style = MaterialTheme.typography.titleMedium)
        InlineStat(label = "Logs", value = totalInspections.toString())
        InlineStat(label = "Average days", value = daysBetween.toString())
        InlineStat(label = "Need inspection", value = hivesNeedingInspection.toString())
    }
}

@Composable
private fun TreatmentSummaryPanel(
    activeTreatmentsPerHive: Map<Long, String>,
) {
    val totalActiveTreatments = activeTreatmentsPerHive.size
    val hivesWithTreatments = activeTreatmentsPerHive.count { it.value.isNotBlank() }

    Panel {
        Text("Treatment summary", style = MaterialTheme.typography.titleMedium)
        InlineStat(label = "Active treatments", value = totalActiveTreatments.toString())
        InlineStat(label = "Hives treated", value = hivesWithTreatments.toString())
        if (totalActiveTreatments > 0) {
            Text("Review completion dates in hive details.", style = MaterialTheme.typography.bodySmall, color = StatusBad)
        }
    }
}
