package com.furrow.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.util.displayFormat

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    accentColor: Color = GardenGlow,
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }
    val focusRequester = remember { FocusRequester() }

    val displayValue = options.firstOrNull { it.equals(selected, ignoreCase = true) }
        ?: selected.displayFormat()

    val filteredOptions = if (searchText.isBlank()) {
        options
    } else {
        options.filter { it.contains(searchText, ignoreCase = true) }
    }

    // Does the typed text exactly match an existing option?
    val exactMatch = options.any { it.equals(searchText, ignoreCase = true) }

    Column {
        // DISPLAY FIELD — read-only, tapping opens the menu
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = "Toggle dropdown",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .onGloballyPositioned { coordinates ->
                    fieldSize = coordinates.size
                },
            colors = AppTextFieldDefaults.colors(accentColor = accentColor, bordered = true),
            shape = RoundedCornerShape(8.dp),
        )

        // DROPDOWN MENU — contains search field + options
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                searchText = ""
            },
            modifier = Modifier
                .width(with(LocalDensity.current) { fieldSize.width.toDp() })
                .heightIn(max = 300.dp),
        ) {
            // Search / custom input field pinned at top
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search or type custom...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .focusRequester(focusRequester),
            )

            // Auto-focus the search field when menu opens
            LaunchedEffect(expanded) {
                if (expanded) {
                    focusRequester.requestFocus()
                }
            }

            // Option items
            filteredOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                        searchText = ""
                    },
                )
            }

            // "Use custom value" option — only show if user typed something
            // that doesn't exactly match an existing option
            if (searchText.isNotBlank() && !exactMatch) {
                DropdownMenuItem(
                    text = { Text("Use \"$searchText\"") },
                    onClick = {
                        onSelect(searchText)
                        expanded = false
                        searchText = ""
                    },
                )
            }
        }
    }
}
