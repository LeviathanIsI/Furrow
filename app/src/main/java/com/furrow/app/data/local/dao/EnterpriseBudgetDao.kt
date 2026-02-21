package com.furrow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furrow.app.data.local.entity.EnterpriseBudget
import kotlinx.coroutines.flow.Flow

@Dao
interface EnterpriseBudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(enterpriseBudget: EnterpriseBudget): Long

    @Update
    suspend fun update(enterpriseBudget: EnterpriseBudget)

    @Delete
    suspend fun delete(enterpriseBudget: EnterpriseBudget)

    @Query("SELECT * FROM enterprise_budgets ORDER BY name")
    fun getAll(): Flow<List<EnterpriseBudget>>

    @Query("SELECT * FROM enterprise_budgets WHERE id = :id")
    fun getById(id: Long): Flow<EnterpriseBudget?>
}
