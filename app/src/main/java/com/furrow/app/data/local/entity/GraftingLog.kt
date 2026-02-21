package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "grafting_logs",
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
data class GraftingLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "plant_id") val plantId: Long,
    val date: Long,
    @ColumnInfo(name = "scion_source") val scionSource: String? = null,
    @ColumnInfo(name = "graft_type") val graftType: String? = null,
    val success: String? = null,
    val photos: String? = null,
)
