package com.eece430L.inflaterates.utilities

object HttpStatusCodesUtil {

    fun httpStatusCodeToMessage(statusCode: Int): String {
        return when (statusCode) {
            401, 403 -> "Wrong Credentials!"
            500 -> "Internal Server Error!"
            503 -> "Service Unavailable!"
            else -> ""
        }
    }
}