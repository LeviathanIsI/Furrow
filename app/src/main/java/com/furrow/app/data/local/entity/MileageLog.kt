package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mileage_logs")
data class MileageLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val origin: String? = null,
    val destination: String? = null,
    val purpose: String? = null,
    val miles: Double? = null,
    val rate: Double? = null,
    @ColumnInfo(name = "total_deduction") val totalDeduction: Double? = null,
)
