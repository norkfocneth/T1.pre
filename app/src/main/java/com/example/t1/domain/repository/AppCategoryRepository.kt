package com.example.t1.domain.repository

enum class EngineCategory {
    PRODUCTIVITY, EDUCATION, COMMUNICATION, SOCIAL, ENTERTAINMENT, UTILITY
}

interface AppCategoryRepository {
    fun getCategory(packageName: String): EngineCategory
}
