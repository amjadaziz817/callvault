package io.github.amjadaziz817.callvault.model

import java.io.File

/** One saved call recording. */
data class Recording(
    val file: File,
    val number: String,
    val startedAt: Long,
    val durationMs: Long
) {
    val sizeBytes: Long get() = file.length()
}
