package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.BarterTradeDao
import com.furrow.app.data.local.dao.DepreciationAssetDao
import com.furrow.app.data.local.dao.EnterpriseBudgetDao
import com.furrow.app.data.local.dao.ExpenseDao
import com.furrow.app.data.local.dao.GrantRecordDao
import com.furrow.app.data.local.dao.MileageLogDao
import com.furrow.app.data.local.dao.RevenueDao
import com.furrow.app.data.local.entity.BarterTrade
import com.furrow.app.data.local.entity.DepreciationAsset
import com.furrow.app.data.local.entity.EnterpriseBudget
import com.furrow.app.data.local.entity.Expense
import com.furrow.app.data.local.entity.GrantRecord
import com.furrow.app.data.local.entity.MileageLog
import com.furrow.app.data.local.entity.Revenue
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val revenueDao: RevenueDao,
    private val mileageLogDao: MileageLogDao,
    private val barterTradeDao: BarterTradeDao,
    private val grantRecordDao: GrantRecordDao,
    private val depreciationAssetDao: DepreciationAssetDao,
    private val enterpriseBudgetDao: EnterpriseBudgetDao,
) {

    // --- Expenses ---

    suspend fun insertExpense(expense: Expense): Long = expenseDao.insert(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.update(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.delete(expense)

    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAll()

    fun getExpenseById(id: Long): Flow<Expense?> = expenseDao.getById(id)

    fun getExpensesForDateRange(startMillis: Long, endMillis: Long): Flow<List<Expense>> =
        expenseDao.getForDateRange(startMillis, endMillis)

    fun getExpensesByCategory(category: String): Flow<List<Expense>> =
        expenseDao.getByCategory(category)

    fun getExpenseTotalForDateRange(startMillis: Long, endMillis: Long): Flow<Double?> =
        expenseDao.getTotalForDateRange(startMillis, endMillis)

    // --- Revenues ---

    suspend fun insertRevenue(revenue: Revenue): Long = revenueDao.insert(revenue)

    suspend fun updateRevenue(revenue: Revenue) = revenueDao.update(revenue)

    suspend fun deleteRevenue(revenue: Revenue) = revenueDao.delete(revenue)

    fun getAllRevenues(): Flow<List<Revenue>> = revenueDao.getAll()

    fun getRevenueById(id: Long): Flow<Revenue?> = revenueDao.getById(id)

    fun getRevenuesForDateRange(startMillis: Long, endMillis: Long): Flow<List<Revenue>> =
        revenueDao.getForDateRange(startMillis, endMillis)

    fun getRevenueTotalForDateRange(startMillis: Long, endMillis: Long): Flow<Double?> =
        revenueDao.getTotalForDateRange(startMillis, endMillis)

    // --- Mileage Logs ---

    suspend fun insertMileageLog(mileageLog: MileageLog): Long = mileageLogDao.insert(mileageLog)

    suspend fun updateMileageLog(mileageLog: MileageLog) = mileageLogDao.update(mileageLog)

    suspend fun deleteMileageLog(mileageLog: MileageLog) = mileageLogDao.delete(mileageLog)

    fun getAllMileageLogs(): Flow<List<MileageLog>> = mileageLogDao.getAll()

    fun getMileageLogById(id: Long): Flow<MileageLog?> = mileageLogDao.getById(id)

    // --- Barter Trades ---

    suspend fun insertBarterTrade(barterTrade: BarterTrade): Long =
        barterTradeDao.insert(barterTrade)

    suspend fun updateBarterTrade(barterTrade: BarterTrade) = barterTradeDao.update(barterTrade)

    suspend fun deleteBarterTrade(barterTrade: BarterTrade) = barterTradeDao.delete(barterTrade)

    fun getAllBarterTrades(): Flow<List<BarterTrade>> = barterTradeDao.getAll()

    fun getBarterTradeById(id: Long): Flow<BarterTrade?> = barterTradeDao.getById(id)

    // --- Grant Records ---

    suspend fun insertGrantRecord(grantRecord: GrantRecord): Long =
        grantRecordDao.insert(grantRecord)

    suspend fun updateGrantRecord(grantRecord: GrantRecord) = grantRecordDao.update(grantRecord)

    suspend fun deleteGrantRecord(grantRecord: GrantRecord) = grantRecordDao.delete(grantRecord)

    fun getAllGrantRecords(): Flow<List<GrantRecord>> = grantRecordDao.getAll()

    fun getGrantRecordById(id: Long): Flow<GrantRecord?> = grantRecordDao.getById(id)

    fun getActiveGrantRecords(): Flow<List<GrantRecord>> = grantRecordDao.getActive()

    // --- Depreciation Assets ---

    suspend fun insertDepreciationAsset(depreciationAsset: DepreciationAsset): Long =
        depreciationAssetDao.insert(depreciationAsset)

    suspend fun updateDepreciationAsset(depreciationAsset: DepreciationAsset) =
        depreciationAssetDao.update(depreciationAsset)

    suspend fun deleteDepreciationAsset(depreciationAsset: DepreciationAsset) =
        depreciationAssetDao.delete(depreciationAsset)

    fun getAllDepreciationAssets(): Flow<List<DepreciationAsset>> = depreciationAssetDao.getAll()

    fun getDepreciationAssetById(id: Long): Flow<DepreciationAsset?> =
        depreciationAssetDao.getById(id)

    // --- Enterprise Budgets ---

    suspend fun insertEnterpriseBudget(enterpriseBudget: EnterpriseBudget): Long =
        enterpriseBudgetDao.insert(enterpriseBudget)

    suspend fun updateEnterpriseBudget(enterpriseBudget: EnterpriseBudget) =
        enterpriseBudgetDao.update(enterpriseBudget)

    suspend fun deleteEnterpriseBudget(enterpriseBudget: EnterpriseBudget) =
        enterpriseBudgetDao.delete(enterpriseBudget)

    fun getAllEnterpriseBudgets(): Flow<List<EnterpriseBudget>> = enterpriseBudgetDao.getAll()

    fun getEnterpriseBudgetById(id: Long): Flow<EnterpriseBudget?> =
        enterpriseBudgetDao.getById(id)
}
