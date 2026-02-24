package com.furrow.app.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.BorderVisible
import com.furrow.app.ui.theme.AppRadius
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.Graphite
import com.furrow.app.ui.theme.Obsidian
import com.furrow.app.ui.theme.Slate
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NumberStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    minValue: Int = 0,
    maxValue: Int = 99,
    modifier: Modifier = Modifier,
    label: String? = null,
    accentColor: Color = GardenGlow,
) {
    val view = LocalView.current

    var isDecrementing by remember { mutableStateOf(false) }
    var isIncrementing by remember { mutableStateOf(false) }

    LaunchedEffect(isDecrementing) {
        if (isDecrementing) {
            delay(400)
            while (isDecrementing) {
                val newVal = (value - 1).coerceAtLeast(minValue)
                if (newVal != value) {
                    onValueChange(newVal)
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
                delay(120)
            }
        }
    }

    LaunchedEffect(isIncrementing) {
        if (isIncrementing) {
            delay(400)
            while (isIncrementing) {
                val newVal = (value + 1).coerceAtMost(maxValue)
                if (newVal != value) {
                    onValueChange(newVal)
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
                delay(120)
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        label?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = TextTertiary,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(
                onClick = {
                    val newVal = (value - 1).coerceAtLeast(minValue)
                    if (newVal != value) {
                        onValueChange(newVal)
                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    }
                },
                modifier = Modifier.size(48.dp),
                enabled = value > minValue,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Charcoal,
                    contentColor = TextPrimary,
                    disabledContentColor = TextTertiary,
                ),
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text(
                text = "$value",
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 36.sp),
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(80.dp),
            )
            IconButton(
                onClick = {
                    val newVal = (value + 1).coerceAtMost(maxValue)
                    if (newVal != value) {
                        onValueChange(newVal)
                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    }
                },
                modifier = Modifier.size(48.dp),
                enabled = value < maxValue,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Charcoal,
                    contentColor = TextPrimary,
                    disabledContentColor = TextTertiary,
                ),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }
    }
}

@Composable
fun ToggleRow(
    label: String,
    checked: Boolean,
    accentColor: Color = GardenGlow,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.3f),
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = TextTertiary,
                uncheckedTrackColor = BorderSubtle,
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    itemName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Obsidian,
        shape = RoundedCornerShape(AppRadius.input),
        title = {
            Text(
                "Delete $itemName?",
                color = TextPrimary,
            )
        },
        text = {
            Text(
                "This action cannot be undone.",
                color = TextSecondary,
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = StatusBad)
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FurrowBottomSheet(
    onDismiss: () -> Unit,
    title: String,
    content: @Composable ColumnScope.() -> Unit,
    confirmText: String = "Save",
    confirmEnabled: Boolean = true,
    glowColor: Color = GardenGlow,
    onConfirm: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Obsidian,
        shape = RoundedCornerShape(topStart = AppRadius.sheet, topEnd = AppRadius.sheet),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = AppSpacing.xs, bottom = AppSpacing.xs)
                    .size(width = 40.dp, height = 4.dp)
                    .background(BorderVisible, RoundedCornerShape(AppRadius.sm)),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.md),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = AppSpacing.md),
            )
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
                        content = content,
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppSpacing.md))
            HorizontalDivider(color = BorderSubtle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppSpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                SecondaryButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
                PrimaryButton(
                    text = confirmText,
                    onClick = onConfirm,
                    enabled = confirmEnabled,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

data class ExtraAction(
    val label: String,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemActionSheet(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    extraActions: List<ExtraAction> = emptyList(),
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Obsidian,
        shape = RoundedCornerShape(topStart = AppRadius.sheet, topEnd = AppRadius.sheet),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = AppSpacing.xs, bottom = AppSpacing.xs)
                    .size(width = 40.dp, height = 4.dp)
                    .background(BorderVisible, RoundedCornerShape(AppRadius.sm)),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppSpacing.lg),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEdit()
                        onDismiss()
                    }
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = TextPrimary,
                )
                Text(
                    "Edit",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                )
            }
            HorizontalDivider(color = BorderSubtle)
            extraActions.forEach { action ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            action.onClick()
                            onDismiss()
                        }
                        .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    action.icon()
                    Text(
                        action.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                    )
                }
                HorizontalDivider(color = BorderSubtle)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDelete()
                        onDismiss()
                    }
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = StatusBad,
                )
                Text(
                    "Delete",
                    style = MaterialTheme.typography.bodyLarge,
                    color = StatusBad,
                )
            }
        }
    }
}

private val dateFieldFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFieldWithToggle(
    label: String,
    dateMillis: Long,
    onDateChange: (Long) -> Unit,
    useTodayDefault: Boolean = true,
    accentColor: Color = GardenGlow,
    zone: ZoneId = ZoneId.systemDefault(),
) {
    var useToday by remember { mutableStateOf(useTodayDefault) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(useToday) {
        if (useToday) {
            val todayMillis = LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli()
            onDateChange(todayMillis)
        }
    }

    val dateText = Instant.ofEpochMilli(dateMillis)
        .atZone(zone)
        .toLocalDate()
        .format(dateFieldFormatter)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = useToday,
                onCheckedChange = { checked ->
                    useToday = checked
                    if (!checked) showDatePicker = true
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = accentColor,
                    uncheckedColor = TextTertiary,
                    checkmarkColor = Void,
                ),
            )
            Text(
                "Use today's date",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (!useToday) Modifier.clickable { showDatePicker = true }
                    else Modifier
                )
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextTertiary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodyLarge,
                color = if (useToday) TextSecondary else accentColor,
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { utcMillis ->
                        // DatePicker returns UTC midnight — convert to same date at midnight in user's zone
                        val localDate = Instant.ofEpochMilli(utcMillis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        val corrected = localDate.atStartOfDay(zone).toInstant().toEpochMilli()
                        onDateChange(corrected)
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = accentColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
