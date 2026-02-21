package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.SeedInventory
import kotlinx.coroutines.flow.Flow

@Dao
interface SeedInventoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(seed: SeedInventory): Long

    @Update
    suspend fun update(seed: SeedInventory)

    @Delete
    suspend fun delete(seed: SeedInventory)

    @Query("SELECT * FROM seed_inventory ORDER BY species, variety")
    fun getAll(): Flow<List<SeedInventory>>

    @Query("SELECT * FROM seed_inventory WHERE id = :id")
    fun getById(id: Long): Flow<SeedInventory?>

    @Query("SELECT * FROM seed_inventory WHERE expiration_date IS NOT NULL AND expiration_date < :beforeDate ORDER BY expiration_date")
    fun getExpiringSoon(beforeDate: Long): Flow<List<SeedInventory>>
}
