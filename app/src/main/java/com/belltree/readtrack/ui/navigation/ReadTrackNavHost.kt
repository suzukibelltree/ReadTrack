package com.belltree.readtrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
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
        composable<Route.BookDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.BookDetail>()
            SearchedBookDetailScreen(
                bookId = route.bookId,
                navController = navController,
            )
        }
        composable<Route.MyBook> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.MyBook>()
            MyBookScreen(
                navController = navController,
                bookId = route.savedBookId,
            )
        }
    }
}