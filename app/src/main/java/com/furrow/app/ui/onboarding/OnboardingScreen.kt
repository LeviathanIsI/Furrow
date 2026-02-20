package com.furrow.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.ZoneLookup
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.DmSans
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    var zipCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isValid = zipCode.length == 5 && ZoneLookup.isValidZip(zipCode)
    val preview = if (isValid) ZoneLookup.deriveProfile(zipCode) else null

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Icon with TINY glow — barely visible, 80dp box
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .drawBehind {
                    drawCircle(
                        color = GardenGlow.copy(alpha = 0.05f),
                        radius = size.minDimension * 0.45f,
                    )
                },
        ) {
            Icon(
                Icons.Filled.Grass,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = GardenGlow,
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            "Welcome to Furrow",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            fontFamily = DmSans,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Enter your ZIP code to personalize\nrecommendations for your garden, flock, and hives.",
            fontSize = 14.sp,
            color = TextSecondary,
            fontFamily = DmSans,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = zipCode,
            onValueChange = { input ->
                if (input.length <= 5 && input.all { it.isDigit() }) {
                    zipCode = input
                    errorMessage = null
                }
            },
            label = { Text("ZIP Code") },
            placeholder = { Text("e.g. 30301") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = errorMessage != null,
            supportingText = errorMessage?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors,
            shape = RoundedCornerShape(12.dp),
        )

        if (preview != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Zone ${preview.hardinessZone} \u2022 ${preview.state} \u2022 ${preview.climateCategory}",
                fontSize = 13.sp,
                color = GardenGlow,
                fontFamily = DmSans,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isValid) {
                    viewModel.saveProfile(zipCode)
                } else {
                    errorMessage = "Enter a valid US ZIP code"
                }
            },
            enabled = zipCode.length == 5,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GardenGlow,
                contentColor = Void,
            ),
        ) {
            Text(
                "Get Started",
                fontFamily = DmSans,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
