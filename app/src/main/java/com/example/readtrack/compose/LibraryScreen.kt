package com.example.readtrack.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readtrack.ReadTrackApplication
import com.example.readtrack.network.BookData
import com.example.readtrack.room.SavedBooksViewModel
import com.example.readtrack.room.SavedBooksViewModelFactory

@Composable
fun LibraryScreen(
    navController: NavController,
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val app = LocalContext.current.applicationContext as ReadTrackApplication
    val savedBooksViewModel: SavedBooksViewModel = viewModel(
        factory = SavedBooksViewModelFactory(app.appContainer.booksRepository)
    )
    val savedBooks by savedBooksViewModel.savedBooks.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            contentColor = Color.Blue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("未読") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("読書中") }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                text = { Text("読了") }
            )
        }
        LazyVerticalGrid(columns = GridCells.Adaptive(screenWidthDp/3 )) {
            items(savedBooks) { book ->
                AsyncImage(
                    model = book.thumbnail,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }
    }
}