package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.WeatherLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WeatherLog): Long

    @Update
    suspend fun update(log: WeatherLog)

    @Delete
    suspend fun delete(log: WeatherLog)

    @Query("SELECT * FROM weather_logs ORDER BY date DESC")
    fun getAll(): Flow<List<WeatherLog>>

    @Query("SELECT * FROM weather_logs WHERE id = :id")
    fun getById(id: Long): Flow<WeatherLog?>

    @Query("SELECT * FROM weather_logs WHERE date BETWEEN :startMillis AND :endMillis ORDER BY date DESC")
    fun getForDateRange(startMillis: Long, endMillis: Long): Flow<List<WeatherLog>>
}
