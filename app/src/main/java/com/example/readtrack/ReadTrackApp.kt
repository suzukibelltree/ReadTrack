package com.example.readtrack

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.readtrack.compose.HomeScreen
import com.example.readtrack.compose.LibraryScreen
import com.example.readtrack.compose.SettingScreen

@Composable
fun ReadTrackApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.LightGray,
            ) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { navController.navigate(ReadTrackScreen.Home.name) }) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { navController.navigate(ReadTrackScreen.Library.name) }) {
                    Icon(Icons.Default.Menu, contentDescription = "Library")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { navController.navigate(ReadTrackScreen.Setting.name) }) {
                    Icon(Icons.Default.Settings, contentDescription = "Setting")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ReadTrackScreen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ReadTrackScreen.Home.name) {
                HomeScreen()
            }
            composable(ReadTrackScreen.Library.name) {
                LibraryScreen()
            }
            composable(ReadTrackScreen.Setting.name) {
                SettingScreen()
            }
        }
    }
}
