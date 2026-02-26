package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "health_records",
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
data class HealthRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "animal_id") val animalId: Long,
    val date: Long,
    val type: String,
    @ColumnInfo(name = "product_name") val productName: String? = null,
    val dosage: String? = null,
    val route: String? = null,
    @ColumnInfo(name = "withdrawal_period_days") val withdrawalPeriodDays: Int? = null,
    @ColumnInfo(name = "withdrawal_end_date") val withdrawalEndDate: Long? = null,
    @ColumnInfo(name = "vet_name") val vetName: String? = null,
    val diagnosis: String? = null,
    @ColumnInfo(name = "body_condition_score") val bodyConditionScore: Int? = null,
    @ColumnInfo(name = "famacha_score") val famachaScore: Int? = null,
    val cost: Double? = null,
    val notes: String? = null,
    val photoUri: String? = null,
)
