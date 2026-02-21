package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_logs")
data class WeatherLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    @ColumnInfo(name = "high_temp") val highTemp: Int? = null,
    @ColumnInfo(name = "low_temp") val lowTemp: Int? = null,
    @ColumnInfo(name = "rainfall_in") val rainfallIn: Double? = null,
    @ColumnInfo(name = "snowfall_in") val snowfallIn: Double? = null,
    @ColumnInfo(name = "humidity_pct") val humidityPct: Int? = null,
    @ColumnInfo(name = "wind_speed") val windSpeed: Int? = null,
    @ColumnInfo(name = "growing_degree_days") val growingDegreeDays: Double? = null,
    @ColumnInfo(name = "frost_flag") val frostFlag: Boolean = false,
    val notes: String? = null,
)
