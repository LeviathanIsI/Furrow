package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gardens")
data class Garden(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "gps_lat") val gpsLat: Double? = null,
    @ColumnInfo(name = "gps_lon") val gpsLon: Double? = null,
    @ColumnInfo(name = "usda_zone") val usdaZone: String? = null,
    @ColumnInfo(name = "last_frost_date") val lastFrostDate: String? = null,
    @ColumnInfo(name = "first_frost_date") val firstFrostDate: String? = null,
    @ColumnInfo(name = "growing_season_days") val growingSeasonDays: Int? = null,
)
