package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compost_bins")
data class CompostBin(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val location: String? = null,
    @ColumnInfo(name = "volume_cu_ft") val volumeCuFt: Double? = null,
    @ColumnInfo(name = "start_date") val startDate: Long? = null,
    @ColumnInfo(name = "turn_dates") val turnDates: String? = null,
    @ColumnInfo(name = "temperature_f") val temperatureF: Int? = null,
    @ColumnInfo(name = "moisture_pct") val moisturePct: Double? = null,
    @ColumnInfo(name = "maturity_stage") val maturityStage: String? = null,
    @ColumnInfo(name = "output_volume") val outputVolume: Double? = null,
    @ColumnInfo(name = "applied_to") val appliedTo: String? = null,
)
