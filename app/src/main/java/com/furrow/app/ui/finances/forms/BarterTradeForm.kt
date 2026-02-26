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
import com.furrow.app.data.local.entity.BarterTrade
import com.furrow.app.ui.components.AppTextFieldDefaults
import com.furrow.app.ui.components.DateFieldWithToggle
import com.furrow.app.ui.components.InputField
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.finances.FinanceViewModel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.FinanceGlow
import com.furrow.app.util.filterDecimal

@Composable
internal fun BarterTradeForm(
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
            zone = viewModel.zone,
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
            onValueChange = { fairMarketValue = it.filterDecimal() },
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
            },
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
