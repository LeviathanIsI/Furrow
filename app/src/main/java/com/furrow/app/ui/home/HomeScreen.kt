package com.furrow.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.ui.components.GlowCard
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BeeGlow
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.DmSans
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.PoultryGlow
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit = {},
    onNavigateToBees: () -> Unit = {},
    onNavigateToPoultry: () -> Unit = {},
    onNavigateToGarden: () -> Unit = {},
    onNavigateToEggLog: () -> Unit = {},
    onNavigateToInspection: (() -> Unit)? = null,
    onNavigateToHarvest: (() -> Unit)? = null,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val bees by viewModel.beeInsights.collectAsState()
    val poultry by viewModel.poultryInsights.collectAsState()
    val garden by viewModel.gardenInsights.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val seasonalTip by viewModel.seasonalTip.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(top = AppSpacing.md, start = AppSpacing.md, end = AppSpacing.md),
    ) {
        // ── Header ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Furrow",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = DmSans,
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.weight(1f))
            profile?.let { p ->
                Surface(
                    color = Charcoal,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(0.5.dp, BorderSubtle),
                    modifier = Modifier.clickable(onClick = onSettingsClick),
                ) {
                    Text(
                        text = "${p.hardinessZone} \u00b7 ${p.state}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontFamily = DmSans,
                        modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(AppSpacing.xxs))
        Text(
            text = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()),
            ),
            fontSize = 14.sp,
            color = TextTertiary,
            fontFamily = DmSans,
        )

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // ── Seasonal Tip ──
        if (!seasonalTip.isNullOrBlank()) {
            GlowCard(glowColor = Color.Transparent) {
                Column {
                    Text(
                        "THIS MONTH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextTertiary,
                        fontFamily = DmSans,
                        letterSpacing = 2.sp,
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    Text(
                        seasonalTip!!,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontFamily = DmSans,
                        lineHeight = 20.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppSpacing.md))
        }

        // ── Bees Card ──
        GlowCard(
            glowColor = BeeGlow,
            glowIntensity = 0.10f,
            onClick = onNavigateToBees,
        ) {
            Column {
                ModuleCardHeader("Bees")
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                if (bees.hiveCount > 0) {
                    Row {
                        Text(
                            "${bees.hiveCount} hive${if (bees.hiveCount != 1) "s" else ""}",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            fontFamily = DmSans,
                        )
                        val inspDays = bees.daysSinceLastInspection
                        if (inspDays != null) {
                            Spacer(modifier = Modifier.width(AppSpacing.md))
                            Text(
                                "$inspDays days since inspection",
                                fontSize = 14.sp,
                                fontFamily = DmSans,
                                color = when {
                                    inspDays <= 7 -> StatusGood
                                    inspDays <= 13 -> StatusWarn
                                    else -> TextSecondary
                                },
                            )
                        }
                    }
                } else {
                    Text("No hives yet", fontSize = 14.sp, color = TextSecondary, fontFamily = DmSans)
                    Spacer(modifier = Modifier.height(AppSpacing.xxs))
                    Text("Add Hive \u2192", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BeeGlow, fontFamily = DmSans)
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.sm))

        // ── Poultry Card ──
        GlowCard(
            glowColor = PoultryGlow,
            glowIntensity = 0.10f,
            onClick = onNavigateToPoultry,
        ) {
            Column {
                ModuleCardHeader("Poultry")
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                if (poultry.flockSize > 0) {
                    Row {
                        Text(
                            "${poultry.todayEggs} eggs today",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            fontFamily = DmSans,
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.md))
                        Text(
                            "${poultry.thisWeekTotal} this week",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            fontFamily = DmSans,
                        )
                    }
                } else {
                    Text("Start tracking your flock", fontSize = 14.sp, color = TextSecondary, fontFamily = DmSans)
                    Spacer(modifier = Modifier.height(AppSpacing.xxs))
                    Text("Add Chicken \u2192", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = PoultryGlow, fontFamily = DmSans)
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.sm))

        // ── Garden Card ──
        GlowCard(
            glowColor = GardenGlow,
            glowIntensity = 0.10f,
            onClick = onNavigateToGarden,
        ) {
            Column {
                ModuleCardHeader("Garden")
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                if (garden.activePlantings > 0) {
                    Row {
                        Text(
                            "${garden.activePlantings} plantings",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            fontFamily = DmSans,
                        )
                        if (garden.readyToHarvestCount > 0) {
                            Spacer(modifier = Modifier.width(AppSpacing.md))
                            Text(
                                "${garden.readyToHarvestCount} ready to harvest",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                fontFamily = DmSans,
                            )
                        }
                    }
                } else {
                    Text("Plant your first seed", fontSize = 14.sp, color = TextSecondary, fontFamily = DmSans)
                    Spacer(modifier = Modifier.height(AppSpacing.xxs))
                    Text("Add Bed \u2192", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GardenGlow, fontFamily = DmSans)
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

@Composable
private fun ModuleCardHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            fontFamily = DmSans,
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextTertiary,
        )
    }
}
