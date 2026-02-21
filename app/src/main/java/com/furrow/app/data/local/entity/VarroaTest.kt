package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "varroa_tests",
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
data class VarroaTest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "hive_id") val hiveId: Long,
    val date: Long,
    @ColumnInfo(name = "test_method") val testMethod: String,
    @ColumnInfo(name = "sample_size") val sampleSize: Int = 300,
    @ColumnInfo(name = "mite_count") val miteCount: Int,
    @ColumnInfo(name = "infestation_rate") val infestationRate: Double? = null,
)
