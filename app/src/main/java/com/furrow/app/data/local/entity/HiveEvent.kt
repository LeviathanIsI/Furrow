package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hive_events")
data class HiveEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "event_type") val eventType: String,
    val date: Long,
    @ColumnInfo(name = "origin_hive_id") val originHiveId: Long? = null,
    @ColumnInfo(name = "destination_hive_id") val destinationHiveId: Long? = null,
    @ColumnInfo(name = "queen_status_post") val queenStatusPost: String? = null,
    val notes: String? = null,
)
