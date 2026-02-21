package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.SeasonExtension
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonExtensionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ext: SeasonExtension): Long

    @Update
    suspend fun update(ext: SeasonExtension)

    @Delete
    suspend fun delete(ext: SeasonExtension)

    @Query("SELECT * FROM season_extensions WHERE bed_id = :bedId ORDER BY deploy_date DESC")
    fun getForBed(bedId: Long): Flow<List<SeasonExtension>>

    @Query("SELECT * FROM season_extensions WHERE id = :id")
    fun getById(id: Long): Flow<SeasonExtension?>

    @Query("SELECT * FROM season_extensions WHERE remove_date IS NULL ORDER BY deploy_date DESC")
    fun getActive(): Flow<List<SeasonExtension>>
}
