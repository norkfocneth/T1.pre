package com.example.t1.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `daily_usage` (
                `userId` TEXT NOT NULL,
                `date` TEXT NOT NULL,
                `totalScreenTimeMs` INTEGER NOT NULL,
                `unlockCount` INTEGER NOT NULL,
                `appOpenCount` INTEGER NOT NULL,
                `foregroundSessionsJson` TEXT NOT NULL,
                `firstAppOpenTime` INTEGER,
                `lastAppCloseTime` INTEGER,
                `collectionTimestamp` INTEGER NOT NULL,
                `isVerified` INTEGER NOT NULL,
                PRIMARY KEY(`userId`, `date`)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `daily_behaviour` (
                `userId` TEXT NOT NULL,
                `date` TEXT NOT NULL,
                `totalScreenTimeMs` INTEGER NOT NULL,
                `socialTimeMs` INTEGER NOT NULL,
                `entertainmentTimeMs` INTEGER NOT NULL,
                `productiveTimeMs` INTEGER NOT NULL,
                `educationTimeMs` INTEGER NOT NULL,
                `communicationTimeMs` INTEGER NOT NULL,
                `financeTimeMs` INTEGER NOT NULL,
                `healthTimeMs` INTEGER NOT NULL,
                `developmentTimeMs` INTEGER NOT NULL,
                `otherTimeMs` INTEGER NOT NULL,
                `unlockCount` INTEGER NOT NULL,
                `appOpenCount` INTEGER NOT NULL,
                `foregroundSessionCount` INTEGER NOT NULL,
                `topUsedAppsJson` TEXT NOT NULL,
                `collectionTimestamp` INTEGER NOT NULL,
                PRIMARY KEY(`userId`, `date`)
            )
        """.trimIndent())
    }
}
