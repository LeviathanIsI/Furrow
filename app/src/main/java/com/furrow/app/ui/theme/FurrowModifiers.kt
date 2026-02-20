package com.furrow.app.ui.theme

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
 * Animates a glow multiplier from 0 → 1 on first composition.
 * Use as a multiplier for glow alpha in drawBehind calls.
 */
@Composable
fun rememberGlowAlpha(): Float {
    var target by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) { target = 1f }
    val alpha by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(400, easing = EaseOutCubic),
        label = "glowEnter",
    )
    return alpha
}

enum class GlowPreset {
    Selected,
    Focused,
    Primary,
}

private data class GlowTuning(
    val wideSpreadMultiplier: Float,
    val wideColorAlpha: Float,
    val wideWhiteAlpha: Float,
    val tightSpreadMultiplier: Float,
    val tightColorAlpha: Float,
    val tightWhiteAlpha: Float,
    val topEdgeAlpha: Float,
    val topEdgeHeightMultiplier: Float,
    val colorDesaturation: Float,
)

private fun GlowPreset.tuning(): GlowTuning = when (this) {
    GlowPreset.Selected -> GlowTuning(
        wideSpreadMultiplier = 4.0f,
        wideColorAlpha = 0.028f,
        wideWhiteAlpha = 0.016f,
        tightSpreadMultiplier = 1.7f,
        tightColorAlpha = 0.075f,
        tightWhiteAlpha = 0.036f,
        topEdgeAlpha = 0.115f,
        topEdgeHeightMultiplier = 1.15f,
        colorDesaturation = 0.28f,
    )
    GlowPreset.Focused -> GlowTuning(
        wideSpreadMultiplier = 6.0f,
        wideColorAlpha = 0.062f,
        wideWhiteAlpha = 0.036f,
        tightSpreadMultiplier = 2.6f,
        tightColorAlpha = 0.158f,
        tightWhiteAlpha = 0.082f,
        topEdgeAlpha = 0.185f,
        topEdgeHeightMultiplier = 1.60f,
        colorDesaturation = 0.18f,
    )
    GlowPreset.Primary -> GlowTuning(
        wideSpreadMultiplier = 5.4f,
        wideColorAlpha = 0.054f,
        wideWhiteAlpha = 0.030f,
        tightSpreadMultiplier = 2.3f,
        tightColorAlpha = 0.134f,
        tightWhiteAlpha = 0.070f,
        topEdgeAlpha = 0.165f,
        topEdgeHeightMultiplier = 1.45f,
        colorDesaturation = 0.20f,
    )
}

private val GlowNeutral = Color(0xFFDCE2EE)

private fun Color.mix(target: Color, amount: Float): Color {
    val t = amount.coerceIn(0f, 1f)
    return Color(
        red = red + (target.red - red) * t,
        green = green + (target.green - green) * t,
        blue = blue + (target.blue - blue) * t,
        alpha = alpha,
    )
}

/**
 * Neon glow model drawn behind the component:
 * 1) Wide layer (large spread): low alpha color haze + white hot center.
 * 2) Tight layer (smaller spread): brighter near-edge bloom with white core.
 *
 * Corner strength is intentionally higher than edges and slightly asymmetric
 * to avoid a perfectly uniform "tube" outline.
 */
