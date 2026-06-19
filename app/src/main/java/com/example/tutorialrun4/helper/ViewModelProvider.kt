package com.example.tutorialrun4.helper

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tutorialrun4.viewmodels.MainViewModel
import com.example.tutorialrun4.viewmodels.PlantListScreenViewModel

fun provideViewModel(): ViewModelProvider.Factory {
    return viewModelFactory {
        initializer {
            MainViewModel()
        }
    }
}