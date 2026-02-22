package com.furrow.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furrow.app.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class SplashModule(
    val label: String,
    val color: Color,
    val radiusDp: Float,
    val strokeDp: Float,
    val sweepStartMs: Int,
    val sweepDurationMs: Int,
    val startAngleDeg: Float,  // offset from top (0 = 12 o'clock)
)

private val modules = listOf(
    SplashModule("BEES",       Color(0xFFFFB300), 185f, 2.5f,    0,  700,   30f),  // amber — 1 o'clock
    SplashModule("GARDEN",     Color(0xFF6ECF72), 163f, 2.5f,  850,  600,  170f),  // green — 7 o'clock
    SplashModule("ANIMALS",    Color(0xFFCDA67A), 141f, 2.25f, 1550, 500,  290f),  // tan — 11 o'clock
    SplashModule("ORCHARD",    Color(0xFFE87CB0), 119f, 2.15f, 2150, 425,   75f),  // rose pink — 3 o'clock
    SplashModule("PRESERVE",   Color(0xFFDA9570),  97f, 2.0f,  2650, 375,  210f),  // terracotta — 8 o'clock
    SplashModule("LAND",       Color(0xFF70C4D8),  75f, 1.9f,  3100, 325,  330f),  // sky blue — 12 o'clock
    SplashModule("FINANCE",    Color(0xFFB89ADE),  53f, 1.8f,  3475, 275,  120f),  // lavender — 5 o'clock
    SplashModule("COMPLIANCE", Color(0xFF92AAC0),  31f, 1.75f, 3800, 250,  245f),  // steel blue — 9 o'clock
)

