package com.nextcloud.client.logger

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class LogEntry(val timestamp: Date, val level: Level, val tag: String, val message: String) {

    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        private val TIME_ZONE = TimeZone.getTimeZone("UTC")
        private val DATE_GROUP_INDEX = 1
        private val LEVEL_GROUP_INDEX = 2
        private val TAG_GROUP_INDEX = 3
        private val MESSAGE_GROUP_INDEX = 4

        /**
         *  <iso8601 date>;<level tag>;<entry tag>;<message>
         *  1970-01-01T00:00:00.000Z;D;tag;some message
         */
        private val ENTRY_PARSE_REGEXP = Regex(
            pattern = """(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z);([ADEIVW]);([^;]+);(.*)"""
        )

        @JvmStatic
        fun buildDateFormat(): SimpleDateFormat {
            return SimpleDateFormat(DATE_FORMAT, Locale.US).apply {
                timeZone = TIME_ZONE
                isLenient = false
            }
        }

        @Suppress("ReturnCount")
        @JvmStatic
        fun parse(s: String): LogEntry? {
            val result = ENTRY_PARSE_REGEXP.matchEntire(s) ?: return null

            val date = try {
                buildDateFormat().parse(result.groupValues[DATE_GROUP_INDEX])
            } catch (ex: ParseException) {
                return null
            }

            val level: Level = Level.fromTag(result.groupValues[LEVEL_GROUP_INDEX]) ?: Level.UNKNOWN
            val tag = result.groupValues[TAG_GROUP_INDEX]
            val message = result.groupValues[MESSAGE_GROUP_INDEX].replace("\\n", "\n")

            return LogEntry(
                timestamp = date,
                level = level,
                tag = tag,
                message = message
            )
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        format(sb, buildDateFormat())
        return sb.toString()
    }

    fun format(sb: StringBuilder, dateFormat: SimpleDateFormat) {
        sb.append(dateFormat.format(timestamp))
        sb.append(';')
        sb.append(level.tag)
        sb.append(';')
        sb.append(tag.replace(';', ' '))
        sb.append(';')
        sb.append(message.replace("\n", "\\n"))
    }
}
