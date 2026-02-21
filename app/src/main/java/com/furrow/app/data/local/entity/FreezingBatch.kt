package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "freezing_batches")
data class FreezingBatch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val item: String,
    @ColumnInfo(name = "quantity_lbs") val quantityLbs: Double? = null,
    @ColumnInfo(name = "packaging_method") val packagingMethod: String? = null,
    val blanched: Boolean = false,
    @ColumnInfo(name = "date_frozen") val dateFrozen: Long,
    @ColumnInfo(name = "freezer_id") val freezerId: String? = null,
    @ColumnInfo(name = "quality_duration_months") val qualityDurationMonths: Int? = null,
    @ColumnInfo(name = "use_by_date") val useByDate: Long? = null,
)
