package com.belltree.readtrack.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun BottomBar(
    navController: NavHostController,
    containerColor: androidx.compose.ui.graphics.Color
) {
    val items = listOf(
        BottomNavItem(Route.Home, Icons.Default.Home, "ホーム"),
        BottomNavItem(Route.Library, Icons.Default.MenuBook, "ライブラリ"),
        BottomNavItem(Route.Setting, Icons.Default.Settings, "設定")
    )
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRouteString = currentBackStackEntry?.destination?.route
    NavigationBar(
        containerColor = containerColor
    ) {

        items.forEach { item ->
            val itemRouteString = Json.encodeToString(item.route)
            NavigationBarItem(
                selected = currentRouteString == itemRouteString,
                onClick = {
                    if (currentRouteString != itemRouteString) {
                        navController.navigate(item.route) {
                            // バックスタックを整理
                            popUpTo(0) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}

data class BottomNavItem(
    val route: Route,
    val icon: ImageVector,
    val label: String
)

fun NavHostController.navigate(route: Route) {
    val routeString = Json.encodeToString(route)
    this.navigate(routeString)
}
