package com.example.tclstreamclone.data

object M3uParser {
    fun parse(text: String): List<Channel> {
        val lines = text.lines()
        val channels = mutableListOf<Channel>()
        var currentName: String? = null
        var currentLogo: String? = null
        var currentGroup: String? = null
        for (raw in lines) {
            val line = raw.trim()
            if (line.isEmpty()) continue
            if (line.startsWith("#EXTINF")) {
                // #EXTINF:-1 tvg-name="..." tvg-logo="..." group-title="...",Title
                currentName = line.substringAfterLast(",").trim().ifEmpty { "Canal" }
                currentLogo = Regex("""tvg-logo="([^"]*)"""").find(line)?.groupValues?.get(1)
                currentGroup = Regex("""group-title="([^"]*)"""").find(line)?.groupValues?.get(1)
            } else if (!line.startsWith("#")) {
                if (line.startsWith("http") || line.startsWith("rtmp") || line.startsWith("rtsp") || line.contains("://")) {
                    channels.add(
                        Channel(
                            name = currentName ?: "Canal",
                            url = line,
                            logo = currentLogo,
                            group = currentGroup
                        )
                    )
                    currentName = null
                    currentLogo = null
                    currentGroup = null
                }
            }
        }
        return channels
    }
}
