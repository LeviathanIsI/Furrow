package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seed_inventory")
data class SeedInventory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val species: String,
    val variety: String? = null,
    val supplier: String? = null,
    @ColumnInfo(name = "lot_number") val lotNumber: String? = null,
    @ColumnInfo(name = "purchase_date") val purchaseDate: Long? = null,
    val quantity: String? = null,
    @ColumnInfo(name = "germination_rate") val germinationRate: Int? = null,
    @ColumnInfo(name = "expiration_date") val expirationDate: Long? = null,
    @ColumnInfo(name = "storage_location") val storageLocation: String? = null,
    val cost: Double? = null,
    @ColumnInfo(name = "saved_seed") val savedSeed: Boolean = false,
)
