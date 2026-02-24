package com.furrow.app.util

import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

/**
 * Replaces underscores with spaces and applies Title Case to each word.
 * "leafy_green" → "Leafy Green", "cut_and_come_again" → "Cut And Come Again"
 */
fun String.displayFormat(): String =
    replace('_', ' ').split(" ").joinToString(" ") {
        it.replaceFirstChar { c -> c.uppercase() }
    }

/**
 * Ensures comma-separated lists have a space after each comma.
 * "Basil,Peas,Corn" → "Basil, Peas, Corn"
 */
fun String.formatList(): String =
    split(",").joinToString(", ") { it.trim() }

/**
 * Formats a fertilizer type string: applies Title Case and appends (N-P-K) after any ratio.
 * "balanced 10-10-10" → "Balanced 10-10-10 (N-P-K)"
 * "5-10-10 low nitrogen" → "5-10-10 (N-P-K) Low Nitrogen"
 */
fun String.formatFertilizer(): String {
    val npkPattern = Regex("""\d+-\d+-\d+""")
    val formatted = displayFormat()
    return if (npkPattern.containsMatchIn(formatted)) {
        npkPattern.replace(formatted) { "${it.value} (N-P-K)" }
    } else {
        formatted
    }
}

/**
 * Converts a month number (1-12) to its full name: 1 → "January", 10 → "October"
 */
fun monthName(month: Int): String =
    Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())
