package com.furrow.app.ui.animals

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
import com.furrow.app.ui.animals.forms.AnimalForm
import com.furrow.app.ui.animals.forms.BreedingRecordForm
import com.furrow.app.ui.animals.forms.FeedLogForm
import com.furrow.app.ui.animals.forms.HealthRecordForm
import com.furrow.app.ui.animals.forms.WeightLogForm
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.DiscardChangesDialog
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.SuccessSnackbarEffect
import com.furrow.app.ui.theme.TextPrimary

@Composable
fun AnimalFormScreen(
    type: String,
    animalId: Long = 0L,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: AnimalViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L

    val title = when (type) {
        "animal" -> if (isEditMode) "Edit Animal" else "Add Animal"
        "health" -> if (isEditMode) "Edit Health Record" else "Add Health Record"
        "breeding" -> if (isEditMode) "Edit Breeding Record" else "Add Breeding Record"
        "weight" -> if (isEditMode) "Edit Weight Log" else "Add Weight Log"
        "feed" -> if (isEditMode) "Edit Feed Log" else "Add Feed Log"
        else -> "Form"
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
                title = title,
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
            "animal" -> AnimalForm(
                animalId = animalId,
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "health" -> HealthRecordForm(
                animalId = animalId,
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "breeding" -> BreedingRecordForm(
                animalId = animalId,
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "weight" -> WeightLogForm(
                animalId = animalId,
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "feed" -> FeedLogForm(
                animalId = animalId,
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
        }
    }
}
