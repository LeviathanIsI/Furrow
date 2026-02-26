package com.furrow.app.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.Graphite
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PlantingCalendarScreen(
    onBack: () -> Unit,
    onBedClick: (Long) -> Unit,
    viewModel: PlantingCalendarViewModel = hiltViewModel(),
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now(viewModel.zone)) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val eventsFlow = remember(currentMonth) { viewModel.getEventsForMonth(currentMonth) }
    val events by eventsFlow.collectAsState()
    val activeWindows by viewModel.activeWindowsThisMonth.collectAsState()
    val upcomingHarvests by viewModel.upcomingHarvests.collectAsState()

    val today = LocalDate.now(viewModel.zone)

    val eventsByDate = remember(events) {
        events.groupBy { it.date }
    }

    val selectedDayEvents = selectedDate?.let { eventsByDate[it] } ?: emptyList()

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Planting Calendar",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                end = AppSpacing.md,
                top = AppSpacing.sm,
                bottom = AppSpacing.bottomListPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            // Month navigation
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous month",
                            tint = TextPrimary,
                        )
                    }
                    Text(
                        text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next month",
                            tint = TextPrimary,
                        )
                    }
                }
            }

            // Calendar grid
            item {
                MonthGrid(
                    yearMonth = currentMonth,
                    today = today,
                    selectedDate = selectedDate,
                    eventsByDate = eventsByDate,
                    onDateClick = { selectedDate = it },
                )
            }

            // Selected day detail
            if (selectedDayEvents.isNotEmpty()) {
                item {
                    Panel {
                        Text(
                            "Events on ${selectedDate?.dayOfMonth} ${selectedDate?.month?.getDisplayName(TextStyle.SHORT, Locale.getDefault())}",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.xs))
                        selectedDayEvents.forEach { event ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                                modifier = Modifier.padding(vertical = 2.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(eventTypeColor(event.type)),
                                )
                                Text(
                                    "${event.type.name.lowercase().replaceFirstChar { it.uppercase() }}: ${event.label}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                        }
                    }
                }
            }

            // This month's planting windows
            if (activeWindows.isNotEmpty()) {
                item {
                    Text(
                        "This Month",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                }
                items(activeWindows) { window ->
                    Panel {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    window.plantName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                )
                                Text(
                                    window.method,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextTertiary,
                                )
                            }
                            if (window.isPlanted) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = StatusGood,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Text(
                                        "Planted",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StatusGood,
                                    )
                                }
                            } else {
                                Text(
                                    "Not planted",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary,
                                )
                            }
                        }
                    }
                }
            }

            // Upcoming harvests
            if (upcomingHarvests.isNotEmpty()) {
                item {
                    Text(
                        "Upcoming Harvests",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                }
                items(upcomingHarvests) { item ->
                    val urgencyColor = when {
                        item.daysUntil <= 0 -> StatusBad
                        item.daysUntil <= 7 -> StatusWarn
                        else -> TextTertiary
                    }
                    Panel(
                        modifier = Modifier.clickable { onBedClick(item.bedId) },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${item.plantName}${item.variety?.let { " ($it)" } ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    item.bedName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextTertiary,
                                )
                            }
                            Text(
                                text = when {
                                    item.daysUntil < 0 -> "${-item.daysUntil}d overdue"
                                    item.daysUntil == 0L -> "Today"
                                    else -> "${item.daysUntil}d"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = urgencyColor,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthGrid(
    yearMonth: YearMonth,
    today: LocalDate,
    selectedDate: LocalDate?,
    eventsByDate: Map<LocalDate, List<CalendarEvent>>,
    onDateClick: (LocalDate) -> Unit,
) {
    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
    val firstDay = yearMonth.atDay(1)
    val firstDayOffset = (firstDay.dayOfWeek.value % 7) // Sunday=0
    val daysInMonth = yearMonth.lengthOfMonth()

    Column {
        // Day headers
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                )
            }
        }
        Spacer(modifier = Modifier.height(AppSpacing.xxs))

        // Calendar cells
        val totalCells = firstDayOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - firstDayOffset + 1
                    if (dayNum in 1..daysInMonth) {
                        val date = yearMonth.atDay(dayNum)
                        val isToday = date == today
                        val isSelected = date == selectedDate
                        val dayEvents = eventsByDate[date] ?: emptyList()
                        val eventTypes = dayEvents.map { it.type }.toSet()

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(1.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when {
                                        isSelected -> GardenGlow.copy(alpha = 0.2f)
                                        isToday -> Graphite
                                        else -> Charcoal
                                    }
                                )
                                .clickable { onDateClick(date) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = "$dayNum",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isToday) GardenGlow else TextPrimary,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                )
                                if (eventTypes.isNotEmpty()) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    ) {
                                        eventTypes.take(3).forEach { type ->
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(eventTypeColor(type)),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

private fun eventTypeColor(type: CalendarEventType) = when (type) {
    CalendarEventType.PLANTED -> StatusGood
    CalendarEventType.HARVEST -> StatusWarn
    CalendarEventType.WATERED -> GardenGlow
    CalendarEventType.FERTILIZED -> StatusBad
}
