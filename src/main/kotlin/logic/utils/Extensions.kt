package org.example.logic.utils

import org.example.logic.utils.Constants.MAX_ID_LENGTH
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Uuid.getCroppedId(limit: Int = MAX_ID_LENGTH): String = this.toHexString().substring(0, limit)
