package com.furrow.app.ui.animals.forms

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
import com.furrow.app.data.local.entity.WeightLog
import com.furrow.app.ui.animals.AnimalViewModel
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.theme.AnimalsGlow
import com.furrow.app.ui.theme.AppSpacing

@Composable
internal fun WeightLogForm(
    animalId: Long,
    editId: Long,
    isEditMode: Boolean,
    viewModel: AnimalViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var weight by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getWeightLogById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                weight = it.weight.toString()
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

        DateFieldWithToggle(
            label = "Date",
            dateMillis = date,
            onDateChange = { date = it },
            useTodayDefault = !isEditMode,
            accentColor = AnimalsGlow,
            zone = viewModel.zone,
        )

        InputField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (lbs) *") },
            singleLine = true,
            accentColor = AnimalsGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Weight Log" else "Save Weight Log",
            onClick = {
                val weightValue = weight.toDoubleOrNull() ?: return@PrimaryButton
                val log = WeightLog(
                    id = if (isEditMode) editId else 0,
                    animalId = animalId,
                    date = date,
                    weight = weightValue,
                )
                if (isEditMode) viewModel.updateWeightLog(log) else viewModel.addWeightLog(log)
            },
            enabled = weight.toDoubleOrNull() != null,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
