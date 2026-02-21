package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class Property(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "total_acreage") val totalAcreage: Double? = null,
    @ColumnInfo(name = "parcel_ids") val parcelIds: String? = null,
    val zoning: String? = null,
    val county: String? = null,
    @ColumnInfo(name = "state_province") val stateProvince: String? = null,
    val elevation: Int? = null,
    @ColumnInfo(name = "purchase_date") val purchaseDate: Long? = null,
    @ColumnInfo(name = "assessed_value") val assessedValue: Double? = null,
    @ColumnInfo(name = "tax_parcel_number") val taxParcelNumber: String? = null,
)
