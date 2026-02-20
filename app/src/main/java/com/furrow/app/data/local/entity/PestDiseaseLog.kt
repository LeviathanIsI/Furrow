package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pest_disease_logs",
    foreignKeys = [
        ForeignKey(
            entity = Planting::class,
            parentColumns = ["id"],
            childColumns = ["plantingId"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = GardenBed::class,
            parentColumns = ["id"],
            childColumns = ["bedId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("plantingId"), Index("bedId")],
)
data class PestDiseaseLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantingId: Long? = null,
    val bedId: Long,
    val date: Long,
    val type: String,
    val name: String,
    val severity: String,
    val treatment: String? = null,
    val resolved: Boolean = false,
    val resolvedDate: Long? = null,
    val notes: String? = null,
)
