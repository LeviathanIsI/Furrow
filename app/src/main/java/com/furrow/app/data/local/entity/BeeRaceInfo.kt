package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bee_race_info")
data class BeeRaceInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val temperament: String,
    val honeyProduction: Int,
    val miteResistance: Int,
    val swarmingTendency: Int,
    val overwinteringAbility: Int,
    val climateSuitability: String,
    val notes: String? = null,
    val isCustom: Boolean = false,
)
