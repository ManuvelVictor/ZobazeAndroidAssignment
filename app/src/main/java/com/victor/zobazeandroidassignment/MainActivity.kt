package com.victor.zobazeandroidassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.victor.zobazeandroidassignment.navigation.NavGraph
import com.victor.zobazeandroidassignment.ui.theme.ZobazeAndroidAssignmentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZobazeAndroidAssignmentTheme {
                NavGraph()
            }
        }
    }
}