package com.example.tclstreamclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tclstreamclone.data.ThemePreferences
import com.example.tclstreamclone.ui.epg.EpgScreen
import com.example.tclstreamclone.ui.playlists.PlaylistsScreen
import com.example.tclstreamclone.ui.screens.HomeScreen
import com.example.tclstreamclone.ui.screens.PlayerScreen
import com.example.tclstreamclone.ui.theme.TclStreamCloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themePrefs = remember { ThemePreferences(this) }
            val mode by themePrefs.mode.collectAsState(initial = "system")
            val darkTheme = when (mode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }
            TclStreamCloneTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppScaffold(themePrefs)
                }
            }
        }
    }
}

@Composable
fun AppScaffold(themePrefs: ThemePreferences) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = currentRoute != null && !currentRoute.startsWith("player")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = backStack?.destination?.hierarchy?.any { it.route == "home" } == true,
                        onClick = { navController.navigateTab("home") },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Início") }
                    )
                    NavigationBarItem(
                        selected = backStack?.destination?.hierarchy?.any { it.route == "playlists" } == true,
                        onClick = { navController.navigateTab("playlists") },
                        icon = { Icon(Icons.Default.List, contentDescription = null) },
                        label = { Text("Playlists") }
                    )
                    NavigationBarItem(
                        selected = backStack?.destination?.hierarchy?.any { it.route == "epg" } == true,
                        onClick = { navController.navigateTab("epg") },
                        icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                        label = { Text("EPG") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen(navController, themePrefs) }
            composable("playlists") { PlaylistsScreen(navController) }
            composable("epg") { EpgScreen() }
            composable(
                route = "player/{url}?name={name}",
                arguments = listOf(
                    navArgument("url") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStack ->
                val url = backStack.arguments?.getString("url").orEmpty()
                val name = backStack.arguments?.getString("name").orEmpty()
                PlayerScreen(url = url, channelName = name, onBack = { navController.popBackStack() })
            }
        }
    }
}

private fun androidx.navigation.NavController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
