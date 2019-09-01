package org.sugarandrose.app.util

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class ZonedDateTimeGsonTypeAdapter : TypeAdapter<ZonedDateTime>() {
    private val DATE_TIME_FORMATTER_INTERNAL: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: ZonedDateTime) {
        out.value(DATE_TIME_FORMATTER_INTERNAL.format(value))
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): ZonedDateTime {
        return ZonedDateTime.of(LocalDateTime.parse(`in`.nextString(), DATE_TIME_FORMATTER_INTERNAL), ZoneId.of("GMT"))
    }
}
