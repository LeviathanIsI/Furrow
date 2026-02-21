package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.LabelTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelTemplateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(labelTemplate: LabelTemplate): Long

    @Update
    suspend fun update(labelTemplate: LabelTemplate)

    @Delete
    suspend fun delete(labelTemplate: LabelTemplate)

    @Query("SELECT * FROM label_templates ORDER BY product_name")
    fun getAll(): Flow<List<LabelTemplate>>

    @Query("SELECT * FROM label_templates WHERE id = :id")
    fun getById(id: Long): Flow<LabelTemplate?>
}
