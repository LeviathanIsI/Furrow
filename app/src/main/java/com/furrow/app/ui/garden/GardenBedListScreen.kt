package com.furrow.app.ui.garden

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.GardenBed
import com.furrow.app.ui.bees.DropdownSelector
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.FurrowBottomSheet
import com.furrow.app.ui.components.ItemActionSheet
import androidx.compose.foundation.BorderStroke
import com.furrow.app.ui.theme.CardBorderDark
import com.furrow.app.ui.theme.FurrowBackground
import com.furrow.app.ui.theme.LocalFurrowColors
import com.furrow.app.ui.theme.shimmer
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenBedListScreen(
    onBedClick: (Long) -> Unit,
    onReportsClick: () -> Unit,
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val beds by viewModel.activeBeds.collectAsState()
    val plantingCounts by viewModel.activePlantingCounts.collectAsState()
    val plantingsByBed by viewModel.activePlantingsByBed.collectAsState()

    val furrowColors = LocalFurrowColors.current
    var showAddDialog by remember { mutableStateOf(false) }
    var bedToDelete by remember { mutableStateOf<GardenBed?>(null) }
    var bedForAction by remember { mutableStateOf<GardenBed?>(null) }
    var bedToEdit by remember { mutableStateOf<GardenBed?>(null) }

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
                title = { Text("Garden") },
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
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Bed") },
                modifier = Modifier.graphicsLayer {
                    scaleX = fabScale
                    scaleY = fabScale
                },
                interactionSource = fabInteraction,
            )
        }
    ) { padding ->
        FurrowBackground(modifier = Modifier.fillMaxSize().padding(padding)) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { isRefreshing = true },
                modifier = Modifier.fillMaxSize(),
            ) {
                if (beds.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .shimmer(),
                        ) {
                            Icon(
                                Icons.Filled.Grass,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No garden beds yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Tap \"Add Bed\" to get started",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(beds, key = { it.id }) { bed ->
                            BedCard(
                                bed = bed,
                                activePlantings = plantingCounts[bed.id] ?: 0,
                                plantingSummaries = plantingsByBed[bed.id] ?: emptyList(),
                                accentColor = furrowColors.gardenAccent,
                                onClick = { onBedClick(bed.id) },
                                onLongClick = { bedForAction = bed },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog || bedToEdit != null) {
        AddBedSheet(
            existingBed = bedToEdit,
            onDismiss = { showAddDialog = false; bedToEdit = null },
            onSave = { bed ->
                if (bedToEdit != null) viewModel.updateBed(bed) else viewModel.addBed(bed)
                showAddDialog = false
                bedToEdit = null
            },
        )
    }

    bedToDelete?.let { bed ->
        DeleteConfirmationDialog(
            itemName = bed.name,
            onConfirm = { viewModel.deleteBed(bed); bedToDelete = null },
            onDismiss = { bedToDelete = null },
        )
    }

    bedForAction?.let { bed ->
        ItemActionSheet(
            onDismiss = { bedForAction = null },
            onEdit = { bedToEdit = bed },
            onDelete = { bedToDelete = bed },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun BedCard(
    bed: GardenBed,
    activePlantings: Int,
    plantingSummaries: List<PlantingSummary>,
    accentColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val view = LocalView.current

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(0.5.dp, CardBorderDark.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
                onLongClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onLongClick()
                },
            ),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Accent strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor),
            )
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        bed.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = accentColor.copy(alpha = 0.12f),
                        ) {
                            Text(
                                bed.type.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                        bed.sunExposure?.let {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                            ) {
                                Text(
                                    it.replaceFirstChar { c -> c.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "$activePlantings active planting${if (activePlantings != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (plantingSummaries.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        plantingSummaries.forEach { summary ->
                            PlantingChip(summary.name, growthEmoji(summary.status))
                        }
                    }
                }
            }
        }
    }
}

private fun growthEmoji(status: String): String = when (status) {
    "growing" -> "\uD83C\uDF31"   // 🌱
    "producing" -> "\uD83C\uDF45" // 🍅
    "finished" -> "\u2705"         // ✅
    else -> "\uD83C\uDF3F"         // 🌿
}

@Composable
private fun PlantingChip(name: String, emoji: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
    ) {
        Text(
            "$emoji $name",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun AddBedSheet(existingBed: GardenBed? = null, onDismiss: () -> Unit, onSave: (GardenBed) -> Unit) {
    val isEditMode = existingBed != null
    var name by remember { mutableStateOf(existingBed?.name ?: "") }
    var type by remember { mutableStateOf(existingBed?.type ?: "raised bed") }
    var sizeGallons by remember { mutableStateOf(existingBed?.sizeGallons?.toString() ?: "") }
    var lengthFt by remember { mutableStateOf(existingBed?.lengthFt?.toString() ?: "") }
    var widthFt by remember { mutableStateOf(existingBed?.widthFt?.toString() ?: "") }
    var sunExposure by remember { mutableStateOf(existingBed?.sunExposure ?: "full sun") }
    var soilType by remember { mutableStateOf(existingBed?.soilType ?: "") }
    var notes by remember { mutableStateOf(existingBed?.notes ?: "") }

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = if (isEditMode) "Edit Bed" else "Add Bed",
        confirmText = "Save",
        confirmEnabled = name.isNotBlank(),
        onConfirm = {
            if (name.isNotBlank()) {
                onSave(
                    GardenBed(
                        id = existingBed?.id ?: 0,
                        name = name.trim(),
                        type = type,
                        sizeGallons = sizeGallons.toIntOrNull(),
                        lengthFt = lengthFt.toDoubleOrNull(),
                        widthFt = widthFt.toDoubleOrNull(),
                        sunExposure = sunExposure,
                        soilType = soilType.ifBlank { null },
                        notes = notes.ifBlank { null },
                    )
                )
            }
        },
        content = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            DropdownSelector(
                label = "Type",
                options = listOf("raised bed", "grow bag", "in-ground", "container"),
                selected = type,
                onSelect = { type = it },
            )
            if (type == "grow bag" || type == "container") {
                OutlinedTextField(
                    value = sizeGallons,
                    onValueChange = { sizeGallons = it.filter { c -> c.isDigit() } },
                    label = { Text("Size (gallons)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = lengthFt,
                        onValueChange = { lengthFt = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Length (ft)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = widthFt,
                        onValueChange = { widthFt = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Width (ft)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
            }
            DropdownSelector(
                label = "Sun Exposure",
                options = listOf("full sun", "partial sun", "partial shade", "full shade"),
                selected = sunExposure,
                onSelect = { sunExposure = it },
            )
            OutlinedTextField(
                value = soilType,
                onValueChange = { soilType = it },
                label = { Text("Soil Type (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
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
}
