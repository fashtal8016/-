package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.dao.RepairDao

object AppContainer {
    @Volatile
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "repair_db"
            )
                .fallbackToDestructiveMigration()
                .build()
            database = instance
            instance
        }
    }

    fun getRepairDao(context: Context): RepairDao {
        return getDatabase(context).repairDao()
    }
}
