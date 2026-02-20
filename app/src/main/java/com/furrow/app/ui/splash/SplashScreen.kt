package com.furrow.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furrow.app.ui.theme.BeeGlow
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.PoultryGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun quadBezier(t: Float, p0: Offset, p1: Offset, p2: Offset): Offset {
    val u = 1f - t
    return Offset(
        u * u * p0.x + 2f * u * t * p1.x + t * t * p2.x,
        u * u * p0.y + 2f * u * t * p1.y + t * t * p2.y,
    )
}

private fun DrawScope.drawLeaf(
    cx: Float, cy: Float, length: Float, maxW: Float, rotDeg: Float, color: Color,
) {
    val path = Path().apply {
        moveTo(cx, cy)
        cubicTo(cx - maxW * 0.5f, cy - length * 0.3f, cx - maxW * 0.3f, cy - length * 0.9f, cx, cy - length)
        cubicTo(cx + maxW * 0.3f, cy - length * 0.9f, cx + maxW * 0.5f, cy - length * 0.3f, cx, cy)
        close()
    }
    rotate(degrees = rotDeg, pivot = Offset(cx, cy)) {
        drawPath(path, color)
    }
}

private fun DrawScope.drawBee(center: Offset, wingAng: Float, alpha: Float) {
    val dp1 = 1.dp.toPx()
    val bw = 8f * dp1
    val bh = 5f * dp1
    val ww = 5f * dp1
    val wh = 3f * dp1

    // Body
    drawOval(BeeGlow, Offset(center.x - bw / 2, center.y - bh / 2), Size(bw, bh), alpha = alpha)

    // Stripes
    val s1 = center.y - bh * 0.15f
    val s2 = center.y + bh * 0.15f
    drawLine(TextTertiary, Offset(center.x - bw * 0.3f, s1), Offset(center.x + bw * 0.3f, s1), dp1, alpha = alpha)
    drawLine(TextTertiary, Offset(center.x - bw * 0.3f, s2), Offset(center.x + bw * 0.3f, s2), dp1, alpha = alpha)

    // Wings (vertical oscillation for buzz)
    val flap = wingAng / 15f * 2f * dp1
    drawOval(
        Color.White.copy(alpha = 0.3f),
        Offset(center.x - ww * 0.8f, center.y - bh * 0.6f - wh + flap),
        Size(ww, wh), alpha = alpha,
    )
    drawOval(
        Color.White.copy(alpha = 0.3f),
        Offset(center.x + bw * 0.1f, center.y - bh * 0.6f - wh - flap),
        Size(ww, wh), alpha = alpha,
    )
}

private data class ShellFrag(val dx: Float, val dy: Float, val vx: Float, val vy: Float, val rot: Float)

