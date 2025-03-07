package com.example.readtrack

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.readtrack.authentication.FirebaseAuthScreen
import com.example.readtrack.compose.BookDetail
import com.example.readtrack.compose.HomeScreen
import com.example.readtrack.compose.LibraryScreen
import com.example.readtrack.compose.MyBookScreen
import com.example.readtrack.compose.RegisterProcessScreen
import com.example.readtrack.compose.SearchScreen
import com.example.readtrack.compose.SettingScreen
import com.example.readtrack.network.BookListViewModel
import com.example.readtrack.room.HomeViewModel
import com.example.readtrack.room.MyBooksViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * アプリの画面遷移を管理する
 * @param navController ナビゲーションコントローラー
 * @param modifier Modifier
 */
@Composable
fun ReadTrackNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val bookListViewModel: BookListViewModel = hiltViewModel()
    val myBooksViewModel: MyBooksViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    var currentUser = FirebaseAuth.getInstance().currentUser
    LaunchedEffect(currentUser) {
        Log.d("hoge", "hoge")
        if (currentUser == null) {
            navController.navigate(Route.Login) {
                popUpTo("home") { inclusive = true }
            }
        } else {
            navController.navigate(Route.Home) {
                popUpTo("login") { inclusive = true }
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = if (currentUser == null) Route.Login else Route.Home,
        modifier = modifier
    ) {
        composable<Route.Login> {
            FirebaseAuthScreen {
                currentUser = it
            }
        }
        composable<Route.Home> {
            HomeScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                user = currentUser!!
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

