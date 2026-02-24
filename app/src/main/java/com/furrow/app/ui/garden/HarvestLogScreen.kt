package com.furrow.app.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.ui.components.AppButtonPrimary
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppSectionHeader
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.AppTextField
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.NumberStepper
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HarvestLogScreen(
    bedId: Long,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: BedDetailViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L
    val zone = viewModel.zone
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

    val fieldColors = AppTextFieldDefaults.colors(accentColor = GardenGlow)

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(
                title = if (isEditMode) "Edit harvest" else "Log harvest",
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            // ── Section: Harvest Details ──
            FormSectionBar("Harvest details")

            PlantingSelector(
                plantings = plantings,
                selected = selectedPlanting,
                onSelect = { selectedPlanting = it },
                fieldColors = fieldColors,
            )

            DateFieldWithToggle(
                label = "Date",
                dateMillis = date,
                onDateChange = { date = it },
                useTodayDefault = !isEditMode,
                accentColor = GardenGlow,
                zone = zone,
            )

            // ── Section: Amount ──
            FormSectionBar("Amount")

            AppTextField(
                value = amountOz,
                onValueChange = { amountOz = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Weight (oz)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
            )

            NumberStepper(
                value = count,
                onValueChange = { count = it },
                minValue = 0,
                maxValue = 999,
                label = "Count",
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
                text = "Save Harvest",
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
                enabled = selectedPlanting != null && (amountOz.isNotBlank() || count > 0),
                modifier = Modifier
                    .fillMaxWidth(),
                glowEnabled = true,
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FormSectionBar(title: String) {
    AppSectionHeader(title = title)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlantingSelector(
    plantings: List<Planting>,
    selected: Planting?,
    onSelect: (Planting) -> Unit,
    fieldColors: androidx.compose.material3.TextFieldColors,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        AppTextField(
            value = selected?.let { "${it.plantName}${it.variety?.let { v -> " ($v)" } ?: ""}" } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Planting") },
            placeholder = { Text("Select a planting") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth(),
            colors = fieldColors,
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (plantings.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No plantings in this bed", color = TextSecondary) },
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
