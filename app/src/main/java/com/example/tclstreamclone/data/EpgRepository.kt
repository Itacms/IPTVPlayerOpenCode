package com.example.tclstreamclone.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object EpgRepository {
    private val _programmes = MutableStateFlow<List<EpgProgramme>>(emptyList())
    val programmes: StateFlow<List<EpgProgramme>> = _programmes.asStateFlow()

    fun set(list: List<EpgProgramme>) { _programmes.value = list }

    fun currentFor(channelName: String, now: Long = System.currentTimeMillis()): EpgProgramme? =
        _programmes.value.firstOrNull { p ->
            p.channelId.contains(channelName, true) || channelName.contains(p.channelId, true)
        }?.let { p ->
            if (p.start <= now && (p.stop == 0L || p.stop >= now)) p else null
        }
}
