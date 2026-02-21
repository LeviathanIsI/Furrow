package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_module_preferences")
data class UserModulePreference(
    @PrimaryKey
    @ColumnInfo(name = "module_name") val moduleName: String,
    val enabled: Boolean = false,
    @ColumnInfo(name = "display_order") val displayOrder: Int = 0,
)
