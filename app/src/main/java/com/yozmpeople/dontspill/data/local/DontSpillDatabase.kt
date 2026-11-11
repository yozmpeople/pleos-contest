package com.yozmpeople.dontspill.data.local

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.yozmpeople.dontspill.data.driving.local.DrivingDao
import com.yozmpeople.dontspill.data.driving.local.DrivingEntity

@Database(entities = [DrivingEntity::class], version = 1)
abstract class DontSpillDatabase : RoomDatabase() {
    abstract fun drivingDao(): DrivingDao
}
