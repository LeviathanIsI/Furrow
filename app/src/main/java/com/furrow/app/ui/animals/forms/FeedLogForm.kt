package com.furrow.app.ui.animals.forms

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
import com.furrow.app.data.local.entity.FeedLog
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.animals.AnimalViewModel
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.theme.AnimalsGlow
import com.furrow.app.ui.theme.AppSpacing

@Composable
internal fun FeedLogForm(
    animalId: Long,
    editId: Long,
    isEditMode: Boolean,
    viewModel: AnimalViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var feedType by remember { mutableStateOf("") }
    var quantityLbs by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getFeedLogById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                feedType = it.feedType
                quantityLbs = it.quantityLbs?.toString() ?: ""
                cost = it.cost?.toString() ?: ""
                supplier = it.supplier ?: ""
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
            accentColor = AnimalsGlow,
            zone = viewModel.zone,
        )

        DropdownSelector(
            label = "Feed Type",
            options = ModuleCatalogData.FEED_TYPES,
            selected = feedType,
            onSelect = { feedType = it },
            accentColor = AnimalsGlow,
        )

        InputField(
            value = quantityLbs,
            onValueChange = { quantityLbs = it },
            label = { Text("Quantity (lbs)") },
            singleLine = true,
            accentColor = AnimalsGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = cost,
            onValueChange = { cost = it },
            label = { Text("Cost") },
            singleLine = true,
            accentColor = AnimalsGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        InputField(
            value = supplier,
            onValueChange = { supplier = it },
            label = { Text("Supplier") },
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
            text = if (isEditMode) "Update Feed Log" else "Save Feed Log",
            onClick = {
                if (feedType.isBlank()) return@PrimaryButton
                val log = FeedLog(
                    id = if (isEditMode) editId else 0,
                    animalId = animalId,
                    date = date,
                    feedType = feedType.trim(),
                    quantityLbs = quantityLbs.toDoubleOrNull(),
                    cost = cost.toDoubleOrNull(),
                    supplier = supplier.ifBlank { null },
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateFeedLog(log) else viewModel.addFeedLog(log)
            },
            enabled = feedType.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
