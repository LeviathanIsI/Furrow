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

    // ── Ring sweeps ──────────────────────────────────────────────────────
    val outerSweep = remember { Animatable(0f) }
    val middleSweep = remember { Animatable(0f) }
    val innerSweep = remember { Animatable(0f) }
    val ringsAlpha = remember { Animatable(1f) }
    val outerLeadAlpha = remember { Animatable(0.8f) }
    val middleLeadAlpha = remember { Animatable(0.8f) }
    val innerLeadAlpha = remember { Animatable(0.8f) }
    val ringScale = remember { Animatable(1f) }

    // ── Ring flash + shockwave + burst ───────────────────────────────────
    val outerFlashAlpha = remember { Animatable(0f) }
    val middleFlashAlpha = remember { Animatable(0f) }
    val innerFlashAlpha = remember { Animatable(0f) }
    val outerShockProgress = remember { Animatable(0f) }
    val middleShockProgress = remember { Animatable(0f) }
    val innerShockProgress = remember { Animatable(0f) }
    val outerBurstProgress = remember { Animatable(0f) }
    val middleBurstProgress = remember { Animatable(0f) }
    val innerBurstProgress = remember { Animatable(0f) }

    // ── Data readout labels ──────────────────────────────────────────────
    val outerLabelAlpha = remember { Animatable(0f) }
    val middleLabelAlpha = remember { Animatable(0f) }
    val innerLabelAlpha = remember { Animatable(0f) }

    // ── Structural web lines ─────────────────────────────────────────────
    val webLinesProgress = remember { Animatable(0f) }

    // ── Logo reveal line ─────────────────────────────────────────────────
    val logoLineProgress = remember { Animatable(0f) }
    val logoLineAlpha = remember { Animatable(0f) }

    // ── Title ────────────────────────────────────────────────────────────
    val titleAlpha = remember { Animatable(0f) }
    val titleOffsetDp = remember { Animatable(20f) }
    val titleLetterSpacing = remember { Animatable(8f) }

    // ── 6. Bass-drop canvas scale ────────────────────────────────────────
    val canvasScale = remember { Animatable(1f) }

    // ── Tagline typewriter ───────────────────────────────────────────────
    val taglineCharCount = remember { mutableIntStateOf(0) }
    val periodFlashAlpha = remember { Animatable(0f) }

    // ── Module dots ──────────────────────────────────────────────────────
    val dot1Alpha = remember { Animatable(0f) }
    val dot2Alpha = remember { Animatable(0f) }
    val dot3Alpha = remember { Animatable(0f) }
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

        // ── Phase 1: Garden ring (0–1500ms) ──────────────────────────
        launch { outerSweep.animateTo(360f, tween(1000, easing = EaseInOutCubic)) }
        launch { delay(1000); outerLeadAlpha.animateTo(0.3f, tween(200)) }
        launch { delay(1000); outerFlashAlpha.snapTo(0.5f); outerFlashAlpha.animateTo(0f, tween(150)) }
        launch { delay(1000); outerShockProgress.animateTo(1f, tween(200)) }
        launch { delay(1000); outerBurstProgress.animateTo(1f, tween(300)) }
        launch { delay(1000); outerLabelAlpha.animateTo(0.7f, tween(100)) }

        // ── Phase 2: Apiary ring (1500–3000ms) ───────────────────────
        launch { delay(1500); outerLabelAlpha.animateTo(0.35f, tween(200)) }
        launch { delay(1500); middleSweep.animateTo(360f, tween(1000, easing = EaseInOutCubic)) }
        launch { delay(2500); middleLeadAlpha.animateTo(0.3f, tween(200)) }
        launch { delay(2500); middleFlashAlpha.snapTo(0.5f); middleFlashAlpha.animateTo(0f, tween(150)) }
        launch { delay(2500); middleShockProgress.animateTo(1f, tween(200)) }
        launch { delay(2500); middleBurstProgress.animateTo(1f, tween(300)) }
        launch { delay(2500); middleLabelAlpha.animateTo(0.7f, tween(100)) }

        // ── Phase 3: Flock ring (3000–4500ms) ────────────────────────
        launch { delay(3000); middleLabelAlpha.animateTo(0.35f, tween(200)) }
        launch { delay(3000); outerLabelAlpha.animateTo(0.2f, tween(200)) }
        launch { delay(3000); innerSweep.animateTo(360f, tween(900, easing = EaseInOutCubic)) }
        launch { delay(3900); innerLeadAlpha.animateTo(0.3f, tween(200)) }
        launch { delay(3900); innerFlashAlpha.snapTo(0.5f); innerFlashAlpha.animateTo(0f, tween(150)) }
        launch { delay(3900); innerShockProgress.animateTo(1f, tween(200)) }
        launch { delay(3900); innerBurstProgress.animateTo(1f, tween(300)) }
        launch { delay(3900); innerLabelAlpha.animateTo(0.7f, tween(100)) }

        // "All systems go" — all labels flash bright then fade out
        launch { delay(4300); outerLabelAlpha.animateTo(0.7f, tween(150)); outerLabelAlpha.animateTo(0f, tween(200)) }
        launch { delay(4300); middleLabelAlpha.animateTo(0.7f, tween(150)); middleLabelAlpha.animateTo(0f, tween(200)) }
        launch { delay(4300); innerLabelAlpha.animateTo(0.7f, tween(150)); innerLabelAlpha.animateTo(0f, tween(200)) }

        // ── Phase 4: Logo reveal (4300–5500ms) ───────────────────────
        // Web lines draw outward
        launch { delay(4300); webLinesProgress.animateTo(1f, tween(400, easing = EaseOutCubic)) }

        // Ring pulse
        launch {
            delay(4500)
            ringScale.animateTo(1.03f, tween(150, easing = EaseInOutCubic))
            ringScale.animateTo(1f, tween(150, easing = EaseInOutCubic))
        }

        // Grid out + rings dim
        launch { delay(4500); gridAlpha.animateTo(0f, tween(400)) }
        launch { delay(4500); ringsAlpha.animateTo(0.15f, tween(500, easing = EaseOutCubic)) }

        // Logo reveal line (wipe → flash → fade)
        launch {
            delay(4500)
            logoLineAlpha.snapTo(0.5f)
            logoLineProgress.animateTo(1f, tween(300, easing = EaseOutCubic))
            logoLineAlpha.snapTo(0.8f)
            delay(16)
            logoLineAlpha.animateTo(0f, tween(100))
        }

        // Title
        launch {
            delay(4600)
            launch { titleAlpha.animateTo(1f, tween(500, easing = EaseOutCubic)) }
            launch { titleOffsetDp.animateTo(0f, tween(500, easing = EaseOutCubic)) }
            launch { titleLetterSpacing.animateTo(4f, tween(500, easing = EaseOutCubic)) }
        }

        // Bass-drop scale punch when title reaches full opacity
        launch {
            delay(5100)
            canvasScale.animateTo(1.025f, tween(75, easing = EaseOutCubic))
            canvasScale.animateTo(1f, tween(75, easing = EaseInCubic))
        }

        // Tagline typewriter
        launch {
            delay(4900)
            val text = "grow smarter."
            for (i in 1..text.length) {
                taglineCharCount.intValue = i
                delay(40)
            }
            periodFlashAlpha.snapTo(1f)
            delay(200)
            periodFlashAlpha.animateTo(0f, tween(100))
        }

        // Module dots
        launch { delay(5200); dot1Alpha.animateTo(1f, tween(200, easing = EaseOutCubic)) }
        launch { delay(5300); dot2Alpha.animateTo(1f, tween(200, easing = EaseOutCubic)) }
        launch { delay(5400); dot3Alpha.animateTo(1f, tween(200, easing = EaseOutCubic)) }
        launch { delay(5400); connectLineProgress.animateTo(1f, tween(200, easing = EaseOutCubic)) }

        // ── Phase 5: Exit (5900–6400ms) ──────────────────────────────
        launch {
            delay(5900)
            launch { ringScale.animateTo(0f, tween(250, easing = EaseInCubic)) }
            launch { webLinesProgress.animateTo(0f, tween(250, easing = EaseInCubic)) }
        }
        launch { delay(5950); implosionFlashAlpha.snapTo(0.03f); delay(100); implosionFlashAlpha.snapTo(0f) }
        launch {
            delay(6100)
            launch { textExitAlpha.animateTo(0f, tween(300)) }
            launch { textExitScale.animateTo(1.1f, tween(300)) }
        }

        delay(6400)
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
        val poultryGlow = Color(0xFFE8956E)
        val textPrimary = Color(0xFFEAEAEA)
        val textTertiary = Color(0xFF505050)
        val textMuted = Color(0xFF333333)

        val cx = size.width / 2f
        val cy = size.height / 2f
        val diagonal = sqrt(size.width * size.width + size.height * size.height)

        // ── Background ───────────────────────────────────────────────────
        drawRect(color = bg, size = size)

        // 5. Enhanced breathing — primary gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(gardenGlow.copy(alpha = 0.04f), Color.Transparent),
                center = Offset(cx, cy),
                radius = diagonal * breathScale,
            ),
            radius = diagonal,
            center = Offset(cx, cy),
        )
        // 5. Secondary gradient — offset amber glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(beeGlow.copy(alpha = 0.015f), Color.Transparent),
                center = Offset(cx + 50.dp.toPx(), cy - 30.dp.toPx()),
                radius = diagonal * breathScale,
            ),
            radius = diagonal,
            center = Offset(cx, cy),
        )

        // 2. Grid
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

        // ── Ring constants ───────────────────────────────────────────────
        val outerR = 120.dp.toPx()
        val middleR = 80.dp.toPx()
        val innerR = 40.dp.toPx()
        val outerStroke = 3.dp.toPx()
        val middleStroke = 2.5f.dp.toPx()
        val innerStroke = 2.dp.toPx()
        val glowStroke = 8.dp.toPx()
        val ringAlpha = ringsAlpha.value

        // Glow layer specs: 12 layers for smooth point-light falloff
        val glowRadii = floatArrayOf(32f, 28f, 24f, 21f, 18f, 15f, 12f, 9f, 7f, 5f, 3f, 1.5f)
        val glowAlphas = floatArrayOf(0.01f, 0.015f, 0.02f, 0.03f, 0.045f, 0.06f, 0.08f, 0.12f, 0.18f, 0.28f, 0.45f, 0.85f)
        // Trail glow: 60% radii, 50% alphas
        val trailGlowRadii = floatArrayOf(19.2f, 16.8f, 14.4f, 12.6f, 10.8f, 9f, 7.2f, 5.4f, 4.2f, 3f, 1.8f, 0.9f)
        val trailGlowAlphas = floatArrayOf(0.005f, 0.0075f, 0.01f, 0.015f, 0.0225f, 0.03f, 0.04f, 0.06f, 0.09f, 0.14f, 0.225f, 0.425f)
        val burstDotR = 1.5f.dp.toPx()
        val burstDist = 30.dp.toPx()

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
                    style = Stroke(12.dp.toPx(), cap = StrokeCap.Round),
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
                    style = Stroke(18.dp.toPx(), cap = StrokeCap.Round),
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

            // 1. Hot spot — 60° at 2x stroke, 0.25f alpha
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

            // 4. Flash overlay — 3x stroke, fading over 150ms
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

            // Comet trail — 5 trailing glow dots (50% scale of main glow)
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

            // Leading edge glow — 7 concentric circles for point-light falloff
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

            // 4. Burst particles — 8 radial dots from completion point
            if (burstProgress > 0f && burstProgress < 1f) {
                val dist = burstDist * burstProgress
                val bAlpha = 0.6f * (1f - burstProgress) * ringAlpha
                val origin = Offset(cx, cy - radius)
                for (i in 0 until 8) {
                    val angle = Math.toRadians(i * 45.0)
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

        drawRing(outerR, outerSweep.value, gardenGlow, outerLeadAlpha.value, outerFlashAlpha.value, outerStroke, outerBurstProgress.value)
        drawRing(middleR, middleSweep.value, beeGlow, middleLeadAlpha.value, middleFlashAlpha.value, middleStroke, middleBurstProgress.value)
        drawRing(innerR, innerSweep.value, poultryGlow, innerLeadAlpha.value, innerFlashAlpha.value, innerStroke, innerBurstProgress.value)

        // Shockwaves
        fun drawShockwave(radius: Float, color: Color, progress: Float) {
            if (progress <= 0f || progress >= 1f) return
            drawCircle(
                color = color,
                radius = 20.dp.toPx() * progress,
                center = Offset(cx, cy - radius),
                style = Stroke(1.dp.toPx()),
                alpha = 0.2f * (1f - progress),
            )
        }

        drawShockwave(outerR, gardenGlow, outerShockProgress.value)
        drawShockwave(middleR, beeGlow, middleShockProgress.value)
        drawShockwave(innerR, poultryGlow, innerShockProgress.value)

        // Data readout labels
        val labelStyle = TextStyle(
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = dmSansMedium,
            letterSpacing = 3.sp,
        )

        fun drawLabel(text: String, color: Color, radius: Float, alpha: Float) {
            if (alpha <= 0f) return
            val layout = textMeasurer.measure(text, labelStyle)
            drawText(
                textLayoutResult = layout,
                color = color,
                alpha = alpha,
                topLeft = Offset(
                    cx - layout.size.width / 2f,
                    cy - radius - 12.dp.toPx() - layout.size.height,
                ),
            )
        }

        drawLabel("GARDEN", gardenGlow, outerR, outerLabelAlpha.value)
        drawLabel("APIARY", beeGlow, middleR, middleLabelAlpha.value)
        drawLabel("FLOCK", poultryGlow, innerR, innerLabelAlpha.value)

        drawContext.canvas.restore()

        // ── Structural web lines ─────────────────────────────────────────
        if (webLinesProgress.value > 0f) {
            val webLen = diagonal * webLinesProgress.value
            val webStroke = 0.5f.dp.toPx()
            val webAngles = doubleArrayOf(0.0, 120.0, 240.0)
            val webColors = arrayOf(gardenGlow, beeGlow, poultryGlow)

            for (i in webAngles.indices) {
                val rad = Math.toRadians(webAngles[i])
                drawLine(
                    color = webColors[i],
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

        // ── 7. Enhanced logo reveal line + glow ──────────────────────────
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

            // 8. Period flash — full GardenGlow for 200ms, settle to TextTertiary
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

        // Module dots — layered glow
        val dotSpacing = 20.dp.toPx()
        val dotColors = arrayOf(gardenGlow, beeGlow, poultryGlow)
        val dotAlphas = floatArrayOf(dot1Alpha.value, dot2Alpha.value, dot3Alpha.value)
        val dotCenters = arrayOf(
            Offset(cx - dotSpacing, dotsY),
            Offset(cx, dotsY),
            Offset(cx + dotSpacing, dotsY),
        )

        for (d in 0 until 3) {
            if (dotAlphas[d] > 0f) {
                for (g in glowRadii.indices) {
                    drawCircle(
                        color = dotColors[d],
                        radius = glowRadii[g].dp.toPx(),
                        center = dotCenters[d],
                        alpha = glowAlphas[g] * dotAlphas[d] * exitAlpha,
                    )
                }
            }
        }

        // Connecting line between dots
        if (connectLineProgress.value > 0f) {
            val lineStartX = cx - dotSpacing
            val lineEndX = lineStartX + (dotSpacing * 2f) * connectLineProgress.value
            drawLine(
                color = textMuted,
                start = Offset(lineStartX, dotsY),
                end = Offset(lineEndX, dotsY),
                strokeWidth = 0.5f.dp.toPx(),
                alpha = exitAlpha,
            )
        }

        drawContext.canvas.restore()

        // ── 9. Implosion screen flash ────────────────────────────────────
        if (implosionFlashAlpha.value > 0f) {
            drawRect(Color.White, size = size, alpha = implosionFlashAlpha.value)
        }
    }
}
