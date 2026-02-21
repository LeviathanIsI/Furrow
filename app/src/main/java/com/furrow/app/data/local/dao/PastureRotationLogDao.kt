package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.PastureRotationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface PastureRotationLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: PastureRotationLog): Long

    @Update
    suspend fun update(log: PastureRotationLog)

    @Delete
    suspend fun delete(log: PastureRotationLog)

    @Query("SELECT * FROM pasture_rotation_logs WHERE paddock_id = :paddockId ORDER BY move_in_date DESC")
    fun getForPaddock(paddockId: Long): Flow<List<PastureRotationLog>>

    @Query("SELECT * FROM pasture_rotation_logs WHERE id = :id")
    fun getById(id: Long): Flow<PastureRotationLog?>

    @Query("SELECT * FROM pasture_rotation_logs WHERE move_out_date IS NULL ORDER BY move_in_date ASC")
    fun getCurrentRotations(): Flow<List<PastureRotationLog>>
}
