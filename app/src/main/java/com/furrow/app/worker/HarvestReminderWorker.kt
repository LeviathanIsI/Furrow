package com.furrow.app.worker

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import com.furrow.app.util.NotificationHelper

class HarvestReminderWorker(
    context: Context,
    params: WorkerParameters,
    notificationHelper: NotificationHelper,
) : FurrowWorker(context, params, notificationHelper) {

    override suspend fun doWork(): Result {
        Log.d("HarvestReminderWorker", "HarvestReminderWorker executed")
        return Result.success()
    }
}
