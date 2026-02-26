package com.furrow.app.ui.land

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.layout.padding
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
import com.furrow.app.ui.land.forms.CompostBinForm
import com.furrow.app.ui.land.forms.FenceForm
import com.furrow.app.ui.land.forms.PaddockForm
import com.furrow.app.ui.land.forms.PropertyForm
import com.furrow.app.ui.land.forms.SoilTestForm
import com.furrow.app.ui.land.forms.StructureForm
import com.furrow.app.ui.land.forms.WaterSourceForm
import com.furrow.app.ui.land.forms.WeatherForm
import com.furrow.app.ui.theme.TextPrimary

@Composable
fun LandFormScreen(
    type: String,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: LandViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L

    val title = when (type) {
        "property" -> if (isEditMode) "Edit Property" else "Add Property"
        "structure" -> if (isEditMode) "Edit Structure" else "Add Structure"
        "fence" -> if (isEditMode) "Edit Fence" else "Add Fence"
        "paddock" -> if (isEditMode) "Edit Paddock" else "Add Paddock"
        "water_source" -> if (isEditMode) "Edit Water Source" else "Add Water Source"
        "compost_bin" -> if (isEditMode) "Edit Compost Bin" else "Add Compost Bin"
        "soil_test" -> if (isEditMode) "Edit Soil Test" else "Add Soil Test"
        "weather" -> if (isEditMode) "Edit Weather Log" else "Add Weather Log"
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
            "property" -> PropertyForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "structure" -> StructureForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "fence" -> FenceForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "paddock" -> PaddockForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "water_source" -> WaterSourceForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "compost_bin" -> CompostBinForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "soil_test" -> SoilTestForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "weather" -> WeatherForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
        }
    }
}
