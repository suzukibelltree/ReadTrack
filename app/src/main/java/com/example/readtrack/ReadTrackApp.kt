package com.example.readtrack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.readtrack.compose.BottomBar

/**
 * アプリのメイン画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadTrackApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    Scaffold(
        topBar = {
            if (currentRoute != null && currentRoute.substringAfter("Route.") != Route.Login.toString()) {
                TopAppBar(
                    title = {},
                )
            }
        },
        bottomBar = {
            if (currentRoute != null && currentRoute.substringAfter("Route.") != Route.Login.toString()) {
                BottomBar(navController = navController)
            }
        },
        floatingActionButton = {
            if (currentRoute != null && currentRoute.substringAfter("Route.") != Route.Login.toString()) {
                FloatingActionButton(
                    onClick = { navController.navigate(Route.RegisterProcess) },
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Image(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }
        }
    ) { innerPadding ->
        ReadTrackNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}


