package com.example.t1.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add V2 demographic fields (NEVER used for score calculation)
        db.execSQL("ALTER TABLE user_profile ADD COLUMN age INTEGER DEFAULT NULL")
        db.execSQL("ALTER TABLE user_profile ADD COLUMN gender TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE user_profile ADD COLUMN occupation TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE user_profile ADD COLUMN primaryGoal TEXT DEFAULT NULL")

        // Add V2 migration tracking fields
        db.execSQL("ALTER TABLE user_profile ADD COLUMN focusScoreVersion INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE user_profile ADD COLUMN onboardingCompletedV2 INTEGER NOT NULL DEFAULT 0")
    }
}
