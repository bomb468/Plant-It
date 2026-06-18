package com.example.tutorialrun4.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutorialrun4.room.Plant
import com.example.tutorialrun4.room.PlantDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantListScreenViewModel(
    val dao: PlantDao
): ViewModel() {
    val plantList: StateFlow<List<Plant>> = dao.getAllPlants()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keeps upstream flow alive for 5s after UI disconnects
            initialValue = emptyList() // The initial state before Room loads the data
        )
}