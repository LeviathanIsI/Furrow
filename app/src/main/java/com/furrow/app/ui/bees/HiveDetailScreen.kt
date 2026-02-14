package com.furrow.app.ui.bees

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.BeeRaceInfo
import com.furrow.app.data.local.entity.Inspection
import com.furrow.app.data.local.entity.Treatment
import androidx.compose.material3.ripple
import com.furrow.app.ui.components.DecoratedSectionHeader
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.theme.FurrowBackground
import com.furrow.app.ui.theme.FurrowCardDefaults
import com.furrow.app.ui.theme.LocalFurrowColors
import com.furrow.app.ui.theme.shimmer
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiveDetailScreen(
    onBack: () -> Unit,
    onAddInspection: (Long) -> Unit,
    onAddTreatment: (Long) -> Unit,
    onEditInspection: (Long, Long) -> Unit,
    onEditTreatment: (Long, Long) -> Unit,
    viewModel: BeeViewModel = hiltViewModel(),
) {
    val hive by viewModel.selectedHive.collectAsState()
    val inspections by viewModel.inspections.collectAsState()
    val treatments by viewModel.treatments.collectAsState()
    val queenConfDate by viewModel.latestQueenConfirmation.collectAsState()
    val activeTreatments by viewModel.activeTreatments.collectAsState()
    val raceInfoMap by viewModel.raceInfoMap.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("INSPECTIONS", "TREATMENTS")
    var selectedInspectionId by remember { mutableStateOf<Long?>(null) }
    var inspectionToDelete by remember { mutableStateOf<Inspection?>(null) }
    var treatmentToDelete by remember { mutableStateOf<Treatment?>(null) }
    var inspectionForAction by remember { mutableStateOf<Inspection?>(null) }
    var treatmentForAction by remember { mutableStateOf<Treatment?>(null) }

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
                title = { Text(hive?.name ?: "Hive") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        floatingActionButton = {
            hive?.let { h ->
                FloatingActionButton(
                    onClick = {
                        if (selectedTab == 0) onAddInspection(h.id) else onAddTreatment(h.id)
                    },
                    modifier = Modifier.graphicsLayer {
                        scaleX = fabScale
                        scaleY = fabScale
                    },
                    interactionSource = fabInteraction,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { padding ->
        FurrowBackground(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                hive?.let { h ->
                    // Hive header card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = FurrowCardDefaults.elevatedCardColors,
                        elevation = FurrowCardDefaults.elevatedCardElevation,
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(h.name, style = MaterialTheme.typography.headlineSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                DetailChip("Queen: ${h.queenStatus}")
                                DetailChip("Source: ${h.source}")
                                h.beeRace?.let { DetailChip("Race: $it") }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Installed ${formatDate(h.installDate)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            // Queen confirmation
                            queenConfDate?.let { date ->
                                Text(
                                    "Queen confirmed: ${formatDate(date)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            h.notes?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    // Breed characteristics card
                    val raceInfo = h.beeRace?.let { raceInfoMap[it] }
                    if (raceInfo != null) {
                        BreedCharacteristicsCard(
                            race = raceInfo,
                            zoneGroup = userProfile?.zoneGroup,
                        )
                    }

                    // Inspection timeline
                    if (inspections.isNotEmpty()) {
                        InspectionTimeline(
                            inspections = inspections,
                            selectedId = selectedInspectionId,
                            onSelect = { id ->
                                selectedInspectionId = if (selectedInspectionId == id) null else id
                            },
                        )
                        // Selected inspection summary
                        val selected = inspections.find { it.id == selectedInspectionId }
                        AnimatedVisibility(visible = selected != null) {
                            selected?.let { InspectionSummaryCard(it) }
                        }
                    }

                    // Active treatments
                    if (activeTreatments.isNotEmpty()) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            activeTreatments.forEach { treatment ->
                                ActiveTreatmentCard(treatment)
                            }
                        }
                    }
                }

                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                val count = if (index == 0) inspections.size else treatments.size
                                Text(
                                    "$title ($count)",
                                    letterSpacing = 1.2.sp,
                                )
                            }
                        )
                    }
                }

                when (selectedTab) {
                    0 -> InspectionList(inspections, onLongPress = { inspectionForAction = it })
                    1 -> TreatmentList(treatments, onLongPress = { treatmentForAction = it })
                }
            }
        }
    }

    inspectionToDelete?.let { inspection ->
        DeleteConfirmationDialog(
            itemName = "inspection from ${formatDate(inspection.date)}",
            onConfirm = { viewModel.deleteInspection(inspection); inspectionToDelete = null },
            onDismiss = { inspectionToDelete = null },
        )
    }

    treatmentToDelete?.let { treatment ->
        DeleteConfirmationDialog(
            itemName = treatment.type.replaceFirstChar { it.uppercase() },
            onConfirm = { viewModel.deleteTreatment(treatment); treatmentToDelete = null },
            onDismiss = { treatmentToDelete = null },
        )
    }

    inspectionForAction?.let { inspection ->
        ItemActionSheet(
            onDismiss = { inspectionForAction = null },
            onEdit = { hive?.let { h -> onEditInspection(h.id, inspection.id) } },
            onDelete = { inspectionToDelete = inspection },
        )
    }

    treatmentForAction?.let { treatment ->
        ItemActionSheet(
            onDismiss = { treatmentForAction = null },
            onEdit = { hive?.let { h -> onEditTreatment(h.id, treatment.id) } },
            onDelete = { treatmentToDelete = treatment },
        )
    }
}

// ── Breed Characteristics Card ──

@Composable
private fun BreedCharacteristicsCard(race: BeeRaceInfo, zoneGroup: String?) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = FurrowCardDefaults.outlinedCardColors,
        border = FurrowCardDefaults.outlinedCardBorder,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            val beeAccent = LocalFurrowColors.current.beeAccent
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DecoratedSectionHeader(
                    title = "Breed Characteristics",
                    accentColor = beeAccent,
                    modifier = Modifier.weight(1f),
                )
                if (zoneGroup != null) {
                    val badge = climateBadgeFor(race, zoneGroup)
                    val badgeColor = climateBadgeColor(badge)
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = badgeColor.copy(alpha = 0.12f),
                    ) {
                        Text(
                            badge.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rating rows
            RatingRow("Honey Production", race.honeyProduction, MaterialTheme.colorScheme.tertiary)
            RatingRow("Mite Resistance", race.miteResistance, MaterialTheme.colorScheme.primary)
            RatingRow("Swarming Tendency", race.swarmingTendency, MaterialTheme.colorScheme.error)
            RatingRow("Overwintering", race.overwinteringAbility, MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(8.dp))

            // Text details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Temperament",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(race.temperament, style = MaterialTheme.typography.bodySmall)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Climate",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        race.climateSuitability,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            race.notes?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun RatingRow(label: String, rating: Int, activeColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = if (index < rating) activeColor
                            else activeColor.copy(alpha = 0.15f),
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}

// ── Existing composables ──

@Composable
private fun InspectionTimeline(
    inspections: List<Inspection>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
) {
    val sorted = remember(inspections) { inspections.sortedBy { it.date } }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = FurrowCardDefaults.elevatedCardColors,
        elevation = FurrowCardDefaults.elevatedCardElevation,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            DecoratedSectionHeader(
                title = "Inspection History",
                accentColor = LocalFurrowColors.current.beeAccent,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
            ) {
                itemsIndexed(sorted, key = { _, i -> i.id }) { _, inspection ->
                    val color = inspectionHealthColor(inspection)
                    val isSelected = inspection.id == selectedId
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 20.dp else 16.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) color else color.copy(alpha = 0.6f),
                                CircleShape,
                            )
                            .clickable { onSelect(inspection.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun inspectionHealthColor(inspection: Inspection): Color {
    val goodSignals = listOfNotNull(
        if (inspection.queenSeen) true else null,
        if (inspection.broodPattern == "solid") true else null,
        if (inspection.honeyStores == "heavy" || inspection.honeyStores == "moderate") true else null,
        if (inspection.eggsLarvae) true else null,
    ).size
    val badSignals = listOfNotNull(
        if (!inspection.queenSeen) true else null,
        if (inspection.queenCells) true else null,
        if (inspection.broodPattern == "none") true else null,
        if (inspection.honeyStores == "none") true else null,
        if (!inspection.pestsSigns.isNullOrBlank()) true else null,
        if (!inspection.diseasesSigns.isNullOrBlank()) true else null,
    ).size

    return when {
        badSignals >= 3 -> MaterialTheme.colorScheme.error
        goodSignals >= 3 -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.tertiary
    }
}

@Composable
private fun InspectionSummaryCard(inspection: Inspection) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = FurrowCardDefaults.outlinedCardColors,
        border = FurrowCardDefaults.outlinedCardBorder,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(formatDate(inspection.date), style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (inspection.queenSeen) Text(
                    "Queen \u2713",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                inspection.broodPattern?.let {
                    Text(
                        "Brood: $it",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
                inspection.honeyStores?.let {
                    Text(
                        "Honey: $it",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
            inspection.temperament?.let {
                Text(
                    "Temperament: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            inspection.notes?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ActiveTreatmentCard(treatment: Treatment) {
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val startDate = Instant.ofEpochMilli(treatment.date).atZone(zone).toLocalDate()
    val daysRemaining = treatment.endDate?.let {
        val endDate = Instant.ofEpochMilli(it).atZone(zone).toLocalDate()
        ChronoUnit.DAYS.between(today, endDate).coerceAtLeast(0)
    }
    val progress = treatment.endDate?.let {
        val totalDays = ChronoUnit.DAYS.between(startDate,
            Instant.ofEpochMilli(it).atZone(zone).toLocalDate()).toFloat()
        val elapsedDays = ChronoUnit.DAYS.between(startDate, today).toFloat()
        if (totalDays > 0) (elapsedDays / totalDays).coerceIn(0f, 1f) else null
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = FurrowCardDefaults.outlinedCardColors,
        border = FurrowCardDefaults.outlinedCardBorder,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Treatment active: ${treatment.type.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                daysRemaining?.let {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    ) {
                        Text(
                            "$it days left",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }
            progress?.let {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { it },
                    modifier = Modifier.fillMaxWidth().height(6.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                )
            }
        }
    }
}

@Composable
private fun DetailChip(text: String) {
    Text(
        text = text.replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InspectionList(inspections: List<Inspection>, onLongPress: (Inspection) -> Unit) {
    val view = LocalView.current
    if (inspections.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .shimmer(),
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No inspections yet",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "Tap + to log your first inspection",
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
            items(inspections, key = { it.id }) { inspection ->
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                onLongPress(inspection)
                            },
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() },
                        ),
                    colors = FurrowCardDefaults.outlinedCardColors,
                    border = FurrowCardDefaults.outlinedCardBorder,
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(formatDate(inspection.date), style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (inspection.queenSeen) Text(
                                "Queen \u2713",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            inspection.broodPattern?.let {
                                Text(
                                    "Brood: $it",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            }
                            inspection.honeyStores?.let {
                                Text(
                                    "Honey: $it",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.tertiary,
                                )
                            }
                        }
                        inspection.temperament?.let {
                            Text(
                                "Temperament: $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        inspection.notes?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TreatmentList(treatments: List<Treatment>, onLongPress: (Treatment) -> Unit) {
    val view = LocalView.current
    if (treatments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .shimmer(),
            ) {
                Icon(
                    Icons.Outlined.Vaccines,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No treatments yet",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "Tap + to log a treatment",
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
            items(treatments, key = { it.id }) { treatment ->
                val nowMillis = Instant.now().toEpochMilli()
                val isActive = treatment.endDate == null || treatment.endDate > nowMillis
                val statusColor = if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                onLongPress(treatment)
                            },
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() },
                        ),
                    colors = FurrowCardDefaults.outlinedCardColors,
                    border = FurrowCardDefaults.outlinedCardBorder,
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                treatment.type.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                if (isActive) "Active" else "Completed",
                                style = MaterialTheme.typography.labelMedium,
                                color = statusColor,
                            )
                        }
                        Text(
                            formatDate(treatment.date),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        treatment.method?.let {
                            Text("Method: $it", style = MaterialTheme.typography.bodySmall)
                        }
                        treatment.dose?.let {
                            Text("Dose: $it", style = MaterialTheme.typography.bodySmall)
                        }
                        treatment.endDate?.let {
                            Text(
                                "Until ${formatDate(it)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        treatment.notes?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
