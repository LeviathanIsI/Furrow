package com.furrow.app.ui.preservation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.CanningBatch
import com.furrow.app.data.local.entity.DehydratingBatch
import com.furrow.app.data.local.entity.FermentingBatch
import com.furrow.app.data.local.entity.FreezingBatch
import com.furrow.app.data.local.entity.PantryItem
import com.furrow.app.data.local.entity.SmokingCuringBatch
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.SearchableSelector
import com.furrow.app.ui.components.ToggleRow
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.PreservationGlow
import com.furrow.app.ui.theme.TextPrimary
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreservationFormScreen(
    type: String,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: PreservationViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    when (type) {
        "canning" -> CanningForm(editId = editId, isEditMode = isEditMode, onBack = onBack, viewModel = viewModel, snackbarHostState = snackbarHostState)
        "dehydrating" -> DehydratingForm(editId = editId, isEditMode = isEditMode, onBack = onBack, viewModel = viewModel, snackbarHostState = snackbarHostState)
        "fermenting" -> FermentingForm(editId = editId, isEditMode = isEditMode, onBack = onBack, viewModel = viewModel, snackbarHostState = snackbarHostState)
        "freezing" -> FreezingForm(editId = editId, isEditMode = isEditMode, onBack = onBack, viewModel = viewModel, snackbarHostState = snackbarHostState)
        "smoking" -> SmokingForm(editId = editId, isEditMode = isEditMode, onBack = onBack, viewModel = viewModel, snackbarHostState = snackbarHostState)
        "pantry" -> PantryForm(editId = editId, isEditMode = isEditMode, onBack = onBack, viewModel = viewModel, snackbarHostState = snackbarHostState)
    }
}

// -- Canning Form --

