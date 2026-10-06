package io.github.amjadaziz817.callvault

import android.content.Context
import io.github.amjadaziz817.callvault.model.Deal
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Stores deals as a JSON array in one file.
 *
 * The store uses org.json, which is built into Android. So the app needs no
 * extra library. The file lives in the app's internal storage.
 */
object DealStore {

    private const val FILE_NAME = "deals.json"

    private fun file(context: Context): File = File(context.filesDir, FILE_NAME)

    fun list(context: Context): List<Deal> {
        val f = file(context)
        if (!f.exists()) return emptyList()
        return runCatching {
            val array = JSONArray(f.readText())
            (0 until array.length()).map { fromJson(array.getJSONObject(it)) }
                .sortedByDescending { it.createdAt }
        }.getOrDefault(emptyList())
    }

    fun add(context: Context, deal: Deal) {
        val current = list(context).toMutableList()
        current.add(0, deal)
        save(context, current)
    }

    fun delete(context: Context, id: String) {
        save(context, list(context).filterNot { it.id == id })
    }

    private fun save(context: Context, deals: List<Deal>) {
        val array = JSONArray()
        deals.forEach { array.put(toJson(it)) }
        file(context).writeText(array.toString())
    }

    private fun toJson(deal: Deal): JSONObject = JSONObject().apply {
        put("id", deal.id)
        put("number", deal.number)
        put("crop", deal.crop)
        put("quantity", deal.quantity)
        put("unit", deal.unit)
        put("rate", deal.rate)
        put("deliveryDate", deal.deliveryDate)
        put("createdAt", deal.createdAt)
    }

    private fun fromJson(o: JSONObject): Deal = Deal(
        id = o.getString("id"),
        number = o.optString("number"),
        crop = o.optString("crop"),
        quantity = o.optString("quantity"),
        unit = o.optString("unit"),
        rate = o.optString("rate"),
        deliveryDate = o.optString("deliveryDate"),
        createdAt = o.optLong("createdAt")
    )
}
