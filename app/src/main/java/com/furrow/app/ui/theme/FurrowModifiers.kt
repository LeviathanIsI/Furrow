package com.furrow.app.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws a layered glow effect behind the composable using three
 * concentric rounded rectangles at decreasing alpha.
 */
fun Modifier.glowBorder(
    color: Color,
    cornerRadius: Dp = 16.dp,
    spread: Dp = 8.dp,
): Modifier = this.drawBehind {
    val cornerPx = cornerRadius.toPx()
    val spreadPx = spread.toPx()
    for ((index, alpha) in listOf(0.3f, 0.15f, 0.05f).withIndex()) {
        val expansion = spreadPx * (index + 1)
        drawRoundRect(
            color = color.copy(alpha = alpha),
            topLeft = Offset(-expansion, -expansion),
            size = Size(size.width + expansion * 2, size.height + expansion * 2),
            cornerRadius = CornerRadius(cornerPx + expansion),
        )
    }
}

/**
 * Adds a subtle animated shimmer sweep across the composable.
 * Used on empty-state icons/cards to make them feel alive.
 */
fun Modifier.shimmer(
    color: Color = Color.White.copy(alpha = 0.06f),
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
        ),
        label = "shimmerTranslate",
    )
    this.drawWithContent {
        drawContent()
        val shimmerBrush = Brush.linearGradient(
            colors = listOf(Color.Transparent, color, Color.Transparent),
            start = Offset(size.width * translateX, 0f),
            end = Offset(size.width * (translateX + 1f), size.height),
        )
        drawRect(brush = shimmerBrush)
    }
}
