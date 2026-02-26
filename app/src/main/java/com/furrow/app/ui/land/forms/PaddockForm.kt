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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.furrow.app.data.local.entity.Paddock
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.land.LandViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.LandGlow

@Composable
internal fun PaddockForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: LandViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var acreage by remember { mutableStateOf("") }
    var forageType by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getPaddockById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                name = it.name
                acreage = it.acreage?.toString() ?: ""
                forageType = it.forageType ?: ""
                notes = it.notes ?: ""
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
            value = acreage,
            onValueChange = { acreage = it },
            label = { Text("Acreage") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        DropdownSelector(
            label = "Forage Type",
            options = ModuleCatalogData.FORAGE_TYPES,
            selected = forageType,
            onSelect = { forageType = it },
            accentColor = LandGlow,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = LandGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Paddock" else "Save Paddock",
            onClick = {
                if (name.isBlank()) return@PrimaryButton
                val paddock = Paddock(
                    id = if (isEditMode) editId else 0,
                    name = name.trim(),
                    acreage = acreage.toDoubleOrNull(),
                    forageType = forageType.ifBlank { null },
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updatePaddock(paddock) else viewModel.addPaddock(paddock)
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
