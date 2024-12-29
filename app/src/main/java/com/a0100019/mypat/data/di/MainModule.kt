package com.a0100019.mypat.data.di

import android.app.Application
import android.content.Context
import com.a0100019.mypat.data.usecase.CombineNumberUseCaseImpl
import com.a0100019.mypat.domain.CombineNumberUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MainModule {

    @Binds
    abstract fun bindContext(application: Application): Context

    @Binds
    abstract fun bindCombineNumberUseCase(uc: CombineNumberUseCaseImpl): CombineNumberUseCase

}