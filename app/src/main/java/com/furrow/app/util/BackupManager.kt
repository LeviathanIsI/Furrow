package com.furrow.app.util

import android.content.Context
import android.net.Uri
import com.furrow.app.data.local.FurrowDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: FurrowDatabase,
) {

    fun exportBackup(destinationUri: Uri): Boolean {
        return try {
            // Flush WAL to main database file
            database.openHelper.writableDatabase
                .query("PRAGMA wal_checkpoint(FULL)").close()
            val dbFile = context.getDatabasePath("furrow_database")
            context.contentResolver.openOutputStream(destinationUri)?.use { output ->
                dbFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    fun importBackup(sourceUri: Uri): Boolean {
        return try {
            database.close()
            val dbFile = context.getDatabasePath("furrow_database")
            val walFile = context.getDatabasePath("furrow_database-wal")
            val shmFile = context.getDatabasePath("furrow_database-shm")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                dbFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            walFile.delete()
            shmFile.delete()
            true
        } catch (e: IOException) {
            false
        }
    }
}
