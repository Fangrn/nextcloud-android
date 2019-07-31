package com.nextcloud.client.logger

enum class Level(val tag: String) {
    UNKNOWN("U"),
    VERBOSE("V"),
    DEBUG("D"),
    INFO("I"),
    WARNING("W"),
    ERROR("E"),
    ASSERT("A");

    companion object {
        @JvmStatic
        fun fromTag(tag: String): Level? = when (tag) {
            "U" -> UNKNOWN
            "V" -> VERBOSE
            "D" -> DEBUG
            "I" -> INFO
            "W" -> WARNING
            "E" -> ERROR
            "A" -> ASSERT
            else -> null
        }
    }
}
