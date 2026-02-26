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
import com.furrow.app.data.local.entity.DehydratingBatch
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.preservation.PreservationViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.PreservationGlow
import com.furrow.app.util.filterDecimal
import com.furrow.app.util.filterInteger

@Composable
internal fun DehydratingForm(
    editId: Long,
    isEditMode: Boolean,
    onBack: () -> Unit,
    viewModel: PreservationViewModel,
    snackbarHostState: SnackbarHostState,
    initialItemName: String = "",
) {
    var product by remember { mutableStateOf(initialItemName) }
    var weightBeforeLbs by remember { mutableStateOf("") }
    var weightAfterLbs by remember { mutableStateOf("") }
    var dehydratorTempF by remember { mutableStateOf("") }
    var dryingTimeHrs by remember { mutableStateOf("") }
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }

    if (isEditMode) {
        val existing by viewModel.getDehydratingBatchById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                product = it.product
                weightBeforeLbs = it.weightBeforeLbs?.toString() ?: ""
                weightAfterLbs = it.weightAfterLbs?.toString() ?: ""
                dehydratorTempF = it.dehydratorTempF?.toString() ?: ""
                dryingTimeHrs = it.dryingTimeHrs?.toString() ?: ""
                date = it.date
            }
        }
    }

    val title = if (isEditMode) "Edit Dehydrating Batch" else "Add Dehydrating Batch"

    FormScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) {
        InputField(
            value = product,
            onValueChange = { product = it },
            label = { Text("Product") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = weightBeforeLbs,
            onValueChange = { weightBeforeLbs = it.filterDecimal() },
            label = { Text("Weight Before (lbs)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = weightAfterLbs,
            onValueChange = { weightAfterLbs = it.filterDecimal() },
            label = { Text("Weight After (lbs)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = dehydratorTempF,
            onValueChange = { dehydratorTempF = it.filterInteger() },
            label = { Text("Dehydrator Temp (\u00B0F)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = dryingTimeHrs,
            onValueChange = { dryingTimeHrs = it.filterDecimal() },
            label = { Text("Drying Time (hrs)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        DateFieldWithToggle(
            label = "Date:",
            dateMillis = date,
            onDateChange = { date = it },
            useTodayDefault = !isEditMode,
            accentColor = PreservationGlow,
            zone = viewModel.zone,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Batch" else "Save Batch",
            onClick = {
                val batch = DehydratingBatch(
                    id = if (isEditMode) editId else 0,
                    product = product.trim(),
                    weightBeforeLbs = weightBeforeLbs.toDoubleOrNull(),
                    weightAfterLbs = weightAfterLbs.toDoubleOrNull(),
                    dehydratorTempF = dehydratorTempF.toIntOrNull(),
                    dryingTimeHrs = dryingTimeHrs.toDoubleOrNull(),
                    date = date,
                )
                if (isEditMode) viewModel.updateDehydratingBatch(batch) else viewModel.addDehydratingBatch(batch)
            },
            enabled = product.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
