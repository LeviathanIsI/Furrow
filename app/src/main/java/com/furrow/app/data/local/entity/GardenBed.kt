package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garden_beds")
data class GardenBed(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val sizeGallons: Int? = null,
    val lengthFt: Double? = null,
    val widthFt: Double? = null,
    val soilType: String? = null,
    val sunExposure: String? = null,
    val notes: String? = null,
    val isActive: Boolean = true,
    val checkCompanionPlanting: Boolean = true,
)
