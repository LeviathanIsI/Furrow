package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "license_permits")
data class LicensePermit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val number: String? = null,
    val authority: String? = null,
    @ColumnInfo(name = "issue_date") val issueDate: Long? = null,
    val expiration: Long? = null,
    @ColumnInfo(name = "renewal_reminder") val renewalReminder: Long? = null,
    val notes: String? = null,
)
