package com.tcl.moviearkclone

data class M3uEntry(
    val title: String,
    val url: String,
    val group: String? = null,
    val logo: String? = null
)

object M3uParser {
    fun parse(text: String): List<M3uEntry> {
        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val result = mutableListOf<M3uEntry>()
        var pendingTitle = "Canal"
        var pendingGroup: String? = null
        var pendingLogo: String? = null
        for (line in lines) {
            if (line.startsWith("#EXTINF", ignoreCase = true)) {
                // parse attributes
                val attrRegex = Regex("""([A-Za-z-]+)="([^"]*)"""")
                attrRegex.findAll(line).forEach { m ->
                    when (m.groupValues[1].lowercase()) {
                        "group-title" -> pendingGroup = m.groupValues[2]
                        "tvg-logo" -> pendingLogo = m.groupValues[2]
                    }
                }
                // title after last comma
                val commaIdx = line.lastIndexOf(',')
                pendingTitle = if (commaIdx >= 0) line.substring(commaIdx + 1).trim() else "Canal"
                if (pendingTitle.isEmpty()) pendingTitle = "Canal"
            } else if (line.startsWith("#")) {
                // skip other directives
            } else {
                // url line
                result.add(M3uEntry(pendingTitle, line, pendingGroup, pendingLogo))
                pendingTitle = "Canal"
                pendingGroup = null
                pendingLogo = null
            }
        }
        return result
    }
}
