package com.furrow.app.ui.garden.tabs

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.SwipeToDeleteItem
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.util.DateUtil

import java.time.ZoneId
import java.util.Locale


internal data class HarvestTotal(
    val totalOz: Double,
    val totalCount: Int,
    val entryCount: Int,
)


@Composable
internal fun HarvestsTab(
    harvests: List<HarvestLog>,
    plantingNames: Map<Long, String>,
    harvestTotals: Map<Long, HarvestTotal>,
    onLongPress: (HarvestLog) -> Unit,
    onDelete: (HarvestLog) -> Unit = {},
    onLogHarvest: () -> Unit = {},
    zone: ZoneId,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current

    if (harvests.isEmpty()) {
        EmptyState(
            title = "No harvest logs yet",
            subtitle = "Log your first harvest to start tracking yield from this bed",
            modifier = modifier,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Inventory2,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = GardenGlow,
                )
            },
            actionLabel = "Log Harvest",
            onAction = onLogHarvest,
        )
        return
    }

    val grouped = harvests.groupBy { it.plantingId }

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
        grouped.forEach { (plantingId, logs) ->
            item(key = "header_$plantingId") {
                Text(
                    text = plantingNames[plantingId] ?: "Unknown planting",
                    style = MaterialTheme.typography.titleMedium,
                )
                val total = harvestTotals[plantingId]
                total?.let {
                    Text(
                        text = buildString {
                            if (it.totalOz > 0) append(String.format(Locale.US, "%.1f oz", it.totalOz))
                            if (it.totalOz > 0 && it.totalCount > 0) append(" • ")
                            if (it.totalCount > 0) append("${it.totalCount} items")
                            append(" • ${it.entryCount} logs")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            item(key = "panel_$plantingId") {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    logs.forEachIndexed { index, harvest ->
                        val harvestDateStr = DateUtil.formatDate(harvest.date, zone)
                        SwipeToDeleteItem(
                            itemName = "harvest",
                            onDelete = { onDelete(harvest) },
                        ) {
                            ListRow(
                                modifier = Modifier.pointerInput(harvest) {
                                    detectTapGestures(onLongPress = {
                                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                        onLongPress(harvest)
                                    })
                                },
                                title = harvestDateStr,
                                subtitle = harvest.notes,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Inventory2,
                                        contentDescription = "Harvest",
                                        tint = TextSecondary,
                                    )
                                },
                                trailingText = buildString {
                                    harvest.amountOz?.let { append("${it}oz") }
                                    harvest.count?.let {
                                        if (isNotBlank()) append(" • ")
                                        append("$it")
                                    }
                                },
                                showDivider = index != logs.lastIndex,
                            )
                        }
                    }
                }
            }
        }
    }
}
