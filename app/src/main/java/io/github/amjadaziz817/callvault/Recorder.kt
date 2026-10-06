package io.github.amjadaziz817.callvault

import android.content.Context
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File

/**
 * Wraps MediaRecorder for one call.
 *
 * Without root, Android does not give a clean copy of the other person's
 * voice. So the recorder uses the microphone and, if the user allows it,
 * turns on the speaker. The speaker plays the other person out loud, and
 * the microphone then picks that voice up. The result depends on the phone.
 *
 * The audio source order (VOICE_RECOGNITION first) gives the least system
 * processing, so it captures the most audio on most phones. If that source
 * fails, the recorder falls back to the plain microphone.
 */
class Recorder(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var previousSpeakerState: Boolean? = null
    var outputFile: File? = null
        private set
    var startedAt: Long = 0L
        private set

    private val audioManager: AudioManager
        get() = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val sources = intArrayOf(
        MediaRecorder.AudioSource.VOICE_RECOGNITION,
        MediaRecorder.AudioSource.MIC
    )

    fun start(number: String, forceSpeaker: Boolean): Boolean {
        startedAt = System.currentTimeMillis()
        val file = RecordingStore.newFile(context, number, startedAt)
        outputFile = file

        if (forceSpeaker) enableSpeaker()

        for (source in sources) {
            if (tryStart(source, file)) {
                // Do not log the file name. It contains the phone number.
                Log.i(TAG, "Recording started with source $source")
                return true
            }
        }
        Log.e(TAG, "All audio sources failed")
        outputFile = null
        return false
    }

    private fun tryStart(source: Int, file: File): Boolean {
        val mr = buildRecorder()
        return try {
            mr.setAudioSource(source)
            mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mr.setAudioEncodingBitRate(96_000)
            mr.setAudioSamplingRate(44_100)
            mr.setOutputFile(file.absolutePath)
            mr.prepare()
            mr.start()
            recorder = mr
            true
        } catch (t: Throwable) {
            Log.w(TAG, "Source $source failed: ${t.message}")
            runCatching { mr.release() }
            if (file.exists() && file.length() == 0L) file.delete()
            false
        }
    }

    fun stop(): File? {
        val file = outputFile
        try {
            recorder?.stop()
        } catch (t: Throwable) {
            Log.w(TAG, "stop() failed: ${t.message}")
        } finally {
            runCatching { recorder?.release() }
            recorder = null
            restoreSpeaker()
        }
        if (file != null && file.exists() && file.length() == 0L) {
            file.delete()
            return null
        }
        return file
    }

    private fun buildRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context)
        else @Suppress("DEPRECATION") MediaRecorder()

    private fun enableSpeaker() {
        previousSpeakerState = audioManager.isSpeakerphoneOn
        runCatching { audioManager.isSpeakerphoneOn = true }
    }

    private fun restoreSpeaker() {
        previousSpeakerState?.let { prev ->
            runCatching { audioManager.isSpeakerphoneOn = prev }
        }
        previousSpeakerState = null
    }

    companion object {
        private const val TAG = "CallVaultRecorder"
    }
}
