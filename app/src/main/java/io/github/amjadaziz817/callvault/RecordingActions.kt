package io.github.amjadaziz817.callvault

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import io.github.amjadaziz817.callvault.model.Recording

/** Play, share, and delete actions for a recording. */
object RecordingActions {

    fun play(context: Context, rec: Recording) {
        val uri = uriFor(context, rec)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "audio/mp4")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.play)))
    }

    fun share(context: Context, rec: Recording) {
        val uri = uriFor(context, rec)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/mp4"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)))
    }

    fun delete(rec: Recording) {
        rec.file.delete()
    }

    private fun uriFor(context: Context, rec: Recording) =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", rec.file)
}
