package com.furrow.app.ui.transition

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.Void
import kotlin.math.sin

// ── Cubic bezier path (coordinates as fractions of screen width/height) ──

private data class FracOffset(val x: Float, val y: Float)

private data class LeafPath(
    val p0: FracOffset,
    val p1: FracOffset,
    val p2: FracOffset,
    val p3: FracOffset,
)

private fun LeafPath.evaluate(t: Float): FracOffset {
    val mt = 1f - t
    return FracOffset(
        x = mt * mt * mt * p0.x + 3f * mt * mt * t * p1.x + 3f * mt * t * t * p2.x + t * t * t * p3.x,
        y = mt * mt * mt * p0.y + 3f * mt * mt * t * p1.y + 3f * mt * t * t * p2.y + t * t * t * p3.y,
    )
}

// ── Leaf specs ──

private data class LeafSpec(
    val sizeDp: Float,
    val alpha: Float,
    val startFraction: Float,
    val path: LeafPath,
    val baseRotation: Float,
    val wobbleAmount: Float,
    val wobbleFreq: Float,
)

private val leaves = listOf(
    // Leaf 1 (large): enters middle, swoops up, dips, exits upper-right — slow gentle rotation
    LeafSpec(
        sizeDp = 20f, alpha = 0.85f, startFraction = 0.00f,
        path = LeafPath(
            p0 = FracOffset(-0.10f, 0.50f),
            p1 = FracOffset(0.25f, 0.30f),
            p2 = FracOffset(0.65f, 0.55f),
            p3 = FracOffset(1.10f, 0.35f),
        ),
        baseRotation = 180f, wobbleAmount = 25f, wobbleFreq = 3f,
    ),
    // Leaf 2 (medium): enters low, big rising arc, exits mid — moderate spin
    LeafSpec(
        sizeDp = 15f, alpha = 0.6f, startFraction = 0.07f,
        path = LeafPath(
            p0 = FracOffset(-0.10f, 0.65f),
            p1 = FracOffset(0.30f, 0.60f),
            p2 = FracOffset(0.55f, 0.35f),
            p3 = FracOffset(1.10f, 0.45f),
        ),
        baseRotation = 360f, wobbleAmount = 35f, wobbleFreq = 4f,
    ),
    // Leaf 3 (large): enters high, dips through center, exits lower-right — slow glide
    LeafSpec(
        sizeDp = 22f, alpha = 0.8f, startFraction = 0.17f,
        path = LeafPath(
            p0 = FracOffset(-0.10f, 0.35f),
            p1 = FracOffset(0.20f, 0.50f),
            p2 = FracOffset(0.70f, 0.60f),
            p3 = FracOffset(1.10f, 0.55f),
        ),
        baseRotation = 200f, wobbleAmount = 40f, wobbleFreq = 2.5f,
    ),
    // Leaf 4 (small): tight S-curve through center — fast tumbling, really caught in wind
    LeafSpec(
        sizeDp = 13f, alpha = 0.5f, startFraction = 0.10f,
        path = LeafPath(
            p0 = FracOffset(-0.10f, 0.45f),
            p1 = FracOffset(0.35f, 0.30f),
            p2 = FracOffset(0.50f, 0.65f),
            p3 = FracOffset(1.10f, 0.50f),
        ),
        baseRotation = 540f, wobbleAmount = 50f, wobbleFreq = 5f,
    ),
    // Leaf 5 (medium): enters low, lazy rise across entire screen — gentle arc
    LeafSpec(
        sizeDp = 18f, alpha = 0.7f, startFraction = 0.20f,
        path = LeafPath(
            p0 = FracOffset(-0.10f, 0.60f),
            p1 = FracOffset(0.40f, 0.55f),
            p2 = FracOffset(0.75f, 0.40f),
            p3 = FracOffset(1.10f, 0.30f),
        ),
        baseRotation = 300f, wobbleAmount = 30f, wobbleFreq = 3.5f,
    ),
)

// ── Wind wisp specs ──

