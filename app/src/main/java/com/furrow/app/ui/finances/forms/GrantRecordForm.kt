package com.furrow.app.ui.finances.forms

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
import androidx.compose.ui.unit.dp
import com.furrow.app.data.local.entity.GrantRecord
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.finances.FinanceViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.FinanceGlow
import com.furrow.app.util.filterDecimal

@Composable
internal fun GrantRecordForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: FinanceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var program by remember { mutableStateOf("") }
    var agency by remember { mutableStateOf("") }
    var applicationDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var status by remember { mutableStateOf("Applied") }
    var awardAmount by remember { mutableStateOf("") }
    var costSharePct by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getGrantRecordById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                program = it.program ?: ""
                agency = it.agency ?: ""
                applicationDate = it.applicationDate ?: System.currentTimeMillis()
                status = it.status ?: "applied"
                awardAmount = it.awardAmount?.toString() ?: ""
                costSharePct = it.costSharePct?.toString() ?: ""
                notes = it.notes ?: ""
            }
        }
    }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = FinanceGlow)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        InputField(
            value = program,
            onValueChange = { program = it },
            label = { Text("Program (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        InputField(
            value = agency,
            onValueChange = { agency = it },
            label = { Text("Agency (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        DateFieldWithToggle(
            label = "Application Date",
            dateMillis = applicationDate,
            onDateChange = { applicationDate = it },
            zone = viewModel.zone,
            useTodayDefault = !isEditMode,
            accentColor = FinanceGlow,
        )

        DropdownSelector(
            label = "Status",
            options = listOf("Applied", "Awarded", "Denied", "Completed"),
            selected = status,
            onSelect = { status = it },
            accentColor = FinanceGlow,
        )

        InputField(
            value = awardAmount,
            onValueChange = { awardAmount = it.filterDecimal() },
            label = { Text("Award Amount (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        InputField(
            value = costSharePct,
            onValueChange = { costSharePct = it.filterDecimal() },
            label = { Text("Cost Share % (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Grant" else "Save Grant",
            onClick = {
                val grant = GrantRecord(
                    id = if (isEditMode) editId else 0,
                    program = program.ifBlank { null },
                    agency = agency.ifBlank { null },
                    applicationDate = applicationDate,
                    status = status,
                    awardAmount = awardAmount.toDoubleOrNull(),
                    costSharePct = costSharePct.toDoubleOrNull(),
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateGrantRecord(grant) else viewModel.addGrantRecord(grant)
            },
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
