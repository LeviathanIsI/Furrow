package com.furrow.app.ui.garden.tabs

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.AppRadius
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.shimmer
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val zone: ZoneId = ZoneId.systemDefault()
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

internal data class HarvestTotal(
    val totalOz: Double,
    val totalCount: Int,
    val entryCount: Int,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HarvestsTab(
    harvests: List<HarvestLog>,
    plantingNames: Map<Long, String>,
    harvestTotals: Map<Long, HarvestTotal>,
    onLongPress: (HarvestLog) -> Unit,
) {
    val view = LocalView.current
    if (harvests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(AppRadius.card))
                    .shimmer(),
            ) {
                Icon(
                    Icons.Outlined.Scale,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = TextTertiary.copy(alpha = 0.6f),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No harvests yet",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary,
                )
                Text(
                    "Tap + to log a harvest",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                )
            }
        }
    } else {
        val grouped = remember(harvests) {
            harvests.groupBy { it.plantingId }
        }

        LazyColumn(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, top = 16.dp, bottom = AppSpacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            grouped.forEach { (plantingId, logs) ->
                val plantName = plantingNames[plantingId] ?: "Unknown"
                val total = harvestTotals[plantingId]

                item(key = "header_$plantingId") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            plantName,
                            style = MaterialTheme.typography.titleSmall,
                            color = GardenGlow,
                        )
                        total?.let {
                            val yieldText = buildString {
                                if (it.totalOz > 0) append(String.format(Locale.US, "%.1f oz", it.totalOz))
                                if (it.totalOz > 0 && it.totalCount > 0) append(" \u2022 ")
                                if (it.totalCount > 0) append("${it.totalCount} items")
                                append(" (${it.entryCount} harvests)")
                            }
                            Text(
                                yieldText,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                            )
                        }
                    }
                }

                items(logs, key = { it.id }) { harvest ->
                    val harvestDate = remember(harvest.date) {
                        Instant.ofEpochMilli(harvest.date).atZone(zone).toLocalDate()
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(harvest)
                                },
                                indication = ripple(),
                                interactionSource = remember { MutableInteractionSource() },
                            ),
                        shape = RoundedCornerShape(12.dp),
                        color = Charcoal,
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    harvestDate.format(dateFormatter),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    harvest.amountOz?.let {
                                        Text(
                                            "${it}oz",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = GardenGlow,
                                        )
                                    }
                                    harvest.count?.let {
                                        Text(
                                            "$it items",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = GardenGlow,
                                        )
                                    }
                                }
                            }
                            harvest.notes?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = TextTertiary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
