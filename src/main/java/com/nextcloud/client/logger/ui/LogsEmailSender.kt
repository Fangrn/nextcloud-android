package com.nextcloud.client.logger.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.nextcloud.client.core.AsyncRunner
import com.nextcloud.client.core.Cancellable
import com.nextcloud.client.logger.LogEntry
import com.owncloud.android.R
import java.io.File
import java.io.FileWriter

class LogsEmailSender(private val context: Context, private val runner: AsyncRunner) {

    private companion object {
        const val LOGS_MIME_TYPE = "text/plain"
    }

    private class Task(
        private val context: Context,
        private val logs: List<LogEntry>,
        private val file: File
    ) : Function0<Uri?> {

        override fun invoke(): Uri? {
            file.parentFile.mkdirs()
            val fo = FileWriter(file, false)
            logs.forEach {
                fo.write(it.toString())
                fo.write("\n")
            }
            fo.close()
            return FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), file)
        }
    }

    private var task: Cancellable? = null

    fun send(logs: List<LogEntry>) {
        if (task == null) {
            val outFile = File(context.cacheDir, "attachments/logs.txt")
            task = runner.post(Task(context, logs, outFile), onResult = { task = null; send(it) })
        }
    }

    fun stop() {
        if (task != null) {
            task?.cancel()
            task = null
        }
    }

    private fun send(uri: Uri?) {
        task = null
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.putExtra(Intent.EXTRA_EMAIL, context.getString(R.string.mail_logger))
        val subject = context.getString(R.string.log_send_mail_subject).format(context.getString(R.string.app_name))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = LOGS_MIME_TYPE
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(uri))
        try {
            context.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, R.string.log_send_no_mail_app, Toast.LENGTH_SHORT).show()
        }
    }
}
