package com.example.readtrack

import com.example.readtrack.compose.MyBookScreen
import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.readtrack.compose.BookDetail
import com.example.readtrack.compose.HomeScreen
import com.example.readtrack.compose.LibraryScreen
import com.example.readtrack.compose.RegisterProcessScreen
import com.example.readtrack.compose.SearchScreen
import com.example.readtrack.compose.SettingScreen
import com.example.readtrack.network.BookListViewModel
import com.example.readtrack.network.BookViewModel
import com.example.readtrack.network.BookViewModelFactory
import com.example.readtrack.room.SavedBooksViewModel
import com.example.readtrack.room.SavedBooksViewModelFactory

/**
 * ReadTrackアプリケーションの画面遷移を管理する
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadTrackApp(
    viewModel: BookListViewModel,
) {
    val app = LocalContext.current.applicationContext as ReadTrackApplication
    val savedBooksViewModel: SavedBooksViewModel = viewModel(
        factory = SavedBooksViewModelFactory(app.appContainer.booksRepository)
    )
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
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Spacer(modifier = Modifier.weight(0.5f))
                Column(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { navController.navigate(ReadTrackScreen.Home.name) }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                    Text("ホーム", fontSize = 12.sp, color = Color.Black)
                }
                Spacer(modifier = Modifier.weight(0.5f))
                Column(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { navController.navigate(ReadTrackScreen.Library.name) }) {
                        Icon(Icons.Default.Menu, contentDescription = "Library")
                    }
                    Text("ライブラリ", fontSize = 12.sp, color = Color.Black)
                }
                Spacer(modifier = Modifier.weight(0.5f))
                Column(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { navController.navigate(ReadTrackScreen.Setting.name) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Setting")
                    }
                    Text("設定", fontSize = 12.sp, color = Color.Black)
                }
                Spacer(modifier = Modifier.weight(0.5f))
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ReadTrackScreen.registProcess.name) },
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
                HomeScreen(navController,savedBooksViewModel)
            }
            composable(ReadTrackScreen.Library.name) {
                LibraryScreen(navController,savedBooksViewModel)
            }
            composable(ReadTrackScreen.Setting.name) {
                SettingScreen(navController)
            }
            composable(ReadTrackScreen.registProcess.name) {
                RegisterProcessScreen(navController)
            }
            composable(ReadTrackScreen.Search.name) {
                SearchScreen(viewModel,navController)
            }
            composable(
                route = "${ReadTrackScreen.BookDetail.name}/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ){ backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                val bookItem = viewModel.books.find { it.id == bookId }
                val bookViewModel: BookViewModel = viewModel(
                    factory = bookItem?.let { BookViewModelFactory(it) }
                )
                Log.d("ReadTrackApp", "BookViewModel retrieved with key: $bookId")
                BookDetail(navController, bookViewModel)
            }
            composable(
                route = "${ReadTrackScreen.MyBook.name}/{savedBookId}",
                arguments = listOf(navArgument("savedBookId") { type = NavType.StringType })
            ){ backStackEntry ->
                val savedBookId = backStackEntry.arguments?.getString("savedBookId") ?: ""
                MyBookScreen(savedBookId, savedBooksViewModel,navController)
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
