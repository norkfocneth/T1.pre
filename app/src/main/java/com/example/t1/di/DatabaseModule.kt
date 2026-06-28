package com.example.t1.di

import android.content.Context
import androidx.room.Room
import com.example.t1.data.database.T1Database
import com.example.t1.data.database.dao.FocusSessionDao
import com.example.t1.data.database.dao.UserProfileDao
import com.example.t1.data.database.dao.DailyUsageDao
import com.example.t1.data.database.dao.DailyBehaviourDao
import com.example.t1.data.database.migration.MIGRATION_1_2
import com.example.t1.data.database.migration.MIGRATION_2_3
import com.example.t1.data.database.dao.DailyBehaviourScoreDao
import com.example.t1.data.database.dao.DailyFocusScoreDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): T1Database {
        return Room.databaseBuilder(
            context,
            T1Database::class.java,
            "t1_database"
        )
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideUserProfileDao(database: T1Database): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    fun provideFocusSessionDao(database: T1Database): FocusSessionDao {
        return database.focusSessionDao()
    }

    @Provides
    fun provideDailyUsageDao(database: T1Database): DailyUsageDao {
        return database.dailyUsageDao()
    }

    @Provides
    fun provideDailyBehaviourDao(database: T1Database): DailyBehaviourDao {
        return database.dailyBehaviourDao()
    }

    @Provides
    fun provideDailyBehaviourScoreDao(database: T1Database): DailyBehaviourScoreDao {
        return database.dailyBehaviourScoreDao()
    }

    @Provides
    fun provideDailyFocusScoreDao(database: T1Database): DailyFocusScoreDao {
        return database.dailyFocusScoreDao()
    }
}