private data class WispSpec(
    val path: LeafPath,
    val alpha: Float,
    val strokeDp: Float,
    val trailLength: Float, // fraction of path visible at once (0.2 = 20%)
    val startFraction: Float,
)

private val wisps = listOf(
    // Wide sweeping arc, top half
    WispSpec(
        path = LeafPath(
            p0 = FracOffset(-0.05f, 0.40f),
            p1 = FracOffset(0.30f, 0.32f),
            p2 = FracOffset(0.60f, 0.48f),
            p3 = FracOffset(1.05f, 0.38f),
        ),
        alpha = 0.12f, strokeDp = 1.5f, trailLength = 0.25f, startFraction = 0.0f,
    ),
    // Lower curve, slightly behind
    WispSpec(
        path = LeafPath(
            p0 = FracOffset(-0.05f, 0.55f),
            p1 = FracOffset(0.25f, 0.62f),
            p2 = FracOffset(0.65f, 0.42f),
            p3 = FracOffset(1.05f, 0.52f),
        ),
        alpha = 0.10f, strokeDp = 1f, trailLength = 0.20f, startFraction = 0.08f,
    ),
    // Tight subtle streak through middle
    WispSpec(
        path = LeafPath(
            p0 = FracOffset(-0.05f, 0.48f),
            p1 = FracOffset(0.40f, 0.44f),
            p2 = FracOffset(0.55f, 0.54f),
            p3 = FracOffset(1.05f, 0.46f),
        ),
        alpha = 0.08f, strokeDp = 1f, trailLength = 0.18f, startFraction = 0.12f,
    ),
)

// ── Scatter leaf system (wipe-in and wipe-out) ──

private enum class ReactionType { BOUNCE, SLIDE_DOWN, DEFLECT_UP, NONE }

private data class ScatterLeaf(
    val startYFrac: Float,   // where on the edge it originates (0=top, 1=bottom)
    val sizeDp: Float,       // leaf width in dp
    val heightRatio: Float,  // height as fraction of width (0.4-0.6)
    val rotationDeg: Float,  // total rotation during flight phase
    val yWaveDp: Float,      // Y-axis wave amplitude in dp during flight
    val delayFrac: Float,    // delay as fraction of wipe progress (0-0.25)
    val alpha: Float,        // starting alpha
    val reaction: ReactionType = ReactionType.NONE,
    val hitFrac: Float = 1f,    // fraction of leaf progress when it hits the wall
    val hitXFrac: Float = 0.92f, // X fraction of screen where it hits
)

// Fly right, hit the right edge, then bounce/slide/deflect
private val wipeInScatter = listOf(
    ScatterLeaf(0.25f, 35f, 0.50f, 270f, 20f, 0.00f,  0.6f, ReactionType.BOUNCE,      0.55f, 0.93f),
    ScatterLeaf(0.45f, 45f, 0.45f, 200f, 30f, 0.075f, 0.5f, ReactionType.SLIDE_DOWN,   0.50f, 0.92f),
    ScatterLeaf(0.65f, 30f, 0.55f, 320f, 15f, 0.16f,  0.7f, ReactionType.DEFLECT_UP,   0.50f, 0.88f),
    ScatterLeaf(0.80f, 40f, 0.48f, 250f, 25f, 0.25f,  0.55f, ReactionType.BOUNCE,      0.55f, 0.95f),
)

// Fly left, blown backward as curtain opens (no reactions — just exit left edge)
private val wipeOutScatter = listOf(
    ScatterLeaf(0.20f, 40f, 0.48f, -300f, 25f, 0.00f, 0.6f),
    ScatterLeaf(0.40f, 32f, 0.52f, -220f, 18f, 0.09f, 0.7f),
    ScatterLeaf(0.60f, 48f, 0.45f, -280f, 30f, 0.18f, 0.5f),
    ScatterLeaf(0.85f, 36f, 0.50f, -260f, 22f, 0.25f, 0.6f),
)

