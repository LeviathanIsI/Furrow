package com.furrow.app.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object TimerUtil {

    fun daysSince(startMillis: Long, zone: ZoneId): Long {
        val start = Instant.ofEpochMilli(startMillis).atZone(zone).toLocalDate()
        val now = LocalDate.now(zone)
        return ChronoUnit.DAYS.between(start, now)
    }

    fun hoursSince(startMillis: Long): Long {
        return (System.currentTimeMillis() - startMillis) / (1000 * 60 * 60)
    }

    fun estimatedCureDays(cureType: String?): IntRange? = when (cureType?.lowercase()) {
        "dry cure" -> 14..30
        "wet brine" -> 5..14
        "equilibrium cure" -> 7..21
        "smoke only" -> 1..2
        else -> null
    }

    fun cureDayLabel(startMillis: Long, cureType: String?, zone: ZoneId): String {
        val days = daysSince(startMillis, zone)
        val range = estimatedCureDays(cureType)
        return if (range != null && days < range.last) {
            val remaining = range.last - days
            "Day $days \u2022 ~$remaining days remaining"
        } else {
            "Day $days"
        }
    }

    fun fermentDayLabel(startMillis: Long, zone: ZoneId): String {
        val days = daysSince(startMillis, zone)
        return "Day $days \u2022 fermenting"
    }

    fun dehydrateHoursLabel(startMillis: Long): String {
        val hours = hoursSince(startMillis)
        return "Running for ${hours}h"
    }
}
