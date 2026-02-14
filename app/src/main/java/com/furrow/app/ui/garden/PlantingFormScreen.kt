package com.furrow.app.ui.garden

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.ui.bees.DropdownSelector
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.FormCard
import com.furrow.app.ui.components.FormSectionHeader
import com.furrow.app.ui.components.FurrowBottomSheet
import com.furrow.app.ui.components.SearchableSelector
import com.furrow.app.ui.components.StickyBottomButton
import com.furrow.app.ui.theme.FurrowBackground
import kotlin.math.roundToInt

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
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L
    var plantName by remember { mutableStateOf("") }
    var plantQuery by remember { mutableStateOf("") }
    var variety by remember { mutableStateOf("") }
    var datePlanted by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var source by remember { mutableStateOf("seed") }
    var status by remember { mutableStateOf("growing") }
    var notes by remember { mutableStateOf("") }
    var showAddCustomPlant by remember { mutableStateOf(false) }
    var customPlantInitialName by remember { mutableStateOf("") }
    var plantToEdit by remember { mutableStateOf<PlantInfo?>(null) }
    var plantToDelete by remember { mutableStateOf<PlantInfo?>(null) }

    if (isEditMode) {
        val existingPlanting by viewModel.getPlantingById(editId).collectAsState(initial = null)
        LaunchedEffect(existingPlanting) {
            existingPlanting?.let {
                plantName = it.plantName
                plantQuery = it.plantName
                variety = it.variety ?: ""
                datePlanted = it.datePlanted
                source = it.source
                status = it.status
                notes = it.notes ?: ""
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Planting" else "Add Planting") },
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
        bottomBar = {
            StickyBottomButton(
                text = "Save Planting",
                enabled = plantName.isNotBlank(),
                onClick = {
                    val planting = Planting(
                        id = if (isEditMode) editId else 0,
                        bedId = bedId,
                        plantName = plantName.trim(),
                        variety = variety.ifBlank { null },
                        datePlanted = datePlanted,
                        source = source,
                        status = status,
                        notes = notes.ifBlank { null },
                    )
                    if (isEditMode) viewModel.updatePlanting(planting) else viewModel.addPlanting(planting)
                    onBack()
                },
            )
        }
    ) { padding ->
        FurrowBackground(modifier = Modifier.fillMaxSize().padding(padding)) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                FormSectionHeader(title = "Plant Info")
                FormCard {
                    SearchableSelector(
                        query = plantQuery,
                        onQueryChange = {
                            plantQuery = it
                            plantName = it
                        },
                        items = filteredPlants,
                        onItemSelected = { plant ->
                            plantName = plant.name
                            plantQuery = plant.name
                        },
                        label = "Plant Name",
                        nameSelector = { it.name },
                        isCustom = { it.isCustom },
                        onAddCustom = { name ->
                            customPlantInitialName = name
                            showAddCustomPlant = true
                        },
                        onEditCustom = { plant ->
                            plantToEdit = plant
                        },
                        onDeleteCustom = { plant ->
                            plantToDelete = plant
                        },
                        itemContent = { plant ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    plant.name,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                PlantRecommendationBadge(badgeFor(plant))
                            }
                        },
                    )
                    OutlinedTextField(
                        value = variety,
                        onValueChange = { variety = it },
                        label = { Text("Variety (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    DropdownSelector(
                        label = "Source",
                        options = listOf("seed", "transplant", "cutting"),
                        selected = source,
                        onSelect = { source = it },
                    )
                }

                FormSectionHeader(title = "Status")
                FormCard {
                    DateFieldWithToggle(
                        label = "Date Planted",
                        dateMillis = datePlanted,
                        onDateChange = { datePlanted = it },
                        useTodayDefault = !isEditMode,
                    )
                    DropdownSelector(
                        label = "Status",
                        options = listOf("growing", "producing", "finished", "failed"),
                        selected = status,
                        onSelect = { status = it },
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
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
private fun PlantRecommendationBadge(badge: PlantBadge) {
    val color = when (badge) {
        PlantBadge.RECOMMENDED -> MaterialTheme.colorScheme.primary
        PlantBadge.CAN_GROW -> MaterialTheme.colorScheme.tertiary
        PlantBadge.NOT_RECOMMENDED -> MaterialTheme.colorScheme.error
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
private fun AddCustomPlantSheet(
    existingPlant: PlantInfo?,
    initialName: String,
    onDismiss: () -> Unit,
    onSave: (PlantInfo) -> Unit,
) {
    var name by remember { mutableStateOf(existingPlant?.name ?: initialName) }
    var category by remember { mutableStateOf(existingPlant?.category ?: "vegetable") }
    var minZone by remember { mutableStateOf(existingPlant?.minZone?.toString() ?: "3") }
    var maxZone by remember { mutableStateOf(existingPlant?.maxZone?.toString() ?: "10") }
    var daysMin by remember { mutableStateOf(existingPlant?.daysToHarvestMin?.toString() ?: "60") }
    var daysMax by remember { mutableStateOf(existingPlant?.daysToHarvestMax?.toString() ?: "90") }
    var sun by remember { mutableStateOf(existingPlant?.sunRequirement ?: "full sun") }
    var water by remember { mutableStateOf(existingPlant?.waterFrequency ?: "moderate") }
    var containerSuitable by remember { mutableStateOf(existingPlant?.containerSuitable ?: true) }
    var containerGallons by remember { mutableStateOf(existingPlant?.containerMinGallons?.toString() ?: "5") }
    var companions by remember { mutableStateOf(existingPlant?.companionPlants ?: "") }
    var incompatible by remember { mutableStateOf(existingPlant?.incompatiblePlants ?: "") }
    var plantNotes by remember { mutableStateOf(existingPlant?.notes ?: "") }

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = if (existingPlant != null) "Edit Custom Plant" else "Add Custom Plant",
        confirmText = "Save",
        confirmEnabled = name.isNotBlank(),
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            DropdownSelector(
                label = "Category",
                options = listOf("vegetable", "fruit", "herb", "berry", "legume", "grain"),
                selected = category,
                onSelect = { category = it },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = minZone,
                    onValueChange = { minZone = it.filter { c -> c.isDigit() } },
                    label = { Text("Min Zone") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = maxZone,
                    onValueChange = { maxZone = it.filter { c -> c.isDigit() } },
                    label = { Text("Max Zone") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = daysMin,
                    onValueChange = { daysMin = it.filter { c -> c.isDigit() } },
                    label = { Text("Days Min") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = daysMax,
                    onValueChange = { daysMax = it.filter { c -> c.isDigit() } },
                    label = { Text("Days Max") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }
            DropdownSelector(
                label = "Sun Requirement",
                options = listOf("full sun", "partial sun", "partial shade", "full shade"),
                selected = sun,
                onSelect = { sun = it },
            )
            DropdownSelector(
                label = "Water Frequency",
                options = listOf("low", "moderate", "high"),
                selected = water,
                onSelect = { water = it },
            )
            OutlinedTextField(
                value = companions,
                onValueChange = { companions = it },
                label = { Text("Companion Plants") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = incompatible,
                onValueChange = { incompatible = it },
                label = { Text("Incompatible Plants") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = plantNotes,
                onValueChange = { plantNotes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
        },
    )
}
