package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pantry_items")
data class PantryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val quantity: Double? = null,
    val unit: String? = null,
    @ColumnInfo(name = "batch_id") val batchId: Long? = null,
    @ColumnInfo(name = "storage_location") val storageLocation: String? = null,
    @ColumnInfo(name = "date_produced") val dateProduced: Long? = null,
    @ColumnInfo(name = "expiration_date") val expirationDate: Long? = null,
    val status: String = "in_stock",
    val source: String? = null,
)
