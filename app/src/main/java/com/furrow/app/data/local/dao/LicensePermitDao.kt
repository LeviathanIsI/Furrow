package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.LicensePermit
import kotlinx.coroutines.flow.Flow

@Dao
interface LicensePermitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(licensePermit: LicensePermit): Long

    @Update
    suspend fun update(licensePermit: LicensePermit)

    @Delete
    suspend fun delete(licensePermit: LicensePermit)

    @Query("SELECT * FROM license_permits ORDER BY expiration ASC")
    fun getAll(): Flow<List<LicensePermit>>

    @Query("SELECT * FROM license_permits WHERE id = :id")
    fun getById(id: Long): Flow<LicensePermit?>

    @Query("SELECT * FROM license_permits WHERE expiration < :beforeDate ORDER BY expiration ASC")
    fun getExpiringSoon(beforeDate: Long): Flow<List<LicensePermit>>
}
