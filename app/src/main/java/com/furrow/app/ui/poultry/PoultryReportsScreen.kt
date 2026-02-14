package com.furrow.app.ui.poultry

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
import com.furrow.app.ui.components.AccentStatCard
import com.furrow.app.ui.components.DecoratedSectionHeader
import com.furrow.app.ui.theme.CardBorderDark
import com.furrow.app.ui.theme.FurrowBackground
import com.furrow.app.ui.theme.LocalFurrowColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoultryReportsScreen(
    onNavigateBack: () -> Unit,
    viewModel: PoultryViewModel = hiltViewModel(),
) {
    val dailyEggCounts by viewModel.dailyEggCounts.collectAsState()
    val weeklyTotal by viewModel.weeklyTotal.collectAsState()
    val expectedWeeklyEggs by viewModel.expectedWeeklyEggs.collectAsState()
    val layRatePercent by viewModel.layRatePercent.collectAsState()
    val dailyAvg by viewModel.dailyAvg.collectAsState()
    val poultryAccent = LocalFurrowColors.current.poultryAccent

    FurrowBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Poultry Reports") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Navigate back",
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 80.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    AccentStatCard(accentColor = poultryAccent) {
                        DecoratedSectionHeader(title = "7-Day Egg Production", accentColor = poultryAccent)
                        Spacer(modifier = Modifier.height(16.dp))
                        EggProductionChart(
                            dailyCounts = dailyEggCounts,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                        )
                    }
                }

                item {
                    AccentStatCard(accentColor = poultryAccent) {
                        DecoratedSectionHeader(title = "Weekly Production Stats", accentColor = poultryAccent)
                        Spacer(modifier = Modifier.height(12.dp))

                        StatsRow(
                            label = "Weekly Total",
                            value = String.format(Locale.US, "%d eggs", weeklyTotal),
                        )

                        StatsRow(
                            label = "Expected Weekly",
                            value = String.format(Locale.US, "%.0f eggs", expectedWeeklyEggs),
                        )

                        StatsRow(
                            label = "Daily Average",
                            value = String.format(Locale.US, "%.1f eggs/day", dailyAvg),
                        )

                        if (layRatePercent != null) {
                            val layRateColor = when {
                                layRatePercent!! >= 70 -> MaterialTheme.colorScheme.primary
                                layRatePercent!! >= 40 -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.error
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Lay Rate",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = String.format(Locale.US, "%d%%", layRatePercent),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = layRateColor,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun EggProductionChart(
    dailyCounts: List<DailyEggCount>,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val primaryFaded = primary.copy(alpha = 0.35f)
    val onSurface = MaterialTheme.colorScheme.onSurface
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val countStyle = TextStyle(
        fontSize = 11.sp,
        color = onSurface,
        textAlign = TextAlign.Center,
    )
    val cornerRadiusPx = with(density) { 4.dp.toPx() }
    val labelSpacePx = with(density) { 16.dp.toPx() }

    Canvas(modifier = modifier) {
        val maxCount = (dailyCounts.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)
        val barCount = dailyCounts.size
        val totalSpacing = size.width * 0.3f
        val barWidth = (size.width - totalSpacing) / barCount
        val gap = totalSpacing / (barCount + 1)
        val maxBarHeight = size.height - labelSpacePx

        dailyCounts.forEachIndexed { index, day ->
            val barHeight = if (maxCount > 0) (day.count.toFloat() / maxCount) * maxBarHeight else 0f
            val x = gap + index * (barWidth + gap)
            val y = maxBarHeight - barHeight
            val barColor = if (day.isToday) primary else primaryFaded

            if (barHeight > 0f) {
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                )
            }

            if (day.count > 0) {
                val label = day.count.toString()
                val textLayout = textMeasurer.measure(label, countStyle)
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        x + (barWidth - textLayout.size.width) / 2,
                        y - textLayout.size.height - with(density) { 2.dp.toPx() },
                    ),
                )
            }
        }
    }
}
