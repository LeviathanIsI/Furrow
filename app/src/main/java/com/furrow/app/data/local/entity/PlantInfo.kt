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
    val plantingDepthInches: Float? = null,
    val spacingInches: Int? = null,
    val rowSpacingInches: Int? = null,
    val germinationDaysMin: Int? = null,
    val germinationDaysMax: Int? = null,
    val plantHeight: String? = null,
    val soilPH: String? = null,
    val frostTolerant: Boolean = false,
    val perennial: Boolean = false,
    val sowMethod: String? = null,
    val harvestMethod: String? = null,
    val isCustom: Boolean = false,

    // Germination & Starting
    val indoorStartWeeksBefore: Int? = null,
    val minSoilTempF: Int? = null,
    val seedSoakHours: Int? = null,
    val scarification: Boolean? = null,
    val startNotes: String? = null,

    // Growing Requirements
    val waterInchesPerWeek: Float? = null,
    val fertilizerNeeds: String? = null,
    val fertilizerType: String? = null,
    val fertilizerFrequency: String? = null,
    val mulchRecommended: Boolean? = null,
    val stakingRequired: Boolean? = null,
    val pruningNotes: String? = null,
    val thinningNotes: String? = null,
    val minTempF: Int? = null,
    val maxTempF: Int? = null,
    val heatTips: String? = null,
    val coldTips: String? = null,

    // Harvest Details
    val harvestIndicators: String? = null,
    val harvestFrequency: String? = null,
    val yieldPerPlant: String? = null,
    val storageNotes: String? = null,

    // Succession & Rotation
    val successionPlantingDays: Int? = null,
    val canSuccessionPlant: Boolean? = null,
    val rotationGroup: String? = null,
    val rotationNotes: String? = null,

    // Common Problems
    val commonPests: String? = null,
    val commonDiseases: String? = null,
    val pestNotes: String? = null,
)
