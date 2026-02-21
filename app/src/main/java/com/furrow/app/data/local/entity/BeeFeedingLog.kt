package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bee_feeding_logs",
    foreignKeys = [
        ForeignKey(
            entity = Hive::class,
            parentColumns = ["id"],
            childColumns = ["hive_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("hive_id")],
)
data class BeeFeedingLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "hive_id") val hiveId: Long,
    val date: Long,
    @ColumnInfo(name = "feed_type") val feedType: String,
    val amount: String? = null,
    @ColumnInfo(name = "feeder_type") val feederType: String? = null,
    val notes: String? = null,
)
