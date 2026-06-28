package com.example.t1.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `daily_behaviour_score` (
                `userId` TEXT NOT NULL,
                `date` TEXT NOT NULL,
                `behaviourScore` INTEGER NOT NULL,
                `confidence` INTEGER NOT NULL,
                `generatedAt` INTEGER NOT NULL,
                `sourceVersion` TEXT NOT NULL,
                `verified` INTEGER NOT NULL,
                PRIMARY KEY(`userId`, `date`)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `daily_focus_score` (
                `userId` TEXT NOT NULL,
                `date` TEXT NOT NULL,
                `questionnaireScore` INTEGER NOT NULL,
                `behaviourScore` INTEGER NOT NULL,
                `confidence` INTEGER NOT NULL,
                `finalFocusScore` INTEGER NOT NULL,
                `trend` TEXT NOT NULL,
                `timeSaved` INTEGER NOT NULL,
                `generatedAt` INTEGER NOT NULL,
                `version` TEXT NOT NULL,
                `verified` INTEGER NOT NULL,
                `synced` INTEGER NOT NULL,
                PRIMARY KEY(`userId`, `date`)
            )
        """.trimIndent())
    }
}
