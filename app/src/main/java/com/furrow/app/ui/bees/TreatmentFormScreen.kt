package com.furrow.app.ui.bees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.Treatment
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.FormCard
import com.furrow.app.ui.components.FormSectionHeader
import com.furrow.app.ui.components.StickyBottomButton
import com.furrow.app.ui.theme.FurrowBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentFormScreen(
    hiveId: Long,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: BeeViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var type by remember { mutableStateOf("oxalic acid") }
    var method by remember { mutableStateOf("dribble") }
    var dose by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existingTreatment by viewModel.getTreatmentById(editId).collectAsState(initial = null)
        LaunchedEffect(existingTreatment) {
            existingTreatment?.let {
                date = it.date
                type = it.type
                method = it.method ?: "dribble"
                dose = it.dose ?: ""
                notes = it.notes ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Treatment" else "Add Treatment") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        bottomBar = {
            StickyBottomButton(
                text = "Save Treatment",
                onClick = {
                    val treatment = Treatment(
                        id = if (isEditMode) editId else 0,
                        hiveId = hiveId,
                        date = date,
                        type = type,
                        method = method.ifBlank { null },
                        dose = dose.ifBlank { null },
                        notes = notes.ifBlank { null },
                    )
                    if (isEditMode) viewModel.updateTreatment(treatment) else viewModel.addTreatment(treatment)
                    onBack()
                },
            )
        }
    ) { padding ->
        FurrowBackground(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DateFieldWithToggle(
                    label = "Date",
                    dateMillis = date,
                    onDateChange = { date = it },
                    useTodayDefault = !isEditMode,
                )

                FormSectionHeader(title = "Treatment Details")
                FormCard {
                    DropdownSelector(
                        label = "Treatment Type",
                        options = listOf("oxalic acid", "apivar", "formic pro", "apiguard", "thymol", "other"),
                        selected = type,
                        onSelect = { type = it },
                    )
                    DropdownSelector(
                        label = "Method",
                        options = listOf("dribble", "vaporize", "strip", "pad", "other"),
                        selected = method,
                        onSelect = { method = it },
                    )
                    OutlinedTextField(
                        value = dose,
                        onValueChange = { dose = it },
                        label = { Text("Dose") },
                        placeholder = { Text("e.g. 2ml per seam") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
