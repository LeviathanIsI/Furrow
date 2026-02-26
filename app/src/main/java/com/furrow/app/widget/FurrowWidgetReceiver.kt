package com.furrow.app.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class FurrowWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = FurrowWidget()
}
