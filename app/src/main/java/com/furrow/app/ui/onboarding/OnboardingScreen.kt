package com.furrow.app.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.FurrowModule
import com.furrow.app.data.ZoneLookup
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.theme.AccentMutedGreen
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    var zipCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedModules by remember {
        mutableStateOf(
            FurrowModule.entries.filter { it.enabledByDefault }.map { it.key }.toSet(),
        )
    }

    val isValid = zipCode.length == 5 && ZoneLookup.isValidZip(zipCode)
    val preview = if (isValid) ZoneLookup.deriveProfile(zipCode) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.lg)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = currentStep == 1,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Panel {
                Text(
                    "Welcome to Furrow",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                )
                Text(
                    "Enter your ZIP code to configure climate-aware recommendations for your homestead.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
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
                    modifier = Modifier.fillMaxWidth(),
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
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                PrimaryButton(
                    text = "Next",
                    onClick = {
                        if (isValid) {
                            currentStep = 2
                        } else {
                            errorMessage = "Enter a valid US ZIP code"
                        }
                    },
                    enabled = zipCode.length == 5,
                    modifier = Modifier.fillMaxWidth(),
                )
                TextButton(
                    onClick = { viewModel.skipOnboarding() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Skip for now", color = TextSecondary)
                }
            }
        }

        AnimatedVisibility(
            visible = currentStep == 2,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Panel {
                Text(
                    "What do you manage?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                )
                Text(
                    "Select the modules you use. You can change this later in Settings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                ) {
                    FurrowModule.entries.forEach { module ->
                        val selected = module.key in selectedModules
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedModules = if (selected) {
                                    selectedModules - module.key
                                } else {
                                    selectedModules + module.key
                                }
                            },
                            label = { Text(module.displayName) },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (selected) module.selectedIcon else module.unselectedIcon,
                                    contentDescription = module.displayName,
                                    modifier = Modifier.size(18.dp),
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = module.accentColor.copy(alpha = 0.2f),
                                selectedLabelColor = TextPrimary,
                                selectedLeadingIconColor = module.accentColor,
                            ),
                            border = if (selected) {
                                BorderStroke(1.dp, module.accentColor.copy(alpha = 0.5f))
                            } else {
                                FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = false,
                                )
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                PrimaryButton(
                    text = "Get started",
                    onClick = {
                        viewModel.completeOnboarding(zipCode, selectedModules)
                    },
                    enabled = selectedModules.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
