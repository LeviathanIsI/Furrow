package com.furrow.app.ui.orchard

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.BloomRecord
import com.furrow.app.data.local.entity.GraftingLog
import com.furrow.app.data.local.entity.OrchardHarvest
import com.furrow.app.data.local.entity.OrchardPlant
import com.furrow.app.data.local.entity.PruningLog
import com.furrow.app.data.local.entity.SprayLog
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.data.OrchardCatalogData
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.SearchableSelector
import com.furrow.app.ui.components.ToggleRow
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.OrchardGlow
import com.furrow.app.ui.theme.TextPrimary
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrchardFormScreen(
    type: String,
    plantId: Long = 0L,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: OrchardViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L
    val formTitle = buildString {
        append(if (isEditMode) "Edit " else "Add ")
        append(
            when (type) {
                "plant" -> "Plant"
                "harvest" -> "Harvest"
                "pruning" -> "Pruning Log"
                "spray" -> "Spray Log"
                "bloom" -> "Bloom Record"
                "grafting" -> "Grafting Log"
                else -> "Entry"
            },
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(
                title = formTitle,
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
                .padding(horizontal = AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            Spacer(modifier = Modifier.height(AppSpacing.xs))

            when (type) {
                "plant" -> PlantForm(
                    editId = editId,
                    isEditMode = isEditMode,
                    viewModel = viewModel,
                    onBack = onBack,
                )
                "harvest" -> HarvestForm(
                    plantId = plantId,
                    editId = editId,
                    isEditMode = isEditMode,
                    viewModel = viewModel,
                    onBack = onBack,
                )
                "pruning" -> PruningForm(
                    plantId = plantId,
                    editId = editId,
                    isEditMode = isEditMode,
                    viewModel = viewModel,
                    onBack = onBack,
                )
                "spray" -> SprayForm(
                    plantId = plantId,
                    editId = editId,
                    isEditMode = isEditMode,
                    viewModel = viewModel,
                    onBack = onBack,
                )
                "bloom" -> BloomForm(
                    plantId = plantId,
                    editId = editId,
                    isEditMode = isEditMode,
                    viewModel = viewModel,
                    onBack = onBack,
                )
                "grafting" -> GraftingForm(
                    plantId = plantId,
                    editId = editId,
                    isEditMode = isEditMode,
                    viewModel = viewModel,
                    onBack = onBack,
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.xl))
        }
    }
}

// ── Plant Form ──

@Composable
private fun PlantForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: OrchardViewModel,
    onBack: () -> Unit,
) {
    var species by remember { mutableStateOf("") }
    var variety by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Pome Fruit") }
    var rootstock by remember { mutableStateOf("") }
    var plantingYear by remember { mutableStateOf("") }
    var chillHoursRequired by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Dormant") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getPlantById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                species = it.species
                variety = it.variety ?: ""
                category = it.category
                rootstock = it.rootstock ?: ""
                plantingYear = it.plantingYear?.toString() ?: ""
                chillHoursRequired = it.chillHoursRequired?.toString() ?: ""
                status = it.status
                notes = it.notes ?: ""
            }
        }
    }

    val speciesDefaults = OrchardCatalogData.forSpecies(species)
    val rootstockOptions = speciesDefaults?.commonRootstocks ?: emptyList()
    val varietyOptions = OrchardCatalogData.varietiesForSpecies(species)

    SearchableSelector(
        query = species,
        onQueryChange = { species = it },
        items = OrchardCatalogData.SPECIES_NAMES.filter {
            it.contains(species, ignoreCase = true)
        },
        onItemSelected = {
            species = it
            variety = ""
            val defaults = OrchardCatalogData.forSpecies(it)
            if (defaults != null) category = defaults.category
        },
        label = "Species",
        nameSelector = { it },
        accentColor = OrchardGlow,
        onAddCustom = { species = it; variety = "" },
    )
    if (varietyOptions.isNotEmpty()) {
        SearchableSelector(
            query = variety,
            onQueryChange = { variety = it },
            items = varietyOptions.filter {
                it.contains(variety, ignoreCase = true)
            },
            onItemSelected = { variety = it },
            label = "Variety",
            nameSelector = { it },
            accentColor = OrchardGlow,
            onAddCustom = { variety = it },
        )
    } else {
        InputField(
            value = variety,
            onValueChange = { variety = it },
            label = { Text("Variety") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = OrchardGlow,
        )
    }
    SearchableSelector(
        query = category,
        onQueryChange = { category = it },
        items = ModuleCatalogData.ORCHARD_CATEGORIES.filter {
            it.contains(category, ignoreCase = true)
        },
        onItemSelected = { category = it },
        label = "Category",
        nameSelector = { it },
        accentColor = OrchardGlow,
        onAddCustom = { category = it },
    )
    SearchableSelector(
        query = rootstock,
        onQueryChange = { rootstock = it },
        items = rootstockOptions.filter {
            it.contains(rootstock, ignoreCase = true)
        },
        onItemSelected = { rootstock = it },
        label = "Rootstock",
        nameSelector = { it },
        accentColor = OrchardGlow,
        onAddCustom = { rootstock = it },
    )
    InputField(
        value = plantingYear,
        onValueChange = { plantingYear = it.filter { c -> c.isDigit() } },
        label = { Text("Planting Year") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        accentColor = OrchardGlow,
    )
    InputField(
        value = chillHoursRequired,
        onValueChange = { chillHoursRequired = it.filter { c -> c.isDigit() } },
        label = { Text("Chill Hours Required") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        accentColor = OrchardGlow,
    )
    SearchableSelector(
        query = status,
        onQueryChange = { status = it },
        items = listOf("Dormant", "Growing", "Producing", "Removed").filter {
            it.contains(status, ignoreCase = true)
        },
        onItemSelected = { status = it },
        label = "Status",
        nameSelector = { it },
        accentColor = OrchardGlow,
    )
    InputField(
        value = notes,
        onValueChange = { notes = it },
        label = { Text("Notes") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3,
        accentColor = OrchardGlow,
    )

    Spacer(modifier = Modifier.height(AppSpacing.xs))

    PrimaryButton(
        text = if (isEditMode) "Update Plant" else "Save Plant",
        onClick = {
            if (species.isNotBlank()) {
                val plant = OrchardPlant(
                    id = if (isEditMode) editId else 0,
                    species = species.trim(),
                    variety = variety.trim().ifBlank { null },
                    category = category,
                    rootstock = rootstock.trim().ifBlank { null },
                    plantingYear = plantingYear.toIntOrNull(),
                    chillHoursRequired = chillHoursRequired.toIntOrNull(),
                    status = status,
                    notes = notes.trim().ifBlank { null },
                )
                if (isEditMode) viewModel.updatePlant(plant) else viewModel.addPlant(plant)
                onBack()
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

// ── Harvest Form ──

@Composable
private fun HarvestForm(
    plantId: Long,
    editId: Long,
    isEditMode: Boolean,
    viewModel: OrchardViewModel,
    onBack: () -> Unit,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var yieldLbs by remember { mutableStateOf("") }
    var fruitQuality by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getHarvestById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                yieldLbs = it.yieldLbs?.toString() ?: ""
                fruitQuality = it.fruitQuality ?: ""
                destination = it.destination ?: ""
            }
        }
    }

    DateFieldWithToggle(
        label = "Date",
        dateMillis = date,
        onDateChange = { date = it },
        useTodayDefault = !isEditMode,
        accentColor = OrchardGlow,
    )
    InputField(
        value = yieldLbs,
        onValueChange = { yieldLbs = it.filter { c -> c.isDigit() || c == '.' } },
        label = { Text("Yield (lbs)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        accentColor = OrchardGlow,
    )
    SearchableSelector(
        query = fruitQuality,
        onQueryChange = { fruitQuality = it },
        items = ModuleCatalogData.FRUIT_QUALITIES.filter {
            it.contains(fruitQuality, ignoreCase = true)
        },
        onItemSelected = { fruitQuality = it },
        label = "Fruit Quality",
        nameSelector = { it },
        accentColor = OrchardGlow,
        onAddCustom = { fruitQuality = it },
    )
    InputField(
        value = destination,
        onValueChange = { destination = it },
        label = { Text("Destination") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        accentColor = OrchardGlow,
    )

    Spacer(modifier = Modifier.height(AppSpacing.xs))

    PrimaryButton(
        text = if (isEditMode) "Update Harvest" else "Save Harvest",
        onClick = {
            val harvest = OrchardHarvest(
                id = if (isEditMode) editId else 0,
                plantId = plantId,
                date = date,
                yieldLbs = yieldLbs.toDoubleOrNull(),
                fruitQuality = fruitQuality.trim().ifBlank { null },
                destination = destination.trim().ifBlank { null },
            )
            if (isEditMode) viewModel.updateHarvest(harvest) else viewModel.addHarvest(harvest)
            onBack()
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

// ── Pruning Form ──

@Composable
private fun PruningForm(
    plantId: Long,
    editId: Long,
    isEditMode: Boolean,
    viewModel: OrchardViewModel,
    onBack: () -> Unit,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var pruningType by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getPruningLogById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                pruningType = it.pruningType ?: ""
                method = it.method ?: ""
                notes = it.notes ?: ""
            }
        }
    }

    DateFieldWithToggle(
        label = "Date",
        dateMillis = date,
        onDateChange = { date = it },
        useTodayDefault = !isEditMode,
        accentColor = OrchardGlow,
    )
    SearchableSelector(
        query = pruningType,
        onQueryChange = { pruningType = it },
        items = ModuleCatalogData.PRUNING_TYPES.filter {
            it.contains(pruningType, ignoreCase = true)
        },
        onItemSelected = { pruningType = it },
        label = "Pruning Type",
        nameSelector = { it },
        accentColor = OrchardGlow,
        onAddCustom = { pruningType = it },
    )
    SearchableSelector(
        query = method,
        onQueryChange = { method = it },
        items = ModuleCatalogData.PRUNING_METHODS.filter {
            it.contains(method, ignoreCase = true)
        },
        onItemSelected = { method = it },
        label = "Method",
        nameSelector = { it },
        accentColor = OrchardGlow,
        onAddCustom = { method = it },
    )
    InputField(
        value = notes,
        onValueChange = { notes = it },
        label = { Text("Notes") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3,
        accentColor = OrchardGlow,
    )

    Spacer(modifier = Modifier.height(AppSpacing.xs))

    PrimaryButton(
        text = if (isEditMode) "Update Pruning Log" else "Save Pruning Log",
        onClick = {
            val log = PruningLog(
                id = if (isEditMode) editId else 0,
                plantId = plantId,
                date = date,
                pruningType = pruningType.trim().ifBlank { null },
                method = method.trim().ifBlank { null },
                notes = notes.trim().ifBlank { null },
            )
            if (isEditMode) viewModel.updatePruningLog(log) else viewModel.addPruningLog(log)
            onBack()
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

// ── Spray Form ──

@Composable
private fun SprayForm(
    plantId: Long,
    editId: Long,
    isEditMode: Boolean,
    viewModel: OrchardViewModel,
    onBack: () -> Unit,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var product by remember { mutableStateOf("") }
    var timing by remember { mutableStateOf("") }
    var activeIngredient by remember { mutableStateOf("") }
    var organicApproved by remember { mutableStateOf(false) }
    var weatherConditions by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getSprayLogById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                product = it.product ?: ""
                timing = it.timing ?: ""
                activeIngredient = it.activeIngredient ?: ""
                organicApproved = it.organicApproved
                weatherConditions = it.weatherConditions ?: ""
            }
        }
    }

    DateFieldWithToggle(
        label = "Date",
        dateMillis = date,
        onDateChange = { date = it },
        useTodayDefault = !isEditMode,
        accentColor = OrchardGlow,
    )
    InputField(
        value = product,
        onValueChange = { product = it },
        label = { Text("Product") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        accentColor = OrchardGlow,
    )
    SearchableSelector(
        query = timing,
        onQueryChange = { timing = it },
        items = ModuleCatalogData.SPRAY_TIMINGS.filter {
            it.contains(timing, ignoreCase = true)
        },
        onItemSelected = { timing = it },
        label = "Timing",
        nameSelector = { it },
        accentColor = OrchardGlow,
        onAddCustom = { timing = it },
    )
    InputField(
        value = activeIngredient,
        onValueChange = { activeIngredient = it },
        label = { Text("Active Ingredient") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        accentColor = OrchardGlow,
    )
    ToggleRow(
        label = "Organic Approved",
        checked = organicApproved,
        accentColor = OrchardGlow,
        onCheckedChange = { organicApproved = it },
    )
    InputField(
        value = weatherConditions,
        onValueChange = { weatherConditions = it },
        label = { Text("Weather Conditions") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        accentColor = OrchardGlow,
    )

    Spacer(modifier = Modifier.height(AppSpacing.xs))

    PrimaryButton(
        text = if (isEditMode) "Update Spray Log" else "Save Spray Log",
        onClick = {
            val log = SprayLog(
                id = if (isEditMode) editId else 0,
                plantId = plantId,
                date = date,
                product = product.trim().ifBlank { null },
                timing = timing.trim().ifBlank { null },
                activeIngredient = activeIngredient.trim().ifBlank { null },
                organicApproved = organicApproved,
                weatherConditions = weatherConditions.trim().ifBlank { null },
            )
            if (isEditMode) viewModel.updateSprayLog(log) else viewModel.addSprayLog(log)
            onBack()
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

// ── Bloom Form ──

@Composable
private fun BloomForm(
    plantId: Long,
    editId: Long,
    isEditMode: Boolean,
    viewModel: OrchardViewModel,
    onBack: () -> Unit,
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    var year by remember { mutableStateOf(currentYear.toString()) }
    var firstBloomDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var fullBloomDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var fruitSet by remember { mutableStateOf("") }
    var useFirstBloomToday by remember { mutableStateOf(true) }
    var useFullBloomToday by remember { mutableStateOf(true) }

    if (isEditMode) {
        val existing by viewModel.getBloomRecordById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                year = it.year.toString()
                it.firstBloomDate?.let { d -> firstBloomDate = d; useFirstBloomToday = false }
                it.fullBloomDate?.let { d -> fullBloomDate = d; useFullBloomToday = false }
                fruitSet = it.fruitSet ?: ""
            }
        }
    }

    InputField(
        value = year,
        onValueChange = { year = it.filter { c -> c.isDigit() } },
        label = { Text("Year") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        accentColor = OrchardGlow,
    )
    DateFieldWithToggle(
        label = "First Bloom Date",
        dateMillis = firstBloomDate,
        onDateChange = { firstBloomDate = it },
        useTodayDefault = if (isEditMode) useFirstBloomToday else true,
        accentColor = OrchardGlow,
    )
    DateFieldWithToggle(
        label = "Full Bloom Date",
        dateMillis = fullBloomDate,
        onDateChange = { fullBloomDate = it },
        useTodayDefault = if (isEditMode) useFullBloomToday else true,
        accentColor = OrchardGlow,
    )
    InputField(
        value = fruitSet,
        onValueChange = { fruitSet = it },
        label = { Text("Fruit Set") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        accentColor = OrchardGlow,
    )

    Spacer(modifier = Modifier.height(AppSpacing.xs))

    PrimaryButton(
        text = if (isEditMode) "Update Bloom Record" else "Save Bloom Record",
        onClick = {
            val parsedYear = year.toIntOrNull() ?: currentYear
            val record = BloomRecord(
                id = if (isEditMode) editId else 0,
                plantId = plantId,
                year = parsedYear,
                firstBloomDate = firstBloomDate,
                fullBloomDate = fullBloomDate,
                fruitSet = fruitSet.trim().ifBlank { null },
            )
            if (isEditMode) viewModel.updateBloomRecord(record) else viewModel.addBloomRecord(record)
            onBack()
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

// ── Grafting Form ──

@Composable
private fun GraftingForm(
    plantId: Long,
    editId: Long,
    isEditMode: Boolean,
    viewModel: OrchardViewModel,
    onBack: () -> Unit,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var scionSource by remember { mutableStateOf("") }
    var graftType by remember { mutableStateOf("") }
    var success by remember { mutableStateOf("Pending") }

    if (isEditMode) {
        val existing by viewModel.getGraftingLogById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                scionSource = it.scionSource ?: ""
                graftType = it.graftType ?: ""
                success = it.success ?: "Pending"
            }
        }
    }

    DateFieldWithToggle(
        label = "Date",
        dateMillis = date,
        onDateChange = { date = it },
        useTodayDefault = !isEditMode,
        accentColor = OrchardGlow,
    )
    InputField(
        value = scionSource,
        onValueChange = { scionSource = it },
        label = { Text("Scion Source") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        accentColor = OrchardGlow,
    )
    SearchableSelector(
        query = graftType,
        onQueryChange = { graftType = it },
        items = ModuleCatalogData.GRAFT_TYPES.filter {
            it.contains(graftType, ignoreCase = true)
        },
        onItemSelected = { graftType = it },
        label = "Graft Type",
        nameSelector = { it },
        accentColor = OrchardGlow,
        onAddCustom = { graftType = it },
    )
    SearchableSelector(
        query = success,
        onQueryChange = { success = it },
        items = listOf("Pending", "Successful", "Failed").filter {
            it.contains(success, ignoreCase = true)
        },
        onItemSelected = { success = it },
        label = "Success",
        nameSelector = { it },
        accentColor = OrchardGlow,
    )

    Spacer(modifier = Modifier.height(AppSpacing.xs))

    PrimaryButton(
        text = if (isEditMode) "Update Grafting Log" else "Save Grafting Log",
        onClick = {
            val log = GraftingLog(
                id = if (isEditMode) editId else 0,
                plantId = plantId,
                date = date,
                scionSource = scionSource.trim().ifBlank { null },
                graftType = graftType.trim().ifBlank { null },
                success = success,
            )
            if (isEditMode) viewModel.updateGraftingLog(log) else viewModel.addGraftingLog(log)
            onBack()
        },
        modifier = Modifier.fillMaxWidth(),
    )
}
