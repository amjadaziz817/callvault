package io.github.amjadaziz817.callvault

import android.content.Context

/** Small wrapper over SharedPreferences for user settings. */
class Prefs(context: Context) {

    private val sp = context.getSharedPreferences("callvault", Context.MODE_PRIVATE)

    /** When true, the recorder turns on the speaker to capture the other person. */
    var forceSpeaker: Boolean
        get() = sp.getBoolean(KEY_FORCE_SPEAKER, true)
        set(value) = sp.edit().putBoolean(KEY_FORCE_SPEAKER, value).apply()

    /** When true, CallVault records calls automatically. */
    var recordingEnabled: Boolean
        get() = sp.getBoolean(KEY_ENABLED, true)
        set(value) = sp.edit().putBoolean(KEY_ENABLED, value).apply()

    companion object {
        private const val KEY_FORCE_SPEAKER = "force_speaker"
        private const val KEY_ENABLED = "recording_enabled"
    }
}
