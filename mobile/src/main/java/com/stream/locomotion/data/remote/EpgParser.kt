package com.stream.locomotion.data.remote

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import com.stream.locomotion.domain.model.Program
import com.stream.locomotion.domain.model.Schedule
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class EpgParser {

    fun parse(xml: String, preferredChannelId: String? = null): Schedule {
        val channels = mutableMapOf<String, Channel>()
        val programs = mutableListOf<ProgramRaw>()

        val parser = Xml.newPullParser()
        parser.setInput(StringReader(xml))

        var eventType = parser.eventType
        var currentChannelId: String? = null
        var currentChannelName: String? = null
        var currentChannelIcon: String? = null
        var currentChannelUrl: String? = null

        var currentProgram: ProgramRaw? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val name = parser.name ?: ""
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (name) {
                        "channel" -> {
                            currentChannelId = parser.getAttributeValue(null, "id")
                            currentChannelName = null
                            currentChannelIcon = null
                            currentChannelUrl = null
                        }
                        "display-name" -> {
                            if (currentChannelId != null) {
                                currentChannelName = readText(parser)
                            }
                        }
                        "icon" -> {
                            val src = parser.getAttributeValue(null, "src")
                            if (currentProgram != null) {
                                currentProgram = currentProgram?.copy(iconUrl = src)
                            } else if (currentChannelId != null) {
                                currentChannelIcon = src
                            }
                        }
                        "url" -> {
                            if (currentChannelId != null) {
                                currentChannelUrl = readText(parser)
                            }
                        }
                        "programme" -> {
                            val start = parser.getAttributeValue(null, "start")
                            val stop = parser.getAttributeValue(null, "stop")
                            val channel = parser.getAttributeValue(null, "channel")
                            currentProgram = ProgramRaw(
                                channelId = channel ?: "",
                                start = start,
                                stop = stop
                            )
                        }
                        "title" -> {
                            currentProgram = currentProgram?.copy(title = readText(parser))
                        }
                        "desc" -> {
                            currentProgram = currentProgram?.copy(description = readText(parser))
                        }
                        "date" -> {
                            val year = readText(parser)
                            currentProgram = currentProgram?.copy(year = year)
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    when (name) {
                        "channel" -> {
                            if (currentChannelId != null) {
                                channels[currentChannelId!!] = Channel(
                                    id = currentChannelId!!,
                                    name = currentChannelName ?: currentChannelId!!,
                                    iconUrl = currentChannelIcon,
                                    url = currentChannelUrl
                                )
                            }
                            currentChannelId = null
                        }
                        "programme" -> {
                            currentProgram?.let { programs.add(it) }
                            currentProgram = null
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        val channelId = preferredChannelId
            ?: channels.keys.firstOrNull()
            ?: ""

        val sdf = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US).apply {
            isLenient = false
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val outDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val mappedPrograms = programs
            .filter { it.channelId == channelId }
            .map { raw ->
                val start = raw.start?.let { parseDateMillis(sdf, it) } ?: 0L
                val end = raw.stop?.let { parseDateMillis(sdf, it) } ?: 0L
                Program(
                    id = "${raw.channelId}-${raw.start ?: ""}-${raw.title}",
                    channelId = raw.channelId,
                    title = raw.title ?: "",
                    description = raw.description ?: "",
                    startTime = start,
                    endTime = end,
                    iconUrl = raw.iconUrl,
                    year = raw.year?.toIntOrNull()
                )
            }

        val scheduleDate = mappedPrograms.firstOrNull()?.startTime?.let {
            outDateFormat.format(java.util.Date(it))
        } ?: ""

        return Schedule(
            channelId = channelId,
            date = scheduleDate,
            programs = mappedPrograms
        )
    }

    private fun parseDateMillis(format: SimpleDateFormat, value: String): Long {
        return try {
            format.parse(value)?.time ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    private data class Channel(
        val id: String,
        val name: String,
        val iconUrl: String?,
        val url: String?
    )

    private data class ProgramRaw(
        val channelId: String,
        val start: String?,
        val stop: String?,
        val title: String? = null,
        val description: String? = null,
        val year: String? = null,
        val iconUrl: String? = null
    )
}