/** Draws a single almond-shaped scatter leaf at [cx],[cy] with given size, rotation, and alpha. */
private fun DrawScope.drawScatterLeaf(
    leafPath: Path,
    cx: Float,
    cy: Float,
    widthPx: Float,
    heightPx: Float,
    rotation: Float,
    alpha: Float,
) {
    if (alpha <= 0f) return
    rotate(degrees = rotation, pivot = Offset(cx, cy)) {
        val halfW = widthPx / 2f
        val halfH = heightPx / 2f

        leafPath.reset()
        leafPath.moveTo(cx - halfW, cy)
        leafPath.cubicTo(
            cx - halfW * 0.3f, cy - halfH,
            cx + halfW * 0.3f, cy - halfH,
            cx + halfW, cy,
        )
        leafPath.cubicTo(
            cx + halfW * 0.3f, cy + halfH,
            cx - halfW * 0.3f, cy + halfH,
            cx - halfW, cy,
        )
        leafPath.close()
        drawPath(leafPath, GardenGlow.copy(alpha = alpha))
    }
}

/**
 * Wipe-in scatter: leaves fly LEFT→RIGHT, then hit the right edge and react
 * (bounce & tumble down, slam & slide, or deflect upward).
 */
private fun DrawScope.drawWipeInScatter(
    leaves: List<ScatterLeaf>,
    progress: Float,
    h: Float,
) {
    val w = size.width
    val leafPath = Path()

    leaves.forEach { leaf ->
        val leafP = ((progress - leaf.delayFrac) / (1f - leaf.delayFrac)).coerceIn(0f, 1f)
        if (leafP <= 0f) return@forEach

        val widthPx = leaf.sizeDp.dp.toPx()
        val heightPx = widthPx * leaf.heightRatio
        val wavePx = leaf.yWaveDp.dp.toPx()

        val startX = w * 0.08f
        val hitX = w * leaf.hitXFrac
        val startY = h * leaf.startYFrac

        val cx: Float
        val cy: Float
        val rotation: Float
        val currentAlpha: Float

        if (leafP < leaf.hitFrac) {
            // ── FLIGHT PHASE: gentle arc from left toward the wall ──
            val fp = leafP / leaf.hitFrac
            cx = startX + (hitX - startX) * fp
            cy = startY + sin(fp * Math.PI).toFloat() * wavePx
            rotation = leaf.rotationDeg * fp
            currentAlpha = leaf.alpha * if (fp < 0.15f) fp / 0.15f else 1f
        } else {
            // ── REACTION PHASE: floaty physics after hitting the right edge ──
            val rp = (leafP - leaf.hitFrac) / (1f - leaf.hitFrac)
            val hitRotation = leaf.rotationDeg

            // Alpha stays strong for 70% of reaction, fades only in last 30%
            val alphaFade = if (rp < 0.7f) 1f else (1f - (rp - 0.7f) / 0.3f)

            when (leaf.reaction) {
                ReactionType.BOUNCE -> {
                    val pause = 0.15f // brief stun after impact
                    if (rp < pause) {
                        // Stunned — barely moves, selling the moment of contact
                        val pp = rp / pause
                        cx = hitX - 3.dp.toPx() * pp
                        cy = startY + 2.dp.toPx() * pp
                        rotation = hitRotation + pp * 5f
                    } else {
                        val fallP = ((rp - pause) / (1f - pause)).coerceIn(0f, 1f)

                        // Small bounce-back that decelerates smoothly
                        val bounceBack = w * 0.06f
                        val bounceCurve = if (fallP < 0.25f) {
                            sin(fallP / 0.25f * Math.PI).toFloat()
                        } else {
                            0f
                        }

                        // Terminal velocity fall — starts slow, caps gently (NOT quadratic)
                        val maxFall = h * 0.35f
                        val easedFall = sin(fallP * Math.PI / 2.0).toFloat()

                        // Pendulum sway — left-right oscillation, dampened over time
                        val swayPx = 15.dp.toPx()
                        val swayPhase = fallP * 2.5 * 2.0 * Math.PI
                        val sway = sin(swayPhase).toFloat() * swayPx * (1f - fallP * 0.5f)

                        // Flutter — rotation rocks back and forth, synced with sway
                        val flutter = sin(swayPhase * 1.1).toFloat() * 45f * (1f - fallP * 0.3f)

                        cx = hitX - bounceBack * bounceCurve + sway
                        cy = startY + 2.dp.toPx() + easedFall * maxFall
                        rotation = hitRotation + flutter
                    }
                    currentAlpha = leaf.alpha * alphaFade
                }
                ReactionType.SLIDE_DOWN -> {
                    val pause = 0.12f // flatten against wall moment
                    if (rp < pause) {
                        // Impact — sticks to wall, barely moves
                        val pp = rp / pause
                        cx = hitX
                        cy = startY + 1.dp.toPx() * pp
                        rotation = hitRotation + pp * 3f
                    } else {
                        val slideP = ((rp - pause) / (1f - pause)).coerceIn(0f, 1f)

                        // Slow slide with terminal velocity curve
                        val maxSlide = h * 0.25f
                        val easedSlide = sin(slideP * Math.PI / 2.0).toFloat()

                        // Gentle wobble against the wall
                        val wobblePx = 4.dp.toPx()
                        val wobblePhase = slideP * 3.0 * 2.0 * Math.PI
                        val wobble = sin(wobblePhase).toFloat() * wobblePx * (1f - slideP * 0.4f)

                        // Flutter rotation while sliding
                        val flutter = sin(wobblePhase * 1.1).toFloat() * 25f * (1f - slideP * 0.3f)

                        cx = hitX + wobble
                        cy = startY + 1.dp.toPx() + easedSlide * maxSlide
                        rotation = hitRotation + flutter
                    }
                    currentAlpha = leaf.alpha * alphaFade
                }
                ReactionType.DEFLECT_UP -> {
                    val arcFrac = 0.30f // first 30% = upward deflection arc
                    if (rp < arcFrac) {
                        // Upward arc from glancing blow
                        val ap = rp / arcFrac
                        val arcUpPx = 60.dp.toPx()
                        cx = hitX - ap * w * 0.08f
                        cy = startY - sin(ap * Math.PI).toFloat() * arcUpPx
                        rotation = hitRotation - ap * 120f
                    } else {
                        // Sway + flutter fall after arc peaks
                        val fallP = ((rp - arcFrac) / (1f - arcFrac)).coerceIn(0f, 1f)
                        val arcEndX = hitX - w * 0.08f
                        val arcEndRot = hitRotation - 120f

                        // Terminal velocity fall
                        val maxFall = h * 0.30f
                        val easedFall = sin(fallP * Math.PI / 2.0).toFloat()

                        // Pendulum sway
                        val swayPx = 12.dp.toPx()
                        val swayPhase = fallP * 2.0 * 2.0 * Math.PI
                        val sway = sin(swayPhase).toFloat() * swayPx * (1f - fallP * 0.5f)

                        // Flutter synced with sway
                        val flutter = sin(swayPhase * 1.1).toFloat() * 40f * (1f - fallP * 0.3f)

                        cx = arcEndX - fallP * w * 0.05f + sway
                        cy = startY + easedFall * maxFall
                        rotation = arcEndRot + flutter
                    }
                    currentAlpha = leaf.alpha * alphaFade
                }
                ReactionType.NONE -> {
                    cx = hitX; cy = startY; rotation = hitRotation; currentAlpha = 0f
                }
            }
        }

        drawScatterLeaf(leafPath, cx, cy, widthPx, heightPx, rotation, currentAlpha)
    }
}

