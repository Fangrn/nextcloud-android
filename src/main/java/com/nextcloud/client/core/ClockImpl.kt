package com.nextcloud.client.core

import java.util.Date

class ClockImpl : Clock {
    override val currentTime: Long
        get() {
            return System.currentTimeMillis()
        }

    override val currentDate: Date
        get() {
            return Date(currentTime)
        }
}
