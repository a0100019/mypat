package com.a0100019.mypat.data.di

import com.a0100019.mypat.data.usecase.CombineNumberUseCaseImpl
import com.a0100019.mypat.domain.CombineNumberUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MainModule {

    @Binds
    abstract fun bindCombineNumberUseCase(uc: CombineNumberUseCaseImpl): CombineNumberUseCase

}