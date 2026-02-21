package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fermenting_batches")
data class FermentingBatch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val product: String,
    val ingredients: String? = null,
    @ColumnInfo(name = "salt_pct") val saltPct: Double? = null,
    val method: String? = null,
    @ColumnInfo(name = "brine_ratio") val brineRatio: String? = null,
    @ColumnInfo(name = "vessel_type") val vesselType: String? = null,
    val airlock: Boolean = false,
    @ColumnInfo(name = "ferment_temp_f") val fermentTempF: Int? = null,
    @ColumnInfo(name = "start_date") val startDate: Long,
    @ColumnInfo(name = "end_date") val endDate: Long? = null,
    @ColumnInfo(name = "ph_readings") val phReadings: String? = null,
    @ColumnInfo(name = "taste_test_notes") val tasteTestNotes: String? = null,
    @ColumnInfo(name = "storage_method") val storageMethod: String? = null,
)
