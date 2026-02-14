package com.furrow.app.ui.theme

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Neutral99,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = Brown40,
    onSecondary = Neutral99,
    secondaryContainer = Brown90,
    onSecondaryContainer = Brown10,
    tertiary = Amber40,
    onTertiary = Neutral99,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber10,
    error = Error40,
    onError = Neutral99,
    errorContainer = Error90,
    onErrorContainer = Error10,
    background = SurfaceContainerLowestLight,
    onBackground = Neutral10,
    surface = SurfaceContainerLowLight,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
    surfaceBright = SurfaceBrightLight,
    surfaceDim = SurfaceDimLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    secondary = Brown80,
    onSecondary = Brown20,
    secondaryContainer = Brown30,
    onSecondaryContainer = Brown90,
    tertiary = Amber80,
    onTertiary = Amber20,
    tertiaryContainer = Amber30,
    onTertiaryContainer = Amber90,
    error = Error80,
    onError = Error20,
    errorContainer = Error30,
    onErrorContainer = Error90,
    background = SurfaceContainerLowestDark,
    onBackground = Neutral90,
    surface = SurfaceContainerLowDark,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant60,
    outline = NeutralVariant60,
    outlineVariant = NeutralVariant30,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    surfaceBright = SurfaceBrightDark,
    surfaceDim = SurfaceDimDark,
)

@Immutable
data class FurrowColors(
    val beeAccent: Color,
    val beeContainer: Color,
    val poultryAccent: Color,
    val poultryContainer: Color,
    val gardenAccent: Color,
    val gardenContainer: Color,
)

private val LightFurrowColors = FurrowColors(
    beeAccent = BeeGold,
    beeContainer = BeeGoldContainerLight,
    poultryAccent = PoultryWarm,
    poultryContainer = PoultryWarmContainerLight,
    gardenAccent = GardenRich,
    gardenContainer = GardenRichContainerLight,
)

private val DarkFurrowColors = FurrowColors(
    beeAccent = BeeGoldDark,
    beeContainer = BeeGoldContainer,
    poultryAccent = PoultryWarmDark,
    poultryContainer = PoultryWarmContainer,
    gardenAccent = GardenRichDark,
    gardenContainer = GardenRichContainer,
)

val LocalFurrowColors = staticCompositionLocalOf { LightFurrowColors }

val FurrowShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun FurrowBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val bg = MaterialTheme.colorScheme.background
    Box(
        modifier = modifier.drawBehind {
            drawRect(color = bg)
        }
    ) {
        content()
    }
}

object FurrowCardDefaults {
    val elevatedCardElevation: CardElevation
        @Composable get() = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)

    val elevatedCardColors: CardColors
        @Composable get() = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )

    val elevatedCardBorder: BorderStroke
        @Composable get() = BorderStroke(
            0.5.dp,
            CardBorderDark.copy(alpha = 0.5f),
        )

    val formCardColors: CardColors
        @Composable get() = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )

    val outlinedCardColors: CardColors
        @Composable get() = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )

    val outlinedCardBorder: BorderStroke
        @Composable get() = BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
        )
}

@Composable
fun GradientCard(
    gradientStart: Color,
    gradientEnd: Color,
    modifier: Modifier = Modifier,
    borderColor: Color = CardBorderDark,
    backgroundIcon: ImageVector? = null,
    backgroundIconTint: Color = Color.White.copy(alpha = 0.05f),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(0.5.dp, borderColor.copy(alpha = 0.5f)),
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(gradientStart, gradientEnd),
                    )
                ),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                content()
            }
            if (backgroundIcon != null) {
                Icon(
                    backgroundIcon,
                    contentDescription = null,
                    tint = backgroundIconTint,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = 10.dp),
                )
            }
        }
    }
}

@Composable
fun FurrowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val furrowColors = if (darkTheme) DarkFurrowColors else LightFurrowColors

    CompositionLocalProvider(LocalFurrowColors provides furrowColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = FurrowShapes,
            content = content
        )
    }
}
