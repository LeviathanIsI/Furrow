package com.furrow.app.ui.finances

import androidx.lifecycle.viewModelScope
import com.furrow.app.data.local.entity.BarterTrade
import com.furrow.app.data.local.entity.Expense
import com.furrow.app.data.local.entity.GrantRecord
import com.furrow.app.data.local.entity.MileageLog
import com.furrow.app.data.local.entity.Revenue
import com.furrow.app.data.repository.FinanceRepository
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.ui.FurrowViewModel
import com.furrow.app.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val repository: FinanceRepository,
    userProfileRepository: UserProfileRepository,
) : FurrowViewModel() {

    internal val zone: ZoneId = runBlocking {
        DateUtil.profileZone(userProfileRepository.getProfile().firstOrNull())
    }
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
    fun addExpense(e: Expense) { safeLaunch { repository.insertExpense(e) } }
    fun updateExpense(e: Expense) { safeLaunch { repository.updateExpense(e) } }
    fun deleteExpense(e: Expense) { safeLaunch { repository.deleteExpense(e) } }

    fun addRevenue(r: Revenue) { safeLaunch { repository.insertRevenue(r) } }
    fun updateRevenue(r: Revenue) { safeLaunch { repository.updateRevenue(r) } }
    fun deleteRevenue(r: Revenue) { safeLaunch { repository.deleteRevenue(r) } }

    fun addMileageLog(m: MileageLog) { safeLaunch { repository.insertMileageLog(m) } }
    fun updateMileageLog(m: MileageLog) { safeLaunch { repository.updateMileageLog(m) } }
    fun deleteMileageLog(m: MileageLog) { safeLaunch { repository.deleteMileageLog(m) } }

    fun addBarterTrade(b: BarterTrade) { safeLaunch { repository.insertBarterTrade(b) } }
    fun updateBarterTrade(b: BarterTrade) { safeLaunch { repository.updateBarterTrade(b) } }
    fun deleteBarterTrade(b: BarterTrade) { safeLaunch { repository.deleteBarterTrade(b) } }

    fun addGrantRecord(g: GrantRecord) { safeLaunch { repository.insertGrantRecord(g) } }
    fun updateGrantRecord(g: GrantRecord) { safeLaunch { repository.updateGrantRecord(g) } }
    fun deleteGrantRecord(g: GrantRecord) { safeLaunch { repository.deleteGrantRecord(g) } }
}
