package com.furrow.app.ui.animals.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import com.furrow.app.data.local.entity.Animal
import com.furrow.app.util.displayFormat
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.animals.AnimalViewModel
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.theme.AnimalsGlow
import com.furrow.app.ui.theme.AppSpacing

@Composable
internal fun AnimalForm(
    animalId: Long,
    editId: Long,
    isEditMode: Boolean,
    viewModel: AnimalViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var speciesDisplay by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("Unknown") }
    var tagId by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Active") }
    var dob by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var useDob by remember { mutableStateOf(false) }
    var acquisitionDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var source by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getAnimalById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                name = it.name ?: ""
                species = it.species
                speciesDisplay = it.species.displayFormat()
                breed = it.breed
                sex = it.sex.displayFormat()
                tagId = it.tagId ?: ""
                status = it.status.displayFormat()
                if (it.dob != null) {
                    dob = it.dob
                    useDob = true
                }
                acquisitionDate = it.acquisitionDate
                source = it.source ?: ""
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
        val breeds by viewModel.getBreedsForSpecies(species).collectAsState(initial = emptyList())

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        InputField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            accentColor = AnimalsGlow,
        )

        DropdownSelector(
            label = "Species *",
            options = ModuleCatalogData.SPECIES,
            selected = speciesDisplay,
            onSelect = {
                species = it.trim().lowercase()
                speciesDisplay = it
                breed = ""
            },
            accentColor = AnimalsGlow,
        )

        DropdownSelector(
            label = "Breed",
            options = breeds.map { it.name },
            selected = breed,
            onSelect = { breed = it },
            accentColor = AnimalsGlow,
        )

        DropdownSelector(
            label = "Sex",
            options = listOf("Male", "Female", "Unknown"),
            selected = sex,
            onSelect = { sex = it },
            accentColor = AnimalsGlow,
        )

        InputField(
            value = tagId,
            onValueChange = { tagId = it },
            label = { Text("Tag ID") },
            singleLine = true,
            accentColor = AnimalsGlow,
        )

        DropdownSelector(
            label = "Status",
            options = listOf("Active", "Sold", "Deceased", "Culled"),
            selected = status,
            onSelect = { status = it },
            accentColor = AnimalsGlow,
        )

        if (useDob || isEditMode) {
            DateFieldWithToggle(
                label = "Date of Birth",
                dateMillis = dob,
                onDateChange = { dob = it; useDob = true },
                useTodayDefault = false,
                accentColor = AnimalsGlow,
                zone = viewModel.zone,
            )
        } else {
            DateFieldWithToggle(
                label = "Date of Birth",
                dateMillis = dob,
                onDateChange = { dob = it; useDob = true },
                useTodayDefault = false,
                accentColor = AnimalsGlow,
                zone = viewModel.zone,
            )
        }

        DateFieldWithToggle(
            label = "Acquisition Date",
            dateMillis = acquisitionDate,
            onDateChange = { acquisitionDate = it },
            useTodayDefault = !isEditMode,
            accentColor = AnimalsGlow,
            zone = viewModel.zone,
        )

        InputField(
            value = source,
            onValueChange = { source = it },
            label = { Text("Source") },
            singleLine = true,
            accentColor = AnimalsGlow,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = AnimalsGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Animal" else "Save Animal",
            onClick = {
                if (species.isBlank()) return@PrimaryButton
                val animal = Animal(
                    id = if (isEditMode) editId else 0,
                    name = name.ifBlank { null },
                    species = species.trim().lowercase(),
                    breed = breed.trim(),
                    sex = sex,
                    tagId = tagId.ifBlank { null },
                    status = status,
                    dob = if (useDob) dob else null,
                    acquisitionDate = acquisitionDate,
                    source = source.ifBlank { null },
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateAnimal(animal) else viewModel.addAnimal(animal)
            },
            enabled = species.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
