package com.furrow.app.ui.compliance

import android.view.HapticFeedbackConstants
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
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.ComplianceDocument
import com.furrow.app.data.local.entity.ComplianceInspection
import com.furrow.app.data.local.entity.LabelTemplate
import com.furrow.app.data.local.entity.LicensePermit
import com.furrow.app.data.local.entity.SalesTracker
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DeleteConfirmationDialog
import com.furrow.app.ui.components.EmptyState
import com.furrow.app.ui.components.InlineStat
import com.furrow.app.ui.components.ItemActionSheet
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Charcoal
import com.furrow.app.ui.theme.ComplianceGlow
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.ui.theme.Void
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val complianceDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

internal fun formatDate(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(complianceDateFormatter)
}

private val tabLabels = listOf("PERMITS", "INSPECTIONS", "SALES", "LABELS", "DOCUMENTS")

private val tabTypes = listOf("permit", "inspection", "sale", "label", "document")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ComplianceListScreen(
    onAddItem: (String) -> Unit,
    onEditItem: (String, Long) -> Unit,
    viewModel: ComplianceViewModel = hiltViewModel(),
) {
    val permits by viewModel.licensePermits.collectAsState()
    val expiringSoon by viewModel.expiringSoonPermits.collectAsState()
    val inspections by viewModel.inspections.collectAsState()
    val sales by viewModel.salesTrackers.collectAsState()
    val labels by viewModel.labelTemplates.collectAsState()
    val documents by viewModel.documents.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    // Action sheet state
    var permitForAction by remember { mutableStateOf<LicensePermit?>(null) }
    var inspectionForAction by remember { mutableStateOf<ComplianceInspection?>(null) }
    var saleForAction by remember { mutableStateOf<SalesTracker?>(null) }
    var labelForAction by remember { mutableStateOf<LabelTemplate?>(null) }
    var documentForAction by remember { mutableStateOf<ComplianceDocument?>(null) }

    // Delete confirmation state
    var permitToDelete by remember { mutableStateOf<LicensePermit?>(null) }
    var inspectionToDelete by remember { mutableStateOf<ComplianceInspection?>(null) }
    var saleToDelete by remember { mutableStateOf<SalesTracker?>(null) }
    var labelToDelete by remember { mutableStateOf<LabelTemplate?>(null) }
    var documentToDelete by remember { mutableStateOf<ComplianceDocument?>(null) }

    val expiringSoonIds = remember(expiringSoon) { expiringSoon.map { it.id }.toSet() }

    val ninetyDaysFromNow = remember {
        Instant.now().atZone(ZoneId.systemDefault())
            .plusDays(90).toInstant().toEpochMilli()
    }

    AppScaffold(
        topBar = {
            AppTopBar(title = "Compliance")
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onAddItem(tabTypes[selectedTab]) },
                containerColor = ComplianceGlow,
                contentColor = Void,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
                Text(
                    text = when (selectedTab) {
                        0 -> "Add Permit"
                        1 -> "Add Inspection"
                        2 -> "Add Sale"
                        3 -> "Add Label"
                        4 -> "Add Document"
                        else -> "Add"
                    },
                    modifier = Modifier.padding(start = AppSpacing.xs),
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Summary Panel
            Panel(
                modifier = Modifier.padding(
                    horizontal = AppSpacing.md,
                    vertical = AppSpacing.sm,
                ),
            ) {
                InlineStat(label = "Total Permits", value = "${permits.size}")
                InlineStat(label = "Expiring Soon", value = "${expiringSoon.size}")
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Charcoal,
                contentColor = TextPrimary,
                modifier = Modifier.padding(horizontal = AppSpacing.md),
                divider = { HorizontalDivider(thickness = 1.dp, color = BorderSubtle) },
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = ComplianceGlow,
                        )
                    }
                },
            ) {
                tabLabels.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp,
                                ),
                                color = if (selectedTab == index) TextPrimary else TextTertiary,
                            )
                        },
                    )
                }
            }

            // Tab content
            when (selectedTab) {
                0 -> PermitsTab(
                    permits = permits,
                    expiringSoonIds = expiringSoonIds,
                    modifier = Modifier.weight(1f),
                    onLongPress = { permitForAction = it },
                )
                1 -> InspectionsTab(
                    inspections = inspections,
                    modifier = Modifier.weight(1f),
                    onLongPress = { inspectionForAction = it },
                )
                2 -> SalesTab(
                    sales = sales,
                    modifier = Modifier.weight(1f),
                    onLongPress = { saleForAction = it },
                )
                3 -> LabelsTab(
                    labels = labels,
                    modifier = Modifier.weight(1f),
                    onLongPress = { labelForAction = it },
                )
                4 -> DocumentsTab(
                    documents = documents,
                    ninetyDaysFromNow = ninetyDaysFromNow,
                    modifier = Modifier.weight(1f),
                    onLongPress = { documentForAction = it },
                )
            }
        }
    }

    // -- Action Sheets --

    permitForAction?.let { permit ->
        ItemActionSheet(
            onDismiss = { permitForAction = null },
            onEdit = { onEditItem("permit", permit.id) },
            onDelete = { permitToDelete = permit },
        )
    }

    inspectionForAction?.let { inspection ->
        ItemActionSheet(
            onDismiss = { inspectionForAction = null },
            onEdit = { onEditItem("inspection", inspection.id) },
            onDelete = { inspectionToDelete = inspection },
        )
    }

    saleForAction?.let { sale ->
        ItemActionSheet(
            onDismiss = { saleForAction = null },
            onEdit = { onEditItem("sale", sale.id) },
            onDelete = { saleToDelete = sale },
        )
    }

    labelForAction?.let { label ->
        ItemActionSheet(
            onDismiss = { labelForAction = null },
            onEdit = { onEditItem("label", label.id) },
            onDelete = { labelToDelete = label },
        )
    }

    documentForAction?.let { document ->
        ItemActionSheet(
            onDismiss = { documentForAction = null },
            onEdit = { onEditItem("document", document.id) },
            onDelete = { documentToDelete = document },
        )
    }

    // -- Delete Confirmation Dialogs --

    permitToDelete?.let { permit ->
        DeleteConfirmationDialog(
            itemName = permit.type,
            onConfirm = {
                viewModel.deleteLicensePermit(permit)
                permitToDelete = null
            },
            onDismiss = { permitToDelete = null },
        )
    }

    inspectionToDelete?.let { inspection ->
        DeleteConfirmationDialog(
            itemName = inspection.agency ?: "Inspection",
            onConfirm = {
                viewModel.deleteInspection(inspection)
                inspectionToDelete = null
            },
            onDismiss = { inspectionToDelete = null },
        )
    }

    saleToDelete?.let { sale ->
        DeleteConfirmationDialog(
            itemName = sale.productType,
            onConfirm = {
                viewModel.deleteSalesTracker(sale)
                saleToDelete = null
            },
            onDismiss = { saleToDelete = null },
        )
    }

    labelToDelete?.let { label ->
        DeleteConfirmationDialog(
            itemName = label.productName,
            onConfirm = {
                viewModel.deleteLabelTemplate(label)
                labelToDelete = null
            },
            onDismiss = { labelToDelete = null },
        )
    }

    documentToDelete?.let { document ->
        DeleteConfirmationDialog(
            itemName = document.name,
            onConfirm = {
                viewModel.deleteDocument(document)
                documentToDelete = null
            },
            onDismiss = { documentToDelete = null },
        )
    }
}

