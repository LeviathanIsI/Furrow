package com.furrow.app.ui.garden

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.FertilizerLog
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.data.local.entity.PestDiseaseLog
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.local.entity.WateringLog
import com.furrow.app.ui.bees.DropdownSelector
import com.furrow.app.ui.components.AppChip
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTextField
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.ExtraAction
import com.furrow.app.ui.components.FurrowBottomSheet
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.garden.tabs.CareLogTab
import com.furrow.app.ui.garden.tabs.HarvestTotal
import com.furrow.app.ui.garden.tabs.HarvestsTab
import com.furrow.app.ui.garden.tabs.PlantingsTab
import com.furrow.app.ui.theme.AppRadius
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BedDetailScreen(
    onBack: () -> Unit,
    onAddPlanting: (Long) -> Unit,
    onAddHarvest: (Long) -> Unit,
    onEditPlanting: (Long, Long) -> Unit,
    onEditHarvest: (Long, Long) -> Unit,
    viewModel: BedDetailViewModel = hiltViewModel(),
) {
    val bed by viewModel.selectedBed.collectAsState()
    val plantings by viewModel.plantings.collectAsState()
    val harvests by viewModel.harvests.collectAsState()
    val plantInfoMap by viewModel.plantInfoMap.collectAsState()
    val zoneWindows by viewModel.zoneWindows.collectAsState()
    val harvestPredictions by viewModel.harvestPredictions.collectAsState()
    val wateringLogs by viewModel.wateringLogs.collectAsState()
    val fertilizerLogs by viewModel.fertilizerLogs.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Plantings", "Harvests", "Care log")

    var plantingToDelete by remember { mutableStateOf<Planting?>(null) }
    var harvestToDelete by remember { mutableStateOf<HarvestLog?>(null) }
    var selectedPlantDetail by remember { mutableStateOf<PlantInfo?>(null) }
    var plantingForAction by remember { mutableStateOf<Planting?>(null) }
    var harvestForAction by remember { mutableStateOf<HarvestLog?>(null) }
    var wateringToDelete by remember { mutableStateOf<WateringLog?>(null) }
    var fertilizerToDelete by remember { mutableStateOf<FertilizerLog?>(null) }
    var showWateringForm by remember { mutableStateOf(false) }
    var showFertilizerForm by remember { mutableStateOf(false) }
    var showCareLogPicker by remember { mutableStateOf(false) }
    var plantingToMarkSprouted by remember { mutableStateOf<Planting?>(null) }
    var plantingForPestReport by remember { mutableStateOf<Planting?>(null) }

    val plantingNames = remember(plantings) {
        plantings.associate {
            it.id to if (it.variety != null) "${it.plantName} \u2014 ${it.variety}" else it.plantName
        }
    }

    val harvestTotals = remember(harvests) {
        harvests.groupBy { it.plantingId }.mapValues { (_, logs) ->
            val totalOz = logs.sumOf { it.amountOz ?: 0.0 }
            val totalCount = logs.sumOf { it.count ?: 0 }
            HarvestTotal(totalOz, totalCount, logs.size)
        }
    }

    AppScaffold(
        topBar = {
            com.furrow.app.ui.components.AppTopBar(
                title = bed?.name ?: "Bed detail",
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
                .padding(padding),
        ) {
            // ── Header Card ──
            bed?.let { b ->
                Panel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.md),
                ) {
                    Text(
                        b.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.xxs))
                    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                        InfoPill(b.type.replaceFirstChar { it.uppercase() })
                        b.sunExposure?.let {
                            InfoPill(it.replaceFirstChar { c -> c.uppercase() })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.sm))
            }

            // ── Tab Row ──
            TabRow(
                modifier = Modifier.padding(horizontal = AppSpacing.md),
                selectedTabIndex = selectedTab,
                containerColor = Charcoal,
                contentColor = TextPrimary,
                divider = { HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle) },
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GardenGlow,
                        )
                    }
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    letterSpacing = 1.2.sp,
                                ),
                                color = if (selectedTab == index) TextPrimary else TextTertiary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
            }

            bed?.let { b ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                    horizontalArrangement = Arrangement.End,
                ) {
                    com.furrow.app.ui.components.PrimaryButton(
                        text = when (selectedTab) {
                            0 -> "Add planting"
                            1 -> "Log harvest"
                            else -> "Log care"
                        },
                        onClick = {
                            when (selectedTab) {
                                0 -> onAddPlanting(b.id)
                                1 -> onAddHarvest(b.id)
                                else -> showCareLogPicker = true
                            }
                        },
                    )
                }
            }

            // ── Tab Content ──
            when (selectedTab) {
                0 -> PlantingsTab(
                    plantings, plantInfoMap, zoneWindows, harvestPredictions,
                    onLongPress = { plantingForAction = it },
                    onPlantInfoClick = { selectedPlantDetail = it },
                    onMarkSprouted = { plantingToMarkSprouted = it },
                )
                1 -> HarvestsTab(
                    harvests, plantingNames, harvestTotals,
                    onLongPress = { harvestForAction = it },
                )
                2 -> CareLogTab(
                    wateringLogs = wateringLogs,
                    fertilizerLogs = fertilizerLogs,
                    plantings = plantings,
                    plantInfoMap = plantInfoMap,
                    onWateringLongPress = { wateringToDelete = it },
                    onFertilizerLongPress = { fertilizerToDelete = it },
                )
            }
        }
    }

    // ── Dialogs & Sheets ──

    plantingToDelete?.let { planting ->
        DeleteConfirmationDialog(
            itemName = planting.plantName,
            onConfirm = { viewModel.deletePlanting(planting); plantingToDelete = null },
            onDismiss = { plantingToDelete = null },
        )
    }

    harvestToDelete?.let { harvest ->
        val name = plantingNames[harvest.plantingId] ?: "harvest"
        DeleteConfirmationDialog(
            itemName = "$name harvest",
            onConfirm = { viewModel.deleteHarvest(harvest); harvestToDelete = null },
            onDismiss = { harvestToDelete = null },
        )
    }

    val allVarieties by viewModel.allVarieties.collectAsState()
    val varietiesByPlantId by viewModel.varietiesByPlantId.collectAsState()
    selectedPlantDetail?.let { plant ->
        val activeWindows by viewModel.activeWindows.collectAsState()
        val plantWindows = activeWindows.filter { it.plantName == plant.name }
        val plantVarieties = varietiesByPlantId[plant.id] ?: emptyList()
        PlantDetailSheet(
            plant = plant,
            varieties = plantVarieties,
            plantingWindows = plantWindows,
            onDismiss = { selectedPlantDetail = null },
        )
    }

    plantingForAction?.let { planting ->
        ItemActionSheet(
            onDismiss = { plantingForAction = null },
            onEdit = { bed?.let { b -> onEditPlanting(b.id, planting.id) } },
            onDelete = { plantingToDelete = planting },
            extraActions = listOf(
                ExtraAction(
                    label = "Report Pest/Disease",
                    icon = {
                        Icon(
                            Icons.Outlined.BugReport,
                            contentDescription = null,
                            tint = StatusBad,
                        )
                    },
                    onClick = { plantingForPestReport = planting },
                ),
            ),
        )
    }

    harvestForAction?.let { harvest ->
        ItemActionSheet(
            onDismiss = { harvestForAction = null },
            onEdit = { bed?.let { b -> onEditHarvest(b.id, harvest.id) } },
            onDelete = { harvestToDelete = harvest },
        )
    }

    wateringToDelete?.let { log ->
        DeleteConfirmationDialog(
            itemName = "watering log",
            onConfirm = { viewModel.deleteWateringLog(log); wateringToDelete = null },
            onDismiss = { wateringToDelete = null },
        )
    }

    fertilizerToDelete?.let { log ->
        DeleteConfirmationDialog(
            itemName = "fertilizer log",
            onConfirm = { viewModel.deleteFertilizerLog(log); fertilizerToDelete = null },
            onDismiss = { fertilizerToDelete = null },
        )
    }

    if (showCareLogPicker) {
        CareLogPickerSheet(
            onDismiss = { showCareLogPicker = false },
            onWater = { showCareLogPicker = false; showWateringForm = true },
            onFertilize = { showCareLogPicker = false; showFertilizerForm = true },
        )
    }

    if (showWateringForm) {
        WateringFormSheet(
            onDismiss = { showWateringForm = false },
            onSave = { date, amount, method, notes ->
                bed?.let { b ->
                    viewModel.addWateringLog(
                        WateringLog(
                            bedId = b.id,
                            date = date,
                            amountGallons = amount,
                            method = method,
                            notes = notes,
                        )
                    )
                }
                showWateringForm = false
            },
        )
    }

    if (showFertilizerForm) {
        FertilizerFormSheet(
            onDismiss = { showFertilizerForm = false },
            onSave = { date, product, amount, notes ->
                bed?.let { b ->
                    viewModel.addFertilizerLog(
                        FertilizerLog(
                            bedId = b.id,
                            date = date,
                            productName = product,
                            amount = amount,
                            notes = notes,
                        )
                    )
                }
                showFertilizerForm = false
            },
        )
    }

    plantingToMarkSprouted?.let { planting ->
        SproutedFormSheet(
            planting = planting,
            onDismiss = { plantingToMarkSprouted = null },
            onSave = { seedsSprouted ->
                viewModel.updatePlanting(
                    planting.copy(
                        germinationDate = System.currentTimeMillis(),
                        seedsSprouted = seedsSprouted,
                    )
                )
                plantingToMarkSprouted = null
            },
        )
    }

    plantingForPestReport?.let { planting ->
        val plantInfo = plantInfoMap[planting.plantName]
        PestDiseaseFormSheet(
            plantName = planting.plantName,
            commonPests = plantInfo?.commonPests,
            commonDiseases = plantInfo?.commonDiseases,
            onDismiss = { plantingForPestReport = null },
            onSave = { type, name, severity, treatment, notes ->
                bed?.let { b ->
                    viewModel.addPestLog(
                        PestDiseaseLog(
                            plantingId = planting.id,
                            bedId = b.id,
                            date = System.currentTimeMillis(),
                            type = type,
                            name = name,
                            severity = severity,
                            treatment = treatment,
                            notes = notes,
                        )
                    )
                }
                plantingForPestReport = null
            },
        )
    }
}

