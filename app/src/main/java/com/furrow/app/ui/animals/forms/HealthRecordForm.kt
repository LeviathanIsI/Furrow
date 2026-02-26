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
import com.furrow.app.data.local.entity.HealthRecord
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.animals.AnimalViewModel
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PhotoAttachment
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.theme.AnimalsGlow
import com.furrow.app.ui.theme.AppSpacing

@Composable
internal fun HealthRecordForm(
    animalId: Long,
    editId: Long,
    isEditMode: Boolean,
    viewModel: AnimalViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var recordType by remember { mutableStateOf("") }
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var productName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var route by remember { mutableStateOf("") }
    var withdrawalPeriodDays by remember { mutableStateOf("") }
    var vetName by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }

    if (isEditMode) {
        val existing by viewModel.getHealthRecordById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                recordType = it.type
                date = it.date
                productName = it.productName ?: ""
                dosage = it.dosage ?: ""
                route = it.route ?: ""
                withdrawalPeriodDays = it.withdrawalPeriodDays?.toString() ?: ""
                vetName = it.vetName ?: ""
                diagnosis = it.diagnosis ?: ""
                cost = it.cost?.toString() ?: ""
                notes = it.notes ?: ""
                photoUri = it.photoUri
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
            options = ModuleCatalogData.HEALTH_RECORD_TYPES,
            selected = recordType,
            onSelect = { recordType = it },
            accentColor = AnimalsGlow,
        )

        DateFieldWithToggle(
            label = "Date",
            dateMillis = date,
            onDateChange = { date = it },
            useTodayDefault = !isEditMode,
            accentColor = AnimalsGlow,
            zone = viewModel.zone,
        )

        InputField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") },
            singleLine = true,
            accentColor = AnimalsGlow,
        )

        InputField(
            value = dosage,
            onValueChange = { dosage = it },
            label = { Text("Dosage") },
            singleLine = true,
            accentColor = AnimalsGlow,
        )

        DropdownSelector(
            label = "Route",
            options = ModuleCatalogData.ADMINISTRATION_ROUTES,
            selected = route,
            onSelect = { route = it },
            accentColor = AnimalsGlow,
        )

        InputField(
            value = withdrawalPeriodDays,
            onValueChange = { withdrawalPeriodDays = it },
            label = { Text("Withdrawal Period (days)") },
            singleLine = true,
            accentColor = AnimalsGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        InputField(
            value = vetName,
            onValueChange = { vetName = it },
            label = { Text("Vet Name") },
            singleLine = true,
            accentColor = AnimalsGlow,
        )

        InputField(
            value = diagnosis,
            onValueChange = { diagnosis = it },
            label = { Text("Diagnosis") },
            singleLine = true,
            accentColor = AnimalsGlow,
        )

        InputField(
            value = cost,
            onValueChange = { cost = it },
            label = { Text("Cost") },
            singleLine = true,
            accentColor = AnimalsGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = AnimalsGlow,
        )

        PhotoAttachment(
            photoUri = photoUri,
            onPhotoChanged = { photoUri = it },
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Health Record" else "Save Health Record",
            onClick = {
                if (recordType.isBlank()) return@PrimaryButton
                val withdrawalDays = withdrawalPeriodDays.toIntOrNull()
                val withdrawalEnd = if (withdrawalDays != null) {
                    date + (withdrawalDays.toLong() * 86_400_000L)
                } else {
                    null
                }
                val record = HealthRecord(
                    id = if (isEditMode) editId else 0,
                    animalId = animalId,
                    date = date,
                    type = recordType.trim(),
                    productName = productName.ifBlank { null },
                    dosage = dosage.ifBlank { null },
                    route = route.ifBlank { null },
                    withdrawalPeriodDays = withdrawalDays,
                    withdrawalEndDate = withdrawalEnd,
                    vetName = vetName.ifBlank { null },
                    diagnosis = diagnosis.ifBlank { null },
                    cost = cost.toDoubleOrNull(),
                    notes = notes.ifBlank { null },
                    photoUri = photoUri,
                )
                if (isEditMode) viewModel.updateHealthRecord(record) else viewModel.addHealthRecord(record)
            },
            enabled = recordType.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
