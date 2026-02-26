package com.furrow.app.ui.land

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.CompostBin
import com.furrow.app.data.local.entity.Fence
import com.furrow.app.data.local.entity.Paddock
import com.furrow.app.data.local.entity.Property
import com.furrow.app.data.local.entity.Structure
import com.furrow.app.data.local.entity.WaterSource
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.SearchField
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.LandGlow
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.Void

private sealed class SelectedItem(val type: String, val id: Long, val displayName: String) {
    class PropertyItem(val property: Property) :
        SelectedItem("property", property.id, property.name)

    class StructureItem(val structure: Structure) :
        SelectedItem("structure", structure.id, structure.name)

    class FenceItem(val fence: Fence) :
        SelectedItem("fence", fence.id, fence.type)

    class PaddockItem(val paddock: Paddock) :
        SelectedItem("paddock", paddock.id, paddock.name)

    class WaterSourceItem(val waterSource: WaterSource) :
        SelectedItem("water_source", waterSource.id, waterSource.type)

    class CompostBinItem(val compostBin: CompostBin) :
        SelectedItem("compost_bin", compostBin.id, compostBin.type)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LandListScreen(
    onAddItem: (String) -> Unit,
    onEditItem: (String, Long) -> Unit,
    viewModel: LandViewModel = hiltViewModel(),
) {
    val propertiesOrNull by viewModel.properties.collectAsState()
    val totalAcreage by viewModel.totalAcreage.collectAsState()
    val properties = propertiesOrNull.orEmpty()
    val structures by viewModel.structures.collectAsState()
    val fences by viewModel.fences.collectAsState()
    val paddocks by viewModel.paddocks.collectAsState()
    val waterSources by viewModel.waterSources.collectAsState()
    val compostBins by viewModel.compostBins.collectAsState()

    var showAddMenu by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<SelectedItem?>(null) }
    var itemToDelete by remember { mutableStateOf<SelectedItem?>(null) }

    var propertiesExpanded by remember { mutableStateOf(true) }
    var structuresExpanded by remember { mutableStateOf(true) }
    var fencesExpanded by remember { mutableStateOf(true) }
    var paddocksExpanded by remember { mutableStateOf(true) }
    var waterSourcesExpanded by remember { mutableStateOf(true) }
    var compostExpanded by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    val allEmpty = properties.isEmpty() && structures.isEmpty() && fences.isEmpty() &&
        paddocks.isEmpty() && waterSources.isEmpty() && compostBins.isEmpty()

    val totalItemCount = properties.size + structures.size + fences.size +
        paddocks.size + waterSources.size + compostBins.size

