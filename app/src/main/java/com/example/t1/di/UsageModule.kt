package com.example.t1.di

import com.example.t1.data.permission.UsagePermissionManagerImpl
import com.example.t1.domain.permission.UsagePermissionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UsageModule {

    @Binds
    @Singleton
    abstract fun bindUsagePermissionManager(
        impl: UsagePermissionManagerImpl
    ): UsagePermissionManager
}
