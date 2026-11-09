package com.yozmpeople.dontspill.di

import com.yozmpeople.dontspill.BuildConfig
import com.yozmpeople.dontspill.data.network.createChaduckHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import javax.inject.Singleton
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    // 서버가 필드를 추가해도 앱이 깨지지 않게 모르는 키는 무시한다.
    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    // 연결 상태를 가진 객체라 앱 전체에서 하나만 만든다.
    @Provides
    @Singleton
    fun provideHttpClient(json: Json): HttpClient =
        createChaduckHttpClient(CIO.create(), BuildConfig.CHADUCK_BASE_URL, json)
}
