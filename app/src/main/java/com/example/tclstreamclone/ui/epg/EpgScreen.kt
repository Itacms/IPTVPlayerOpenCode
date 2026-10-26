package com.example.tclstreamclone.ui.epg

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tclstreamclone.data.EpgProgramme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EpgScreen() {
    val vm = remember { EpgViewModel() }
    val programmes by vm.programmes.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    var url by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }

    val filtered = remember(programmes, search) {
        if (search.isBlank()) programmes else programmes.filter { it.title.contains(search, true) || it.channelId.contains(search, true) }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("EPG (XMLTV)", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL XMLTV") }, modifier = Modifier.weight(1f))
            Button(onClick = { if (url.isNotBlank()) vm.load(url) }) { Text("Carregar") }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = search, onValueChange = { search = it }, label = { Text("Filtrar") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        if (isLoading) CircularProgressIndicator()
        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { vm.clearError() }) { Text("Fechar") }
        }
        LazyColumn(Modifier.fillMaxSize()) {
            items(filtered.take(200)) { p ->
                EpgRow(p)
            }
        }
    }
}

@Composable
private fun EpgRow(p: EpgProgramme) {
    val sdf = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("${p.channelId} • ${sdf.format(Date(p.start))} - ${sdf.format(Date(p.stop))}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(p.title, style = MaterialTheme.typography.bodyMedium)
        if (p.desc != null) Text(p.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
