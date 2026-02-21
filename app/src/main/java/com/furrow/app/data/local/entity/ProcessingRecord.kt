package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "processing_records",
    foreignKeys = [
        ForeignKey(
            entity = Animal::class,
            parentColumns = ["id"],
            childColumns = ["animal_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("animal_id")],
)
data class ProcessingRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "animal_id") val animalId: Long? = null,
    val date: Long,
    @ColumnInfo(name = "live_weight") val liveWeight: Double? = null,
    @ColumnInfo(name = "hanging_weight") val hangingWeight: Double? = null,
    @ColumnInfo(name = "cut_weight") val cutWeight: Double? = null,
    @ColumnInfo(name = "processor_name") val processorName: String? = null,
    val cost: Double? = null,
    @ColumnInfo(name = "cut_list") val cutList: String? = null,
    @ColumnInfo(name = "aging_days") val agingDays: Int? = null,
    @ColumnInfo(name = "pelt_saved") val peltSaved: Boolean = false,
    val tanner: String? = null,
    @ColumnInfo(name = "tanning_cost") val tanningCost: Double? = null,
    val notes: String? = null,
)
