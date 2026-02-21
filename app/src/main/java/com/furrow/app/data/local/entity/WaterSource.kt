package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_sources")
data class WaterSource(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    @ColumnInfo(name = "capacity_gal") val capacityGal: Double? = null,
    @ColumnInfo(name = "flow_rate_gpm") val flowRateGpm: Double? = null,
    @ColumnInfo(name = "depth_ft") val depthFt: Double? = null,
    @ColumnInfo(name = "water_test_date") val waterTestDate: Long? = null,
    val ph: Double? = null,
    @ColumnInfo(name = "coliform_count") val coliformCount: Int? = null,
    @ColumnInfo(name = "nitrate_ppm") val nitratePpm: Double? = null,
    val hardness: Double? = null,
    val tds: Double? = null,
    @ColumnInfo(name = "pump_type") val pumpType: String? = null,
    @ColumnInfo(name = "pump_maintenance_date") val pumpMaintenanceDate: Long? = null,
)
