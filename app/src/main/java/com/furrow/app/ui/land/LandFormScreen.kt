package com.furrow.app.ui.land

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.CompostBin
import com.furrow.app.data.local.entity.Fence
import com.furrow.app.data.local.entity.Paddock
import com.furrow.app.data.local.entity.Property
import com.furrow.app.data.local.entity.SoilTest
import com.furrow.app.data.local.entity.Structure
import com.furrow.app.data.local.entity.WaterSource
import com.furrow.app.data.local.entity.WeatherLog
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.bees.DropdownSelector
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.ToggleRow
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.LandGlow
import com.furrow.app.ui.theme.TextPrimary

@Composable
fun LandFormScreen(
    type: String,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: LandViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L

    val title = when (type) {
        "property" -> if (isEditMode) "Edit Property" else "Add Property"
        "structure" -> if (isEditMode) "Edit Structure" else "Add Structure"
        "fence" -> if (isEditMode) "Edit Fence" else "Add Fence"
        "paddock" -> if (isEditMode) "Edit Paddock" else "Add Paddock"
        "water_source" -> if (isEditMode) "Edit Water Source" else "Add Water Source"
        "compost_bin" -> if (isEditMode) "Edit Compost Bin" else "Add Compost Bin"
        "soil_test" -> if (isEditMode) "Edit Soil Test" else "Add Soil Test"
        "weather" -> if (isEditMode) "Edit Weather Log" else "Add Weather Log"
        else -> "Form"
    }

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
            "property" -> PropertyForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "structure" -> StructureForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "fence" -> FenceForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "paddock" -> PaddockForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "water_source" -> WaterSourceForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "compost_bin" -> CompostBinForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "soil_test" -> SoilTestForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "weather" -> WeatherForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

// ── Property Form ────────────────────────────────────────────────────────────

@Composable
private fun PropertyForm(
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
                onBack()
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

// ── Structure Form ───────────────────────────────────────────────────────────

@Composable
private fun StructureForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: LandViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var dimensions by remember { mutableStateOf("") }
    var sqFt by remember { mutableStateOf("") }
    var buildDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var useBuildDate by remember { mutableStateOf(false) }
    var cost by remember { mutableStateOf("") }
    var conditionRating by remember { mutableStateOf("") }
    var maintenanceSchedule by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getStructureById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                name = it.name
                type = it.type
                dimensions = it.dimensions ?: ""
                sqFt = it.sqFt?.toString() ?: ""
                if (it.buildDate != null) {
                    buildDate = it.buildDate
                    useBuildDate = true
                }
                cost = it.cost?.toString() ?: ""
                conditionRating = it.conditionRating?.toString() ?: ""
                maintenanceSchedule = it.maintenanceSchedule ?: ""
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

        DropdownSelector(
            label = "Type *",
            options = ModuleCatalogData.STRUCTURE_TYPES,
            selected = type,
            onSelect = { type = it },
            accentColor = LandGlow,
        )

        InputField(
            value = dimensions,
            onValueChange = { dimensions = it },
            label = { Text("Dimensions") },
            singleLine = true,
            accentColor = LandGlow,
        )

        InputField(
            value = sqFt,
            onValueChange = { sqFt = it },
            label = { Text("Square Feet") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        DateFieldWithToggle(
            label = "Build Date",
            dateMillis = buildDate,
            onDateChange = { buildDate = it; useBuildDate = true },
            useTodayDefault = false,
            accentColor = LandGlow,
        )

        InputField(
            value = cost,
            onValueChange = { cost = it },
            label = { Text("Cost ($)") },
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

        InputField(
            value = maintenanceSchedule,
            onValueChange = { maintenanceSchedule = it },
            label = { Text("Maintenance Schedule") },
            singleLine = true,
            accentColor = LandGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Structure" else "Save Structure",
            onClick = {
                if (name.isBlank() || type.isBlank()) return@PrimaryButton
                val structure = Structure(
                    id = if (isEditMode) editId else 0,
                    name = name.trim(),
                    type = type.trim(),
                    dimensions = dimensions.ifBlank { null },
                    sqFt = sqFt.toDoubleOrNull(),
                    buildDate = if (useBuildDate) buildDate else null,
                    cost = cost.toDoubleOrNull(),
                    conditionRating = conditionRating.toIntOrNull()?.coerceIn(1, 5),
                    maintenanceSchedule = maintenanceSchedule.ifBlank { null },
                )
                if (isEditMode) viewModel.updateStructure(structure) else viewModel.addStructure(structure)
                onBack()
            },
            enabled = name.isNotBlank() && type.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

// ── Fence Form ───────────────────────────────────────────────────────────────

@Composable
private fun FenceForm(
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
                onBack()
            },
            enabled = type.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

// ── Paddock Form ─────────────────────────────────────────────────────────────

@Composable
private fun PaddockForm(
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
                onBack()
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

// ── Water Source Form ────────────────────────────────────────────────────────

@Composable
private fun WaterSourceForm(
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
                onBack()
            },
            enabled = type.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

// ── Compost Bin Form ─────────────────────────────────────────────────────────

@Composable
private fun CompostBinForm(
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
                onBack()
            },
            enabled = type.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

// ── Soil Test Form ───────────────────────────────────────────────────────────

@Composable
private fun SoilTestForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: LandViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var location by remember { mutableStateOf("") }
    var ph by remember { mutableStateOf("") }
    var nitrogenPpm by remember { mutableStateOf("") }
    var phosphorusPpm by remember { mutableStateOf("") }
    var potassiumPpm by remember { mutableStateOf("") }
    var organicMatterPct by remember { mutableStateOf("") }
    var texture by remember { mutableStateOf("") }
    var recommendations by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getSoilTestById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                location = it.location ?: ""
                ph = it.ph?.toString() ?: ""
                nitrogenPpm = it.nitrogenPpm?.toString() ?: ""
                phosphorusPpm = it.phosphorusPpm?.toString() ?: ""
                potassiumPpm = it.potassiumPpm?.toString() ?: ""
                organicMatterPct = it.organicMatterPct?.toString() ?: ""
                texture = it.texture ?: ""
                recommendations = it.recommendations ?: ""
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
            value = ph,
            onValueChange = { ph = it },
            label = { Text("pH") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = nitrogenPpm,
            onValueChange = { nitrogenPpm = it },
            label = { Text("Nitrogen (ppm)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = phosphorusPpm,
            onValueChange = { phosphorusPpm = it },
            label = { Text("Phosphorus (ppm)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = potassiumPpm,
            onValueChange = { potassiumPpm = it },
            label = { Text("Potassium (ppm)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = organicMatterPct,
            onValueChange = { organicMatterPct = it },
            label = { Text("Organic Matter (%)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        DropdownSelector(
            label = "Texture",
            options = ModuleCatalogData.SOIL_TEXTURES,
            selected = texture,
            onSelect = { texture = it },
            accentColor = LandGlow,
        )

        InputField(
            value = recommendations,
            onValueChange = { recommendations = it },
            label = { Text("Recommendations") },
            minLines = 3,
            accentColor = LandGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Soil Test" else "Save Soil Test",
            onClick = {
                val soilTest = SoilTest(
                    id = if (isEditMode) editId else 0,
                    date = date,
                    location = location.ifBlank { null },
                    ph = ph.toDoubleOrNull(),
                    nitrogenPpm = nitrogenPpm.toDoubleOrNull(),
                    phosphorusPpm = phosphorusPpm.toDoubleOrNull(),
                    potassiumPpm = potassiumPpm.toDoubleOrNull(),
                    organicMatterPct = organicMatterPct.toDoubleOrNull(),
                    texture = texture.ifBlank { null },
                    recommendations = recommendations.ifBlank { null },
                )
                if (isEditMode) viewModel.updateSoilTest(soilTest) else viewModel.addSoilTest(soilTest)
                onBack()
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}

// ── Weather Log Form ─────────────────────────────────────────────────────────

@Composable
private fun WeatherForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: LandViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var highTemp by remember { mutableStateOf("") }
    var lowTemp by remember { mutableStateOf("") }
    var rainfallIn by remember { mutableStateOf("") }
    var snowfallIn by remember { mutableStateOf("") }
    var humidityPct by remember { mutableStateOf("") }
    var windSpeed by remember { mutableStateOf("") }
    var frostFlag by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getWeatherLogById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                highTemp = it.highTemp?.toString() ?: ""
                lowTemp = it.lowTemp?.toString() ?: ""
                rainfallIn = it.rainfallIn?.toString() ?: ""
                snowfallIn = it.snowfallIn?.toString() ?: ""
                humidityPct = it.humidityPct?.toString() ?: ""
                windSpeed = it.windSpeed?.toString() ?: ""
                frostFlag = it.frostFlag
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
            accentColor = LandGlow,
        )

        InputField(
            value = highTemp,
            onValueChange = { highTemp = it },
            label = { Text("High Temp (\u00B0F)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        InputField(
            value = lowTemp,
            onValueChange = { lowTemp = it },
            label = { Text("Low Temp (\u00B0F)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        InputField(
            value = rainfallIn,
            onValueChange = { rainfallIn = it },
            label = { Text("Rainfall (in)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = snowfallIn,
            onValueChange = { snowfallIn = it },
            label = { Text("Snowfall (in)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = humidityPct,
            onValueChange = { humidityPct = it },
            label = { Text("Humidity (%)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        InputField(
            value = windSpeed,
            onValueChange = { windSpeed = it },
            label = { Text("Wind Speed (mph)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        ToggleRow(
            label = "Frost",
            checked = frostFlag,
            accentColor = LandGlow,
            onCheckedChange = { frostFlag = it },
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
            text = if (isEditMode) "Update Weather Log" else "Save Weather Log",
            onClick = {
                val weatherLog = WeatherLog(
                    id = if (isEditMode) editId else 0,
                    date = date,
                    highTemp = highTemp.toIntOrNull(),
                    lowTemp = lowTemp.toIntOrNull(),
                    rainfallIn = rainfallIn.toDoubleOrNull(),
                    snowfallIn = snowfallIn.toDoubleOrNull(),
                    humidityPct = humidityPct.toIntOrNull(),
                    windSpeed = windSpeed.toIntOrNull(),
                    frostFlag = frostFlag,
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateWeatherLog(weatherLog) else viewModel.addWeatherLog(weatherLog)
                onBack()
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
