package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smoking_curing_batches")
data class SmokingCuringBatch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "meat_type") val meatType: String,
    val cut: String? = null,
    val weight: Double? = null,
    @ColumnInfo(name = "cure_recipe") val cureRecipe: String? = null,
    @ColumnInfo(name = "cure_type") val cureType: String? = null,
    val method: String? = null,
    @ColumnInfo(name = "cure_start") val cureStart: Long,
    @ColumnInfo(name = "cure_end") val cureEnd: Long? = null,
    @ColumnInfo(name = "smoke_wood") val smokeWood: String? = null,
    @ColumnInfo(name = "smoke_temp_f") val smokeTempF: Int? = null,
    @ColumnInfo(name = "internal_temp") val internalTemp: Int? = null,
    val duration: String? = null,
    @ColumnInfo(name = "final_weight") val finalWeight: Double? = null,
)
