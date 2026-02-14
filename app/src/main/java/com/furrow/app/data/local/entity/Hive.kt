package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hives")
data class Hive(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val installDate: Long,
    val queenStatus: String,
    val source: String,
    val notes: String? = null,
    val isActive: Boolean = true,
    val beeRace: String? = null,
)
