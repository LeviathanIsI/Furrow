package com.furrow.app.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.ZoneLookup
import com.furrow.app.ui.theme.BeeGlow
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.PoultryGlow
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void

@OptIn(ExperimentalMaterial3Api::class)
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

    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Charcoal,
        focusedContainerColor = Charcoal,
        unfocusedBorderColor = BorderSubtle,
        focusedBorderColor = GardenGlow,
        unfocusedLabelColor = TextTertiary,
        focusedLabelColor = GardenGlow,
        cursorColor = GardenGlow,
        unfocusedTextColor = TextPrimary,
        focusedTextColor = TextPrimary,
    )

    Scaffold(
        containerColor = Void,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Void),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── Profile Section ──
            SectionLabel("PROFILE")

            // Current location info
            profile?.let { p ->
                SettingsInfoRow(
                    icon = Icons.Filled.LocationOn,
                    label = "Location",
                    value = "${p.zipCode} \u2022 Zone ${p.hardinessZone}",
                )
                SettingsInfoRow(label = "Climate", value = p.climateCategory, indent = true)
                SettingsInfoRow(label = "State", value = p.state, indent = true)
                SettingsInfoRow(label = "Last Spring Frost", value = p.lastFrostDate, indent = true)
                SettingsInfoRow(label = "First Fall Frost", value = p.firstFrostDate, indent = true)
            }

            HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))

            // Update ZIP
            OutlinedTextField(
                value = zipCode,
                onValueChange = { input ->
                    if (input.length <= 5 && input.all { it.isDigit() }) {
                        zipCode = input
                        errorMessage = null
                    }
                },
                label = { Text("Update ZIP Code") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errorMessage != null,
                supportingText = errorMessage?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
            )

            if (preview != null) {
                Text(
                    "Zone ${preview.hardinessZone} \u2022 ${preview.state} \u2022 ${preview.climateCategory}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GardenGlow,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }

            Button(
                onClick = {
                    if (isValid && hasChanged) {
                        viewModel.updateZipCode(zipCode)
                        errorMessage = null
                    } else if (!isValid) {
                        errorMessage = "Enter a valid US ZIP code"
                    }
                },
                enabled = isValid && hasChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GardenGlow,
                    contentColor = Void,
                ),
            ) {
                Text("Update Location")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Notifications Section ──
            SectionLabel("NOTIFICATIONS")

            if (permissionGranted) {
                NotificationToggle(
                    label = "Bee Reminders",
                    checked = beeReminders,
                    onCheckedChange = viewModel::setBeeReminders,
                    accentColor = BeeGlow,
                )
                NotificationToggle(
                    label = "Poultry Reminders",
                    checked = poultryReminders,
                    onCheckedChange = viewModel::setPoultryReminders,
                    accentColor = PoultryGlow,
                )
                NotificationToggle(
                    label = "Garden Reminders",
                    checked = gardenReminders,
                    onCheckedChange = viewModel::setGardenReminders,
                    accentColor = GardenGlow,
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS,
                                )
                            }
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        Icons.Filled.NotificationsOff,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = TextTertiary,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Grant Permission",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                        )
                        Text(
                            "Required for reminders",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── About Section ──
            SectionLabel("ABOUT")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Version",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(3.dp, 16.dp)
                .background(GardenGlow, RoundedCornerShape(2.dp)),
        )
        Text(
            title,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
            color = TextTertiary,
        )
    }
}

@Composable
private fun SettingsInfoRow(
    label: String,
    value: String,
    icon: ImageVector? = null,
    indent: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (indent) 36.dp else 0.dp,
                top = 6.dp,
                bottom = 6.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = TextTertiary,
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.width(140.dp),
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
        )
    }
}

@Composable
private fun NotificationToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                Icons.Filled.Notifications,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = TextTertiary,
            )
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.3f),
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = TextTertiary,
                uncheckedTrackColor = BorderSubtle,
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}
