package com.example.tutorialrun4.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutorialrun4.helper.provideViewModel
import com.example.tutorialrun4.viewmodels.PlantListScreenViewModel

@Composable
fun PlantListScreen(){
    val plantListScreenViewModel = viewModel<PlantListScreenViewModel>(factory = provideViewModel())
    Column(
        modifier= Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Plant List")
    }
}