private val fragments = listOf(
    ShellFrag(-1f, 0f, -3f, -4f, 45f),
    ShellFrag(1f, 0f, 3f, -3f, -30f),
    ShellFrag(-0.5f, -0.5f, -2f, -5f, 60f),
    ShellFrag(0.5f, -0.3f, 4f, -2f, -45f),
)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    // Scene 1: Plant
    val soilA = remember { Animatable(0f) }
    val stemP = remember { Animatable(0f) }
    val lf1 = remember { Animatable(0f) }
    val lf2 = remember { Animatable(0f) }
    val sl1 = remember { Animatable(0f) }
    val sl2 = remember { Animatable(0f) }

    // Scene 2: Hive + Bees
    val hiveA = remember { Animatable(0f) }
    val b1t = remember { Animatable(0f) }
    val b1a = remember { Animatable(0f) }
    val b2t = remember { Animatable(0f) }
    val b2a = remember { Animatable(0f) }
    val b3t = remember { Animatable(0f) }
    val b3a = remember { Animatable(0f) }

    // Scene 3: Egg + Chick
    val eggA = remember { Animatable(0f) }
    val eggS = remember { Animatable(0.8f) }
    val crackP = remember { Animatable(0f) }
    val shellSp = remember { Animatable(0f) }
    val fragP = remember { Animatable(0f) }
    val chickA = remember { Animatable(0f) }
    val chickY = remember { Animatable(10f) }

    // Scene 4: Logo
    val elScale = remember { Animatable(1f) }
    val pSlideX = remember { Animatable(0f) }
    val hSlideX = remember { Animatable(0f) }
    val eSlideY = remember { Animatable(0f) }
    val logoA = remember { Animatable(0f) }
    val logoY = remember { Animatable(20f) }
    val tagA = remember { Animatable(0f) }
    val scrA = remember { Animatable(1f) }

    val wingTr = rememberInfiniteTransition(label = "w")
    val wingAng by wingTr.animateFloat(
        initialValue = -15f, targetValue = 15f,
        animationSpec = infiniteRepeatable(tween(150, easing = LinearEasing), RepeatMode.Reverse),
        label = "wa",
    )

    LaunchedEffect(Unit) {
        // Scene 1 (0ms)
        launch { soilA.animateTo(1f, tween(100)) }
        launch { stemP.animateTo(1f, tween(800, easing = EaseOutCubic)) }
        launch {
            delay(560)
            launch { lf1.animateTo(-35f, spring(dampingRatio = 0.5f)) }
            launch { lf2.animateTo(35f, spring(dampingRatio = 0.5f)) }
        }
        launch {
            delay(760)
            launch { sl1.animateTo(-35f, spring(dampingRatio = 0.5f)) }
            launch { sl2.animateTo(35f, spring(dampingRatio = 0.5f)) }
        }
        // Scene 2 (800ms)
        launch { delay(800); hiveA.animateTo(1f, tween(300)) }
        launch { delay(1000); b1a.snapTo(1f); b1t.animateTo(1f, tween(800, easing = EaseInOutCubic)) }
        launch { delay(1750); b1a.animateTo(0f, tween(50)) }
        launch { delay(1300); b2a.snapTo(1f); b2t.animateTo(1f, tween(900, easing = EaseInOutCubic)) }
        launch { delay(1500); b3a.snapTo(1f); b3t.animateTo(1f, tween(1200, easing = EaseInOutCubic)) }
        // Scene 3 (1800ms)
        launch { delay(1800); eggA.snapTo(1f); eggS.animateTo(1f, spring(dampingRatio = 0.6f)) }
        launch { delay(2200); crackP.animateTo(1f, tween(300)) }
        launch { delay(2500); shellSp.animateTo(1f, tween(300)) }
        launch { delay(2500); fragP.animateTo(1f, tween(500)) }
        launch { delay(2600); chickA.snapTo(1f); chickY.animateTo(-5f, tween(400, easing = EaseOutBack)) }
        launch {
            delay(2800)
            repeat(2) {
                chickY.animateTo(-7f, spring(stiffness = 600f))
                chickY.animateTo(-5f, spring(stiffness = 600f))
            }
        }
        // Scene 4 (2800ms)
        launch { delay(2800); elScale.animateTo(0.9f, tween(300)) }
        launch { delay(2800); pSlideX.animateTo(-1f, tween(300)) }
        launch { delay(2800); hSlideX.animateTo(1f, tween(300)) }
        launch { delay(2800); eSlideY.animateTo(-1f, tween(300)) }
        launch {
            delay(2800)
            launch { logoA.animateTo(1f, tween(400, easing = EaseOutCubic)) }
            launch { logoY.animateTo(0f, tween(400, easing = EaseOutCubic)) }
        }
        launch { delay(2950); tagA.animateTo(1f, tween(250)) }
        launch { delay(4700); scrA.animateTo(0f, tween(300)) }
        delay(5000)
        onFinished()
    }

    val eggShell = Color(0xFFF5F0E0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Void)
            .graphicsLayer { alpha = scrA.value },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val dp1 = 1.dp.toPx()
            val sc = elScale.value

            // ── SCENE 1: Plant ──
            val plantCx = w * 0.5f + pSlideX.value * w * 0.08f
            val soilY = h * 0.65f
            val soilHalf = w * 0.2f * sc
            val stemLen = 80f * dp1 * sc

            // Soil line
            drawLine(
                TextTertiary, Offset(plantCx - soilHalf, soilY),
                Offset(plantCx + soilHalf, soilY), 3f * dp1, alpha = soilA.value,
            )

            // Stem
            if (stemP.value > 0f) {
                drawLine(
                    GardenGlow, Offset(plantCx, soilY),
                    Offset(plantCx, soilY - stemLen * stemP.value),
                    3f * dp1, alpha = soilA.value,
                )
            }

            // Big leaves at 60% stem height
            if (lf1.value != 0f) {
                val ly = soilY - stemLen * 0.6f
                val ll = 20f * dp1 * sc
                val lw = 12f * dp1 * sc
                drawLeaf(plantCx, ly, ll, lw, lf1.value, GardenGlow)
                drawLeaf(plantCx, ly, ll, lw, lf2.value, GardenGlow)
            }

            // Small leaves at 85% stem height
            if (sl1.value != 0f) {
                val sy = soilY - stemLen * 0.85f
                val sll = 12f * dp1 * sc
                val slw = 7f * dp1 * sc
                drawLeaf(plantCx, sy, sll, slw, sl1.value, GardenGlow)
                drawLeaf(plantCx, sy, sll, slw, sl2.value, GardenGlow)
            }

            // ── SCENE 2: Hive + Bees ──
            val hiveCx = w * 0.72f + hSlideX.value * w * 0.05f
            val hiveCy = h * 0.48f
            val hiveW = 50f * dp1 * sc
            val hiveH = 60f * dp1 * sc
            val hiveL = hiveCx - hiveW / 2
            val hiveR = hiveCx + hiveW / 2
            val hiveT = hiveCy - hiveH / 2
            val hiveB = hiveCy + hiveH / 2
            val topIn = hiveW * 0.15f

            if (hiveA.value > 0f) {
                // Body trapezoid
                val bodyPath = Path().apply {
                    moveTo(hiveL, hiveB); lineTo(hiveR, hiveB)
                    lineTo(hiveR - topIn, hiveT); lineTo(hiveL + topIn, hiveT); close()
                }
                drawPath(bodyPath, BeeGlow.copy(alpha = 0.8f), alpha = hiveA.value)

                // Roof triangle
                val roofPath = Path().apply {
                    moveTo(hiveL + topIn - 5f * dp1, hiveT)
                    lineTo(hiveCx, hiveT - hiveH * 0.2f)
                    lineTo(hiveR - topIn + 5f * dp1, hiveT); close()
                }
                drawPath(roofPath, BeeGlow.copy(alpha = 0.8f), alpha = hiveA.value)

                // Horizontal lines
                for (i in 1..3) {
                    val ly = hiveT + hiveH * i / 4f
                    val frac = (ly - hiveT) / hiveH
                    val lx = hiveL + topIn * (1f - frac)
                    val rx = hiveR - topIn * (1f - frac)
                    drawLine(TextTertiary.copy(alpha = 0.4f), Offset(lx, ly), Offset(rx, ly), dp1, alpha = hiveA.value)
                }

                // Entrance
                drawOval(
                    Void,
                    Offset(hiveCx - 5f * dp1, hiveB - 8f * dp1),
                    Size(10f * dp1, 8f * dp1), alpha = hiveA.value,
                )
            }

            val hiveEntrance = Offset(hiveCx, hiveB - 4f * dp1)

            // Bee 1: off-screen right → hive entrance
            if (b1a.value > 0f) {
                val pos = quadBezier(
                    b1t.value,
                    Offset(w + 30f * dp1, hiveCy - 50f * dp1),
                    Offset(hiveCx + 60f * dp1, hiveCy - 80f * dp1),
                    hiveEntrance,
                )
                drawBee(pos, wingAng, b1a.value)
            }

            // Bee 2: hive entrance → off-screen left
            if (b2a.value > 0f) {
                val pos = quadBezier(
                    b2t.value, hiveEntrance,
                    Offset(hiveCx - 80f * dp1, hiveCy - 100f * dp1),
                    Offset(-30f * dp1, hiveCy - 40f * dp1),
                )
                drawBee(pos, wingAng, b2a.value)
            }

            // Bee 3: upper right → plant → hive
            if (b3a.value > 0f) {
                val pos = if (b3t.value < 0.5f) {
                    quadBezier(
                        b3t.value * 2f,
                        Offset(w * 0.8f, h * 0.2f),
                        Offset(plantCx + 20f * dp1, soilY - stemLen * 0.7f),
                        Offset(plantCx, soilY - stemLen * 0.5f),
                    )
                } else {
                    quadBezier(
                        (b3t.value - 0.5f) * 2f,
                        Offset(plantCx, soilY - stemLen * 0.5f),
                        Offset(plantCx + 40f * dp1, hiveCy + 20f * dp1),
                        hiveEntrance,
                    )
                }
                drawBee(pos, wingAng, b3a.value)
            }

            // ── SCENE 3: Egg + Chick ──
            val eggCx = w * 0.28f
            val eggCy = h * 0.48f + eSlideY.value * h * 0.05f
            val eggW = 30f * dp1 * sc * eggS.value
            val eggH = 40f * dp1 * sc * eggS.value
            val eggL = eggCx - eggW / 2
            val eggT = eggCy - eggH / 2
            val eggB = eggCy + eggH / 2
            val crackY = eggCy - eggH * 0.15f

            if (eggA.value > 0f) {
                // Crack zigzag points
                val crackPts = listOf(
                    Offset(eggCx - eggW * 0.3f, crackY),
                    Offset(eggCx - eggW * 0.15f, crackY - 3f * dp1),
                    Offset(eggCx, crackY + 2f * dp1),
                    Offset(eggCx + eggW * 0.1f, crackY - 2f * dp1),
                    Offset(eggCx + eggW * 0.2f, crackY + 3f * dp1),
                    Offset(eggCx + eggW * 0.3f, crackY - 1f * dp1),
                )

                if (shellSp.value > 0f) {
                    // ── Split egg ──

                    // Chick (behind bottom half)
                    if (chickA.value > 0f) {
                        val ccy = crackY + chickY.value * dp1
                        val headR = 6f * dp1 * sc

                        // Head
                        drawCircle(BeeGlow, headR, Offset(eggCx, ccy), alpha = chickA.value)

                        // Beak
                        val beakPath = Path().apply {
                            moveTo(eggCx + headR * 0.7f, ccy)
                            lineTo(eggCx + headR * 0.7f + 3f * dp1, ccy - 1.5f * dp1)
                            lineTo(eggCx + headR * 0.7f + 3f * dp1, ccy + 1.5f * dp1)
                            close()
                        }
                        drawPath(beakPath, PoultryGlow, alpha = chickA.value)

                        // Eyes
                        drawCircle(
                            Void, dp1,
                            Offset(eggCx - 2f * dp1, ccy - 1.5f * dp1), alpha = chickA.value,
                        )
                        drawCircle(
                            Void, dp1,
                            Offset(eggCx + 2f * dp1, ccy - 1.5f * dp1), alpha = chickA.value,
                        )
                    }

                    // Bottom half
                    clipRect(
                        left = eggL - dp1, top = crackY,
                        right = eggL + eggW + dp1, bottom = eggB + dp1,
                    ) {
                        drawOval(eggShell, Offset(eggL, eggT), Size(eggW, eggH), alpha = eggA.value)
                    }

                    // Top half (translated + rotated)
                    val splitY = shellSp.value * -8f * dp1
                    val splitRot = shellSp.value * 15f
                    translate(top = splitY) {
                        rotate(degrees = splitRot, pivot = Offset(eggCx, crackY)) {
                            clipRect(
                                left = eggL - dp1, top = eggT - dp1,
                                right = eggL + eggW + dp1, bottom = crackY,
                            ) {
                                drawOval(eggShell, Offset(eggL, eggT), Size(eggW, eggH), alpha = eggA.value)
                            }
                        }
                    }

                    // Shell fragments
                    if (fragP.value > 0f) {
                        val t = fragP.value
                        val fAlpha = (1f - t * 2f).coerceIn(0f, 1f)
                        if (fAlpha > 0f) {
                            fragments.forEach { frag ->
                                val fx = eggCx + frag.dx * eggW * 0.3f + frag.vx * t * dp1 * 8f
                                val fy = crackY + frag.dy * eggH * 0.1f + frag.vy * t * dp1 * 8f + 200f * t * t
                                val fs = 4f * dp1
                                val triPath = Path().apply {
                                    moveTo(fx, fy - fs / 2); lineTo(fx - fs / 2, fy + fs / 2)
                                    lineTo(fx + fs / 2, fy + fs / 2); close()
                                }
                                rotate(frag.rot * t * 3f, Offset(fx, fy)) {
                                    drawPath(triPath, eggShell, alpha = fAlpha)
                                }
                            }
                        }
                    }
                } else {
                    // ── Whole egg ──
                    drawOval(eggShell, Offset(eggL, eggT), Size(eggW, eggH), alpha = eggA.value)
                }

                // Crack line (draws on top of egg)
                if (crackP.value > 0f) {
                    val totalSegs = crackPts.size - 1
                    val segsToShow = (crackP.value * totalSegs).toInt()
                    val partial = crackP.value * totalSegs - segsToShow

                    for (i in 0 until segsToShow) {
                        drawLine(
                            TextTertiary.copy(alpha = 0.6f), crackPts[i], crackPts[i + 1],
                            1.5f * dp1,
                        )
                    }
                    if (segsToShow < totalSegs) {
                        val s = crackPts[segsToShow]
                        val e = crackPts[segsToShow + 1]
                        drawLine(
                            TextTertiary.copy(alpha = 0.6f), s,
                            Offset(s.x + (e.x - s.x) * partial, s.y + (e.y - s.y) * partial),
                            1.5f * dp1,
                        )
                    }
                }
            }
        }

        // ── SCENE 4: Text ──
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Furrow",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                ),
                color = TextPrimary,
                modifier = Modifier.graphicsLayer {
                    alpha = logoA.value
                    translationY = logoY.value * density
                },
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Grow smarter.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                modifier = Modifier.graphicsLayer { alpha = tagA.value },
            )
        }
    }
}
