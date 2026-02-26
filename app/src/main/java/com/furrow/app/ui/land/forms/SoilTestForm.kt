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
import com.furrow.app.data.local.entity.SoilTest
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PhotoAttachment
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.land.LandViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.LandGlow

@Composable
internal fun SoilTestForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: LandViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var location by remember { mutableStateOf("") }
    var ph by remember { mutableStateOf("") }
    var nitrogenPpm by remember { mutableStateOf("") }
    var phosphorusPpm by remember { mutableStateOf("") }
    var potassiumPpm by remember { mutableStateOf("") }
    var organicMatterPct by remember { mutableStateOf("") }
    var texture by remember { mutableStateOf("") }
    var recommendations by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }

    if (isEditMode) {
        val existing by viewModel.getSoilTestById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                location = it.location ?: ""
                ph = it.ph?.toString() ?: ""
                nitrogenPpm = it.nitrogenPpm?.toString() ?: ""
                phosphorusPpm = it.phosphorusPpm?.toString() ?: ""
                potassiumPpm = it.potassiumPpm?.toString() ?: ""
                organicMatterPct = it.organicMatterPct?.toString() ?: ""
                texture = it.texture ?: ""
                recommendations = it.recommendations ?: ""
                photoUri = it.photoUri
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
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            singleLine = true,
            accentColor = LandGlow,
        )

        InputField(
            value = ph,
            onValueChange = { ph = it },
            label = { Text("pH") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = nitrogenPpm,
            onValueChange = { nitrogenPpm = it },
            label = { Text("Nitrogen (ppm)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = phosphorusPpm,
            onValueChange = { phosphorusPpm = it },
            label = { Text("Phosphorus (ppm)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = potassiumPpm,
            onValueChange = { potassiumPpm = it },
            label = { Text("Potassium (ppm)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = organicMatterPct,
            onValueChange = { organicMatterPct = it },
            label = { Text("Organic Matter (%)") },
            singleLine = true,
            accentColor = LandGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        DropdownSelector(
            label = "Texture",
            options = ModuleCatalogData.SOIL_TEXTURES,
            selected = texture,
            onSelect = { texture = it },
            accentColor = LandGlow,
        )

        InputField(
            value = recommendations,
            onValueChange = { recommendations = it },
            label = { Text("Recommendations") },
            minLines = 3,
            accentColor = LandGlow,
        )

        PhotoAttachment(
            photoUri = photoUri,
            onPhotoChanged = { photoUri = it },
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Soil Test" else "Save Soil Test",
            onClick = {
                val soilTest = SoilTest(
                    id = if (isEditMode) editId else 0,
                    date = date,
                    location = location.ifBlank { null },
                    ph = ph.toDoubleOrNull(),
                    nitrogenPpm = nitrogenPpm.toDoubleOrNull(),
                    phosphorusPpm = phosphorusPpm.toDoubleOrNull(),
                    potassiumPpm = potassiumPpm.toDoubleOrNull(),
                    organicMatterPct = organicMatterPct.toDoubleOrNull(),
                    texture = texture.ifBlank { null },
                    recommendations = recommendations.ifBlank { null },
                    photoUri = photoUri,
                )
                if (isEditMode) viewModel.updateSoilTest(soilTest) else viewModel.addSoilTest(soilTest)
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
