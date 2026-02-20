package com.furrow.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.furrow.app.util.NotificationHelper

abstract class FurrowWorker(
    context: Context,
    params: WorkerParameters,
    protected val notificationHelper: NotificationHelper,
) : CoroutineWorker(context, params)
