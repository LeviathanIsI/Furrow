package com.furrow.app.ui.bees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.BeeRaceInfo
import com.furrow.app.data.local.entity.Hive
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.FurrowBottomSheet
import com.furrow.app.ui.components.SearchableSelector
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BeeGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.util.DateUtil
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

// ── Main Screen ──

@Composable
fun HiveListScreen(
    onHiveClick: (Long) -> Unit,
    onReportsClick: () -> Unit,
    viewModel: BeeViewModel = hiltViewModel(),
) {
    val hives by viewModel.activeHives.collectAsState()
    val lastDates by viewModel.lastInspectionDates.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    com.furrow.app.ui.components.AppScaffold(
        topBar = {
            com.furrow.app.ui.components.AppTopBar(
                title = "Hives",
                subtitle = "Inspection cadence and health",
                actions = {
                    IconButton(onClick = onReportsClick) {
                        Icon(Icons.Outlined.Assessment, "Reports", tint = TextSecondary)
                    }
                },
            )
        },
    ) { padding ->
        if (hives.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                EmptyState(
                    icon = {
                        Icon(
                            Icons.Filled.BugReport,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = BeeGlow,
                        )
                    },
                    title = "No hives tracked",
                    subtitle = "Add your first hive to start inspection logs.",
                    actionLabel = "Add Hive",
                    glowColor = BeeGlow,
                    onAction = { showAddDialog = true },
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = AppSpacing.bottomListPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "add_action") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        com.furrow.app.ui.components.PrimaryButton(
                            text = "Add Hive",
                            onClick = { showAddDialog = true },
                        )
                    }
                }

                item(key = "hive_panel") {
                    com.furrow.app.ui.components.Panel(contentPadding = PaddingValues(0.dp)) {
                        hives.forEachIndexed { index, hive ->
                            HiveCard(
                                hive = hive,
                                lastInspectionDate = lastDates[hive.id],
                                onClick = { onHiveClick(hive.id) },
                                showDivider = index != hives.lastIndex,
                                zone = viewModel.zone,
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Sheets & Dialogs ──

    if (showAddDialog) {
        val allRaces by viewModel.allRaces.collectAsState()
        val userProfile by viewModel.userProfile.collectAsState()
        AddHiveSheet(
            onDismiss = { showAddDialog = false },
            onSave = { hive ->
                viewModel.addHive(hive)
                showAddDialog = false
            },
            allRaces = allRaces,
            zoneGroup = userProfile?.zoneGroup,
            onInsertRace = { race -> viewModel.insertRace(race) },
            onUpdateRace = { race -> viewModel.updateRace(race) },
            onDeleteRace = { race -> viewModel.deleteRace(race) },
        )
    }
}

// ── Hive Card ──

@Composable
private fun HiveCard(
    hive: Hive,
    lastInspectionDate: Long?,
    onClick: () -> Unit,
    showDivider: Boolean,
    zone: ZoneId = ZoneId.systemDefault(),
) {
    val today = LocalDate.now(zone)
    val daysAgo = lastInspectionDate?.let {
        ChronoUnit.DAYS.between(
            Instant.ofEpochMilli(it).atZone(zone).toLocalDate(),
            today,
        )
    }
    val daysText = if (daysAgo != null) "$daysAgo d" else "—"
    val queenLabel = when (hive.queenStatus) {
        "present" -> "Present"
        "absent" -> "Absent"
        else -> "Unknown"
    }

    com.furrow.app.ui.components.ListRow(
        title = hive.name,
        subtitle = "${hive.beeRace ?: "Unknown"} • Queen: $queenLabel • Installed ${DateUtil.formatDate(hive.installDate)}",
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.BugReport,
                contentDescription = null,
                tint = TextSecondary,
            )
        },
        trailingText = daysText,
        trailing = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextTertiary,
            )
        },
        onClick = onClick,
        showDivider = showDivider,
    )
}

