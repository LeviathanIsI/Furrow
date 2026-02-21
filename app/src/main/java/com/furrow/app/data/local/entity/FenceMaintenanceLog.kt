package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fence_maintenance_logs",
    foreignKeys = [
        ForeignKey(
            entity = Fence::class,
            parentColumns = ["id"],
            childColumns = ["fence_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("fence_id")],
)
data class FenceMaintenanceLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "fence_id") val fenceId: Long,
    val date: Long,
    val type: String? = null,
    val cost: Double? = null,
    val notes: String? = null,
)
