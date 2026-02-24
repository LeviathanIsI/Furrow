package com.furrow.app.ui.bees

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.furrow.app.data.local.entity.BeeRaceInfo
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.StatusWarn

internal enum class ClimateBadge(val label: String) {
    GREAT("Great for your climate"),
    MANAGEABLE("Manageable"),
    NOT_IDEAL("Not ideal"),
}

internal fun climateBadgeColor(badge: ClimateBadge): Color = when (badge) {
    ClimateBadge.GREAT -> StatusGood
    ClimateBadge.MANAGEABLE -> StatusWarn
    ClimateBadge.NOT_IDEAL -> StatusBad
}

internal fun climateBadgeFor(race: BeeRaceInfo, zoneGroup: String): ClimateBadge {
    val heatFitness = when (race.name) {
        "Italian" -> 5
        "Saskatraz" -> 4
        "Buckfast" -> 4
        "Russian" -> 3
        "Caucasian" -> 2
        "Carniolan" -> 2
        "German Dark" -> 1
        else -> 3
    }
    val score = when (zoneGroup) {
        "hot", "warm" -> heatFitness
        "cold", "extreme_cold" -> race.overwinteringAbility
        else -> minOf(heatFitness, race.overwinteringAbility)
    }
    return when {
        score >= 4 -> ClimateBadge.GREAT
        score == 3 -> ClimateBadge.MANAGEABLE
        else -> ClimateBadge.NOT_IDEAL
    }
}

@Composable
internal fun ClimateBadgePill(badge: ClimateBadge) {
    val color = climateBadgeColor(badge)
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
    ) {
        Text(
            badge.label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
