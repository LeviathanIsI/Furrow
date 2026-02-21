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
import com.furrow.app.data.local.entity.Animal
import com.furrow.app.data.local.entity.AnimalBreedInfo
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

private fun climateBadgeFor(breed: AnimalBreedInfo, zoneGroup: String?): ClimateBadge {
    if (zoneGroup == null) return ClimateBadge.MANAGEABLE
    val relevantTolerance = when (zoneGroup) {
        "hot", "warm" -> breed.heatTolerance ?: 3
        "cold", "extreme_cold" -> breed.coldTolerance ?: 3
        "moderate" -> minOf(breed.heatTolerance ?: 3, breed.coldTolerance ?: 3)
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
    onAnimalClick: (Long) -> Unit,
    onReportsClick: () -> Unit,
    viewModel: PoultryViewModel = hiltViewModel(),
) {
    val eggLogs by viewModel.eggLogs.collectAsState()
    val animals by viewModel.activeAnimals.collectAsState()
    val todayCount by viewModel.todayEggCount.collectAsState()
    val weeklyTotal by viewModel.weeklyTotal.collectAsState()
    val flockSize by viewModel.flockSize.collectAsState()
    val layRate by viewModel.layRatePercent.collectAsState()
    val breedInfoMap by viewModel.breedInfoMap.collectAsState()
    val allBreeds by viewModel.allBreeds.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val dailyCounts by viewModel.dailyEggCounts.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Egg log", "Flock")
    var showAddAnimalDialog by remember { mutableStateOf(false) }
    var eggLogToDelete by remember { mutableStateOf<EggLog?>(null) }
    var animalToDelete by remember { mutableStateOf<Animal?>(null) }
    var eggLogForAction by remember { mutableStateOf<EggLog?>(null) }
    var animalForAction by remember { mutableStateOf<Animal?>(null) }
    var animalToEdit by remember { mutableStateOf<Animal?>(null) }

    com.furrow.app.ui.components.AppScaffold(
        topBar = {
            com.furrow.app.ui.components.AppTopBar(
                title = "Poultry",
                subtitle = "Egg production and flock records",
                actions = {
                    IconButton(onClick = onReportsClick) {
                        Icon(
                            Icons.Outlined.Assessment,
                            contentDescription = "Reports",
                            tint = TextSecondary,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                com.furrow.app.ui.components.PrimaryButton(
                    text = if (selectedTab == 0) "Log Eggs" else "Add Chicken",
                    onClick = { if (selectedTab == 0) onAddEgg() else showAddAnimalDialog = true },
                )
            }

            // ── Production Summary ──
            if (eggLogs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                com.furrow.app.ui.components.Panel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    com.furrow.app.ui.components.InlineStat(
                        label = "Eggs today",
                        value = todayCount.toString(),
                    )
                    com.furrow.app.ui.components.InlineStat(
                        label = "This week",
                        value = weeklyTotal.toString(),
                    )
                    com.furrow.app.ui.components.InlineStat(
                        label = "Flock size",
                        value = flockSize.toString(),
                    )
                    com.furrow.app.ui.components.InlineStat(
                        label = "Lay rate",
                        value = "${layRate ?: 0}%",
                    )
                }
            }

            // ── 7-Day Chart ──
            val hasChartData = dailyCounts.any { it.count > 0 }
            if (dailyCounts.isNotEmpty() && hasChartData) {
                Spacer(modifier = Modifier.height(12.dp))
                com.furrow.app.ui.components.Panel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
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
                    1 -> AnimalList(
                        animals = animals,
                        breedInfoMap = breedInfoMap,
                        onAnimalClick = onAnimalClick,
                        onAddAnimal = { showAddAnimalDialog = true },
                        onLongPress = { animalForAction = it },
                    )
                }
            }
        }
    }

    // ── Sheets & Dialogs ──

    if (showAddAnimalDialog || animalToEdit != null) {
        AddChickenSheet(
            existingAnimal = animalToEdit,
            breeds = allBreeds,
            zoneGroup = userProfile?.zoneGroup,
            onDismiss = { showAddAnimalDialog = false; animalToEdit = null },
            onSave = { animal ->
                if (animalToEdit != null) viewModel.updateAnimal(animal) else viewModel.addAnimal(animal)
                showAddAnimalDialog = false
                animalToEdit = null
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

    animalToDelete?.let { animal ->
        DeleteConfirmationDialog(
            itemName = animal.name ?: animal.breed,
            onConfirm = { viewModel.deleteAnimal(animal); animalToDelete = null },
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

    animalForAction?.let { animal ->
        ItemActionSheet(
            onDismiss = { animalForAction = null },
            onEdit = { animalToEdit = animal },
            onDelete = { animalToDelete = animal },
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
        ) {
            item {
                com.furrow.app.ui.components.Panel(contentPadding = PaddingValues(0.dp)) {
                    eggLogs.forEachIndexed { index, entry ->
                        com.furrow.app.ui.components.ListRow(
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(entry)
                                },
                            ),
                            title = formatDate(entry.date),
                            subtitle = entry.notes,
                            trailingText = "${entry.count} eggs",
                            showDivider = index != eggLogs.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// ── Flock Tab ──

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AnimalList(
    animals: List<Animal>,
    breedInfoMap: Map<String, AnimalBreedInfo>,
    onAnimalClick: (Long) -> Unit,
    onAddAnimal: () -> Unit,
    onLongPress: (Animal) -> Unit,
) {
    val view = LocalView.current

    if (animals.isEmpty()) {
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
            onAction = onAddAnimal,
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                com.furrow.app.ui.components.Panel(contentPadding = PaddingValues(0.dp)) {
                    animals.forEachIndexed { index, animal ->
                        val breedInfo = breedInfoMap[animal.breed]
                        val displayName = animal.name ?: animal.breed
                        val subtitle = buildString {
                            append("${animal.breed} • ${formatAge(animal.acquisitionDate)}")
                            breedInfo?.let {
                                val eggs = it.eggsPerYear ?: 0
                                if (eggs > 0) append(" • $eggs eggs/yr")
                            }
                        }

                        com.furrow.app.ui.components.ListRow(
                            modifier = Modifier.combinedClickable(
                                onClick = { onAnimalClick(animal.id) },
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(animal)
                                },
                            ),
                            title = displayName,
                            subtitle = subtitle,
                            trailing = {
                                com.furrow.app.ui.components.Tag(
                                    text = animal.status.replaceFirstChar { it.uppercase() },
                                    selected = animal.status == "active",
                                )
                            },
                            showDivider = index != animals.lastIndex,
                        )
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
    existingAnimal: Animal? = null,
    breeds: List<AnimalBreedInfo>,
    zoneGroup: String?,
    onDismiss: () -> Unit,
    onSave: (Animal) -> Unit,
    onInsertBreed: (AnimalBreedInfo) -> Unit,
    onUpdateBreed: (AnimalBreedInfo) -> Unit,
    onDeleteBreed: (AnimalBreedInfo) -> Unit,
) {
    val isEditMode = existingAnimal != null
    var name by remember { mutableStateOf(existingAnimal?.name ?: "") }
    var breed by remember { mutableStateOf(existingAnimal?.breed ?: "") }
    var breedQuery by remember { mutableStateOf(existingAnimal?.breed ?: "") }
    var status by remember { mutableStateOf(existingAnimal?.status ?: "active") }
    var notes by remember { mutableStateOf(existingAnimal?.notes ?: "") }
    var dateAcquired by remember { mutableLongStateOf(existingAnimal?.acquisitionDate ?: System.currentTimeMillis()) }
    var showAddCustomBreed by remember { mutableStateOf(false) }
    var customBreedInitialName by remember { mutableStateOf("") }
    var breedToEdit by remember { mutableStateOf<AnimalBreedInfo?>(null) }
    var breedToDelete by remember { mutableStateOf<AnimalBreedInfo?>(null) }

    val filteredBreeds = remember(breeds, breedQuery, zoneGroup) {
        val query = breedQuery.lowercase()
        breeds
            .filter { query.isEmpty() || it.name.lowercase().contains(query) }
            .sortedWith(
                compareBy<AnimalBreedInfo> { b ->
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
                    Animal(
                        id = existingAnimal?.id ?: 0,
                        species = "chicken",
                        name = name.ifBlank { null },
                        breed = breed.trim(),
                        acquisitionDate = dateAcquired,
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
                shape = RoundedCornerShape(8.dp),
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
                            val eggs = b.eggsPerYear ?: 0
                            if (eggs > 0) {
                                Text(
                                    "$eggs eggs/yr",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary,
                                )
                            } else {
                                Text(
                                    (b.purpose ?: "").replaceFirstChar { it.uppercase() },
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
                shape = RoundedCornerShape(8.dp),
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
    existingBreed: AnimalBreedInfo?,
    initialName: String,
    onDismiss: () -> Unit,
    onSave: (AnimalBreedInfo) -> Unit,
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
    var climateNotes by remember { mutableStateOf(existingBreed?.notes ?: "") }

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
                    AnimalBreedInfo(
                        id = existingBreed?.id ?: 0,
                        species = "chicken",
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
                        notes = climateNotes.ifBlank { null },
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
                shape = RoundedCornerShape(8.dp),
            )
            OutlinedTextField(
                value = eggsPerYear,
                onValueChange = { eggsPerYear = it.filter { c -> c.isDigit() } },
                label = { Text("Eggs Per Year") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(8.dp),
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
                shape = RoundedCornerShape(8.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = heatTolerance,
                    onValueChange = { heatTolerance = it.filter { c -> c.isDigit() } },
                    label = { Text("Heat (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(8.dp),
                )
                OutlinedTextField(
                    value = coldTolerance,
                    onValueChange = { coldTolerance = it.filter { c -> c.isDigit() } },
                    label = { Text("Cold (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(8.dp),
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
                shape = RoundedCornerShape(8.dp),
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
