package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "structures")
data class Structure(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val name: String,
    val dimensions: String? = null,
    @ColumnInfo(name = "sq_ft") val sqFt: Double? = null,
    @ColumnInfo(name = "build_date") val buildDate: Long? = null,
    val cost: Double? = null,
    @ColumnInfo(name = "condition_rating") val conditionRating: Int? = null,
    @ColumnInfo(name = "maintenance_schedule") val maintenanceSchedule: String? = null,
    @ColumnInfo(name = "last_maintenance") val lastMaintenance: Long? = null,
    @ColumnInfo(name = "next_maintenance") val nextMaintenance: Long? = null,
    val photos: String? = null,
)