// ── Private Composables ──

@Composable
private fun InfoPill(text: String) {
    AppChip(
        text = text,
        selected = false,
        accentColor = GardenGlow,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CareLogPickerSheet(
    onDismiss: () -> Unit,
    onWater: () -> Unit,
    onFertilize: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Charcoal,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppSpacing.lg),
        ) {
            Text(
                "Log Care Activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onWater() }
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = GardenGlow,
                )
                Text(
                    "Log Watering",
                    fontSize = 16.sp,
                    color = TextPrimary,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFertilize() }
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = StatusWarn,
                )
                Text(
                    "Log Fertilizer",
                    fontSize = 16.sp,
                    color = TextPrimary,
                )
            }
        }
    }
}

@Composable
private fun WateringFormSheet(
    onDismiss: () -> Unit,
    onSave: (date: Long, amount: Float?, method: String?, notes: String?) -> Unit,
) {
    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    var amount by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = GardenGlow)

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = "Log Watering",
        confirmText = "Save",
        glowColor = GardenGlow,
        onConfirm = {
            onSave(
                date,
                amount.toFloatOrNull(),
                method.ifBlank { null },
                notes.ifBlank { null },
            )
        },
        content = {
            DateFieldWithToggle(
                label = "Date",
                dateMillis = date,
                onDateChange = { date = it },
                useTodayDefault = true,
                accentColor = GardenGlow,
            )
            AppTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Amount (gallons, optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
            )
            DropdownSelector(
                label = "Method",
                options = listOf("", "Hose", "Drip", "Sprinkler", "Watering Can", "Rain"),
                selected = method,
                onSelect = { method = it },
                accentColor = GardenGlow,
            )
            AppTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = fieldColors,
            )
        },
    )
}

