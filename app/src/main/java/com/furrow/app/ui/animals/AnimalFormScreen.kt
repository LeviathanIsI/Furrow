package com.furrow.app.ui.animals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.Animal
import com.furrow.app.data.local.entity.BreedingRecord
import com.furrow.app.data.local.entity.FeedLog
import com.furrow.app.data.local.entity.HealthRecord
import com.furrow.app.data.local.entity.WeightLog
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.theme.AnimalsGlow
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalFormScreen(
    type: String,
    animalId: Long = 0L,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: AnimalViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L

    val title = when (type) {
        "animal" -> if (isEditMode) "Edit Animal" else "Add Animal"
        "health" -> if (isEditMode) "Edit Health Record" else "Add Health Record"
        "breeding" -> if (isEditMode) "Edit Breeding Record" else "Add Breeding Record"
        "weight" -> if (isEditMode) "Edit Weight Log" else "Add Weight Log"
        "feed" -> if (isEditMode) "Edit Feed Log" else "Add Feed Log"
        else -> "Form"
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Charcoal,
        unfocusedContainerColor = Charcoal,
        focusedBorderColor = AnimalsGlow,
        unfocusedBorderColor = BorderSubtle,
        focusedLabelColor = AnimalsGlow,
        unfocusedLabelColor = TextTertiary,
        cursorColor = AnimalsGlow,
        unfocusedTextColor = TextPrimary,
        focusedTextColor = TextPrimary,
    )

    AppScaffold(
        topBar = {
            AppTopBar(
                title = title,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                        )
                    }
                },
            )
        },
    ) { padding ->
        when (type) {
            "animal" -> AnimalForm(
                animalId = animalId,
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "health" -> HealthForm(
                animalId = animalId,
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "breeding" -> BreedingForm(
                animalId = animalId,
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "weight" -> WeightForm(
                animalId = animalId,
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "feed" -> FeedForm(
                animalId = animalId,
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

// ── Animal Form ──────────────────────────────────────────────────────────────

@Composable
private fun AnimalForm(
    animalId: Long,
    editId: Long,
    isEditMode: Boolean,
    viewModel: AnimalViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("unknown") }
    var tagId by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("active") }
    var dob by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var useDob by remember { mutableStateOf(false) }
    var acquisitionDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var source by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getAnimalById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                name = it.name ?: ""
                species = it.species
                breed = it.breed
                sex = it.sex
                tagId = it.tagId ?: ""
                status = it.status
                if (it.dob != null) {
                    dob = it.dob
                    useDob = true
                }
                acquisitionDate = it.acquisitionDate
                source = it.source ?: ""
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
            label = { Text("Name") },
            singleLine = true,
            accentColor = AnimalsGlow,
        )

        SimpleDropdown(
            label = "Species *",
            options = ModuleCatalogData.SPECIES,
            selected = species,
            onSelect = { species = it },
        )

        InputField(
            value = breed,
            onValueChange = { breed = it },
            label = { Text("Breed") },
            singleLine = true,
            accentColor = AnimalsGlow,
        )

        SimpleDropdown(
            label = "Sex",
            options = listOf("male", "female", "unknown"),
            selected = sex,
            onSelect = { sex = it },
        )

        InputField(
            value = tagId,
            onValueChange = { tagId = it },
            label = { Text("Tag ID") },
            singleLine = true,
            accentColor = AnimalsGlow,
        )

        SimpleDropdown(
            label = "Status",
            options = listOf("active", "sold", "deceased", "culled"),
            selected = status,
            onSelect = { status = it },
        )

        if (useDob || isEditMode) {
            DateFieldWithToggle(
                label = "Date of Birth",
                dateMillis = dob,
                onDateChange = { dob = it; useDob = true },
                useTodayDefault = false,
                accentColor = AnimalsGlow,
            )
        } else {
            DateFieldWithToggle(
                label = "Date of Birth",
                dateMillis = dob,
                onDateChange = { dob = it; useDob = true },
                useTodayDefault = false,
                accentColor = AnimalsGlow,
            )
        }

        DateFieldWithToggle(
            label = "Acquisition Date",
            dateMillis = acquisitionDate,
            onDateChange = { acquisitionDate = it },
            useTodayDefault = !isEditMode,
            accentColor = AnimalsGlow,
        )

        InputField(
            value = source,
            onValueChange = { source = it },
            label = { Text("Source") },
            singleLine = true,
            accentColor = AnimalsGlow,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = AnimalsGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Animal" else "Save Animal",
            onClick = {
                if (species.isBlank()) return@PrimaryButton
                val animal = Animal(
                    id = if (isEditMode) editId else 0,
                    name = name.ifBlank { null },
                    species = species.trim().lowercase(),
                    breed = breed.trim(),
                    sex = sex,
                    tagId = tagId.ifBlank { null },
                    status = status,
                    dob = if (useDob) dob else null,
                    acquisitionDate = acquisitionDate,
                    source = source.ifBlank { null },
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateAnimal(animal) else viewModel.addAnimal(animal)
                onBack()
            },
            enabled = species.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

// ── Health Record Form ───────────────────────────────────────────────────────

@Composable
private fun HealthForm(
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

        SimpleDropdown(
            label = "Type *",
            options = ModuleCatalogData.HEALTH_RECORD_TYPES,
            selected = recordType,
            onSelect = { recordType = it },
        )

        DateFieldWithToggle(
            label = "Date",
            dateMillis = date,
            onDateChange = { date = it },
            useTodayDefault = !isEditMode,
            accentColor = AnimalsGlow,
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

        SimpleDropdown(
            label = "Route",
            options = ModuleCatalogData.ADMINISTRATION_ROUTES,
            selected = route,
            onSelect = { route = it },
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
                )
                if (isEditMode) viewModel.updateHealthRecord(record) else viewModel.addHealthRecord(record)
                onBack()
            },
            enabled = recordType.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

// ── Breeding Record Form ─────────────────────────────────────────────────────

@Composable
private fun BreedingForm(
    animalId: Long,
    editId: Long,
    isEditMode: Boolean,
    viewModel: AnimalViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var breedingDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var method by remember { mutableStateOf("") }
    var dueDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var useDueDate by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getBreedingRecordById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                breedingDate = it.breedingDate
                method = it.method ?: ""
                if (it.dueDate != null) {
                    dueDate = it.dueDate
                    useDueDate = true
                }
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

        DateFieldWithToggle(
            label = "Breeding Date",
            dateMillis = breedingDate,
            onDateChange = { breedingDate = it },
            useTodayDefault = !isEditMode,
            accentColor = AnimalsGlow,
        )

        SimpleDropdown(
            label = "Method",
            options = ModuleCatalogData.BREEDING_METHODS,
            selected = method,
            onSelect = { method = it },
        )

        DateFieldWithToggle(
            label = "Due Date",
            dateMillis = dueDate,
            onDateChange = { dueDate = it; useDueDate = true },
            useTodayDefault = false,
            accentColor = AnimalsGlow,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = AnimalsGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Breeding Record" else "Save Breeding Record",
            onClick = {
                val record = BreedingRecord(
                    id = if (isEditMode) editId else 0,
                    damId = animalId,
                    breedingDate = breedingDate,
                    method = method.ifBlank { null },
                    dueDate = if (useDueDate) dueDate else null,
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateBreedingRecord(record) else viewModel.addBreedingRecord(record)
                onBack()
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

// ── Weight Log Form ──────────────────────────────────────────────────────────

@Composable
private fun WeightForm(
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
                onBack()
            },
            enabled = weight.toDoubleOrNull() != null,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

// ── Feed Log Form ────────────────────────────────────────────────────────────

@Composable
private fun FeedForm(
    animalId: Long,
    editId: Long,
    isEditMode: Boolean,
    viewModel: AnimalViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var feedType by remember { mutableStateOf("") }
    var quantityLbs by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getFeedLogById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                feedType = it.feedType
                quantityLbs = it.quantityLbs?.toString() ?: ""
                cost = it.cost?.toString() ?: ""
                supplier = it.supplier ?: ""
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

        DateFieldWithToggle(
            label = "Date",
            dateMillis = date,
            onDateChange = { date = it },
            useTodayDefault = !isEditMode,
            accentColor = AnimalsGlow,
        )

        SimpleDropdown(
            label = "Feed Type",
            options = ModuleCatalogData.FEED_TYPES,
            selected = feedType,
            onSelect = { feedType = it },
        )

        InputField(
            value = quantityLbs,
            onValueChange = { quantityLbs = it },
            label = { Text("Quantity (lbs)") },
            singleLine = true,
            accentColor = AnimalsGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
            value = supplier,
            onValueChange = { supplier = it },
            label = { Text("Supplier") },
            singleLine = true,
            accentColor = AnimalsGlow,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = AnimalsGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Feed Log" else "Save Feed Log",
            onClick = {
                if (feedType.isBlank()) return@PrimaryButton
                val log = FeedLog(
                    id = if (isEditMode) editId else 0,
                    animalId = animalId,
                    date = date,
                    feedType = feedType.trim(),
                    quantityLbs = quantityLbs.toDoubleOrNull(),
                    cost = cost.toDoubleOrNull(),
                    supplier = supplier.ifBlank { null },
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateFeedLog(log) else viewModel.addFeedLog(log)
                onBack()
            },
            enabled = feedType.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

// ── Simple Dropdown ──────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Charcoal,
                focusedContainerColor = Charcoal,
                unfocusedBorderColor = BorderSubtle,
                focusedBorderColor = AnimalsGlow,
                unfocusedLabelColor = TextTertiary,
                focusedLabelColor = AnimalsGlow,
                cursorColor = AnimalsGlow,
                unfocusedTextColor = TextPrimary,
                focusedTextColor = TextPrimary,
            ),
            shape = RoundedCornerShape(8.dp),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.replaceFirstChar { it.uppercase() }) },
                    onClick = { onSelect(option); expanded = false },
                )
            }
        }
    }
}
