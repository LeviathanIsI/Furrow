package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compliance_inspections")
data class ComplianceInspection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val inspector: String? = null,
    val agency: String? = null,
    val outcome: String? = null,
    @ColumnInfo(name = "follow_up_items") val followUpItems: String? = null,
    val notes: String? = null,
)
