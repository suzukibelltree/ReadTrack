package com.example.readtrack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.readtrack.compose.HomeScreen
import com.example.readtrack.compose.LibraryScreen
import com.example.readtrack.compose.RegisterProcessScreen
import com.example.readtrack.compose.SearchScreen
import com.example.readtrack.compose.SettingScreen
import com.example.readtrack.network.BooksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadTrackApp(
    viewModel: BooksViewModel
) {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = "${currentRoute(navController)} Screen")
                }
            )
        },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ReadTrackScreen.RegistProcess.name) },
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ReadTrackScreen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ReadTrackScreen.Home.name) {
                HomeScreen(navController)
            }
            composable(ReadTrackScreen.Library.name) {
                LibraryScreen(navController)
            }
            composable(ReadTrackScreen.Setting.name) {
                SettingScreen(navController)
            }
            composable(ReadTrackScreen.RegistProcess.name) {
                RegisterProcessScreen(navController)
            }
            composable(ReadTrackScreen.Search.name) {
                SearchScreen(viewModel)
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
