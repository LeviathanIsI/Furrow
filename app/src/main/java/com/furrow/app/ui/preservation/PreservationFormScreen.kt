package com.furrow.app.ui.preservation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.ui.components.DiscardChangesDialog
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.SuccessSnackbarEffect
import com.furrow.app.ui.preservation.forms.CanningForm
import com.furrow.app.ui.preservation.forms.DehydratingForm
import com.furrow.app.ui.preservation.forms.FermentingForm
import com.furrow.app.ui.preservation.forms.FreezingForm
import com.furrow.app.ui.preservation.forms.PantryItemForm
import com.furrow.app.ui.preservation.forms.SmokingCuringForm

@Composable
fun PreservationFormScreen(
    type: String,
    editId: Long = 0L,
    initialItemName: String = "",
    onBack: () -> Unit,
    viewModel: PreservationViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L

    var showDiscardDialog by remember { mutableStateOf(false) }
    BackHandler { showDiscardDialog = true }
    DiscardChangesDialog(showDialog = showDiscardDialog, onDismiss = { showDiscardDialog = false }, onDiscard = { showDiscardDialog = false; onBack() })

    val guardedBack = { showDiscardDialog = true }

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)
    SuccessSnackbarEffect(
        message = viewModel.successMessage,
        onClear = viewModel::clearSuccess,
        snackbarHostState = snackbarHostState,
        onDismissed = { onBack() },
    )

    when (type) {
        "canning" -> CanningForm(editId = editId, isEditMode = isEditMode, onBack = guardedBack, viewModel = viewModel, snackbarHostState = snackbarHostState, initialItemName = initialItemName)
        "dehydrating" -> DehydratingForm(editId = editId, isEditMode = isEditMode, onBack = guardedBack, viewModel = viewModel, snackbarHostState = snackbarHostState, initialItemName = initialItemName)
        "fermenting" -> FermentingForm(editId = editId, isEditMode = isEditMode, onBack = guardedBack, viewModel = viewModel, snackbarHostState = snackbarHostState, initialItemName = initialItemName)
        "freezing" -> FreezingForm(editId = editId, isEditMode = isEditMode, onBack = guardedBack, viewModel = viewModel, snackbarHostState = snackbarHostState, initialItemName = initialItemName)
        "smoking" -> SmokingCuringForm(editId = editId, isEditMode = isEditMode, onBack = guardedBack, viewModel = viewModel, snackbarHostState = snackbarHostState)
        "pantry" -> PantryItemForm(editId = editId, isEditMode = isEditMode, onBack = guardedBack, viewModel = viewModel, snackbarHostState = snackbarHostState)
    }
}
