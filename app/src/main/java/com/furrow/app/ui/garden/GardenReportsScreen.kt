package com.furrow.app.ui.garden

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.ui.components.AppCard
import com.furrow.app.ui.components.AppChip
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppSectionHeader
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenReportsScreen(
    onBack: () -> Unit,
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val dailyHarvests by viewModel.dailyHarvests.collectAsState()
    val activeWindows by viewModel.activeWindows.collectAsState()
    val plantInfoMap by viewModel.plantInfoMap.collectAsState()

    val plantsToPlantNow = remember(activeWindows, plantInfoMap) {
        activeWindows.mapNotNull { window ->
            plantInfoMap[window.plantName]
        }.distinctBy { it.id }
    }

    AppScaffold(
        topBar = {
            TopAppBar(
                title = { Text("Garden Reports", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = com.furrow.app.ui.theme.Void),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(AppSpacing.md, AppSpacing.md, AppSpacing.md, AppSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                HarvestChartCard(dailyHarvests = dailyHarvests)
            }

            if (plantsToPlantNow.isNotEmpty()) {
                item {
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        WhatToPlantNowSection(plants = plantsToPlantNow)
                    }
                }
            }

            item {
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ReportSectionHeader("YIELD BY PLANT")
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    Text(
                        "Coming soon",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun HarvestChartCard(dailyHarvests: List<DailyHarvest>) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("M/d") }
    AppCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        ReportSectionHeader("30-DAY HARVEST (OZ)")
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        HarvestChart(
            dailyHarvests = dailyHarvests,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
        )
        Spacer(modifier = Modifier.height(AppSpacing.xxs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (dailyHarvests.isNotEmpty()) {
                Text(
                    dailyHarvests.first().date.format(dateFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                )
                Text(
                    dailyHarvests[dailyHarvests.size / 2].date.format(dateFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                )
                Text(
                    dailyHarvests.last().date.format(dateFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (dailyHarvests.last().isToday) GardenGlow else TextTertiary,
                )
            }
        }
    }
}

@Composable
private fun HarvestChart(dailyHarvests: List<DailyHarvest>, modifier: Modifier = Modifier) {
    val barColor = GardenGlow
    val barFaded = GardenGlow.copy(alpha = 0.35f)
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val countStyle = TextStyle(fontSize = 9.sp, color = TextPrimary, textAlign = TextAlign.Center)
    val cornerRadiusPx = with(density) { 3.dp.toPx() }
    val labelSpacePx = with(density) { AppSpacing.sm.toPx() }
    Canvas(modifier = modifier) {
        val maxOz = (dailyHarvests.maxOfOrNull { it.totalOz } ?: 1.0).coerceAtLeast(1.0)
        val barCount = dailyHarvests.size
        val totalSpacing = size.width * 0.15f
        val barWidth = (size.width - totalSpacing) / barCount
        val gap = totalSpacing / (barCount + 1)
        val maxBarHeight = size.height - labelSpacePx
        dailyHarvests.forEachIndexed { index, day ->
            val barHeight = (day.totalOz / maxOz * maxBarHeight).toFloat()
            val x = gap + index * (barWidth + gap)
            val y = maxBarHeight - barHeight
            val color = if (day.isToday) barColor else barFaded
            if (barHeight > 0f) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                )
            }
            if (day.totalOz > 0 && barHeight > labelSpacePx) {
                val label = String.format("%.0f", day.totalOz)
                val textLayout = textMeasurer.measure(label, countStyle)
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        x + (barWidth - textLayout.size.width) / 2,
                        y - textLayout.size.height - with(density) { 1.dp.toPx() },
                    ),
                )
            }
        }
    }
}

@Composable
private fun WhatToPlantNowSection(plants: List<PlantInfo>) {
    Column {
        ReportSectionHeader("WHAT TO PLANT NOW")
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            items(plants, key = { it.id }) { plant ->
                WhatToPlantChip(plant)
            }
        }
    }
}

@Composable
private fun WhatToPlantChip(plant: PlantInfo) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs)) {
        AppChip(
            text = plant.name,
            selected = true,
            accentColor = GardenGlow,
        )
        Text(
            "${plant.daysToHarvestMin}-${plant.daysToHarvestMax} days",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
        )
    }
}

@Composable
private fun ReportSectionHeader(title: String) {
    AppSectionHeader(title = title)
}
