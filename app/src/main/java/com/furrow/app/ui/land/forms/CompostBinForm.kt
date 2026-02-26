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
import com.furrow.app.data.local.entity.CompostBin
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.land.LandViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.LandGlow

@Composable
internal fun CompostBinForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: LandViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var type by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var volumeCuFt by remember { mutableStateOf("") }
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var useStartDate by remember { mutableStateOf(false) }
    var maturityStage by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getCompostBinById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                type = it.type
                location = it.location ?: ""
                volumeCuFt = it.volumeCuFt?.toString() ?: ""
                if (it.startDate != null) {
                    startDate = it.startDate
                    useStartDate = true
                }
                maturityStage = it.maturityStage ?: ""
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
            options = ModuleCatalogData.COMPOST_TYPES,
            selected = type,
            onSelect = { type = it },
            accentColor = LandGlow,
        )

        InputField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            singleLine = true,
            accentColor = LandGlow,
        )

        InputField(
            value = volumeCuFt,
            onValueChange = { volumeCuFt = it },
            label = { Text("Volume (cu ft)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        DateFieldWithToggle(
            label = "Start Date",
            dateMillis = startDate,
            onDateChange = { startDate = it; useStartDate = true },
            useTodayDefault = false,
            accentColor = LandGlow,
            zone = viewModel.zone,
        )

        DropdownSelector(
            label = "Maturity Stage",
            options = ModuleCatalogData.COMPOST_STAGES,
            selected = maturityStage,
            onSelect = { maturityStage = it },
            accentColor = LandGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Compost Bin" else "Save Compost Bin",
            onClick = {
                if (type.isBlank()) return@PrimaryButton
                val bin = CompostBin(
                    id = if (isEditMode) editId else 0,
                    type = type.trim(),
                    location = location.ifBlank { null },
                    volumeCuFt = volumeCuFt.toDoubleOrNull(),
                    startDate = if (useStartDate) startDate else null,
                    maturityStage = maturityStage.ifBlank { null },
                )
                if (isEditMode) viewModel.updateCompostBin(bin) else viewModel.addCompostBin(bin)
            },
            enabled = type.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
