package com.yozmpeople.dontspill.di

import com.yozmpeople.dontspill.data.driving.DefaultDrivingRepository
import com.yozmpeople.dontspill.data.driving.DrivingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DrivingModule {

    @Binds
    abstract fun bindDrivingRepository(impl: DefaultDrivingRepository): DrivingRepository
}
