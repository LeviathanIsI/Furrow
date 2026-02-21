package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.BeeHarvestLog
import kotlinx.coroutines.flow.Flow

@Dao
interface BeeHarvestLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: BeeHarvestLog): Long

    @Update
    suspend fun update(log: BeeHarvestLog)

    @Delete
    suspend fun delete(log: BeeHarvestLog)

    @Query("SELECT * FROM bee_harvest_logs WHERE hive_id = :hiveId ORDER BY date DESC")
    fun getForHive(hiveId: Long): Flow<List<BeeHarvestLog>>

    @Query("SELECT * FROM bee_harvest_logs WHERE id = :id")
    fun getById(id: Long): Flow<BeeHarvestLog?>

    @Query("SELECT * FROM bee_harvest_logs WHERE product = :product ORDER BY date DESC")
    fun getByProduct(product: String): Flow<List<BeeHarvestLog>>
}
