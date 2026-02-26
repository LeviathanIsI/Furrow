package com.furrow.app.ui.garden

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.dao.PlantYield
import com.furrow.app.data.local.dao.PlantingExport
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.Tag
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.util.CsvExporter
import com.furrow.app.util.DateUtil
import java.io.File

@Composable
fun GardenReportsScreen(
    onBack: () -> Unit,
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val dailyHarvests by viewModel.dailyHarvests.collectAsState()
    val yieldByPlant by viewModel.yieldByPlant.collectAsState()
    val activeWindows by viewModel.activeWindows.collectAsState()
    val plantInfoMap by viewModel.plantInfoMap.collectAsState()
    val plantingsForExport by viewModel.plantingsForExport.collectAsState()
    val context = LocalContext.current

    val plantsToPlantNow = remember(activeWindows, plantInfoMap) {
        activeWindows.mapNotNull { window -> plantInfoMap[window.plantName] }.distinctBy { it.id }
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Garden reports",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val csv = buildString {
                            appendLine("Bed Name,Species,Variety,Date Planted,Status,Total Harvest Oz")
                            plantingsForExport.forEach { p ->
                                appendLine("${CsvExporter.escapeCsv(p.bedName)},${CsvExporter.escapeCsv(p.plantName)},${CsvExporter.escapeCsv(p.variety)},${DateUtil.formatDate(p.datePlanted, viewModel.zone)},${CsvExporter.escapeCsv(p.status)},${String.format("%.1f", p.totalHarvestOz)}")
                            }
                        }
                        CsvExporter.share(context, "furrow_garden", csv)
                    }) {
                        Icon(Icons.Outlined.FileDownload, contentDescription = "Export CSV", tint = TextPrimary)
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            item {
                Panel {
                    Text("30-day harvest", style = MaterialTheme.typography.titleMedium)
                    HarvestChart(
                        dailyHarvests = dailyHarvests,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                    )
                    val total = dailyHarvests.sumOf { it.totalOz }
                    InlineStat(label = "Total", value = String.format("%.1f oz", total))
                }
            }

            if (plantsToPlantNow.isNotEmpty()) {
                item {
                    Panel(contentPadding = PaddingValues(0.dp)) {
                        plantsToPlantNow.forEachIndexed { index, plant ->
                            ListRow(
                                title = plant.name,
                                subtitle = "${plant.daysToHarvestMin}-${plant.daysToHarvestMax} days",
                                leadingIcon = {
                                    Icon(Icons.Outlined.Event, contentDescription = "Harvest window", tint = TextSecondary)
                                },
                                trailing = {
                                    Tag(text = "Plant now", selected = true)
                                },
                                showDivider = index != plantsToPlantNow.lastIndex,
                            )
                        }
                    }
                }
            }

            item {
                YieldByPlantPanel(yieldByPlant)
            }
        }
    }
}

@Composable
private fun YieldByPlantPanel(yields: List<PlantYield>) {
    Panel(contentPadding = PaddingValues(0.dp)) {
        Text(
            "Yield by plant",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        )
        if (yields.isEmpty()) {
            Text(
                "No harvests in the last 30 days",
                color = TextTertiary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
            )
        } else {
            yields.forEachIndexed { index, yield ->
                ListRow(
                    title = yield.plantName,
                    subtitle = "${yield.harvestCount} harvest${if (yield.harvestCount != 1) "s" else ""}",
                    trailing = {
                        Text(
                            String.format("%.1f oz", yield.totalOz),
                            style = MaterialTheme.typography.titleSmall,
                            color = GardenGlow,
                        )
                    },
                    showDivider = index != yields.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun HarvestChart(dailyHarvests: List<DailyHarvest>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val maxOz = (dailyHarvests.maxOfOrNull { it.totalOz } ?: 1.0).coerceAtLeast(1.0)
        val barCount = dailyHarvests.size.coerceAtLeast(1)
        val gap = 4.dp.toPx()
        val width = (size.width - gap * (barCount - 1)).coerceAtLeast(1f)
        val barWidth = width / barCount

        dailyHarvests.forEachIndexed { index, day ->
            val x = index * (barWidth + gap)
            val h = ((day.totalOz / maxOz) * size.height).toFloat().coerceAtLeast(2f)
            val y = size.height - h
            drawRoundRect(
                color = if (day.isToday) GardenGlow else GardenGlow.copy(alpha = 0.45f),
                topLeft = Offset(x, y),
                size = Size(barWidth, h),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            )
        }
    }
}
