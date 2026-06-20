package com.example.tutorialrun4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.tutorialrun4.helper.provideViewModel
import com.example.tutorialrun4.screens.PlantListScreen
import com.example.tutorialrun4.screens.PlantScreen
import com.example.tutorialrun4.ui.theme.TutorialRun4Theme
import com.example.tutorialrun4.viewmodels.MainViewModel
import com.example.tutorialrun4.viewmodels.Screens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TutorialRun4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val viewModel : MainViewModel = viewModel(factory = provideViewModel())
    NavDisplay(
        backStack = viewModel.backStack,
        onBack = { viewModel.backStack.removeLastOrNull() },
        entryDecorators = listOf(
            // Add the default decorators for managing scenes and saving state
            rememberSaveableStateHolderNavEntryDecorator(),
            // Then add the view model store decorator
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = { key ->
            when (key) {
                is Screens.PlantListScreen -> NavEntry(key) {
                    PlantListScreen(
                        onPlantClick = { id ->
                            viewModel.backStack.add(Screens.PlantScreen(id))
                        },
                        modifier = modifier
                    )
                }
                is Screens.PlantScreen -> NavEntry(key) {
                    PlantScreen(
                        id = key.id,
                        onBackClick = { viewModel.backStack.removeLastOrNull() }
                    )
                }
            }
        }
    )
}
