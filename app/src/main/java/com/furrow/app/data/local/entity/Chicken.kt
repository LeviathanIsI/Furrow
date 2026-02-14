package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chickens")
data class Chicken(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String? = null,
    val breed: String,
    val dateAcquired: Long,
    val dateOfBirth: Long? = null,
    val status: String = "active",
    val notes: String? = null,
)