private const val MODULE_COUNT = 8

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    // ── 1. Breathing gradient ────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathScale",
    )

    // ── 2. Grid ──────────────────────────────────────────────────────────
    val gridAlpha = remember { Animatable(0f) }

    // ── Ring animatables (array-based) ────────────────────────────────────
    val sweeps       = remember { List(MODULE_COUNT) { Animatable(0f) } }
    val leadAlphas   = remember { List(MODULE_COUNT) { Animatable(0.8f) } }
    val flashAlphas  = remember { List(MODULE_COUNT) { Animatable(0f) } }
    val shockProgs   = remember { List(MODULE_COUNT) { Animatable(0f) } }
    val burstProgs   = remember { List(MODULE_COUNT) { Animatable(0f) } }
    val labelAlphas  = remember { List(MODULE_COUNT) { Animatable(0f) } }
    val dotAlphas    = remember { List(MODULE_COUNT) { Animatable(0f) } }

    // ── Per-ring rotation (scattered → aligned) ───────────────────────────
    val ringRotations = remember { List(MODULE_COUNT) { Animatable(0f) } }

    // ── Shared animatables ────────────────────────────────────────────────
    val ringsAlpha = remember { Animatable(1f) }
    val ringScale = remember { Animatable(1f) }
    val webLinesProgress = remember { Animatable(0f) }

    // ── Logo reveal line ─────────────────────────────────────────────────
    val logoLineProgress = remember { Animatable(0f) }
    val logoLineAlpha = remember { Animatable(0f) }

    // ── Title ────────────────────────────────────────────────────────────
    val titleAlpha = remember { Animatable(0f) }
    val titleOffsetDp = remember { Animatable(20f) }
    val titleLetterSpacing = remember { Animatable(8f) }

    // ── Bass-drop canvas scale ────────────────────────────────────────────
    val canvasScale = remember { Animatable(1f) }

    // ── Tagline typewriter ───────────────────────────────────────────────
    val taglineCharCount = remember { mutableIntStateOf(0) }
    val periodFlashAlpha = remember { Animatable(0f) }

    // ── Module dots ──────────────────────────────────────────────────────
    val connectLineProgress = remember { Animatable(0f) }

    // ── Exit ─────────────────────────────────────────────────────────────
    val textExitAlpha = remember { Animatable(1f) }
    val textExitScale = remember { Animatable(1f) }
    val implosionFlashAlpha = remember { Animatable(0f) }

    // ═════════════════════════════════════════════════════════════════════
    //  TIMELINE
    // ═════════════════════════════════════════════════════════════════════
    LaunchedEffect(Unit) {
        // Grid fade in
        launch { gridAlpha.animateTo(0.04f, tween(500)) }

        // ── Ring sweep phases (data-driven loop) ──────────────────────
        for (i in modules.indices) {
            val mod = modules[i]
            val sweepEnd = mod.sweepStartMs + mod.sweepDurationMs

            // Sweep arc
            launch {
                delay(mod.sweepStartMs.toLong())
                sweeps[i].animateTo(360f, tween(mod.sweepDurationMs, easing = EaseInOutCubic))
            }

            // Lead glow dims after sweep completes
            launch {
                delay(sweepEnd.toLong())
                leadAlphas[i].animateTo(0.3f, tween(200))
            }

            // Flash on completion
            launch {
                delay(sweepEnd.toLong())
                flashAlphas[i].snapTo(0.5f)
                flashAlphas[i].animateTo(0f, tween(150))
            }

            // Shockwave
            launch {
                delay(sweepEnd.toLong())
                shockProgs[i].animateTo(1f, tween(200))
            }

            // Burst particles
            launch {
                delay(sweepEnd.toLong())
                burstProgs[i].animateTo(1f, tween(300))
            }

            // Label flashes in center on ring completion, fades before next
            launch {
                delay(sweepEnd.toLong())
                labelAlphas[i].snapTo(1f)
                val nextEnd = if (i < modules.size - 1) {
                    modules[i + 1].sweepStartMs + modules[i + 1].sweepDurationMs
                } else {
                    4300
                }
                val fadeDuration = (nextEnd - sweepEnd).coerceAtLeast(150)
                labelAlphas[i].animateTo(0f, tween(fadeDuration, easing = EaseOutCubic))
            }
        }

        // ── Phase 4: Logo reveal (4300–5400ms) ───────────────────────
        // Ring alignment — all completion dots rotate to top (12 o'clock)
        for (i in modules.indices) {
            launch {
                delay(4300L + i * 30L)
                // Shortest-path rotation to bring dot to top (undo startAngleDeg)
                val target = -modules[i].startAngleDeg
                val normalized = ((target % 360f) + 540f) % 360f - 180f
                ringRotations[i].animateTo(normalized, tween(500, easing = EaseInOutCubic))
            }
        }

        // Web lines draw outward
        launch { delay(4600); webLinesProgress.animateTo(1f, tween(400, easing = EaseOutCubic)) }

        // Ring pulse
        launch {
            delay(4600)
            ringScale.animateTo(1.03f, tween(150, easing = EaseInOutCubic))
            ringScale.animateTo(1f, tween(150, easing = EaseInOutCubic))
        }

        // Grid out + rings dim
        launch { delay(4600); gridAlpha.animateTo(0f, tween(400)) }
        launch { delay(4600); ringsAlpha.animateTo(0.15f, tween(500, easing = EaseOutCubic)) }

        // Logo reveal line (wipe → flash → fade)
        launch {
            delay(4800)
            logoLineAlpha.snapTo(0.5f)
            logoLineProgress.animateTo(1f, tween(300, easing = EaseOutCubic))
            logoLineAlpha.snapTo(0.8f)
            delay(16)
            logoLineAlpha.animateTo(0f, tween(100))
        }

        // Title
        launch {
            delay(4900)
            launch { titleAlpha.animateTo(1f, tween(500, easing = EaseOutCubic)) }
            launch { titleOffsetDp.animateTo(0f, tween(500, easing = EaseOutCubic)) }
            launch { titleLetterSpacing.animateTo(4f, tween(500, easing = EaseOutCubic)) }
        }

        // Bass-drop scale punch
        launch {
            delay(5400)
            canvasScale.animateTo(1.025f, tween(75, easing = EaseOutCubic))
            canvasScale.animateTo(1f, tween(75, easing = EaseInCubic))
        }

        // Tagline typewriter
        launch {
            delay(5200)
            val text = "grow smarter."
            for (i in 1..text.length) {
                taglineCharCount.intValue = i
                delay(40)
            }
            periodFlashAlpha.snapTo(1f)
            delay(200)
            periodFlashAlpha.animateTo(0f, tween(100))
        }

        // Module dots — 8 dots at 50ms intervals starting at 5600ms
        for (i in modules.indices) {
            launch {
                delay(5600L + i * 50L)
                dotAlphas[i].animateTo(1f, tween(200, easing = EaseOutCubic))
            }
        }
        launch { delay(5600); connectLineProgress.animateTo(1f, tween(300, easing = EaseOutCubic)) }

        // ── Phase 5: Exit (6200–6700ms) ──────────────────────────────
        launch {
            delay(6200)
            launch { ringScale.animateTo(0f, tween(250, easing = EaseInCubic)) }
            launch { webLinesProgress.animateTo(0f, tween(250, easing = EaseInCubic)) }
        }
        launch { delay(6250); implosionFlashAlpha.snapTo(0.03f); delay(100); implosionFlashAlpha.snapTo(0f) }
        launch {
            delay(6400)
            launch { textExitAlpha.animateTo(0f, tween(300)) }
            launch { textExitScale.animateTo(1.1f, tween(300)) }
        }

        delay(6700)
        onFinished()
    }

    // ═════════════════════════════════════════════════════════════════════
    //  DRAW
    // ═════════════════════════════════════════════════════════════════════
    val textMeasurer = rememberTextMeasurer()
    val dmSansBold = remember { FontFamily(Font(R.font.dm_sans_bold, FontWeight.Bold)) }
    val dmSansMedium = remember { FontFamily(Font(R.font.dm_sans_medium, FontWeight.Medium)) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = canvasScale.value
                scaleY = canvasScale.value
            },
    ) {
        val bg = Color(0xFF050505)
        val gardenGlow = Color(0xFF6ECF72)
        val beeGlow = Color(0xFFFFB300)
        val textPrimary = Color(0xFFEAEAEA)
        val textTertiary = Color(0xFF505050)
        val textMuted = Color(0xFF333333)

        val cx = size.width / 2f
        val cy = size.height / 2f
        val diagonal = sqrt(size.width * size.width + size.height * size.height)

        // ── Background ───────────────────────────────────────────────────
        drawRect(color = bg, size = size)

        // Breathing — primary gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(gardenGlow.copy(alpha = 0.04f), Color.Transparent),
                center = Offset(cx, cy),
                radius = diagonal * breathScale,
            ),
            radius = diagonal,
            center = Offset(cx, cy),
        )
        // Secondary gradient — offset amber glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(beeGlow.copy(alpha = 0.015f), Color.Transparent),
                center = Offset(cx + 50.dp.toPx(), cy - 30.dp.toPx()),
                radius = diagonal * breathScale,
            ),
            radius = diagonal,
            center = Offset(cx, cy),
        )

        // Grid
        if (gridAlpha.value > 0f) {
            val spacing = 40.dp.toPx()
            val gridStroke = 0.5f.dp.toPx()
            var gy = spacing
            while (gy < size.height) {
                drawLine(textMuted, Offset(0f, gy), Offset(size.width, gy), gridStroke, alpha = gridAlpha.value)
                gy += spacing
            }
            var gx = spacing
            while (gx < size.width) {
                drawLine(textMuted, Offset(gx, 0f), Offset(gx, size.height), gridStroke, alpha = gridAlpha.value)
                gx += spacing
            }
        }

        // ── Glow layer specs (scaled down ~60% for 16dp ring spacing) ──
        val glowStroke = 5.dp.toPx()
        val ringAlpha = ringsAlpha.value

        val glowRadii = floatArrayOf(19f, 16.8f, 14.4f, 12.6f, 10.8f, 9f, 7.2f, 5.4f, 4.2f, 3f, 1.8f, 1f)
        val glowAlphas = floatArrayOf(0.006f, 0.009f, 0.012f, 0.018f, 0.027f, 0.036f, 0.048f, 0.072f, 0.108f, 0.168f, 0.27f, 0.5f)
        val trailGlowRadii = floatArrayOf(11f, 9.6f, 8.4f, 7.2f, 6f, 5.2f, 4.2f, 3.2f, 2.4f, 1.8f, 1.1f, 0.6f)
        val trailGlowAlphas = floatArrayOf(0.003f, 0.0045f, 0.006f, 0.009f, 0.0135f, 0.018f, 0.024f, 0.036f, 0.054f, 0.084f, 0.135f, 0.25f)
        val burstDotR = 1.2f.dp.toPx()
        val burstDist = 15.dp.toPx()

        fun drawRing(
            radius: Float,
            sweep: Float,
            color: Color,
            leadAlpha: Float,
            flashAlpha: Float,
            strokeW: Float,
            burstProgress: Float,
        ) {
            if (sweep <= 0f) return
            val topLeft = Offset(cx - radius, cy - radius)
            val arcSize = Size(radius * 2f, radius * 2f)

            // Tapered scanning trail (only while drawing)
            if (sweep < 360f) {
                val primarySweep = 20f.coerceAtMost(sweep)
                drawArc(
                    color = color,
                    startAngle = -90f + sweep - primarySweep,
                    sweepAngle = primarySweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(8.dp.toPx(), cap = StrokeCap.Round),
                    alpha = 0.10f * ringAlpha,
                )
                val secondarySweep = 10f.coerceAtMost(sweep)
                drawArc(
                    color = color,
                    startAngle = -90f + sweep - secondarySweep,
                    sweepAngle = secondarySweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(12.dp.toPx(), cap = StrokeCap.Round),
                    alpha = 0.04f * ringAlpha,
                )
            }

            // Glow layer
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(glowStroke, cap = StrokeCap.Round),
                alpha = 0.08f * ringAlpha,
            )

            // Main stroke
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(strokeW, cap = StrokeCap.Round),
                alpha = ringAlpha,
            )

            // Hot spot — 60° at 2x stroke
            val hotSpotSweep = 60f.coerceAtMost(sweep)
            drawArc(
                color = color,
                startAngle = -90f + sweep - hotSpotSweep,
                sweepAngle = hotSpotSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(strokeW * 2f, cap = StrokeCap.Round),
                alpha = 0.25f * ringAlpha,
            )

            // Flash overlay
            if (flashAlpha > 0f) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(strokeW * 3f, cap = StrokeCap.Round),
                    alpha = flashAlpha,
                )
            }

            // Comet trail — 5 trailing glow dots
            for (t in 0 until 5) {
                val offset = t + 1
                if (sweep > offset) {
                    val trailAngle = Math.toRadians(-90.0 + sweep - offset)
                    val trailCenter = Offset(
                        cx + radius * cos(trailAngle).toFloat(),
                        cy + radius * sin(trailAngle).toFloat(),
                    )
                    val trailFade = (1f - t * 0.18f).coerceAtLeast(0.1f)
                    for (g in trailGlowRadii.indices) {
                        drawCircle(
                            color = color,
                            radius = trailGlowRadii[g].dp.toPx(),
                            center = trailCenter,
                            alpha = trailGlowAlphas[g] * trailFade * leadAlpha * ringAlpha,
                        )
                    }
                }
            }

            // Leading edge glow — concentric circles for point-light falloff
            val leadAngle = Math.toRadians(-90.0 + sweep)
            val leadCenter = Offset(
                cx + radius * cos(leadAngle).toFloat(),
                cy + radius * sin(leadAngle).toFloat(),
            )
            for (g in glowRadii.indices) {
                drawCircle(
                    color = color,
                    radius = glowRadii[g].dp.toPx(),
                    center = leadCenter,
                    alpha = glowAlphas[g] * leadAlpha * ringAlpha,
                )
            }

            // Completion dot glow
            if (sweep >= 360f) {
                val completionCenter = Offset(cx, cy - radius)
                for (g in glowRadii.indices) {
                    drawCircle(
                        color = color,
                        radius = glowRadii[g].dp.toPx(),
                        center = completionCenter,
                        alpha = glowAlphas[g] * ringAlpha,
                    )
                }
            }

            // Burst particles — 8 radial dots from completion point
            if (burstProgress > 0f && burstProgress < 1f) {
                val dist = burstDist * burstProgress
                val bAlpha = 0.6f * (1f - burstProgress) * ringAlpha
                val origin = Offset(cx, cy - radius)
                for (j in 0 until 8) {
                    val angle = Math.toRadians(j * 45.0)
                    drawCircle(
                        color = color,
                        radius = burstDotR,
                        center = Offset(
                            origin.x + dist * cos(angle).toFloat(),
                            origin.y + dist * sin(angle).toFloat(),
                        ),
                        alpha = bAlpha,
                    )
                }
            }
        }

        // ── Ring scale transform (pulse → implosion) ─────────────────────
        drawContext.canvas.save()
        drawContext.canvas.translate(cx, cy)
        drawContext.canvas.scale(ringScale.value, ringScale.value)
        drawContext.canvas.translate(-cx, -cy)

        // Draw all 8 rings — each rotated by its own scattered angle
        for (i in modules.indices) {
            val mod = modules[i]
            val rotation = mod.startAngleDeg + ringRotations[i].value

            drawContext.canvas.save()
            drawContext.canvas.translate(cx, cy)
            drawContext.canvas.rotate(rotation)
            drawContext.canvas.translate(-cx, -cy)

            drawRing(
                radius = mod.radiusDp.dp.toPx(),
                sweep = sweeps[i].value,
                color = mod.color,
                leadAlpha = leadAlphas[i].value,
                flashAlpha = flashAlphas[i].value,
                strokeW = mod.strokeDp.dp.toPx(),
                burstProgress = burstProgs[i].value,
            )

            // Shockwave (drawn in ring's rotated space)
            val progress = shockProgs[i].value
            if (progress > 0f && progress < 1f) {
                drawCircle(
                    color = mod.color,
                    radius = 12.dp.toPx() * progress,
                    center = Offset(cx, cy - mod.radiusDp.dp.toPx()),
                    style = Stroke(1.dp.toPx()),
                    alpha = 0.2f * (1f - progress),
                )
            }

            drawContext.canvas.restore()
        }

        // Center-flash labels — glowing white text in the middle of the rings
        val labelStyle = TextStyle(
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = dmSansBold,
            letterSpacing = 4.sp,
        )

        for (i in modules.indices) {
            val alpha = labelAlphas[i].value
            if (alpha > 0f) {
                val mod = modules[i]
                val layout = textMeasurer.measure(mod.label, labelStyle)
                val labelX = cx - layout.size.width / 2f
                val labelY = cy - layout.size.height / 2f

                // Outer glow halo
                drawText(
                    textLayoutResult = layout,
                    color = Color.White,
                    alpha = alpha * 0.15f,
                    topLeft = Offset(labelX, labelY),
                )
                // Main white text
                drawText(
                    textLayoutResult = layout,
                    color = Color.White,
                    alpha = alpha * 0.9f,
                    topLeft = Offset(labelX, labelY),
                )
            }
        }

        drawContext.canvas.restore()

        // ── Structural web lines (8 at 45° intervals) ─────────────────────
        if (webLinesProgress.value > 0f) {
            val webLen = diagonal * webLinesProgress.value
            val webStroke = 0.5f.dp.toPx()

            for (i in modules.indices) {
                val rad = Math.toRadians(i * 45.0)
                drawLine(
                    color = modules[i].color,
                    start = Offset(cx, cy),
                    end = Offset(
                        cx + webLen * cos(rad).toFloat(),
                        cy + webLen * sin(rad).toFloat(),
                    ),
                    strokeWidth = webStroke,
                    alpha = 0.06f * ringAlpha,
                )
            }
        }

        // ── Text measurements ────────────────────────────────────────────
        val titleStyle = TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = dmSansBold,
            letterSpacing = titleLetterSpacing.value.sp,
        )
        val taglineStyle = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = dmSansMedium,
        )
        val taglineText = "grow smarter."
        val titleLayout = textMeasurer.measure("Furrow", titleStyle)
        val fullTaglineLayout = textMeasurer.measure(taglineText, taglineStyle)

        val titleFinalY = cy - titleLayout.size.height / 2f
        val taglineFinalY = titleFinalY + titleLayout.size.height + 8.dp.toPx()
        val dotsY = taglineFinalY + fullTaglineLayout.size.height + 16.dp.toPx()

        // ── Logo reveal line + glow ──────────────────────────────────────
        if (logoLineAlpha.value > 0f) {
            val lineLeft = cx - titleLayout.size.width / 2f
            val lineRight = lineLeft + titleLayout.size.width * logoLineProgress.value
            val lineY = titleFinalY + titleLayout.size.height / 2f

            // Glow underneath
            drawLine(
                color = textPrimary,
                start = Offset(lineLeft, lineY),
                end = Offset(lineRight, lineY),
                strokeWidth = 6.dp.toPx(),
                alpha = logoLineAlpha.value * 0.2f,
            )
            // Main line
            drawLine(
                color = textPrimary,
                start = Offset(lineLeft, lineY),
                end = Offset(lineRight, lineY),
                strokeWidth = 1.5f.dp.toPx(),
                alpha = logoLineAlpha.value,
            )
        }

        // ── Text exit transform ──────────────────────────────────────────
        drawContext.canvas.save()
        drawContext.canvas.translate(cx, cy)
        drawContext.canvas.scale(textExitScale.value, textExitScale.value)
        drawContext.canvas.translate(-cx, -cy)

        val exitAlpha = textExitAlpha.value

        // Title
        if (titleAlpha.value > 0f) {
            drawText(
                textLayoutResult = titleLayout,
                color = textPrimary,
                alpha = titleAlpha.value * exitAlpha,
                topLeft = Offset(
                    cx - titleLayout.size.width / 2f,
                    titleFinalY + titleOffsetDp.value.dp.toPx(),
                ),
            )
        }

        // Tagline typewriter
        val charCount = taglineCharCount.intValue
        if (charCount > 0) {
            val visibleText = taglineText.substring(0, charCount)
            val visibleLayout = textMeasurer.measure(visibleText, taglineStyle)
            val taglineStartX = cx - fullTaglineLayout.size.width / 2f

            drawText(
                textLayoutResult = visibleLayout,
                color = textTertiary,
                alpha = exitAlpha,
                topLeft = Offset(taglineStartX, taglineFinalY),
            )

            // Period flash
            if (charCount == taglineText.length && periodFlashAlpha.value > 0f) {
                val withoutPeriod = textMeasurer.measure(taglineText.dropLast(1), taglineStyle)
                val periodLayout = textMeasurer.measure(".", taglineStyle)
                drawText(
                    textLayoutResult = periodLayout,
                    color = gardenGlow,
                    alpha = periodFlashAlpha.value * exitAlpha,
                    topLeft = Offset(taglineStartX + withoutPeriod.size.width, taglineFinalY),
                )
            }
        }

        // Module dots — 8 dots at 14dp spacing, layered glow
        val dotSpacing = 14.dp.toPx()
        val totalDotsWidth = dotSpacing * (MODULE_COUNT - 1)
        val dotsStartX = cx - totalDotsWidth / 2f

        for (d in modules.indices) {
            val dAlpha = dotAlphas[d].value
            if (dAlpha > 0f) {
                val dotCenter = Offset(dotsStartX + d * dotSpacing, dotsY)
                for (g in glowRadii.indices) {
                    drawCircle(
                        color = modules[d].color,
                        radius = glowRadii[g].dp.toPx(),
                        center = dotCenter,
                        alpha = glowAlphas[g] * dAlpha * exitAlpha,
                    )
                }
            }
        }

        // Connecting line between dots
        if (connectLineProgress.value > 0f) {
            val lineStartX = dotsStartX
            val lineEndX = lineStartX + totalDotsWidth * connectLineProgress.value
            drawLine(
                color = textMuted,
                start = Offset(lineStartX, dotsY),
                end = Offset(lineEndX, dotsY),
                strokeWidth = 0.5f.dp.toPx(),
                alpha = exitAlpha,
            )
        }

        drawContext.canvas.restore()

        // ── Implosion screen flash ────────────────────────────────────────
        if (implosionFlashAlpha.value > 0f) {
            drawRect(Color.White, size = size, alpha = implosionFlashAlpha.value)
        }
    }
}
