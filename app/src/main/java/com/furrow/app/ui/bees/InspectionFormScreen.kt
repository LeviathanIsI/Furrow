package com.furrow.app.ui.bees

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.Inspection
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.FormCard
import com.furrow.app.ui.components.NumberStepper
import com.furrow.app.ui.components.StickyBottomButton
import com.furrow.app.ui.components.ToggleRow
import com.furrow.app.ui.theme.FurrowBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionFormScreen(
    hiveId: Long,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: BeeViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var queenSeen by remember { mutableStateOf(false) }
    var queenCells by remember { mutableStateOf(false) }
    var eggsLarvae by remember { mutableStateOf(false) }
    var temperament by remember { mutableStateOf("calm") }
    var broodPattern by remember { mutableStateOf("solid") }
    var honeyStores by remember { mutableStateOf("moderate") }
    var pollenStores by remember { mutableStateOf("moderate") }
    var pestsSigns by remember { mutableStateOf("") }
    var diseasesSigns by remember { mutableStateOf("") }
    var frameCount by remember { mutableIntStateOf(0) }
    var addedSupers by remember { mutableIntStateOf(0) }
    var removedSupers by remember { mutableIntStateOf(0) }
    var feeding by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var weatherTemp by remember { mutableIntStateOf(70) }
    var weatherCondition by remember { mutableStateOf("sunny") }

    // Load existing inspection for edit mode
    if (isEditMode) {
        val existingInspection by viewModel.getInspectionById(editId).collectAsState(initial = null)
        LaunchedEffect(existingInspection) {
            existingInspection?.let {
                date = it.date
                queenSeen = it.queenSeen
                queenCells = it.queenCells
                eggsLarvae = it.eggsLarvae
                temperament = it.temperament ?: "calm"
                broodPattern = it.broodPattern ?: "solid"
                honeyStores = it.honeyStores ?: "moderate"
                pollenStores = it.pollenStores ?: "moderate"
                pestsSigns = it.pestsSigns ?: ""
                diseasesSigns = it.diseasesSigns ?: ""
                frameCount = it.frameCount ?: 0
                addedSupers = it.addedSupers ?: 0
                removedSupers = it.removedSupers ?: 0
                feeding = it.feeding ?: ""
                notes = it.notes ?: ""
                weatherTemp = it.weatherTemp ?: 70
                weatherCondition = it.weatherCondition ?: "sunny"
            }
        }
    }

    // Section expand states — Colony Status open by default
    var colonyExpanded by remember { mutableStateOf(true) }
    var resourcesExpanded by remember { mutableStateOf(false) }
    var healthExpanded by remember { mutableStateOf(false) }
    var notesExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Inspection" else "Add Inspection") },
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
                text = "Save Inspection",
                onClick = {
                    val inspection = Inspection(
                        id = if (isEditMode) editId else 0,
                        hiveId = hiveId,
                        date = date,
                        queenSeen = queenSeen,
                        queenCells = queenCells,
                        eggsLarvae = eggsLarvae,
                        temperament = temperament,
                        broodPattern = broodPattern,
                        honeyStores = honeyStores,
                        pollenStores = pollenStores,
                        pestsSigns = pestsSigns.ifBlank { null },
                        diseasesSigns = diseasesSigns.ifBlank { null },
                        frameCount = frameCount,
                        addedSupers = addedSupers,
                        removedSupers = removedSupers,
                        feeding = feeding.ifBlank { null },
                        notes = notes.ifBlank { null },
                        weatherTemp = weatherTemp,
                        weatherCondition = weatherCondition,
                    )
                    if (isEditMode) viewModel.updateInspection(inspection) else viewModel.addInspection(inspection)
                    onBack()
                },
            )
        }
    ) { padding ->
        FurrowBackground(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DateFieldWithToggle(
                    label = "Date",
                    dateMillis = date,
                    onDateChange = { date = it },
                    useTodayDefault = !isEditMode,
                )

                // Section 1: Colony Status
                CollapsibleSection(
                    title = "Colony Status",
                    accentColor = MaterialTheme.colorScheme.primary,
                    expanded = colonyExpanded,
                    onToggle = { colonyExpanded = !colonyExpanded },
                ) {
                    FormCard {
                        ToggleRow("Queen Seen", queenSeen) { queenSeen = it }
                        ToggleRow("Queen Cells", queenCells) { queenCells = it }
                        ToggleRow("Eggs / Larvae", eggsLarvae) { eggsLarvae = it }
                        DropdownSelector(
                            label = "Temperament",
                            options = listOf("calm", "nervous", "aggressive"),
                            selected = temperament,
                            onSelect = { temperament = it },
                        )
                        DropdownSelector(
                            label = "Brood Pattern",
                            options = listOf("solid", "spotty", "none"),
                            selected = broodPattern,
                            onSelect = { broodPattern = it },
                        )
                    }
                }

                // Section 2: Resources
                CollapsibleSection(
                    title = "Resources",
                    accentColor = MaterialTheme.colorScheme.secondary,
                    expanded = resourcesExpanded,
                    onToggle = { resourcesExpanded = !resourcesExpanded },
                ) {
                    FormCard {
                        DropdownSelector(
                            label = "Honey Stores",
                            options = listOf("heavy", "moderate", "light", "none"),
                            selected = honeyStores,
                            onSelect = { honeyStores = it },
                        )
                        DropdownSelector(
                            label = "Pollen Stores",
                            options = listOf("heavy", "moderate", "light", "none"),
                            selected = pollenStores,
                            onSelect = { pollenStores = it },
                        )
                        NumberStepper(
                            value = frameCount,
                            onValueChange = { frameCount = it },
                            minValue = 0,
                            maxValue = 99,
                            label = "Frame Count",
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            NumberStepper(
                                value = addedSupers,
                                onValueChange = { addedSupers = it },
                                minValue = 0,
                                maxValue = 20,
                                label = "Supers Added",
                            )
                            NumberStepper(
                                value = removedSupers,
                                onValueChange = { removedSupers = it },
                                minValue = 0,
                                maxValue = 20,
                                label = "Supers Removed",
                            )
                        }
                    }
                }

                // Section 3: Health
                CollapsibleSection(
                    title = "Health",
                    accentColor = MaterialTheme.colorScheme.error,
                    expanded = healthExpanded,
                    onToggle = { healthExpanded = !healthExpanded },
                ) {
                    FormCard {
                        OutlinedTextField(
                            value = pestsSigns,
                            onValueChange = { pestsSigns = it },
                            label = { Text("Pest Signs") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = diseasesSigns,
                            onValueChange = { diseasesSigns = it },
                            label = { Text("Disease Signs") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                }

                // Section 4: Notes
                CollapsibleSection(
                    title = "Notes",
                    accentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    expanded = notesExpanded,
                    onToggle = { notesExpanded = !notesExpanded },
                ) {
                    FormCard {
                        NumberStepper(
                            value = weatherTemp,
                            onValueChange = { weatherTemp = it },
                            minValue = -20,
                            maxValue = 120,
                            label = "Temp (\u00B0F)",
                        )
                        DropdownSelector(
                            label = "Weather",
                            options = listOf("sunny", "cloudy", "overcast", "rainy", "windy"),
                            selected = weatherCondition,
                            onSelect = { weatherCondition = it },
                        )
                        OutlinedTextField(
                            value = feeding,
                            onValueChange = { feeding = it },
                            label = { Text("Feeding") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CollapsibleSection(
    title: String,
    accentColor: Color,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                color = accentColor,
            )
            Icon(
                if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = accentColor,
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            content()
        }
    }
}
