package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "crop_rotation_logs",
    foreignKeys = [
        ForeignKey(
            entity = GardenBed::class,
            parentColumns = ["id"],
            childColumns = ["bed_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("bed_id")],
)
data class CropRotationLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "bed_id") val bedId: Long,
    val year: Int,
    val season: String? = null,
    @ColumnInfo(name = "crop_family_planted") val cropFamilyPlanted: String,
)
