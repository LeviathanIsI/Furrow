package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "enterprise_budgets")
data class EnterpriseBudget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "gross_revenue") val grossRevenue: Double? = null,
    @ColumnInfo(name = "direct_costs") val directCosts: Double? = null,
    @ColumnInfo(name = "allocated_overhead") val allocatedOverhead: Double? = null,
    @ColumnInfo(name = "net_profit") val netProfit: Double? = null,
    @ColumnInfo(name = "margin_pct") val marginPct: Double? = null,
    @ColumnInfo(name = "return_per_labor_hour") val returnPerLaborHour: Double? = null,
)
