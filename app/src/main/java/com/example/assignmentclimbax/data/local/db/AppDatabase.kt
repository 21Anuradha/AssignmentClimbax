package com.example.assignmentclimbax.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.assignmentclimbax.data.local.dao.CartDao
import com.example.assignmentclimbax.data.local.entity.CartEntity

@Database(
    entities = [CartEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
