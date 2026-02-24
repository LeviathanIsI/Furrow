package com.furrow.app.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.FurrowModule
import com.furrow.app.data.ModuleCategory
import com.furrow.app.data.ZoneLookup
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.ToggleRow
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import java.time.ZoneId

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val profile by viewModel.profile.collectAsState()
    var zipCode by remember(profile?.zipCode) { mutableStateOf(profile?.zipCode ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isValid = zipCode.length == 5 && ZoneLookup.isValidZip(zipCode)
    val hasChanged = zipCode != (profile?.zipCode ?: "")
    val preview = if (isValid && hasChanged) ZoneLookup.deriveProfile(zipCode) else null

    val beeReminders by viewModel.beeReminders.collectAsState()
    val poultryReminders by viewModel.poultryReminders.collectAsState()
    val gardenReminders by viewModel.gardenReminders.collectAsState()

    val modulePrefs by viewModel.modulePreferences.collectAsState()
    val enabledSet = remember(modulePrefs) {
        modulePrefs.filter { it.enabled }.map { it.moduleName }.toSet()
    }

    val context = LocalContext.current
    val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
    var permissionGranted by remember { mutableStateOf(hasNotificationPermission) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        permissionGranted = granted
    }

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(
                title = "Settings",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
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
                .padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            Panel {
                Text("Profile", style = MaterialTheme.typography.titleMedium)
                profile?.let { p ->
                    Text("${p.zipCode} • Zone ${p.hardinessZone}", color = TextPrimary)
                    Text("${p.state} • ${p.climateCategory}", color = TextSecondary)
                    Text("Spring frost: ${p.lastFrostDate}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text("Fall frost: ${p.firstFrostDate}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                }
                InputField(
                    value = zipCode,
                    onValueChange = { input ->
                        if (input.length <= 5 && input.all { it.isDigit() }) {
                            zipCode = input
                            errorMessage = null
                        }
                    },
                    label = { Text("ZIP code") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                preview?.let {
                    Text(
                        "Zone ${it.hardinessZone} • ${it.state} • ${it.climateCategory}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GardenGlow,
                    )
                }
                errorMessage?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color.Red)
                }
                PrimaryButton(
                    text = "Update location",
                    onClick = {
                        if (isValid && hasChanged) {
                            viewModel.updateZipCode(zipCode)
                            errorMessage = null
                        } else if (!isValid) {
                            errorMessage = "Enter a valid US ZIP code"
                        }
                    },
                    enabled = isValid && hasChanged,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // ── Timezone ──

            TimezonePanel(
                currentTimezone = profile?.timezone ?: "",
                onTimezoneChange = { viewModel.updateTimezone(it) },
            )

            // ── Modules ──

            Panel {
                Text("Modules", style = MaterialTheme.typography.titleMedium)
                ModuleCategory.entries.forEach { category ->
                    val modules = FurrowModule.entries
                        .filter { it.category == category }
                        .sortedBy { it.displayOrder }
                    if (modules.isNotEmpty()) {
                        Text(
                            category.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = AppSpacing.xs),
                        )
                        modules.forEach { module ->
                            ToggleRow(
                                label = module.displayName,
                                checked = module.key in enabledSet,
                                accentColor = module.accentColor,
                                onCheckedChange = { enabled ->
                                    viewModel.setModuleEnabled(module.key, enabled)
                                },
                            )
                        }
                    }
                }
            }

            Panel(contentPadding = PaddingValues(0.dp)) {
                Text(
                    "Notifications",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(AppSpacing.md),
                )
                if (permissionGranted) {
                    ListRow(
                        title = "Bee reminders",
                        trailingText = if (beeReminders) "On" else "Off",
                        onClick = { viewModel.setBeeReminders(!beeReminders) },
                    )
                    ListRow(
                        title = "Animal reminders",
                        trailingText = if (poultryReminders) "On" else "Off",
                        onClick = { viewModel.setPoultryReminders(!poultryReminders) },
                    )
                    ListRow(
                        title = "Garden reminders",
                        trailingText = if (gardenReminders) "On" else "Off",
                        onClick = { viewModel.setGardenReminders(!gardenReminders) },
                        showDivider = false,
                    )
                } else {
                    ListRow(
                        title = "Grant notification permission",
                        subtitle = "Required for reminders",
                        leadingIcon = {
                            Icon(Icons.Outlined.Notifications, contentDescription = null, tint = TextSecondary)
                        },
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        },
                        showDivider = false,
                    )
                }
            }

            Panel {
                Text("About", style = MaterialTheme.typography.titleMedium)
                Text("Version 1.0.0", color = TextSecondary)
            }
        }
    }
}

private val commonTimezones = listOf(
    "America/New_York" to "Eastern (ET)",
    "America/Chicago" to "Central (CT)",
    "America/Denver" to "Mountain (MT)",
    "America/Los_Angeles" to "Pacific (PT)",
    "America/Anchorage" to "Alaska (AKT)",
    "Pacific/Honolulu" to "Hawaii (HT)",
    "America/Puerto_Rico" to "Atlantic (AT)",
)

private fun timezoneLabel(tzId: String): String {
    return commonTimezones.firstOrNull { it.first == tzId }?.second
        ?: tzId.ifBlank { "System default" }
}

@Composable
private fun TimezonePanel(
    currentTimezone: String,
    onTimezoneChange: (String) -> Unit,
) {
    var showPicker by remember { mutableStateOf(false) }
    var showAll by remember { mutableStateOf(false) }

    Panel {
        Text("Timezone", style = MaterialTheme.typography.titleMedium)
        Text(
            text = timezoneLabel(currentTimezone),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        PrimaryButton(
            text = "Change timezone",
            onClick = { showPicker = true },
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (showPicker) {
        TimezonePicker(
            currentTimezone = currentTimezone,
            showAll = showAll,
            onShowAllToggle = { showAll = it },
            onSelect = {
                onTimezoneChange(it)
                showPicker = false
            },
            onDismiss = { showPicker = false },
        )
    }
}

@Composable
private fun TimezonePicker(
    currentTimezone: String,
    showAll: Boolean,
    onShowAllToggle: (Boolean) -> Unit,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val allZones = remember {
        ZoneId.getAvailableZoneIds().sorted()
    }

    var searchQuery by remember { mutableStateOf("") }

    val displayedZones = if (showAll) {
        if (searchQuery.isBlank()) allZones
        else allZones.filter { it.contains(searchQuery, ignoreCase = true) }
    } else {
        commonTimezones.map { it.first }
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = com.furrow.app.ui.theme.Obsidian,
        title = { Text("Select Timezone", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                if (showAll) {
                    InputField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search") },
                        singleLine = true,
                    )
                }
                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    displayedZones.forEach { tzId ->
                        val label = if (showAll) tzId else timezoneLabel(tzId)
                        val isCurrent = tzId == currentTimezone
                        ListRow(
                            title = label,
                            onClick = { onSelect(tzId) },
                            trailingText = if (isCurrent) "Current" else null,
                            showDivider = false,
                        )
                    }
                }
                ToggleRow(
                    label = "Show all timezones",
                    checked = showAll,
                    accentColor = GardenGlow,
                    onCheckedChange = { onShowAllToggle(it) },
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
    )
}
