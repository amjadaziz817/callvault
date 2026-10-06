package io.github.amjadaziz817.callvault

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import io.github.amjadaziz817.callvault.model.Deal

/**
 * Sends a deal confirmation over WhatsApp, with an SMS fallback.
 *
 * WhatsApp opens with the message ready. The user taps send. The app does not
 * send the message by itself, so the user keeps control.
 */
object Messaging {

    fun sendWhatsApp(context: Context, deal: Deal) {
        val phone = deal.number.filter { it.isDigit() }
        val text = Uri.encode(deal.confirmationMessage())
        val url = if (phone.isNotBlank()) "https://wa.me/$phone?text=$text"
        else "https://wa.me/?text=$text"
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, R.string.whatsapp_missing, Toast.LENGTH_LONG).show()
            sendSms(context, deal)
        }
    }

    fun sendSms(context: Context, deal: Deal) {
        val uri = Uri.parse("smsto:${deal.number}")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", deal.confirmationMessage())
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, R.string.no_sms_app, Toast.LENGTH_LONG).show()
        }
    }
}
