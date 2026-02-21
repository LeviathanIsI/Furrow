package com.furrow.app.ui.animals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.TextPrimary
import java.util.Locale

@Composable
fun AnimalReportsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnimalViewModel = hiltViewModel(),
) {
    val dailyEggCounts by viewModel.dailyEggCounts.collectAsState()
    val weeklyTotal by viewModel.weeklyTotal.collectAsState()
    val expectedWeeklyEggs by viewModel.expectedWeeklyEggs.collectAsState()
    val layRatePercent by viewModel.layRatePercent.collectAsState()
    val dailyAvg by viewModel.dailyAvg.collectAsState()

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Animal reports",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
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
                    Text("Weekly production", style = MaterialTheme.typography.titleMedium)
                    InlineStat(label = "Weekly total", value = String.format(Locale.US, "%d eggs", weeklyTotal))
                    InlineStat(label = "Expected", value = String.format(Locale.US, "%.0f eggs", expectedWeeklyEggs))
                    InlineStat(label = "Daily average", value = String.format(Locale.US, "%.1f eggs/day", dailyAvg))
                    layRatePercent?.let {
                        InlineStat(label = "Lay rate", value = "$it%")
                    }
                }
            }

            item {
                Panel {
                    Text("7-day trend", style = MaterialTheme.typography.titleMedium)
                    dailyEggCounts.forEach { day ->
                        InlineStat(label = day.dayLabel, value = day.count.toString())
                    }
                    if ((layRatePercent ?: 0) < 40) {
                        Text(
                            "Lay rate is low. Review flock status and feed schedule.",
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusBad,
                        )
                    }
                }
            }
        }
    }
}
