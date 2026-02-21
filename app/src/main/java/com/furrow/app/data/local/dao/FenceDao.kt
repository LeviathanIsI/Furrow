package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.Fence
import kotlinx.coroutines.flow.Flow

@Dao
interface FenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fence: Fence): Long

    @Update
    suspend fun update(fence: Fence)

    @Delete
    suspend fun delete(fence: Fence)

    @Query("SELECT * FROM fences ORDER BY install_date DESC")
    fun getAll(): Flow<List<Fence>>

    @Query("SELECT * FROM fences WHERE id = :id")
    fun getById(id: Long): Flow<Fence?>
}
