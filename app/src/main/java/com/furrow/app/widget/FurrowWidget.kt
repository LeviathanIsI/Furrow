package com.furrow.app.widget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.furrow.app.MainActivity

class FurrowWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = WidgetDataProvider.getData(context)

        provideContent {
            GlanceTheme {
                WidgetContent(data = data)
            }
        }
    }
}

private val WidgetBg = Color(0xFF181D1A)
private val WidgetTextPrimary = Color(0xFFF4F6F4)
private val WidgetTextSecondary = Color(0xFFC0C7C0)
private val WidgetAccent = Color(0xFF7A9669)

private val MAIN_COMPONENT = ComponentName("com.furrow.app", "com.furrow.app.MainActivity")

private fun launchAppIntent(): Intent = Intent().apply {
    component = MAIN_COMPONENT
    flags = Intent.FLAG_ACTIVITY_NEW_TASK
}

private fun deepLinkIntent(uri: String): Intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
    component = MAIN_COMPONENT
    flags = Intent.FLAG_ACTIVITY_NEW_TASK
}

@Composable
private fun WidgetContent(data: WidgetData) {
    val bgColor = ColorProvider(WidgetBg)
    val textPrimary = ColorProvider(WidgetTextPrimary)
    val textSecondary = ColorProvider(WidgetTextSecondary)
    val accent = ColorProvider(WidgetAccent)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bgColor)
            .padding(12.dp)
            .clickable(actionStartActivity(launchAppIntent())),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Furrow",
            style = TextStyle(
                color = accent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        StatLine(
            label = "Eggs today",
            value = "${data.todayEggs}",
            textPrimary = textPrimary,
            textSecondary = textSecondary,
        )

        StatLine(
            label = "Ready to harvest",
            value = "${data.readyToHarvest}",
            textPrimary = textPrimary,
            textSecondary = textSecondary,
        )

        StatLine(
            label = "Inspections due",
            value = "${data.hivesNeedingInspection}",
            textPrimary = textPrimary,
            textSecondary = textSecondary,
        )

        if (data.plannedPlantings > 0) {
            StatLine(
                label = "Planned plantings",
                value = "${data.plannedPlantings}",
                textPrimary = textPrimary,
                textSecondary = textSecondary,
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Quick action buttons
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            QuickAction(label = "Eggs", uri = "furrow://eggs", accent = accent)
            Spacer(modifier = GlanceModifier.width(12.dp))
            QuickAction(label = "Water", uri = "furrow://water", accent = accent)
            Spacer(modifier = GlanceModifier.width(12.dp))
            QuickAction(label = "Inspect", uri = "furrow://inspect", accent = accent)
        }
    }
}

@Composable
private fun StatLine(
    label: String,
    value: String,
    textPrimary: ColorProvider,
    textSecondary: ColorProvider,
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth().padding(vertical = 1.dp),
        horizontalAlignment = Alignment.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = value,
            style = TextStyle(
                color = textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        Spacer(modifier = GlanceModifier.width(6.dp))
        Text(
            text = label,
            style = TextStyle(
                color = textSecondary,
                fontSize = 12.sp,
            ),
        )
    }
}

@Composable
private fun QuickAction(
    label: String,
    uri: String,
    accent: ColorProvider,
) {
    Text(
        text = label,
        style = TextStyle(
            color = accent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        ),
        modifier = GlanceModifier.clickable(actionStartActivity(deepLinkIntent(uri))),
    )
}
