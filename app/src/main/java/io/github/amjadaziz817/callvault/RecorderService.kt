package io.github.amjadaziz817.callvault

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

/**
 * Foreground service that owns the microphone during a call.
 *
 * The accessibility service starts this service when a call begins and stops
 * it when the call ends. A foreground microphone service is required so the
 * system keeps the recorder alive during the call.
 */
class RecorderService : Service() {

    private var recorder: Recorder? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startRecording(intent.getStringExtra(EXTRA_NUMBER).orEmpty())
            ACTION_STOP -> stopRecording()
            else -> stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun startRecording(number: String) {
        startForegroundCompat()
        val prefs = Prefs(this)
        val rec = Recorder(this)
        val ok = rec.start(number, forceSpeaker = prefs.forceSpeaker)
        if (ok) {
            recorder = rec
            Log.i(TAG, "RecorderService recording $number")
        } else {
            Log.e(TAG, "RecorderService failed to start")
            stopSelf()
        }
    }

    private fun stopRecording() {
        recorder?.stop()
        recorder = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        recorder?.stop()
        recorder = null
        super.onDestroy()
    }

    private fun startForegroundCompat() {
        createChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.recording_notification_title))
            .setContentText(getString(R.string.recording_notification_text))
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIF_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(NOTIF_ID, notification)
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.recording_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TAG = "CallVaultService"
        private const val CHANNEL_ID = "callvault_recording"
        private const val NOTIF_ID = 1001
        const val ACTION_START = "io.github.amjadaziz817.callvault.START"
        const val ACTION_STOP = "io.github.amjadaziz817.callvault.STOP"
        const val EXTRA_NUMBER = "number"

        fun start(context: Context, number: String) {
            val intent = Intent(context, RecorderService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_NUMBER, number)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, RecorderService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
