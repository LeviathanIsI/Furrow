package com.furrow.app.ui.animals

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.theme.AnimalsGlow
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EggLogScreen(
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: AnimalViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var count by remember { mutableIntStateOf(0) }
    var notes by remember { mutableStateOf("") }
    var showNotes by remember { mutableStateOf(false) }
    var countInitialized by remember { mutableStateOf(false) }
    val view = LocalView.current

    val animals by viewModel.activeChickens.collectAsState()

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

    LaunchedEffect(animals) {
        if (!countInitialized && animals.isNotEmpty()) {
            count = animals.size
            countInitialized = true
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Charcoal,
        unfocusedContainerColor = Charcoal,
        focusedBorderColor = AnimalsGlow,
        unfocusedBorderColor = BorderSubtle,
        focusedLabelColor = AnimalsGlow,
        unfocusedLabelColor = TextTertiary,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        cursorColor = AnimalsGlow,
    )

    AppScaffold(
        topBar = {
            AppTopBar(
                title = if (isEditMode) "Edit egg log" else "Log eggs",
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DateFieldWithToggle(
                label = "Date",
                dateMillis = date,
                onDateChange = { date = it },
                useTodayDefault = !isEditMode,
                accentColor = AnimalsGlow,
            )

            Spacer(modifier = Modifier.weight(1f))

            // Big egg counter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                IconButton(
                    onClick = {
                        if (count > 0) {
                            count--
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        }
                    },
                    modifier = Modifier.size(56.dp),
                    enabled = count > 0,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Charcoal,
                        contentColor = TextPrimary,
                        disabledContentColor = TextTertiary,
                    ),
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }

                // Big number display
                Box(
                    modifier = Modifier
                        .size(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = AnimalsGlow,
                        )
                        Text(
                            text = "eggs",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                        )
                    }
                }

                IconButton(
                    onClick = {
                        if (count < 999) {
                            count++
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        }
                    },
                    modifier = Modifier.size(56.dp),
                    enabled = count < 999,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Charcoal,
                        contentColor = TextPrimary,
                        disabledContentColor = TextTertiary,
                    ),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Notes section
            Panel(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showNotes = !showNotes },
            ) {
                Column {
                    Text(
                        text = if (showNotes) "Notes" else "Add Note",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary,
                    )
                    AnimatedVisibility(visible = showNotes) {
                        InputField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = { Text("Soft shells, abnormalities, etc.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            minLines = 3,
                            colors = fieldColors,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            PrimaryButton(
                text = "Save",
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
                enabled = count > 0,
                modifier = Modifier
                    .fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
