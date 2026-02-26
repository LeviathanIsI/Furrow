package com.furrow.app.ui.preservation.forms

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import com.furrow.app.data.local.entity.FermentingBatch
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.ToggleRow
import com.furrow.app.ui.preservation.PreservationViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.PreservationGlow
import com.furrow.app.util.filterDecimal

@Composable
internal fun FermentingForm(
    editId: Long,
    isEditMode: Boolean,
    onBack: () -> Unit,
    viewModel: PreservationViewModel,
    snackbarHostState: SnackbarHostState,
    initialItemName: String = "",
) {
    var product by remember { mutableStateOf(initialItemName) }
    var ingredients by remember { mutableStateOf("") }
    var saltPct by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("") }
    var vesselType by remember { mutableStateOf("") }
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableLongStateOf(0L) }
    var hasEndDate by remember { mutableStateOf(false) }

    if (isEditMode) {
        val existing by viewModel.getFermentingBatchById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                product = it.product
                ingredients = it.ingredients ?: ""
                saltPct = it.saltPct?.toString() ?: ""
                method = it.method ?: ""
                vesselType = it.vesselType ?: ""
                startDate = it.startDate
                if (it.endDate != null) {
                    hasEndDate = true
                    endDate = it.endDate
                }
            }
        }
    }

    val title = if (isEditMode) "Edit Fermenting Batch" else "Add Fermenting Batch"

    FormScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) {
        InputField(
            value = product,
            onValueChange = { product = it },
            label = { Text("Product") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = ingredients,
            onValueChange = { ingredients = it },
            label = { Text("Ingredients") },
            minLines = 2,
            accentColor = PreservationGlow,
        )

        InputField(
            value = saltPct,
            onValueChange = { saltPct = it.filterDecimal() },
            label = { Text("Salt %") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        DropdownSelector(
            label = "Method",
            options = ModuleCatalogData.FERMENTATION_METHODS,
            selected = method,
            onSelect = { method = it },
            accentColor = PreservationGlow,
        )

        DropdownSelector(
            label = "Vessel Type",
            options = ModuleCatalogData.VESSEL_TYPES,
            selected = vesselType,
            onSelect = { vesselType = it },
            accentColor = PreservationGlow,
        )

        DateFieldWithToggle(
            label = "Start Date:",
            dateMillis = startDate,
            onDateChange = { startDate = it },
            useTodayDefault = !isEditMode,
            accentColor = PreservationGlow,
            zone = viewModel.zone,
        )

        ToggleRow(
            label = "Has End Date",
            checked = hasEndDate,
            accentColor = PreservationGlow,
            onCheckedChange = { checked ->
                hasEndDate = checked
                if (checked && endDate == 0L) {
                    endDate = System.currentTimeMillis()
                }
            },
        )

        if (hasEndDate) {
            DateFieldWithToggle(
                label = "End Date:",
                dateMillis = if (endDate > 0L) endDate else System.currentTimeMillis(),
                onDateChange = { endDate = it },
                useTodayDefault = false,
                accentColor = PreservationGlow,
                zone = viewModel.zone,
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Batch" else "Save Batch",
            onClick = {
                val batch = FermentingBatch(
                    id = if (isEditMode) editId else 0,
                    product = product.trim(),
                    ingredients = ingredients.ifBlank { null },
                    saltPct = saltPct.toDoubleOrNull(),
                    method = method.ifBlank { null },
                    vesselType = vesselType.ifBlank { null },
                    startDate = startDate,
                    endDate = if (hasEndDate && endDate > 0L) endDate else null,
                )
                if (isEditMode) viewModel.updateFermentingBatch(batch) else viewModel.addFermentingBatch(batch)
            },
            enabled = product.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
