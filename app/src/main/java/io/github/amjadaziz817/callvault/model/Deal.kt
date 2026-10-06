package io.github.amjadaziz817.callvault.model

/**
 * One deal that the user agreed on a call.
 *
 * The user logs the deal after the call. The app can then send a WhatsApp or
 * SMS confirmation, so there is a written record of the rate and the quantity.
 */
data class Deal(
    val id: String,
    val number: String,
    val crop: String,
    val quantity: String,
    val unit: String,
    val rate: String,
    val deliveryDate: String,
    val createdAt: Long
) {
    /** Builds the confirmation text to send to the other person. */
    fun confirmationMessage(): String = buildString {
        append("Deal confirmed:\n")
        append("Crop: ").append(crop).append('\n')
        append("Quantity: ").append(quantity).append(' ').append(unit).append('\n')
        append("Rate: ").append(rate).append('\n')
        append("Delivery: ").append(deliveryDate).append('\n')
        append("Please reply OK to confirm.")
    }
}
