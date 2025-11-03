package com.belltree.readtrack.app

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.belltree.readtrack.R
import com.belltree.readtrack.data.local.datastore.getValue
import com.belltree.readtrack.themecolor.getPrimaryColor
import com.belltree.readtrack.ui.TopTextList
import com.belltree.readtrack.ui.navigation.BottomBar
import com.belltree.readtrack.ui.navigation.ReadTrackNavHost
import com.belltree.readtrack.ui.navigation.Route

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "character_size")
val Context.LoginDataStore: DataStore<Preferences> by preferencesDataStore(name = "login_prefs")

/**
 * アプリのメイン画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadTrackApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route?.substringAfter("Route.")
    val topBarTitle = getTopBarTitle(currentRoute)
    val context = LocalContext.current
    val themeColor by getValue(context, "theme_color").collectAsState(initial = "")
    val primaryColor = getPrimaryColor(isSystemInDarkTheme(), themeColor)
    val topLevelRoutes = setOf(
        Route.Home.toString(),
        Route.Library.toString(),
        Route.Setting.toString()
    )
    Scaffold(
        modifier = Modifier.Companion
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        modifier = Modifier.Companion.padding(16.dp)
                    )
                },
                navigationIcon = {
                    // ルートによって表示するアイコンを切り替える
                    if (currentRoute !in topLevelRoutes) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "戻る",
                            modifier = Modifier.Companion
                                .padding(16.dp)
                                .size(32.dp)
                                .clickable {
                                    navController.popBackStack()
                                }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                ),
                windowInsets = WindowInsets.Companion.statusBars
            )
        },
        bottomBar = {
            if (currentRoute != Route.Search.toString() && currentRoute != Route.BarcodeScanner.toString()) {
                BottomBar(navController, primaryColor)
            }

        },
        floatingActionButton = {
            if (currentRoute == Route.Home.toString() || currentRoute == Route.Library.toString()) {
                FloatingActionButton(
                    onClick = { navController.navigate(Route.RegisterProcess) },
                    containerColor = primaryColor,
                    modifier = Modifier.Companion.padding(8.dp)
                ) {
                    Image(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.Companion.navigationBars
    ) { innerPadding ->
        ReadTrackNavHost(
            navController = navController,
            modifier = Modifier.Companion.padding(innerPadding),
        )
    }
}

@Composable
fun getTopBarTitle(currentRoute: String?): String {
    val context = LocalContext.current
    return when (currentRoute) {
        Route.Home.toString() -> context.getString(TopTextList.Home.resId)
        Route.Library.toString() -> context.getString(TopTextList.Library.resId)
        Route.RegisterProcess.toString() -> context.getString(TopTextList.RegisterProcess.resId)
        Route.Setting.toString() -> context.getString(TopTextList.Setting.resId)
        Route.Search.toString() -> context.getString(TopTextList.Search.resId)
        Route.BookDetail.toString() -> context.getString(TopTextList.BookDetail.resId)
        Route.BarcodeScanner.toString() -> context.getString(TopTextList.BarcodeScanner.resId)
        Route.RegisterManually.toString() -> context.getString(TopTextList.ManualEntry.resId)
        else -> stringResource(R.string.app_name)
    }
}