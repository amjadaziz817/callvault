package io.github.amjadaziz817.callvault

import android.content.Context
import io.github.amjadaziz817.callvault.model.Recording
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manages where recordings live on disk and reads the list back.
 *
 * Files are stored in the app's INTERNAL files directory. Call recordings are
 * sensitive, so the app keeps them in internal storage, which other apps
 * cannot read. The app plays and shares a recording through a FileProvider.
 * The file name encodes the phone number and the start time.
 *
 * File name format: call_<number>_<yyyyMMdd-HHmmss>.m4a
 */
object RecordingStore {

    private const val PREFIX = "call_"
    private const val EXT = ".m4a"
    private val NAME_FORMAT = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)

    fun recordingsDir(context: Context): File {
        val dir = File(context.filesDir, "recordings")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun newFile(context: Context, number: String, startedAt: Long): File {
        val safeNumber = number.ifBlank { "unknown" }.replace(Regex("[^0-9A-Za-z+]"), "")
        val stamp = NAME_FORMAT.format(Date(startedAt))
        return File(recordingsDir(context), "$PREFIX${safeNumber}_$stamp$EXT")
    }

    fun list(context: Context): List<Recording> {
        val dir = recordingsDir(context)
        val files = dir.listFiles { f -> f.isFile && f.name.endsWith(EXT) } ?: return emptyList()
        return files
            .mapNotNull { parse(it) }
            .sortedByDescending { it.startedAt }
    }

    private fun parse(file: File): Recording? {
        val base = file.name.removePrefix(PREFIX).removeSuffix(EXT)
        val sep = base.lastIndexOf('_')
        if (sep <= 0) return null
        val number = base.substring(0, sep)
        val stampText = base.substring(sep + 1)
        val startedAt = runCatching { NAME_FORMAT.parse(stampText)?.time }.getOrNull()
            ?: file.lastModified()
        return Recording(file = file, number = number, startedAt = startedAt, durationMs = 0L)
    }
}
