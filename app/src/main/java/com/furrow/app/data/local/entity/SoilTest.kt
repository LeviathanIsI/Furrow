package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soil_tests")
data class SoilTest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val location: String? = null,
    @ColumnInfo(name = "paddock_or_bed_id") val paddockOrBedId: Long? = null,
    @ColumnInfo(name = "depth_in") val depthIn: Double? = null,
    @ColumnInfo(name = "lab_name") val labName: String? = null,
    val ph: Double? = null,
    @ColumnInfo(name = "nitrogen_ppm") val nitrogenPpm: Double? = null,
    @ColumnInfo(name = "phosphorus_ppm") val phosphorusPpm: Double? = null,
    @ColumnInfo(name = "potassium_ppm") val potassiumPpm: Double? = null,
    @ColumnInfo(name = "organic_matter_pct") val organicMatterPct: Double? = null,
    val cec: Double? = null,
    @ColumnInfo(name = "calcium_ppm") val calciumPpm: Double? = null,
    @ColumnInfo(name = "magnesium_ppm") val magnesiumPpm: Double? = null,
    val texture: String? = null,
    val recommendations: String? = null,
    @ColumnInfo(name = "report_url") val reportUrl: String? = null,
    val photoUri: String? = null,
)
