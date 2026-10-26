package com.example.tclstreamclone.data

data class Channel(
    val name: String,
    val url: String,
    val logo: String? = null,
    val group: String? = null
)

data class Playlist(
    val id: String,
    val name: String,
    val source: String, // URL ou URI local
    val channels: List<Channel> = emptyList()
)
