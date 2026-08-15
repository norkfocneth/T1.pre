package com.example.t1.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.t1.data.database.entity.AppCategoryOverrideEntity

@Dao
interface AppCategoryOverrideDao {
    @Query("SELECT * FROM app_category_override")
    suspend fun getAllOverrides(): List<AppCategoryOverrideEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOverride(override: AppCategoryOverrideEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOverrides(overrides: List<AppCategoryOverrideEntity>)

    @Query("DELETE FROM app_category_override WHERE packageName = :packageName")
    suspend fun deleteOverride(packageName: String)

    @Query("DELETE FROM app_category_override")
    suspend fun clearAllOverrides()
}
