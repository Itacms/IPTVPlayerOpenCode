package com.example.tclstreamclone.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tclstreamclone.data.ThemePreferences
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, themePrefs: ThemePreferences) {
    val mode by themePrefs.mode.collectAsState(initial = "system")
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.height(64.dp))
        Spacer(Modifier.height(12.dp))
        Text("TCL Stream Clone", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Clone inspirado no TCL Stream, com M3U/M3U8, EPG e favoritos.")
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { navController.navigate("playlists") }) {
                Icon(Icons.Default.List, contentDescription = null)
                Text("  Playlists")
            }
            Button(onClick = { navController.navigate("epg") }) {
                Text("EPG")
            }
        }
        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(if (mode == "dark") Icons.Default.Brightness4 else Icons.Default.Brightness7, contentDescription = null)
            Text("Tema: $mode")
            Switch(checked = mode == "dark", onCheckedChange = { on ->
                scope.launch { themePrefs.setMode(if (on) "dark" else "light") }
            })
        }
    }
}
