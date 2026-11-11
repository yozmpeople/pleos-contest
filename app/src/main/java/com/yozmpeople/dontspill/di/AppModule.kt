package com.yozmpeople.dontspill.di

import android.os.SystemClock
import com.yozmpeople.dontspill.common.ApplicationScope
import com.yozmpeople.dontspill.common.ElapsedRealtimeClock
import com.yozmpeople.dontspill.common.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    fun provideElapsedRealtimeClock(): ElapsedRealtimeClock = ElapsedRealtimeClock { SystemClock.elapsedRealtime() }
}
