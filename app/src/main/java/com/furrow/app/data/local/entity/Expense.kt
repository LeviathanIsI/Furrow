package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val vendor: String? = null,
    val amount: Double,
    val currency: String = "USD",
    val category: String? = null,
    val subcategory: String? = null,
    @ColumnInfo(name = "payment_method") val paymentMethod: String? = null,
    @ColumnInfo(name = "receipt_photo_url") val receiptPhotoUrl: String? = null,
    @ColumnInfo(name = "tax_deductible") val taxDeductible: Boolean = false,
    @ColumnInfo(name = "schedule_f_line") val scheduleFLine: String? = null,
    @ColumnInfo(name = "t2042_line") val t2042Line: String? = null,
    @ColumnInfo(name = "enterprise_id") val enterpriseId: Long? = null,
    val notes: String? = null,
)
