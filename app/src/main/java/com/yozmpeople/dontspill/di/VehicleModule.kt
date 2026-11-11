package com.yozmpeople.dontspill.di

import com.yozmpeople.dontspill.data.vehicle.DefaultVehicleSignalRepository
import com.yozmpeople.dontspill.data.vehicle.VehicleSignalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class VehicleModule {

    @Binds
    abstract fun bindVehicleSignalRepository(impl: DefaultVehicleSignalRepository): VehicleSignalRepository
}
