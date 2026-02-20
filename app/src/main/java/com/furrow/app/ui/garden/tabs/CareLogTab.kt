package com.furrow.app.ui.garden.tabs

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.furrow.app.data.local.entity.FertilizerLog
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.WateringLog
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextSecondary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val zone: ZoneId = ZoneId.systemDefault()
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CareLogTab(
    wateringLogs: List<WateringLog>,
    fertilizerLogs: List<FertilizerLog>,
    plantings: List<Planting>,
    plantInfoMap: Map<String, PlantInfo>,
    onWateringLongPress: (WateringLog) -> Unit,
    onFertilizerLongPress: (FertilizerLog) -> Unit,
) {
    val view = LocalView.current

    data class CareEvent(
        val date: Long,
        val type: String,
        val watering: WateringLog? = null,
        val fertilizer: FertilizerLog? = null,
    )

    val events = (wateringLogs.map { CareEvent(it.date, "water", watering = it) } +
        fertilizerLogs.map { CareEvent(it.date, "fertilizer", fertilizer = it) })
        .sortedByDescending { it.date }

    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No care logs yet", style = MaterialTheme.typography.titleMedium)
                Text("Use Add to track watering and feeding", color = TextSecondary)
            }
        }
        return
    }

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
                events.forEachIndexed { index, event ->
                    val eventDate = Instant.ofEpochMilli(event.date).atZone(zone).toLocalDate()
                    val subtitle = when (event.type) {
                        "water" -> listOfNotNull(
                            event.watering?.amountGallons?.let { "${it} gal" },
                            event.watering?.method,
                            event.watering?.notes,
                        ).joinToString(" • ")
                        else -> listOfNotNull(
                            event.fertilizer?.productName,
                            event.fertilizer?.amount,
                            event.fertilizer?.notes,
                        ).joinToString(" • ")
                    }

                    ListRow(
                        modifier = Modifier.combinedClickable(
                            onClick = {},
                            onLongClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                if (event.type == "water") {
                                    event.watering?.let(onWateringLongPress)
                                } else {
                                    event.fertilizer?.let(onFertilizerLongPress)
                                }
                            },
                        ),
                        title = if (event.type == "water") "Watered" else "Fertilized",
                        subtitle = "${eventDate.format(dateFormatter)}${if (subtitle.isNotBlank()) " • $subtitle" else ""}",
                        leadingIcon = {
                            Icon(
                                imageVector = if (event.type == "water") Icons.Outlined.Opacity else Icons.Outlined.Science,
                                contentDescription = null,
                                tint = if (event.type == "water") TextSecondary else StatusWarn,
                            )
                        },
                        showDivider = index != events.lastIndex,
                    )
                }
            }
        }
    }
}
