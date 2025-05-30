package com.belltree.readtrack

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
import com.belltree.readtrack.compose.home.HomeScreen
import com.belltree.readtrack.compose.home.HomeViewModel
import com.belltree.readtrack.compose.myBooks.LibraryScreen
import com.belltree.readtrack.compose.myBooks.MyBookScreen
import com.belltree.readtrack.compose.myBooks.MyBooksViewModel
import com.belltree.readtrack.compose.registerManually.ManualBookEntryScreen
import com.belltree.readtrack.compose.search.BookDetail
import com.belltree.readtrack.compose.search.RegisterProcessScreen
import com.belltree.readtrack.compose.search.SearchedBookDetailViewModel
import com.belltree.readtrack.compose.search.isbnSearch.BarcodeScannerScreen
import com.belltree.readtrack.compose.search.isbnSearch.ISBNSearchViewModel
import com.belltree.readtrack.compose.search.titleSearch.SearchScreen
import com.belltree.readtrack.compose.search.titleSearch.TitleSearchViewModel
import com.belltree.readtrack.compose.setting.SettingScreen

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
    val titleSearchViewModel: TitleSearchViewModel = hiltViewModel()
    val isbnSearchViewModel: ISBNSearchViewModel = hiltViewModel()
    val myBooksViewModel: MyBooksViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val searchedBookDetailViewModel: SearchedBookDetailViewModel = hiltViewModel()
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
        composable<Route.BarcodeScanner> {
            BarcodeScannerScreen(
                isbnSearchViewModel = isbnSearchViewModel,
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
                titleSearchViewModel = titleSearchViewModel,
                searchedBookDetailViewModel = searchedBookDetailViewModel
            )
        }
        composable(
            route = "${Route.BookDetail}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
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

