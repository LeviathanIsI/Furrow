package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plant_variety",
    foreignKeys = [
        ForeignKey(
            entity = PlantInfo::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("plantId")],
)
data class PlantVariety(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val name: String,
    val daysToHarvestMin: Int?,
    val daysToHarvestMax: Int?,
    val description: String?,
    val notes: String?,
    val isCustom: Boolean = false,
)
