package com.nextcloud.client.logger

interface Logger {

    fun v(tag: String, message: String)
    fun d(tag: String, message: String)
    fun d(tag: String, message: String, t: Throwable)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String)
    fun e(tag: String, message: String)
    fun e(tag: String, message: String, t: Throwable)
}
