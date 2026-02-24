package com.furrow.app.ui.compliance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.furrow.app.data.local.entity.ComplianceDocument
import com.furrow.app.data.local.entity.ComplianceInspection
import com.furrow.app.data.local.entity.LabelTemplate
import com.furrow.app.data.local.entity.LicensePermit
import com.furrow.app.data.local.entity.SalesTracker
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.ErrorSnackbarEffect
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.ComplianceGlow
import com.furrow.app.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplianceFormScreen(
    type: String,
    editId: Long = 0L,
    onBack: () -> Unit,
    viewModel: ComplianceViewModel = hiltViewModel(),
) {
    val isEditMode = editId > 0L

    val titleText = when (type) {
        "permit" -> if (isEditMode) "Edit Permit" else "Add Permit"
        "inspection" -> if (isEditMode) "Edit Inspection" else "Add Inspection"
        "sale" -> if (isEditMode) "Edit Sale" else "Add Sale"
        "label" -> if (isEditMode) "Edit Label" else "Add Label"
        "document" -> if (isEditMode) "Edit Document" else "Add Document"
        else -> if (isEditMode) "Edit" else "Add"
    }

    val snackbarHostState = remember { SnackbarHostState() }
    ErrorSnackbarEffect(viewModel.errorMessage, viewModel::clearError, snackbarHostState)

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopBar(
                title = titleText,
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
    ) { padding ->
        when (type) {
            "permit" -> PermitForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "inspection" -> InspectionForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "sale" -> SaleForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "label" -> LabelForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "document" -> DocumentForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

// -- Permit Form --

@Composable
private fun PermitForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: ComplianceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var permitType by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var authority by remember { mutableStateOf("") }
    var issueDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var expiration by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getLicensePermitById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                permitType = it.type
                number = it.number ?: ""
                authority = it.authority ?: ""
                issueDate = it.issueDate ?: System.currentTimeMillis()
                expiration = it.expiration ?: System.currentTimeMillis()
                notes = it.notes ?: ""
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        DropdownSelector(
            label = "Permit Type",
            options = ModuleCatalogData.PERMIT_TYPES,
            selected = permitType,
            onSelect = { permitType = it },
            accentColor = ComplianceGlow,
        )

        InputField(
            value = number,
            onValueChange = { number = it },
            label = { Text("Permit Number") },
            singleLine = true,
            accentColor = ComplianceGlow,
        )

        InputField(
            value = authority,
            onValueChange = { authority = it },
            label = { Text("Issuing Authority") },
            singleLine = true,
            accentColor = ComplianceGlow,
        )

        DateFieldWithToggle(
            label = "Issue Date:",
            dateMillis = issueDate,
            onDateChange = { issueDate = it },
            useTodayDefault = !isEditMode,
            accentColor = ComplianceGlow,
        )

        DateFieldWithToggle(
            label = "Expiration:",
            dateMillis = expiration,
            onDateChange = { expiration = it },
            useTodayDefault = false,
            accentColor = ComplianceGlow,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = ComplianceGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Permit" else "Save Permit",
            onClick = {
                val permit = LicensePermit(
                    id = if (isEditMode) editId else 0,
                    type = permitType.trim(),
                    number = number.ifBlank { null },
                    authority = authority.ifBlank { null },
                    issueDate = issueDate,
                    expiration = expiration,
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateLicensePermit(permit)
                else viewModel.addLicensePermit(permit)
                onBack()
            },
            enabled = permitType.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// -- Inspection Form --

@Composable
private fun InspectionForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: ComplianceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var inspector by remember { mutableStateOf("") }
    var agency by remember { mutableStateOf("") }
    var outcome by remember { mutableStateOf("Pending") }
    var followUpItems by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getInspectionById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                inspector = it.inspector ?: ""
                agency = it.agency ?: ""
                outcome = it.outcome ?: "Pending"
                followUpItems = it.followUpItems ?: ""
                notes = it.notes ?: ""
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        DateFieldWithToggle(
            label = "Inspection Date:",
            dateMillis = date,
            onDateChange = { date = it },
            useTodayDefault = !isEditMode,
            accentColor = ComplianceGlow,
        )

        InputField(
            value = inspector,
            onValueChange = { inspector = it },
            label = { Text("Inspector") },
            singleLine = true,
            accentColor = ComplianceGlow,
        )

        InputField(
            value = agency,
            onValueChange = { agency = it },
            label = { Text("Agency") },
            singleLine = true,
            accentColor = ComplianceGlow,
        )

        DropdownSelector(
            label = "Outcome",
            options = ModuleCatalogData.INSPECTION_OUTCOMES,
            selected = outcome,
            onSelect = { outcome = it },
            accentColor = ComplianceGlow,
        )

        InputField(
            value = followUpItems,
            onValueChange = { followUpItems = it },
            label = { Text("Follow-up Items") },
            minLines = 2,
            accentColor = ComplianceGlow,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = ComplianceGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Inspection" else "Save Inspection",
            onClick = {
                val inspection = ComplianceInspection(
                    id = if (isEditMode) editId else 0,
                    date = date,
                    inspector = inspector.ifBlank { null },
                    agency = agency.ifBlank { null },
                    outcome = outcome,
                    followUpItems = followUpItems.ifBlank { null },
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateInspection(inspection)
                else viewModel.addInspection(inspection)
                onBack()
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// -- Sale Form --

@Composable
private fun SaleForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: ComplianceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var productType by remember { mutableStateOf("") }
    var year by remember { mutableStateOf(java.time.Year.now().value.toString()) }
    var runningTotal by remember { mutableStateOf("0.00") }
    var annualCap by remember { mutableStateOf("") }
    var thresholdAlertPct by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getSalesTrackerById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                productType = it.productType
                year = it.year.toString()
                runningTotal = String.format("%.2f", it.runningTotal)
                annualCap = it.annualCap?.let { cap -> String.format("%.2f", cap) } ?: ""
                thresholdAlertPct = it.thresholdAlertPct?.let { pct -> String.format("%.0f", pct) } ?: ""
                notes = it.notes ?: ""
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        DropdownSelector(
            label = "Product Type",
            options = ModuleCatalogData.SALES_PRODUCT_TYPES,
            selected = productType,
            onSelect = { productType = it },
            accentColor = ComplianceGlow,
        )

        InputField(
            value = year,
            onValueChange = { year = it.filter { c -> c.isDigit() } },
            label = { Text("Year") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            accentColor = ComplianceGlow,
        )

        InputField(
            value = runningTotal,
            onValueChange = { runningTotal = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Running Total ($)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            accentColor = ComplianceGlow,
        )

        InputField(
            value = annualCap,
            onValueChange = { annualCap = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Annual Cap ($)") },
            placeholder = { Text("Optional") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            accentColor = ComplianceGlow,
        )

        InputField(
            value = thresholdAlertPct,
            onValueChange = { thresholdAlertPct = it.filter { c -> c.isDigit() } },
            label = { Text("Threshold Alert (%)") },
            placeholder = { Text("Optional") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            accentColor = ComplianceGlow,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = ComplianceGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Sale" else "Save Sale",
            onClick = {
                val sale = SalesTracker(
                    id = if (isEditMode) editId else 0,
                    productType = productType.trim(),
                    year = year.toIntOrNull() ?: java.time.Year.now().value,
                    runningTotal = runningTotal.toDoubleOrNull() ?: 0.0,
                    annualCap = annualCap.toDoubleOrNull(),
                    thresholdAlertPct = thresholdAlertPct.toDoubleOrNull(),
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateSalesTracker(sale)
                else viewModel.addSalesTracker(sale)
                onBack()
            },
            enabled = productType.isNotBlank() && year.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// -- Label Form --

@Composable
private fun LabelForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: ComplianceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var productName by remember { mutableStateOf("") }
    var templateContent by remember { mutableStateOf("") }
    var requiredFields by remember { mutableStateOf("") }
    var disclaimer by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getLabelTemplateById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                productName = it.productName
                templateContent = it.templateContent ?: ""
                requiredFields = it.requiredFields ?: ""
                disclaimer = it.disclaimer ?: ""
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        InputField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") },
            singleLine = true,
            accentColor = ComplianceGlow,
        )

        InputField(
            value = templateContent,
            onValueChange = { templateContent = it },
            label = { Text("Template Content") },
            minLines = 5,
            accentColor = ComplianceGlow,
        )

        InputField(
            value = requiredFields,
            onValueChange = { requiredFields = it },
            label = { Text("Required Fields") },
            placeholder = { Text("Comma-separated, e.g. weight, ingredients") },
            minLines = 2,
            accentColor = ComplianceGlow,
        )

        InputField(
            value = disclaimer,
            onValueChange = { disclaimer = it },
            label = { Text("Disclaimer") },
            minLines = 3,
            accentColor = ComplianceGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Label" else "Save Label",
            onClick = {
                val label = LabelTemplate(
                    id = if (isEditMode) editId else 0,
                    productName = productName.trim(),
                    templateContent = templateContent.ifBlank { null },
                    requiredFields = requiredFields.ifBlank { null },
                    disclaimer = disclaimer.ifBlank { null },
                )
                if (isEditMode) viewModel.updateLabelTemplate(label)
                else viewModel.addLabelTemplate(label)
                onBack()
            },
            enabled = productName.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// -- Document Form --

@Composable
private fun DocumentForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: ComplianceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var docType by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var fileUrl by remember { mutableStateOf("") }
    var issueDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var expirationDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getDocumentById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                docType = it.type
                name = it.name
                fileUrl = it.fileUrl ?: ""
                issueDate = it.issueDate ?: System.currentTimeMillis()
                expirationDate = it.expirationDate ?: System.currentTimeMillis()
                notes = it.notes ?: ""
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        DropdownSelector(
            label = "Document Type",
            options = ModuleCatalogData.DOCUMENT_TYPES,
            selected = docType,
            onSelect = { docType = it },
            accentColor = ComplianceGlow,
        )

        InputField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Document Name") },
            singleLine = true,
            accentColor = ComplianceGlow,
        )

        InputField(
            value = fileUrl,
            onValueChange = { fileUrl = it },
            label = { Text("File URL") },
            placeholder = { Text("Optional") },
            singleLine = true,
            accentColor = ComplianceGlow,
        )

        DateFieldWithToggle(
            label = "Issue Date:",
            dateMillis = issueDate,
            onDateChange = { issueDate = it },
            useTodayDefault = !isEditMode,
            accentColor = ComplianceGlow,
        )

        DateFieldWithToggle(
            label = "Expiration Date:",
            dateMillis = expirationDate,
            onDateChange = { expirationDate = it },
            useTodayDefault = false,
            accentColor = ComplianceGlow,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            minLines = 3,
            accentColor = ComplianceGlow,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Document" else "Save Document",
            onClick = {
                val document = ComplianceDocument(
                    id = if (isEditMode) editId else 0,
                    type = docType.trim(),
                    name = name.trim(),
                    fileUrl = fileUrl.ifBlank { null },
                    issueDate = issueDate,
                    expirationDate = expirationDate,
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateDocument(document)
                else viewModel.addDocument(document)
                onBack()
            },
            enabled = docType.isNotBlank() && name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
