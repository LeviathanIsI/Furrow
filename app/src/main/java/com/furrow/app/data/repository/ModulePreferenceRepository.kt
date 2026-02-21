package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.UserModulePreferenceDao
import com.furrow.app.data.local.entity.UserModulePreference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModulePreferenceRepository @Inject constructor(
    private val dao: UserModulePreferenceDao,
) {
    fun getAll(): Flow<List<UserModulePreference>> = dao.getAll()

    fun getEnabled(): Flow<List<UserModulePreference>> = dao.getEnabled()

    suspend fun setEnabled(moduleName: String, enabled: Boolean) =
        dao.setEnabled(moduleName, enabled)

    suspend fun insertAll(prefs: List<UserModulePreference>) = dao.insertAll(prefs)
}
