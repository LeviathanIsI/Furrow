package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_logs")
data class FeedLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "animal_id") val animalId: Long? = null,
    @ColumnInfo(name = "flock_id") val flockId: String? = null,
    val date: Long,
    @ColumnInfo(name = "feed_type") val feedType: String,
    @ColumnInfo(name = "quantity_lbs") val quantityLbs: Double? = null,
    val cost: Double? = null,
    val supplier: String? = null,
    val notes: String? = null,
)
