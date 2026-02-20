package com.furrow.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Configuration
import com.furrow.app.worker.FurrowWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FurrowApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: FurrowWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

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
                    "furrow_poultry",
                    "Poultry",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Egg logging reminders and flock alerts"
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
