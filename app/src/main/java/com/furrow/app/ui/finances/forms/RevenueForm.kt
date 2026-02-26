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
import com.furrow.app.data.local.entity.Revenue
import com.furrow.app.data.ModuleCatalogData
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.DropdownSelector
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.finances.FinanceViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.FinanceGlow
import com.furrow.app.util.filterDecimal

@Composable
internal fun RevenueForm(
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
            zone = viewModel.zone,
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
            onValueChange = { quantity = it.filterDecimal() },
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
            onValueChange = { unitPrice = it.filterDecimal() },
            label = { Text("Unit Price (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            accentColor = FinanceGlow,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = fieldColors,
        )

        InputField(
            value = total,
            onValueChange = { total = it.filterDecimal() },
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
            },
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
