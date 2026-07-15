package com.example.t1.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create token_wallets table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `token_wallets` (
                `userId` TEXT NOT NULL,
                `balance` INTEGER NOT NULL,
                `lifetimeEarned` INTEGER NOT NULL,
                `lifetimeSpent` INTEGER NOT NULL,
                `lastUpdated` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                PRIMARY KEY(`userId`)
            )
        """.trimIndent())

        // Create token_transactions table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `token_transactions` (
                `transactionId` TEXT NOT NULL,
                `userId` TEXT NOT NULL,
                `amount` INTEGER NOT NULL,
                `type` TEXT NOT NULL,
                `reason` TEXT NOT NULL,
                `timestamp` INTEGER NOT NULL,
                `status` TEXT NOT NULL,
                `source` TEXT NOT NULL,
                `hashPlaceholder` TEXT NOT NULL DEFAULT '',
                `chainStatus` TEXT NOT NULL DEFAULT '',
                `walletVersion` INTEGER NOT NULL DEFAULT 1,
                PRIMARY KEY(`transactionId`)
            )
        """.trimIndent())

        // Create index on userId
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_token_transactions_userId` ON `token_transactions` (`userId`)")
    }
}
