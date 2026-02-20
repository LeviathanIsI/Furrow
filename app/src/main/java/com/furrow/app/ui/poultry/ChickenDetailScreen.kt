package com.furrow.app.ui.poultry

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.ui.bees.DropdownSelector
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.SecondaryButton
import com.furrow.app.ui.components.Tag
import com.furrow.app.ui.components.AppSectionHeader
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.PoultryGlow
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val zone: ZoneId = ZoneId.systemDefault()
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChickenDetailScreen(
    onBack: () -> Unit,
    viewModel: PoultryViewModel = hiltViewModel(),
) {
    val chicken by viewModel.selectedChicken.collectAsState()

    val c = chicken
    var name by remember(c) { mutableStateOf(c?.name ?: "") }
    var breed by remember(c) { mutableStateOf(c?.breed ?: "") }
    var status by remember(c) { mutableStateOf(c?.status ?: "active") }
    var notes by remember(c) { mutableStateOf(c?.notes ?: "") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Charcoal,
        focusedContainerColor = Charcoal,
        unfocusedBorderColor = BorderSubtle,
        focusedBorderColor = PoultryGlow,
        unfocusedLabelColor = TextTertiary,
        focusedLabelColor = PoultryGlow,
        cursorColor = PoultryGlow,
        unfocusedTextColor = TextPrimary,
        focusedTextColor = TextPrimary,
    )

    com.furrow.app.ui.components.AppScaffold(
        topBar = {
            com.furrow.app.ui.components.AppTopBar(
                title = c?.name ?: c?.breed ?: "Chicken",
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
        if (c == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading...", color = TextTertiary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                // ── Header Card ──
                Panel(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        c.name ?: c.breed,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                    )
                    if (c.name != null) {
                        Text(
                            c.breed,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val statusColor = when (c.status) {
                            "active" -> StatusGood
                            "deceased" -> StatusBad
                            else -> TextSecondary
                        }
                        Tag(
                            text = c.status.replaceFirstChar { it.uppercase() },
                            selected = c.status == "active",
                            accentColor = statusColor,
                        )
                        Text(
                            "Acquired ${formatDate(c.dateAcquired)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                    c.dateOfBirth?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Born ${formatDate(it)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                        )
                    }
                }

                // ── Details Section ──
                FormSectionHeader("Details")

                InputField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors,
                )

                InputField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Breed") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors,
                )

                DropdownSelector(
                    label = "Status",
                    options = listOf("active", "deceased", "rehomed", "processed"),
                    selected = status,
                    onSelect = { status = it },
                    accentColor = PoultryGlow,
                )

                // ── Notes Section ──
                FormSectionHeader("Notes")

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
                    text = "Save changes",
                    onClick = {
                        viewModel.updateChicken(
                            c.copy(
                                name = name.ifBlank { null },
                                breed = breed.trim(),
                                status = status,
                                notes = notes.ifBlank { null },
                            ),
                        )
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = breed.isNotBlank(),
                )

                // ── Delete Button ──
                SecondaryButton(
                    text = "Delete",
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showDeleteDialog && c != null) {
        DeleteConfirmationDialog(
            itemName = c.name ?: c.breed,
            onConfirm = {
                viewModel.deleteChicken(c)
                showDeleteDialog = false
                onBack()
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

@Composable
private fun FormSectionHeader(title: String) {
    AppSectionHeader(title = title)
}

private fun formatDate(millis: Long): String {
    return Instant.ofEpochMilli(millis).atZone(zone).toLocalDate().format(dateFormatter)
}



