package com.furrow.app.ui.garden.tabs

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.furrow.app.util.DateUtil
import com.furrow.app.util.displayFormat
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PlantingsTab(
    plantings: List<Planting>,
    zoneWindows: List<PlantingWindow>,
    harvestPredictions: Map<Long, HarvestPrediction>,
    onLongPress: (Planting) -> Unit,
    onPlantingClick: (Planting) -> Unit,
    onMarkSprouted: (Planting) -> Unit = {},
    zone: ZoneId = ZoneId.systemDefault(),
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current

    if (plantings.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
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
    fun formatShort(date: LocalDate): String = DateUtil.formatShortDate(date, today)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = AppSpacing.md,
            end = AppSpacing.md,
            top = AppSpacing.md,
            bottom = AppSpacing.bottomListPadding,
        ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        item {
            Panel(contentPadding = PaddingValues(0.dp)) {
                plantings.forEachIndexed { index, planting ->
                    val plantedDate = Instant.ofEpochMilli(planting.datePlanted).atZone(zone).toLocalDate()
                    val prediction = harvestPredictions[planting.id]
                    val displayName = if (planting.variety != null) {
                        "${planting.plantName} • ${planting.variety}"
                    } else {
                        planting.plantName
                    }

                    ListRow(
                        modifier = Modifier.combinedClickable(
                            onClick = { onPlantingClick(planting) },
                            onLongClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                onLongPress(planting)
                            },
                        ),
                        title = displayName,
                        subtitle = buildString {
                            append("Planted ${formatShort(plantedDate)}")
                            planting.germinationDate?.let { millis ->
                                val sproutDate = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
                                append(" · Sprouted ${formatShort(sproutDate)}")
                            }
                            prediction?.let {
                                append("\n")
                                when {
                                    it.isOverdue -> {
                                        val daysPast = ChronoUnit.DAYS.between(it.latestHarvest, today)
                                        append("$daysPast days past expected harvest")
                                    }
                                    it.isReady -> append("Ready to harvest")
                                    else -> append("~${it.daysUntilEarliest} days to harvest")
                                }
                            }
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
                                if (planting.source.equals("seed", ignoreCase = true) && planting.germinationDate == null) {
                                    SecondaryButton(
                                        text = "Sprouted",
                                        onClick = { onMarkSprouted(planting) },
                                    )
                                } else {
                                    Tag(
                                        text = planting.status.displayFormat(),
                                        selected = planting.status.equals("producing", ignoreCase = true),
                                        accentColor = GardenGlow,
                                    )
                                    if (planting.source.equals("seed", ignoreCase = true) && planting.germinationDate != null) {
                                        Icon(
                                            imageVector = Icons.Outlined.Edit,
                                            contentDescription = "Edit sprout date",
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clickable { onMarkSprouted(planting) },
                                            tint = GardenGlow,
                                        )
                                    }
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
