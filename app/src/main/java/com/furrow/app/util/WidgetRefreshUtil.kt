package com.furrow.app.util

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.furrow.app.widget.FurrowWidget

object WidgetRefreshUtil {
    suspend fun refresh(context: Context) {
        try {
            val widget = FurrowWidget()
            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(FurrowWidget::class.java)
            ids.forEach { widget.update(context, it) }
        } catch (_: Exception) {
            // Widget might not be placed — that's fine
        }
    }
}
