package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apiaries")
data class Apiary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "gps_lat") val gpsLat: Double? = null,
    @ColumnInfo(name = "gps_lon") val gpsLon: Double? = null,
    val elevation: Int? = null,
    @ColumnInfo(name = "forage_notes") val forageNotes: String? = null,
)
