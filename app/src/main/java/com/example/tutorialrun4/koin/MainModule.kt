package com.example.tutorialrun4.koin

import androidx.room.Room
import com.example.tutorialrun4.room.AppDatabase
import com.example.tutorialrun4.room.PlantDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mainModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "plant_database"
        ).build()
    }
    single<PlantDao> {
        get<AppDatabase>().plantDao()
    }
}