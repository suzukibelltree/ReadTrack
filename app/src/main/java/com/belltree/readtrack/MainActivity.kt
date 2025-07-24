package com.belltree.readtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.belltree.readtrack.initialization.AppInitializer
import com.belltree.readtrack.ui.theme.ReadTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReadTrackTheme {
                AppInitializer {
                    ReadTrackApp()
                }
            }
        }
    }
}