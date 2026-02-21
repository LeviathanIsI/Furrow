package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.MileageLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MileageLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mileageLog: MileageLog): Long

    @Update
    suspend fun update(mileageLog: MileageLog)

    @Delete
    suspend fun delete(mileageLog: MileageLog)

    @Query("SELECT * FROM mileage_logs ORDER BY date DESC")
    fun getAll(): Flow<List<MileageLog>>

    @Query("SELECT * FROM mileage_logs WHERE id = :id")
    fun getById(id: Long): Flow<MileageLog?>
}
