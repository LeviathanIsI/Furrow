package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "milk_logs",
    foreignKeys = [
        ForeignKey(
            entity = Animal::class,
            parentColumns = ["id"],
            childColumns = ["animal_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("animal_id")],
)
data class MilkLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "animal_id") val animalId: Long,
    val date: Long,
    @ColumnInfo(name = "milk_am_lbs") val milkAmLbs: Double? = null,
    @ColumnInfo(name = "milk_pm_lbs") val milkPmLbs: Double? = null,
    @ColumnInfo(name = "total_daily") val totalDaily: Double? = null,
    @ColumnInfo(name = "butterfat_pct") val butterfatPct: Double? = null,
    @ColumnInfo(name = "somatic_cell_count") val somaticCellCount: Int? = null,
)
