package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.RepairDao
import com.example.data.local.entity.CustomerEntity
import com.example.data.local.entity.DeviceOrderEntity

@Database(entities = [CustomerEntity::class, DeviceOrderEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repairDao(): RepairDao
}
