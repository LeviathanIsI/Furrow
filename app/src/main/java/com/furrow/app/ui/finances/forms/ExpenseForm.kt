package com.furrow.app.ui.finances.forms

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
import com.furrow.app.data.local.entity.Expense
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PhotoAttachment
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.ToggleRow
import com.furrow.app.ui.finances.FinanceViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.FinanceGlow
import com.furrow.app.util.filterDecimal

@Composable
internal fun ExpenseForm(
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
    var receiptPhoto by remember { mutableStateOf<String?>(null) }

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
                receiptPhoto = it.receiptPhotoUrl
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
            zone = viewModel.zone,
            useTodayDefault = !isEditMode,
            accentColor = FinanceGlow,
        )

        InputField(
            value = amount,
            onValueChange = { amount = it.filterDecimal() },
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

        PhotoAttachment(
            photoUri = receiptPhoto,
            onPhotoChanged = { receiptPhoto = it },
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
                    receiptPhotoUrl = receiptPhoto,
                    notes = notes.ifBlank { null },
                )
                if (isEditMode) viewModel.updateExpense(expense) else viewModel.addExpense(expense)
            },
            enabled = amount.toDoubleOrNull() != null,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
