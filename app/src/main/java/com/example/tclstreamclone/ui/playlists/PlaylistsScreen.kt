package com.example.tclstreamclone.ui.playlists

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tclstreamclone.data.Channel
import com.example.tclstreamclone.data.EpgRepository
import com.example.tclstreamclone.data.FavoritesRepository
import com.example.tclstreamclone.data.Playlist
import kotlinx.coroutines.launch

@Composable
fun PlaylistsScreen(navController: NavController) {
    val context = LocalContext.current
    val vm = remember { PlaylistsViewModel(context) }
    val favRepo = remember { FavoritesRepository(context) }
    val playlists by vm.playlists.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val favs by favRepo.favorites.collectAsState(initial = emptySet())

    var showAddDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<String?>(null) }
    var showFavoritesOnly by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) vm.addFromFile(context, uri, nameInput)
    }

    val allChannels = playlists.flatMap { it.channels }
    val groups = remember(allChannels) { allChannels.mapNotNull { it.group }.distinct().sorted() }
    val filtered = remember(allChannels, searchQuery, selectedGroup, showFavoritesOnly, favs) {
        allChannels.filter { ch ->
            (searchQuery.isBlank() || ch.name.contains(searchQuery, true)) &&
                (selectedGroup == null || ch.group == selectedGroup) &&
                (!showFavoritesOnly || favs.contains(ch.url))
        }
    }
    // Agrupa por categoria para exibição com header
    val grouped = remember(filtered) { filtered.groupBy { it.group ?: "Sem categoria" } }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar lista")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Playlists & Canais", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            if (isLoading) CircularProgressIndicator()
            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { vm.clearError() }) { Text("Fechar") }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar canal") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = selectedGroup == null && !showFavoritesOnly, onClick = { selectedGroup = null; showFavoritesOnly = false }, label = { Text("Todos") })
                FilterChip(selected = showFavoritesOnly, onClick = { showFavoritesOnly = !showFavoritesOnly; selectedGroup = null }, label = { Text("Favoritos") })
                groups.take(5).forEach { g ->
                    FilterChip(selected = selectedGroup == g, onClick = { selectedGroup = if (selectedGroup == g) null else g }, label = { Text(g) })
                }
            }
            Spacer(Modifier.height(8.dp))

            Text("Listas (${playlists.size})", style = MaterialTheme.typography.titleMedium)
            LazyColumn(Modifier.weight(0.35f)) {
                items(playlists, key = { it.id }) { pl ->
                    PlaylistRow(pl, onDelete = { vm.remove(pl.id) })
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Canais (${filtered.size})", style = MaterialTheme.typography.titleMedium)
            LazyColumn(Modifier.weight(0.65f)) {
                grouped.forEach { (group, channels) ->
                    item {
                        Text(group, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    }
                    items(channels, key = { it.url }) { ch ->
                        val isFav = favs.contains(ch.url)
                        ChannelRow(
                            ch = ch,
                            isFav = isFav,
                            nowProgramme = EpgRepository.currentFor(ch.name),
                            onToggleFav = { scope.launch { favRepo.toggle(ch.url) } },
                            onClick = { navController.navigate("player/${Uri.encode(ch.url)}?name=${Uri.encode(ch.name)}") }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Adicionar lista") },
            text = {
                Column {
                    Row {
                        TextButton(onClick = { tab = 0 }) { Text(if (tab == 0) "[URL]" else "URL") }
                        TextButton(onClick = { tab = 1 }) { Text(if (tab == 1) "[Arquivo]" else "Arquivo") }
                    }
                    OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
                    if (tab == 0) {
                        OutlinedTextField(value = urlInput, onValueChange = { urlInput = it }, label = { Text("URL (http/https)") }, modifier = Modifier.fillMaxWidth())
                    } else {
                        Text("Selecione um arquivo .m3u/.m3u8 local")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tab == 0) vm.addFromUrl(nameInput, urlInput)
                    else filePicker.launch(arrayOf("*/*"))
                    showAddDialog = false
                    nameInput = ""; urlInput = ""
                }) { Text("Adicionar") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun PlaylistRow(pl: Playlist, onDelete: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(pl.name, style = MaterialTheme.typography.titleMedium)
            Text("${pl.channels.size} canais • ${pl.source}", style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Remover") }
    }
}

@Composable
private fun ChannelRow(
    ch: Channel,
    isFav: Boolean,
    nowProgramme: com.example.tclstreamclone.data.EpgProgramme?,
    onToggleFav: () -> Unit,
    onClick: () -> Unit
) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.PlayArrow, contentDescription = null)
        Column(Modifier.weight(1f).padding(start = 8.dp)) {
            Text(ch.name, style = MaterialTheme.typography.bodyMedium)
            if (ch.group != null) Text(ch.group, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (nowProgramme != null) Text("Agora: ${nowProgramme.title}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
        IconButton(onClick = onToggleFav) {
            Icon(if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "Favorito", tint = if (isFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
        }
    }
}
