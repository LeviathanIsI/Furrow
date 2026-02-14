package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.Hive
import kotlinx.coroutines.flow.Flow

@Dao
interface HiveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hive: Hive): Long

    @Update
    suspend fun update(hive: Hive)

    @Delete
    suspend fun delete(hive: Hive)

    @Query("SELECT * FROM hives WHERE id = :id")
    fun getById(id: Long): Flow<Hive?>

    @Query("SELECT * FROM hives ORDER BY name ASC")
    fun getAll(): Flow<List<Hive>>

    @Query("SELECT * FROM hives WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveHives(): Flow<List<Hive>>
}
