package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.Paddock
import kotlinx.coroutines.flow.Flow

@Dao
interface PaddockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paddock: Paddock): Long

    @Update
    suspend fun update(paddock: Paddock)

    @Delete
    suspend fun delete(paddock: Paddock)

    @Query("SELECT * FROM paddocks ORDER BY name")
    fun getAll(): Flow<List<Paddock>>

    @Query("SELECT * FROM paddocks WHERE id = :id")
    fun getById(id: Long): Flow<Paddock?>
}
