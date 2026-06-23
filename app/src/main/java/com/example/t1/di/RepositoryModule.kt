package com.example.t1.di

import com.example.t1.data.repository.AuthRepositoryImpl
import com.example.t1.data.repository.LeaderboardRepositoryImpl
import com.example.t1.data.repository.UserRepositoryImpl
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.domain.repository.LeaderboardRepository
import com.example.t1.domain.repository.UserRepository
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
}
