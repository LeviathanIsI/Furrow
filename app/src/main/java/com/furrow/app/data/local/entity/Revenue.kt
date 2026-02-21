package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "revenues")
data class Revenue(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val buyer: String? = null,
    val product: String? = null,
    @ColumnInfo(name = "product_category") val productCategory: String? = null,
    val quantity: Double? = null,
    val unit: String? = null,
    @ColumnInfo(name = "unit_price") val unitPrice: Double? = null,
    val total: Double? = null,
    @ColumnInfo(name = "payment_method") val paymentMethod: String? = null,
    @ColumnInfo(name = "payment_status") val paymentStatus: String? = null,
    @ColumnInfo(name = "sales_channel") val salesChannel: String? = null,
    @ColumnInfo(name = "market_name") val marketName: String? = null,
    val notes: String? = null,
)
