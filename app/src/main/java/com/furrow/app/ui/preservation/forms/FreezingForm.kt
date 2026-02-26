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
import com.furrow.app.data.local.entity.FreezingBatch
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
internal fun FreezingForm(
    editId: Long,
    isEditMode: Boolean,
    onBack: () -> Unit,
    viewModel: PreservationViewModel,
    snackbarHostState: SnackbarHostState,
    initialItemName: String = "",
) {
    var item by remember { mutableStateOf(initialItemName) }
    var quantityLbs by remember { mutableStateOf("") }
    var packagingMethod by remember { mutableStateOf("") }
    var blanched by remember { mutableStateOf(false) }
    var dateFrozen by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var freezerId by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getFreezingBatchById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                item = it.item
                quantityLbs = it.quantityLbs?.toString() ?: ""
                packagingMethod = it.packagingMethod ?: ""
                blanched = it.blanched
                dateFrozen = it.dateFrozen
                freezerId = it.freezerId ?: ""
            }
        }
    }

    val title = if (isEditMode) "Edit Freezing Batch" else "Add Freezing Batch"

    FormScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) {
        InputField(
            value = item,
            onValueChange = { item = it },
            label = { Text("Item") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = quantityLbs,
            onValueChange = { quantityLbs = it.filterDecimal() },
            label = { Text("Quantity (lbs)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        DropdownSelector(
            label = "Packaging Method",
            options = ModuleCatalogData.PACKAGING_METHODS,
            selected = packagingMethod,
            onSelect = { packagingMethod = it },
            accentColor = PreservationGlow,
        )

        ToggleRow(
            label = "Blanched",
            checked = blanched,
            accentColor = PreservationGlow,
            onCheckedChange = { blanched = it },
        )

        DateFieldWithToggle(
            label = "Date Frozen:",
            dateMillis = dateFrozen,
            onDateChange = { dateFrozen = it },
            useTodayDefault = !isEditMode,
            accentColor = PreservationGlow,
            zone = viewModel.zone,
        )

        InputField(
            value = freezerId,
            onValueChange = { freezerId = it },
            label = { Text("Freezer ID") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Batch" else "Save Batch",
            onClick = {
                val batch = FreezingBatch(
                    id = if (isEditMode) editId else 0,
                    item = item.trim(),
                    quantityLbs = quantityLbs.toDoubleOrNull(),
                    packagingMethod = packagingMethod.ifBlank { null },
                    blanched = blanched,
                    dateFrozen = dateFrozen,
                    freezerId = freezerId.ifBlank { null },
                )
                if (isEditMode) viewModel.updateFreezingBatch(batch) else viewModel.addFreezingBatch(batch)
            },
            enabled = item.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
