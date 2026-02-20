package com.furrow.app.ui.theme

import androidx.compose.ui.graphics.Color

val Void = Color(0xFF0B0D0C)
val Obsidian = Color(0xFF111513)
val Charcoal = Color(0xFF181D1A)
val Graphite = Color(0xFF222923)
val Slate = Color(0xFF2A322B)

val TextPrimary = Color(0xFFF4F6F4)
val TextSecondary = Color(0xFFC0C7C0)
val TextTertiary = Color(0xFF98A094)
val TextMuted = Color(0xFF6F776F)

val BorderSubtle = Color.White.copy(alpha = 0.16f)
val BorderVisible = Color.White.copy(alpha = 0.30f)
val BorderFocus = Color.White.copy(alpha = 0.36f)
val StrokeSubtle = BorderSubtle
val StrokeMedium = BorderVisible
val StrokeStrong = BorderFocus

val AccentMutedGreen = Color(0xFF7A9669)

// Keep legacy names for compatibility while enforcing single accent.
val BeeGlow = AccentMutedGreen
val PoultryGlow = AccentMutedGreen
val GardenGlow = AccentMutedGreen

val StatusGood = AccentMutedGreen
val StatusWarn = Color(0xFFC19A57)
val StatusBad = Color(0xFFCA6C67)
