package com.furrow.app.ui.poultry

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.EggAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.Chicken
import com.furrow.app.data.local.entity.ChickenBreedInfo
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.ui.bees.DropdownSelector
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.FurrowBottomSheet
import com.furrow.app.ui.components.GlowCard
import com.furrow.app.ui.components.GlowRing
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.SearchableSelector
import com.furrow.app.ui.components.StatNumber
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.DmSans
import com.furrow.app.ui.theme.PoultryGlow
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val zone: ZoneId = ZoneId.systemDefault()
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

private enum class ClimateBadge(val label: String) {
    GREAT("Great for your climate"),
    MANAGEABLE("Manageable"),
    NOT_IDEAL("Not ideal"),
}

private fun climateBadgeFor(breed: ChickenBreedInfo, zoneGroup: String?): ClimateBadge {
    if (zoneGroup == null) return ClimateBadge.MANAGEABLE
    val relevantTolerance = when (zoneGroup) {
        "hot", "warm" -> breed.heatTolerance
        "cold", "extreme_cold" -> breed.coldTolerance
        "moderate" -> minOf(breed.heatTolerance, breed.coldTolerance)
        else -> 3
    }
    return when {
        relevantTolerance >= 4 -> ClimateBadge.GREAT
        relevantTolerance == 3 -> ClimateBadge.MANAGEABLE
        else -> ClimateBadge.NOT_IDEAL
    }
}

