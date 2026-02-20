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
import com.furrow.app.data.ZoneLookup
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary

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

    AppScaffold(
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
                        title = "Poultry reminders",
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
