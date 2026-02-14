package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_info")
data class PlantInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val minZone: Int,
    val maxZone: Int,
    val daysToHarvestMin: Int,
    val daysToHarvestMax: Int,
    val sunRequirement: String,
    val waterFrequency: String,
    val containerSuitable: Boolean,
    val containerMinGallons: Int,
    val companionPlants: String,
    val incompatiblePlants: String,
    val notes: String? = null,
    val isCustom: Boolean = false,
)
