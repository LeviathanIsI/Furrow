package com.furrow.app.ui.orchard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.OrchardPlant
import com.furrow.app.util.displayFormat
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.Tag
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.OrchardGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.Void

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrchardListScreen(
    onPlantClick: (Long) -> Unit,
    onAddPlant: () -> Unit,
    viewModel: OrchardViewModel = hiltViewModel(),
) {
    val plants by viewModel.filteredPlants.collectAsState()
    val allPlants by viewModel.allPlants.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val totalHarvestLbs by viewModel.totalHarvestLbs.collectAsState()

    val view = LocalView.current

    var plantForAction by remember { mutableStateOf<OrchardPlant?>(null) }
    var plantToDelete by remember { mutableStateOf<OrchardPlant?>(null) }

    AppScaffold(
        topBar = {
            AppTopBar(title = "Orchard")
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddPlant,
                containerColor = OrchardGlow,
                contentColor = Void,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add plant")
                Text("Add", modifier = Modifier.padding(start = AppSpacing.xxs))
            }
        },
    ) { padding ->
        if (allPlants.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                EmptyState(
                    title = "No orchard plants yet",
                    subtitle = "Add your first fruit tree, berry bush, or vine to get started.",
                    icon = {
                        Icon(
                            Icons.Outlined.Forest,
                            contentDescription = null,
                            tint = OrchardGlow,
                        )
                    },
                    actionLabel = "Add Plant",
                    onAction = onAddPlant,
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
                // Category filter tags
                item(key = "category_filter") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    ) {
                        item {
                            Tag(
                                text = "All",
                                selected = selectedCategory == null,
                                onClick = { viewModel.setCategoryFilter(null) },
                                accentColor = OrchardGlow,
                            )
                        }
                        items(categories, key = { it }) { category ->
                            Tag(
                                text = category.displayFormat(),
                                selected = selectedCategory == category,
                                onClick = { viewModel.setCategoryFilter(category) },
                                accentColor = OrchardGlow,
                            )
                        }
                    }
                }

                // Summary stats
                item(key = "summary_stats") {
                    Panel {
                        InlineStat(
                            label = "Total Plants",
                            value = "${allPlants.size}",
                        )
                        InlineStat(
                            label = "Total Harvest",
                            value = "${"%.1f".format(totalHarvestLbs)} lbs",
                        )
                    }
                }

                // Plant list
                item(key = "plant_list") {
                    Panel(contentPadding = PaddingValues(0.dp)) {
                        plants.forEachIndexed { index, plant ->
                            val title = buildString {
                                append(plant.species)
                                plant.variety?.let { append(" - $it") }
                            }
                            val subtitle = buildString {
                                append(plant.category.displayFormat())
                                plant.rootstock?.let { append(" | $it") }
                            }
                            ListRow(
                                modifier = Modifier.combinedClickable(
                                    onClick = { onPlantClick(plant.id) },
                                    onLongClick = {
                                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                        plantForAction = plant
                                    },
                                ),
                                title = title,
                                subtitle = subtitle,
                                metadata = plant.status.displayFormat(),
                                showDivider = index != plants.lastIndex,
                            )
                        }
                    }
                }
            }
        }
    }

    // Action sheet
    plantForAction?.let { plant ->
        ItemActionSheet(
            onDismiss = { plantForAction = null },
            onEdit = { onPlantClick(plant.id) },
            onDelete = { plantToDelete = plant },
        )
    }

    // Delete confirmation
    plantToDelete?.let { plant ->
        DeleteConfirmationDialog(
            itemName = buildString {
                append(plant.species)
                plant.variety?.let { append(" - $it") }
            },
            onConfirm = {
                viewModel.deletePlant(plant)
                plantToDelete = null
            },
            onDismiss = { plantToDelete = null },
        )
    }
}
