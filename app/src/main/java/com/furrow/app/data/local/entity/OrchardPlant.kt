package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orchard_plants")
data class OrchardPlant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "location_gps_lat") val locationGpsLat: Double? = null,
    @ColumnInfo(name = "location_gps_lon") val locationGpsLon: Double? = null,
    val category: String,
    val species: String,
    val variety: String? = null,
    val rootstock: String? = null,
    @ColumnInfo(name = "pollinator_group") val pollinatorGroup: String? = null,
    @ColumnInfo(name = "source_nursery") val sourceNursery: String? = null,
    @ColumnInfo(name = "planting_year") val plantingYear: Int? = null,
    @ColumnInfo(name = "years_to_bearing") val yearsToBearing: Int? = null,
    @ColumnInfo(name = "chill_hours_required") val chillHoursRequired: Int? = null,
    @ColumnInfo(name = "zone_hardiness") val zoneHardiness: String? = null,
    val status: String = "dormant",
    @ColumnInfo(name = "cane_type") val caneType: String? = null,
    @ColumnInfo(name = "trellis_type") val trellisType: String? = null,
    val notes: String? = null,
)
