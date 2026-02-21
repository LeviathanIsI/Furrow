package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales_trackers")
data class SalesTracker(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "product_type") val productType: String,
    val year: Int,
    @ColumnInfo(name = "running_total") val runningTotal: Double = 0.0,
    @ColumnInfo(name = "annual_cap") val annualCap: Double? = null,
    @ColumnInfo(name = "threshold_alert_pct") val thresholdAlertPct: Double? = null,
    val notes: String? = null,
)
