package com.furrow.app.ui.poultry

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.ui.components.BigNumberDisplay
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.StickyBottomButton
import com.furrow.app.ui.theme.FurrowBackground
import com.furrow.app.ui.theme.FurrowCardDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EggLogScreen(
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: PoultryViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var count by remember { mutableIntStateOf(0) }
    var notes by remember { mutableStateOf("") }
    var showNotes by remember { mutableStateOf(false) }
    var countInitialized by remember { mutableStateOf(false) }
    val view = LocalView.current

    val chickens by viewModel.activeChickens.collectAsState()

    if (isEditMode) {
        val existingEggLog by viewModel.getEggLogById(editId).collectAsState(initial = null)
        LaunchedEffect(existingEggLog) {
            existingEggLog?.let {
                date = it.date
                count = it.count
                notes = it.notes ?: ""
                if (it.notes?.isNotBlank() == true) showNotes = true
                countInitialized = true
            }
        }
    }

    LaunchedEffect(chickens) {
        if (!countInitialized && chickens.isNotEmpty()) {
            count = chickens.size
            countInitialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Egg Log" else "Log Eggs") },
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
                text = "Save",
                enabled = count > 0,
                onClick = {
                    val eggLog = EggLog(
                        id = if (isEditMode) editId else 0,
                        date = date,
                        count = count,
                        notes = notes.ifBlank { null },
                    )
                    if (isEditMode) viewModel.updateEggLog(eggLog) else viewModel.addEggLog(eggLog)
                    onBack()
                },
            )
        }
    ) { padding ->
        FurrowBackground(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DateFieldWithToggle(
                    label = "Date",
                    dateMillis = date,
                    onDateChange = { date = it },
                    useTodayDefault = !isEditMode,
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            if (count > 0) {
                                count--
                                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        enabled = count > 0,
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    BigNumberDisplay(
                        value = count,
                        label = "eggs",
                        color = MaterialTheme.colorScheme.primary,
                    )
                    FilledTonalIconButton(
                        onClick = {
                            if (count < 999) {
                                count++
                                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        enabled = count < 999,
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                OutlinedCard(
                    onClick = { showNotes = !showNotes },
                    modifier = Modifier.fillMaxWidth(),
                    colors = FurrowCardDefaults.outlinedCardColors,
                    border = FurrowCardDefaults.outlinedCardBorder,
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (showNotes) "Notes" else "Add Note",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        AnimatedVisibility(visible = showNotes) {
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                placeholder = { Text("Soft shells, abnormalities, etc.") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                minLines = 3,
                            )
                        }
                    }
                }
            }
        }
    }

}
