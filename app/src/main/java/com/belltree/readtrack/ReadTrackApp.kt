package com.belltree.readtrack

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.belltree.readtrack.compose.TopTextList
import com.belltree.readtrack.datastore.getValue
import com.belltree.readtrack.themecolor.AppColors
import com.belltree.readtrack.themecolor.getPrimaryColor
import com.belltree.readtrack.ui.theme.LightBlue
import kotlinx.coroutines.launch

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "character_size")

/**
 * アプリのメイン画面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadTrackApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route?.substringAfter("Route.")
    val topBarTitle = getTopBarTitle(currentRoute)
    val context = LocalContext.current
    val themeColor by getValue(context, "theme_color").collectAsState(initial = "")
    val primaryColor = getPrimaryColor(isSystemInDarkTheme(), themeColor)
    Log.d("primaryColor", primaryColor.toString())
    val scrimColor = AppColors.scrimColor
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                drawerState = drawerState,
                navController = navController,
            )
        },
        scrimColor = scrimColor
    ) {
        Scaffold(
            topBar = {
                if (currentRoute != Route.Login.toString()) {
                    TopAppBar(
                        title = {
                            Text(
                                text = topBarTitle,
                                modifier = Modifier.padding(16.dp)
                            )
                        },
                        navigationIcon = {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "メニュー",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(32.dp)
                                    .clickable {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = getPrimaryColor(isSystemInDarkTheme(), themeColor),
                        )
                    )
                }
            },
            floatingActionButton = {
                if (currentRoute != Route.Login.toString()) {
                    FloatingActionButton(
                        onClick = { navController.navigate(Route.RegisterProcess) },
                        containerColor = LightBlue,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Image(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add"
                        )
                    }
                }
            }
        ) { innerPadding ->
            ReadTrackNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
fun DrawerContent(
    drawerState: DrawerState,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    val menuItems = listOf(
        stringResource(R.string.drawer_home) to Route.Home,
        stringResource(R.string.drawer_library) to Route.Library,
        stringResource(R.string.drawer_setting) to Route.Setting,
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        menuItems.forEach { (title, route) ->
            ListItem(
                modifier = Modifier
                    .clickable {
                        scope.launch { drawerState.close() }
                        navController.navigate(route)
                    }
                    .padding(8.dp),
                colors = ListItemDefaults.colors(
                    containerColor = AppColors.containerColor
                ),
                headlineContent = {
                    Text(
                        text = title,
                    )
                },
            )
            HorizontalDivider()
        }
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

