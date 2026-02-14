package com.furrow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inspections",
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
data class Inspection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hiveId: Long,
    val date: Long,
    val temperament: String? = null,
    val queenSeen: Boolean = false,
    val queenCells: Boolean = false,
    val eggsLarvae: Boolean = false,
    val broodPattern: String? = null,
    val honeyStores: String? = null,
    val pollenStores: String? = null,
    val pestsSigns: String? = null,
    val diseasesSigns: String? = null,
    val frameCount: Int? = null,
    val addedSupers: Int? = null,
    val removedSupers: Int? = null,
    val feeding: String? = null,
    val notes: String? = null,
    val weatherTemp: Int? = null,
    val weatherCondition: String? = null,
)