// -- Permits Tab --

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PermitsTab(
    permits: List<LicensePermit>,
    expiringSoonIds: Set<Long>,
    modifier: Modifier = Modifier,
    onLongPress: (LicensePermit) -> Unit,
) {
    val view = LocalView.current
    if (permits.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No permits",
                subtitle = "Add your first license or permit to track compliance.",
                icon = {
                    Icon(
                        Icons.Outlined.Balance,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = ComplianceGlow,
                    )
                },
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                end = AppSpacing.md,
                top = AppSpacing.sm,
                bottom = 80.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    permits.forEachIndexed { index, permit ->
                        val isExpiring = expiringSoonIds.contains(permit.id)
                        ListRow(
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(permit)
                                },
                            ),
                            title = permit.type,
                            subtitle = buildString {
                                permit.authority?.let { append(it) }
                                permit.number?.let {
                                    if (isNotEmpty()) append(" ")
                                    append("# $it")
                                }
                            }.ifBlank { null },
                            metadata = permit.expiration?.let { formatDate(it) },
                            trailing = if (isExpiring) {
                                {
                                    Icon(
                                        Icons.Outlined.Warning,
                                        contentDescription = "Expiring soon",
                                        tint = StatusWarn,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            } else null,
                            showDivider = index != permits.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// -- Inspections Tab --

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InspectionsTab(
    inspections: List<ComplianceInspection>,
    modifier: Modifier = Modifier,
    onLongPress: (ComplianceInspection) -> Unit,
) {
    val view = LocalView.current
    if (inspections.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No inspections",
                subtitle = "Record compliance inspections to keep track of results.",
                icon = {
                    Icon(
                        Icons.Outlined.Balance,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = ComplianceGlow,
                    )
                },
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                end = AppSpacing.md,
                top = AppSpacing.sm,
                bottom = 80.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    inspections.forEachIndexed { index, inspection ->
                        ListRow(
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(inspection)
                                },
                            ),
                            title = inspection.agency ?: "Inspection",
                            subtitle = buildString {
                                inspection.inspector?.let { append(it) }
                                inspection.outcome?.let {
                                    if (isNotEmpty()) append(" - ")
                                    append(it.replaceFirstChar { c -> c.uppercase() })
                                }
                            }.ifBlank { null },
                            metadata = formatDate(inspection.date),
                            showDivider = index != inspections.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// -- Sales Tab --

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SalesTab(
    sales: List<SalesTracker>,
    modifier: Modifier = Modifier,
    onLongPress: (SalesTracker) -> Unit,
) {
    val view = LocalView.current
    if (sales.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No sales tracked",
                subtitle = "Track your sales to stay within annual caps.",
                icon = {
                    Icon(
                        Icons.Outlined.Balance,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = ComplianceGlow,
                    )
                },
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                end = AppSpacing.md,
                top = AppSpacing.sm,
                bottom = 80.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    sales.forEachIndexed { index, sale ->
                        ListRow(
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(sale)
                                },
                            ),
                            title = sale.productType,
                            subtitle = "Year: ${sale.year}",
                            trailingText = "$${String.format("%.2f", sale.runningTotal)}",
                            metadata = sale.annualCap?.let { "Cap: $${String.format("%.2f", it)}" },
                            showDivider = index != sales.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// -- Labels Tab --

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LabelsTab(
    labels: List<LabelTemplate>,
    modifier: Modifier = Modifier,
    onLongPress: (LabelTemplate) -> Unit,
) {
    val view = LocalView.current
    if (labels.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No label templates",
                subtitle = "Create label templates for your products.",
                icon = {
                    Icon(
                        Icons.Outlined.Balance,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = ComplianceGlow,
                    )
                },
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                end = AppSpacing.md,
                top = AppSpacing.sm,
                bottom = 80.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    labels.forEachIndexed { index, label ->
                        val fieldCount = label.requiredFields
                            ?.split(",")
                            ?.filter { it.isNotBlank() }
                            ?.size ?: 0
                        ListRow(
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(label)
                                },
                            ),
                            title = label.productName,
                            subtitle = label.disclaimer?.take(50),
                            metadata = "$fieldCount required fields",
                            showDivider = index != labels.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

// -- Documents Tab --

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DocumentsTab(
    documents: List<ComplianceDocument>,
    ninetyDaysFromNow: Long,
    modifier: Modifier = Modifier,
    onLongPress: (ComplianceDocument) -> Unit,
) {
    val view = LocalView.current
    val now = remember { System.currentTimeMillis() }
    if (documents.isEmpty()) {
        Box(modifier = modifier) {
            EmptyState(
                title = "No documents",
                subtitle = "Upload and track compliance documents.",
                icon = {
                    Icon(
                        Icons.Outlined.Balance,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = ComplianceGlow,
                    )
                },
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                end = AppSpacing.md,
                top = AppSpacing.sm,
                bottom = 80.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            item {
                Panel(contentPadding = PaddingValues(0.dp)) {
                    documents.forEachIndexed { index, document ->
                        val isApproaching = document.expirationDate?.let {
                            it in now..ninetyDaysFromNow
                        } ?: false
                        ListRow(
                            modifier = Modifier.combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    onLongPress(document)
                                },
                            ),
                            title = document.name,
                            subtitle = document.type,
                            metadata = document.issueDate?.let { formatDate(it) },
                            trailing = if (isApproaching) {
                                {
                                    Icon(
                                        Icons.Outlined.Warning,
                                        contentDescription = "Expiration approaching",
                                        tint = StatusWarn,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            } else null,
                            showDivider = index != documents.lastIndex,
                        )
                    }
                }
            }
        }
    }
}
