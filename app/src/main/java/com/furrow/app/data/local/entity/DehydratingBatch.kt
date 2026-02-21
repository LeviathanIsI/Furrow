package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dehydrating_batches")
data class DehydratingBatch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val product: String,
    @ColumnInfo(name = "weight_before_lbs") val weightBeforeLbs: Double? = null,
    @ColumnInfo(name = "weight_after_lbs") val weightAfterLbs: Double? = null,
    @ColumnInfo(name = "dehydrator_temp_f") val dehydratorTempF: Int? = null,
    @ColumnInfo(name = "drying_time_hrs") val dryingTimeHrs: Double? = null,
    @ColumnInfo(name = "pre_treatment") val preTreatment: String? = null,
    @ColumnInfo(name = "storage_method") val storageMethod: String? = null,
    val date: Long,
    @ColumnInfo(name = "use_by_estimate") val useByEstimate: Long? = null,
)
