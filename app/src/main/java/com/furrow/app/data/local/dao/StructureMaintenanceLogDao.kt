package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.StructureMaintenanceLog
import kotlinx.coroutines.flow.Flow

@Dao
interface StructureMaintenanceLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: StructureMaintenanceLog): Long

    @Update
    suspend fun update(log: StructureMaintenanceLog)

    @Delete
    suspend fun delete(log: StructureMaintenanceLog)

    @Query("SELECT * FROM structure_maintenance_logs WHERE structure_id = :structureId ORDER BY date DESC")
    fun getForStructure(structureId: Long): Flow<List<StructureMaintenanceLog>>

    @Query("SELECT * FROM structure_maintenance_logs WHERE id = :id")
    fun getById(id: Long): Flow<StructureMaintenanceLog?>
}
