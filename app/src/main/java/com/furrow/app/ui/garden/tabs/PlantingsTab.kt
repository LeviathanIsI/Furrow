package com.furrow.app.ui.garden.tabs

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.SecondaryButton
import com.furrow.app.ui.components.Tag
import com.furrow.app.ui.garden.HarvestPrediction
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextSecondary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

private val zone: ZoneId = ZoneId.systemDefault()

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PlantingsTab(
    plantings: List<Planting>,
    plantInfoMap: Map<String, PlantInfo>,
    zoneWindows: List<PlantingWindow>,
    harvestPredictions: Map<Long, HarvestPrediction>,
    onLongPress: (Planting) -> Unit,
    onPlantInfoClick: (PlantInfo) -> Unit,
    onMarkSprouted: (Planting) -> Unit = {},
) {
    val view = LocalView.current

    if (plantings.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No plantings yet",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Use Add to start tracking crops",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        }
        return
    }

    val today = LocalDate.now(zone)

    LazyColumn(
        contentPadding = PaddingValues(
            start = AppSpacing.md,
            end = AppSpacing.md,
            top = AppSpacing.md,
            bottom = AppSpacing.xl,
        ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        item {
            Panel(contentPadding = PaddingValues(0.dp)) {
                plantings.forEachIndexed { index, planting ->
                    val plantedDate = Instant.ofEpochMilli(planting.datePlanted).atZone(zone).toLocalDate()
                    val daysSincePlanting = ChronoUnit.DAYS.between(plantedDate, today)
                    val prediction = harvestPredictions[planting.id]
                    val statusText = prediction?.let {
                        when {
                            it.isOverdue -> "Overdue"
                            it.isReady -> "Ready"
                            else -> "${it.daysUntilEarliest}d"
                        }
                    } ?: ""
                    val plantInfo = plantInfoMap[planting.plantName]
                    val displayName = if (planting.variety != null) {
                        "${planting.plantName} • ${planting.variety}"
                    } else {
                        planting.plantName
                    }

                    ListRow(
                        modifier = Modifier.combinedClickable(
                            onClick = { plantInfo?.let { onPlantInfoClick(it) } },
                            onLongClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                onLongPress(planting)
                            },
                        ),
                        title = displayName,
                        subtitle = buildString {
                            append("${planting.status.replaceFirstChar { it.uppercase() }} • $daysSincePlanting days")
                            if (statusText.isNotBlank()) append(" • $statusText")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Spa,
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
                                if (planting.source == "seed" && planting.germinationDate == null) {
                                    SecondaryButton(
                                        text = "Sprouted",
                                        onClick = { onMarkSprouted(planting) },
                                    )
                                } else {
                                    Tag(
                                        text = planting.status.replaceFirstChar { it.uppercase() },
                                        selected = planting.status == "producing",
                                        accentColor = GardenGlow,
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = TextSecondary,
                                )
                            }
                        },
                        showDivider = index != plantings.lastIndex,
                    )
                }
            }
        }
    }
}
