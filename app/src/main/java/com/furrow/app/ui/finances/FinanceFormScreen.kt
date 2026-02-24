package com.furrow.app.ui.finances

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.furrow.app.data.local.entity.BarterTrade
import com.furrow.app.data.local.entity.Expense
import com.furrow.app.data.local.entity.GrantRecord
import com.furrow.app.data.local.entity.MileageLog
import com.furrow.app.data.local.entity.Revenue
import com.furrow.app.data.FinanceCategoryData
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppSectionHeader
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.ToggleRow
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.FinanceGlow
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.Void

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

    AppScaffold(
        topBar = {
            AppTopBar(
                title = screenTitle,
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
            "mileage" -> MileageForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "barter" -> BarterForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
            "grant" -> GrantForm(
                editId = editId,
                isEditMode = isEditMode,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

// ── Expense Form ──

@Composable
private fun ExpenseForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: FinanceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var amount by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    var taxDeductible by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getExpenseById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                amount = it.amount.toString()
                vendor = it.vendor ?: ""
                category = it.category ?: ""
                paymentMethod = it.paymentMethod ?: ""
                taxDeductible = it.taxDeductible
                notes = it.notes ?: ""
            }
        }
    }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = FinanceGlow)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        DateFieldWithToggle(
            label = "Date",
            dateMillis = date,
            onDateChange = { date = it },
            useTodayDefault = !isEditMode,
            accentColor = FinanceGlow,
        )

        InputField(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        InputField(
            value = vendor,
            onValueChange = { vendor = it },
            label = { Text("Vendor (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        DropdownSelector(
            label = "Category",
            options = ModuleCatalogData.EXPENSE_CATEGORIES,
            selected = category,
            onSelect = { category = it },
            accentColor = FinanceGlow,
        )

        DropdownSelector(
            label = "Payment Method",
            options = ModuleCatalogData.PAYMENT_METHODS,
            selected = paymentMethod,
            onSelect = { paymentMethod = it },
            accentColor = FinanceGlow,
        )

        ToggleRow(
            label = "Tax Deductible",
            checked = taxDeductible,
            accentColor = FinanceGlow,
            onCheckedChange = { taxDeductible = it },
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Expense" else "Save Expense",
            onClick = {
                val parsedAmount = amount.toDoubleOrNull() ?: return@PrimaryButton
                val expense = Expense(
                    id = if (isEditMode) editId else 0,
                    date = date,
                    amount = parsedAmount,
                    vendor = vendor.ifBlank { null },
                    category = category.ifBlank { null },
                    paymentMethod = paymentMethod.ifBlank { null },
                    taxDeductible = taxDeductible,
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateExpense(expense) else viewModel.addExpense(expense)
                onBack()
            },
            enabled = amount.toDoubleOrNull() != null,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Revenue Form ──

@Composable
private fun RevenueForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: FinanceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var product by remember { mutableStateOf("") }
    var buyer by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    var salesChannel by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getRevenueById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                product = it.product ?: ""
                buyer = it.buyer ?: ""
                quantity = it.quantity?.toString() ?: ""
                unit = it.unit ?: ""
                unitPrice = it.unitPrice?.toString() ?: ""
                total = it.total?.toString() ?: ""
                paymentMethod = it.paymentMethod ?: ""
                salesChannel = it.salesChannel ?: ""
                notes = it.notes ?: ""
            }
        }
    }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = FinanceGlow)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        DateFieldWithToggle(
            label = "Date",
            dateMillis = date,
            onDateChange = { date = it },
            useTodayDefault = !isEditMode,
            accentColor = FinanceGlow,
        )

        InputField(
            value = product,
            onValueChange = { product = it },
            label = { Text("Product (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        InputField(
            value = buyer,
            onValueChange = { buyer = it },
            label = { Text("Buyer (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        InputField(
            value = quantity,
            onValueChange = { quantity = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Quantity (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        DropdownSelector(
            label = "Unit",
            options = ModuleCatalogData.UNITS,
            selected = unit,
            onSelect = { unit = it },
            accentColor = FinanceGlow,
        )

        InputField(
            value = unitPrice,
            onValueChange = { unitPrice = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Unit Price (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        InputField(
            value = total,
            onValueChange = { total = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Total") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        DropdownSelector(
            label = "Payment Method",
            options = ModuleCatalogData.PAYMENT_METHODS,
            selected = paymentMethod,
            onSelect = { paymentMethod = it },
            accentColor = FinanceGlow,
        )

        DropdownSelector(
            label = "Sales Channel",
            options = ModuleCatalogData.SALES_CHANNELS,
            selected = salesChannel,
            onSelect = { salesChannel = it },
            accentColor = FinanceGlow,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Revenue" else "Save Revenue",
            onClick = {
                val revenue = Revenue(
                    id = if (isEditMode) editId else 0,
                    date = date,
                    product = product.ifBlank { null },
                    buyer = buyer.ifBlank { null },
                    quantity = quantity.toDoubleOrNull(),
                    unit = unit.ifBlank { null },
                    unitPrice = unitPrice.toDoubleOrNull(),
                    total = total.toDoubleOrNull(),
                    paymentMethod = paymentMethod.ifBlank { null },
                    salesChannel = salesChannel.ifBlank { null },
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateRevenue(revenue) else viewModel.addRevenue(revenue)
                onBack()
            },
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Mileage Form ──

@Composable
private fun MileageForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: FinanceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var miles by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("0.67") }

    if (isEditMode) {
        val existing by viewModel.getMileageLogById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                origin = it.origin ?: ""
                destination = it.destination ?: ""
                purpose = it.purpose ?: ""
                miles = it.miles?.toString() ?: ""
                rate = it.rate?.toString() ?: "0.67"
            }
        }
    }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = FinanceGlow)

    val parsedMiles = miles.toDoubleOrNull() ?: 0.0
    val parsedRate = rate.toDoubleOrNull() ?: 0.0
    val calculatedDeduction = parsedMiles * parsedRate

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        DateFieldWithToggle(
            label = "Date",
            dateMillis = date,
            onDateChange = { date = it },
            useTodayDefault = !isEditMode,
            accentColor = FinanceGlow,
        )

        InputField(
            value = origin,
            onValueChange = { origin = it },
            label = { Text("Origin (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        InputField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        DropdownSelector(
            label = "Purpose",
            options = ModuleCatalogData.MILEAGE_PURPOSES,
            selected = purpose,
            onSelect = { purpose = it },
            accentColor = FinanceGlow,
        )

        InputField(
            value = miles,
            onValueChange = { miles = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Miles") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        InputField(
            value = rate,
            onValueChange = { rate = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Rate ($/mile)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Mileage" else "Save Mileage",
            onClick = {
                val mileageLog = MileageLog(
                    id = if (isEditMode) editId else 0,
                    date = date,
                    origin = origin.ifBlank { null },
                    destination = destination.ifBlank { null },
                    purpose = purpose.ifBlank { null },
                    miles = miles.toDoubleOrNull(),
                    rate = rate.toDoubleOrNull(),
                    totalDeduction = calculatedDeduction,
                )
                if (isEditMode) viewModel.updateMileageLog(mileageLog) else viewModel.addMileageLog(mileageLog)
                onBack()
            },
            enabled = miles.toDoubleOrNull() != null,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Barter Form ──

@Composable
private fun BarterForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: FinanceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var partner by remember { mutableStateOf("") }
    var givenItems by remember { mutableStateOf("") }
    var receivedItems by remember { mutableStateOf("") }
    var fairMarketValue by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getBarterTradeById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                date = it.date
                partner = it.partner ?: ""
                givenItems = it.givenItems ?: ""
                receivedItems = it.receivedItems ?: ""
                fairMarketValue = it.fairMarketValue?.toString() ?: ""
            }
        }
    }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = FinanceGlow)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        DateFieldWithToggle(
            label = "Date",
            dateMillis = date,
            onDateChange = { date = it },
            useTodayDefault = !isEditMode,
            accentColor = FinanceGlow,
        )

        InputField(
            value = partner,
            onValueChange = { partner = it },
            label = { Text("Partner (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        InputField(
            value = givenItems,
            onValueChange = { givenItems = it },
            label = { Text("Given Items") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        InputField(
            value = receivedItems,
            onValueChange = { receivedItems = it },
            label = { Text("Received Items") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        InputField(
            value = fairMarketValue,
            onValueChange = { fairMarketValue = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Fair Market Value (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Trade" else "Save Trade",
            onClick = {
                val trade = BarterTrade(
                    id = if (isEditMode) editId else 0,
                    date = date,
                    partner = partner.ifBlank { null },
                    givenItems = givenItems.ifBlank { null },
                    receivedItems = receivedItems.ifBlank { null },
                    fairMarketValue = fairMarketValue.toDoubleOrNull(),
                )
                if (isEditMode) viewModel.updateBarterTrade(trade) else viewModel.addBarterTrade(trade)
                onBack()
            },
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Grant Form ──

@Composable
private fun GrantForm(
    editId: Long,
    isEditMode: Boolean,
    viewModel: FinanceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var program by remember { mutableStateOf("") }
    var agency by remember { mutableStateOf("") }
    var applicationDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var status by remember { mutableStateOf("Applied") }
    var awardAmount by remember { mutableStateOf("") }
    var costSharePct by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (isEditMode) {
        val existing by viewModel.getGrantRecordById(editId).collectAsState(initial = null)
        LaunchedEffect(existing) {
            existing?.let {
                program = it.program ?: ""
                agency = it.agency ?: ""
                applicationDate = it.applicationDate ?: System.currentTimeMillis()
                status = it.status ?: "applied"
                awardAmount = it.awardAmount?.toString() ?: ""
                costSharePct = it.costSharePct?.toString() ?: ""
                notes = it.notes ?: ""
            }
        }
    }

    val fieldColors = AppTextFieldDefaults.colors(accentColor = FinanceGlow)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(AppSpacing.xs))

        InputField(
            value = program,
            onValueChange = { program = it },
            label = { Text("Program (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        InputField(
            value = agency,
            onValueChange = { agency = it },
            label = { Text("Agency (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        DateFieldWithToggle(
            label = "Application Date",
            dateMillis = applicationDate,
            onDateChange = { applicationDate = it },
            useTodayDefault = !isEditMode,
            accentColor = FinanceGlow,
        )

        DropdownSelector(
            label = "Status",
            options = listOf("Applied", "Awarded", "Denied", "Completed"),
            selected = status,
            onSelect = { status = it },
            accentColor = FinanceGlow,
        )

        InputField(
            value = awardAmount,
            onValueChange = { awardAmount = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Award Amount (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        InputField(
            value = costSharePct,
            onValueChange = { costSharePct = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Cost Share % (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        InputField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            accentColor = FinanceGlow,
            colors = fieldColors,
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        PrimaryButton(
            text = if (isEditMode) "Update Grant" else "Save Grant",
            onClick = {
                val grant = GrantRecord(
                    id = if (isEditMode) editId else 0,
                    program = program.ifBlank { null },
                    agency = agency.ifBlank { null },
                    applicationDate = applicationDate,
                    status = status,
                    awardAmount = awardAmount.toDoubleOrNull(),
                    costSharePct = costSharePct.toDoubleOrNull(),
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateGrantRecord(grant) else viewModel.addGrantRecord(grant)
                onBack()
            },
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
