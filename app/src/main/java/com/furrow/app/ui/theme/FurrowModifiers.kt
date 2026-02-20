package com.furrow.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun rememberGlowAlpha(): Float = 1f

enum class GlowPreset {
    Selected,
    Focused,
    Primary,
}

fun Modifier.stateGlow(
    color: Color,
    cornerRadius: Dp = AppRadius.card,
    spread: Dp = AppSpacing.xs,
    intensity: Float = 1f,
    preset: GlowPreset = GlowPreset.Selected,
): Modifier = this

fun Modifier.glowBorder(
    color: Color,
    cornerRadius: Dp = AppRadius.card,
    spread: Dp = AppSpacing.xs,
): Modifier = this

fun Modifier.shimmer(
    color: Color = Color.Transparent,
): Modifier = this
