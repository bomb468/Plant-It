package com.example.tutorialrun4.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Plant::class, Reminder::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}
