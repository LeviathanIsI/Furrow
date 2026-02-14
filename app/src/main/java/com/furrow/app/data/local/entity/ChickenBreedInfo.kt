package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chicken_breed_info")
data class ChickenBreedInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val eggsPerYear: Int,
    val eggColor: String,
    val eggSize: String,
    val weight: String,
    val purpose: String,
    val temperament: String,
    val combType: String,
    val heatTolerance: Int,
    val coldTolerance: Int,
    val broodiness: String,
    val climateNotes: String? = null,
    val isCustom: Boolean = false,
)
