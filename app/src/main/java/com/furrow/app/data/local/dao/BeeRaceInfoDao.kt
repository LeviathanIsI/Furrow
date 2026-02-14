package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.BeeRaceInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface BeeRaceInfoDao {
    @Query("SELECT * FROM bee_race_info ORDER BY name")
    fun getAllRaces(): Flow<List<BeeRaceInfo>>

    @Query("SELECT * FROM bee_race_info WHERE name = :name")
    fun getRaceByName(name: String): Flow<BeeRaceInfo?>

    @Query("SELECT * FROM bee_race_info WHERE name LIKE '%' || :query || '%'")
    fun searchRaces(query: String): Flow<List<BeeRaceInfo>>

    @Insert
    suspend fun insert(race: BeeRaceInfo): Long

    @Update
    suspend fun update(race: BeeRaceInfo)

    @Delete
    suspend fun delete(race: BeeRaceInfo)

    @Insert
    suspend fun insertAll(races: List<BeeRaceInfo>)
}
