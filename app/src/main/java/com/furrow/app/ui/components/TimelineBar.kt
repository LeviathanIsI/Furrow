package com.furrow.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.TextPrimary
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun TimelineBar(
    startDate: LocalDate,
    endDate: LocalDate,
    windowStart: LocalDate?,
    windowEnd: LocalDate?,
    today: LocalDate,
    glowColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
) {
    val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toFloat().coerceAtLeast(1f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        val cornerPx = size.height / 2f

        // Full track
        drawRoundRect(
            color = BorderSubtle,
            cornerRadius = CornerRadius(cornerPx),
        )

        // Window highlight
        if (windowStart != null && windowEnd != null) {
            val wStartFraction = (ChronoUnit.DAYS.between(startDate, windowStart) / totalDays)
                .coerceIn(0f, 1f)
            val wEndFraction = (ChronoUnit.DAYS.between(startDate, windowEnd) / totalDays)
                .coerceIn(0f, 1f)

            if (wEndFraction > wStartFraction) {
                val wStartX = size.width * wStartFraction
                val wWidth = size.width * (wEndFraction - wStartFraction)

                val isPastWindow = today.isAfter(windowEnd)
                val windowColor = if (isPastWindow) StatusBad.copy(alpha = 0.4f)
                else glowColor.copy(alpha = 0.3f)

                drawRoundRect(
                    color = windowColor,
                    topLeft = Offset(wStartX, 0f),
                    size = Size(wWidth, size.height),
                    cornerRadius = CornerRadius(cornerPx),
                )
            }
        }

        // Today marker
        val todayFraction = (ChronoUnit.DAYS.between(startDate, today) / totalDays)
            .coerceIn(0f, 1f)
        val todayX = size.width * todayFraction
        val diamondSize = size.height * 1.2f

        // Glow behind marker
        drawCircle(
            color = TextPrimary.copy(alpha = 0.15f),
            radius = diamondSize,
            center = Offset(todayX, size.height / 2f),
        )

        // Diamond marker
        val cx = todayX
        val cy = size.height / 2f
        val half = diamondSize / 2f
        val diamond = Path().apply {
            moveTo(cx, cy - half)
            lineTo(cx + half, cy)
            lineTo(cx, cy + half)
            lineTo(cx - half, cy)
            close()
        }
        drawPath(diamond, color = TextPrimary)
    }
}
