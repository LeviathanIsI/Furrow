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
import com.furrow.app.data.local.entity.PantryItem
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
internal fun PantryItemForm(
    editId: Long,
    isEditMode: Boolean,
    onBack: () -> Unit,
    viewModel: PreservationViewModel,
    snackbarHostState: SnackbarHostState,
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var storageLocation by remember { mutableStateOf("") }
    var dateProduced by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var hasDateProduced by remember { mutableStateOf(true) }
    var expirationDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var hasExpirationDate by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("In Stock") }

    if (isEditMode) {
        val existing by viewModel.getPantryItemById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                name = it.name
                category = it.category
                quantity = it.quantity?.toString() ?: ""
                unit = it.unit ?: ""
                storageLocation = it.storageLocation ?: ""
                if (it.dateProduced != null) {
                    hasDateProduced = true
                    dateProduced = it.dateProduced
                } else {
                    hasDateProduced = false
                }
                if (it.expirationDate != null) {
                    hasExpirationDate = true
                    expirationDate = it.expirationDate
                } else {
                    hasExpirationDate = false
                }
                status = it.status
            }
        }
    }

    val title = if (isEditMode) "Edit Pantry Item" else "Add Pantry Item"

    FormScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) {
        InputField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        DropdownSelector(
            label = "Category",
            options = ModuleCatalogData.PANTRY_CATEGORIES,
            selected = category,
            onSelect = { category = it },
            accentColor = PreservationGlow,
        )

        InputField(
            value = quantity,
            onValueChange = { quantity = it.filterDecimal() },
            label = { Text("Quantity") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        DropdownSelector(
            label = "Unit",
            options = ModuleCatalogData.PANTRY_UNITS,
            selected = unit,
            onSelect = { unit = it },
            accentColor = PreservationGlow,
        )

        DropdownSelector(
            label = "Storage Location",
            options = ModuleCatalogData.STORAGE_LOCATIONS,
            selected = storageLocation,
            onSelect = { storageLocation = it },
            accentColor = PreservationGlow,
        )

        ToggleRow(
            label = "Date Produced",
            checked = hasDateProduced,
            accentColor = PreservationGlow,
            onCheckedChange = { hasDateProduced = it },
        )

        if (hasDateProduced) {
            DateFieldWithToggle(
                label = "Date Produced:",
                dateMillis = dateProduced,
                onDateChange = { dateProduced = it },
                useTodayDefault = !isEditMode,
                accentColor = PreservationGlow,
                zone = viewModel.zone,
            )
        }

        ToggleRow(
            label = "Expiration Date",
            checked = hasExpirationDate,
            accentColor = PreservationGlow,
            onCheckedChange = { checked ->
                hasExpirationDate = checked
                if (checked && expirationDate == 0L) {
                    expirationDate = System.currentTimeMillis()
                }
            },
        )

        if (hasExpirationDate) {
            DateFieldWithToggle(
                label = "Expiration Date:",
                dateMillis = if (expirationDate > 0L) expirationDate else System.currentTimeMillis(),
                onDateChange = { expirationDate = it },
                useTodayDefault = false,
                accentColor = PreservationGlow,
                zone = viewModel.zone,
            )
        }

        DropdownSelector(
            label = "Status",
            options = ModuleCatalogData.PANTRY_STATUSES,
            selected = status,
            onSelect = { status = it },
            accentColor = PreservationGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Item" else "Save Item",
            onClick = {
                val item = PantryItem(
                    id = if (isEditMode) editId else 0,
                    name = name.trim(),
                    category = category.trim(),
                    quantity = quantity.toDoubleOrNull(),
                    unit = unit.ifBlank { null },
                    storageLocation = storageLocation.ifBlank { null },
                    dateProduced = if (hasDateProduced) dateProduced else null,
                    expirationDate = if (hasExpirationDate && expirationDate > 0L) expirationDate else null,
                    status = status,
                )
                if (isEditMode) viewModel.updatePantryItem(item) else viewModel.addPantryItem(item)
            },
            enabled = name.isNotBlank() && category.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