// ── Main Screen ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlockScreen(
    onAddEgg: () -> Unit,
    onEditEgg: (Long) -> Unit,
    onChickenClick: (Long) -> Unit,
    onReportsClick: () -> Unit,
    viewModel: PoultryViewModel = hiltViewModel(),
) {
    val eggLogs by viewModel.eggLogs.collectAsState()
    val chickens by viewModel.activeChickens.collectAsState()
    val todayCount by viewModel.todayEggCount.collectAsState()
    val weeklyTotal by viewModel.weeklyTotal.collectAsState()
    val flockSize by viewModel.flockSize.collectAsState()
    val layRate by viewModel.layRatePercent.collectAsState()
    val breedInfoMap by viewModel.breedInfoMap.collectAsState()
    val allBreeds by viewModel.allBreeds.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val dailyCounts by viewModel.dailyEggCounts.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("EGG LOG", "FLOCK")
    var showAddChickenDialog by remember { mutableStateOf(false) }
    var eggLogToDelete by remember { mutableStateOf<EggLog?>(null) }
    var chickenToDelete by remember { mutableStateOf<Chicken?>(null) }
    var eggLogForAction by remember { mutableStateOf<EggLog?>(null) }
    var chickenForAction by remember { mutableStateOf<Chicken?>(null) }
    var chickenToEdit by remember { mutableStateOf<Chicken?>(null) }

    Scaffold(
        containerColor = Void,
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = onAddEgg,
                    containerColor = PoultryGlow,
                    contentColor = Void,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(Icons.Filled.Egg, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Eggs")
                }
            } else {
                FloatingActionButton(
                    onClick = { showAddChickenDialog = true },
                    containerColor = PoultryGlow,
                    contentColor = Void,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Chicken")
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // ── Header ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 8.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Poultry",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = DmSans,
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onReportsClick) {
                    Icon(
                        Icons.Outlined.Assessment,
                        contentDescription = "Reports",
                        tint = TextTertiary,
                    )
                }
            }

            // ── Stats Card ──
            if (eggLogs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                GlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    glowColor = PoultryGlow,
                    glowIntensity = 0.12f,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            StatNumber(
                                value = "$todayCount",
                                label = "today",
                                glowColor = PoultryGlow,
                                fontSize = 48,
                            )
                        }
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            GlowRing(
                                progress = (layRate ?: 0) / 100f,
                                glowColor = PoultryGlow,
                                size = 80.dp,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${layRate ?: 0}%",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PoultryGlow,
                                    )
                                    Text(
                                        "rate",
                                        fontSize = 10.sp,
                                        color = TextTertiary,
                                    )
                                }
                            }
                        }
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            StatNumber(
                                value = "$weeklyTotal",
                                label = "this week",
                                glowColor = TextPrimary,
                                fontSize = 32,
                            )
                        }
                    }
                }
            }

            // ── 7-Day Chart ──
            val hasChartData = dailyCounts.any { it.count > 0 }
            if (dailyCounts.isNotEmpty() && hasChartData) {
                Spacer(modifier = Modifier.height(12.dp))
                GlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    glowColor = Color.Transparent,
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "7-DAY EGGS",
                            fontSize = 12.sp,
                            color = TextTertiary,
                            letterSpacing = 2.sp,
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

            Spacer(modifier = Modifier.height(12.dp))

            // ── Tabs ──
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Charcoal,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            height = 2.dp,
                            color = PoultryGlow,
                        )
                    }
                },
                divider = { HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle) },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    letterSpacing = 1.2.sp,
                                ),
                                color = if (selectedTab == index) TextPrimary else TextTertiary,
                            )
                        },
                    )
                }
            }

            // ── Tab Content ──
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> EggLogList(
                        eggLogs = eggLogs,
                        onAddEgg = onAddEgg,
                        onLongPress = { eggLogForAction = it },
                    )
                    1 -> ChickenList(
                        chickens = chickens,
                        breedInfoMap = breedInfoMap,
                        onChickenClick = onChickenClick,
                        onAddChicken = { showAddChickenDialog = true },
                        onLongPress = { chickenForAction = it },
                    )
                }
            }
        }
    }

    // ── Sheets & Dialogs ──

    if (showAddChickenDialog || chickenToEdit != null) {
        AddChickenSheet(
            existingChicken = chickenToEdit,
            breeds = allBreeds,
            zoneGroup = userProfile?.zoneGroup,
            onDismiss = { showAddChickenDialog = false; chickenToEdit = null },
            onSave = { chicken ->
                if (chickenToEdit != null) viewModel.updateChicken(chicken) else viewModel.addChicken(chicken)
                showAddChickenDialog = false
                chickenToEdit = null
            },
            onInsertBreed = { breed -> viewModel.insertBreed(breed) },
            onUpdateBreed = { breed -> viewModel.updateBreed(breed) },
            onDeleteBreed = { breed -> viewModel.deleteBreed(breed) },
        )
    }

    eggLogToDelete?.let { eggLog ->
        DeleteConfirmationDialog(
            itemName = "egg log from ${formatDate(eggLog.date)}",
            onConfirm = { viewModel.deleteEggLog(eggLog); eggLogToDelete = null },
            onDismiss = { eggLogToDelete = null },
        )
    }

    chickenToDelete?.let { chicken ->
        DeleteConfirmationDialog(
            itemName = chicken.name ?: chicken.breed,
            onConfirm = { viewModel.deleteChicken(chicken); chickenToDelete = null },
            onDismiss = { chickenToDelete = null },
        )
    }

    eggLogForAction?.let { eggLog ->
        ItemActionSheet(
            onDismiss = { eggLogForAction = null },
            onEdit = { onEditEgg(eggLog.id) },
            onDelete = { eggLogToDelete = eggLog },
        )
    }

    chickenForAction?.let { chicken ->
        ItemActionSheet(
            onDismiss = { chickenForAction = null },
            onEdit = { chickenToEdit = chicken },
            onDelete = { chickenToDelete = chicken },
        )
    }
}

// ── Egg Log Tab ──

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EggLogList(
    eggLogs: List<EggLog>,
    onAddEgg: () -> Unit,
    onLongPress: (EggLog) -> Unit,
) {
    val view = LocalView.current

    if (eggLogs.isEmpty()) {
        EmptyState(
            icon = {
                Icon(
                    Icons.Outlined.EggAlt,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = PoultryGlow,
                )
            },
            title = "No egg logs yet",
            subtitle = "Tap \"Log Eggs\" to record today's count",
            actionLabel = "Log Eggs",
            glowColor = PoultryGlow,
            onAction = onAddEgg,
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            items(eggLogs, key = { it.id }) { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                onLongPress(entry)
                            },
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() },
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(StatusGood, CircleShape),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        formatDate(entry.date),
                        fontSize = 14.sp,
                        color = TextPrimary,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "${entry.count} eggs",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = PoultryGlow,
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = BorderSubtle,
                )
            }
        }
    }
}

