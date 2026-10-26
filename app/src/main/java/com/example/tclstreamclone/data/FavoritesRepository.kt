package com.example.tclstreamclone.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.favStore by preferencesDataStore(name = "favorites")

class FavoritesRepository(private val context: Context) {
    private val KEY = stringSetPreferencesKey("fav_urls")

    val favorites: Flow<Set<String>> = context.favStore.data.map { it[KEY] ?: emptySet() }

    suspend fun toggle(url: String) {
        context.favStore.edit { prefs ->
            val cur = (prefs[KEY] ?: emptySet()).toMutableSet()
            if (cur.contains(url)) cur.remove(url) else cur.add(url)
            prefs[KEY] = cur
        }
    }
}