@Composable
private fun CanningForm(
    editId: Long,
    isEditMode: Boolean,
    onBack: () -> Unit,
    viewModel: PreservationViewModel,
    snackbarHostState: SnackbarHostState,
) {
    var recipeName by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("Water Bath") }
    var jarSize by remember { mutableStateOf("") }
    var jarCount by remember { mutableStateOf("") }
    var dateProcessed by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var storageLocation by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getCanningBatchById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                recipeName = it.recipeName
                method = it.method ?: "Water Bath"
                jarSize = it.jarSize ?: ""
                jarCount = it.jarCount?.toString() ?: ""
                dateProcessed = it.dateProcessed
                storageLocation = it.storageLocation ?: ""
                notes = it.ingredientsList ?: ""
            }
        }
    }

    val title = if (isEditMode) "Edit Canning Batch" else "Add Canning Batch"

    FormScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) {
        InputField(
            value = recipeName,
            onValueChange = { recipeName = it },
            label = { Text("Recipe Name") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        SearchableSelector(
            query = method,
            onQueryChange = { method = it },
            items = ModuleCatalogData.CANNING_METHODS.filter {
                it.contains(method, ignoreCase = true)
            },
            onItemSelected = { method = it },
            label = "Method",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { method = it },
        )

        SearchableSelector(
            query = jarSize,
            onQueryChange = { jarSize = it },
            items = ModuleCatalogData.JAR_SIZES.filter {
                it.contains(jarSize, ignoreCase = true)
            },
            onItemSelected = { jarSize = it },
            label = "Jar Size",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { jarSize = it },
        )

        InputField(
            value = jarCount,
            onValueChange = { jarCount = it.filter { c -> c.isDigit() } },
            label = { Text("Jar Count") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        DateFieldWithToggle(
            label = "Date Processed:",
            dateMillis = dateProcessed,
            onDateChange = { dateProcessed = it },
            useTodayDefault = !isEditMode,
            accentColor = PreservationGlow,
            zone = viewModel.zone,
        )

        SearchableSelector(
            query = storageLocation,
            onQueryChange = { storageLocation = it },
            items = ModuleCatalogData.STORAGE_LOCATIONS.filter {
                it.contains(storageLocation, ignoreCase = true)
            },
            onItemSelected = { storageLocation = it },
            label = "Storage Location",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { storageLocation = it },
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = PreservationGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Batch" else "Save Batch",
            onClick = {
                val batch = CanningBatch(
                    id = if (isEditMode) editId else 0,
                    recipeName = recipeName.trim(),
                    method = method.ifBlank { null },
                    jarSize = jarSize.ifBlank { null },
                    jarCount = jarCount.toIntOrNull(),
                    dateProcessed = dateProcessed,
                    storageLocation = storageLocation.ifBlank { null },
                    ingredientsList = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateCanningBatch(batch) else viewModel.addCanningBatch(batch)
                onBack()
            },
            enabled = recipeName.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// -- Dehydrating Form --

@Composable
private fun DehydratingForm(
    editId: Long,
    isEditMode: Boolean,
    onBack: () -> Unit,
    viewModel: PreservationViewModel,
    snackbarHostState: SnackbarHostState,
) {
    var product by remember { mutableStateOf("") }
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
            onValueChange = { weightBeforeLbs = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Weight Before (lbs)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = weightAfterLbs,
            onValueChange = { weightAfterLbs = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Weight After (lbs)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = dehydratorTempF,
            onValueChange = { dehydratorTempF = it.filter { c -> c.isDigit() } },
            label = { Text("Dehydrator Temp (\u00B0F)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = dryingTimeHrs,
            onValueChange = { dryingTimeHrs = it.filter { c -> c.isDigit() || c == '.' } },
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
                onBack()
            },
            enabled = product.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// -- Fermenting Form --

@Composable
private fun FermentingForm(
    editId: Long,
    isEditMode: Boolean,
    onBack: () -> Unit,
    viewModel: PreservationViewModel,
    snackbarHostState: SnackbarHostState,
) {
    var product by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var saltPct by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("") }
    var vesselType by remember { mutableStateOf("") }
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableLongStateOf(0L) }
    var hasEndDate by remember { mutableStateOf(false) }

    if (isEditMode) {
        val existing by viewModel.getFermentingBatchById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                product = it.product
                ingredients = it.ingredients ?: ""
                saltPct = it.saltPct?.toString() ?: ""
                method = it.method ?: ""
                vesselType = it.vesselType ?: ""
                startDate = it.startDate
                if (it.endDate != null) {
                    hasEndDate = true
                    endDate = it.endDate
                }
            }
        }
    }

    val title = if (isEditMode) "Edit Fermenting Batch" else "Add Fermenting Batch"

    FormScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) {
        InputField(
            value = product,
            onValueChange = { product = it },
            label = { Text("Product") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = ingredients,
            onValueChange = { ingredients = it },
            label = { Text("Ingredients") },
            minLines = 2,
            accentColor = PreservationGlow,
        )

        InputField(
            value = saltPct,
            onValueChange = { saltPct = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Salt %") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        SearchableSelector(
            query = method,
            onQueryChange = { method = it },
            items = ModuleCatalogData.FERMENTATION_METHODS.filter {
                it.contains(method, ignoreCase = true)
            },
            onItemSelected = { method = it },
            label = "Method",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { method = it },
        )

        SearchableSelector(
            query = vesselType,
            onQueryChange = { vesselType = it },
            items = ModuleCatalogData.VESSEL_TYPES.filter {
                it.contains(vesselType, ignoreCase = true)
            },
            onItemSelected = { vesselType = it },
            label = "Vessel Type",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { vesselType = it },
        )

        DateFieldWithToggle(
            label = "Start Date:",
            dateMillis = startDate,
            onDateChange = { startDate = it },
            useTodayDefault = !isEditMode,
            accentColor = PreservationGlow,
            zone = viewModel.zone,
        )

        ToggleRow(
            label = "Has End Date",
            checked = hasEndDate,
            accentColor = PreservationGlow,
            onCheckedChange = { checked ->
                hasEndDate = checked
                if (checked && endDate == 0L) {
                    endDate = System.currentTimeMillis()
                }
            },
        )

        if (hasEndDate) {
            DateFieldWithToggle(
                label = "End Date:",
                dateMillis = if (endDate > 0L) endDate else System.currentTimeMillis(),
                onDateChange = { endDate = it },
                useTodayDefault = false,
                accentColor = PreservationGlow,
                zone = viewModel.zone,
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Batch" else "Save Batch",
            onClick = {
                val batch = FermentingBatch(
                    id = if (isEditMode) editId else 0,
                    product = product.trim(),
                    ingredients = ingredients.ifBlank { null },
                    saltPct = saltPct.toDoubleOrNull(),
                    method = method.ifBlank { null },
                    vesselType = vesselType.ifBlank { null },
                    startDate = startDate,
                    endDate = if (hasEndDate && endDate > 0L) endDate else null,
                )
                if (isEditMode) viewModel.updateFermentingBatch(batch) else viewModel.addFermentingBatch(batch)
                onBack()
            },
            enabled = product.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// -- Freezing Form --

@Composable
private fun FreezingForm(
    editId: Long,
    isEditMode: Boolean,
    onBack: () -> Unit,
    viewModel: PreservationViewModel,
    snackbarHostState: SnackbarHostState,
) {
    var item by remember { mutableStateOf("") }
    var quantityLbs by remember { mutableStateOf("") }
    var packagingMethod by remember { mutableStateOf("") }
    var blanched by remember { mutableStateOf(false) }
    var dateFrozen by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var freezerId by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getFreezingBatchById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                item = it.item
                quantityLbs = it.quantityLbs?.toString() ?: ""
                packagingMethod = it.packagingMethod ?: ""
                blanched = it.blanched
                dateFrozen = it.dateFrozen
                freezerId = it.freezerId ?: ""
            }
        }
    }

    val title = if (isEditMode) "Edit Freezing Batch" else "Add Freezing Batch"

    FormScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) {
        InputField(
            value = item,
            onValueChange = { item = it },
            label = { Text("Item") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = quantityLbs,
            onValueChange = { quantityLbs = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Quantity (lbs)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        SearchableSelector(
            query = packagingMethod,
            onQueryChange = { packagingMethod = it },
            items = ModuleCatalogData.PACKAGING_METHODS.filter {
                it.contains(packagingMethod, ignoreCase = true)
            },
            onItemSelected = { packagingMethod = it },
            label = "Packaging Method",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { packagingMethod = it },
        )

        ToggleRow(
            label = "Blanched",
            checked = blanched,
            accentColor = PreservationGlow,
            onCheckedChange = { blanched = it },
        )

        DateFieldWithToggle(
            label = "Date Frozen:",
            dateMillis = dateFrozen,
            onDateChange = { dateFrozen = it },
            useTodayDefault = !isEditMode,
            accentColor = PreservationGlow,
            zone = viewModel.zone,
        )

        InputField(
            value = freezerId,
            onValueChange = { freezerId = it },
            label = { Text("Freezer ID") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Batch" else "Save Batch",
            onClick = {
                val batch = FreezingBatch(
                    id = if (isEditMode) editId else 0,
                    item = item.trim(),
                    quantityLbs = quantityLbs.toDoubleOrNull(),
                    packagingMethod = packagingMethod.ifBlank { null },
                    blanched = blanched,
                    dateFrozen = dateFrozen,
                    freezerId = freezerId.ifBlank { null },
                )
                if (isEditMode) viewModel.updateFreezingBatch(batch) else viewModel.addFreezingBatch(batch)
                onBack()
            },
            enabled = item.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// -- Smoking Form --

@Composable
private fun SmokingForm(
    editId: Long,
    isEditMode: Boolean,
    onBack: () -> Unit,
    viewModel: PreservationViewModel,
    snackbarHostState: SnackbarHostState,
) {
    var meatType by remember { mutableStateOf("") }
    var cut by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var cureType by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("") }
    var cureStart by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var smokeWood by remember { mutableStateOf("") }
    var smokeTempF by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getSmokingCuringBatchById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                meatType = it.meatType
                cut = it.cut ?: ""
                weight = it.weight?.toString() ?: ""
                cureType = it.cureType ?: ""
                method = it.method ?: ""
                cureStart = it.cureStart
                smokeWood = it.smokeWood ?: ""
                smokeTempF = it.smokeTempF?.toString() ?: ""
            }
        }
    }

    val title = if (isEditMode) "Edit Smoking Batch" else "Add Smoking Batch"

    FormScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) {
        SearchableSelector(
            query = meatType,
            onQueryChange = { meatType = it },
            items = ModuleCatalogData.MEAT_TYPES.filter {
                it.contains(meatType, ignoreCase = true)
            },
            onItemSelected = { meatType = it },
            label = "Meat Type",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { meatType = it },
        )

        InputField(
            value = cut,
            onValueChange = { cut = it },
            label = { Text("Cut") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        InputField(
            value = weight,
            onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Weight (lbs)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        SearchableSelector(
            query = cureType,
            onQueryChange = { cureType = it },
            items = ModuleCatalogData.CURE_TYPES.filter {
                it.contains(cureType, ignoreCase = true)
            },
            onItemSelected = { cureType = it },
            label = "Cure Type",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { cureType = it },
        )

        SearchableSelector(
            query = method,
            onQueryChange = { method = it },
            items = ModuleCatalogData.SMOKING_METHODS.filter {
                it.contains(method, ignoreCase = true)
            },
            onItemSelected = { method = it },
            label = "Method",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { method = it },
        )

        DateFieldWithToggle(
            label = "Cure Start:",
            dateMillis = cureStart,
            onDateChange = { cureStart = it },
            useTodayDefault = !isEditMode,
            accentColor = PreservationGlow,
            zone = viewModel.zone,
        )

        SearchableSelector(
            query = smokeWood,
            onQueryChange = { smokeWood = it },
            items = ModuleCatalogData.SMOKE_WOODS.filter {
                it.contains(smokeWood, ignoreCase = true)
            },
            onItemSelected = { smokeWood = it },
            label = "Smoke Wood",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { smokeWood = it },
        )

        InputField(
            value = smokeTempF,
            onValueChange = { smokeTempF = it.filter { c -> c.isDigit() } },
            label = { Text("Smoke Temp (\u00B0F)") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Batch" else "Save Batch",
            onClick = {
                val batch = SmokingCuringBatch(
                    id = if (isEditMode) editId else 0,
                    meatType = meatType.trim(),
                    cut = cut.ifBlank { null },
                    weight = weight.toDoubleOrNull(),
                    cureType = cureType.ifBlank { null },
                    method = method.ifBlank { null },
                    cureStart = cureStart,
                    smokeWood = smokeWood.ifBlank { null },
                    smokeTempF = smokeTempF.toIntOrNull(),
                )
                if (isEditMode) viewModel.updateSmokingCuringBatch(batch) else viewModel.addSmokingCuringBatch(batch)
                onBack()
            },
            enabled = meatType.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// -- Pantry Form --

@Composable
private fun PantryForm(
    editId: Long,
    isEditMode: Boolean,
    onBack: () -> Unit,
    viewModel: PreservationViewModel,
    snackbarHostState: SnackbarHostState,
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var storageLocation by remember { mutableStateOf("") }
    var dateProduced by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var hasDateProduced by remember { mutableStateOf(true) }
    var expirationDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var hasExpirationDate by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("In Stock") }

    if (isEditMode) {
        val existing by viewModel.getPantryItemById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                name = it.name
                category = it.category
                quantity = it.quantity?.toString() ?: ""
                unit = it.unit ?: ""
                storageLocation = it.storageLocation ?: ""
                if (it.dateProduced != null) {
                    hasDateProduced = true
                    dateProduced = it.dateProduced
                } else {
                    hasDateProduced = false
                }
                if (it.expirationDate != null) {
                    hasExpirationDate = true
                    expirationDate = it.expirationDate
                } else {
                    hasExpirationDate = false
                }
                status = it.status
            }
        }
    }

    val title = if (isEditMode) "Edit Pantry Item" else "Add Pantry Item"

    FormScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) {
        InputField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        SearchableSelector(
            query = category,
            onQueryChange = { category = it },
            items = ModuleCatalogData.PANTRY_CATEGORIES.filter {
                it.contains(category, ignoreCase = true)
            },
            onItemSelected = { category = it },
            label = "Category",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { category = it },
        )

        InputField(
            value = quantity,
            onValueChange = { quantity = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Quantity") },
            singleLine = true,
            accentColor = PreservationGlow,
        )

        SearchableSelector(
            query = unit,
            onQueryChange = { unit = it },
            items = ModuleCatalogData.PANTRY_UNITS.filter {
                it.contains(unit, ignoreCase = true)
            },
            onItemSelected = { unit = it },
            label = "Unit",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { unit = it },
        )

        SearchableSelector(
            query = storageLocation,
            onQueryChange = { storageLocation = it },
            items = ModuleCatalogData.STORAGE_LOCATIONS.filter {
                it.contains(storageLocation, ignoreCase = true)
            },
            onItemSelected = { storageLocation = it },
            label = "Storage Location",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { storageLocation = it },
        )

        ToggleRow(
            label = "Date Produced",
            checked = hasDateProduced,
            accentColor = PreservationGlow,
            onCheckedChange = { hasDateProduced = it },
        )

        if (hasDateProduced) {
            DateFieldWithToggle(
                label = "Date Produced:",
                dateMillis = dateProduced,
                onDateChange = { dateProduced = it },
                useTodayDefault = !isEditMode,
                accentColor = PreservationGlow,
                zone = viewModel.zone,
            )
        }

        ToggleRow(
            label = "Expiration Date",
            checked = hasExpirationDate,
            accentColor = PreservationGlow,
            onCheckedChange = { checked ->
                hasExpirationDate = checked
                if (checked && expirationDate == 0L) {
                    expirationDate = System.currentTimeMillis()
                }
            },
        )

        if (hasExpirationDate) {
            DateFieldWithToggle(
                label = "Expiration Date:",
                dateMillis = if (expirationDate > 0L) expirationDate else System.currentTimeMillis(),
                onDateChange = { expirationDate = it },
                useTodayDefault = false,
                accentColor = PreservationGlow,
                zone = viewModel.zone,
            )
        }

        SearchableSelector(
            query = status,
            onQueryChange = { status = it },
            items = ModuleCatalogData.PANTRY_STATUSES.filter {
                it.contains(status, ignoreCase = true)
            },
            onItemSelected = { status = it },
            label = "Status",
            nameSelector = { it },
            accentColor = PreservationGlow,
            onAddCustom = { status = it },
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Item" else "Save Item",
            onClick = {
                val item = PantryItem(
                    id = if (isEditMode) editId else 0,
                    name = name.trim(),
                    category = category.trim(),
                    quantity = quantity.toDoubleOrNull(),
                    unit = unit.ifBlank { null },
                    storageLocation = storageLocation.ifBlank { null },
                    dateProduced = if (hasDateProduced) dateProduced else null,
                    expirationDate = if (hasExpirationDate && expirationDate > 0L) expirationDate else null,
                    status = status,
                )
                if (isEditMode) viewModel.updatePantryItem(item) else viewModel.addPantryItem(item)
                onBack()
            },
            enabled = name.isNotBlank() && category.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// -- Shared Form Scaffold --

@Composable
private fun FormScaffold(
    title: String,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    content: @Composable () -> Unit,
) {
    AppScaffold(
        snackbarHostState = snackbarHostState,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            content()
        }
    }
}
