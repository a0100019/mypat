package com.a0100019.mypat.domain.alarm

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {
    @Provides
    @Singleton
    fun provideStepAlarmManager(@ApplicationContext context: Context): StepAlarmManager {
        return StepAlarmManager(context)
    }
}
