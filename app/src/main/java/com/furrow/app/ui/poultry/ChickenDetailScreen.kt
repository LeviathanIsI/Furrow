package com.furrow.app.ui.poultry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.ui.bees.DropdownSelector
import com.furrow.app.ui.bees.formatDate
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.FormCard
import com.furrow.app.ui.components.FormSectionHeader
import com.furrow.app.ui.theme.FurrowBackground
import com.furrow.app.ui.theme.FurrowCardDefaults

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(c?.name ?: c?.breed ?: "Chicken") },
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
            if (c != null) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.updateChicken(
                                        c.copy(
                                            name = name.ifBlank { null },
                                            breed = breed.trim(),
                                            status = status,
                                            notes = notes.ifBlank { null },
                                        )
                                    )
                                    onBack()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = breed.isNotBlank(),
                            ) {
                                Text("Save Changes")
                            }
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error,
                                ),
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        FurrowBackground(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (c == null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Header card
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(FurrowCardDefaults.elevatedCardBorder, MaterialTheme.shapes.medium),
                        colors = FurrowCardDefaults.elevatedCardColors,
                        elevation = FurrowCardDefaults.elevatedCardElevation,
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                c.name ?: c.breed,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            if (c.name != null) {
                                Text(
                                    c.breed,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                val (statusLabel, statusColor) = when (c.status) {
                                    "active" -> "Active" to MaterialTheme.colorScheme.primary
                                    "deceased" -> "Deceased" to MaterialTheme.colorScheme.error
                                    else -> c.status.replaceFirstChar { it.uppercase() } to MaterialTheme.colorScheme.onSurfaceVariant
                                }
                                Text(
                                    statusLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = statusColor,
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Acquired: ${formatDate(c.dateAcquired)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            c.dateOfBirth?.let {
                                Text(
                                    "Born: ${formatDate(it)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    // Editable form
                    FormSectionHeader(title = "Details")
                    FormCard {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = breed,
                            onValueChange = { breed = it },
                            label = { Text("Breed") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        DropdownSelector(
                            label = "Status",
                            options = listOf("active", "deceased", "rehomed", "processed"),
                            selected = status,
                            onSelect = { status = it },
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
