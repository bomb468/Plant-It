package com.example.tutorialrun4.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutorialrun4.repository.PlantData
import com.example.tutorialrun4.repository.PlantRepository
import com.example.tutorialrun4.room.Plant
import com.example.tutorialrun4.room.Reminder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class PlantScreenViewModel(
    private val plantRepository: PlantRepository,
    private val id: Long
) : ViewModel() {

    // Internal state for editing the plant
    var editedName by mutableStateOf("")
        private set
    
    var editedImageFile by mutableStateOf<File?>(null)
        private set

    // Expose a clean StateFlow of PlantData directly to the UI
    val plantState: StateFlow<PlantData> = plantRepository.plantDataList
        .map { allPlants ->
            val plant = allPlants.find { it.id == id } ?: createDefaultPlant()
            if (editedName.isEmpty() && plant.id != 0L) {
                editedName = plant.plantName
            }
            if (editedImageFile == null && plant.id != 0L) {
                editedImageFile = plant.imageFile
            }
            plant
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = createDefaultPlant()
        )

    fun onNameChange(newName: String) {
        editedName = newName
    }

    fun onImageChange(file: File?) {
        editedImageFile = file
    }

    private fun createDefaultPlant(): PlantData {
        return PlantData(
            id = 0L,
            plantName = "",
            imageFile = null,
            reminderList = emptyList()
        )
    }

    fun savePlant(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val imageName = editedImageFile?.absolutePath
            if (id == 0L) {
                plantRepository.addPlant(Plant(name = editedName, imageName = imageName))
            } else {
                val existingPlant = plantRepository.getPlantById(id)
                if (existingPlant != null) {
                    plantRepository.updatePlant(existingPlant.copy(name = editedName, imageName = imageName))
                }
            }
            onSuccess()
        }
    }

    fun deletePlant(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val existingPlant = plantRepository.getPlantById(id)
            if (existingPlant != null) {
                // Delete physical file if exists
                existingPlant.imageName?.let { path ->
                    val file = File(path)
                    if (file.exists()) file.delete()
                }
                plantRepository.deletePlant(existingPlant)
            }
            onSuccess()
        }
    }

    fun addReminder(dayOfWeek: Int, hour: Int, minute: Int) {
        viewModelScope.launch {
            if (id != 0L) {
                plantRepository.addReminder(
                    Reminder(
                        plantId = id,
                        dayOfWeek = dayOfWeek,
                        hour = hour,
                        minute = minute
                    )
                )
            }
        }
    }

    fun deleteReminder(reminderId: Int) {
        viewModelScope.launch {
            plantRepository.deleteReminderById(reminderId)
        }
    }
}
