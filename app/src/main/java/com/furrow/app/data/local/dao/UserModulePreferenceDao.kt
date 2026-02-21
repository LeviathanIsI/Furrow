package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furrow.app.data.local.entity.UserModulePreference
import kotlinx.coroutines.flow.Flow

@Dao
interface UserModulePreferenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prefs: List<UserModulePreference>)

    @Query("SELECT * FROM user_module_preferences ORDER BY display_order")
    fun getAll(): Flow<List<UserModulePreference>>

    @Query("SELECT * FROM user_module_preferences WHERE enabled = 1 ORDER BY display_order")
    fun getEnabled(): Flow<List<UserModulePreference>>

    @Query("UPDATE user_module_preferences SET enabled = :enabled WHERE module_name = :moduleName")
    suspend fun setEnabled(moduleName: String, enabled: Boolean)
}
