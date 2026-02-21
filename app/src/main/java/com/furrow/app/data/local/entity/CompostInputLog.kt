package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "compost_input_logs",
    foreignKeys = [
        ForeignKey(
            entity = CompostBin::class,
            parentColumns = ["id"],
            childColumns = ["bin_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("bin_id")],
)
data class CompostInputLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "bin_id") val binId: Long,
    val date: Long,
    val material: String? = null,
    val volume: Double? = null,
    @ColumnInfo(name = "carbon_nitrogen") val carbonNitrogen: String? = null,
)
