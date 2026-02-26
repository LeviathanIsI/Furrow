package com.furrow.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.furrow.app.worker.BeeInspectionReminderWorker
import com.furrow.app.worker.EggLogReminderWorker
import com.furrow.app.worker.FurrowWorkerFactory
import com.furrow.app.worker.HarvestReminderWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class FurrowApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: FurrowWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        scheduleWorkers()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun scheduleWorkers() {
        val wm = WorkManager.getInstance(this)

        wm.enqueueUniquePeriodicWork(
            "harvest_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<HarvestReminderWorker>(6, TimeUnit.HOURS).build(),
        )

        wm.enqueueUniquePeriodicWork(
            "bee_inspection_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<BeeInspectionReminderWorker>(1, TimeUnit.DAYS).build(),
        )

        wm.enqueueUniquePeriodicWork(
            "egg_log_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<EggLogReminderWorker>(1, TimeUnit.DAYS).build(),
        )
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    "furrow_bees",
                    "Bees",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Hive inspections, treatments, and seasonal tasks"
                },
                NotificationChannel(
                    "furrow_animals",
                    "Animals",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Egg logging reminders, flock alerts, and animal tracking"
                },
                NotificationChannel(
                    "furrow_garden",
                    "Garden",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Harvest reminders, planting windows, and garden tasks"
                },
                NotificationChannel(
                    "furrow_general",
                    "General",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    description = "App updates and tips"
                },
            )
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannels(channels)
        }
    }
}
