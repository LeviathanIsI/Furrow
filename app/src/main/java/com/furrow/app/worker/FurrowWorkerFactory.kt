package com.furrow.app.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.furrow.app.data.repository.AnimalRepository
import com.furrow.app.data.repository.BeeRepository
import com.furrow.app.data.repository.GardenRepository
import com.furrow.app.data.repository.PlantRepository
import com.furrow.app.util.NotificationHelper
import com.furrow.app.util.NotificationPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FurrowWorkerFactory @Inject constructor(
    private val notificationHelper: NotificationHelper,
    private val notificationPreferences: NotificationPreferences,
    private val gardenRepository: GardenRepository,
    private val plantRepository: PlantRepository,
    private val beeRepository: BeeRepository,
    private val animalRepository: AnimalRepository,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            HarvestReminderWorker::class.java.name ->
                HarvestReminderWorker(appContext, workerParameters, notificationHelper, notificationPreferences, gardenRepository, plantRepository)
            BeeInspectionReminderWorker::class.java.name ->
                BeeInspectionReminderWorker(appContext, workerParameters, notificationHelper, notificationPreferences, beeRepository)
            EggLogReminderWorker::class.java.name ->
                EggLogReminderWorker(appContext, workerParameters, notificationHelper, notificationPreferences, animalRepository)
            else -> null
        }
    }
}
