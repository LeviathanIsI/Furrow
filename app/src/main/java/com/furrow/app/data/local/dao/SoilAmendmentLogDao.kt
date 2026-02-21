package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.SoilAmendmentLog
import kotlinx.coroutines.flow.Flow

@Dao
interface SoilAmendmentLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: SoilAmendmentLog): Long

    @Update
    suspend fun update(log: SoilAmendmentLog)

    @Delete
    suspend fun delete(log: SoilAmendmentLog)

    @Query("SELECT * FROM soil_amendment_logs WHERE bed_id = :bedId ORDER BY date DESC")
    fun getForBed(bedId: Long): Flow<List<SoilAmendmentLog>>

    @Query("SELECT * FROM soil_amendment_logs WHERE id = :id")
    fun getById(id: Long): Flow<SoilAmendmentLog?>
}
