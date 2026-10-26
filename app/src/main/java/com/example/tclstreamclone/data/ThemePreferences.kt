package com.example.tclstreamclone.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeStore by preferencesDataStore(name = "theme")

class ThemePreferences(private val context: Context) {
    private val KEY = stringPreferencesKey("mode") // system | light | dark

    val mode: Flow<String> = context.themeStore.data.map { it[KEY] ?: "system" }

    suspend fun setMode(mode: String) {
        context.themeStore.edit { it[KEY] = mode }
    }
}