// ── Form Sheets ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHiveSheet(
    existingHive: Hive? = null,
    onDismiss: () -> Unit,
    onSave: (Hive) -> Unit,
    allRaces: List<BeeRaceInfo>,
    zoneGroup: String?,
    onInsertRace: (BeeRaceInfo) -> Unit,
    onUpdateRace: (BeeRaceInfo) -> Unit,
    onDeleteRace: (BeeRaceInfo) -> Unit,
) {
    val isEditMode = existingHive != null
    var name by remember { mutableStateOf(existingHive?.name ?: "") }
    var installDate by remember { mutableLongStateOf(existingHive?.installDate ?: System.currentTimeMillis()) }
    var queenStatus by remember { mutableStateOf(existingHive?.queenStatus ?: "Unknown") }
    var source by remember { mutableStateOf(existingHive?.source ?: "Package") }
    var notes by remember { mutableStateOf(existingHive?.notes ?: "") }
    var selectedRace by remember { mutableStateOf(existingHive?.beeRace) }
    var raceQuery by remember { mutableStateOf(existingHive?.beeRace ?: "") }
    var showAddCustomRace by remember { mutableStateOf(false) }
    var customRaceInitialName by remember { mutableStateOf("") }
    var raceToEdit by remember { mutableStateOf<BeeRaceInfo?>(null) }
    var raceToDelete by remember { mutableStateOf<BeeRaceInfo?>(null) }

    val filteredRaces = remember(allRaces, raceQuery, zoneGroup) {
        val query = raceQuery.lowercase()
        val filtered = allRaces.filter { query.isEmpty() || it.name.lowercase().contains(query) }
        if (zoneGroup == null) filtered
        else filtered.sortedWith(
            compareBy<BeeRaceInfo> {
                when (climateBadgeFor(it, zoneGroup)) {
                    ClimateBadge.GREAT -> 0
                    ClimateBadge.MANAGEABLE -> 1
                    ClimateBadge.NOT_IDEAL -> 2
                }
            }.thenBy { it.name },
        )
    }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = BeeGlow, bordered = true)

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = if (isEditMode) "Edit Hive" else "Add Hive",
        confirmText = "Save",
        confirmEnabled = name.isNotBlank(),
        glowColor = BeeGlow,
        onConfirm = {
            if (name.isNotBlank()) {
                onSave(
                    Hive(
                        id = existingHive?.id ?: 0,
                        name = name.trim(),
                        installDate = installDate,
                        queenStatus = queenStatus,
                        source = source,
                        notes = notes.ifBlank { null },
                        beeRace = selectedRace,
                    ),
                )
            }
        },
        content = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(8.dp),
            )
            SearchableSelector(
                query = raceQuery,
                onQueryChange = {
                    raceQuery = it
                    selectedRace = it.ifBlank { null }
                },
                items = filteredRaces,
                onItemSelected = { race ->
                    selectedRace = race.name
                    raceQuery = race.name
                },
                label = "Bee Race (optional)",
                accentColor = BeeGlow,
                nameSelector = { it.name },
                isCustom = { it.isCustom },
                onAddCustom = { customName ->
                    customRaceInitialName = customName
                    showAddCustomRace = true
                },
                onEditCustom = { race -> raceToEdit = race },
                onDeleteCustom = { race -> raceToDelete = race },
                itemContent = { race ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(race.name, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                race.temperament,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary,
                            )
                        }
                        if (zoneGroup != null) {
                            ClimateBadgePill(climateBadgeFor(race, zoneGroup))
                        }
                    }
                },
            )
            DateFieldWithToggle(
                label = "Install Date:",
                dateMillis = installDate,
                onDateChange = { installDate = it },
                useTodayDefault = !isEditMode,
            )
            DropdownSelector(
                label = "Queen Status",
                options = listOf("Present", "Absent", "Unknown"),
                selected = queenStatus,
                onSelect = { queenStatus = it },
            )
            DropdownSelector(
                label = "Source",
                options = listOf("Package", "Nuc", "Swarm", "Split"),
                selected = source,
                onSelect = { source = it },
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = fieldColors,
                shape = RoundedCornerShape(8.dp),
            )
        },
    )

    if (showAddCustomRace || raceToEdit != null) {
        AddCustomRaceSheet(
            existingRace = raceToEdit,
            initialName = if (raceToEdit != null) raceToEdit!!.name else customRaceInitialName,
            onDismiss = {
                showAddCustomRace = false
                raceToEdit = null
            },
            onSave = { race ->
                if (raceToEdit != null) onUpdateRace(race) else onInsertRace(race)
                selectedRace = race.name
                raceQuery = race.name
                showAddCustomRace = false
                raceToEdit = null
            },
        )
    }

    raceToDelete?.let { race ->
        DeleteConfirmationDialog(
            itemName = race.name,
            onConfirm = {
                onDeleteRace(race)
                if (selectedRace == race.name) {
                    selectedRace = null
                    raceQuery = ""
                }
                raceToDelete = null
            },
            onDismiss = { raceToDelete = null },
        )
    }
}

