package com.furrow.app.ui.bees

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.DropdownSelector
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.Treatment
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.DiscardChangesDialog
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.SuccessSnackbarEffect
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.theme.BeeGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.Void

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
    var type by remember { mutableStateOf("Oxalic Acid") }
    var method by remember { mutableStateOf("Dribble") }
    var dose by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDiscardDialog by remember { mutableStateOf(false) }
    BackHandler { showDiscardDialog = true }
    DiscardChangesDialog(showDialog = showDiscardDialog, onDismiss = { showDiscardDialog = false }, onDiscard = { showDiscardDialog = false; onBack() })

    if (isEditMode) {
        val existingTreatment by viewModel.getTreatmentById(editId).collectAsState(initial = null)
        LaunchedEffect(existingTreatment) {
            existingTreatment?.let {
                date = it.date
                type = it.type
                method = it.method ?: "Dribble"
                dose = it.dose ?: ""
                notes = it.notes ?: ""
            }
        }
    }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = BeeGlow, bordered = true)

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)
    SuccessSnackbarEffect(
        message = viewModel.successMessage,
        onClear = viewModel::clearSuccess,
        snackbarHostState = snackbarHostState,
        onDismissed = { onBack() },
    )

    com.furrow.app.ui.components.AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            com.furrow.app.ui.components.AppTopBar(
                title = if (isEditMode) "Edit treatment" else "Add treatment",
                navigationIcon = {
                    IconButton(onClick = { showDiscardDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DateFieldWithToggle(
                label = "Date",
                dateMillis = date,
                onDateChange = { date = it },
                useTodayDefault = !isEditMode,
                zone = viewModel.zone,
            )

            // ── Section Header ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(3.dp, 16.dp)
                        .background(BeeGlow, RoundedCornerShape(2.dp)),
                )
                Text(
                    "Treatment details",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary,
                )
            }

            DropdownSelector(
                label = "Treatment Type",
                options = listOf("Oxalic Acid", "Apivar", "Formic Pro", "Apiguard", "Thymol", "Other"),
                selected = type,
                onSelect = { type = it },
            )

            DropdownSelector(
                label = "Method",
                options = listOf("Dribble", "Vaporize", "Strip", "Pad", "Other"),
                selected = method,
                onSelect = { method = it },
            )

            InputField(
                value = dose,
                onValueChange = { dose = it },
                label = { Text("Dose") },
                placeholder = { Text("e.g. 2ml per seam") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors,
            )

            InputField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = fieldColors,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Save Button ──
            PrimaryButton(
                text = "Save treatment",
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
                },
                modifier = Modifier
                    .fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


