package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.ComplianceDocument
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplianceDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(complianceDocument: ComplianceDocument): Long

    @Update
    suspend fun update(complianceDocument: ComplianceDocument)

    @Delete
    suspend fun delete(complianceDocument: ComplianceDocument)

    @Query("SELECT * FROM compliance_documents ORDER BY issue_date DESC")
    fun getAll(): Flow<List<ComplianceDocument>>

    @Query("SELECT * FROM compliance_documents WHERE id = :id")
    fun getById(id: Long): Flow<ComplianceDocument?>
}
