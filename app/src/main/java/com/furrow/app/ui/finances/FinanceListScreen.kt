package com.furrow.app.ui.finances

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.BarterTrade
import com.furrow.app.data.local.entity.Expense
import com.furrow.app.data.local.entity.GrantRecord
import com.furrow.app.data.local.entity.MileageLog
import com.furrow.app.data.local.entity.Revenue
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.FinanceGlow
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusGood
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void
import com.furrow.app.util.DateUtil
import com.furrow.app.util.displayFormat

private enum class FinanceTab(val label: String, val type: String) {
    EXPENSES("Expenses", "expense"),
    REVENUE("Revenue", "revenue"),
    MILEAGE("Mileage", "mileage"),
    BARTER("Barter", "barter"),
    GRANTS("Grants", "grant"),
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinanceListScreen(
    onAddItem: (String) -> Unit,
    onEditItem: (String, Long) -> Unit,
    viewModel: FinanceViewModel = hiltViewModel(),
) {
    val expenses by viewModel.expenses.collectAsState()
    val revenues by viewModel.revenues.collectAsState()
    val mileageLogs by viewModel.mileageLogs.collectAsState()
    val barterTrades by viewModel.barterTrades.collectAsState()
    val grantRecords by viewModel.grantRecords.collectAsState()

    val monthExpenses by viewModel.monthExpenses.collectAsState()
    val monthRevenue by viewModel.monthRevenue.collectAsState()
    val netIncome by viewModel.netIncome.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = FinanceTab.entries

    var expenseForAction by remember { mutableStateOf<Expense?>(null) }
    var revenueForAction by remember { mutableStateOf<Revenue?>(null) }
    var mileageForAction by remember { mutableStateOf<MileageLog?>(null) }
    var barterForAction by remember { mutableStateOf<BarterTrade?>(null) }
    var grantForAction by remember { mutableStateOf<GrantRecord?>(null) }

    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }
    var revenueToDelete by remember { mutableStateOf<Revenue?>(null) }
    var mileageToDelete by remember { mutableStateOf<MileageLog?>(null) }
    var barterToDelete by remember { mutableStateOf<BarterTrade?>(null) }
    var grantToDelete by remember { mutableStateOf<GrantRecord?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(title = "Finances")
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onAddItem(tabs[selectedTab].type) },
                containerColor = FinanceGlow,
                contentColor = Void,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                    )
                },
                text = {
                    Text(
                        text = "Add ${tabs[selectedTab].label.trimEnd('s')}",
                    )
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
                top = AppSpacing.md,
                bottom = AppSpacing.bottomListPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            // -- Summary Panel --
            item(key = "summary") {
                Panel {
                    Text(
                        text = "This Month",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary,
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    InlineStat(
                        label = "Expenses",
                        value = String.format("$%.2f", monthExpenses),
                    )
                    InlineStat(
                        label = "Revenue",
                        value = String.format("$%.2f", monthRevenue),
                    )
                    InlineStat(
                        label = "Net Income",
                        value = String.format("$%.2f", netIncome),
                    )
                }
            }

            // -- Tab Row --
            item(key = "tabs") {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Charcoal,
                    contentColor = TextPrimary,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = FinanceGlow,
                            )
                        }
                    },
                    divider = {},
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 11.sp,
                                    ),
                                    color = if (selectedTab == index) TextPrimary else TextTertiary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                        )
                    }
                }
            }

            // -- Tab Content --
            when (tabs[selectedTab]) {
                FinanceTab.EXPENSES -> {
                    if (expenses.isEmpty()) {
                        item(key = "empty_expenses") {
                            EmptyState(
                                title = "No expenses yet",
                                subtitle = "Track your farm expenses to stay on top of your budget.",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Payments,
                                        contentDescription = null,
                                        tint = TextTertiary,
                                        modifier = Modifier.size(28.dp),
                                    )
                                },
                                actionLabel = "Add Expense",
                                onAction = { onAddItem("expense") },
                            )
                        }
                    } else {
                        item(key = "expense_panel") {
                            Panel(contentPadding = PaddingValues(0.dp)) {
                                expenses.forEachIndexed { index, expense ->
                                    ListRow(
                                        title = expense.category?.displayFormat() ?: "Expense",
                                        subtitle = listOfNotNull(
                                            expense.vendor,
                                            expense.paymentMethod?.displayFormat(),
                                        ).joinToString(" \u2022 ").ifEmpty { null },
                                        trailing = {
                                            Column(
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                Text(
                                                    text = "-${String.format("$%.2f", expense.amount)}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = StatusBad,
                                                )
                                                Text(
                                                    text = DateUtil.formatDate(expense.date, viewModel.zone),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextTertiary,
                                                )
                                            }
                                        },
                                        modifier = Modifier.combinedClickable(
                                            onClick = { onEditItem("expense", expense.id) },
                                            onLongClick = { expenseForAction = expense },
                                        ),
                                        showDivider = index != expenses.lastIndex,
                                    )
                                }
                            }
                        }
                    }
                }

                FinanceTab.REVENUE -> {
                    if (revenues.isEmpty()) {
                        item(key = "empty_revenue") {
                            EmptyState(
                                title = "No revenue yet",
                                subtitle = "Record sales and income from your homestead.",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Payments,
                                        contentDescription = null,
                                        tint = TextTertiary,
                                        modifier = Modifier.size(28.dp),
                                    )
                                },
                                actionLabel = "Add Revenue",
                                onAction = { onAddItem("revenue") },
                            )
                        }
                    } else {
                        item(key = "revenue_panel") {
                            Panel(contentPadding = PaddingValues(0.dp)) {
                                revenues.forEachIndexed { index, revenue ->
                                    ListRow(
                                        title = revenue.product ?: "Revenue",
                                        subtitle = listOfNotNull(
                                            revenue.buyer,
                                            revenue.salesChannel,
                                        ).joinToString(" \u2022 ").ifEmpty { null },
                                        trailing = {
                                            Column(
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                Text(
                                                    text = "+${String.format("$%.2f", revenue.total ?: 0.0)}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = StatusGood,
                                                )
                                                Text(
                                                    text = DateUtil.formatDate(revenue.date, viewModel.zone),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextTertiary,
                                                )
                                            }
                                        },
                                        modifier = Modifier.combinedClickable(
                                            onClick = { onEditItem("revenue", revenue.id) },
                                            onLongClick = { revenueForAction = revenue },
                                        ),
                                        showDivider = index != revenues.lastIndex,
                                    )
                                }
                            }
                        }
                    }
                }

                FinanceTab.MILEAGE -> {
                    if (mileageLogs.isEmpty()) {
                        item(key = "empty_mileage") {
                            EmptyState(
                                title = "No mileage logs yet",
                                subtitle = "Track farm-related trips for tax deductions.",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Payments,
                                        contentDescription = null,
                                        tint = TextTertiary,
                                        modifier = Modifier.size(28.dp),
                                    )
                                },
                                actionLabel = "Add Mileage",
                                onAction = { onAddItem("mileage") },
                            )
                        }
                    } else {
                        item(key = "mileage_panel") {
                            Panel(contentPadding = PaddingValues(0.dp)) {
                                mileageLogs.forEachIndexed { index, log ->
                                    ListRow(
                                        title = buildString {
                                            append("${log.miles ?: 0.0} mi")
                                            log.purpose?.let { append(" \u2022 $it") }
                                        },
                                        subtitle = if (log.origin != null || log.destination != null) {
                                            "${log.origin ?: "?"}\u2192${log.destination ?: "?"}"
                                        } else null,
                                        trailing = {
                                            Column(
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                Text(
                                                    text = String.format("$%.2f", log.totalDeduction ?: 0.0),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = TextPrimary,
                                                )
                                                Text(
                                                    text = DateUtil.formatDate(log.date, viewModel.zone),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextTertiary,
                                                )
                                            }
                                        },
                                        modifier = Modifier.combinedClickable(
                                            onClick = { onEditItem("mileage", log.id) },
                                            onLongClick = { mileageForAction = log },
                                        ),
                                        showDivider = index != mileageLogs.lastIndex,
                                    )
                                }
                            }
                        }
                    }
                }

                FinanceTab.BARTER -> {
                    if (barterTrades.isEmpty()) {
                        item(key = "empty_barter") {
                            EmptyState(
                                title = "No barter trades yet",
                                subtitle = "Log trades with neighbors and other homesteaders.",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Payments,
                                        contentDescription = null,
                                        tint = TextTertiary,
                                        modifier = Modifier.size(28.dp),
                                    )
                                },
                                actionLabel = "Add Trade",
                                onAction = { onAddItem("barter") },
                            )
                        }
                    } else {
                        item(key = "barter_panel") {
                            Panel(contentPadding = PaddingValues(0.dp)) {
                                barterTrades.forEachIndexed { index, trade ->
                                    ListRow(
                                        title = trade.partner ?: "Trade",
                                        subtitle = buildString {
                                            append("Gave: ${trade.givenItems ?: "?"}")
                                            append(" \u2192 Got: ${trade.receivedItems ?: "?"}")
                                        },
                                        metadata = DateUtil.formatDate(trade.date, viewModel.zone),
                                        modifier = Modifier.combinedClickable(
                                            onClick = { onEditItem("barter", trade.id) },
                                            onLongClick = { barterForAction = trade },
                                        ),
                                        showDivider = index != barterTrades.lastIndex,
                                    )
                                }
                            }
                        }
                    }
                }

                FinanceTab.GRANTS -> {
                    if (grantRecords.isEmpty()) {
                        item(key = "empty_grants") {
                            EmptyState(
                                title = "No grants yet",
                                subtitle = "Track grant applications and awards for your operation.",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Payments,
                                        contentDescription = null,
                                        tint = TextTertiary,
                                        modifier = Modifier.size(28.dp),
                                    )
                                },
                                actionLabel = "Add Grant",
                                onAction = { onAddItem("grant") },
                            )
                        }
                    } else {
                        item(key = "grants_panel") {
                            Panel(contentPadding = PaddingValues(0.dp)) {
                                grantRecords.forEachIndexed { index, grant ->
                                    ListRow(
                                        title = grant.program ?: "Grant",
                                        subtitle = listOfNotNull(
                                            grant.agency,
                                            grant.status?.displayFormat(),
                                        ).joinToString(" \u2022 ").ifEmpty { null },
                                        trailing = {
                                            Column(
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                grant.awardAmount?.let { amount ->
                                                    Text(
                                                        text = String.format("$%.2f", amount),
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = TextPrimary,
                                                    )
                                                }
                                                grant.applicationDate?.let { date ->
                                                    Text(
                                                        text = DateUtil.formatDate(date, viewModel.zone),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = TextTertiary,
                                                    )
                                                }
                                            }
                                        },
                                        modifier = Modifier.combinedClickable(
                                            onClick = { onEditItem("grant", grant.id) },
                                            onLongClick = { grantForAction = grant },
                                        ),
                                        showDivider = index != grantRecords.lastIndex,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // -- Action Sheets --
    expenseForAction?.let { expense ->
        ItemActionSheet(
            onDismiss = { expenseForAction = null },
            onEdit = { onEditItem("expense", expense.id) },
            onDelete = { expenseToDelete = expense },
        )
    }
    revenueForAction?.let { revenue ->
        ItemActionSheet(
            onDismiss = { revenueForAction = null },
            onEdit = { onEditItem("revenue", revenue.id) },
            onDelete = { revenueToDelete = revenue },
        )
    }
    mileageForAction?.let { log ->
        ItemActionSheet(
            onDismiss = { mileageForAction = null },
            onEdit = { onEditItem("mileage", log.id) },
            onDelete = { mileageToDelete = log },
        )
    }
    barterForAction?.let { trade ->
        ItemActionSheet(
            onDismiss = { barterForAction = null },
            onEdit = { onEditItem("barter", trade.id) },
            onDelete = { barterToDelete = trade },
        )
    }
    grantForAction?.let { grant ->
        ItemActionSheet(
            onDismiss = { grantForAction = null },
            onEdit = { onEditItem("grant", grant.id) },
            onDelete = { grantToDelete = grant },
        )
    }

    // -- Delete Confirmations --
    expenseToDelete?.let { expense ->
        DeleteConfirmationDialog(
            itemName = expense.category ?: "Expense",
            onConfirm = {
                viewModel.deleteExpense(expense)
                expenseToDelete = null
            },
            onDismiss = { expenseToDelete = null },
        )
    }
    revenueToDelete?.let { revenue ->
        DeleteConfirmationDialog(
            itemName = revenue.product ?: "Revenue",
            onConfirm = {
                viewModel.deleteRevenue(revenue)
                revenueToDelete = null
            },
            onDismiss = { revenueToDelete = null },
        )
    }
    mileageToDelete?.let { log ->
        DeleteConfirmationDialog(
            itemName = "Mileage Log",
            onConfirm = {
                viewModel.deleteMileageLog(log)
                mileageToDelete = null
            },
            onDismiss = { mileageToDelete = null },
        )
    }
    barterToDelete?.let { trade ->
        DeleteConfirmationDialog(
            itemName = trade.partner ?: "Trade",
            onConfirm = {
                viewModel.deleteBarterTrade(trade)
                barterToDelete = null
            },
            onDismiss = { barterToDelete = null },
        )
    }
    grantToDelete?.let { grant ->
        DeleteConfirmationDialog(
            itemName = grant.program ?: "Grant",
            onConfirm = {
                viewModel.deleteGrantRecord(grant)
                grantToDelete = null
            },
            onDismiss = { grantToDelete = null },
        )
    }
}
