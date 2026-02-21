package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pruning_logs",
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
data class PruningLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "plant_id") val plantId: Long,
    val date: Long,
    @ColumnInfo(name = "pruning_type") val pruningType: String? = null,
    val method: String? = null,
    @ColumnInfo(name = "before_photo") val beforePhoto: String? = null,
    @ColumnInfo(name = "after_photo") val afterPhoto: String? = null,
    val notes: String? = null,
)
