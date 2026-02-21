package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bloom_records",
    foreignKeys = [
        ForeignKey(
            entity = OrchardPlant::class,
            parentColumns = ["id"],
            childColumns = ["plant_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("plant_id")],
)
data class BloomRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "plant_id") val plantId: Long,
    val year: Int,
    @ColumnInfo(name = "first_bloom_date") val firstBloomDate: Long? = null,
    @ColumnInfo(name = "full_bloom_date") val fullBloomDate: Long? = null,
    @ColumnInfo(name = "petal_fall_date") val petalFallDate: Long? = null,
    @ColumnInfo(name = "fruit_set") val fruitSet: String? = null,
    @ColumnInfo(name = "thinning_date") val thinningDate: Long? = null,
    @ColumnInfo(name = "thinning_method") val thinningMethod: String? = null,
)
