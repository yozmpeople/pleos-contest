package com.yozmpeople.dontspill.di

import com.yozmpeople.dontspill.data.location.DefaultLocationRepository
import com.yozmpeople.dontspill.data.location.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    abstract fun bindLocationRepository(impl: DefaultLocationRepository): LocationRepository
}
