package com.example.readtrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.readtrack.compose.BookDetail
import com.example.readtrack.compose.HomeScreen
import com.example.readtrack.compose.LibraryScreen
import com.example.readtrack.compose.MyBookScreen
import com.example.readtrack.compose.RegisterProcessScreen
import com.example.readtrack.compose.SearchScreen
import com.example.readtrack.compose.SettingScreen
import com.example.readtrack.network.BookListViewModel
import com.example.readtrack.room.ReadLogsViewModel
import com.example.readtrack.room.SavedBooksViewModel

/**
 * アプリの画面遷移を管理する
 * @param navController ナビゲーションコントローラー
 * @param viewModel 本のリストのViewModel
 * @param savedBooksViewModel 保存された本のViewModel
 * @param modifier Modifier
 */
@Composable
fun ReadTrackNavHost(
    navController: NavHostController,
    viewModel: BookListViewModel,
    savedBooksViewModel: SavedBooksViewModel,
    readLogsViewModel: ReadLogsViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(navController, savedBooksViewModel, readLogsViewModel)
        }
        composable<Route.Library> {
            LibraryScreen(navController, savedBooksViewModel)
        }
        composable<Route.Setting> {
            SettingScreen(navController)
        }
        composable<Route.RegisterProcess> {
            RegisterProcessScreen(navController)
        }
        composable<Route.Search> {
            SearchScreen(viewModel, navController)
        }
        composable(
            route = "${Route.BookDetail}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            val bookItem = viewModel.fetchBookById(bookId)
            if (bookItem != null) {
                BookDetail(navController, bookItem)
            } else {
                // エラーハンドリング
            }
        }
        composable(
            route = "${Route.MyBook}/{savedBookId}",
            arguments = listOf(navArgument("savedBookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val savedBookId = backStackEntry.arguments?.getString("savedBookId") ?: ""
            MyBookScreen(savedBookId, savedBooksViewModel, readLogsViewModel, navController)
        }
    }
}

