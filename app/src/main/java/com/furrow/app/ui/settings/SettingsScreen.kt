package com.furrow.app.ui.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.ZoneLookup
import com.furrow.app.ui.theme.FurrowBackground
import com.furrow.app.ui.theme.FurrowCardDefaults

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    ) { padding ->
        FurrowBackground(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Current zone info
                profile?.let { p ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(FurrowCardDefaults.elevatedCardBorder, MaterialTheme.shapes.medium),
                        colors = FurrowCardDefaults.elevatedCardColors,
                        elevation = FurrowCardDefaults.elevatedCardElevation,
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Your Location",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            InfoRow("ZIP Code", p.zipCode)
                            InfoRow("Hardiness Zone", p.hardinessZone)
                            InfoRow("Zone Group", p.climateCategory)
                            InfoRow("State", p.state)
                            InfoRow("Last Spring Frost", p.lastFrostDate)
                            InfoRow("First Fall Frost", p.firstFrostDate)
                        }
                    }
                }

                // Update ZIP
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(FurrowCardDefaults.elevatedCardBorder, MaterialTheme.shapes.medium),
                    colors = FurrowCardDefaults.elevatedCardColors,
                    elevation = FurrowCardDefaults.elevatedCardElevation,
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Update Location",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = zipCode,
                            onValueChange = { input ->
                                if (input.length <= 5 && input.all { it.isDigit() }) {
                                    zipCode = input
                                    errorMessage = null
                                }
                            },
                            label = { Text("ZIP Code") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = errorMessage != null,
                            supportingText = errorMessage?.let { { Text(it) } },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        if (preview != null) {
                            Text(
                                "Zone ${preview.hardinessZone} \u2022 ${preview.state} \u2022 ${preview.climateCategory}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
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
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Update")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(140.dp),
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
