package com.furrow.app.ui.bees

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.BeeRaceInfo
import com.furrow.app.data.local.entity.Hive
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.FurrowBottomSheet
import com.furrow.app.ui.components.GlowCard
import com.furrow.app.ui.components.SearchableSelector
import com.furrow.app.ui.theme.BeeGlow
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.DmSans
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// ── Climate badge logic (shared with HiveDetailScreen) ──

internal enum class ClimateBadge(val label: String) {
    GREAT("Great for your climate"),
    MANAGEABLE("Manageable"),
    NOT_IDEAL("Not ideal"),
}

internal fun climateBadgeColor(badge: ClimateBadge): Color = when (badge) {
    ClimateBadge.GREAT -> StatusGood
    ClimateBadge.MANAGEABLE -> StatusWarn
    ClimateBadge.NOT_IDEAL -> StatusBad
}

internal fun climateBadgeFor(race: BeeRaceInfo, zoneGroup: String): ClimateBadge {
    val heatFitness = when (race.name) {
        "Italian" -> 5
        "Saskatraz" -> 4
        "Buckfast" -> 4
        "Russian" -> 3
        "Caucasian" -> 2
        "Carniolan" -> 2
        "German Dark" -> 1
        else -> 3
    }
    val score = when (zoneGroup) {
        "hot", "warm" -> heatFitness
        "cold", "extreme_cold" -> race.overwinteringAbility
        else -> minOf(heatFitness, race.overwinteringAbility)
    }
    return when {
        score >= 4 -> ClimateBadge.GREAT
        score == 3 -> ClimateBadge.MANAGEABLE
        else -> ClimateBadge.NOT_IDEAL
    }
}

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

    Scaffold(
        containerColor = Void,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = BeeGlow,
                contentColor = Void,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Hive")
            }
        },
    ) { padding ->
        if (hives.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 8.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Hives",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = DmSans,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onReportsClick) {
                        Icon(Icons.Outlined.Assessment, "Reports", tint = TextTertiary)
                    }
                }
                EmptyState(
                    icon = {
                        Icon(
                            Icons.Filled.BugReport,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = BeeGlow,
                        )
                    },
                    title = "Your first hive awaits",
                    subtitle = "Tap + to add your first hive",
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
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Hives",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = DmSans,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onReportsClick) {
                            Icon(Icons.Outlined.Assessment, "Reports", tint = TextTertiary)
                        }
                    }
                }

                items(hives, key = { it.id }) { hive ->
                    HiveCard(
                        hive = hive,
                        lastInspectionDate = lastDates[hive.id],
                        onClick = { onHiveClick(hive.id) },
                    )
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
) {
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val daysAgo = lastInspectionDate?.let {
        ChronoUnit.DAYS.between(
            Instant.ofEpochMilli(it).atZone(zone).toLocalDate(),
            today,
        )
    }
    val daysColor = when {
        daysAgo == null -> TextTertiary
        daysAgo <= 7 -> StatusGood
        daysAgo <= 13 -> StatusWarn
        else -> StatusBad
    }
    val daysText = if (daysAgo != null) "$daysAgo" else "\u2014"
    val queenLabel = when (hive.queenStatus) {
        "present" -> "Present"
        "absent" -> "Absent"
        else -> "Unknown"
    }

    GlowCard(
        glowColor = BeeGlow,
        glowIntensity = 0.10f,
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        hive.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${hive.beeRace ?: "Unknown"} \u00b7 Queen: $queenLabel",
                        fontSize = 12.sp,
                        color = TextSecondary,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Installed ${formatDate(hive.installDate)}",
                        fontSize = 12.sp,
                        color = TextTertiary,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        daysText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = daysColor,
                    )
                    Text(
                        "days",
                        fontSize = 10.sp,
                        color = TextTertiary,
                    )
                }
            }
        }
    }
}

// ── Helper Composables ──

@Composable
private fun ClimateBadgePill(badge: ClimateBadge) {
    val color = climateBadgeColor(badge)
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
    ) {
        Text(
            badge.label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
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
    var queenStatus by remember { mutableStateOf(existingHive?.queenStatus ?: "unknown") }
    var source by remember { mutableStateOf(existingHive?.source ?: "package") }
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

    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Charcoal,
        focusedContainerColor = Charcoal,
        unfocusedBorderColor = BorderSubtle,
        focusedBorderColor = BeeGlow,
        unfocusedLabelColor = TextTertiary,
        focusedLabelColor = BeeGlow,
        cursorColor = BeeGlow,
        unfocusedTextColor = TextPrimary,
        focusedTextColor = TextPrimary,
    )

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
                shape = RoundedCornerShape(12.dp),
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
                options = listOf("present", "absent", "unknown"),
                selected = queenStatus,
                onSelect = { queenStatus = it },
            )
            DropdownSelector(
                label = "Source",
                options = listOf("package", "nuc", "swarm", "split"),
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
                shape = RoundedCornerShape(12.dp),
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
    var climateSuitability by remember { mutableStateOf(existingRace?.climateSuitability ?: "moderate") }
    var raceNotes by remember { mutableStateOf(existingRace?.notes ?: "") }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Charcoal,
        focusedContainerColor = Charcoal,
        unfocusedBorderColor = BorderSubtle,
        focusedBorderColor = BeeGlow,
        unfocusedLabelColor = TextTertiary,
        focusedLabelColor = BeeGlow,
        cursorColor = BeeGlow,
        unfocusedTextColor = TextPrimary,
        focusedTextColor = TextPrimary,
    )

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
                shape = RoundedCornerShape(12.dp),
            )
            OutlinedTextField(
                value = temperament,
                onValueChange = { temperament = it },
                label = { Text("Temperament") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = honeyProduction,
                    onValueChange = { honeyProduction = it.filter { c -> c.isDigit() } },
                    label = { Text("Honey (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                )
                OutlinedTextField(
                    value = miteResistance,
                    onValueChange = { miteResistance = it.filter { c -> c.isDigit() } },
                    label = { Text("Mites (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
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
                    shape = RoundedCornerShape(12.dp),
                )
                OutlinedTextField(
                    value = overwintering,
                    onValueChange = { overwintering = it.filter { c -> c.isDigit() } },
                    label = { Text("Winter (1-5)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                )
            }
            DropdownSelector(
                label = "Climate Suitability",
                options = listOf("cold", "moderate", "warm", "hot", "all climates"),
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
                shape = RoundedCornerShape(12.dp),
            )
        },
    )
}

// ── Shared Utilities ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DropdownSelector(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit, accentColor: Color = BeeGlow) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Charcoal,
                focusedContainerColor = Charcoal,
                unfocusedBorderColor = BorderSubtle,
                focusedBorderColor = accentColor,
                unfocusedLabelColor = TextTertiary,
                focusedLabelColor = accentColor,
                cursorColor = accentColor,
                unfocusedTextColor = TextPrimary,
                focusedTextColor = TextPrimary,
            ),
            shape = RoundedCornerShape(12.dp),
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

private val hiveDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

internal fun formatDate(millis: Long): String {
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().format(hiveDateFormatter)
}
