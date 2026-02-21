package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animal_breed_info")
data class AnimalBreedInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val species: String,
    val name: String,
    val purpose: String? = null,
    val temperament: String? = null,
    val weight: String? = null,
    @ColumnInfo(name = "gestation_days") val gestationDays: Int? = null,
    @ColumnInfo(name = "eggs_per_year") val eggsPerYear: Int? = null,
    @ColumnInfo(name = "egg_color") val eggColor: String? = null,
    @ColumnInfo(name = "egg_size") val eggSize: String? = null,
    @ColumnInfo(name = "milk_production_gal_day") val milkProductionGalDay: Double? = null,
    @ColumnInfo(name = "fiber_yield_lbs") val fiberYieldLbs: Double? = null,
    @ColumnInfo(name = "mature_weight_lbs") val matureWeightLbs: Double? = null,
    @ColumnInfo(name = "dress_percentage") val dressPercentage: Double? = null,
    @ColumnInfo(name = "heat_tolerance") val heatTolerance: Int? = null,
    @ColumnInfo(name = "cold_tolerance") val coldTolerance: Int? = null,
    @ColumnInfo(name = "comb_type") val combType: String? = null,
    val broodiness: String? = null,
    @ColumnInfo(name = "climate_suitability") val climateSuitability: String? = null,
    val notes: String? = null,
    @ColumnInfo(name = "is_custom") val isCustom: Boolean = false,
)
