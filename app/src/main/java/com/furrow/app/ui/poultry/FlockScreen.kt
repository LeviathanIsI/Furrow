package com.furrow.app.ui.poultry

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.EggAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.Chicken
import com.furrow.app.data.local.entity.ChickenBreedInfo
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.ui.bees.DropdownSelector
import com.furrow.app.ui.components.DecoratedSectionHeader
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.FurrowBottomSheet
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.SearchableSelector
import com.furrow.app.ui.theme.CardBorderDark
import com.furrow.app.ui.theme.FurrowBackground
import com.furrow.app.ui.theme.LocalFurrowColors
import com.furrow.app.ui.theme.glowBorder
import com.furrow.app.ui.theme.shimmer
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

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
    val dailyAvg by viewModel.dailyAvg.collectAsState()
    val expectedWeekly by viewModel.expectedWeeklyEggs.collectAsState()
    val breedInfoMap by viewModel.breedInfoMap.collectAsState()
    val allBreeds by viewModel.allBreeds.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val dailyCounts by viewModel.dailyEggCounts.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("EGG LOG", "FLOCK")
    var showAddChickenDialog by remember { mutableStateOf(false) }
    val furrowColors = LocalFurrowColors.current
    var eggLogToDelete by remember { mutableStateOf<EggLog?>(null) }
    var chickenToDelete by remember { mutableStateOf<Chicken?>(null) }
    var eggLogForAction by remember { mutableStateOf<EggLog?>(null) }
    var chickenForAction by remember { mutableStateOf<Chicken?>(null) }
    var chickenToEdit by remember { mutableStateOf<Chicken?>(null) }

    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(500)
            isRefreshing = false
        }
    }

    val fabInteraction = remember { MutableInteractionSource() }
    val isFabPressed by fabInteraction.collectIsPressedAsState()
    val fabScale by animateFloatAsState(
        targetValue = if (isFabPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "fabScale",
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Poultry") },
                actions = {
                    IconButton(onClick = onReportsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Assessment,
                            contentDescription = "Reports",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = onAddEgg,
                    icon = { Icon(Icons.Filled.Egg, contentDescription = null) },
                    text = { Text("Log Eggs") },
                    modifier = Modifier.graphicsLayer {
                        scaleX = fabScale
                        scaleY = fabScale
                    },
                    interactionSource = fabInteraction,
                )
            } else {
                FloatingActionButton(
                    onClick = { showAddChickenDialog = true },
                    modifier = Modifier.graphicsLayer {
                        scaleX = fabScale
                        scaleY = fabScale
                    },
                    interactionSource = fabInteraction,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Chicken")
                }
            }
        }
    ) { padding ->
        FurrowBackground(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Stats
                val poultryAccentColor = furrowColors.poultryAccent
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = BorderStroke(0.5.dp, CardBorderDark.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .glowBorder(poultryAccentColor),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                drawCircle(
                                    color = poultryAccentColor.copy(alpha = 0.08f),
                                    radius = size.maxDimension * 0.5f,
                                    center = Offset(size.width * 0.8f, size.height * 0.2f),
                                )
                            },
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // Hero: today's eggs
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(3.dp, 36.dp)
                                        .background(furrowColors.poultryAccent, MaterialTheme.shapes.extraSmall),
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "TODAY'S EGGS",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        letterSpacing = 1.sp,
                                    )
                                    Text(
                                        "$flockSize birds in flock",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Text(
                                    "$todayCount",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = furrowColors.poultryAccent,
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f),
                            )
                            // This week
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(3.dp, 36.dp)
                                        .background(furrowColors.poultryAccent, MaterialTheme.shapes.extraSmall),
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "THIS WEEK",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        letterSpacing = 1.sp,
                                    )
                                    Text(
                                        "Avg ${String.format(Locale.US, "%.1f", dailyAvg)}/day",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "$weeklyTotal",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = furrowColors.poultryAccent,
                                    )
                                    Text(
                                        "eggs",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                        // Lay Rate Ring Chart
                        if (layRate != null) {
                            LayRateRing(
                                layRate = layRate,
                                accentColor = furrowColors.poultryAccent,
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .align(Alignment.CenterVertically),
                            )
                        }
                    }
                }

                // 7-day egg chart
                if (dailyCounts.isNotEmpty()) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = BorderStroke(0.5.dp, CardBorderDark.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            DecoratedSectionHeader(
                                title = "7-Day Eggs",
                                accentColor = furrowColors.poultryAccent,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            WeeklyEggChart(
                                dailyCounts = dailyCounts,
                                accentColor = furrowColors.poultryAccent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                            )
                        }
                    }
                }

                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                val count = if (index == 0) eggLogs.size else chickens.size
                                Text(
                                    "$title ($count)",
                                    letterSpacing = 1.2.sp,
                                )
                            }
                        )
                    }
                }

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { isRefreshing = true },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ) {
                    when (selectedTab) {
                        0 -> EggLogList(eggLogs, flockSize, onLongPress = { eggLogForAction = it })
                        1 -> ChickenList(chickens, breedInfoMap, userProfile?.zoneGroup, onChickenClick, onLongPress = { chickenForAction = it })
                    }
                }
            }
        }
    }

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EggLogList(eggLogs: List<EggLog>, flockSize: Int, onLongPress: (EggLog) -> Unit) {
    val view = LocalView.current
    if (eggLogs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .shimmer(),
            ) {
                Icon(
                    Icons.Outlined.EggAlt,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No egg logs yet",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "Tap \"Log Eggs\" to record today's count",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(eggLogs, key = { it.id }) { entry ->
                val dotColor = when {
                    entry.count == 0 -> MaterialTheme.colorScheme.error
                    flockSize > 0 && entry.count >= flockSize -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.tertiary
                }

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = BorderStroke(0.5.dp, CardBorderDark.copy(alpha = 0.4f)),
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
                        ),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        // Colored dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(dotColor, CircleShape),
                        )
                        // Date + note icon
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                formatDate(entry.date),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            if (entry.notes != null) {
                                Icon(
                                    Icons.AutoMirrored.Filled.StickyNote2,
                                    contentDescription = "Has notes",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                )
                            }
                        }
                        // Count
                        Text(
                            "${entry.count} eggs",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChickenList(
    chickens: List<Chicken>,
    breedInfoMap: Map<String, ChickenBreedInfo>,
    zoneGroup: String?,
    onChickenClick: (Long) -> Unit,
    onLongPress: (Chicken) -> Unit,
) {
    val view = LocalView.current
    if (chickens.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .shimmer(),
            ) {
                Icon(
                    Icons.Filled.Egg,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No chickens yet",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "Tap + to add your first bird",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(chickens, key = { it.id }) { chicken ->
                val breedInfo = breedInfoMap[chicken.breed]
                val poultryAccent = LocalFurrowColors.current.poultryAccent

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = BorderStroke(0.5.dp, CardBorderDark.copy(alpha = 0.4f)),
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
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(poultryAccent),
                        )
                        Column(modifier = Modifier.weight(1f).padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val displayName = chicken.name ?: chicken.breed
                                val avatarColor = avatarColorFor(displayName)
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(avatarColor, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        displayName.first().uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.surface,
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        displayName,
                                        style = MaterialTheme.typography.titleSmall,
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        if (chicken.name != null) {
                                            Text(
                                                chicken.breed,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        Text(
                                            formatAge(chicken.dateAcquired),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        )
                                    }
                                }
                                StatusBadge(chicken.status)
                            }
                            if (breedInfo != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (breedInfo.eggsPerYear > 0) {
                                        Text(
                                            "${breedInfo.eggsPerYear} eggs/yr expected",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    } else {
                                        Text(
                                            breedInfo.purpose.replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    ClimateBadgePill(climateBadgeFor(breedInfo, zoneGroup))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClimateBadgePill(badge: ClimateBadge) {
    val color = when (badge) {
        ClimateBadge.GREAT -> MaterialTheme.colorScheme.primary
        ClimateBadge.MANAGEABLE -> MaterialTheme.colorScheme.tertiary
        ClimateBadge.NOT_IDEAL -> MaterialTheme.colorScheme.error
    }
    Surface(
        shape = MaterialTheme.shapes.small,
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

@Composable
private fun StatusBadge(status: String) {
    val (label, color) = when (status) {
        "active" -> "Active" to MaterialTheme.colorScheme.primary
        "deceased" -> "Deceased" to MaterialTheme.colorScheme.error
        "rehomed" -> "Rehomed" to MaterialTheme.colorScheme.onSurfaceVariant
        "processed" -> "Processed" to MaterialTheme.colorScheme.onSurfaceVariant
        else -> status.replaceFirstChar { it.uppercase() } to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.12f),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

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
                }.thenBy { it.name }
            )
    }

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = if (isEditMode) "Edit Chicken" else "Add Chicken",
        confirmText = "Save",
        confirmEnabled = breed.isNotBlank(),
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
                    )
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
                nameSelector = { it.name },
                isCustom = { it.isCustom },
                onAddCustom = { customName ->
                    customBreedInitialName = customName
                    showAddCustomBreed = true
                },
                onEditCustom = { b ->
                    breedToEdit = b
                },
                onDeleteCustom = { b ->
                    breedToDelete = b
                },
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
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } else {
                                Text(
                                    b.purpose.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
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
                if (breedToEdit != null) {
                    onUpdateBreed(b)
                } else {
                    onInsertBreed(b)
                }
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

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = if (existingBreed != null) "Edit Custom Breed" else "Add Custom Breed",
        confirmText = "Save",
        confirmEnabled = breedName.isNotBlank(),
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
                    )
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
            )
            OutlinedTextField(
                value = eggsPerYear,
                onValueChange = { eggsPerYear = it.filter { c -> c.isDigit() } },
                label = { Text("Eggs Per Year") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            DropdownSelector(
                label = "Egg Color",
                options = listOf("brown", "white", "blue", "green", "cream", "tinted"),
                selected = eggColor,
                onSelect = { eggColor = it },
            )
            DropdownSelector(
                label = "Purpose",
                options = listOf("dual-purpose", "egg", "meat", "ornamental"),
                selected = purpose,
                onSelect = { purpose = it },
            )
            OutlinedTextField(
                value = temperament,
                onValueChange = { temperament = it },
                label = { Text("Temperament") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = heatTolerance,
                    onValueChange = { heatTolerance = it.filter { c -> c.isDigit() } },
                    label = { Text("Heat (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = coldTolerance,
                    onValueChange = { coldTolerance = it.filter { c -> c.isDigit() } },
                    label = { Text("Cold (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }
            DropdownSelector(
                label = "Broodiness",
                options = listOf("low", "moderate", "high"),
                selected = broodiness,
                onSelect = { broodiness = it },
            )
            OutlinedTextField(
                value = climateNotes,
                onValueChange = { climateNotes = it },
                label = { Text("Climate Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
        },
    )
}

@Composable
private fun WeeklyEggChart(
    dailyCounts: List<DailyEggCount>,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val accentFaded = accentColor.copy(alpha = 0.35f)
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val dashColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val countStyle = TextStyle(fontSize = 10.sp, color = onSurface, textAlign = TextAlign.Center)
    val labelStyle = TextStyle(fontSize = 10.sp, color = onSurfaceVariant, textAlign = TextAlign.Center)
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

        // Dashed "no data" line at zero
        val hasAnyData = dailyCounts.any { it.count > 0 }
        if (!hasAnyData) {
            val midY = topPadPx + maxBarHeight / 2
            val dashWidth = 6.dp.toPx()
            val dashGap = 4.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = dashColor,
                    start = Offset(x, midY),
                    end = Offset((x + dashWidth).coerceAtMost(size.width), midY),
                    strokeWidth = 1.dp.toPx(),
                )
                x += dashWidth + dashGap
            }
        }

        dailyCounts.forEachIndexed { index, day ->
            val barHeight = if (maxCount > 0) (day.count.toFloat() / maxCount) * maxBarHeight else 0f
            val x = gap + index * (barWidth + gap)
            val y = topPadPx + maxBarHeight - barHeight
            val barColor = if (day.isToday) accentColor else accentFaded

            if (barHeight > 0f) {
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                )
            }

            // Count label above bar
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
            val dayLayout = textMeasurer.measure(day.dayLabel, labelStyle)
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

@Composable
private fun LayRateRing(
    layRate: Int?,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val rate = layRate ?: 0
    val sweepAngle = rate * 3.6f
    val trackColor = accentColor.copy(alpha = 0.12f)
    val ringColor = when {
        layRate == null -> accentColor.copy(alpha = 0.3f)
        rate >= 70 -> accentColor
        rate >= 40 -> Color(0xFFDEC057)
        else -> Color(0xFFFFB4AB)
    }

    Box(modifier = modifier.size(100.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(100.dp)) {
            val strokeWidth = 10.dp.toPx()
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val arcOffset = Offset(strokeWidth / 2, strokeWidth / 2)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            if (sweepAngle > 0f) {
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = arcOffset,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (layRate != null) "$layRate%" else "--",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ringColor,
            )
            Text(
                text = "lay rate",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private val avatarColors = listOf(
    Color(0xFFE67E22), // warm orange
    Color(0xFF8D6E63), // brown
    Color(0xFFF39C12), // amber
    Color(0xFF27AE60), // green
    Color(0xFF2980B9), // blue
    Color(0xFFC0392B), // red
    Color(0xFF8E44AD), // purple
    Color(0xFF16A085), // teal
)

private fun avatarColorFor(name: String): Color {
    val hash = name.fold(0) { acc, c -> acc * 31 + c.code }
    return avatarColors[(hash and 0x7FFFFFFF) % avatarColors.size]
}

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
