package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.GrantRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface GrantRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(grantRecord: GrantRecord): Long

    @Update
    suspend fun update(grantRecord: GrantRecord)

    @Delete
    suspend fun delete(grantRecord: GrantRecord)

    @Query("SELECT * FROM grant_records ORDER BY application_date DESC")
    fun getAll(): Flow<List<GrantRecord>>

    @Query("SELECT * FROM grant_records WHERE id = :id")
    fun getById(id: Long): Flow<GrantRecord?>

    @Query("SELECT * FROM grant_records WHERE LOWER(status) = 'active' ORDER BY contract_end_date ASC")
    fun getActive(): Flow<List<GrantRecord>>
}
