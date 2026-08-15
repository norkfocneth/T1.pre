package com.example.t1.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_category_override")
data class AppCategoryOverrideEntity(
    @PrimaryKey
    val packageName: String,
    val category: String // "PRODUCTIVITY", "EDUCATION", "SOCIAL", "ENTERTAINMENT", "COMMUNICATION", "UTILITY"
)
