package com.belltree.readtrack.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.belltree.readtrack.ui.home.HomeScreen
import com.belltree.readtrack.ui.library.LibraryScreen
import com.belltree.readtrack.ui.mybookdetail.MyBookScreen
import com.belltree.readtrack.ui.registermanually.ManualBookEntryScreen
import com.belltree.readtrack.ui.search.BookDetail
import com.belltree.readtrack.ui.search.RegisterProcessScreen
import com.belltree.readtrack.ui.search.SearchedBookDetailViewModel
import com.belltree.readtrack.ui.search.isbn.BarcodeScannerScreen
import com.belltree.readtrack.ui.search.title.SearchScreen
import com.belltree.readtrack.ui.setting.SettingScreen

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
    val searchedBookDetailViewModel: SearchedBookDetailViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                navController = navController,
            )
        }
        composable<Route.Library> {
            LibraryScreen(navController = navController)
        }
        composable<Route.Setting> {
            SettingScreen(navController = navController)
        }
        composable<Route.RegisterProcess> {
            RegisterProcessScreen(navController = navController)
        }
        composable<Route.BarcodeScanner> {
            BarcodeScannerScreen(
                searchedBookDetailViewModel = searchedBookDetailViewModel,
                navController = navController
            )
        }
        composable<Route.RegisterManually> {
            ManualBookEntryScreen(
                navController = navController
            )
        }
        composable<Route.Search> {
            SearchScreen(
                navController = navController,
                searchedBookDetailViewModel = searchedBookDetailViewModel
            )
        }
        composable(
            route = "${Route.BookDetail}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.Companion.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("bookId") ?: return@composable
            val bookItem by searchedBookDetailViewModel.bookItem

            if (bookItem != null) {
                BookDetail(
                    viewmodel = searchedBookDetailViewModel,
                    navController = navController,
                )
            } else {
                // 読み込み中表示やエラー処理
                Text("読み込み中...")
            }

        }
        composable(
            route = "${Route.MyBook}/{savedBookId}",
            arguments = listOf(navArgument("savedBookId") { type = NavType.Companion.StringType })
        ) { backStackEntry ->
            val savedBookId = backStackEntry.arguments?.getString("savedBookId") ?: ""
            MyBookScreen(
                navController = navController,
                bookId = savedBookId,
            )
        }
    }
}