package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val zipCode: String,
    val hardinessZone: String,
    val zoneGroup: String,
    val state: String,
    val climateCategory: String,
    val lastFrostDate: String,
    val firstFrostDate: String,
    val timezone: String = "",
)
