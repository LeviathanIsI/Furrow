package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "structure_maintenance_logs",
    foreignKeys = [
        ForeignKey(
            entity = Structure::class,
            parentColumns = ["id"],
            childColumns = ["structure_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("structure_id")],
)
data class StructureMaintenanceLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "structure_id") val structureId: Long,
    val date: Long,
    val type: String? = null,
    val cost: Double? = null,
    val notes: String? = null,
)
