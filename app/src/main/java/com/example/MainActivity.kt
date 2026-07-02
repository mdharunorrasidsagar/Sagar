package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.HealthRepository
import com.example.presentation.HealthViewModel
import com.example.presentation.MainScreen
import com.example.ui.theme.HealthTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Database and Repository
        val database = AppDatabase.getDatabase(this)
        val repository = HealthRepository(database.healthDao())

        // Initialize ViewModel using Factory
        val viewModelFactory = HealthViewModel.Factory(application, repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[HealthViewModel::class.java]

        setContent {
            HealthTrackerTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
