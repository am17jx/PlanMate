package org.example.logic.utils

import kotlinx.datetime.Instant
import org.example.logic.utils.Constants.MAX_ID_LENGTH
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Uuid.getCroppedId(limit: Int = MAX_ID_LENGTH): String = this.toHexString().substring(0, limit)

fun String.isValidId() = matches(Regex("^[a-zA-Z0-9]+$")) && length == MAX_ID_LENGTH

fun Long.toInstant() = Instant.fromEpochMilliseconds(this)
