package com.example.readtrack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.readtrack.compose.BottomBar
import com.example.readtrack.network.BookListViewModel
import com.example.readtrack.room.ReadLogsViewModel
import com.example.readtrack.room.SavedBooksViewModel

/**
 * アプリのメイン画面
 * @param viewModel 本のリストのViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadTrackApp(
    viewModel: BookListViewModel,
) {
    val savedBooksViewModel: SavedBooksViewModel = hiltViewModel()
    val readLogsViewModel: ReadLogsViewModel = hiltViewModel()
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        bottomBar = {
            BottomBar(navController)
        },
        floatingActionButton = {
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
    ) { innerPadding ->
        ReadTrackNavHost(
            navController = navController,
            bookListViewModel = viewModel,
            savedBooksViewModel = savedBooksViewModel,
            modifier = Modifier.padding(innerPadding),
            readLogsViewModel = readLogsViewModel
        )
    }
}