    val filteredProperties = remember(properties, searchQuery) {
        if (searchQuery.isBlank()) properties
        else properties.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                (it.county ?: "").contains(searchQuery, ignoreCase = true) ||
                (it.zoning ?: "").contains(searchQuery, ignoreCase = true)
        }
    }
    val filteredStructures = remember(structures, searchQuery) {
        if (searchQuery.isBlank()) structures
        else structures.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                (it.type ?: "").contains(searchQuery, ignoreCase = true)
        }
    }
    val filteredFences = remember(fences, searchQuery) {
        if (searchQuery.isBlank()) fences
        else fences.filter {
            it.type.contains(searchQuery, ignoreCase = true)
        }
    }
    val filteredPaddocks = remember(paddocks, searchQuery) {
        if (searchQuery.isBlank()) paddocks
        else paddocks.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                (it.forageType ?: "").contains(searchQuery, ignoreCase = true)
        }
    }
    val filteredWaterSources = remember(waterSources, searchQuery) {
        if (searchQuery.isBlank()) waterSources
        else waterSources.filter {
            it.type.contains(searchQuery, ignoreCase = true) ||
                (it.pumpType ?: "").contains(searchQuery, ignoreCase = true)
        }
    }
    val filteredCompostBins = remember(compostBins, searchQuery) {
        if (searchQuery.isBlank()) compostBins
        else compostBins.filter {
            it.type.contains(searchQuery, ignoreCase = true) ||
                (it.location ?: "").contains(searchQuery, ignoreCase = true)
        }
    }
    val filteredEmpty = searchQuery.isNotBlank() && filteredProperties.isEmpty() &&
        filteredStructures.isEmpty() && filteredFences.isEmpty() &&
        filteredPaddocks.isEmpty() && filteredWaterSources.isEmpty() &&
        filteredCompostBins.isEmpty()

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(title = "Land Management")
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = { showAddMenu = true },
                    containerColor = LandGlow,
                    contentColor = Void,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                    )
                }
                DropdownMenu(
                    expanded = showAddMenu,
                    onDismissRequest = { showAddMenu = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Property") },
                        onClick = { showAddMenu = false; onAddItem("property") },
                    )
                    DropdownMenuItem(
                        text = { Text("Structure") },
                        onClick = { showAddMenu = false; onAddItem("structure") },
                    )
                    DropdownMenuItem(
                        text = { Text("Fence") },
                        onClick = { showAddMenu = false; onAddItem("fence") },
                    )
                    DropdownMenuItem(
                        text = { Text("Paddock") },
                        onClick = { showAddMenu = false; onAddItem("paddock") },
                    )
                    DropdownMenuItem(
                        text = { Text("Water Source") },
                        onClick = { showAddMenu = false; onAddItem("water_source") },
                    )
                    DropdownMenuItem(
                        text = { Text("Compost Bin") },
                        onClick = { showAddMenu = false; onAddItem("compost_bin") },
                    )
                    DropdownMenuItem(
                        text = { Text("Soil Test") },
                        onClick = { showAddMenu = false; onAddItem("soil_test") },
                    )
                    DropdownMenuItem(
                        text = { Text("Weather Log") },
                        onClick = { showAddMenu = false; onAddItem("weather") },
                    )
                }
            }
        },
    ) { padding ->
        if (propertiesOrNull == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@AppScaffold
        }

        if (allEmpty) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                EmptyState(
                    title = "No land records yet",
                    subtitle = "Add properties, structures, fences, and more to manage your land.",
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Landscape,
                            contentDescription = "Property",
                            modifier = Modifier.size(28.dp),
                            tint = LandGlow,
                        )
                    },
                    actionLabel = "Add Property",
                    onAction = { onAddItem("property") },
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = AppSpacing.md,
                    end = AppSpacing.md,
                    top = AppSpacing.md,
                    bottom = AppSpacing.bottomListPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                // ── Summary Stats ────────────────────────────────────────
                item(key = "summary_stats") {
                    Panel {
                        InlineStat(
                            label = "Properties",
                            value = "${properties.size}",
                        )
                        InlineStat(
                            label = "Total Acreage",
                            value = "%.1f ac".format(totalAcreage),
                        )
                        InlineStat(
                            label = "Structures",
                            value = "${structures.size}",
                        )
                        InlineStat(
                            label = "Paddocks",
                            value = "${paddocks.size}",
                        )
                    }
                }

                if (totalItemCount > 5) {
                    item(key = "search") {
                        SearchField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            accentColor = LandGlow,
                        )
                    }
                }

                if (filteredEmpty) {
                    item(key = "empty_search") {
                        EmptyState(
                            title = "No items match \"$searchQuery\"",
                            subtitle = "Try a different search term.",
                        )
                    }
                }

                // ── Properties Section ───────────────────────────────────
                if (filteredProperties.isNotEmpty()) {
                    item(key = "properties_header") {
                        Panel(contentPadding = PaddingValues(0.dp)) {
                            ListRow(
                                title = "Properties",
                                subtitle = "${filteredProperties.size} total",
                                showDivider = propertiesExpanded && filteredProperties.isNotEmpty(),
                                onClick = { propertiesExpanded = !propertiesExpanded },
                            )
                            AnimatedVisibility(
                                visible = propertiesExpanded,
                                enter = expandVertically(),
                                exit = shrinkVertically(),
                            ) {
                                Column {
                                    filteredProperties.forEachIndexed { index, property ->
                                        val subtitle = listOfNotNull(
                                            property.county,
                                            property.zoning,
                                        ).joinToString(" \u2022 ")

                                        ListRow(
                                            title = property.name,
                                            subtitle = subtitle.ifEmpty { null },
                                            metadata = property.totalAcreage?.let { "%.1f ac".format(it) },
                                            showDivider = index != filteredProperties.lastIndex,
                                            modifier = Modifier.combinedClickable(
                                                onClick = { onEditItem("property", property.id) },
                                                onLongClick = { selectedItem = SelectedItem.PropertyItem(property) },
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Structures Section ───────────────────────────────────
                if (filteredStructures.isNotEmpty()) {
                    item(key = "structures_header") {
                        Panel(contentPadding = PaddingValues(0.dp)) {
                            ListRow(
                                title = "Structures",
                                subtitle = "${filteredStructures.size} total",
                                showDivider = structuresExpanded && filteredStructures.isNotEmpty(),
                                onClick = { structuresExpanded = !structuresExpanded },
                            )
                            AnimatedVisibility(
                                visible = structuresExpanded,
                                enter = expandVertically(),
                                exit = shrinkVertically(),
                            ) {
                                Column {
                                    filteredStructures.forEachIndexed { index, structure ->
                                        val subtitle = listOfNotNull(
                                            structure.type,
                                            structure.dimensions,
                                        ).joinToString(" \u2022 ")

                                        ListRow(
                                            title = structure.name,
                                            subtitle = subtitle.ifEmpty { null },
                                            metadata = structure.conditionRating?.let { "\u2605 $it" },
                                            showDivider = index != filteredStructures.lastIndex,
                                            modifier = Modifier.combinedClickable(
                                                onClick = { onEditItem("structure", structure.id) },
                                                onLongClick = { selectedItem = SelectedItem.StructureItem(structure) },
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Fences Section ───────────────────────────────────────
                if (filteredFences.isNotEmpty()) {
                    item(key = "fences_header") {
                        Panel(contentPadding = PaddingValues(0.dp)) {
                            ListRow(
                                title = "Fences",
                                subtitle = "${filteredFences.size} total",
                                showDivider = fencesExpanded && filteredFences.isNotEmpty(),
                                onClick = { fencesExpanded = !fencesExpanded },
                            )
                            AnimatedVisibility(
                                visible = fencesExpanded,
                                enter = expandVertically(),
                                exit = shrinkVertically(),
                            ) {
                                Column {
                                    filteredFences.forEachIndexed { index, fence ->
                                        val lengthStr = fence.lengthFt?.let { "%.0f".format(it) } ?: "?"
                                        val heightStr = fence.heightFt?.let { "%.0f".format(it) } ?: "?"
                                        val subtitle = "${lengthStr} ft \u00D7 ${heightStr} ft"

                                        ListRow(
                                            title = fence.type,
                                            subtitle = subtitle,
                                            metadata = fence.conditionRating?.let { "\u2605 $it" },
                                            showDivider = index != filteredFences.lastIndex,
                                            modifier = Modifier.combinedClickable(
                                                onClick = { onEditItem("fence", fence.id) },
                                                onLongClick = { selectedItem = SelectedItem.FenceItem(fence) },
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Paddocks Section ─────────────────────────────────────
                if (filteredPaddocks.isNotEmpty()) {
                    item(key = "paddocks_header") {
                        Panel(contentPadding = PaddingValues(0.dp)) {
                            ListRow(
                                title = "Paddocks",
                                subtitle = "${filteredPaddocks.size} total",
                                showDivider = paddocksExpanded && filteredPaddocks.isNotEmpty(),
                                onClick = { paddocksExpanded = !paddocksExpanded },
                            )
                            AnimatedVisibility(
                                visible = paddocksExpanded,
                                enter = expandVertically(),
                                exit = shrinkVertically(),
                            ) {
                                Column {
                                    filteredPaddocks.forEachIndexed { index, paddock ->
                                        val subtitle = listOfNotNull(
                                            paddock.acreage?.let { "%.1f ac".format(it) },
                                            paddock.forageType,
                                        ).joinToString(" \u2022 ")

                                        ListRow(
                                            title = paddock.name,
                                            subtitle = subtitle.ifEmpty { null },
                                            showDivider = index != filteredPaddocks.lastIndex,
                                            modifier = Modifier.combinedClickable(
                                                onClick = { onEditItem("paddock", paddock.id) },
                                                onLongClick = { selectedItem = SelectedItem.PaddockItem(paddock) },
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Water Sources Section ────────────────────────────────
                if (filteredWaterSources.isNotEmpty()) {
                    item(key = "water_sources_header") {
                        Panel(contentPadding = PaddingValues(0.dp)) {
                            ListRow(
                                title = "Water Sources",
                                subtitle = "${filteredWaterSources.size} total",
                                showDivider = waterSourcesExpanded && filteredWaterSources.isNotEmpty(),
                                onClick = { waterSourcesExpanded = !waterSourcesExpanded },
                            )
                            AnimatedVisibility(
                                visible = waterSourcesExpanded,
                                enter = expandVertically(),
                                exit = shrinkVertically(),
                            ) {
                                Column {
                                    filteredWaterSources.forEachIndexed { index, ws ->
                                        val subtitle = ws.capacityGal?.let { "%.0f gal".format(it) }

                                        ListRow(
                                            title = ws.type,
                                            subtitle = subtitle,
                                            metadata = ws.pumpType,
                                            showDivider = index != filteredWaterSources.lastIndex,
                                            modifier = Modifier.combinedClickable(
                                                onClick = { onEditItem("water_source", ws.id) },
                                                onLongClick = { selectedItem = SelectedItem.WaterSourceItem(ws) },
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Compost Section ──────────────────────────────────────
                if (filteredCompostBins.isNotEmpty()) {
                    item(key = "compost_header") {
                        Panel(contentPadding = PaddingValues(0.dp)) {
                            ListRow(
                                title = "Compost",
                                subtitle = "${filteredCompostBins.size} total",
                                showDivider = compostExpanded && filteredCompostBins.isNotEmpty(),
                                onClick = { compostExpanded = !compostExpanded },
                            )
                            AnimatedVisibility(
                                visible = compostExpanded,
                                enter = expandVertically(),
                                exit = shrinkVertically(),
                            ) {
                                Column {
                                    filteredCompostBins.forEachIndexed { index, bin ->
                                        val subtitle = listOfNotNull(
                                            bin.location,
                                            bin.maturityStage,
                                        ).joinToString(" \u2022 ")

                                        ListRow(
                                            title = bin.type,
                                            subtitle = subtitle.ifEmpty { null },
                                            showDivider = index != filteredCompostBins.lastIndex,
                                            modifier = Modifier.combinedClickable(
                                                onClick = { onEditItem("compost_bin", bin.id) },
                                                onLongClick = { selectedItem = SelectedItem.CompostBinItem(bin) },
                                            ),
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

    // ── Action Sheet ─────────────────────────────────────────────────────────
    selectedItem?.let { item ->
        ItemActionSheet(
            onDismiss = { selectedItem = null },
            onEdit = { onEditItem(item.type, item.id) },
            onDelete = {
                itemToDelete = item
                selectedItem = null
            },
        )
    }

    // ── Delete Confirmation ──────────────────────────────────────────────────
    itemToDelete?.let { item ->
        DeleteConfirmationDialog(
            itemName = item.displayName,
            onConfirm = {
                when (item) {
                    is SelectedItem.PropertyItem -> viewModel.deleteProperty(item.property)
                    is SelectedItem.StructureItem -> viewModel.deleteStructure(item.structure)
                    is SelectedItem.FenceItem -> viewModel.deleteFence(item.fence)
                    is SelectedItem.PaddockItem -> viewModel.deletePaddock(item.paddock)
                    is SelectedItem.WaterSourceItem -> viewModel.deleteWaterSource(item.waterSource)
                    is SelectedItem.CompostBinItem -> viewModel.deleteCompostBin(item.compostBin)
                }
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null },
        )
    }
}
