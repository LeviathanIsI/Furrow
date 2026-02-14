package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.Chicken
import kotlinx.coroutines.flow.Flow

@Dao
interface ChickenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chicken: Chicken): Long

    @Update
    suspend fun update(chicken: Chicken)

    @Delete
    suspend fun delete(chicken: Chicken)

    @Query("SELECT * FROM chickens WHERE status = 'active' ORDER BY name")
    fun getActiveChickens(): Flow<List<Chicken>>

    @Query("SELECT * FROM chickens ORDER BY status, name")
    fun getAll(): Flow<List<Chicken>>

    @Query("SELECT * FROM chickens WHERE id = :id")
    fun getById(id: Long): Flow<Chicken?>
}
