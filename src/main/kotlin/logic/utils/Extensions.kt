package org.example.logic.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.example.logic.utils.Constants.MAX_ID_LENGTH
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Uuid.getCroppedId(limit: Int = MAX_ID_LENGTH): String = this.toHexString().substring(0, limit)

fun String.isValidId() = matches(Regex("^[a-zA-Z0-9]+$")) && length == MAX_ID_LENGTH

fun Long.toInstant() = Instant.fromEpochMilliseconds(this)

fun Instant.formattedString(): String{
    val customFormat = DateTimeComponents.Format {
        year(); char('/'); monthNumber(padding = Padding.ZERO); char('/'); dayOfMonth(padding = Padding.ZERO)
        char(' ')
        hour(Padding.ZERO); char(':'); minute(padding = Padding.ZERO); char(' ')
        amPm(this@formattedString.toLocalDateTime(TimeZone.currentSystemDefault()).hour)
    }
    return this.format(customFormat)
}

fun DateTimeFormatBuilder.WithDateTimeComponents.amPm(hour: Int) {
    chars(if (hour < 12) "AM" else "PM")
}