/**
 * Wipe-out scatter: leaves fly RIGHT→LEFT from the trailing edge and exit the screen.
 * No reactions — they just fly off and fade.
 */
private fun DrawScope.drawWipeOutScatter(
    leaves: List<ScatterLeaf>,
    progress: Float,
    edgeX: Float,
    h: Float,
) {
    val travelPx = size.width * 0.15f
    val leafPath = Path()

    leaves.forEach { leaf ->
        val leafP = ((progress - leaf.delayFrac) / (1f - leaf.delayFrac)).coerceIn(0f, 1f)
        if (leafP <= 0f) return@forEach

        val widthPx = leaf.sizeDp.dp.toPx()
        val heightPx = widthPx * leaf.heightRatio
        val wavePx = leaf.yWaveDp.dp.toPx()

        val cx = edgeX - leafP * travelPx
        val cy = h * leaf.startYFrac + sin(leafP * Math.PI * 2).toFloat() * wavePx
        val rotation = leaf.rotationDeg * leafP

        val alphaFade = when {
            leafP < 0.1f -> leafP / 0.1f
            leafP > 0.7f -> (1f - leafP) / 0.3f
            else -> 1f
        }

        drawScatterLeaf(leafPath, cx, cy, widthPx, heightPx, rotation, leaf.alpha * alphaFade)
    }
}

