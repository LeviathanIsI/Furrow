package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "soil_amendment_logs",
    foreignKeys = [
        ForeignKey(
            entity = GardenBed::class,
            parentColumns = ["id"],
            childColumns = ["bed_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("bed_id")],
)
data class SoilAmendmentLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "bed_id") val bedId: Long,
    val date: Long,
    @ColumnInfo(name = "amendment_type") val amendmentType: String,
    @ColumnInfo(name = "npk_values") val npkValues: String? = null,
    val amount: String? = null,
    val method: String? = null,
    val notes: String? = null,
)
