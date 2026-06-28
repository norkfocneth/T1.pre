package com.example.t1.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.t1.data.database.dao.FocusSessionDao
import com.example.t1.data.database.dao.UserProfileDao
import com.example.t1.data.database.dao.DailyUsageDao
import com.example.t1.data.database.dao.DailyBehaviourDao
import com.example.t1.data.database.entity.FocusSessionEntity
import com.example.t1.data.database.entity.UserProfileEntity
import com.example.t1.data.database.entity.DailyUsageEntity
import com.example.t1.data.database.entity.DailyBehaviourEntity
import com.example.t1.data.database.entity.DailyBehaviourScoreEntity
import com.example.t1.data.database.entity.DailyFocusScoreEntity
import com.example.t1.data.database.dao.DailyBehaviourScoreDao
import com.example.t1.data.database.dao.DailyFocusScoreDao

@Database(
    entities = [
        UserProfileEntity::class,
        FocusSessionEntity::class,
        DailyUsageEntity::class,
        DailyBehaviourEntity::class,
        DailyBehaviourScoreEntity::class,
        DailyFocusScoreEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class T1Database : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun dailyUsageDao(): DailyUsageDao
    abstract fun dailyBehaviourDao(): DailyBehaviourDao
    abstract fun dailyBehaviourScoreDao(): DailyBehaviourScoreDao
    abstract fun dailyFocusScoreDao(): DailyFocusScoreDao
}

