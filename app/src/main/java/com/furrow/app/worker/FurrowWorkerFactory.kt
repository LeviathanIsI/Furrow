package com.furrow.app.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.furrow.app.util.NotificationHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FurrowWorkerFactory @Inject constructor(
    private val notificationHelper: NotificationHelper,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            HarvestReminderWorker::class.java.name ->
                HarvestReminderWorker(appContext, workerParameters, notificationHelper)
            else -> null
        }
    }
}
