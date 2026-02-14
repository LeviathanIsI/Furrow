package com.furrow.app.ui.garden

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.FormCard
import com.furrow.app.ui.components.FormSectionHeader
import com.furrow.app.ui.components.NumberStepper
import com.furrow.app.ui.components.StickyBottomButton
import com.furrow.app.ui.theme.FurrowBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HarvestLogScreen(
    bedId: Long,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L
    val plantings by viewModel.plantings.collectAsState()
    var selectedPlanting by remember { mutableStateOf<Planting?>(null) }
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var amountOz by remember { mutableStateOf("") }
    var count by remember { mutableIntStateOf(0) }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existingHarvest by viewModel.getHarvestById(editId).collectAsState(initial = null)
        LaunchedEffect(existingHarvest, plantings) {
            existingHarvest?.let { harvest ->
                date = harvest.date
                amountOz = harvest.amountOz?.toString() ?: ""
                count = harvest.count ?: 0
                notes = harvest.notes ?: ""
                if (selectedPlanting == null) {
                    selectedPlanting = plantings.firstOrNull { it.id == harvest.plantingId }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Harvest" else "Log Harvest") },
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
                text = "Save Harvest",
                enabled = selectedPlanting != null && (amountOz.isNotBlank() || count > 0),
                onClick = {
                    selectedPlanting?.let { planting ->
                        val harvest = HarvestLog(
                            id = if (isEditMode) editId else 0,
                            plantingId = planting.id,
                            date = date,
                            amountOz = amountOz.toDoubleOrNull(),
                            count = if (count > 0) count else null,
                            notes = notes.ifBlank { null },
                        )
                        if (isEditMode) viewModel.updateHarvest(harvest) else viewModel.addHarvest(harvest)
                        onBack()
                    }
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                FormSectionHeader(title = "Harvest Details")
                FormCard {
                    PlantingSelector(
                        plantings = plantings,
                        selected = selectedPlanting,
                        onSelect = { selectedPlanting = it },
                    )
                    DateFieldWithToggle(
                        label = "Date",
                        dateMillis = date,
                        onDateChange = { date = it },
                        useTodayDefault = !isEditMode,
                    )
                }

                FormSectionHeader(title = "Amount")
                FormCard {
                    OutlinedTextField(
                        value = amountOz,
                        onValueChange = { amountOz = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Weight (oz)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    NumberStepper(
                        value = count,
                        onValueChange = { count = it },
                        minValue = 0,
                        maxValue = 999,
                        label = "Count",
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlantingSelector(
    plantings: List<Planting>,
    selected: Planting?,
    onSelect: (Planting) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected?.let { "${it.plantName}${it.variety?.let { v -> " ($v)" } ?: ""}" } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Planting") },
            placeholder = { Text("Select a planting") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (plantings.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No plantings in this bed", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    onClick = { expanded = false },
                )
            } else {
                plantings.forEach { planting ->
                    DropdownMenuItem(
                        text = {
                            Text("${planting.plantName}${planting.variety?.let { " ($it)" } ?: ""}")
                        },
                        onClick = { onSelect(planting); expanded = false },
                    )
                }
            }
        }
    }
}
