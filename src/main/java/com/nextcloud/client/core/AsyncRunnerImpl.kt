package com.nextcloud.client.core

import android.os.Handler
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.atomic.AtomicBoolean

internal class AsyncRunnerImpl(private val uiThreadHandler: Handler, corePoolSize: Int) : AsyncRunner {

    private class Task<T>(
        private val handler: Handler,
        private val callable: () -> T,
        private val onSuccess: OnResultCallback<T>?,
        private val onError: OnErrorCallback?
    ) : Runnable, Cancellable {

        private val cancelled = AtomicBoolean(false)

        override fun run() {
            @Suppress("TooGenericExceptionCaught") // this is exactly what we want here
            try {
                val result = callable.invoke()
                if (!cancelled.get()) {
                    handler.post {
                        onSuccess?.invoke(result)
                    }
                }
            } catch (t: Throwable) {
                if (!cancelled.get()) {
                    handler.post { onError?.invoke(t) }
                }
            }
        }

        override fun cancel() {
            cancelled.set(true)
        }
    }

    private val executor = ScheduledThreadPoolExecutor(corePoolSize)

    override fun <T> post(block: () -> T, onResult: OnResultCallback<T>?, onError: OnErrorCallback?): Cancellable {
        val task = Task(uiThreadHandler, block, onResult, onError)
        executor.execute(task)
        return task
    }
}
