package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planting_windows")
data class PlantingWindow(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantName: String,
    val zoneGroup: String,
    val startMonth: Int,
    val endMonth: Int,
    val method: String,
    val notes: String? = null,
)
