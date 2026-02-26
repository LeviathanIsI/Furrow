package com.furrow.app.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.furrow.app.data.repository.AnimalRepository
import com.furrow.app.util.NotificationHelper
import com.furrow.app.util.NotificationPreferences
import kotlinx.coroutines.flow.firstOrNull

class EggLogReminderWorker(
    context: Context,
    params: WorkerParameters,
    notificationHelper: NotificationHelper,
    private val notificationPreferences: NotificationPreferences,
    private val animalRepository: AnimalRepository,
) : FurrowWorker(context, params, notificationHelper) {

    override suspend fun doWork(): Result {
        val enabled = notificationPreferences.poultryReminders.firstOrNull() ?: true
        if (!enabled) return Result.success()

        val chickens = animalRepository.getAnimalsBySpecies("chicken").firstOrNull() ?: emptyList()
        if (chickens.isEmpty()) return Result.success()

        notificationHelper.showNotification(
            channelId = "furrow_animals",
            title = "Egg log reminder",
            body = "Don't forget to log today's egg count!",
            notificationId = NOTIFICATION_ID,
        )

        return Result.success()
    }

    companion object {
        const val NOTIFICATION_ID = 1003
    }
}
