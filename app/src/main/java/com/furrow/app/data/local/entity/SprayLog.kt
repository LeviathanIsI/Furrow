package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "spray_logs",
    foreignKeys = [
        ForeignKey(
            entity = OrchardPlant::class,
            parentColumns = ["id"],
            childColumns = ["plant_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("plant_id")],
)
data class SprayLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "plant_id") val plantId: Long,
    val date: Long,
    val product: String? = null,
    val timing: String? = null,
    @ColumnInfo(name = "active_ingredient") val activeIngredient: String? = null,
    val concentration: String? = null,
    @ColumnInfo(name = "pre_harvest_interval_days") val preHarvestIntervalDays: Int? = null,
    @ColumnInfo(name = "organic_approved") val organicApproved: Boolean = false,
    @ColumnInfo(name = "weather_conditions") val weatherConditions: String? = null,
)
