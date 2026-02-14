package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "egg_logs")
data class EggLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val count: Int,
    val notes: String? = null,
)
