package com.furrow.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }

    val displayValue = options.firstOrNull { it.equals(selected, ignoreCase = true) }
        ?: selected.displayFormat()

    Column {
        Box {
            OutlinedTextField(
                value = displayValue,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(label) },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp
                            else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { fieldSize = it.size },
                colors = AppTextFieldDefaults.colors(accentColor = accentColor, bordered = true),
                shape = RoundedCornerShape(8.dp),
            )
            // Transparent overlay that catches taps anywhere on the field
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { expanded = !expanded },
            )
        }

        // Plain DropdownMenu — does NOT filter items against the text field value
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { fieldSize.width.toDp() })
                .heightIn(max = 300.dp),
        ) {
            if (options.size > 8) {
                var searchText by remember { mutableStateOf("") }
                val filtered = if (searchText.isBlank()) options
                else options.filter { it.contains(searchText, ignoreCase = true) }

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    singleLine = true,
                    colors = AppTextFieldDefaults.colors(accentColor = accentColor),
                )

                filtered.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            } else {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
