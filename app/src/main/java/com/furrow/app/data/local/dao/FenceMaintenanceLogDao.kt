package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.FenceMaintenanceLog
import kotlinx.coroutines.flow.Flow

@Dao
interface FenceMaintenanceLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: FenceMaintenanceLog): Long

    @Update
    suspend fun update(log: FenceMaintenanceLog)

    @Delete
    suspend fun delete(log: FenceMaintenanceLog)

    @Query("SELECT * FROM fence_maintenance_logs WHERE fence_id = :fenceId ORDER BY date DESC")
    fun getForFence(fenceId: Long): Flow<List<FenceMaintenanceLog>>

    @Query("SELECT * FROM fence_maintenance_logs WHERE id = :id")
    fun getById(id: Long): Flow<FenceMaintenanceLog?>
}
