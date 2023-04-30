package com.eece430L.inflaterates.utilities

import java.text.SimpleDateFormat
import java.util.*

object DateTimeFormatUtils {

    private val ISODateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val localDateTimeFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

    fun iSODateTimeStringToLocalDateTimeString(isoTimeString: String): String {
        val isoDateTime = ISODateTimeFormat.parse(isoTimeString)
        val localDateTime =  localDateTimeFormat.format(isoDateTime)

        return localDateTime.replace(" ", " at ")
    }
}