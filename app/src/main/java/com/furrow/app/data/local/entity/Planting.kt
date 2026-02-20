package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plantings",
    foreignKeys = [
        ForeignKey(
            entity = GardenBed::class,
            parentColumns = ["id"],
            childColumns = ["bedId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlantVariety::class,
            parentColumns = ["id"],
            childColumns = ["varietyId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("bedId"), Index("varietyId")],
)
data class Planting(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bedId: Long,
    val plantName: String,
    val variety: String? = null,
    val varietyId: Long? = null,
    val datePlanted: Long,
    val dateTransplanted: Long? = null,
    val source: String,
    val status: String = "growing",
    val notes: String? = null,
    // Germination tracking
    val germinationDate: Long? = null,
    val expectedGerminationDate: Long? = null,
    val seedsPlanted: Int? = null,
    val seedsSprouted: Int? = null,
)
