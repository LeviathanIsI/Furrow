package com.furrow.app.ui.preservation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.CanningBatch
import com.furrow.app.data.local.entity.DehydratingBatch
import com.furrow.app.data.local.entity.FermentingBatch
import com.furrow.app.data.local.entity.FreezingBatch
import com.furrow.app.data.local.entity.SmokingCuringBatch
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.SearchField
import com.furrow.app.ui.components.StatusPill
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.PreservationGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.Void
import com.furrow.app.util.DateUtil
import com.furrow.app.util.TimerUtil
import com.furrow.app.util.displayFormat

private enum class PreservationTab(val label: String, val type: String) {
    CANNING("Canning", "canning"),
    DEHYDRATING("Dehydrating", "dehydrating"),
    FERMENTING("Fermenting", "fermenting"),
    FREEZING("Freezing", "freezing"),
    SMOKING("Smoking", "smoking"),
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PreservationListScreen(
    onNavigateToPantry: () -> Unit,
    onAddBatch: (String) -> Unit,
    onEditBatch: (String, Long) -> Unit,
    viewModel: PreservationViewModel = hiltViewModel(),
) {
    val canningBatches by viewModel.canningBatches.collectAsState()
    val dehydratingBatches by viewModel.dehydratingBatches.collectAsState()
    val fermentingBatches by viewModel.fermentingBatches.collectAsState()
    val freezingBatches by viewModel.freezingBatches.collectAsState()
    val smokingBatches by viewModel.smokingCuringBatches.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = PreservationTab.entries
    var searchQuery by remember { mutableStateOf("") }

    val totalItemCount = canningBatches.orEmpty().size + dehydratingBatches.size +
        fermentingBatches.size + freezingBatches.size + smokingBatches.size

    val filteredCanning = remember(canningBatches, searchQuery) {
        val list = canningBatches.orEmpty()
        if (searchQuery.isBlank()) list
        else list.filter { it.recipeName.contains(searchQuery, ignoreCase = true) }
    }
    val filteredDehydrating = remember(dehydratingBatches, searchQuery) {
        if (searchQuery.isBlank()) dehydratingBatches
        else dehydratingBatches.filter { it.product.contains(searchQuery, ignoreCase = true) }
    }
    val filteredFermenting = remember(fermentingBatches, searchQuery) {
        if (searchQuery.isBlank()) fermentingBatches
        else fermentingBatches.filter { it.product.contains(searchQuery, ignoreCase = true) }
    }
    val filteredFreezing = remember(freezingBatches, searchQuery) {
        if (searchQuery.isBlank()) freezingBatches
        else freezingBatches.filter { it.item.contains(searchQuery, ignoreCase = true) }
    }
    val filteredSmoking = remember(smokingBatches, searchQuery) {
        if (searchQuery.isBlank()) smokingBatches
        else smokingBatches.filter {
            it.meatType.contains(searchQuery, ignoreCase = true) ||
                (it.cut ?: "").contains(searchQuery, ignoreCase = true)
        }
    }

    // Canning action sheet state
    var canningForAction by remember { mutableStateOf<CanningBatch?>(null) }
    var canningToDelete by remember { mutableStateOf<CanningBatch?>(null) }

    // Dehydrating action sheet state
    var dehydratingForAction by remember { mutableStateOf<DehydratingBatch?>(null) }
    var dehydratingToDelete by remember { mutableStateOf<DehydratingBatch?>(null) }

    // Fermenting action sheet state
    var fermentingForAction by remember { mutableStateOf<FermentingBatch?>(null) }
    var fermentingToDelete by remember { mutableStateOf<FermentingBatch?>(null) }

    // Freezing action sheet state
    var freezingForAction by remember { mutableStateOf<FreezingBatch?>(null) }
    var freezingToDelete by remember { mutableStateOf<FreezingBatch?>(null) }

    // Smoking action sheet state
    var smokingForAction by remember { mutableStateOf<SmokingCuringBatch?>(null) }
    var smokingToDelete by remember { mutableStateOf<SmokingCuringBatch?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(
                title = "Preservation",
                actions = {
                    IconButton(onClick = onNavigateToPantry) {
                        Icon(
                            Icons.Outlined.Kitchen,
                            contentDescription = "Pantry",
                            tint = TextSecondary,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddBatch(tabs[selectedTab].type) },
                containerColor = PreservationGlow,
                contentColor = Void,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add batch")
            }
        },
    ) { padding ->
        if (canningBatches == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@AppScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Charcoal,
                contentColor = TextPrimary,
                edgePadding = 16.dp,
                divider = { HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle) },
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = PreservationGlow,
                        )
                    }
                },
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                tab.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 11.sp,
                                ),
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selectedTab == index) PreservationGlow else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
            }

            if (totalItemCount > 5) {
                SearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    accentColor = PreservationGlow,
                    modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                )
            } else {
                Spacer(modifier = Modifier.height(AppSpacing.sm))
            }

            when (tabs[selectedTab]) {
                PreservationTab.CANNING -> {
                    if (filteredCanning.isEmpty()) {
                        Box(modifier = Modifier.weight(1f)) {
                            EmptyState(
                                title = if (searchQuery.isNotBlank()) "No canning batches match \"$searchQuery\"" else "No canning batches",
                                subtitle = if (searchQuery.isNotBlank()) "Try a different search term." else "Add your first canning batch to start tracking.",
                                icon = {
                                    Icon(
                                        Icons.Outlined.Kitchen,
                                        contentDescription = "Preservation",
                                        modifier = Modifier.size(28.dp),
                                        tint = PreservationGlow,
                                    )
                                },
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                start = AppSpacing.md,
                                end = AppSpacing.md,
                                bottom = AppSpacing.bottomListPadding,
                            ),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        ) {
                            item(key = "canning_panel") {
                                Panel(contentPadding = PaddingValues(0.dp)) {
                                    filteredCanning.forEachIndexed { index, batch ->
                                        val subtitle = listOfNotNull(
                                            batch.method?.displayFormat(),
                                            batch.jarSize,
                                            batch.jarCount?.let { "${it} jars" },
                                        ).joinToString(" \u2022 ")
                                        ListRow(
                                            title = batch.recipeName,
                                            subtitle = subtitle.ifEmpty { null },
                                            metadata = DateUtil.formatDate(batch.dateProcessed, viewModel.zone),
                                            modifier = Modifier.combinedClickable(
                                                onClick = { onEditBatch("canning", batch.id) },
                                                onLongClick = { canningForAction = batch },
                                            ),
                                            showDivider = index != filteredCanning.lastIndex,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                PreservationTab.DEHYDRATING -> {
                    if (filteredDehydrating.isEmpty()) {
                        Box(modifier = Modifier.weight(1f)) {
                            EmptyState(
                                title = if (searchQuery.isNotBlank()) "No dehydrating batches match \"$searchQuery\"" else "No dehydrating batches",
                                subtitle = if (searchQuery.isNotBlank()) "Try a different search term." else "Add your first dehydrating batch to start tracking.",
                                icon = {
                                    Icon(
                                        Icons.Outlined.Kitchen,
                                        contentDescription = "Preservation",
                                        modifier = Modifier.size(28.dp),
                                        tint = PreservationGlow,
                                    )
                                },
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                start = AppSpacing.md,
                                end = AppSpacing.md,
                                bottom = AppSpacing.bottomListPadding,
                            ),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        ) {
                            item(key = "dehydrating_panel") {
                                Panel(contentPadding = PaddingValues(0.dp)) {
                                    filteredDehydrating.forEachIndexed { index, batch ->
                                        val beforeStr = batch.weightBeforeLbs?.let { "%.1f".format(it) } ?: "?"
                                        val afterStr = batch.weightAfterLbs?.let { "%.1f".format(it) } ?: "?"
                                        val timerText = TimerUtil.dehydrateHoursLabel(batch.date)
                                        val subtitle = "${beforeStr}\u2192${afterStr} lbs \u2022 $timerText"
                                        ListRow(
                                            title = batch.product,
                                            subtitle = subtitle,
                                            metadata = DateUtil.formatDate(batch.date, viewModel.zone),
                                            modifier = Modifier.combinedClickable(
                                                onClick = { onEditBatch("dehydrating", batch.id) },
                                                onLongClick = { dehydratingForAction = batch },
                                            ),
                                            showDivider = index != filteredDehydrating.lastIndex,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                PreservationTab.FERMENTING -> {
                    if (filteredFermenting.isEmpty()) {
                        Box(modifier = Modifier.weight(1f)) {
                            EmptyState(
                                title = if (searchQuery.isNotBlank()) "No fermenting batches match \"$searchQuery\"" else "No fermenting batches",
                                subtitle = if (searchQuery.isNotBlank()) "Try a different search term." else "Add your first fermenting batch to start tracking.",
                                icon = {
                                    Icon(
                                        Icons.Outlined.Kitchen,
                                        contentDescription = "Preservation",
                                        modifier = Modifier.size(28.dp),
                                        tint = PreservationGlow,
                                    )
                                },
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                start = AppSpacing.md,
                                end = AppSpacing.md,
                                bottom = AppSpacing.bottomListPadding,
                            ),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        ) {
                            item(key = "fermenting_panel") {
                                Panel(contentPadding = PaddingValues(0.dp)) {
                                    filteredFermenting.forEachIndexed { index, batch ->
                                        val parts = listOfNotNull(
                                            batch.method?.displayFormat(),
                                            batch.vesselType?.displayFormat(),
                                        )
                                        val timerText = if (batch.endDate == null) {
                                            TimerUtil.fermentDayLabel(batch.startDate, viewModel.zone)
                                        } else null
                                        val subtitle = (parts + listOfNotNull(timerText)).joinToString(" \u2022 ")
                                        ListRow(
                                            title = batch.product,
                                            subtitle = subtitle.ifEmpty { null },
                                            metadata = DateUtil.formatDate(batch.startDate, viewModel.zone),
                                            trailing = if (batch.endDate == null) {
                                                {
                                                    StatusPill(text = "Active", active = true)
                                                }
                                            } else {
                                                null
                                            },
                                            modifier = Modifier.combinedClickable(
                                                onClick = { onEditBatch("fermenting", batch.id) },
                                                onLongClick = { fermentingForAction = batch },
                                            ),
                                            showDivider = index != filteredFermenting.lastIndex,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                PreservationTab.FREEZING -> {
                    if (filteredFreezing.isEmpty()) {
                        Box(modifier = Modifier.weight(1f)) {
                            EmptyState(
                                title = if (searchQuery.isNotBlank()) "No freezing batches match \"$searchQuery\"" else "No freezing batches",
                                subtitle = if (searchQuery.isNotBlank()) "Try a different search term." else "Add your first freezing batch to start tracking.",
                                icon = {
                                    Icon(
                                        Icons.Outlined.Kitchen,
                                        contentDescription = "Preservation",
                                        modifier = Modifier.size(28.dp),
                                        tint = PreservationGlow,
                                    )
                                },
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                start = AppSpacing.md,
                                end = AppSpacing.md,
                                bottom = AppSpacing.bottomListPadding,
                            ),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        ) {
                            item(key = "freezing_panel") {
                                Panel(contentPadding = PaddingValues(0.dp)) {
                                    filteredFreezing.forEachIndexed { index, batch ->
                                        val qtyStr = batch.quantityLbs?.let { "%.1f lbs".format(it) }
                                        val subtitle = listOfNotNull(
                                            qtyStr,
                                            batch.packagingMethod?.displayFormat(),
                                        ).joinToString(" \u2022 ")
                                        ListRow(
                                            title = batch.item,
                                            subtitle = subtitle.ifEmpty { null },
                                            metadata = DateUtil.formatDate(batch.dateFrozen, viewModel.zone),
                                            modifier = Modifier.combinedClickable(
                                                onClick = { onEditBatch("freezing", batch.id) },
                                                onLongClick = { freezingForAction = batch },
                                            ),
                                            showDivider = index != filteredFreezing.lastIndex,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                PreservationTab.SMOKING -> {
                    if (filteredSmoking.isEmpty()) {
                        Box(modifier = Modifier.weight(1f)) {
                            EmptyState(
                                title = if (searchQuery.isNotBlank()) "No smoking batches match \"$searchQuery\"" else "No smoking batches",
                                subtitle = if (searchQuery.isNotBlank()) "Try a different search term." else "Add your first smoking/curing batch to start tracking.",
                                icon = {
                                    Icon(
                                        Icons.Outlined.Kitchen,
                                        contentDescription = "Preservation",
                                        modifier = Modifier.size(28.dp),
                                        tint = PreservationGlow,
                                    )
                                },
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                start = AppSpacing.md,
                                end = AppSpacing.md,
                                bottom = AppSpacing.bottomListPadding,
                            ),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        ) {
                            item(key = "smoking_panel") {
                                Panel(contentPadding = PaddingValues(0.dp)) {
                                    filteredSmoking.forEachIndexed { index, batch ->
                                        val parts = listOfNotNull(
                                            batch.cut?.displayFormat(),
                                            batch.cureType?.displayFormat(),
                                        )
                                        val timerText = if (batch.cureEnd == null) {
                                            TimerUtil.cureDayLabel(batch.cureStart, batch.cureType, viewModel.zone)
                                        } else null
                                        val subtitle = (parts + listOfNotNull(timerText)).joinToString(" \u2022 ")
                                        ListRow(
                                            title = batch.meatType,
                                            subtitle = subtitle.ifEmpty { null },
                                            metadata = DateUtil.formatDate(batch.cureStart, viewModel.zone),
                                            modifier = Modifier.combinedClickable(
                                                onClick = { onEditBatch("smoking", batch.id) },
                                                onLongClick = { smokingForAction = batch },
                                            ),
                                            showDivider = index != filteredSmoking.lastIndex,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // -- Action Sheets --

    canningForAction?.let { batch ->
        ItemActionSheet(
            onDismiss = { canningForAction = null },
            onEdit = { onEditBatch("canning", batch.id) },
            onDelete = {
                canningToDelete = batch
                canningForAction = null
            },
        )
    }

    dehydratingForAction?.let { batch ->
        ItemActionSheet(
            onDismiss = { dehydratingForAction = null },
            onEdit = { onEditBatch("dehydrating", batch.id) },
            onDelete = {
                dehydratingToDelete = batch
                dehydratingForAction = null
            },
        )
    }

    fermentingForAction?.let { batch ->
        ItemActionSheet(
            onDismiss = { fermentingForAction = null },
            onEdit = { onEditBatch("fermenting", batch.id) },
            onDelete = {
                fermentingToDelete = batch
                fermentingForAction = null
            },
        )
    }

    freezingForAction?.let { batch ->
        ItemActionSheet(
            onDismiss = { freezingForAction = null },
            onEdit = { onEditBatch("freezing", batch.id) },
            onDelete = {
                freezingToDelete = batch
                freezingForAction = null
            },
        )
    }

    smokingForAction?.let { batch ->
        ItemActionSheet(
            onDismiss = { smokingForAction = null },
            onEdit = { onEditBatch("smoking", batch.id) },
            onDelete = {
                smokingToDelete = batch
                smokingForAction = null
            },
        )
    }

    // -- Delete Confirmation Dialogs --

    canningToDelete?.let { batch ->
        DeleteConfirmationDialog(
            itemName = batch.recipeName,
            onConfirm = {
                viewModel.deleteCanningBatch(batch)
                canningToDelete = null
            },
            onDismiss = { canningToDelete = null },
        )
    }

    dehydratingToDelete?.let { batch ->
        DeleteConfirmationDialog(
            itemName = batch.product,
            onConfirm = {
                viewModel.deleteDehydratingBatch(batch)
                dehydratingToDelete = null
            },
            onDismiss = { dehydratingToDelete = null },
        )
    }

    fermentingToDelete?.let { batch ->
        DeleteConfirmationDialog(
            itemName = batch.product,
            onConfirm = {
                viewModel.deleteFermentingBatch(batch)
                fermentingToDelete = null
            },
            onDismiss = { fermentingToDelete = null },
        )
    }

    freezingToDelete?.let { batch ->
        DeleteConfirmationDialog(
            itemName = batch.item,
            onConfirm = {
                viewModel.deleteFreezingBatch(batch)
                freezingToDelete = null
            },
            onDismiss = { freezingToDelete = null },
        )
    }

    smokingToDelete?.let { batch ->
        DeleteConfirmationDialog(
            itemName = batch.meatType,
            onConfirm = {
                viewModel.deleteSmokingCuringBatch(batch)
                smokingToDelete = null
            },
            onDismiss = { smokingToDelete = null },
        )
    }
}
