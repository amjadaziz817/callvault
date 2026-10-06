package io.github.amjadaziz817.callvault

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.amjadaziz817.callvault.databinding.ActivityDealBinding
import io.github.amjadaziz817.callvault.model.Deal
import java.util.UUID

/**
 * Form to log a deal and send a WhatsApp confirmation.
 *
 * You can open it with an optional phone number, so the number is ready after
 * a call.
 */
class DealActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDealBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra(EXTRA_NUMBER)?.let { binding.number.setText(it) }

        binding.saveButton.setOnClickListener { save(sendWhatsApp = false) }
        binding.sendButton.setOnClickListener { save(sendWhatsApp = true) }
    }

    private fun save(sendWhatsApp: Boolean) {
        val deal = buildDeal()
        if (deal == null) {
            Toast.makeText(this, R.string.deal_missing_fields, Toast.LENGTH_SHORT).show()
            return
        }
        DealStore.add(this, deal)
        if (sendWhatsApp) Messaging.sendWhatsApp(this, deal)
        Toast.makeText(this, R.string.deal_saved, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun buildDeal(): Deal? {
        val crop = binding.crop.text.toString().trim()
        val quantity = binding.quantity.text.toString().trim()
        val rate = binding.rate.text.toString().trim()
        if (crop.isEmpty() || quantity.isEmpty() || rate.isEmpty()) return null
        return Deal(
            id = UUID.randomUUID().toString(),
            number = binding.number.text.toString().trim(),
            crop = crop,
            quantity = quantity,
            unit = binding.unit.text.toString().trim().ifEmpty { "kg" },
            rate = rate,
            deliveryDate = binding.deliveryDate.text.toString().trim(),
            createdAt = System.currentTimeMillis()
        )
    }

    companion object {
        const val EXTRA_NUMBER = "number"
    }
}
