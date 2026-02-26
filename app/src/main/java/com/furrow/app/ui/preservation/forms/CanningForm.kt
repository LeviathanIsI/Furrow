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
import com.furrow.app.data.local.entity.CanningBatch
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.preservation.PreservationViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.PreservationGlow
import com.furrow.app.util.filterInteger

@Composable
internal fun CanningForm(
    editId: Long,
    isEditMode: Boolean,
    onBack: () -> Unit,
    viewModel: PreservationViewModel,
    snackbarHostState: SnackbarHostState,
    initialItemName: String = "",
) {
    var recipeName by remember { mutableStateOf(initialItemName) }
    var method by remember { mutableStateOf("Water Bath") }
    var jarSize by remember { mutableStateOf("") }
    var jarCount by remember { mutableStateOf("") }
    var dateProcessed by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var storageLocation by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getCanningBatchById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                recipeName = it.recipeName
                method = it.method ?: "Water Bath"
                jarSize = it.jarSize ?: ""
                jarCount = it.jarCount?.toString() ?: ""
                dateProcessed = it.dateProcessed
                storageLocation = it.storageLocation ?: ""
                notes = it.ingredientsList ?: ""
            }
        }
    }

    val title = if (isEditMode) "Edit Canning Batch" else "Add Canning Batch"

    FormScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) {
        InputField(
            value = recipeName,
            onValueChange = { recipeName = it },
            label = { Text("Recipe Name") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        DropdownSelector(
            label = "Method",
            options = ModuleCatalogData.CANNING_METHODS,
            selected = method,
            onSelect = { method = it },
            accentColor = PreservationGlow,
        )

        DropdownSelector(
            label = "Jar Size",
            options = ModuleCatalogData.JAR_SIZES,
            selected = jarSize,
            onSelect = { jarSize = it },
            accentColor = PreservationGlow,
        )

        InputField(
            value = jarCount,
            onValueChange = { jarCount = it.filterInteger() },
            label = { Text("Jar Count") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        DateFieldWithToggle(
            label = "Date Processed:",
            dateMillis = dateProcessed,
            onDateChange = { dateProcessed = it },
            useTodayDefault = !isEditMode,
            accentColor = PreservationGlow,
            zone = viewModel.zone,
        )

        DropdownSelector(
            label = "Storage Location",
            options = ModuleCatalogData.STORAGE_LOCATIONS,
            selected = storageLocation,
            onSelect = { storageLocation = it },
            accentColor = PreservationGlow,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = PreservationGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Batch" else "Save Batch",
            onClick = {
                val batch = CanningBatch(
                    id = if (isEditMode) editId else 0,
                    recipeName = recipeName.trim(),
                    method = method.ifBlank { null },
                    jarSize = jarSize.ifBlank { null },
                    jarCount = jarCount.toIntOrNull(),
                    dateProcessed = dateProcessed,
                    storageLocation = storageLocation.ifBlank { null },
                    ingredientsList = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateCanningBatch(batch) else viewModel.addCanningBatch(batch)
            },
            enabled = recipeName.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
