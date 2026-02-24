package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.QueenRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface QueenRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(queen: QueenRecord): Long

    @Update
    suspend fun update(queen: QueenRecord)

    @Delete
    suspend fun delete(queen: QueenRecord)

    @Query("SELECT * FROM queen_records WHERE hive_id = :hiveId ORDER BY introduction_date DESC")
    fun getForHive(hiveId: Long): Flow<List<QueenRecord>>

    @Query("SELECT * FROM queen_records WHERE id = :id")
    fun getById(id: Long): Flow<QueenRecord?>

    @Query("SELECT * FROM queen_records WHERE hive_id = :hiveId AND LOWER(status) = 'laying' LIMIT 1")
    fun getCurrentQueen(hiveId: Long): Flow<QueenRecord?>
}
