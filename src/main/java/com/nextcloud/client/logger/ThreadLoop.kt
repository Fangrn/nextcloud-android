package com.nextcloud.client.logger

/**
 * This utility runs provided loop body continuously in a loop on a background thread
 * and allows start and stop the loop thread in a safe way.
 */
internal class ThreadLoop {

    private val lock = Object()
    private var thread: Thread? = null
    private var loopBody: (() -> Unit)? = null

    /**
     * Start running [loopBody] in a loop on a background [Thread].
     * If loop is already started, it no-ops.
     *
     * This method is thread safe.
     *
     * @throws IllegalStateException if loop is already running
     */
    fun start(loopBody: () -> Unit) {
        synchronized(lock) {
            if (thread == null) {
                this.loopBody = loopBody
                this.thread = Thread(this::loop)
                this.thread?.start()
            }
        }
    }

    /**
     * Stops the background [Thread] by interrupting it and waits for [Thread.join].
     * If loop is not started, it no-ops.
     *
     * This method is thread safe.
     *
     * @throws IllegalStateException if thread is not running
     */
    fun stop() {
        synchronized(lock) {
            if (thread != null) {
                thread?.interrupt()
                thread?.join()
            }
        }
    }

    private fun loop() {
        try {
            while (true) {
                loopBody?.invoke()
            }
        } catch (ex: InterruptedException) {
            return
        }
    }
}
