package com.furrow.app.widget

import android.content.Context
import com.furrow.app.data.local.FurrowDatabase
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

data class WidgetData(
    val todayEggs: Int,
    val readyToHarvest: Int,
    val hivesNeedingInspection: Int,
    val totalTasks: Int,
    val plannedPlantings: Int,
)

object WidgetDataProvider {

    suspend fun getData(context: Context): WidgetData {
        val db = FurrowDatabase.getInstance(context)
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val todayStartMillis = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val todayEndMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1

        // Today's eggs
        val todayEggs = try {
            db.eggLogDao().getEggCountForDateRange(todayStartMillis, todayEndMillis).firstOrNull() ?: 0
        } catch (_: Exception) { 0 }

        // Ready to harvest
        val readyToHarvest = try {
            val plantings = db.plantingDao().getActivePlantings().firstOrNull() ?: emptyList()
            val plants = db.plantInfoDao().getAllPlants().firstOrNull() ?: emptyList()
            val plantMap = plants.associateBy { it.name }
            plantings.count { planting ->
                val info = plantMap[planting.plantName] ?: return@count false
                val plantedDate = Instant.ofEpochMilli(planting.datePlanted).atZone(zone).toLocalDate()
                val earliest = plantedDate.plusDays(info.daysToHarvestMin.toLong())
                !today.isBefore(earliest)
            }
        } catch (_: Exception) { 0 }

        // Hives needing inspection (>7 days since last)
        val hivesNeedingInspection = try {
            val hives = db.hiveDao().getActiveHives().firstOrNull() ?: emptyList()
            val lastInspections = db.inspectionDao().getLastInspectionDatePerHive().firstOrNull() ?: emptyList()
            val lastMap = lastInspections.associate { it.hiveId to it.date }
            hives.count { hive ->
                val lastDate = lastMap[hive.id]
                if (lastDate == null) true
                else ChronoUnit.DAYS.between(
                    Instant.ofEpochMilli(lastDate).atZone(zone).toLocalDate(),
                    today,
                ) >= 7
            }
        } catch (_: Exception) { 0 }

        // Planned plantings
        val plannedPlantings = try {
            db.plantingDao().getPlannedPlantings().firstOrNull()?.size ?: 0
        } catch (_: Exception) { 0 }

        val totalTasks = (if (todayEggs == 0) 1 else 0) + readyToHarvest + hivesNeedingInspection

        return WidgetData(
            todayEggs = todayEggs,
            readyToHarvest = readyToHarvest,
            hivesNeedingInspection = hivesNeedingInspection,
            totalTasks = totalTasks,
            plannedPlantings = plannedPlantings,
        )
    }
}
