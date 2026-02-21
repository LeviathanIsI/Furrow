package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "breeding_records",
    foreignKeys = [
        ForeignKey(
            entity = Animal::class,
            parentColumns = ["id"],
            childColumns = ["sire_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = Animal::class,
            parentColumns = ["id"],
            childColumns = ["dam_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("sire_id"), Index("dam_id")],
)
data class BreedingRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "sire_id") val sireId: Long? = null,
    @ColumnInfo(name = "dam_id") val damId: Long? = null,
    @ColumnInfo(name = "breeding_date") val breedingDate: Long,
    val method: String? = null,
    @ColumnInfo(name = "due_date") val dueDate: Long? = null,
    @ColumnInfo(name = "birth_date") val birthDate: Long? = null,
    @ColumnInfo(name = "offspring_count") val offspringCount: Int? = null,
    @ColumnInfo(name = "offspring_sexes") val offspringSexes: String? = null,
    @ColumnInfo(name = "birth_weights") val birthWeights: String? = null,
    @ColumnInfo(name = "ease_score") val easeScore: Int? = null,
    val complications: String? = null,
    val notes: String? = null,
)