// ── Flock Tab ──

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChickenList(
    chickens: List<Chicken>,
    breedInfoMap: Map<String, ChickenBreedInfo>,
    onChickenClick: (Long) -> Unit,
    onAddChicken: () -> Unit,
    onLongPress: (Chicken) -> Unit,
) {
    val view = LocalView.current

    if (chickens.isEmpty()) {
        EmptyState(
            icon = {
                Icon(
                    Icons.Filled.Egg,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = PoultryGlow,
                )
            },
            title = "No chickens yet",
            subtitle = "Add your first bird to start tracking",
            actionLabel = "Add Chicken",
            glowColor = PoultryGlow,
            onAction = onAddChicken,
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(chickens, key = { it.id }) { chicken ->
                val breedInfo = breedInfoMap[chicken.breed]
                val displayName = chicken.name ?: chicken.breed
                val isActive = chicken.status == "active"

                GlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onChickenClick(chicken.id) },
                            onLongClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                onLongPress(chicken)
                            },
                        ),
                    glowColor = Color.Transparent,
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(PoultryGlow.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                chicken.breed.first().uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PoultryGlow,
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                displayName,
                                fontSize = 16.sp,
                                color = TextPrimary,
                            )
                            Text(
                                "${chicken.breed} \u00b7 ${formatAge(chicken.dateAcquired)}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            if (isActive) {
                                Text(
                                    "Active",
                                    fontSize = 10.sp,
                                    color = StatusGood,
                                )
                            }
                            breedInfo?.let {
                                if (it.eggsPerYear > 0) {
                                    Text(
                                        "${it.eggsPerYear} eggs/yr",
                                        fontSize = 12.sp,
                                        color = TextTertiary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
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
            val barColor = if (day.isToday) PoultryGlow else PoultryGlow.copy(alpha = 0.4f)

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

// ── Form Sheets ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddChickenSheet(
    existingChicken: Chicken? = null,
    breeds: List<ChickenBreedInfo>,
    zoneGroup: String?,
    onDismiss: () -> Unit,
    onSave: (Chicken) -> Unit,
    onInsertBreed: (ChickenBreedInfo) -> Unit,
    onUpdateBreed: (ChickenBreedInfo) -> Unit,
    onDeleteBreed: (ChickenBreedInfo) -> Unit,
) {
    val isEditMode = existingChicken != null
    var name by remember { mutableStateOf(existingChicken?.name ?: "") }
    var breed by remember { mutableStateOf(existingChicken?.breed ?: "") }
    var breedQuery by remember { mutableStateOf(existingChicken?.breed ?: "") }
    var status by remember { mutableStateOf(existingChicken?.status ?: "active") }
    var notes by remember { mutableStateOf(existingChicken?.notes ?: "") }
    var dateAcquired by remember { mutableLongStateOf(existingChicken?.dateAcquired ?: System.currentTimeMillis()) }
    var showAddCustomBreed by remember { mutableStateOf(false) }
    var customBreedInitialName by remember { mutableStateOf("") }
    var breedToEdit by remember { mutableStateOf<ChickenBreedInfo?>(null) }
    var breedToDelete by remember { mutableStateOf<ChickenBreedInfo?>(null) }

    val filteredBreeds = remember(breeds, breedQuery, zoneGroup) {
        val query = breedQuery.lowercase()
        breeds
            .filter { query.isEmpty() || it.name.lowercase().contains(query) }
            .sortedWith(
                compareBy<ChickenBreedInfo> { b ->
                    when (climateBadgeFor(b, zoneGroup)) {
                        ClimateBadge.GREAT -> 0
                        ClimateBadge.MANAGEABLE -> 1
                        ClimateBadge.NOT_IDEAL -> 2
                    }
                }.thenBy { it.name },
            )
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Charcoal,
        focusedContainerColor = Charcoal,
        unfocusedBorderColor = BorderSubtle,
        focusedBorderColor = PoultryGlow,
        unfocusedLabelColor = TextTertiary,
        focusedLabelColor = PoultryGlow,
        cursorColor = PoultryGlow,
        unfocusedTextColor = TextPrimary,
        focusedTextColor = TextPrimary,
    )

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = if (isEditMode) "Edit Chicken" else "Add Chicken",
        confirmText = "Save",
        confirmEnabled = breed.isNotBlank(),
        glowColor = PoultryGlow,
        onConfirm = {
            if (breed.isNotBlank()) {
                onSave(
                    Chicken(
                        id = existingChicken?.id ?: 0,
                        name = name.ifBlank { null },
                        breed = breed.trim(),
                        dateAcquired = dateAcquired,
                        status = status,
                        notes = notes.ifBlank { null },
                    ),
                )
            }
        },
        content = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
            )
            SearchableSelector(
                query = breedQuery,
                onQueryChange = {
                    breedQuery = it
                    breed = it
                },
                items = filteredBreeds,
                onItemSelected = { b ->
                    breed = b.name
                    breedQuery = b.name
                },
                label = "Breed",
                accentColor = PoultryGlow,
                nameSelector = { it.name },
                isCustom = { it.isCustom },
                onAddCustom = { customName ->
                    customBreedInitialName = customName
                    showAddCustomBreed = true
                },
                onEditCustom = { b -> breedToEdit = b },
                onDeleteCustom = { b -> breedToDelete = b },
                itemContent = { b ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(b.name, style = MaterialTheme.typography.bodyLarge)
                            if (b.eggsPerYear > 0) {
                                Text(
                                    "${b.eggsPerYear} eggs/yr",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary,
                                )
                            } else {
                                Text(
                                    b.purpose.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary,
                                )
                            }
                        }
                        ClimateBadgePill(climateBadgeFor(b, zoneGroup))
                    }
                },
            )
            DateFieldWithToggle(
                label = "Date Acquired:",
                dateMillis = dateAcquired,
                onDateChange = { dateAcquired = it },
                useTodayDefault = !isEditMode,
            )
            DropdownSelector(
                label = "Status",
                options = listOf("active", "deceased", "rehomed", "processed"),
                selected = status,
                onSelect = { status = it },
                accentColor = PoultryGlow,
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
            )
        },
    )

    if (showAddCustomBreed || breedToEdit != null) {
        AddCustomBreedSheet(
            existingBreed = breedToEdit,
            initialName = if (breedToEdit != null) breedToEdit!!.name else customBreedInitialName,
            onDismiss = {
                showAddCustomBreed = false
                breedToEdit = null
            },
            onSave = { b ->
                if (breedToEdit != null) onUpdateBreed(b) else onInsertBreed(b)
                breed = b.name
                breedQuery = b.name
                showAddCustomBreed = false
                breedToEdit = null
            },
        )
    }

    breedToDelete?.let { b ->
        DeleteConfirmationDialog(
            itemName = b.name,
            onConfirm = {
                onDeleteBreed(b)
                if (breed == b.name) {
                    breed = ""
                    breedQuery = ""
                }
                breedToDelete = null
            },
            onDismiss = { breedToDelete = null },
        )
    }
}

