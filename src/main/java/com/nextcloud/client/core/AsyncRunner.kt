package com.nextcloud.client.core

typealias TaskBody<T> = () -> T
typealias OnResultCallback<T> = (T) -> Unit
typealias OnErrorCallback = (Throwable) -> Unit

/**
 * This interface allows to post background tasks that report results via callbacks invoked on main thread.
 *
 * It is privided as an alternative for heavy, platform specific and virtually untestable [android.os.AsyncTask]
 */
interface AsyncRunner {
    fun <T> post(block: () -> T, onResult: OnResultCallback<T>? = null, onError: OnErrorCallback? = null): Cancellable
}
