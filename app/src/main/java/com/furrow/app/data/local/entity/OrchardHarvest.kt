package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orchard_harvests",
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
data class OrchardHarvest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "plant_id") val plantId: Long,
    val date: Long,
    @ColumnInfo(name = "yield_lbs") val yieldLbs: Double? = null,
    @ColumnInfo(name = "fruit_quality") val fruitQuality: String? = null,
    val brix: Double? = null,
    val destination: String? = null,
)
