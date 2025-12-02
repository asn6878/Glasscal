package com.example.glasscal.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.glasscal.data.local.dao.TaskDao
import com.example.glasscal.data.local.entity.Task

/**
 * Room Database for Glasscal App
 * 앱의 로컬 데이터베이스
 */
@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "glasscal_database"
                )
                    .fallbackToDestructiveMigration() // 개발 중에는 간단하게 처리
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
