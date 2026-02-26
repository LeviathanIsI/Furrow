package com.furrow.app.ui.finances.forms

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
import androidx.compose.ui.unit.dp
import com.furrow.app.data.local.entity.MileageLog
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.finances.FinanceViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.FinanceGlow
import com.furrow.app.util.filterDecimal

@Composable
internal fun MileageLogForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: FinanceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var miles by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("0.67") }

    if (isEditMode) {
        val existing by viewModel.getMileageLogById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                origin = it.origin ?: ""
                destination = it.destination ?: ""
                purpose = it.purpose ?: ""
                miles = it.miles?.toString() ?: ""
                rate = it.rate?.toString() ?: "0.67"
            }
        }
    }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = FinanceGlow)

    val parsedMiles = miles.toDoubleOrNull() ?: 0.0
    val parsedRate = rate.toDoubleOrNull() ?: 0.0
    val calculatedDeduction = parsedMiles * parsedRate

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        DateFieldWithToggle(
            label = "Date",
            dateMillis = date,
            onDateChange = { date = it },
            zone = viewModel.zone,
            useTodayDefault = !isEditMode,
            accentColor = FinanceGlow,
        )

        InputField(
            value = origin,
            onValueChange = { origin = it },
            label = { Text("Origin (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        InputField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        DropdownSelector(
            label = "Purpose",
            options = ModuleCatalogData.MILEAGE_PURPOSES,
            selected = purpose,
            onSelect = { purpose = it },
            accentColor = FinanceGlow,
        )

        InputField(
            value = miles,
            onValueChange = { miles = it.filterDecimal() },
            label = { Text("Miles") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        InputField(
            value = rate,
            onValueChange = { rate = it.filterDecimal() },
            label = { Text("Rate ($/mile)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Mileage" else "Save Mileage",
            onClick = {
                val mileageLog = MileageLog(
                    id = if (isEditMode) editId else 0,
                    date = date,
                    origin = origin.ifBlank { null },
                    destination = destination.ifBlank { null },
                    purpose = purpose.ifBlank { null },
                    miles = miles.toDoubleOrNull(),
                    rate = rate.toDoubleOrNull(),
                    totalDeduction = calculatedDeduction,
                )
                if (isEditMode) viewModel.updateMileageLog(mileageLog) else viewModel.addMileageLog(mileageLog)
            },
            enabled = miles.toDoubleOrNull() != null,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
