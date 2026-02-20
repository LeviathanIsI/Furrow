package com.furrow.app.ui.bees

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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.furrow.app.ui.theme.BeeGlow
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextTertiary
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

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Charcoal,
        unfocusedContainerColor = Charcoal,
        focusedBorderColor = BeeGlow,
        unfocusedBorderColor = BorderSubtle,
        focusedLabelColor = BeeGlow,
        unfocusedLabelColor = TextTertiary,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        cursorColor = BeeGlow,
    )

    Scaffold(
        containerColor = Void,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Treatment" else "Add Treatment",
                        color = TextPrimary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Void),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DateFieldWithToggle(
                label = "Date",
                dateMillis = date,
                onDateChange = { date = it },
                useTodayDefault = !isEditMode,
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
                    "TREATMENT DETAILS",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
                    color = TextTertiary,
                )
            }

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
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Save Button ──
            Button(
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BeeGlow,
                    contentColor = Void,
                ),
            ) {
                Text("Save Treatment")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
