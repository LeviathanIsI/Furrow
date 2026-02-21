package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.ComplianceInspection
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplianceInspectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(complianceInspection: ComplianceInspection): Long

    @Update
    suspend fun update(complianceInspection: ComplianceInspection)

    @Delete
    suspend fun delete(complianceInspection: ComplianceInspection)

    @Query("SELECT * FROM compliance_inspections ORDER BY date DESC")
    fun getAll(): Flow<List<ComplianceInspection>>

    @Query("SELECT * FROM compliance_inspections WHERE id = :id")
    fun getById(id: Long): Flow<ComplianceInspection?>
}
