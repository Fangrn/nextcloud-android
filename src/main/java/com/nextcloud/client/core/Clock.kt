package com.nextcloud.client.core

import java.util.Date

interface Clock {
    val currentTime: Long
    val currentDate: Date
}
