package com.furrow.app.ui.land.forms

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.furrow.app.data.local.entity.Structure
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.land.LandViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.LandGlow

@Composable
internal fun StructureForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: LandViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var dimensions by remember { mutableStateOf("") }
    var sqFt by remember { mutableStateOf("") }
    var buildDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var useBuildDate by remember { mutableStateOf(false) }
    var cost by remember { mutableStateOf("") }
    var conditionRating by remember { mutableStateOf("") }
    var maintenanceSchedule by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getStructureById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                name = it.name
                type = it.type
                dimensions = it.dimensions ?: ""
                sqFt = it.sqFt?.toString() ?: ""
                if (it.buildDate != null) {
                    buildDate = it.buildDate
                    useBuildDate = true
                }
                cost = it.cost?.toString() ?: ""
                conditionRating = it.conditionRating?.toString() ?: ""
                maintenanceSchedule = it.maintenanceSchedule ?: ""
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        InputField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name *") },
            singleLine = true,
            accentColor = LandGlow,
        )

        DropdownSelector(
            label = "Type *",
            options = ModuleCatalogData.STRUCTURE_TYPES,
            selected = type,
            onSelect = { type = it },
            accentColor = LandGlow,
        )

        InputField(
            value = dimensions,
            onValueChange = { dimensions = it },
            label = { Text("Dimensions") },
            singleLine = true,
            accentColor = LandGlow,
        )

        InputField(
            value = sqFt,
            onValueChange = { sqFt = it },
            label = { Text("Square Feet") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        DateFieldWithToggle(
            label = "Build Date",
            dateMillis = buildDate,
            onDateChange = { buildDate = it; useBuildDate = true },
            useTodayDefault = false,
            accentColor = LandGlow,
            zone = viewModel.zone,
        )

        InputField(
            value = cost,
            onValueChange = { cost = it },
            label = { Text("Cost ($)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = conditionRating,
            onValueChange = { conditionRating = it },
            label = { Text("Condition Rating (1-5)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        InputField(
            value = maintenanceSchedule,
            onValueChange = { maintenanceSchedule = it },
            label = { Text("Maintenance Schedule") },
            singleLine = true,
            accentColor = LandGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Structure" else "Save Structure",
            onClick = {
                if (name.isBlank() || type.isBlank()) return@PrimaryButton
                val structure = Structure(
                    id = if (isEditMode) editId else 0,
                    name = name.trim(),
                    type = type.trim(),
                    dimensions = dimensions.ifBlank { null },
                    sqFt = sqFt.toDoubleOrNull(),
                    buildDate = if (useBuildDate) buildDate else null,
                    cost = cost.toDoubleOrNull(),
                    conditionRating = conditionRating.toIntOrNull()?.coerceIn(1, 5),
                    maintenanceSchedule = maintenanceSchedule.ifBlank { null },
                )
                if (isEditMode) viewModel.updateStructure(structure) else viewModel.addStructure(structure)
            },
            enabled = name.isNotBlank() && type.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
