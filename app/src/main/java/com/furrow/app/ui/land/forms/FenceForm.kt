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
import com.furrow.app.data.local.entity.Fence
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.land.LandViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.LandGlow

@Composable
internal fun FenceForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: LandViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var type by remember { mutableStateOf("") }
    var lengthFt by remember { mutableStateOf("") }
    var heightFt by remember { mutableStateOf("") }
    var installDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var useInstallDate by remember { mutableStateOf(false) }
    var materialCost by remember { mutableStateOf("") }
    var laborCost by remember { mutableStateOf("") }
    var conditionRating by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getFenceById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                type = it.type
                lengthFt = it.lengthFt?.toString() ?: ""
                heightFt = it.heightFt?.toString() ?: ""
                if (it.installDate != null) {
                    installDate = it.installDate
                    useInstallDate = true
                }
                materialCost = it.materialCost?.toString() ?: ""
                laborCost = it.laborCost?.toString() ?: ""
                conditionRating = it.conditionRating?.toString() ?: ""
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
            options = ModuleCatalogData.FENCE_TYPES,
            selected = type,
            onSelect = { type = it },
            accentColor = LandGlow,
        )

        InputField(
            value = lengthFt,
            onValueChange = { lengthFt = it },
            label = { Text("Length (ft)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = heightFt,
            onValueChange = { heightFt = it },
            label = { Text("Height (ft)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        DateFieldWithToggle(
            label = "Install Date",
            dateMillis = installDate,
            onDateChange = { installDate = it; useInstallDate = true },
            useTodayDefault = false,
            accentColor = LandGlow,
            zone = viewModel.zone,
        )

        InputField(
            value = materialCost,
            onValueChange = { materialCost = it },
            label = { Text("Material Cost ($)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = laborCost,
            onValueChange = { laborCost = it },
            label = { Text("Labor Cost ($)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = conditionRating,
            onValueChange = { conditionRating = it },
            label = { Text("Condition Rating (1-5)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Fence" else "Save Fence",
            onClick = {
                if (type.isBlank()) return@PrimaryButton
                val fence = Fence(
                    id = if (isEditMode) editId else 0,
                    type = type.trim(),
                    lengthFt = lengthFt.toDoubleOrNull(),
                    heightFt = heightFt.toDoubleOrNull(),
                    installDate = if (useInstallDate) installDate else null,
                    materialCost = materialCost.toDoubleOrNull(),
                    laborCost = laborCost.toDoubleOrNull(),
                    conditionRating = conditionRating.toIntOrNull()?.coerceIn(1, 5),
                )
                if (isEditMode) viewModel.updateFence(fence) else viewModel.addFence(fence)
            },
            enabled = type.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
