package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.SprayLog
import kotlinx.coroutines.flow.Flow

@Dao
interface SprayLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: SprayLog): Long

    @Update
    suspend fun update(log: SprayLog)

    @Delete
    suspend fun delete(log: SprayLog)

    @Query("SELECT * FROM spray_logs WHERE plant_id = :plantId ORDER BY date DESC")
    fun getForPlant(plantId: Long): Flow<List<SprayLog>>

    @Query("SELECT * FROM spray_logs WHERE id = :id")
    fun getById(id: Long): Flow<SprayLog?>
}
