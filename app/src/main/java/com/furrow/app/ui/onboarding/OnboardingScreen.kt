package com.furrow.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.ZoneLookup
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    var zipCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isValid = zipCode.length == 5 && ZoneLookup.isValidZip(zipCode)
    val preview = if (isValid) ZoneLookup.deriveProfile(zipCode) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.lg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Panel {
            Text(
                "Welcome to Furrow",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
            )
            Text(
                "Enter your ZIP code to configure climate-aware recommendations for bees, poultry, and garden modules.",
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
                text = "Get started",
                onClick = {
                    if (isValid) {
                        viewModel.saveProfile(zipCode)
                    } else {
                        errorMessage = "Enter a valid US ZIP code"
                    }
                },
                enabled = zipCode.length == 5,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
