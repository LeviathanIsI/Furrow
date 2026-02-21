package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.PantryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PantryItem): Long

    @Update
    suspend fun update(item: PantryItem)

    @Delete
    suspend fun delete(item: PantryItem)

    @Query("SELECT * FROM pantry_items ORDER BY expiration_date ASC")
    fun getAll(): Flow<List<PantryItem>>

    @Query("SELECT * FROM pantry_items WHERE id = :id")
    fun getById(id: Long): Flow<PantryItem?>

    @Query("SELECT * FROM pantry_items WHERE status = 'in_stock' ORDER BY expiration_date ASC")
    fun getInStock(): Flow<List<PantryItem>>

    @Query("SELECT * FROM pantry_items WHERE category = :category ORDER BY expiration_date ASC")
    fun getByCategory(category: String): Flow<List<PantryItem>>

    @Query("SELECT * FROM pantry_items WHERE status = 'in_stock' AND expiration_date < :beforeDate ORDER BY expiration_date ASC")
    fun getExpiringSoon(beforeDate: Long): Flow<List<PantryItem>>
}
