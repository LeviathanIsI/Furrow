package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.BarterTrade
import kotlinx.coroutines.flow.Flow

@Dao
interface BarterTradeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(barterTrade: BarterTrade): Long

    @Update
    suspend fun update(barterTrade: BarterTrade)

    @Delete
    suspend fun delete(barterTrade: BarterTrade)

    @Query("SELECT * FROM barter_trades ORDER BY date DESC")
    fun getAll(): Flow<List<BarterTrade>>

    @Query("SELECT * FROM barter_trades WHERE id = :id")
    fun getById(id: Long): Flow<BarterTrade?>
}
