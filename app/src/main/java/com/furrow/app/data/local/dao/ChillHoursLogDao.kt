package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.ChillHoursLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ChillHoursLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: ChillHoursLog): Long

    @Update
    suspend fun update(log: ChillHoursLog)

    @Delete
    suspend fun delete(log: ChillHoursLog)

    @Query("SELECT * FROM chill_hours_logs ORDER BY season DESC")
    fun getAll(): Flow<List<ChillHoursLog>>

    @Query("SELECT * FROM chill_hours_logs WHERE season = :season")
    fun getBySeason(season: String): Flow<ChillHoursLog?>

    @Query("SELECT * FROM chill_hours_logs WHERE id = :id")
    fun getById(id: Long): Flow<ChillHoursLog?>
}
