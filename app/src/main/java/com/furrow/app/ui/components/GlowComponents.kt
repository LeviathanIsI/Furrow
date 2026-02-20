package com.furrow.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furrow.app.ui.theme.*

/**
 * Legacy wrapper retained for compatibility.
 * Prefer AppCard for default cards and AppGlowCard only for allowed state usage.
 */
@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    glowColor: Color = Color.Transparent,
    glowIntensity: Float = 0.15f,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    AppGlowCard(
        modifier = modifier,
        onClick = onClick,
        glowColor = if (glowColor != Color.Transparent) glowColor else GardenGlow,
        glowEnabled = glowIntensity > 0f,
        content = content,
    )
}

/**
 * Large stat number with a subtle glow halo behind it.
 * The halo is barely visible — just enough to give the number presence.
 */
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
            color = glowColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = DmSans,
            modifier = Modifier.drawBehind {
                // Very subtle halo behind the number — barely visible
                drawCircle(
                    color = glowColor.copy(alpha = 0.04f),
                    radius = size.maxDimension * 0.7f,
                )
            },
        )
        Text(
            text = label.uppercase(),
            color = TextTertiary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = DmSans,
            letterSpacing = 1.5.sp,
        )
    }
}

/**
 * Circular progress ring with rounded stroke caps.
 */
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
            // Track
            drawArc(
                color = BorderSubtle,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
            )
            // Progress arc
            if (clampedProgress > 0f) {
                drawArc(
                    color = glowColor,
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

/**
 * Horizontal progress bar with gradient fill.
 */
@Composable
fun GlowBar(
    progress: Float,
    glowColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        // Track
        drawRoundRect(
            color = BorderSubtle,
            cornerRadius = CornerRadius(this.size.height / 2),
        )
        // Fill
        if (clampedProgress > 0f) {
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.3f),
                        glowColor,
                    ),
                ),
                size = Size(this.size.width * clampedProgress, this.size.height),
                cornerRadius = CornerRadius(this.size.height / 2),
            )
        }
    }
}

/**
 * Empty state with a small icon, title, subtitle, and action button.
 * The icon gets a tiny, barely-visible glow. Nothing else.
 */
@Composable
fun EmptyState(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    actionLabel: String,
    glowColor: Color,
    onAction: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Icon with minimal glow — 80dp box, circle at 0.06 alpha
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .drawBehind {
                    drawCircle(
                        color = glowColor.copy(alpha = 0.06f),
                        radius = size.minDimension * 0.5f,
                    )
                },
        ) {
            icon()
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = DmSans,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            color = TextSecondary,
            fontSize = 14.sp,
            fontFamily = DmSans,
        )
        Spacer(modifier = Modifier.height(28.dp))
        OutlinedButton(
            onClick = onAction,
            border = BorderStroke(1.dp, glowColor.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = glowColor,
            ),
        ) {
            Text(
                text = actionLabel,
                fontFamily = DmSans,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
