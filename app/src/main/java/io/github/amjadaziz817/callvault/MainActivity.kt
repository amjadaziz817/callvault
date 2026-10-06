package io.github.amjadaziz817.callvault

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.amjadaziz817.callvault.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RecordingsAdapter
    private lateinit var prefs: Prefs

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { refresh() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = Prefs(this)

        adapter = RecordingsAdapter(
            onPlay = { rec -> RecordingActions.play(this, rec) },
            onShare = { rec -> RecordingActions.share(this, rec) },
            onDelete = { rec -> RecordingActions.delete(rec); refresh() }
        )
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = adapter

        binding.grantPermissions.setOnClickListener { requestPermissions() }
        binding.openAccessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        binding.speakerSwitch.isChecked = prefs.forceSpeaker
        binding.speakerSwitch.setOnCheckedChangeListener { _, checked ->
            prefs.forceSpeaker = checked
        }
        binding.logDeal.setOnClickListener {
            startActivity(Intent(this, DealActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        adapter.submit(RecordingStore.list(this))
        binding.setupCard.visibility =
            if (hasAllPermissions() && isAccessibilityEnabled()) android.view.View.GONE
            else android.view.View.VISIBLE
        binding.emptyText.visibility =
            if (adapter.itemCount == 0) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun requestPermissions() {
        val needed = requiredPermissions().filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        if (needed.isNotEmpty()) permissionLauncher.launch(needed.toTypedArray())
    }

    private fun requiredPermissions(): List<String> {
        val perms = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return perms
    }

    private fun hasAllPermissions(): Boolean = requiredPermissions().all {
        checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
    }

    private fun isAccessibilityEnabled(): Boolean {
        val expected = "$packageName/${CallRecordingService::class.java.name}"
        val enabled = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(enabled)
        while (splitter.hasNext()) {
            if (splitter.next().equals(expected, ignoreCase = true)) return true
        }
        return false
    }
}
