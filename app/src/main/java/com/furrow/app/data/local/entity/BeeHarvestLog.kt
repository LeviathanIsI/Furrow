package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bee_harvest_logs",
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
data class BeeHarvestLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "hive_id") val hiveId: Long,
    val date: Long,
    val product: String,
    @ColumnInfo(name = "yield_lbs") val yieldLbs: Double? = null,
    @ColumnInfo(name = "frames_pulled") val framesPulled: Int? = null,
    @ColumnInfo(name = "moisture_pct") val moisturePct: Double? = null,
    @ColumnInfo(name = "floral_source") val floralSource: String? = null,
    val notes: String? = null,
)
