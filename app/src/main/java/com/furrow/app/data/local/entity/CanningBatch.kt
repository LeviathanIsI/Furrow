package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "canning_batches")
data class CanningBatch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "recipe_name") val recipeName: String,
    @ColumnInfo(name = "recipe_source") val recipeSource: String? = null,
    @ColumnInfo(name = "usda_approved") val usdaApproved: Boolean = false,
    val method: String? = null,
    @ColumnInfo(name = "canner_type") val cannerType: String? = null,
    @ColumnInfo(name = "jar_size") val jarSize: String? = null,
    @ColumnInfo(name = "jar_count") val jarCount: Int? = null,
    @ColumnInfo(name = "processing_time_min") val processingTimeMin: Int? = null,
    @ColumnInfo(name = "pressure_psi") val pressurePsi: Int? = null,
    @ColumnInfo(name = "user_elevation") val userElevation: Int? = null,
    val acidity: String? = null,
    @ColumnInfo(name = "added_acid") val addedAcid: String? = null,
    @ColumnInfo(name = "ingredients_list") val ingredientsList: String? = null,
    @ColumnInfo(name = "seal_check") val sealCheck: String? = null,
    @ColumnInfo(name = "seal_check_date") val sealCheckDate: Long? = null,
    @ColumnInfo(name = "date_processed") val dateProcessed: Long,
    @ColumnInfo(name = "storage_location") val storageLocation: String? = null,
    @ColumnInfo(name = "use_by_date") val useByDate: Long? = null,
    val photos: String? = null,
)
