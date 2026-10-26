package com.example.tclstreamclone.ui.epg

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tclstreamclone.data.EpgParser
import com.example.tclstreamclone.data.EpgProgramme
import com.example.tclstreamclone.data.EpgRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class EpgViewModel : ViewModel() {
    private val client = OkHttpClient()
    private val _programmes = MutableStateFlow<List<EpgProgramme>>(emptyList())
    val programmes: StateFlow<List<EpgProgramme>> = _programmes.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun load(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _error.value = null
            try {
                val req = Request.Builder().url(url).build()
                val text = client.newCall(req).execute().use { it.body?.string() ?: "" }
                val parsed = EpgParser.parse(text)
                _programmes.value = parsed
                EpgRepository.set(parsed)
                if (parsed.isEmpty()) _error.value = "Nenhum programa encontrado"
            } catch (e: Exception) {
                _error.value = e.message ?: "Falha ao baixar EPG"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }
}
