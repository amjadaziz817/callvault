package io.github.amjadaziz817.callvault

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/**
 * The accessibility service.
 *
 * Its real job is NOT to read the screen. On Android 10 and later, a normal
 * app gets silence when it records a call. An active accessibility service is
 * one of the few components that can still start a microphone recording during
 * a call. So CallVault runs as an accessibility service and uses it only to
 * keep the recorder alive.
 *
 * The service listens for the phone call state. When a call becomes active
 * (OFFHOOK), it starts [RecorderService]. When the call ends (IDLE), it stops
 * the recorder.
 */
class CallRecordingService : AccessibilityService() {

    private lateinit var telephonyManager: TelephonyManager
    private var legacyListener: PhoneStateListener? = null
    private var callback: TelephonyCallback? = null

    private var recording = false
    private var lastNumber: String = ""

    override fun onServiceConnected() {
        super.onServiceConnected()
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        registerCallStateListener()
        Log.i(TAG, "CallVault accessibility service connected")
    }

    private fun registerCallStateListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val cb = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                override fun onCallStateChanged(state: Int) = handleState(state, "")
            }
            callback = cb
            telephonyManager.registerTelephonyCallback(mainExecutor, cb)
        } else {
            val listener = object : PhoneStateListener() {
                @Deprecated("Deprecated in Java")
                override fun onCallStateChanged(state: Int, phoneNumber: String?) =
                    handleState(state, phoneNumber.orEmpty())
            }
            legacyListener = listener
            @Suppress("DEPRECATION")
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    private fun handleState(state: Int, incomingNumber: String) {
        if (incomingNumber.isNotBlank()) lastNumber = incomingNumber
        when (state) {
            TelephonyManager.CALL_STATE_OFFHOOK -> onCallActive()
            TelephonyManager.CALL_STATE_IDLE -> onCallEnded()
            TelephonyManager.CALL_STATE_RINGING -> { /* wait for OFFHOOK */ }
        }
    }

    private fun onCallActive() {
        if (recording) return
        if (!Prefs(this).recordingEnabled) return
        recording = true
        RecorderService.start(this, lastNumber)
    }

    private fun onCallEnded() {
        if (!recording) return
        recording = false
        RecorderService.stop(this)
        lastNumber = ""
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used. The service exists only to hold the audio capability.
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            callback?.let { telephonyManager.unregisterTelephonyCallback(it) }
        } else {
            @Suppress("DEPRECATION")
            legacyListener?.let { telephonyManager.listen(it, PhoneStateListener.LISTEN_NONE) }
        }
        super.onDestroy()
    }

    companion object {
        private const val TAG = "CallVaultAccessibility"
    }
}
