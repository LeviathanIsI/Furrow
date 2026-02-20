package com.furrow.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furrow.app.ui.theme.AppRadius
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.DmSans
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary

@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    glowColor: Color = Color.Transparent,
    glowIntensity: Float = 0.15f,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    AppCard(
        modifier = modifier,
        onClick = onClick,
        content = content,
    )
}

@Composable
fun StatNumber(
    value: String,
    label: String,
    glowColor: Color,
    fontSize: Int = 48,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = TextPrimary,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = DmSans,
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = DmSans,
        )
    }
}

@Composable
fun GlowRing(
    progress: Float,
    glowColor: Color,
    size: Dp = 100.dp,
    strokeWidth: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = BorderSubtle,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
            )
            if (clampedProgress > 0f) {
                drawArc(
                    color = TextPrimary,
                    startAngle = -90f,
                    sweepAngle = 360f * clampedProgress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
                )
            }
        }
        content()
    }
}

@Composable
fun GlowBar(
    progress: Float,
    glowColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    androidx.compose.material3.LinearProgressIndicator(
        progress = { clampedProgress },
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        color = TextPrimary,
        trackColor = BorderSubtle,
        gapSize = 0.dp,
    )
}

@Composable
fun EmptyState(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    actionLabel: String,
    glowColor: Color,
    onAction: () -> Unit,
) {
    com.furrow.app.ui.components.EmptyState(
        title = title,
        subtitle = subtitle,
        icon = icon,
        actionLabel = actionLabel,
        onAction = onAction,
    )
}
