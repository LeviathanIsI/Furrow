package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.CompostBin
import kotlinx.coroutines.flow.Flow

@Dao
interface CompostBinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bin: CompostBin): Long

    @Update
    suspend fun update(bin: CompostBin)

    @Delete
    suspend fun delete(bin: CompostBin)

    @Query("SELECT * FROM compost_bins ORDER BY start_date DESC")
    fun getAll(): Flow<List<CompostBin>>

    @Query("SELECT * FROM compost_bins WHERE id = :id")
    fun getById(id: Long): Flow<CompostBin?>

    @Query("SELECT * FROM compost_bins WHERE maturity_stage != 'finished' OR maturity_stage IS NULL ORDER BY start_date DESC")
    fun getActive(): Flow<List<CompostBin>>
}
