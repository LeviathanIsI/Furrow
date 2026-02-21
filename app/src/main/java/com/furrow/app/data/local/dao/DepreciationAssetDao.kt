package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.DepreciationAsset
import kotlinx.coroutines.flow.Flow

@Dao
interface DepreciationAssetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(depreciationAsset: DepreciationAsset): Long

    @Update
    suspend fun update(depreciationAsset: DepreciationAsset)

    @Delete
    suspend fun delete(depreciationAsset: DepreciationAsset)

    @Query("SELECT * FROM depreciation_assets ORDER BY purchase_date DESC")
    fun getAll(): Flow<List<DepreciationAsset>>

    @Query("SELECT * FROM depreciation_assets WHERE id = :id")
    fun getById(id: Long): Flow<DepreciationAsset?>
}
