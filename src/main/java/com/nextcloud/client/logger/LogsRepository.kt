package com.nextcloud.client.logger

/**
 * This interface provides safe, read only access to application
 * logs stored on a device.
 */
interface LogsRepository {

    @FunctionalInterface
    interface Listener {
        fun onLoaded(entries: List<LogEntry>)
    }

    /**
     * If true, logger was unable to handle some messages, which means
     * it cannot cope with amount of logged data.
     *
     * This property is thread-safe.
     */
    val lostEntries: Boolean

    /**
     * Asynchronously load available logs. Load can be scheduled on any thread,
     * but the listener will be called on main thread.
     */
    fun load(listener: Listener)

    /**
     * Asynchronously delete logs.
     */
    fun deleteAll()
}
