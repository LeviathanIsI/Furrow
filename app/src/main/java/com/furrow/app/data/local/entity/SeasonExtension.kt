package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "season_extensions",
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
data class SeasonExtension(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "equipment_type") val equipmentType: String,
    @ColumnInfo(name = "bed_id") val bedId: Long,
    @ColumnInfo(name = "deploy_date") val deployDate: Long? = null,
    @ColumnInfo(name = "remove_date") val removeDate: Long? = null,
    @ColumnInfo(name = "temp_inside") val tempInside: Int? = null,
    val notes: String? = null,
)
