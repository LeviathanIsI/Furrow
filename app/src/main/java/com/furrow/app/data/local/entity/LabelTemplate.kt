package com.furrow.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "label_templates")
data class LabelTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "template_content") val templateContent: String? = null,
    @ColumnInfo(name = "required_fields") val requiredFields: String? = null,
    val disclaimer: String? = null,
    val notes: String? = null,
)
