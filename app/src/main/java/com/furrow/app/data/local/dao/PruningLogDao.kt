package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.PruningLog
import kotlinx.coroutines.flow.Flow

@Dao
interface PruningLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: PruningLog): Long

    @Update
    suspend fun update(log: PruningLog)

    @Delete
    suspend fun delete(log: PruningLog)

    @Query("SELECT * FROM pruning_logs WHERE plant_id = :plantId ORDER BY date DESC")
    fun getForPlant(plantId: Long): Flow<List<PruningLog>>

    @Query("SELECT * FROM pruning_logs WHERE id = :id")
    fun getById(id: Long): Flow<PruningLog?>
}
