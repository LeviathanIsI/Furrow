package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fences")
data class Fence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    @ColumnInfo(name = "length_ft") val lengthFt: Double? = null,
    @ColumnInfo(name = "height_ft") val heightFt: Double? = null,
    @ColumnInfo(name = "install_date") val installDate: Long? = null,
    @ColumnInfo(name = "material_cost") val materialCost: Double? = null,
    @ColumnInfo(name = "labor_cost") val laborCost: Double? = null,
    @ColumnInfo(name = "condition_rating") val conditionRating: Int? = null,
    @ColumnInfo(name = "linked_paddock_ids") val linkedPaddockIds: String? = null,
)
