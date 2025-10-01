package com.example.controller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val midiController = MidiController(this)
        val viewModel: DJViewModel by viewModels { 
            object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(DJViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return DJViewModel(midiController) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
        setContent {
            DJControllerScreen(viewModel)
        }
    }
}