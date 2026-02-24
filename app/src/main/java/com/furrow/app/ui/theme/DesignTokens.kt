package com.furrow.app.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppSpacing {
    val xxs: Dp = 4.dp
    val xs: Dp = 8.dp
    val sm: Dp = 12.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val bottomListPadding: Dp = 80.dp
}

object AppRadius {
    val none: Dp = 0.dp
    val sm: Dp = 4.dp
    val md: Dp = 8.dp

    // Compatibility aliases used across existing UI code.
    val input: Dp = md
    val chip: Dp = md
    val card: Dp = md
    val sheet: Dp = md
}

object AppStroke {
    const val subtleAlpha: Float = 0.10f
    const val mediumAlpha: Float = 0.18f
    const val strongAlpha: Float = 0.28f
}
