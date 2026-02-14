package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.HarvestLog
import com.furrow.app.data.local.entity.Planting
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantingDao {

    // --- Plantings ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanting(planting: Planting): Long

    @Update
    suspend fun updatePlanting(planting: Planting)

    @Delete
    suspend fun deletePlanting(planting: Planting)

    @Query("SELECT * FROM plantings WHERE bedId = :bedId ORDER BY datePlanted DESC")
    fun getPlantingsForBed(bedId: Long): Flow<List<Planting>>

    @Query("SELECT * FROM plantings WHERE id = :id")
    fun getPlantingById(id: Long): Flow<Planting?>

    @Query("SELECT COUNT(*) FROM plantings WHERE bedId = :bedId AND status IN ('growing', 'producing')")
    fun getActivePlantingCount(bedId: Long): Flow<Int>

    // --- Harvests ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHarvest(harvest: HarvestLog): Long

    @Update
    suspend fun updateHarvest(harvest: HarvestLog)

    @Delete
    suspend fun deleteHarvest(harvest: HarvestLog)

    @Query("SELECT * FROM harvest_logs WHERE id = :id")
    fun getHarvestById(id: Long): Flow<HarvestLog?>

    @Query("SELECT * FROM harvest_logs WHERE plantingId = :plantingId ORDER BY date DESC")
    fun getHarvestsForPlanting(plantingId: Long): Flow<List<HarvestLog>>

    @Query("SELECT * FROM harvest_logs ORDER BY date DESC LIMIT :limit")
    fun getRecentHarvests(limit: Int): Flow<List<HarvestLog>>

    @Query("SELECT * FROM harvest_logs WHERE plantingId IN (SELECT id FROM plantings WHERE bedId = :bedId) ORDER BY date DESC")
    fun getHarvestsForBed(bedId: Long): Flow<List<HarvestLog>>

    @Query("SELECT bedId, COUNT(*) as count FROM plantings WHERE status IN ('growing', 'producing') GROUP BY bedId")
    fun getActivePlantingCountPerBed(): Flow<List<BedPlantingCount>>

    @Query("SELECT * FROM plantings WHERE status IN ('growing', 'producing') ORDER BY datePlanted ASC")
    fun getActivePlantings(): Flow<List<Planting>>

    @Query("SELECT * FROM harvest_logs WHERE date >= :startMillis AND date <= :endMillis ORDER BY date DESC")
    fun getHarvestsForDateRange(startMillis: Long, endMillis: Long): Flow<List<HarvestLog>>
}

data class BedPlantingCount(
    val bedId: Long,
    val count: Int,
)
