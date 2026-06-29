package com.example.t1.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Alter user_profile table to add streak and lastActiveDate
        db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `streak` INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `lastActiveDate` TEXT")
        db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `behaviourScore` INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `socialRatio` REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `productivityRatio` REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `totalFocusSessions` INTEGER NOT NULL DEFAULT 0")

        // Create leaderboard_daily table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `leaderboard_daily` (
                `snapshotDate` TEXT NOT NULL,
                `userId` TEXT NOT NULL,
                `username` TEXT NOT NULL,
                `displayName` TEXT,
                `focusScore` INTEGER NOT NULL,
                `behaviourScore` INTEGER NOT NULL,
                `percentile` INTEGER NOT NULL,
                `rank` INTEGER NOT NULL,
                `rankMovement` TEXT NOT NULL,
                `badge` TEXT NOT NULL,
                `rankReason` TEXT NOT NULL,
                `streak` INTEGER NOT NULL DEFAULT 0,
                `createdAt` INTEGER NOT NULL,
                PRIMARY KEY(`snapshotDate`, `userId`)
            )
        """.trimIndent())
    }
}
