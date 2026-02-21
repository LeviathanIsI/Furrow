package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "queen_records",
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
data class QueenRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "hive_id") val hiveId: Long,
    val breed: String? = null,
    val source: String? = null,
    @ColumnInfo(name = "introduction_date") val introductionDate: Long? = null,
    val marked: Boolean = false,
    @ColumnInfo(name = "mark_color") val markColor: String? = null,
    @ColumnInfo(name = "age_years") val ageYears: Int? = null,
    val status: String = "laying",
    @ColumnInfo(name = "parent_queen_id") val parentQueenId: Long? = null,
    val notes: String? = null,
)
