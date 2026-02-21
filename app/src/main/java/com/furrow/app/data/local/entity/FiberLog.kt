package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fiber_logs",
    foreignKeys = [
        ForeignKey(
            entity = Animal::class,
            parentColumns = ["id"],
            childColumns = ["animal_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("animal_id")],
)
data class FiberLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "animal_id") val animalId: Long,
    @ColumnInfo(name = "shearing_date") val shearingDate: Long,
    @ColumnInfo(name = "fleece_weight") val fleeceWeight: Double? = null,
    @ColumnInfo(name = "fiber_grade") val fiberGrade: String? = null,
    @ColumnInfo(name = "staple_length") val stapleLength: Double? = null,
)
