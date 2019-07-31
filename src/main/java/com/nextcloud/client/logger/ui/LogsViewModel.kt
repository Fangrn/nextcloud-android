package com.nextcloud.client.logger.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nextcloud.client.core.AsyncRunner
import com.nextcloud.client.logger.LogEntry
import com.nextcloud.client.logger.LogsRepository
import javax.inject.Inject

class LogsViewModel @Inject constructor(
    context: Context,
    asyncRunner: AsyncRunner,
    private val logsRepository: LogsRepository
) : ViewModel() {

    private val sender = LogsEmailSender(context, asyncRunner)
    val entries: LiveData<List<LogEntry>> = MutableLiveData()
    private val listener = object : LogsRepository.Listener {
        override fun onLoaded(entries: List<LogEntry>) {
            this@LogsViewModel.entries as MutableLiveData
            this@LogsViewModel.entries.value = entries
        }
    }

    fun send() {
        entries.value?.let {
            sender.send(it)
        }
    }

    fun load() {
        logsRepository.load(listener)
    }

    fun deleteAll() {
        logsRepository.deleteAll()
        (entries as MutableLiveData).value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        sender.stop()
    }
}
