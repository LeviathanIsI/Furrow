package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.VarroaTest
import kotlinx.coroutines.flow.Flow

@Dao
interface VarroaTestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(test: VarroaTest): Long

    @Update
    suspend fun update(test: VarroaTest)

    @Delete
    suspend fun delete(test: VarroaTest)

    @Query("SELECT * FROM varroa_tests WHERE hive_id = :hiveId ORDER BY date DESC")
    fun getForHive(hiveId: Long): Flow<List<VarroaTest>>

    @Query("SELECT * FROM varroa_tests WHERE id = :id")
    fun getById(id: Long): Flow<VarroaTest?>

    @Query("SELECT * FROM varroa_tests WHERE hive_id = :hiveId ORDER BY date DESC LIMIT 1")
    fun getLatestForHive(hiveId: Long): Flow<VarroaTest?>
}
