package com.furrow.app.ui.garden

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.GardenBed
import com.furrow.app.ui.bees.DropdownSelector
import com.furrow.app.ui.components.AppCard
import com.furrow.app.ui.components.AppChip
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTextField
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.FurrowBottomSheet
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.theme.AppRadius
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.DmSans
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void

@Composable
fun GardenBedListScreen(
    onBedClick: (Long) -> Unit,
    onReportsClick: () -> Unit,
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val beds by viewModel.activeBeds.collectAsState()
    val plantingCounts by viewModel.activePlantingCounts.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var bedToDelete by remember { mutableStateOf<GardenBed?>(null) }
    var bedForAction by remember { mutableStateOf<GardenBed?>(null) }
    var bedToEdit by remember { mutableStateOf<GardenBed?>(null) }

    AppScaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Bed") },
                containerColor = GardenGlow,
                contentColor = Void,
                shape = RoundedCornerShape(AppRadius.input),
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(end = AppSpacing.md, bottom = AppSpacing.md),
            )
        },
    ) { padding ->
        if (beds.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.md, vertical = AppSpacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Garden",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = DmSans,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onReportsClick) {
                        Icon(Icons.Outlined.Assessment, "Reports", tint = TextTertiary)
                    }
                }
                EmptyState(
                    icon = {
                        Icon(
                            Icons.Outlined.Grass,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = GardenGlow,
                        )
                    },
                    title = "No garden beds yet",
                    subtitle = "Add your first bed to start tracking plantings",
                    actionLabel = "Add Bed",
                    glowColor = GardenGlow,
                    onAction = { showAddDialog = true },
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
                    top = AppSpacing.xs,
                    bottom = AppSpacing.xl,
                ),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                item(key = "header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = AppSpacing.xxs),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Garden",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = DmSans,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onReportsClick) {
                            Icon(Icons.Outlined.Assessment, "Reports", tint = TextTertiary)
                        }
                    }
                }

                items(beds, key = { it.id }) { bed ->
                    BedCard(
                        bed = bed,
                        activePlantings = plantingCounts[bed.id] ?: 0,
                        onClick = { onBedClick(bed.id) },
                    )
                }
            }
        }
    }

    // ── Dialogs & Sheets ──

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

// ── Bed Card ──

@Composable
private fun BedCard(
    bed: GardenBed,
    activePlantings: Int,
    onClick: () -> Unit,
) {
    AppCard(
        onClick = onClick,
    ) {
        Text(
            bed.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.height(AppSpacing.xxs))
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            InfoPill(bed.type.replaceFirstChar { it.uppercase() })
            bed.sunExposure?.let {
                InfoPill(it.replaceFirstChar { c -> c.uppercase() })
            }
        }
        Spacer(modifier = Modifier.height(AppSpacing.xxs))
        Text(
            "$activePlantings active planting${if (activePlantings != 1) "s" else ""}",
            fontSize = 12.sp,
            color = TextSecondary,
        )
    }
}

@Composable
private fun InfoPill(text: String) {
    AppChip(
        text = text,
        selected = false,
        accentColor = GardenGlow,
    )
}

// ── Form Sheets ──

@Composable
private fun AddBedSheet(
    existingBed: GardenBed? = null,
    onDismiss: () -> Unit,
    onSave: (GardenBed) -> Unit,
) {
    val isEditMode = existingBed != null
    var name by remember { mutableStateOf(existingBed?.name ?: "") }
    var type by remember { mutableStateOf(existingBed?.type ?: "raised bed") }
    var sizeGallons by remember { mutableStateOf(existingBed?.sizeGallons?.toString() ?: "") }
    var lengthFt by remember { mutableStateOf(existingBed?.lengthFt?.toString() ?: "") }
    var widthFt by remember { mutableStateOf(existingBed?.widthFt?.toString() ?: "") }
    var sunExposure by remember { mutableStateOf(existingBed?.sunExposure ?: "full sun") }
    var soilType by remember { mutableStateOf(existingBed?.soilType ?: "") }
    var notes by remember { mutableStateOf(existingBed?.notes ?: "") }

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
                options = listOf("raised bed", "grow bag", "in-ground", "container"),
                selected = type,
                onSelect = { type = it },
                accentColor = GardenGlow,
            )
            if (type == "grow bag" || type == "container") {
                AppTextField(
                    value = sizeGallons,
                    onValueChange = { sizeGallons = it.filter { c -> c.isDigit() } },
                    label = { Text("Size (gallons)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors,
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    AppTextField(
                        value = lengthFt,
                        onValueChange = { lengthFt = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Length (ft)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = fieldColors,
                    )
                    AppTextField(
                        value = widthFt,
                        onValueChange = { widthFt = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Width (ft)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = fieldColors,
                    )
                }
            }
            DropdownSelector(
                label = "Sun Exposure",
                options = listOf("full sun", "partial sun", "partial shade", "full shade"),
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
