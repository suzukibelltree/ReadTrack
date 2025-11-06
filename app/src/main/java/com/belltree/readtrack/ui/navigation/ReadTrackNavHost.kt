package com.belltree.readtrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.belltree.readtrack.ui.home.HomeScreen
import com.belltree.readtrack.ui.library.LibraryScreen
import com.belltree.readtrack.ui.mybookdetail.MyBookScreen
import com.belltree.readtrack.ui.registermanually.ManualBookEntryScreen
import com.belltree.readtrack.ui.search.RegisterProcessScreen
import com.belltree.readtrack.ui.search.SearchedBookDetailScreen
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
            )
        }
        composable(
            route = "${Route.BookDetail}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.Companion.StringType })
        ) {
            val bookId = it.arguments?.getString("bookId") ?: ""
            SearchedBookDetailScreen(
                bookId = bookId,
                navController = navController,
            )
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