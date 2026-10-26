package com.example.tclstreamclone.ui.playlists

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tclstreamclone.data.Channel
import com.example.tclstreamclone.data.M3uParser
import com.example.tclstreamclone.data.Playlist
import com.example.tclstreamclone.data.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID

class PlaylistsViewModel(context: Context) : ViewModel() {
    private val repo = PlaylistRepository(context)
    private val client = OkHttpClient()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            repo.playlists.collect { _playlists.value = it }
        }
    }

    fun addFromUrl(name: String, url: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            _isLoading.value = true
            _error.value = null
            try {
                val req = Request.Builder().url(url).build()
                val text = client.newCall(req).execute().use { it.body?.string() ?: "" }
                val channels = M3uParser.parse(text)
                repo.add(Playlist(UUID.randomUUID().toString(), name.ifBlank { url }, url, channels))
            } catch (e: Exception) {
                _error.value = e.message ?: "Falha ao baixar lista"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addFromFile(context: Context, uri: Uri, name: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            _isLoading.value = true
            _error.value = null
            try {
                val text = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
                val channels = M3uParser.parse(text)
                repo.add(Playlist(UUID.randomUUID().toString(), name.ifBlank { uri.lastPathSegment ?: "Lista local" }, uri.toString(), channels))
            } catch (e: Exception) {
                _error.value = e.message ?: "Falha ao ler arquivo"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun remove(id: String) {
        viewModelScope.launch { repo.remove(id) }
    }

    fun clearError() { _error.value = null }
}
