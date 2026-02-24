package com.furrow.app.ui.animals

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.EggAlt
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.Animal
import com.furrow.app.util.DateUtil
import com.furrow.app.util.displayFormat
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.Tag
import com.furrow.app.ui.theme.AnimalsGlow
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void
@Composable
fun AnimalListScreen(
    onAnimalClick: (Long) -> Unit,
    onAddAnimal: () -> Unit,
    onAddEgg: () -> Unit = {},
    onEditEgg: (Long) -> Unit = {},
    onReportsClick: () -> Unit = {},
    viewModel: AnimalViewModel = hiltViewModel(),
) {
    val animals by viewModel.filteredAnimals.collectAsState()
    val allActive by viewModel.activeAnimals.collectAsState()
    val speciesList by viewModel.speciesList.collectAsState()
    val selectedSpecies by viewModel.selectedSpecies.collectAsState()
    val upcomingBirths by viewModel.upcomingBirths.collectAsState()
    val activeWithdrawals by viewModel.activeWithdrawals.collectAsState()

    // Egg tracking state
    val chickenCount by viewModel.chickenCount.collectAsState()
    val todayEggCount by viewModel.todayEggCount.collectAsState()
    val weeklyTotal by viewModel.weeklyTotal.collectAsState()
    val layRate by viewModel.layRatePercent.collectAsState()
    val dailyCounts by viewModel.dailyEggCounts.collectAsState()
    val eggLogs by viewModel.eggLogs.collectAsState()

    var selectedAnimal by remember { mutableStateOf<Animal?>(null) }
    var animalToDelete by remember { mutableStateOf<Animal?>(null) }
    var eggLogForAction by remember { mutableStateOf<EggLog?>(null) }
    var eggLogToDelete by remember { mutableStateOf<EggLog?>(null) }

    val hasChickens = chickenCount > 0

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(
                title = "Animals",
                actions = {
                    if (hasChickens) {
                        IconButton(onClick = onReportsClick) {
                            Icon(
                                Icons.Outlined.Assessment,
                                contentDescription = "Reports",
                                tint = TextSecondary,
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAnimal,
                containerColor = AnimalsGlow,
                contentColor = Void,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Animal",
                )
            }
        },
    ) { padding ->
        if (animals.isEmpty() && selectedSpecies == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                EmptyState(
                    title = "No animals yet",
                    subtitle = "Add your first animal to start tracking your livestock.",
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Pets,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = AnimalsGlow,
                        )
                    },
                    actionLabel = "Add Animal",
                    onAction = onAddAnimal,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = AppSpacing.md,
                    end = AppSpacing.md,
                    top = AppSpacing.md,
                    bottom = AppSpacing.bottomListPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                item(key = "species_filter") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    ) {
                        item {
                            Tag(
                                text = "All",
                                selected = selectedSpecies == null,
                                onClick = { viewModel.setSpeciesFilter(null) },
                                accentColor = AnimalsGlow,
                            )
                        }
                        items(speciesList, key = { it }) { species ->
                            Tag(
                                text = species.displayFormat(),
                                selected = selectedSpecies == species,
                                onClick = { viewModel.setSpeciesFilter(species) },
                                accentColor = AnimalsGlow,
                            )
                        }
                    }
                }

                item(key = "summary_stats") {
                    Panel {
                        InlineStat(
                            label = "Total Active",
                            value = "${allActive.size}",
                        )
                        InlineStat(
                            label = "Upcoming Births",
                            value = "${upcomingBirths.size}",
                        )
                        InlineStat(
                            label = "Active Withdrawals",
                            value = "${activeWithdrawals.size}",
                        )
                    }
                }

                // ── Poultry Dashboard (when chickens exist) ──
                if (hasChickens && (selectedSpecies == null || selectedSpecies == "chicken")) {
                    item(key = "egg_stats") {
                        Panel {
                            Text(
                                "Egg production",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                            )
                            InlineStat(label = "Eggs today", value = todayEggCount.toString())
                            InlineStat(label = "This week", value = weeklyTotal.toString())
                            InlineStat(label = "Flock size", value = chickenCount.toString())
                            InlineStat(label = "Lay rate", value = "${layRate ?: 0}%")
                            PrimaryButton(
                                text = "Log Eggs",
                                onClick = onAddEgg,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    val hasChartData = dailyCounts.any { it.count > 0 }
                    if (dailyCounts.isNotEmpty() && hasChartData) {
                        item(key = "egg_chart") {
                            Panel {
                                Column {
                                    Text(
                                        "7-day egg trend",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary,
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    WeeklyEggChart(
                                        dailyCounts = dailyCounts,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp),
                                    )
                                }
                            }
                        }
                    }

                    if (eggLogs.isNotEmpty()) {
                        item(key = "egg_log_list") {
                            val view = LocalView.current
                            Panel(contentPadding = PaddingValues(0.dp)) {
                                eggLogs.take(10).forEachIndexed { index, entry ->
                                    ListRow(
                                        modifier = Modifier.pointerInput(entry) {
                                            detectTapGestures(onLongPress = {
                                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                                eggLogForAction = entry
                                            })
                                        },
                                        title = DateUtil.formatDate(entry.date),
                                        subtitle = entry.notes,
                                        trailingText = "${entry.count} eggs",
                                        showDivider = index != (eggLogs.take(10).lastIndex),
                                    )
                                }
                            }
                        }
                    }
                }

                if (animals.isNotEmpty()) {
                    item(key = "animals_panel") {
                        val view = LocalView.current
                        Panel(contentPadding = PaddingValues(0.dp)) {
                            animals.forEachIndexed { index, animal ->
                                val displayName = animal.name
                                    ?: "Unnamed ${animal.species.displayFormat()}"
                                val subtitle = buildString {
                                    append(animal.breed)
                                    append(" \u2022 ")
                                    append(animal.sex.displayFormat())
                                }

                                ListRow(
                                    title = displayName,
                                    subtitle = subtitle,
                                    metadata = animal.status.displayFormat(),
                                    onClick = { onAnimalClick(animal.id) },
                                    showDivider = index != animals.lastIndex,
                                    modifier = Modifier.pointerInput(animal) {
                                        detectTapGestures(onLongPress = {
                                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                            selectedAnimal = animal
                                        })
                                    },
                                )
                            }
                        }
                    }
                } else {
                    item(key = "empty_filtered") {
                        EmptyState(
                            title = "No animals found",
                            subtitle = "No animals match the selected filter.",
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Pets,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = TextSecondary,
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    selectedAnimal?.let { animal ->
        ItemActionSheet(
            onDismiss = { selectedAnimal = null },
            onEdit = { onAnimalClick(animal.id) },
            onDelete = {
                animalToDelete = animal
                selectedAnimal = null
            },
        )
    }

    animalToDelete?.let { animal ->
        DeleteConfirmationDialog(
            itemName = animal.name ?: "Unnamed ${animal.species.displayFormat()}",
            onConfirm = {
                viewModel.deleteAnimal(animal)
                animalToDelete = null
            },
            onDismiss = { animalToDelete = null },
        )
    }

    eggLogForAction?.let { eggLog ->
        ItemActionSheet(
            onDismiss = { eggLogForAction = null },
            onEdit = { onEditEgg(eggLog.id) },
            onDelete = { eggLogToDelete = eggLog },
        )
    }

    eggLogToDelete?.let { eggLog ->
        DeleteConfirmationDialog(
            itemName = "egg log from ${DateUtil.formatDate(eggLog.date)}",
            onConfirm = {
                viewModel.deleteEggLog(eggLog)
                eggLogToDelete = null
            },
            onDismiss = { eggLogToDelete = null },
        )
    }
}

// ── 7-Day Chart ──

@Composable
private fun WeeklyEggChart(
    dailyCounts: List<DailyEggCount>,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val countStyle = TextStyle(fontSize = 10.sp, color = TextSecondary, textAlign = TextAlign.Center)
    val labelStyle = TextStyle(fontSize = 10.sp, color = TextTertiary, textAlign = TextAlign.Center)
    val todayLabelStyle = TextStyle(fontSize = 10.sp, color = TextPrimary, textAlign = TextAlign.Center)
    val cornerRadiusPx = with(density) { 4.dp.toPx() }
    val labelSpacePx = with(density) { 16.dp.toPx() }
    val topPadPx = with(density) { 14.dp.toPx() }

    Canvas(modifier = modifier) {
        val maxCount = (dailyCounts.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)
        val barCount = dailyCounts.size
        val totalSpacing = size.width * 0.3f
        val barWidth = (size.width - totalSpacing) / barCount
        val gap = totalSpacing / (barCount + 1)
        val maxBarHeight = size.height - labelSpacePx - topPadPx

        dailyCounts.forEachIndexed { index, day ->
            val barHeight = if (maxCount > 0) (day.count.toFloat() / maxCount) * maxBarHeight else 0f
            val x = gap + index * (barWidth + gap)
            val y = topPadPx + maxBarHeight - barHeight
            val barColor = if (day.isToday) AnimalsGlow else AnimalsGlow.copy(alpha = 0.4f)

            if (barHeight > 0f) {
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                )
            }

            // Value above bar
            if (day.count > 0) {
                val label = day.count.toString()
                val textLayout = textMeasurer.measure(label, countStyle)
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        x + (barWidth - textLayout.size.width) / 2,
                        y - textLayout.size.height - 2.dp.toPx(),
                    ),
                )
            }

            // Day label below
            val style = if (day.isToday) todayLabelStyle else labelStyle
            val dayLayout = textMeasurer.measure(day.dayLabel, style)
            drawText(
                textLayoutResult = dayLayout,
                topLeft = Offset(
                    x + (barWidth - dayLayout.size.width) / 2,
                    topPadPx + maxBarHeight + 4.dp.toPx(),
                ),
            )
        }
    }
}

