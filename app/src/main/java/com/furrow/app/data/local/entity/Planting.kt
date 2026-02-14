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
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bedId")]
)
data class Planting(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bedId: Long,
    val plantName: String,
    val variety: String? = null,
    val datePlanted: Long,
    val dateTransplanted: Long? = null,
    val source: String,
    val status: String = "growing",
    val notes: String? = null,
)
