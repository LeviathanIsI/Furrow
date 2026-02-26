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

// ── Leaf-shaped wipe edge ──

private val leafBumpSizes = listOf(0.7f, 1.0f, 0.5f, 0.9f, 0.6f, 1.0f, 0.4f, 0.8f, 0.7f, 0.95f, 0.55f, 0.85f)

/**
 * Builds a closed Path whose one edge is a series of leaf-shaped silhouette bumps.
 * [direction] = +1 → bumps extend RIGHT of [baseX] (wipe-in leading edge).
 * [direction] = -1 → bumps extend LEFT  of [baseX] (wipe-out trailing edge).
 * The path fills everything on the solid-black side of the edge.
 */
private fun buildLeafEdgePath(
    baseX: Float,
    screenHeight: Float,
    screenWidth: Float,
    direction: Int,
    bumpDepth: Float,
): Path {
    val path = Path()
    val leafCount = 12
    val segmentHeight = screenHeight / leafCount

    // Start corner on the solid-black side
    if (direction > 0) {
        path.moveTo(0f, 0f)
        path.lineTo(baseX, 0f)
    } else {
        path.moveTo(screenWidth, 0f)
        path.lineTo(baseX, 0f)
    }

    // Draw leaf bumps down the edge
    for (i in 0 until leafCount) {
        val y1 = i * segmentHeight
        val yMid = y1 + segmentHeight * 0.5f
        val y2 = y1 + segmentHeight
        val bumpSize = bumpDepth * (0.5f + 0.5f * leafBumpSizes[i % leafBumpSizes.size])
        val tipX = baseX + bumpSize * direction

        // Upper half: gentle curve to pointed tip
        path.cubicTo(
            baseX + bumpSize * 0.3f * direction, y1 + segmentHeight * 0.1f,
            tipX, y1 + segmentHeight * 0.3f,
            tipX, yMid,
        )
        // Lower half: pointed tip back to base
        path.cubicTo(
            tipX, y2 - segmentHeight * 0.3f,
            baseX + bumpSize * 0.3f * direction, y2 - segmentHeight * 0.1f,
            baseX, y2,
        )
    }

    // Close back to the starting corner
    if (direction > 0) {
        path.lineTo(0f, screenHeight)
        path.close()
    } else {
        path.lineTo(screenWidth, screenHeight)
        path.close()
    }

    return path
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
                    // Black grows from left with leaf-silhouette leading edge
                    val bumpDepth = with(density) { 50.dp.toPx() }
                    val fadeZone = with(density) { 25.dp.toPx() }
                    // Overshoot: at progress=1.0, baseX=w → bumps past screen → fully covered
                    val baseX = (w + bumpDepth) * wipeProgress.value - bumpDepth

                    // Shadow gradient beyond the leaf tips
                    val shadowStart = (baseX + bumpDepth).coerceIn(0f, w)
                    val shadowEnd = (shadowStart + fadeZone).coerceAtMost(w)
                    if (shadowEnd > shadowStart) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Void.copy(alpha = 0.5f), Color.Transparent),
                                startX = shadowStart,
                                endX = shadowEnd,
                            ),
                            topLeft = Offset(shadowStart, 0f),
                            size = Size(shadowEnd - shadowStart, h),
                        )
                    }

                    // Solid black with leafy right edge
                    val leafPath = buildLeafEdgePath(baseX, h, w, +1, bumpDepth)
                    drawPath(path = leafPath, color = Void)
                }
                2 -> {
                    // Full black background
                    drawRect(color = Void, size = size)
                }
                3 -> {
                    // Black shrinks — trailing edge moves right with leaf-silhouette edge
                    val bumpDepth = with(density) { 50.dp.toPx() }
                    val fadeZone = with(density) { 25.dp.toPx() }
                    // Overshoot: at progress=1.0, baseX past w+bumpDepth → fully revealed
                    val baseX = (w + 2 * bumpDepth) * wipeProgress.value

                    // Shadow gradient extending LEFT of the leaf tips
                    val shadowEnd = (baseX - bumpDepth).coerceIn(0f, w)
                    val shadowStart = (shadowEnd - fadeZone).coerceAtLeast(0f)
                    if (shadowEnd > shadowStart) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Void.copy(alpha = 0.5f)),
                                startX = shadowStart,
                                endX = shadowEnd,
                            ),
                            topLeft = Offset(shadowStart, 0f),
                            size = Size(shadowEnd - shadowStart, h),
                        )
                    }

                    // Solid black with leafy left edge
                    val leafPath = buildLeafEdgePath(baseX, h, w, -1, bumpDepth)
                    drawPath(path = leafPath, color = Void)
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
