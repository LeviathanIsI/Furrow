package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.SalesTracker
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesTrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(salesTracker: SalesTracker): Long

    @Update
    suspend fun update(salesTracker: SalesTracker)

    @Delete
    suspend fun delete(salesTracker: SalesTracker)

    @Query("SELECT * FROM sales_trackers ORDER BY year DESC, product_type ASC")
    fun getAll(): Flow<List<SalesTracker>>

    @Query("SELECT * FROM sales_trackers WHERE id = :id")
    fun getById(id: Long): Flow<SalesTracker?>

    @Query("SELECT * FROM sales_trackers WHERE year = :year")
    fun getByYear(year: Int): Flow<List<SalesTracker>>
}
