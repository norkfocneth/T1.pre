package com.example.t1.di

import com.example.t1.data.repository.AuthRepositoryImpl
import com.example.t1.data.repository.LeaderboardRepositoryImpl
import com.example.t1.data.repository.UserRepositoryImpl
import com.example.t1.data.repository.UsageRepositoryImpl
import com.example.t1.data.repository.BehaviourRepositoryImpl
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.domain.repository.LeaderboardRepository
import com.example.t1.domain.repository.UserRepository
import com.example.t1.domain.repository.UsageRepository
import com.example.t1.domain.repository.BehaviourRepository
import com.example.t1.domain.repository.BehaviourScoreRepository
import com.example.t1.domain.repository.FocusScoreRepository
import com.example.t1.data.repository.BehaviourScoreRepositoryImpl
import com.example.t1.data.repository.FocusScoreRepositoryImpl
import com.example.t1.domain.repository.AppCategoryRepository
import com.example.t1.data.repository.AppCategoryRepositoryImpl
import com.example.t1.domain.repository.ResearchBenchmarkRepository
import com.example.t1.data.repository.ResearchBenchmarkRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAppCategoryRepository(
        impl: AppCategoryRepositoryImpl
    ): AppCategoryRepository

    @Binds
    @Singleton
    abstract fun bindResearchBenchmarkRepository(
        impl: ResearchBenchmarkRepositoryImpl
    ): ResearchBenchmarkRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindLeaderboardRepository(
        impl: LeaderboardRepositoryImpl
    ): LeaderboardRepository

    @Binds
    @Singleton
    abstract fun bindUsageRepository(
        impl: UsageRepositoryImpl
    ): UsageRepository

    @Binds
    @Singleton
    abstract fun bindBehaviourRepository(
        impl: BehaviourRepositoryImpl
    ): BehaviourRepository

    @Binds
    @Singleton
    abstract fun bindBehaviourScoreRepository(
        impl: BehaviourScoreRepositoryImpl
    ): BehaviourScoreRepository

    @Binds
    @Singleton
    abstract fun bindFocusScoreRepository(
        impl: FocusScoreRepositoryImpl
    ): FocusScoreRepository

    @Binds
    @Singleton
    abstract fun bindAuthProvider(
        impl: com.example.t1.data.remote.GoogleAuthProvider
    ): com.example.t1.data.remote.AuthProvider
}