@Composable
private fun SproutedFormSheet(
    planting: Planting,
    onDismiss: () -> Unit,
    onSave: (seedsSprouted: Int?) -> Unit,
) {
    var seedsSprouted by remember { mutableStateOf(planting.seedsPlanted?.toString() ?: "") }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = GardenGlow)

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = "Mark as Sprouted",
        confirmText = "Save",
        glowColor = GardenGlow,
        onConfirm = { onSave(seedsSprouted.toIntOrNull()) },
        content = {
            Text(
                "${planting.plantName}${planting.variety?.let { " \u2014 $it" } ?: ""}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = GardenGlow,
            )
            if (planting.seedsPlanted != null) {
                AppTextField(
                    value = seedsSprouted,
                    onValueChange = { seedsSprouted = it.filter { c -> c.isDigit() } },
                    label = { Text("Seeds Sprouted (of ${planting.seedsPlanted})") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors,
                )
            }
        },
    )
}

@Composable
private fun FertilizerFormSheet(
    onDismiss: () -> Unit,
    onSave: (date: Long, product: String?, amount: String?, notes: String?) -> Unit,
) {
    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    var product by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = GardenGlow)

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = "Log Fertilizer",
        confirmText = "Save",
        glowColor = GardenGlow,
        onConfirm = {
            onSave(
                date,
                product.ifBlank { null },
                amount.ifBlank { null },
                notes.ifBlank { null },
            )
        },
        content = {
            DateFieldWithToggle(
                label = "Date",
                dateMillis = date,
                onDateChange = { date = it },
                useTodayDefault = true,
                accentColor = GardenGlow,
            )
            AppTextField(
                value = product,
                onValueChange = { product = it },
                label = { Text("Product Name (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
            )
            AppTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
            )
            AppTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = fieldColors,
            )
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PestDiseaseFormSheet(
    plantName: String,
    commonPests: String?,
    commonDiseases: String?,
    onDismiss: () -> Unit,
    onSave: (type: String, name: String, severity: String, treatment: String?, notes: String?) -> Unit,
) {
    var type by remember { mutableStateOf("Pest") }
    var name by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("Moderate") }
    var treatment by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val suggestions = remember(type, commonPests, commonDiseases) {
        val source = if (type == "Pest") commonPests else commonDiseases
        source?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
    }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = GardenGlow)

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = "Report Pest/Disease",
        confirmText = "Save",
        confirmEnabled = name.isNotBlank(),
        glowColor = GardenGlow,
        onConfirm = {
            if (name.isNotBlank()) {
                onSave(type, name.trim(), severity, treatment.ifBlank { null }, notes.ifBlank { null })
            }
        },
        content = {
            Text(
                plantName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = GardenGlow,
            )
            DropdownSelector(
                label = "Type",
                options = listOf("Pest", "Disease"),
                selected = type,
                onSelect = { type = it; name = "" },
                accentColor = GardenGlow,
            )
            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (type == "Pest") "Pest Name" else "Disease Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
            )
            if (suggestions.isNotEmpty()) {
                Text(
                    "Common for $plantName:",
                    fontSize = 12.sp,
                    color = TextTertiary,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs),
                ) {
                    suggestions.forEach { suggestion ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StatusBad.copy(alpha = 0.12f),
                            modifier = Modifier.clickable { name = suggestion },
                        ) {
                            Text(
                                suggestion,
                                fontSize = 10.sp,
                                color = StatusBad,
                                modifier = Modifier.padding(horizontal = AppSpacing.xs, vertical = AppSpacing.xxs),
                            )
                        }
                    }
                }
            }
            DropdownSelector(
                label = "Severity",
                options = listOf("Low", "Moderate", "Severe"),
                selected = severity,
                onSelect = { severity = it },
                accentColor = GardenGlow,
            )
            AppTextField(
                value = treatment,
                onValueChange = { treatment = it },
                label = { Text("Treatment (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
            )
            AppTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = fieldColors,
            )
        },
    )
}
