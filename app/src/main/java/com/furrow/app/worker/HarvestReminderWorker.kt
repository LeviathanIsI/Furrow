package com.furrow.app.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.Planting
import com.furrow.app.data.repository.GardenRepository
import com.furrow.app.data.repository.PlantRepository
import com.furrow.app.util.NotificationHelper
import com.furrow.app.util.NotificationPreferences
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HarvestReminderWorker(
    context: Context,
    params: WorkerParameters,
    notificationHelper: NotificationHelper,
    private val notificationPreferences: NotificationPreferences,
    private val gardenRepository: GardenRepository,
    private val plantRepository: PlantRepository,
) : FurrowWorker(context, params, notificationHelper) {

    override suspend fun doWork(): Result {
        val enabled = notificationPreferences.gardenReminders.firstOrNull() ?: true
        if (!enabled) return Result.success()

        val plantings = gardenRepository.getActivePlantings().firstOrNull() ?: return Result.success()
        if (plantings.isEmpty()) return Result.success()

        val plantInfoList = plantRepository.getAllPlants().firstOrNull() ?: return Result.success()
        val infoMap = plantInfoList.associateBy { it.name }

        val today = LocalDate.now()
        val readyCount = plantings.count { planting ->
            val info = infoMap[planting.plantName] ?: return@count false
            val plantedDate = Instant.ofEpochMilli(planting.datePlanted)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val earliest = plantedDate.plusDays(info.daysToHarvestMin.toLong())
            !today.isBefore(earliest)
        }

        if (readyCount > 0) {
            val body = if (readyCount == 1) "1 plant is ready to harvest!"
            else "$readyCount plants are ready to harvest!"
            notificationHelper.showNotification(
                channelId = "furrow_garden",
                title = "Harvest time",
                body = body,
                notificationId = NOTIFICATION_ID,
            )
        }

        return Result.success()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
    }
}
