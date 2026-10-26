package com.example.tclstreamclone.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "playlists")

class PlaylistRepository(private val context: Context) {
    private val KEY = stringPreferencesKey("playlists_json")

    val playlists: Flow<List<Playlist>> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY] ?: "[]"
        parseList(json)
    }

    suspend fun add(playlist: Playlist) {
        context.dataStore.edit { prefs ->
            val current = parseList(prefs[KEY] ?: "[]").toMutableList()
            current.add(playlist)
            prefs[KEY] = serialize(current)
        }
    }

    suspend fun remove(id: String) {
        context.dataStore.edit { prefs ->
            val current = parseList(prefs[KEY] ?: "[]").filterNot { it.id == id }
            prefs[KEY] = serialize(current)
        }
    }

    private fun serialize(list: List<Playlist>): String {
        val arr = JSONArray()
        list.forEach { pl ->
            val o = JSONObject()
            o.put("id", pl.id)
            o.put("name", pl.name)
            o.put("source", pl.source)
            val ch = JSONArray()
            pl.channels.forEach { c ->
                val co = JSONObject()
                co.put("name", c.name)
                co.put("url", c.url)
                co.put("logo", c.logo)
                co.put("group", c.group)
                ch.put(co)
            }
            o.put("channels", ch)
            arr.put(o)
        }
        return arr.toString()
    }

    private fun parseList(json: String): List<Playlist> = try {
        val arr = JSONArray(json)
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            val chArr = o.optJSONArray("channels") ?: JSONArray()
            val channels = (0 until chArr.length()).map { j ->
                val co = chArr.getJSONObject(j)
                Channel(
                    name = co.optString("name"),
                    url = co.optString("url"),
                    logo = co.optString("logo").ifEmpty { null },
                    group = co.optString("group").ifEmpty { null }
                )
            }
            Playlist(
                id = o.optString("id"),
                name = o.optString("name"),
                source = o.optString("source"),
                channels = channels
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}
