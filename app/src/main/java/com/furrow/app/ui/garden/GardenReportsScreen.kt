package com.furrow.app.ui.garden

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.furrow.app.ui.components.AccentStatCard
import com.furrow.app.ui.components.DecoratedSectionHeader
import com.furrow.app.ui.theme.CardBorderDark
import com.furrow.app.ui.theme.FurrowBackground
import com.furrow.app.ui.theme.LocalFurrowColors
import com.furrow.app.ui.theme.glowBorder
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenReportsScreen(
    onBack: () -> Unit,
    viewModel: GardenViewModel = hiltViewModel()
) {
    val dailyHarvests by viewModel.dailyHarvests.collectAsState()
    val activeWindows by viewModel.activeWindows.collectAsState()
    val plantInfoMap by viewModel.plantInfoMap.collectAsState()

    val gardenAccent = LocalFurrowColors.current.gardenAccent

    // Get plants that can be planted now
    val plantsToPlantNow = remember(activeWindows, plantInfoMap) {
        activeWindows.mapNotNull { window ->
            plantInfoMap[window.plantName]
        }.distinctBy { it.id }
    }

    FurrowBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Garden Reports") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Harvest Chart
                item {
                    HarvestChartCard(dailyHarvests = dailyHarvests, accentColor = gardenAccent)
                }

                // What to Plant Now
                if (plantsToPlantNow.isNotEmpty()) {
                    item {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            border = BorderStroke(0.5.dp, CardBorderDark.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .glowBorder(gardenAccent),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                WhatToPlantNowSection(plants = plantsToPlantNow, accentColor = gardenAccent)
                            }
                        }
                    }
                }

                // Yield by Plant Summary (placeholder for now)
                item {
                    AccentStatCard(accentColor = gardenAccent) {
                        DecoratedSectionHeader(title = "Yield by Plant", accentColor = gardenAccent)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Coming soon",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HarvestChartCard(dailyHarvests: List<DailyHarvest>, accentColor: Color) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("M/d") }
    AccentStatCard(accentColor = accentColor) {
        DecoratedSectionHeader(title = "30-Day Harvest (oz)", accentColor = accentColor)
        Spacer(modifier = Modifier.height(8.dp))
        HarvestChart(dailyHarvests = dailyHarvests, modifier = Modifier.fillMaxWidth().height(120.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (dailyHarvests.isNotEmpty()) {
                Text(dailyHarvests.first().date.format(dateFormatter), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(dailyHarvests[dailyHarvests.size / 2].date.format(dateFormatter), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(dailyHarvests.last().date.format(dateFormatter), style = MaterialTheme.typography.labelSmall,
                    color = if (dailyHarvests.last().isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun HarvestChart(dailyHarvests: List<DailyHarvest>, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val primaryFaded = primary.copy(alpha = 0.35f)
    val onSurface = MaterialTheme.colorScheme.onSurface
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val countStyle = TextStyle(fontSize = 9.sp, color = onSurface, textAlign = TextAlign.Center)
    val cornerRadiusPx = with(density) { 3.dp.toPx() }
    val labelSpacePx = with(density) { 14.dp.toPx() }
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
            val barColor = if (day.isToday) primary else primaryFaded
            if (barHeight > 0f) {
                drawRoundRect(color = barColor, topLeft = Offset(x, y), size = Size(barWidth, barHeight), cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx))
            }
            if (day.totalOz > 0 && barHeight > labelSpacePx) {
                val label = String.format("%.0f", day.totalOz)
                val textLayout = textMeasurer.measure(label, countStyle)
                drawText(textLayoutResult = textLayout, topLeft = Offset(x + (barWidth - textLayout.size.width) / 2, y - textLayout.size.height - with(density) { 1.dp.toPx() }))
            }
        }
    }
}

@Composable
private fun WhatToPlantNowSection(plants: List<PlantInfo>, accentColor: Color) {
    Column {
        DecoratedSectionHeader(title = "What to Plant Now", accentColor = accentColor)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(plants, key = { it.id }) { plant ->
                WhatToPlantChip(plant)
            }
        }
    }
}

@Composable
private fun WhatToPlantChip(plant: PlantInfo) {
    Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(plant.name, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text("${plant.daysToHarvestMin}-${plant.daysToHarvestMax} days", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        }
    }
}
