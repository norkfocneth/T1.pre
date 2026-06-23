package com.example.t1.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.t1.data.database.dao.FocusSessionDao
import com.example.t1.data.database.dao.UserProfileDao
import com.example.t1.data.database.entity.FocusSessionEntity
import com.example.t1.data.database.entity.UserProfileEntity

@Database(
    entities = [UserProfileEntity::class, FocusSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class T1Database : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun focusSessionDao(): FocusSessionDao
}
