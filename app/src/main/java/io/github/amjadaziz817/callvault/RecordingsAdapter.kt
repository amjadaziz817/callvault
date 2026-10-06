package io.github.amjadaziz817.callvault

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.amjadaziz817.callvault.databinding.ItemRecordingBinding
import io.github.amjadaziz817.callvault.model.Recording
import java.text.DateFormat
import java.util.Date
import java.util.Locale

class RecordingsAdapter(
    private val onPlay: (Recording) -> Unit,
    private val onShare: (Recording) -> Unit,
    private val onDelete: (Recording) -> Unit
) : RecyclerView.Adapter<RecordingsAdapter.VH>() {

    private val items = mutableListOf<Recording>()

    fun submit(list: List<Recording>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRecordingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    inner class VH(private val binding: ItemRecordingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rec: Recording) {
            binding.number.text = rec.number.ifBlank { "Unknown" }
            binding.date.text = dateFormat.format(Date(rec.startedAt))
            binding.size.text = formatSize(rec.sizeBytes)
            binding.root.setOnClickListener { onPlay(rec) }
            binding.shareButton.setOnClickListener { onShare(rec) }
            binding.deleteButton.setOnClickListener { onDelete(rec) }
        }
    }

    private fun formatSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return String.format(Locale.US, "%.0f KB", kb)
        return String.format(Locale.US, "%.1f MB", kb / 1024.0)
    }

    companion object {
        private val dateFormat: DateFormat =
            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault())
    }
}
