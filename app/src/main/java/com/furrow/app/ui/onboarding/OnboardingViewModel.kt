package com.furrow.app.ui.onboarding

import com.furrow.app.data.FurrowModule
import com.furrow.app.ui.FurrowViewModel
import com.furrow.app.data.ZoneLookup
import com.furrow.app.data.local.entity.UserModulePreference
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.data.repository.ModulePreferenceRepository
import com.furrow.app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: UserProfileRepository,
    private val modulePreferenceRepository: ModulePreferenceRepository,
) : FurrowViewModel() {

    fun saveProfile(zipCode: String) {
        val profile = ZoneLookup.deriveProfile(zipCode) ?: return
        safeLaunch {
            repository.saveProfile(profile)
        }
    }

    fun completeOnboarding(zipCode: String, selectedModuleKeys: Set<String>) {
        val profile = ZoneLookup.deriveProfile(zipCode) ?: return
        safeLaunch {
            repository.saveProfile(profile)
            val prefs = FurrowModule.entries.map { module ->
                UserModulePreference(
                    moduleName = module.key,
                    enabled = module.key in selectedModuleKeys,
                    displayOrder = module.displayOrder,
                )
            }
            modulePreferenceRepository.insertAll(prefs)
        }
    }

    fun skipOnboarding() {
        safeLaunch {
            val defaultProfile = UserProfile(
                zipCode = "",
                hardinessZone = "7a",
                zoneGroup = "7",
                state = "",
                climateCategory = "Temperate",
                lastFrostDate = "Apr 15",
                firstFrostDate = "Oct 15",
            )
            repository.saveProfile(defaultProfile)
            val prefs = FurrowModule.entries.map { module ->
                UserModulePreference(
                    moduleName = module.key,
                    enabled = module.enabledByDefault,
                    displayOrder = module.displayOrder,
                )
            }
            modulePreferenceRepository.insertAll(prefs)
        }
    }
}
