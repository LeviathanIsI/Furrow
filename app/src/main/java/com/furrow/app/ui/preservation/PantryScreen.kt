package com.furrow.app.ui.preservation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.PantryItem
import com.furrow.app.util.displayFormat
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.PreservationGlow
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.Void

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantryScreen(
    onBack: () -> Unit,
    onAddPantryItem: () -> Unit,
    onEditPantryItem: (Long) -> Unit,
    viewModel: PreservationViewModel = hiltViewModel(),
) {
    val pantryItems by viewModel.pantryItems.collectAsState()
    val inStockPantry by viewModel.inStockPantry.collectAsState()
    val expiringSoon by viewModel.expiringSoon.collectAsState()

    var itemForAction by remember { mutableStateOf<PantryItem?>(null) }
    var itemToDelete by remember { mutableStateOf<PantryItem?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(
                title = "Pantry",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPantryItem,
                containerColor = PreservationGlow,
                contentColor = Void,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add pantry item")
            }
        },
    ) { padding ->
        if (pantryItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                EmptyState(
                    title = "No pantry items",
                    subtitle = "Add items to track your pantry inventory.",
                    icon = {
                        Icon(
                            Icons.Outlined.Kitchen,
                            contentDescription = "Pantry item",
                            modifier = Modifier.size(28.dp),
                            tint = PreservationGlow,
                        )
                    },
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
                    top = AppSpacing.sm,
                    bottom = AppSpacing.bottomListPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                item(key = "summary") {
                    Panel {
                        InlineStat(
                            label = "In Stock",
                            value = "${inStockPantry.size}",
                        )
                        InlineStat(
                            label = "Expiring Soon",
                            value = "${expiringSoon.size}",
                        )
                    }
                }

                item(key = "pantry_list") {
                    Panel(contentPadding = PaddingValues(0.dp)) {
                        pantryItems.forEachIndexed { index, item ->
                            val isExpiring = expiringSoon.any { it.id == item.id }
                            val subtitle = listOfNotNull(
                                item.category.displayFormat(),
                                item.quantity?.let { qty ->
                                    val qtyStr = if (qty == qty.toLong().toDouble()) {
                                        qty.toLong().toString()
                                    } else {
                                        "%.1f".format(qty)
                                    }
                                    item.unit?.let { "$qtyStr $it" } ?: qtyStr
                                },
                            ).joinToString(" \u2022 ")
                            val statusText = item.status.displayFormat()
                            ListRow(
                                title = item.name,
                                subtitle = subtitle.ifEmpty { null },
                                metadata = statusText,
                                trailing = if (isExpiring) {
                                    {
                                        Text(
                                            text = "Expiring",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = StatusWarn,
                                        )
                                    }
                                } else {
                                    null
                                },
                                modifier = Modifier.combinedClickable(
                                    onClick = { onEditPantryItem(item.id) },
                                    onLongClick = { itemForAction = item },
                                ),
                                showDivider = index != pantryItems.lastIndex,
                            )
                        }
                    }
                }
            }
        }
    }

    // -- Action Sheet --

    itemForAction?.let { item ->
        ItemActionSheet(
            onDismiss = { itemForAction = null },
            onEdit = { onEditPantryItem(item.id) },
            onDelete = {
                itemToDelete = item
                itemForAction = null
            },
        )
    }

    // -- Delete Confirmation --

    itemToDelete?.let { item ->
        DeleteConfirmationDialog(
            itemName = item.name,
            onConfirm = {
                viewModel.deletePantryItem(item)
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null },
        )
    }
}
