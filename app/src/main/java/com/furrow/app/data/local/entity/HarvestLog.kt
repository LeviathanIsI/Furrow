package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "harvest_logs",
    foreignKeys = [
        ForeignKey(
            entity = Planting::class,
            parentColumns = ["id"],
            childColumns = ["plantingId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plantingId")]
)
data class HarvestLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantingId: Long,
    val date: Long,
    val amountOz: Double? = null,
    val count: Int? = null,
    val notes: String? = null,
)
