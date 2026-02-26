package com.furrow.app.ui.finances

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.DiscardChangesDialog
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.SuccessSnackbarEffect
import com.furrow.app.ui.finances.forms.BarterTradeForm
import com.furrow.app.ui.finances.forms.ExpenseForm
import com.furrow.app.ui.finances.forms.GrantRecordForm
import com.furrow.app.ui.finances.forms.MileageLogForm
import com.furrow.app.ui.finances.forms.RevenueForm
import com.furrow.app.ui.theme.TextPrimary

@Composable
fun FinanceFormScreen(
    type: String,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: FinanceViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L

    val screenTitle = when (type) {
        "expense" -> if (isEditMode) "Edit Expense" else "Add Expense"
        "revenue" -> if (isEditMode) "Edit Revenue" else "Add Revenue"
        "mileage" -> if (isEditMode) "Edit Mileage" else "Add Mileage"
        "barter" -> if (isEditMode) "Edit Trade" else "Add Trade"
        "grant" -> if (isEditMode) "Edit Grant" else "Add Grant"
        else -> "Finance"
    }

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)
    SuccessSnackbarEffect(
        message = viewModel.successMessage,
        onClear = viewModel::clearSuccess,
        snackbarHostState = snackbarHostState,
        onDismissed = { onBack() },
    )

    var showDiscardDialog by remember { mutableStateOf(false) }
    BackHandler { showDiscardDialog = true }
    DiscardChangesDialog(
        showDialog = showDiscardDialog,
        onDismiss = { showDiscardDialog = false },
        onDiscard = { showDiscardDialog = false; onBack() },
    )

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(
                title = screenTitle,
                navigationIcon = {
                    IconButton(onClick = { showDiscardDialog = true }) {
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
        when (type) {
            "expense" -> ExpenseForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "revenue" -> RevenueForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "mileage" -> MileageLogForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "barter" -> BarterTradeForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "grant" -> GrantRecordForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
        }
    }
}
