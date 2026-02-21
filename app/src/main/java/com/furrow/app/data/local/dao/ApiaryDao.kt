package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.Apiary
import kotlinx.coroutines.flow.Flow

@Dao
interface ApiaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(apiary: Apiary): Long

    @Update
    suspend fun update(apiary: Apiary)

    @Delete
    suspend fun delete(apiary: Apiary)

    @Query("SELECT * FROM apiaries ORDER BY name")
    fun getAll(): Flow<List<Apiary>>

    @Query("SELECT * FROM apiaries WHERE id = :id")
    fun getById(id: Long): Flow<Apiary?>
}
