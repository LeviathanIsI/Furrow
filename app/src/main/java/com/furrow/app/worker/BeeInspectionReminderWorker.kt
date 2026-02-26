package com.furrow.app.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.furrow.app.data.repository.BeeRepository
import com.furrow.app.util.NotificationHelper
import com.furrow.app.util.NotificationPreferences
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class BeeInspectionReminderWorker(
    context: Context,
    params: WorkerParameters,
    notificationHelper: NotificationHelper,
    private val notificationPreferences: NotificationPreferences,
    private val beeRepository: BeeRepository,
) : FurrowWorker(context, params, notificationHelper) {

    override suspend fun doWork(): Result {
        val enabled = notificationPreferences.beeReminders.firstOrNull() ?: true
        if (!enabled) return Result.success()

        val hives = beeRepository.getActiveHives().firstOrNull() ?: return Result.success()
        if (hives.isEmpty()) return Result.success()

        val lastInspections = beeRepository.getLastInspectionDatePerHive().firstOrNull() ?: emptyList()
        val lastInspectionMap = lastInspections.associate { it.hiveId to it.date }

        val today = LocalDate.now()
        val overdueCount = hives.count { hive ->
            val lastDate = lastInspectionMap[hive.id]
            if (lastDate == null) true
            else {
                val inspectedDate = Instant.ofEpochMilli(lastDate)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                ChronoUnit.DAYS.between(inspectedDate, today) >= 14
            }
        }

        if (overdueCount > 0) {
            val body = if (overdueCount == 1) "1 hive is due for inspection"
            else "$overdueCount hives are due for inspection"
            notificationHelper.showNotification(
                channelId = "furrow_bees",
                title = "Hive inspection due",
                body = body,
                notificationId = NOTIFICATION_ID,
            )
        }

        return Result.success()
    }

    companion object {
        const val NOTIFICATION_ID = 1002
    }
}
