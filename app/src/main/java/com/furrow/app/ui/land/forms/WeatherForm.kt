package com.furrow.app.ui.land.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.furrow.app.data.local.entity.WeatherLog
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.ToggleRow
import com.furrow.app.ui.land.LandViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.LandGlow

@Composable
internal fun WeatherForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: LandViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var highTemp by remember { mutableStateOf("") }
    var lowTemp by remember { mutableStateOf("") }
    var rainfallIn by remember { mutableStateOf("") }
    var snowfallIn by remember { mutableStateOf("") }
    var humidityPct by remember { mutableStateOf("") }
    var windSpeed by remember { mutableStateOf("") }
    var frostFlag by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getWeatherLogById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                highTemp = it.highTemp?.toString() ?: ""
                lowTemp = it.lowTemp?.toString() ?: ""
                rainfallIn = it.rainfallIn?.toString() ?: ""
                snowfallIn = it.snowfallIn?.toString() ?: ""
                humidityPct = it.humidityPct?.toString() ?: ""
                windSpeed = it.windSpeed?.toString() ?: ""
                frostFlag = it.frostFlag
                notes = it.notes ?: ""
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        DateFieldWithToggle(
            label = "Date",
            dateMillis = date,
            onDateChange = { date = it },
            useTodayDefault = !isEditMode,
            accentColor = LandGlow,
            zone = viewModel.zone,
        )

        InputField(
            value = highTemp,
            onValueChange = { highTemp = it },
            label = { Text("High Temp (\u00B0F)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        InputField(
            value = lowTemp,
            onValueChange = { lowTemp = it },
            label = { Text("Low Temp (\u00B0F)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        InputField(
            value = rainfallIn,
            onValueChange = { rainfallIn = it },
            label = { Text("Rainfall (in)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = snowfallIn,
            onValueChange = { snowfallIn = it },
            label = { Text("Snowfall (in)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = humidityPct,
            onValueChange = { humidityPct = it },
            label = { Text("Humidity (%)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        InputField(
            value = windSpeed,
            onValueChange = { windSpeed = it },
            label = { Text("Wind Speed (mph)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        ToggleRow(
            label = "Frost",
            checked = frostFlag,
            accentColor = LandGlow,
            onCheckedChange = { frostFlag = it },
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = LandGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Weather Log" else "Save Weather Log",
            onClick = {
                val weatherLog = WeatherLog(
                    id = if (isEditMode) editId else 0,
                    date = date,
                    highTemp = highTemp.toIntOrNull(),
                    lowTemp = lowTemp.toIntOrNull(),
                    rainfallIn = rainfallIn.toDoubleOrNull(),
                    snowfallIn = snowfallIn.toDoubleOrNull(),
                    humidityPct = humidityPct.toIntOrNull(),
                    windSpeed = windSpeed.toIntOrNull(),
                    frostFlag = frostFlag,
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateWeatherLog(weatherLog) else viewModel.addWeatherLog(weatherLog)
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
