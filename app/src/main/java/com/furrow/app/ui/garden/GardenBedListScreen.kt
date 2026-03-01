package com.furrow.app.ui.garden

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.GardenBed
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.util.DateUtil
import com.furrow.app.util.displayFormat
import com.furrow.app.util.filterDecimal
import com.furrow.app.util.filterInteger
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.ToggleRow
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTextField
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.FurrowBottomSheet
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.SearchField
import com.furrow.app.ui.components.Tag
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary

@Composable
fun GardenBedListScreen(
    onBedClick: (Long) -> Unit,
    onReportsClick: () -> Unit,
    onCalendarClick: () -> Unit = {},
    onSeasonPlannerClick: () -> Unit = {},
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val beds by viewModel.activeBeds.collectAsState()
    val plantingCounts by viewModel.activePlantingCounts.collectAsState()
    val plannedPlantings by viewModel.plannedPlantings.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var bedToDelete by remember { mutableStateOf<GardenBed?>(null) }
    var bedForAction by remember { mutableStateOf<GardenBed?>(null) }
    var bedToEdit by remember { mutableStateOf<GardenBed?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(
                title = "Garden",
                subtitle = "Beds and current workload",
                actions = {
                    IconButton(onClick = onSeasonPlannerClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.EventNote,
                            contentDescription = "Season Planner",
                            tint = TextSecondary,
                        )
                    }
                    IconButton(onClick = onCalendarClick) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = TextSecondary,
                        )
                    }
                    IconButton(onClick = onReportsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Assessment,
                            contentDescription = "Reports",
                            tint = TextSecondary,
                        )
                    }
                },
            )
        },
    ) { padding ->
        if (beds == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@AppScaffold
        }
        val bedList = beds!!
        val filteredBeds = remember(bedList, searchQuery) {
            if (searchQuery.isBlank()) bedList
            else bedList.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
        if (bedList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                EmptyState(
                    title = "No garden beds yet",
                    subtitle = "Create a bed to start tracking plantings and harvests.",
                    actionLabel = "Add Bed",
                    onAction = { showAddDialog = true },
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
                item(key = "toolbar_actions") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        PrimaryButton(
                            text = "Add Bed",
                            onClick = { showAddDialog = true },
                        )
                    }
                }

                if (bedList.size > 5) {
                    item(key = "search") {
                        SearchField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            accentColor = GardenGlow,
                        )
                    }
                }

                if (filteredBeds.isEmpty()) {
                    item(key = "empty_search") {
                        EmptyState(
                            title = "No beds match \"$searchQuery\"",
                            subtitle = "Try a different search term.",
                        )
                    }
                } else {
                    item(key = "beds_panel") {
                        Panel(contentPadding = PaddingValues(0.dp)) {
                            filteredBeds.forEachIndexed { index, bed ->
                                ListRow(
                                    title = bed.name,
                                    subtitle = buildString {
                                        append(bed.type.displayFormat())
                                        bed.sunExposure?.let {
                                            append(" • ")
                                            append(it.displayFormat())
                                        }
                                        if (!bed.checkCompanionPlanting) {
                                            append(" • No companion checks")
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Outlined.Grass,
                                            contentDescription = "Garden bed",
                                            tint = TextSecondary,
                                        )
                                    },
                                    trailing = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                                        ) {
                                            Text(
                                                text = "${plantingCounts[bed.id] ?: 0}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = TextTertiary,
                                            )
                                            IconButton(onClick = { bedForAction = bed }) {
                                                Icon(
                                                    imageVector = Icons.Filled.MoreVert,
                                                    contentDescription = "Manage",
                                                    tint = TextTertiary,
                                                )
                                            }
                                            Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                contentDescription = "View details",
                                                tint = TextTertiary,
                                            )
                                        }
                                    },
                                    onClick = { onBedClick(bed.id) },
                                    showDivider = index != filteredBeds.lastIndex,
                                )
                            }
                        }
                    }
                }

                if (plannedPlantings.isNotEmpty() && searchQuery.isBlank()) {
                    item(key = "planned_header") {
                        Text(
                            text = "Planned (${plannedPlantings.size})",
                            style = MaterialTheme.typography.titleSmall,
                            color = GardenGlow,
                        )
                    }

                    item(key = "planned_panel") {
                        val bedMap = bedList.associateBy { it.id }
                        Panel(contentPadding = PaddingValues(0.dp)) {
                            plannedPlantings.forEachIndexed { index, planting ->
                                val bedName = bedMap[planting.bedId]?.name ?: "Bed"
                                val targetStr = planting.targetPlantDate?.let {
                                    DateUtil.formatDate(it, viewModel.zone)
                                } ?: ""
                                ListRow(
                                    title = planting.plantName,
                                    subtitle = "$bedName · $targetStr",
                                    trailing = {
                                        PrimaryButton(
                                            text = "Plant",
                                            onClick = { viewModel.plantNow(planting) },
                                        )
                                    },
                                    showDivider = index != plannedPlantings.lastIndex,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog || bedToEdit != null) {
        AddBedSheet(
            existingBed = bedToEdit,
            onDismiss = {
                showAddDialog = false
                bedToEdit = null
            },
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

@Composable
private fun AddBedSheet(
    existingBed: GardenBed? = null,
    onDismiss: () -> Unit,
    onSave: (GardenBed) -> Unit,
) {
    val isEditMode = existingBed != null
    var name by remember { mutableStateOf(existingBed?.name ?: "") }
    var type by remember { mutableStateOf(existingBed?.type ?: "Raised Bed") }
    var sizeGallons by remember { mutableStateOf(existingBed?.sizeGallons?.toString() ?: "") }
    var lengthFt by remember { mutableStateOf(existingBed?.lengthFt?.toString() ?: "") }
    var widthFt by remember { mutableStateOf(existingBed?.widthFt?.toString() ?: "") }
    var sunExposure by remember { mutableStateOf(existingBed?.sunExposure ?: "Full Sun") }
    var soilType by remember { mutableStateOf(existingBed?.soilType ?: "") }
    var notes by remember { mutableStateOf(existingBed?.notes ?: "") }
    var checkCompanionPlanting by remember { mutableStateOf(existingBed?.checkCompanionPlanting ?: true) }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = GardenGlow)

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
                        checkCompanionPlanting = checkCompanionPlanting,
                    ),
                )
            }
        },
        content = {
            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
            )
            DropdownSelector(
                label = "Type",
                options = listOf("Raised Bed", "Grow Bag", "In-Ground", "Container", "Mixed"),
                selected = type,
                onSelect = { type = it },
                accentColor = GardenGlow,
            )
            Column {
                ToggleRow(
                    label = "Check companion planting",
                    checked = checkCompanionPlanting,
                    onCheckedChange = { checkCompanionPlanting = it },
                    accentColor = GardenGlow,
                )
                Text(
                    text = "Disable for large areas where plants aren't next to each other",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
            if (type == "Grow Bag" || type == "Container") {
                AppTextField(
                    value = sizeGallons,
                    onValueChange = { sizeGallons = it.filterInteger() },
                    label = { Text("Size (gallons)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors,
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    AppTextField(
                        value = lengthFt,
                        onValueChange = { lengthFt = it.filterDecimal() },
                        label = { Text("Length (ft)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = fieldColors,
                    )
                    AppTextField(
                        value = widthFt,
                        onValueChange = { widthFt = it.filterDecimal() },
                        label = { Text("Width (ft)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = fieldColors,
                    )
                }
            }
            DropdownSelector(
                label = "Sun Exposure",
                options = listOf("Full Sun", "Partial Sun", "Partial Shade", "Full Shade"),
                selected = sunExposure,
                onSelect = { sunExposure = it },
                accentColor = GardenGlow,
            )
            AppTextField(
                value = soilType,
                onValueChange = { soilType = it },
                label = { Text("Soil Type (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
            )
            AppTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = fieldColors,
            )
        },
    )
}
