package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.Revenue
import kotlinx.coroutines.flow.Flow

@Dao
interface RevenueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(revenue: Revenue): Long

    @Update
    suspend fun update(revenue: Revenue)

    @Delete
    suspend fun delete(revenue: Revenue)

    @Query("SELECT * FROM revenues ORDER BY date DESC")
    fun getAll(): Flow<List<Revenue>>

    @Query("SELECT * FROM revenues WHERE id = :id")
    fun getById(id: Long): Flow<Revenue?>

    @Query("SELECT * FROM revenues WHERE date BETWEEN :startMillis AND :endMillis ORDER BY date DESC")
    fun getForDateRange(startMillis: Long, endMillis: Long): Flow<List<Revenue>>

    @Query("SELECT COALESCE(SUM(total), 0.0) FROM revenues WHERE date BETWEEN :startMillis AND :endMillis")
    fun getTotalForDateRange(startMillis: Long, endMillis: Long): Flow<Double?>
}
