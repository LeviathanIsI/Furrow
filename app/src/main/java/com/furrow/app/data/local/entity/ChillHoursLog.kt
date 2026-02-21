package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chill_hours_logs")
data class ChillHoursLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val season: String,
    @ColumnInfo(name = "accumulated_chill_hours") val accumulatedChillHours: Int,
    @ColumnInfo(name = "model_used") val modelUsed: String? = null,
    @ColumnInfo(name = "variety_requirement") val varietyRequirement: Int? = null,
    @ColumnInfo(name = "predicted_bloom_quality") val predictedBloomQuality: String? = null,
)