@Composable
private fun AddCustomBreedSheet(
    existingBreed: ChickenBreedInfo?,
    initialName: String,
    onDismiss: () -> Unit,
    onSave: (ChickenBreedInfo) -> Unit,
) {
    var breedName by remember { mutableStateOf(existingBreed?.name ?: initialName) }
    var eggsPerYear by remember { mutableStateOf(existingBreed?.eggsPerYear?.toString() ?: "250") }
    var eggColor by remember { mutableStateOf(existingBreed?.eggColor ?: "brown") }
    var eggSize by remember { mutableStateOf(existingBreed?.eggSize ?: "large") }
    var weight by remember { mutableStateOf(existingBreed?.weight ?: "5-7 lbs") }
    var purpose by remember { mutableStateOf(existingBreed?.purpose ?: "dual-purpose") }
    var temperament by remember { mutableStateOf(existingBreed?.temperament ?: "friendly") }
    var combType by remember { mutableStateOf(existingBreed?.combType ?: "single") }
    var heatTolerance by remember { mutableStateOf(existingBreed?.heatTolerance?.toString() ?: "3") }
    var coldTolerance by remember { mutableStateOf(existingBreed?.coldTolerance?.toString() ?: "3") }
    var broodiness by remember { mutableStateOf(existingBreed?.broodiness ?: "low") }
    var climateNotes by remember { mutableStateOf(existingBreed?.climateNotes ?: "") }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Charcoal,
        focusedContainerColor = Charcoal,
        unfocusedBorderColor = BorderSubtle,
        focusedBorderColor = PoultryGlow,
        unfocusedLabelColor = TextTertiary,
        focusedLabelColor = PoultryGlow,
        cursorColor = PoultryGlow,
        unfocusedTextColor = TextPrimary,
        focusedTextColor = TextPrimary,
    )

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = if (existingBreed != null) "Edit Custom Breed" else "Add Custom Breed",
        confirmText = "Save",
        confirmEnabled = breedName.isNotBlank(),
        glowColor = PoultryGlow,
        onConfirm = {
            if (breedName.isNotBlank()) {
                onSave(
                    ChickenBreedInfo(
                        id = existingBreed?.id ?: 0,
                        name = breedName.trim(),
                        eggsPerYear = eggsPerYear.toIntOrNull() ?: 250,
                        eggColor = eggColor,
                        eggSize = eggSize,
                        weight = weight,
                        purpose = purpose,
                        temperament = temperament,
                        combType = combType,
                        heatTolerance = heatTolerance.toIntOrNull() ?: 3,
                        coldTolerance = coldTolerance.toIntOrNull() ?: 3,
                        broodiness = broodiness,
                        climateNotes = climateNotes.ifBlank { null },
                        isCustom = true,
                    ),
                )
            }
        },
        content = {
            OutlinedTextField(
                value = breedName,
                onValueChange = { breedName = it },
                label = { Text("Breed Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
            )
            OutlinedTextField(
                value = eggsPerYear,
                onValueChange = { eggsPerYear = it.filter { c -> c.isDigit() } },
                label = { Text("Eggs Per Year") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
            )
            DropdownSelector(
                label = "Egg Color",
                options = listOf("brown", "white", "blue", "green", "cream", "tinted"),
                selected = eggColor,
                onSelect = { eggColor = it },
                accentColor = PoultryGlow,
            )
            DropdownSelector(
                label = "Purpose",
                options = listOf("dual-purpose", "egg", "meat", "ornamental"),
                selected = purpose,
                onSelect = { purpose = it },
                accentColor = PoultryGlow,
            )
            OutlinedTextField(
                value = temperament,
                onValueChange = { temperament = it },
                label = { Text("Temperament") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = heatTolerance,
                    onValueChange = { heatTolerance = it.filter { c -> c.isDigit() } },
                    label = { Text("Heat (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                )
                OutlinedTextField(
                    value = coldTolerance,
                    onValueChange = { coldTolerance = it.filter { c -> c.isDigit() } },
                    label = { Text("Cold (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                )
            }
            DropdownSelector(
                label = "Broodiness",
                options = listOf("low", "moderate", "high"),
                selected = broodiness,
                onSelect = { broodiness = it },
                accentColor = PoultryGlow,
            )
            OutlinedTextField(
                value = climateNotes,
                onValueChange = { climateNotes = it },
                label = { Text("Climate Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
            )
        },
    )
}

// ── Helper Composables ──

@Composable
private fun ClimateBadgePill(badge: ClimateBadge) {
    val color = when (badge) {
        ClimateBadge.GREAT -> StatusGood
        ClimateBadge.MANAGEABLE -> StatusWarn
        ClimateBadge.NOT_IDEAL -> StatusBad
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
    ) {
        Text(
            badge.label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

// ── Helper Functions ──

private fun formatAge(dateAcquiredMillis: Long): String {
    val acquired = Instant.ofEpochMilli(dateAcquiredMillis).atZone(zone).toLocalDate()
    val today = LocalDate.now(zone)
    val days = ChronoUnit.DAYS.between(acquired, today)
    return when {
        days >= 365 -> "${days / 365} yr"
        days >= 30 -> "${days / 30} mo"
        else -> "$days days"
    }
}

private fun formatDate(millis: Long): String {
    return Instant.ofEpochMilli(millis).atZone(zone).toLocalDate().format(dateFormatter)
}
