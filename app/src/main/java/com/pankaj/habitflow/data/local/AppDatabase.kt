package com.pankaj.habitflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pankaj.habitflow.data.local.dao.HabitDao
import com.pankaj.habitflow.data.local.dao.HabitRecordDao
import com.pankaj.habitflow.data.local.entity.HabitEntity
import com.pankaj.habitflow.data.local.entity.HabitRecordEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitRecordEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitRecordDao(): HabitRecordDao

    companion object {
        const val DATABASE_NAME = "habitflow_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
