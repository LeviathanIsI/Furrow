package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grant_records")
data class GrantRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val program: String? = null,
    val agency: String? = null,
    @ColumnInfo(name = "application_date") val applicationDate: Long? = null,
    val status: String? = null,
    @ColumnInfo(name = "award_amount") val awardAmount: Double? = null,
    @ColumnInfo(name = "cost_share_pct") val costSharePct: Double? = null,
    @ColumnInfo(name = "contract_start_date") val contractStartDate: Long? = null,
    @ColumnInfo(name = "contract_end_date") val contractEndDate: Long? = null,
    @ColumnInfo(name = "compliance_checks") val complianceChecks: String? = null,
    val notes: String? = null,
)
