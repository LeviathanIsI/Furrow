package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pasture_rotation_logs",
    foreignKeys = [
        ForeignKey(
            entity = Paddock::class,
            parentColumns = ["id"],
            childColumns = ["paddock_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("paddock_id")],
)
data class PastureRotationLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "paddock_id") val paddockId: Long,
    @ColumnInfo(name = "assigned_animal_group_id") val assignedAnimalGroupId: Long? = null,
    val species: String? = null,
    @ColumnInfo(name = "head_count") val headCount: Int? = null,
    @ColumnInfo(name = "move_in_date") val moveInDate: Long,
    @ColumnInfo(name = "move_out_date") val moveOutDate: Long? = null,
    @ColumnInfo(name = "planned_rest_days") val plannedRestDays: Int? = null,
    @ColumnInfo(name = "actual_rest_days") val actualRestDays: Int? = null,
    @ColumnInfo(name = "forage_height_in") val forageHeightIn: Double? = null,
    @ColumnInfo(name = "forage_condition") val forageCondition: Int? = null,
    @ColumnInfo(name = "stocking_rate") val stockingRate: Double? = null,
)
