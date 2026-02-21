package com.furrow.app.data

/**
 * Expense category → tax form line number mappings.
 * US: IRS Schedule F (Form 1040)
 * Canada: CRA T2042 (Statement of Farming Activities)
 */
object FinanceCategoryData {

    data class ExpenseCategoryMapping(
        val category: String,
        val scheduleFLine: String?,
        val t2042Line: String?,
    )

    private val mappings = listOf(
        ExpenseCategoryMapping("seeds & plants", "Line 22", "Line 9471"),
        ExpenseCategoryMapping("feed", "Line 12", "Line 9270"),
        ExpenseCategoryMapping("fertilizer & lime", "Line 13", "Line 9275"),
        ExpenseCategoryMapping("machine hire", "Line 14", "Line 9281"),
        ExpenseCategoryMapping("repairs & maintenance", "Line 21", "Line 9468"),
        ExpenseCategoryMapping("supplies", "Line 22", "Line 9471"),
        ExpenseCategoryMapping("utilities", "Line 25", "Line 9559"),
        ExpenseCategoryMapping("veterinary & medicine", "Line 26", "Line 9565"),
        ExpenseCategoryMapping("fuel & oil", "Line 10", "Line 9252"),
        ExpenseCategoryMapping("labor", "Line 15", "Line 9281"),
        ExpenseCategoryMapping("insurance", "Line 16", "Line 9378"),
        ExpenseCategoryMapping("rent & lease", "Line 20a", "Line 9398"),
        ExpenseCategoryMapping("taxes", "Line 23", "Line 9477"),
        ExpenseCategoryMapping("interest", "Line 17", "Line 9384"),
        ExpenseCategoryMapping("depreciation", "Line 11", "Line 9936"),
        ExpenseCategoryMapping("storage & warehousing", "Line 24a", "Line 9471"),
        ExpenseCategoryMapping("marketing", "Line 22", "Line 9471"),
        ExpenseCategoryMapping("custom hire", "Line 9", "Line 9248"),
        ExpenseCategoryMapping("freight & trucking", "Line 14", "Line 9281"),
        ExpenseCategoryMapping("other", "Line 28", "Line 9790"),
    )

    private val byCategory = mappings.associateBy { it.category }

    /** Look up the Schedule F / T2042 line for a given expense category. */
    fun forCategory(category: String): ExpenseCategoryMapping? =
        byCategory[category.lowercase()]

    /** All mappings as a list. */
    fun allMappings(): List<ExpenseCategoryMapping> = mappings
}
