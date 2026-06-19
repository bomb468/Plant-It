package com.example.tutorialrun4.koin

import androidx.room.Room
import com.example.tutorialrun4.repository.PlantRepository
import com.example.tutorialrun4.room.AppDatabase
import com.example.tutorialrun4.room.PlantDao
import com.example.tutorialrun4.viewmodels.PlantListScreenViewModel
import com.example.tutorialrun4.viewmodels.PlantScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
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
    single<PlantRepository>{
        PlantRepository(get(), androidContext())
    }
    viewModel {
        PlantListScreenViewModel(get())
    }
    viewModel { params ->
        PlantScreenViewModel(
            plantRepository = get(),
            id = params.get()
        )
    }
}