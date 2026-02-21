package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compliance_documents")
data class ComplianceDocument(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val name: String,
    @ColumnInfo(name = "file_url") val fileUrl: String? = null,
    @ColumnInfo(name = "issue_date") val issueDate: Long? = null,
    @ColumnInfo(name = "expiration_date") val expirationDate: Long? = null,
    val notes: String? = null,
)
