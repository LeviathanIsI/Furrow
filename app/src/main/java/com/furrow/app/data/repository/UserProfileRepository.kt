package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.UserProfileDao
import com.furrow.app.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao,
) {
    fun getProfile(): Flow<UserProfile?> = userProfileDao.getProfile()

    suspend fun saveProfile(profile: UserProfile) = userProfileDao.insert(profile)
}
