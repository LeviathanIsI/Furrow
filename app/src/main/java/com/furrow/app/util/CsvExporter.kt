package com.furrow.app.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object CsvExporter {

    fun share(context: Context, fileName: String, csvContent: String) {
        val file = File(context.cacheDir, "${fileName}_${System.currentTimeMillis()}.csv")
        file.writeText(csvContent)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Export CSV"))
    }

    fun escapeCsv(value: String?): String {
        if (value == null) return ""
        return if (value.contains(',') || value.contains('"') || value.contains('\n')) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}
