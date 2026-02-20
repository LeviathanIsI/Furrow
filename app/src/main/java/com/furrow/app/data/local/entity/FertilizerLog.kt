package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fertilizer_logs",
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
data class FertilizerLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bedId: Long,
    val date: Long,
    val productName: String? = null,
    val amount: String? = null,
    val notes: String? = null,
)
