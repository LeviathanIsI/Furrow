package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.CompostInputLog
import kotlinx.coroutines.flow.Flow

@Dao
interface CompostInputLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: CompostInputLog): Long

    @Update
    suspend fun update(log: CompostInputLog)

    @Delete
    suspend fun delete(log: CompostInputLog)

    @Query("SELECT * FROM compost_input_logs WHERE bin_id = :binId ORDER BY date DESC")
    fun getForBin(binId: Long): Flow<List<CompostInputLog>>

    @Query("SELECT * FROM compost_input_logs WHERE id = :id")
    fun getById(id: Long): Flow<CompostInputLog?>

    @Query("SELECT bin_id AS binId, MAX(date) AS lastDate FROM compost_input_logs GROUP BY bin_id")
    fun getLastInputDatePerBin(): Flow<List<BinLastInput>>
}

data class BinLastInput(val binId: Long, val lastDate: Long)
