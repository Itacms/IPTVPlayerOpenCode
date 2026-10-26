package com.example.tclstreamclone.data

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class EpgProgramme(
    val channelId: String,
    val title: String,
    val start: Long,
    val stop: Long,
    val desc: String? = null
)

object EpgParser {
    private val fmt = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }

    fun parse(text: String): List<EpgProgramme> {
        val out = mutableListOf<EpgProgramme>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val p = factory.newPullParser()
            p.setInput(StringReader(text))
            var event = p.eventType
            var tag: String? = null
            var channelId = ""
            var start = 0L
            var stop = 0L
            var title = ""
            var desc = ""
            while (event != XmlPullParser.END_DOCUMENT) {
                when (event) {
                    XmlPullParser.START_TAG -> {
                        tag = p.name
                        if (tag == "programme") {
                            channelId = p.getAttributeValue(null, "channel") ?: ""
                            start = parseTime(p.getAttributeValue(null, "start"))
                            stop = parseTime(p.getAttributeValue(null, "stop"))
                            title = ""; desc = ""
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val t = p.text?.trim().orEmpty()
                        if (t.isNotEmpty()) {
                            when (tag) {
                                "title" -> title = t
                                "desc" -> desc = t
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (p.name == "programme") {
                            out.add(EpgProgramme(channelId, title.ifBlank { "Programa" }, start, stop, desc.ifBlank { null }))
                        }
                        tag = null
                    }
                }
                event = p.next()
            }
        } catch (_: Exception) { }
        return out
    }

    private fun parseTime(s: String?): Long {
        if (s.isNullOrBlank()) return 0L
        return try { fmt.parse(s)?.time ?: 0L } catch (_: Exception) { 0L }
    }
}
