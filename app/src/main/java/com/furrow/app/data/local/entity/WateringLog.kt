package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watering_logs",
    foreignKeys = [
        ForeignKey(
            entity = GardenBed::class,
            parentColumns = ["id"],
            childColumns = ["bedId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("bedId")],
)
data class WateringLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bedId: Long,
    val date: Long,
    val amountGallons: Float? = null,
    val method: String? = null,
    val notes: String? = null,
    val photoUri: String? = null,
)
