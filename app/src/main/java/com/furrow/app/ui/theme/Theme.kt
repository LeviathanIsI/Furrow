package com.furrow.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = Void,
    surface = Obsidian,
    surfaceVariant = Charcoal,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderVisible,
    primary = GardenGlow,
    onPrimary = Void,
    secondary = GardenGlow,
    onSecondary = Void,
    tertiary = GardenGlow,
    onTertiary = Void,
    error = StatusBad,
    onError = TextPrimary,
    surfaceContainerLowest = Void,
    surfaceContainerLow = Obsidian,
    surfaceContainer = Charcoal,
    surfaceContainerHigh = Graphite,
    surfaceContainerHighest = Slate,
)

@Composable
fun FurrowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = FurrowTypography,
        content = content,
    )
}
