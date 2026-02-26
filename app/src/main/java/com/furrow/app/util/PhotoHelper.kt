package com.furrow.app.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

object PhotoHelper {

    private fun photosDir(context: Context): File =
        File(context.filesDir, "photos").also { it.mkdirs() }

    fun createTempPhotoUri(context: Context): Uri {
        val file = File(photosDir(context), "photo_${UUID.randomUUID()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    fun persistPhoto(context: Context, sourceUri: Uri): String {
        val dest = File(photosDir(context), "photo_${UUID.randomUUID()}.jpg")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        }
        return dest.absolutePath
    }

    fun deletePhoto(path: String) {
        val file = File(path)
        if (file.exists()) file.delete()
    }
}
