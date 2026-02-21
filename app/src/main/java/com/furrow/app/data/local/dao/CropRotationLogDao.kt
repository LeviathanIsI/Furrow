package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.CropRotationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface CropRotationLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: CropRotationLog): Long

    @Update
    suspend fun update(log: CropRotationLog)

    @Delete
    suspend fun delete(log: CropRotationLog)

    @Query("SELECT * FROM crop_rotation_logs WHERE bed_id = :bedId ORDER BY year DESC, season")
    fun getForBed(bedId: Long): Flow<List<CropRotationLog>>

    @Query("SELECT * FROM crop_rotation_logs WHERE id = :id")
    fun getById(id: Long): Flow<CropRotationLog?>

    @Query("SELECT * FROM crop_rotation_logs WHERE bed_id = :bedId AND crop_family_planted = :family ORDER BY year DESC")
    fun getFamilyHistoryForBed(bedId: Long, family: String): Flow<List<CropRotationLog>>
}
