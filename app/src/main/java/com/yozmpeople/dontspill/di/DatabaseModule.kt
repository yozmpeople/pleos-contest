package com.yozmpeople.dontspill.di

import android.content.Context
import androidx.room3.Room
import com.yozmpeople.dontspill.data.driving.local.DrivingDao
import com.yozmpeople.dontspill.data.local.DontSpillDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "dontspill.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // 드라이버를 정하지 않으면 Room이 Android 기본 SQLite(AndroidSQLiteDriver)를 쓴다.
    // 배포 전이라 버전이 바뀌면 마이그레이션 없이 테이블을 새로 만든다. 지워지는 건 에뮬레이터 기록뿐이다.
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DontSpillDatabase =
        Room.databaseBuilder(context, DontSpillDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideDrivingDao(database: DontSpillDatabase): DrivingDao = database.drivingDao()
}
