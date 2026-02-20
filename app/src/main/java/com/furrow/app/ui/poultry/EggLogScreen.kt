package com.furrow.app.ui.poultry

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.EggLog
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.PoultryGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void

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

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Charcoal,
        unfocusedContainerColor = Charcoal,
        focusedBorderColor = PoultryGlow,
        unfocusedBorderColor = BorderSubtle,
        focusedLabelColor = PoultryGlow,
        unfocusedLabelColor = TextTertiary,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        cursorColor = PoultryGlow,
    )

    Scaffold(
        containerColor = Void,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Egg Log" else "Log Eggs",
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DateFieldWithToggle(
                label = "Date",
                dateMillis = date,
                onDateChange = { date = it },
                useTodayDefault = !isEditMode,
                accentColor = PoultryGlow,
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
                            color = PoultryGlow,
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Charcoal,
                onClick = { showNotes = !showNotes },
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (showNotes) "Notes" else "Add Note",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary,
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
                            colors = fieldColors,
                            shape = RoundedCornerShape(12.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
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
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PoultryGlow,
                    contentColor = Void,
                ),
            ) {
                Text("Save")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