@Composable
private fun AddCustomRaceSheet(
    existingRace: BeeRaceInfo?,
    initialName: String,
    onDismiss: () -> Unit,
    onSave: (BeeRaceInfo) -> Unit,
) {
    var raceName by remember { mutableStateOf(existingRace?.name ?: initialName) }
    var temperament by remember { mutableStateOf(existingRace?.temperament ?: "gentle") }
    var honeyProduction by remember { mutableStateOf(existingRace?.honeyProduction?.toString() ?: "3") }
    var miteResistance by remember { mutableStateOf(existingRace?.miteResistance?.toString() ?: "3") }
    var swarmingTendency by remember { mutableStateOf(existingRace?.swarmingTendency?.toString() ?: "3") }
    var overwintering by remember { mutableStateOf(existingRace?.overwinteringAbility?.toString() ?: "3") }
    var climateSuitability by remember { mutableStateOf(existingRace?.climateSuitability ?: "Moderate") }
    var raceNotes by remember { mutableStateOf(existingRace?.notes ?: "") }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = BeeGlow, bordered = true)

    FurrowBottomSheet(
        onDismiss = onDismiss,
        title = if (existingRace != null) "Edit Custom Race" else "Add Custom Race",
        confirmText = "Save",
        confirmEnabled = raceName.isNotBlank(),
        glowColor = BeeGlow,
        onConfirm = {
            if (raceName.isNotBlank()) {
                onSave(
                    BeeRaceInfo(
                        id = existingRace?.id ?: 0,
                        name = raceName.trim(),
                        temperament = temperament,
                        honeyProduction = honeyProduction.toIntOrNull() ?: 3,
                        miteResistance = miteResistance.toIntOrNull() ?: 3,
                        swarmingTendency = swarmingTendency.toIntOrNull() ?: 3,
                        overwinteringAbility = overwintering.toIntOrNull() ?: 3,
                        climateSuitability = climateSuitability,
                        notes = raceNotes.ifBlank { null },
                        isCustom = true,
                    ),
                )
            }
        },
        content = {
            OutlinedTextField(
                value = raceName,
                onValueChange = { raceName = it },
                label = { Text("Race Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(8.dp),
            )
            OutlinedTextField(
                value = temperament,
                onValueChange = { temperament = it },
                label = { Text("Temperament") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(8.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = honeyProduction,
                    onValueChange = { honeyProduction = it.filter { c -> c.isDigit() } },
                    label = { Text("Honey (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(8.dp),
                )
                OutlinedTextField(
                    value = miteResistance,
                    onValueChange = { miteResistance = it.filter { c -> c.isDigit() } },
                    label = { Text("Mites (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(8.dp),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = swarmingTendency,
                    onValueChange = { swarmingTendency = it.filter { c -> c.isDigit() } },
                    label = { Text("Swarm (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(8.dp),
                )
                OutlinedTextField(
                    value = overwintering,
                    onValueChange = { overwintering = it.filter { c -> c.isDigit() } },
                    label = { Text("Winter (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(8.dp),
                )
            }
            DropdownSelector(
                label = "Climate Suitability",
                options = listOf("Cold", "Moderate", "Warm", "Hot", "All Climates"),
                selected = climateSuitability,
                onSelect = { climateSuitability = it },
            )
            OutlinedTextField(
                value = raceNotes,
                onValueChange = { raceNotes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = fieldColors,
                shape = RoundedCornerShape(8.dp),
            )
        },
    )
}


