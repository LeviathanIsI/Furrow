package com.furrow.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.furrow.app.ui.theme.AppRadius
import androidx.compose.ui.unit.dp
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableSelector(
    query: String,
    onQueryChange: (String) -> Unit,
    items: List<T>,
    onItemSelected: (T) -> Unit,
    label: String,
    nameSelector: (T) -> String,
    modifier: Modifier = Modifier,
    accentColor: Color = GardenGlow,
    isCustom: (T) -> Boolean = { false },
    onAddCustom: ((String) -> Unit)? = null,
    onEditCustom: ((T) -> Unit)? = null,
    onDeleteCustom: ((T) -> Unit)? = null,
    itemContent: @Composable (T) -> Unit = { item ->
        Text(nameSelector(item), style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
    },
) {
    var expanded by remember { mutableStateOf(false) }

    val hasExactMatch = items.any { nameSelector(it).equals(query, ignoreCase = true) }
    val showAddCustom = onAddCustom != null && query.isNotBlank() && !hasExactMatch

    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Charcoal,
        focusedContainerColor = Charcoal,
        unfocusedBorderColor = BorderSubtle,
        focusedBorderColor = accentColor,
        unfocusedLabelColor = TextTertiary,
        focusedLabelColor = accentColor,
        cursorColor = accentColor,
        unfocusedTextColor = TextPrimary,
        focusedTextColor = TextPrimary,
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                expanded = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable, true),
            label = { Text(label) },
            placeholder = { Text("Search...") },
            singleLine = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = fieldColors,
            shape = RoundedCornerShape(AppRadius.input),
        )
        ExposedDropdownMenu(
            expanded = expanded && (items.isNotEmpty() || showAddCustom),
            onDismissRequest = { expanded = false },
        ) {
            items.take(8).forEach { item ->
                val custom = isCustom(item)
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(modifier = Modifier.weight(1f)) {
                                itemContent(item)
                            }
                            if (custom) {
                                Row {
                                    if (onEditCustom != null) {
                                        IconButton(
                                            onClick = {
                                                onEditCustom(item)
                                                expanded = false
                                            },
                                            modifier = Modifier.size(32.dp),
                                        ) {
                                            Icon(
                                                Icons.Filled.Edit,
                                                contentDescription = "Edit",
                                                modifier = Modifier.size(16.dp),
                                                tint = TextSecondary,
                                            )
                                        }
                                    }
                                    if (onDeleteCustom != null) {
                                        IconButton(
                                            onClick = {
                                                onDeleteCustom(item)
                                                expanded = false
                                            },
                                            modifier = Modifier.size(32.dp),
                                        ) {
                                            Icon(
                                                Icons.Filled.Delete,
                                                contentDescription = "Delete",
                                                modifier = Modifier.size(16.dp),
                                                tint = StatusBad,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                )
            }
            if (showAddCustom) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                "Add \"$query\" as custom",
                                style = MaterialTheme.typography.bodyMedium,
                                color = accentColor,
                            )
                        }
                    },
                    onClick = {
                        onAddCustom?.invoke(query)
                        expanded = false
                    },
                )
            }
        }
    }
}
