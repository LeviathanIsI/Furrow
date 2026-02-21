package com.furrow.app.ui.finances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.local.entity.BarterTrade
import com.furrow.app.data.local.entity.Expense
import com.furrow.app.data.local.entity.GrantRecord
import com.furrow.app.data.local.entity.MileageLog
import com.furrow.app.data.local.entity.Revenue
import com.furrow.app.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val repository: FinanceRepository,
) : ViewModel() {

    private val zone = ZoneId.systemDefault()
    private val today = LocalDate.now(zone)
    private val monthStart = today.withDayOfMonth(1).atStartOfDay(zone).toInstant().toEpochMilli()
    private val monthEnd = today.plusMonths(1).withDayOfMonth(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1

    val expenses: StateFlow<List<Expense>> = repository.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val revenues: StateFlow<List<Revenue>> = repository.getAllRevenues()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mileageLogs: StateFlow<List<MileageLog>> = repository.getAllMileageLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val barterTrades: StateFlow<List<BarterTrade>> = repository.getAllBarterTrades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val grantRecords: StateFlow<List<GrantRecord>> = repository.getAllGrantRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Monthly totals --
    val monthExpenses: StateFlow<Double> = repository.getExpenseTotalForDateRange(monthStart, monthEnd)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthRevenue: StateFlow<Double> = repository.getRevenueTotalForDateRange(monthStart, monthEnd)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netIncome: StateFlow<Double> = combine(monthRevenue, monthExpenses) { rev, exp ->
        (rev ?: 0.0) - (exp ?: 0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // -- Lookups for edit mode --
    fun getExpenseById(id: Long) = repository.getExpenseById(id)
    fun getRevenueById(id: Long) = repository.getRevenueById(id)
    fun getMileageLogById(id: Long) = repository.getMileageLogById(id)
    fun getBarterTradeById(id: Long) = repository.getBarterTradeById(id)
    fun getGrantRecordById(id: Long) = repository.getGrantRecordById(id)

    // -- Actions --
    fun addExpense(e: Expense) { viewModelScope.launch { repository.insertExpense(e) } }
    fun updateExpense(e: Expense) { viewModelScope.launch { repository.updateExpense(e) } }
    fun deleteExpense(e: Expense) { viewModelScope.launch { repository.deleteExpense(e) } }

    fun addRevenue(r: Revenue) { viewModelScope.launch { repository.insertRevenue(r) } }
    fun updateRevenue(r: Revenue) { viewModelScope.launch { repository.updateRevenue(r) } }
    fun deleteRevenue(r: Revenue) { viewModelScope.launch { repository.deleteRevenue(r) } }

    fun addMileageLog(m: MileageLog) { viewModelScope.launch { repository.insertMileageLog(m) } }
    fun updateMileageLog(m: MileageLog) { viewModelScope.launch { repository.updateMileageLog(m) } }
    fun deleteMileageLog(m: MileageLog) { viewModelScope.launch { repository.deleteMileageLog(m) } }

    fun addBarterTrade(b: BarterTrade) { viewModelScope.launch { repository.insertBarterTrade(b) } }
    fun updateBarterTrade(b: BarterTrade) { viewModelScope.launch { repository.updateBarterTrade(b) } }
    fun deleteBarterTrade(b: BarterTrade) { viewModelScope.launch { repository.deleteBarterTrade(b) } }

    fun addGrantRecord(g: GrantRecord) { viewModelScope.launch { repository.insertGrantRecord(g) } }
    fun updateGrantRecord(g: GrantRecord) { viewModelScope.launch { repository.updateGrantRecord(g) } }
    fun deleteGrantRecord(g: GrantRecord) { viewModelScope.launch { repository.deleteGrantRecord(g) } }
}
