package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class Animal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val species: String,
    val breed: String,
    val sex: String = "unknown",
    val dob: Long? = null,
    val name: String? = null,
    @ColumnInfo(name = "tag_id") val tagId: String? = null,
    @ColumnInfo(name = "acquisition_date") val acquisitionDate: Long,
    val source: String? = null,
    val cost: Double? = null,
    val status: String = "active",
    val purpose: String? = null,
    @ColumnInfo(name = "photo_url") val photoUrl: String? = null,
    @ColumnInfo(name = "color_markings") val colorMarkings: String? = null,
    @ColumnInfo(name = "registration_number") val registrationNumber: String? = null,
    @ColumnInfo(name = "housing_location") val housingLocation: String? = null,
    @ColumnInfo(name = "flock_id") val flockId: String? = null,
    @ColumnInfo(name = "flock_size") val flockSize: Int? = null,
    val notes: String? = null,
)
