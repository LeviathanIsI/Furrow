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
import com.furrow.app.data.local.entity.SmokingCuringBatch
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.preservation.PreservationViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.PreservationGlow
import com.furrow.app.util.filterDecimal
import com.furrow.app.util.filterInteger

@Composable
internal fun SmokingCuringForm(
    editId: Long,
    isEditMode: Boolean,
    onBack: () -> Unit,
    viewModel: PreservationViewModel,
    snackbarHostState: SnackbarHostState,
) {
    var meatType by remember { mutableStateOf("") }
    var cut by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var cureType by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("") }
    var cureStart by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var smokeWood by remember { mutableStateOf("") }
    var smokeTempF by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getSmokingCuringBatchById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                meatType = it.meatType
                cut = it.cut ?: ""
                weight = it.weight?.toString() ?: ""
                cureType = it.cureType ?: ""
                method = it.method ?: ""
                cureStart = it.cureStart
                smokeWood = it.smokeWood ?: ""
                smokeTempF = it.smokeTempF?.toString() ?: ""
            }
        }
    }

    val title = if (isEditMode) "Edit Smoking Batch" else "Add Smoking Batch"

    FormScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) {
        DropdownSelector(
            label = "Meat Type",
            options = ModuleCatalogData.MEAT_TYPES,
            selected = meatType,
            onSelect = { meatType = it },
            accentColor = PreservationGlow,
        )

        InputField(
            value = cut,
            onValueChange = { cut = it },
            label = { Text("Cut") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = weight,
            onValueChange = { weight = it.filterDecimal() },
            label = { Text("Weight (lbs)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        DropdownSelector(
            label = "Cure Type",
            options = ModuleCatalogData.CURE_TYPES,
            selected = cureType,
            onSelect = { cureType = it },
            accentColor = PreservationGlow,
        )

        DropdownSelector(
            label = "Method",
            options = ModuleCatalogData.SMOKING_METHODS,
            selected = method,
            onSelect = { method = it },
            accentColor = PreservationGlow,
        )

        DateFieldWithToggle(
            label = "Cure Start:",
            dateMillis = cureStart,
            onDateChange = { cureStart = it },
            useTodayDefault = !isEditMode,
            accentColor = PreservationGlow,
            zone = viewModel.zone,
        )

        DropdownSelector(
            label = "Smoke Wood",
            options = ModuleCatalogData.SMOKE_WOODS,
            selected = smokeWood,
            onSelect = { smokeWood = it },
            accentColor = PreservationGlow,
        )

        InputField(
            value = smokeTempF,
            onValueChange = { smokeTempF = it.filterInteger() },
            label = { Text("Smoke Temp (\u00B0F)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Batch" else "Save Batch",
            onClick = {
                val batch = SmokingCuringBatch(
                    id = if (isEditMode) editId else 0,
                    meatType = meatType.trim(),
                    cut = cut.ifBlank { null },
                    weight = weight.toDoubleOrNull(),
                    cureType = cureType.ifBlank { null },
                    method = method.ifBlank { null },
                    cureStart = cureStart,
                    smokeWood = smokeWood.ifBlank { null },
                    smokeTempF = smokeTempF.toIntOrNull(),
                )
                if (isEditMode) viewModel.updateSmokingCuringBatch(batch) else viewModel.addSmokingCuringBatch(batch)
            },
            enabled = meatType.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
