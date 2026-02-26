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
import com.furrow.app.data.local.entity.WaterSource
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.land.LandViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.LandGlow

@Composable
internal fun WaterSourceForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: LandViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var type by remember { mutableStateOf("") }
    var capacityGal by remember { mutableStateOf("") }
    var flowRateGpm by remember { mutableStateOf("") }
    var pumpType by remember { mutableStateOf("") }
    var waterTestDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var useWaterTestDate by remember { mutableStateOf(false) }
    var ph by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getWaterSourceById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                type = it.type
                capacityGal = it.capacityGal?.toString() ?: ""
                flowRateGpm = it.flowRateGpm?.toString() ?: ""
                pumpType = it.pumpType ?: ""
                if (it.waterTestDate != null) {
                    waterTestDate = it.waterTestDate
                    useWaterTestDate = true
                }
                ph = it.ph?.toString() ?: ""
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

        DropdownSelector(
            label = "Type *",
            options = ModuleCatalogData.WATER_SOURCE_TYPES,
            selected = type,
            onSelect = { type = it },
            accentColor = LandGlow,
        )

        InputField(
            value = capacityGal,
            onValueChange = { capacityGal = it },
            label = { Text("Capacity (gal)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = flowRateGpm,
            onValueChange = { flowRateGpm = it },
            label = { Text("Flow Rate (gpm)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        DropdownSelector(
            label = "Pump Type",
            options = ModuleCatalogData.PUMP_TYPES,
            selected = pumpType,
            onSelect = { pumpType = it },
            accentColor = LandGlow,
        )

        DateFieldWithToggle(
            label = "Water Test Date",
            dateMillis = waterTestDate,
            onDateChange = { waterTestDate = it; useWaterTestDate = true },
            useTodayDefault = false,
            accentColor = LandGlow,
            zone = viewModel.zone,
        )

        InputField(
            value = ph,
            onValueChange = { ph = it },
            label = { Text("pH") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Water Source" else "Save Water Source",
            onClick = {
                if (type.isBlank()) return@PrimaryButton
                val waterSource = WaterSource(
                    id = if (isEditMode) editId else 0,
                    type = type.trim(),
                    capacityGal = capacityGal.toDoubleOrNull(),
                    flowRateGpm = flowRateGpm.toDoubleOrNull(),
                    pumpType = pumpType.ifBlank { null },
                    waterTestDate = if (useWaterTestDate) waterTestDate else null,
                    ph = ph.toDoubleOrNull(),
                )
                if (isEditMode) viewModel.updateWaterSource(waterSource) else viewModel.addWaterSource(waterSource)
            },
            enabled = type.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
