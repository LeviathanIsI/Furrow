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
import com.furrow.app.data.local.entity.Property
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.land.LandViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.LandGlow

@Composable
internal fun PropertyForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: LandViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var totalAcreage by remember { mutableStateOf("") }
    var zoning by remember { mutableStateOf("") }
    var county by remember { mutableStateOf("") }
    var stateProvince by remember { mutableStateOf("") }
    var elevation by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var usePurchaseDate by remember { mutableStateOf(false) }
    var assessedValue by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getPropertyById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                name = it.name
                totalAcreage = it.totalAcreage?.toString() ?: ""
                zoning = it.zoning ?: ""
                county = it.county ?: ""
                stateProvince = it.stateProvince ?: ""
                elevation = it.elevation?.toString() ?: ""
                if (it.purchaseDate != null) {
                    purchaseDate = it.purchaseDate
                    usePurchaseDate = true
                }
                assessedValue = it.assessedValue?.toString() ?: ""
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

        InputField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name *") },
            singleLine = true,
            accentColor = LandGlow,
        )

        InputField(
            value = totalAcreage,
            onValueChange = { totalAcreage = it },
            label = { Text("Total Acreage") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        DropdownSelector(
            label = "Zoning",
            options = ModuleCatalogData.ZONING_TYPES,
            selected = zoning,
            onSelect = { zoning = it },
            accentColor = LandGlow,
        )

        InputField(
            value = county,
            onValueChange = { county = it },
            label = { Text("County") },
            singleLine = true,
            accentColor = LandGlow,
        )

        InputField(
            value = stateProvince,
            onValueChange = { stateProvince = it },
            label = { Text("State / Province") },
            singleLine = true,
            accentColor = LandGlow,
        )

        InputField(
            value = elevation,
            onValueChange = { elevation = it },
            label = { Text("Elevation (ft)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        DateFieldWithToggle(
            label = "Purchase Date",
            dateMillis = purchaseDate,
            onDateChange = { purchaseDate = it; usePurchaseDate = true },
            useTodayDefault = false,
            accentColor = LandGlow,
            zone = viewModel.zone,
        )

        InputField(
            value = assessedValue,
            onValueChange = { assessedValue = it },
            label = { Text("Assessed Value ($)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Property" else "Save Property",
            onClick = {
                if (name.isBlank()) return@PrimaryButton
                val property = Property(
                    id = if (isEditMode) editId else 0,
                    name = name.trim(),
                    totalAcreage = totalAcreage.toDoubleOrNull(),
                    zoning = zoning.ifBlank { null },
                    county = county.ifBlank { null },
                    stateProvince = stateProvince.ifBlank { null },
                    elevation = elevation.toIntOrNull(),
                    purchaseDate = if (usePurchaseDate) purchaseDate else null,
                    assessedValue = assessedValue.toDoubleOrNull(),
                )
                if (isEditMode) viewModel.updateProperty(property) else viewModel.addProperty(property)
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
