package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "depreciation_assets")
data class DepreciationAsset(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val asset: String,
    @ColumnInfo(name = "purchase_date") val purchaseDate: Long,
    @ColumnInfo(name = "cost_basis") val costBasis: Double,
    @ColumnInfo(name = "useful_life") val usefulLife: Int,
    val method: String? = null,
    @ColumnInfo(name = "annual_deduction") val annualDeduction: Double? = null,
    @ColumnInfo(name = "accumulated_depreciation") val accumulatedDepreciation: Double? = null,
)
