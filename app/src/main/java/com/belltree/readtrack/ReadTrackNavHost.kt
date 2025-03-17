package com.belltree.readtrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.belltree.readtrack.compose.BookDetail
import com.belltree.readtrack.compose.HomeScreen
import com.belltree.readtrack.compose.LibraryScreen
import com.belltree.readtrack.compose.MyBookScreen
import com.belltree.readtrack.compose.RegisterProcessScreen
import com.belltree.readtrack.compose.SearchScreen
import com.belltree.readtrack.compose.SettingScreen
import com.belltree.readtrack.network.BookListViewModel
import com.belltree.readtrack.room.HomeViewModel
import com.belltree.readtrack.room.MyBooksViewModel

/**
 * アプリの画面遷移を管理する
 * @param navController ナビゲーションコントローラー
 * @param modifier Modifier
 */
@Composable
fun ReadTrackNavHost(
    navController: NavHostController,
    modifier: Modifier
) {
    val bookListViewModel: BookListViewModel = hiltViewModel()
    val myBooksViewModel: MyBooksViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                navController = navController,
                homeViewModel = homeViewModel,
            )
        }
        composable<Route.Library> {
            LibraryScreen(navController = navController, myBooksViewModel = myBooksViewModel)
        }
        composable<Route.Setting> {
            SettingScreen(navController = navController)
        }
        composable<Route.RegisterProcess> {
            RegisterProcessScreen(navController = navController)
        }
        composable<Route.Search> {
            SearchScreen(navController = navController, viewModel = bookListViewModel)
        }
        composable(
            route = "${Route.BookDetail}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            val bookItem = bookListViewModel.fetchBookById(bookId)
            if (bookItem != null) {
                BookDetail(navController = navController, bookItem = bookItem)
            } else {
                // エラーハンドリング
            }
        }
        composable(
            route = "${Route.MyBook}/{savedBookId}",
            arguments = listOf(navArgument("savedBookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val savedBookId = backStackEntry.arguments?.getString("savedBookId") ?: ""
            MyBookScreen(
                navController = navController,
                bookId = savedBookId,
                myBooksViewModel = myBooksViewModel
            )
        }
    }
}

