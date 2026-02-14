package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_logs")
data class FeedLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val feedType: String,
    val amountLbs: Double? = null,
    val notes: String? = null,
)