// ── Transition composable ──

@Composable
fun FurrowTransition(controller: TransitionController) {
    var phase by remember { mutableIntStateOf(0) }
    val wipeProgress = remember { Animatable(0f) }
    val leafProgress = remember { Animatable(0f) }

    LaunchedEffect(controller.isTransitioning) {
        if (!controller.isTransitioning) return@LaunchedEffect

        // Phase 1: Black wipe in (left to right)
        phase = 1
        wipeProgress.snapTo(0f)
        wipeProgress.animateTo(1f, tween(800, easing = FastOutSlowInEasing))

        // Screen is fully black — safe to swap screens
        controller.onWipeInComplete()

        // Phase 2: Leaves + watermark on black
        phase = 2
        leafProgress.snapTo(0f)
        leafProgress.animateTo(1f, tween(2344))

        // Phase 3: Black wipe out (left to right)
        phase = 3
        wipeProgress.snapTo(0f)
        wipeProgress.animateTo(1f, tween(800, easing = FastOutSlowInEasing))

        // Done
        phase = 0
        controller.onTransitionComplete()
    }

    if (phase == 0) return

    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Layer 1: Black wipe / full black
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (phase) {
                1 -> {
                    // Black curtain grows from left with soft gradient leading edge
                    val fadeWidthPx = with(density) { 80.dp.toPx() }
                    val edgeX = (w + fadeWidthPx) * wipeProgress.value

                    // Solid black: left edge to start of gradient
                    val solidEnd = (edgeX - fadeWidthPx).coerceAtLeast(0f)
                    if (solidEnd > 0f) {
                        drawRect(color = Void, topLeft = Offset.Zero, size = Size(solidEnd, h))
                    }

                    // Gradient fade: solid black → transparent
                    val gradEnd = edgeX.coerceAtMost(w)
                    if (gradEnd > solidEnd) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Void, Color.Transparent),
                                startX = solidEnd,
                                endX = gradEnd,
                            ),
                            topLeft = Offset(solidEnd, 0f),
                            size = Size(gradEnd - solidEnd, h),
                        )
                    }

                    // Scatter leaves fly right, hit the right edge, then react
                    drawWipeInScatter(wipeInScatter, wipeProgress.value, h)
                }
                2 -> {
                    // Full black background
                    drawRect(color = Void, size = size)
                }
                3 -> {
                    // Black curtain reveals from left with soft gradient trailing edge
                    val fadeWidthPx = with(density) { 80.dp.toPx() }
                    val edgeX = (w + fadeWidthPx) * wipeProgress.value - fadeWidthPx

                    // Gradient fade: transparent → solid black
                    val gradStart = edgeX.coerceAtLeast(0f)
                    val gradEnd = (edgeX + fadeWidthPx).coerceAtMost(w)
                    if (gradEnd > gradStart) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Void),
                                startX = gradStart,
                                endX = gradEnd,
                            ),
                            topLeft = Offset(gradStart, 0f),
                            size = Size(gradEnd - gradStart, h),
                        )
                    }

                    // Solid black: end of gradient to right edge
                    val solidStart = (edgeX + fadeWidthPx).coerceIn(0f, w)
                    if (solidStart < w) {
                        drawRect(
                            color = Void,
                            topLeft = Offset(solidStart, 0f),
                            size = Size(w - solidStart, h),
                        )
                    }

                    // Scatter leaves fly left, blown backward (no reactions)
                    drawWipeOutScatter(wipeOutScatter, wipeProgress.value, edgeX, h)
                }
            }
        }

        // Layer 2: "Furrow" watermark (Phase 2 only)
        if (phase == 2) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Furrow",
                    style = MaterialTheme.typography.displaySmall,
                    color = GardenGlow.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Layer 3: Wind wisps + leaf animation (Phase 2 only)
        if (phase == 2) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val overallProgress = leafProgress.value

                // ── Wind wisps ──
                wisps.forEach { wisp ->
                    val wp = ((overallProgress - wisp.startFraction) / (1f - wisp.startFraction))
                        .coerceIn(0f, 1f)
                    if (wp <= 0f) return@forEach

                    val strokePx = with(density) { wisp.strokeDp.dp.toPx() }
                    val headT = wp
                    val tailT = (wp - wisp.trailLength).coerceAtLeast(0f)

                    // Fade in at start, fade out at end
                    val wispFade = when {
                        wp < 0.1f -> wp / 0.1f
                        wp > 0.9f -> (1f - wp) / 0.1f
                        else -> 1f
                    }
                    val wispColor = GardenGlow.copy(alpha = wisp.alpha * wispFade)

                    // Draw wisp as a series of line segments along the bezier
                    val steps = 12
                    val wispPath = Path()
                    var started = false
                    for (s in 0..steps) {
                        val t = tailT + (headT - tailT) * (s.toFloat() / steps)
                        val pt = wisp.path.evaluate(t)
                        val sx = pt.x * w
                        val sy = pt.y * h
                        if (!started) {
                            wispPath.moveTo(sx, sy)
                            started = true
                        } else {
                            wispPath.lineTo(sx, sy)
                        }
                    }
                    drawPath(wispPath, wispColor, style = Stroke(width = strokePx, cap = StrokeCap.Round))
                }

                // ── Leaves ──
                leaves.forEachIndexed { i, spec ->
                    val p = ((overallProgress - spec.startFraction) / (1f - spec.startFraction))
                        .coerceIn(0f, 1f)
                    if (p <= 0f) return@forEachIndexed

                    val leafSizePx = with(density) { spec.sizeDp.dp.toPx() }

                    // Position from cubic bezier path
                    val pos = spec.path.evaluate(p)
                    val x = pos.x * w
                    val y = pos.y * h

                    // Rotation: base spin + wobble
                    val rotation = spec.baseRotation * p +
                        spec.wobbleAmount * sin(p * spec.wobbleFreq * Math.PI).toFloat()

                    // Edge fade
                    val edgeFade = when {
                        p < 0.12f -> p / 0.12f
                        p > 0.88f -> (1f - p) / 0.12f
                        else -> 1f
                    }
                    val alpha = spec.alpha * edgeFade

                    drawLeaf(x, y, leafSizePx, rotation, GardenGlow.copy(alpha = alpha))
                }
            }
        }
    }
}

private fun DrawScope.drawLeaf(cx: Float, cy: Float, size: Float, rotation: Float, color: Color) {
    rotate(degrees = rotation, pivot = Offset(cx, cy)) {
        val path = Path().apply {
            moveTo(cx - size / 2, cy)
            quadraticBezierTo(cx, cy - size / 2.5f, cx + size / 2, cy)
            quadraticBezierTo(cx, cy + size / 2.5f, cx - size / 2, cy)
            close()
        }
        drawPath(path, color)

        drawLine(
            color = color.copy(alpha = color.alpha * 0.7f),
            start = Offset(cx + size / 2, cy),
            end = Offset(cx + size / 2 + size * 0.2f, cy - size * 0.1f),
            strokeWidth = size * 0.06f,
        )
    }
}
