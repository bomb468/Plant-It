package com.example.tutorialrun4.viewmodels

import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class MainViewModel() : ViewModel() {
    val backStack = mutableStateListOf<Screens>(Screens.PlantListScreen())
}

sealed class Screens() {
    class PlantListScreen() : Screens()
    class PlantScreen(val id : Long) : Screens()
}