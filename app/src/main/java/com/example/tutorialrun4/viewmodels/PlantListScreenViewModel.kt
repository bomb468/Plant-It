package com.example.tutorialrun4.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutorialrun4.repository.PlantRepository
import com.example.tutorialrun4.room.Plant
import com.example.tutorialrun4.room.PlantDao
import com.example.tutorialrun4.room.Reminder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.io.File

class PlantListScreenViewModel(
    val plantRepository: PlantRepository
): ViewModel() {
    val plantList = plantRepository.plantDataList.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}