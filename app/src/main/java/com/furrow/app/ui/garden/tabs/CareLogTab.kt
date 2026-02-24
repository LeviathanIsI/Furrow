package com.furrow.app.ui.garden.tabs

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
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
import com.furrow.app.util.displayFormat
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.WateringLog
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.util.DateUtil
import java.time.ZoneId


@Composable
internal fun CareLogTab(
    wateringLogs: List<WateringLog>,
    fertilizerLogs: List<FertilizerLog>,
    plantings: List<Planting>,
    plantInfoMap: Map<String, PlantInfo>,
    onWateringLongPress: (WateringLog) -> Unit,
    onFertilizerLongPress: (FertilizerLog) -> Unit,
    zone: ZoneId,
    modifier: Modifier = Modifier,
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
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No care logs yet", style = MaterialTheme.typography.titleMedium)
                Text("Use Add to track watering and feeding", color = TextSecondary)
            }
        }
        return
    }

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
                events.forEachIndexed { index, event ->
                    val eventDateStr = DateUtil.formatDate(event.date, zone)
                    val subtitle = when (event.type) {
                        "water" -> listOfNotNull(
                            event.watering?.amountGallons?.let { "${it} gal" },
                            event.watering?.method?.displayFormat(),
                            event.watering?.notes,
                        ).joinToString(" \u2022 ")
                        else -> listOfNotNull(
                            event.fertilizer?.productName,
                            event.fertilizer?.amount,
                            event.fertilizer?.notes,
                        ).joinToString(" \u2022 ")
                    }

                    ListRow(
                        modifier = Modifier.pointerInput(event) {
                            detectTapGestures(onLongPress = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                if (event.type == "water") {
                                    event.watering?.let(onWateringLongPress)
                                } else {
                                    event.fertilizer?.let(onFertilizerLongPress)
                                }
                            })
                        },
                        title = if (event.type == "water") "Watered" else "Fertilized",
                        subtitle = "${eventDateStr}${if (subtitle.isNotBlank()) " • $subtitle" else ""}",
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
