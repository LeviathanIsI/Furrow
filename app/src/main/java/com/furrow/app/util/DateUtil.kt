package com.furrow.app.util

import com.furrow.app.data.local.entity.UserProfile
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Locale

object DateUtil {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    private val shortDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d")
    private val shortDateWithYearFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    private val frostDateParser: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US)

    fun formatDate(millis: Long, zone: ZoneId): String {
        return Instant.ofEpochMilli(millis)
            .atZone(zone)
            .toLocalDate()
            .format(formatter)
    }

    fun formatShortDate(date: LocalDate, today: LocalDate = LocalDate.now()): String =
        date.format(if (date.year == today.year) shortDateFormatter else shortDateWithYearFormatter)

    fun parseFrostDate(dateStr: String, year: Int): LocalDate? {
        return try {
            val parsed = frostDateParser.parse(dateStr)
            val month = parsed.get(ChronoField.MONTH_OF_YEAR)
            val day = parsed.get(ChronoField.DAY_OF_MONTH)
            LocalDate.of(year, month, day)
        } catch (_: Exception) {
            null
        }
    }

    fun profileZone(profile: UserProfile?): ZoneId {
        val tz = profile?.timezone
        return if (!tz.isNullOrBlank()) {
            try {
                ZoneId.of(tz)
            } catch (_: Exception) {
                ZoneId.systemDefault()
            }
        } else {
            ZoneId.systemDefault()
        }
    }
}
