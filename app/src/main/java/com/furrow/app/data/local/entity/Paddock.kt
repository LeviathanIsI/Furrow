package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paddocks")
data class Paddock(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val acreage: Double? = null,
    @ColumnInfo(name = "forage_type") val forageType: String? = null,
    val notes: String? = null,
)
