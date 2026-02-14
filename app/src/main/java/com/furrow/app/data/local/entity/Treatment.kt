package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "treatments",
    foreignKeys = [
        ForeignKey(
            entity = Hive::class,
            parentColumns = ["id"],
            childColumns = ["hiveId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("hiveId")]
)
data class Treatment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hiveId: Long,
    val date: Long,
    val type: String,
    val method: String? = null,
    val dose: String? = null,
    val endDate: Long? = null,
    val notes: String? = null,
)
