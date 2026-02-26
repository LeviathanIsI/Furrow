package com.furrow.app.ui.garden

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.AppButtonPrimary
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppSectionHeader
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.AppTextField
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.DiscardChangesDialog
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.SuccessSnackbarEffect
import com.furrow.app.ui.components.FurrowBottomSheet
import com.furrow.app.ui.components.StatusPill
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void
import com.furrow.app.util.DateUtil
import com.furrow.app.util.filterInteger
import java.time.Instant
import java.time.ZoneId

private enum class PlantBadge(val label: String) {
    RECOMMENDED("Recommended"),
    CAN_GROW("Can grow here"),
    NOT_RECOMMENDED("Not recommended"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantingFormScreen(
    bedId: Long,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: BedDetailViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L
    val zone = viewModel.zone
    var plantName by remember { mutableStateOf("") }
    var plantQuery by remember { mutableStateOf("") }
    var variety by remember { mutableStateOf("") }
    var varietyId by remember { mutableStateOf<Long?>(null) }
    var varietyQuery by remember { mutableStateOf("") }
    var selectedPlantId by remember { mutableStateOf<Long?>(null) }
    var datePlanted by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var source by remember { mutableStateOf("Seed") }
    var status by remember { mutableStateOf("Growing") }
    var notes by remember { mutableStateOf("") }
    var seedsPlanted by remember { mutableStateOf("") }
    var germinationDate by remember { mutableLongStateOf(0L) }
    var seedsSprouted by remember { mutableStateOf("") }
    var showAddCustomPlant by remember { mutableStateOf(false) }
    var customPlantInitialName by remember { mutableStateOf("") }
    var plantToEdit by remember { mutableStateOf<PlantInfo?>(null) }
    var plantToDelete by remember { mutableStateOf<PlantInfo?>(null) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    BackHandler { showDiscardDialog = true }
    DiscardChangesDialog(showDialog = showDiscardDialog, onDismiss = { showDiscardDialog = false }, onDiscard = { showDiscardDialog = false; onBack() })

    if (isEditMode) {
        val existingPlanting by viewModel.getPlantingById(editId).collectAsState(initial = null)
        LaunchedEffect(existingPlanting) {
            existingPlanting?.let {
                plantName = it.plantName
                plantQuery = it.plantName
                variety = it.variety ?: ""
                varietyQuery = it.variety ?: ""
                varietyId = it.varietyId
                datePlanted = it.datePlanted
                source = it.source
                status = it.status
                notes = it.notes ?: ""
                seedsPlanted = it.seedsPlanted?.toString() ?: ""
                germinationDate = it.germinationDate ?: 0L
                seedsSprouted = it.seedsSprouted?.toString() ?: ""
            }
        }
    }

    val allPlants by viewModel.allPlants.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val activeWindows by viewModel.activeWindows.collectAsState()

    val activeWindowNames = remember(activeWindows) {
        activeWindows.map { it.plantName }.toSet()
    }

    val userZoneNumber = remember(userProfile) {
        userProfile?.hardinessZone?.filter { it.isDigit() }?.toIntOrNull()
    }

    fun badgeFor(plant: PlantInfo): PlantBadge {
        val zone = userZoneNumber
        val zoneMatch = zone != null && plant.minZone <= zone && plant.maxZone >= zone
        return when {
            zoneMatch && plant.name in activeWindowNames -> PlantBadge.RECOMMENDED
            zoneMatch -> PlantBadge.CAN_GROW
            else -> PlantBadge.NOT_RECOMMENDED
        }
    }

    val filteredPlants = remember(allPlants, plantQuery, userZoneNumber, activeWindowNames) {
        val query = plantQuery.lowercase()
        allPlants
            .filter { query.isEmpty() || it.name.lowercase().contains(query) }
            .sortedWith(
                compareBy<PlantInfo> { plant ->
                    val zone = userZoneNumber
                    val zoneMatch = zone != null
                        && plant.minZone <= zone
                        && plant.maxZone >= zone
                    when {
                        zoneMatch && plant.name in activeWindowNames -> 0
                        zoneMatch -> 1
                        else -> 2
                    }
                }.thenBy { it.name }
            )
    }

    LaunchedEffect(plantName, allPlants) {
        val match = allPlants.firstOrNull { it.name == plantName }
        selectedPlantId = match?.id
    }

    val varietiesByPlantId by viewModel.varietiesByPlantId.collectAsState()
    val plantVarieties = remember(selectedPlantId, varietiesByPlantId) {
        selectedPlantId?.let { varietiesByPlantId[it] } ?: emptyList()
    }
    val filteredVarieties = remember(plantVarieties, varietyQuery) {
        val query = varietyQuery.lowercase()
        if (query.isEmpty()) plantVarieties
        else plantVarieties.filter { it.name.lowercase().contains(query) }
    }

    // -- Companion / Incompatible planting checks --
    val existingPlantings by viewModel.plantings.collectAsState()
    val selectedPlantInfo = remember(plantName, allPlants) {
        allPlants.firstOrNull { it.name.equals(plantName, ignoreCase = true) }
    }
    val existingPlantNames = remember(existingPlantings) {
        existingPlantings.map { it.plantName.lowercase() }.toSet()
    }
    val incompatibleInBed = remember(selectedPlantInfo, existingPlantNames) {
        selectedPlantInfo?.incompatiblePlants
            ?.split(",")?.map { it.trim().lowercase() }?.filter { it.isNotBlank() && it in existingPlantNames }
            ?: emptyList()
    }
    val companionsInBed = remember(selectedPlantInfo, existingPlantNames) {
        selectedPlantInfo?.companionPlants
            ?.split(",")?.map { it.trim().lowercase() }?.filter { it.isNotBlank() && it in existingPlantNames }
            ?: emptyList()
    }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = GardenGlow)

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)
    SuccessSnackbarEffect(
        message = viewModel.successMessage,
        onClear = viewModel::clearSuccess,
        snackbarHostState = snackbarHostState,
        onDismissed = { onBack() },
    )

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(
                title = if (isEditMode) "Edit planting" else "Add planting",
                navigationIcon = {
                    IconButton(onClick = { showDiscardDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── Section: Plant Info ──
            GardenSectionHeader("Plant info")

            DropdownSelector(
                label = "Plant Name",
                options = filteredPlants.map { it.name },
                selected = plantQuery,
                onSelect = { selectedName ->
                    plantName = selectedName
                    plantQuery = selectedName
                    val plant = allPlants.firstOrNull { it.name == selectedName }
                    selectedPlantId = plant?.id
                    variety = ""
                    varietyQuery = ""
                    varietyId = null
                },
                accentColor = GardenGlow,
            )

            if (incompatibleInBed.isNotEmpty()) {
                Panel {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    ) {
                        Icon(
                            Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = StatusBad,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = "Incompatible with ${incompatibleInBed.joinToString(", ") { it.replaceFirstChar { c -> c.uppercase() } }} already in this bed",
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusBad,
                        )
                    }
                }
            }

            if (companionsInBed.isNotEmpty()) {
                Panel {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    ) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = GardenGlow,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = "Good companion with ${companionsInBed.joinToString(", ") { it.replaceFirstChar { c -> c.uppercase() } }} in this bed",
                            style = MaterialTheme.typography.bodySmall,
                            color = GardenGlow,
                        )
                    }
                }
            }

            if (plantVarieties.isNotEmpty()) {
                DropdownSelector(
                    label = "Variety (optional)",
                    options = filteredVarieties.map { it.name },
                    selected = varietyQuery,
                    onSelect = { selectedName ->
                        variety = selectedName
                        varietyQuery = selectedName
                        varietyId = plantVarieties.firstOrNull { it.name == selectedName }?.id
                    },
                    accentColor = GardenGlow,
                )
            } else {
                AppTextField(
                    value = variety,
                    onValueChange = { variety = it; varietyId = null },
                    label = { Text("Variety (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors,
                )
            }

            DropdownSelector(
                label = "Source",
                options = listOf("Seed", "Transplant", "Cutting"),
                selected = source,
                onSelect = { source = it },
                accentColor = GardenGlow,
            )

            if (source == "Seed") {
                AppTextField(
                    value = seedsPlanted,
                    onValueChange = { seedsPlanted = it.filterInteger() },
                    label = { Text("Seeds Planted (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors,
                )
            }

            // ── Section: Status ──
            GardenSectionHeader("Status")

            DateFieldWithToggle(
                label = "Date Planted",
                dateMillis = datePlanted,
                onDateChange = { datePlanted = it },
                useTodayDefault = !isEditMode,
                accentColor = GardenGlow,
                zone = zone,
            )

            if (source == "Seed" && germinationDate > 0L) {
                DateFieldWithToggle(
                    label = "Date Sprouted",
                    dateMillis = germinationDate,
                    onDateChange = { germinationDate = it },
                    useTodayDefault = false,
                    accentColor = GardenGlow,
                    zone = zone,
                )

                AppTextField(
                    value = seedsSprouted,
                    onValueChange = { seedsSprouted = it.filterInteger() },
                    label = { Text("Seeds Sprouted (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors,
                )
            }

            DropdownSelector(
                label = "Status",
                options = listOf("Growing", "Producing", "Finished", "Failed"),
                selected = status,
                onSelect = { status = it },
                accentColor = GardenGlow,
            )

            AppTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = fieldColors,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Save Button ──
            AppButtonPrimary(
                text = "Save Planting",
                onClick = {
                    val matchedPlant = allPlants.firstOrNull { it.name == plantName.trim() }
                    val expectedGermDate = if (source == "Seed" && matchedPlant?.germinationDaysMin != null) {
                        val plantedInstant = Instant.ofEpochMilli(datePlanted)
                            .atZone(zone).toLocalDate()
                        val expectedDate = plantedInstant.plusDays(matchedPlant.germinationDaysMin.toLong())
                        expectedDate.atStartOfDay(zone).toInstant().toEpochMilli()
                    } else null

                    val planting = Planting(
                        id = if (isEditMode) editId else 0,
                        bedId = bedId,
                        plantName = plantName.trim(),
                        variety = variety.ifBlank { null },
                        varietyId = varietyId,
                        datePlanted = datePlanted,
                        source = source,
                        status = status,
                        notes = notes.ifBlank { null },
                        seedsPlanted = if (source == "Seed") seedsPlanted.toIntOrNull() else null,
                        germinationDate = germinationDate.takeIf { it > 0L },
                        seedsSprouted = if (source == "Seed") seedsSprouted.toIntOrNull() else null,
                        expectedGerminationDate = expectedGermDate,
                    )
                    if (isEditMode) viewModel.updatePlanting(planting) else viewModel.addPlanting(planting)
                },
                enabled = plantName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth(),
                glowEnabled = true,
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAddCustomPlant || plantToEdit != null) {
        AddCustomPlantSheet(
            existingPlant = plantToEdit,
            initialName = if (plantToEdit != null) plantToEdit!!.name else customPlantInitialName,
            onDismiss = {
                showAddCustomPlant = false
                plantToEdit = null
            },
            onSave = { plant ->
                if (plantToEdit != null) {
                    viewModel.updatePlant(plant)
                } else {
                    viewModel.insertPlant(plant)
                }
                plantName = plant.name
                plantQuery = plant.name
                showAddCustomPlant = false
                plantToEdit = null
            },
        )
    }

    plantToDelete?.let { plant ->
        DeleteConfirmationDialog(
            itemName = plant.name,
            onConfirm = {
                viewModel.deletePlant(plant)
                if (plantName == plant.name) {
                    plantName = ""
                    plantQuery = ""
                }
                plantToDelete = null
            },
            onDismiss = { plantToDelete = null },
        )
    }
}

@Composable
private fun GardenSectionHeader(title: String) {
    AppSectionHeader(title = title)
}

@Composable
private fun PlantRecommendationBadge(badge: PlantBadge) {
    val color = when (badge) {
        PlantBadge.RECOMMENDED -> StatusGood
        PlantBadge.CAN_GROW -> StatusWarn
        PlantBadge.NOT_RECOMMENDED -> StatusBad
    }
    StatusPill(
        text = badge.label,
        active = badge == PlantBadge.RECOMMENDED,
        error = badge == PlantBadge.NOT_RECOMMENDED,
    )
}

@Composable
private fun AddCustomPlantSheet(
    existingPlant: PlantInfo?,
    initialName: String,
    onDismiss: () -> Unit,
    onSave: (PlantInfo) -> Unit,
) {
    var name by remember { mutableStateOf(existingPlant?.name ?: initialName) }
    var category by remember { mutableStateOf(existingPlant?.category ?: "Vegetable") }
    var minZone by remember { mutableStateOf(existingPlant?.minZone?.toString() ?: "3") }
    var maxZone by remember { mutableStateOf(existingPlant?.maxZone?.toString() ?: "10") }
    var daysMin by remember { mutableStateOf(existingPlant?.daysToHarvestMin?.toString() ?: "60") }
    var daysMax by remember { mutableStateOf(existingPlant?.daysToHarvestMax?.toString() ?: "90") }
    var sun by remember { mutableStateOf(existingPlant?.sunRequirement ?: "Full Sun") }
    var water by remember { mutableStateOf(existingPlant?.waterFrequency ?: "Moderate") }
    var containerSuitable by remember { mutableStateOf(existingPlant?.containerSuitable ?: true) }
    var containerGallons by remember { mutableStateOf(existingPlant?.containerMinGallons?.toString() ?: "5") }
    var companions by remember { mutableStateOf(existingPlant?.companionPlants ?: "") }
    var incompatible by remember { mutableStateOf(existingPlant?.incompatiblePlants ?: "") }
    var plantNotes by remember { mutableStateOf(existingPlant?.notes ?: "") }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = GardenGlow)

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = if (existingPlant != null) "Edit Custom Plant" else "Add Custom Plant",
        confirmText = "Save",
        confirmEnabled = name.isNotBlank(),
        glowColor = GardenGlow,
        onConfirm = {
            if (name.isNotBlank()) {
                onSave(
                    PlantInfo(
                        id = existingPlant?.id ?: 0,
                        name = name.trim(),
                        category = category,
                        minZone = minZone.toIntOrNull() ?: 3,
                        maxZone = maxZone.toIntOrNull() ?: 10,
                        daysToHarvestMin = daysMin.toIntOrNull() ?: 60,
                        daysToHarvestMax = daysMax.toIntOrNull() ?: 90,
                        sunRequirement = sun,
                        waterFrequency = water,
                        containerSuitable = containerSuitable,
                        containerMinGallons = containerGallons.toIntOrNull() ?: 5,
                        companionPlants = companions,
                        incompatiblePlants = incompatible,
                        notes = plantNotes.ifBlank { null },
                        isCustom = true,
                    )
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
                label = "Category",
                options = listOf("Vegetable", "Fruit", "Herb", "Berry", "Legume", "Grain"),
                selected = category,
                onSelect = { category = it },
                accentColor = GardenGlow,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                AppTextField(
                    value = minZone,
                    onValueChange = { minZone = it.filterInteger() },
                    label = { Text("Min Zone") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                )
                AppTextField(
                    value = maxZone,
                    onValueChange = { maxZone = it.filterInteger() },
                    label = { Text("Max Zone") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                AppTextField(
                    value = daysMin,
                    onValueChange = { daysMin = it.filterInteger() },
                    label = { Text("Days Min") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                )
                AppTextField(
                    value = daysMax,
                    onValueChange = { daysMax = it.filterInteger() },
                    label = { Text("Days Max") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                )
            }
            DropdownSelector(
                label = "Sun Requirement",
                options = listOf("Full Sun", "Partial Sun", "Partial Shade", "Full Shade"),
                selected = sun,
                onSelect = { sun = it },
                accentColor = GardenGlow,
            )
            DropdownSelector(
                label = "Water Frequency",
                options = listOf("Low", "Moderate", "High"),
                selected = water,
                onSelect = { water = it },
                accentColor = GardenGlow,
            )
            AppTextField(
                value = companions,
                onValueChange = { companions = it },
                label = { Text("Companion Plants") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
            )
            AppTextField(
                value = incompatible,
                onValueChange = { incompatible = it },
                label = { Text("Incompatible Plants") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
            )
            AppTextField(
                value = plantNotes,
                onValueChange = { plantNotes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = fieldColors,
            )
        },
    )
}
