package com.furrow.app.ui.garden.tabs

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Science
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
import androidx.compose.ui.unit.sp
import com.furrow.app.data.local.entity.FertilizerLog
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.WateringLog
import com.furrow.app.ui.components.AppChip
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.AppRadius
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.shimmer
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val zone: ZoneId = ZoneId.systemDefault()
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
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

    val events = remember(wateringLogs, fertilizerLogs) {
        val waterEvents = wateringLogs.map { CareEvent(it.date, "water", watering = it) }
        val fertEvents = fertilizerLogs.map { CareEvent(it.date, "fertilizer", fertilizer = it) }
        (waterEvents + fertEvents).sortedByDescending { it.date }
    }

    val fertRecommendations = remember(plantings, plantInfoMap) {
        plantings.mapNotNull { planting ->
            val info = plantInfoMap[planting.plantName] ?: return@mapNotNull null
            if (info.fertilizerType != null || info.fertilizerNeeds != null) {
                Triple(planting.plantName, info.fertilizerType, info.fertilizerFrequency)
            } else null
        }.distinctBy { it.first }
    }

    if (events.isEmpty() && fertRecommendations.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(AppRadius.card))
                    .shimmer(),
            ) {
                Icon(
                    Icons.Outlined.Opacity,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = TextTertiary.copy(alpha = 0.6f),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No care logs yet",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary,
                )
                Text(
                    "Tap + to log watering or fertilizer",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, top = 16.dp, bottom = AppSpacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (fertRecommendations.isNotEmpty()) {
                item(key = "fert_recs_header") {
                    Text(
                        "FERTILIZER RECOMMENDATIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = GardenGlow,
                        letterSpacing = 1.5.sp,
                    )
                }
                item(key = "fert_recs") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        fertRecommendations.forEach { (name, type, freq) ->
                            Column {
                                AppChip(
                                    text = name,
                                    selected = true,
                                    accentColor = GardenGlow,
                                )
                                val detail = listOfNotNull(type, freq).joinToString(" \u2022 ")
                                if (detail.isNotBlank()) {
                                    Text(
                                        detail,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                    )
                                }
                            }
                        }
                    }
                }
                item(key = "fert_recs_spacer") {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (events.isNotEmpty()) {
                item(key = "timeline_header") {
                    Text(
                        "CARE LOG",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        letterSpacing = 1.5.sp,
                    )
                }
            }

            items(events, key = { "${it.type}_${it.watering?.id ?: it.fertilizer?.id}" }) { event ->
                val eventDate = remember(event.date) {
                    Instant.ofEpochMilli(event.date).atZone(zone).toLocalDate()
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                when (event.type) {
                                    "water" -> event.watering?.let { onWateringLongPress(it) }
                                    "fertilizer" -> event.fertilizer?.let { onFertilizerLongPress(it) }
                                }
                            },
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() },
                        ),
                    shape = RoundedCornerShape(12.dp),
                    color = Charcoal,
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val (icon, iconTint, label) = when (event.type) {
                            "water" -> Triple(
                                Icons.Filled.WaterDrop,
                                GardenGlow,
                                "Watered",
                            )
                            else -> Triple(
                                Icons.Outlined.Science,
                                StatusWarn,
                                "Fertilized",
                            )
                        }
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = iconTint,
                                )
                                Text(
                                    eventDate.format(dateFormatter),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                            when (event.type) {
                                "water" -> event.watering?.let { w ->
                                    val details = listOfNotNull(
                                        w.amountGallons?.let { "${it} gal" },
                                        w.method,
                                    ).joinToString(" \u2022 ")
                                    if (details.isNotBlank()) {
                                        Text(
                                            details,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                        )
                                    }
                                    w.notes?.let {
                                        Text(
                                            it,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextTertiary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                                "fertilizer" -> event.fertilizer?.let { f ->
                                    val details = listOfNotNull(
                                        f.productName,
                                        f.amount,
                                    ).joinToString(" \u2022 ")
                                    if (details.isNotBlank()) {
                                        Text(
                                            details,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                        )
                                    }
                                    f.notes?.let {
                                        Text(
                                            it,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextTertiary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