fun Modifier.stateGlow(
    color: Color,
    cornerRadius: Dp = AppRadius.card,
    spread: Dp = AppSpacing.xs,
    intensity: Float = 1f,
    preset: GlowPreset = GlowPreset.Selected,
): Modifier = this.drawBehind {
    val tuning = preset.tuning()
    val glowStrength = intensity.coerceIn(0f, 2f)
    val cornerPx = cornerRadius.toPx()
    val baseSpreadPx = spread.toPx().coerceAtLeast(1f)
    val wideSpreadPx = baseSpreadPx * tuning.wideSpreadMultiplier
    val tightSpreadPx = baseSpreadPx * tuning.tightSpreadMultiplier

    // Shift toward neutral and white so glow reads as light, not painted border.
    val neon = color.mix(GlowNeutral, tuning.colorDesaturation)

    val wideTopLeft = Offset(-wideSpreadPx, -wideSpreadPx)
    val wideSize = Size(size.width + wideSpreadPx * 2f, size.height + wideSpreadPx * 2f)
    val wideCorner = CornerRadius(cornerPx + wideSpreadPx)

    // Wide neon haze behind the body.
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = tuning.wideWhiteAlpha * glowStrength),
                neon.copy(alpha = tuning.wideColorAlpha * glowStrength),
                neon.copy(alpha = tuning.wideColorAlpha * 0.24f * glowStrength),
                Color.Transparent,
            ),
            center = Offset(size.width * 0.56f, size.height * 0.42f),
            radius = maxOf(size.width, size.height) * 0.70f + wideSpreadPx,
        ),
        topLeft = wideTopLeft,
        size = wideSize,
        cornerRadius = wideCorner,
    )

    val tightTopLeft = Offset(-tightSpreadPx, -tightSpreadPx)
    val tightSize = Size(size.width + tightSpreadPx * 2f, size.height + tightSpreadPx * 2f)
    val tightCorner = CornerRadius(cornerPx + tightSpreadPx)

    // Tight bloom around perimeter with white-hot center.
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = tuning.tightWhiteAlpha * glowStrength),
                neon.copy(alpha = tuning.tightColorAlpha * glowStrength),
                Color.Transparent,
            ),
            center = Offset(size.width * 0.52f, size.height * 0.48f),
            radius = maxOf(size.width, size.height) * 0.50f + tightSpreadPx,
        ),
        topLeft = tightTopLeft,
        size = tightSize,
        cornerRadius = tightCorner,
    )

    // Top-edge light is subtle and intentionally asymmetric.
    val edgeHeight = tightSpreadPx * tuning.topEdgeHeightMultiplier
    val topEdgeTopLeft = Offset(-tightSpreadPx, -edgeHeight)
    val topEdgeSize = Size(size.width + tightSpreadPx * 2f, edgeHeight * 2.2f)
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = tuning.topEdgeAlpha * 0.75f * glowStrength),
                neon.copy(alpha = tuning.topEdgeAlpha * glowStrength),
                Color.Transparent,
            ),
            startY = -edgeHeight,
            endY = edgeHeight * 1.35f,
        ),
        topLeft = topEdgeTopLeft,
        size = topEdgeSize,
        cornerRadius = CornerRadius(cornerPx + tightSpreadPx),
    )
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                neon.copy(alpha = tuning.topEdgeAlpha * 0.35f * glowStrength),
                Color.White.copy(alpha = tuning.topEdgeAlpha * 0.70f * glowStrength),
            ),
            startX = 0f,
            endX = size.width,
        ),
        topLeft = topEdgeTopLeft,
        size = topEdgeSize,
        cornerRadius = CornerRadius(cornerPx + tightSpreadPx),
    )

    // D1 asymmetry: one dominant corner bloom.
    val dominantCorner = Offset(size.width * 0.88f, size.height * 0.10f)
    val dominantRadius = tightSpreadPx * 2.35f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = tuning.tightWhiteAlpha * 1.25f * glowStrength),
                neon.copy(alpha = tuning.tightColorAlpha * 1.20f * glowStrength),
                Color.Transparent,
            ),
            center = dominantCorner,
            radius = dominantRadius,
        ),
        radius = dominantRadius,
        center = dominantCorner,
    )
}

/**
 * Backwards-compatible alias; use stateGlow for new code.
 */
fun Modifier.glowBorder(
    color: Color,
    cornerRadius: Dp = AppRadius.card,
    spread: Dp = AppSpacing.xs,
): Modifier = stateGlow(
    color = color,
    cornerRadius = cornerRadius,
    spread = spread,
)

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
