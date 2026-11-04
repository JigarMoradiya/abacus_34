package com.jigar.me.utils

import com.android.billingclient.api.ProductDetails
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.formatPlanPrice
import java.util.Locale
import kotlin.math.roundToInt

/**
 * A Google Play discount offer on the lifetime (one-time) product, set in the Play Console (Purchase options and offers > Add offer >
 * Discount: a percentage or an absolute amount, with an optional start and end time), as the plan card shows it.
 */
data class StoreOneTimeOffer(
    /** the price the offer sells at, and the regular price struck through beside it */
    val offerPriceText: String,
    val fullPriceText: String,
    /** % off: the one Play gives for a percentage offer, else worked out from the two prices (0 when too small to say) */
    val percentOff: Int,
    /** set when the console offer is an ABSOLUTE discount: the amount taken off ("₹500"), shown instead of the % */
    val amountOffText: String?,
    val offerToken: String,
    val offerId: String?,
    /** when the offer ends (epoch millis), null = it runs indefinitely; no countdown is shown for a store offer (they run for days) */
    val endMillis: Long?,
    val priceMicros: Long,
    val currencyCode: String,
) {
    /** "30% OFF" for a percentage offer, "₹500 OFF" for an absolute one, whichever the Play Console offer is; "" when neither can be said. */
    fun badgeText(context: android.content.Context): String = when {
        amountOffText != null -> context.getString(com.jigar.me.R.string.paywall_amount_off_store, amountOffText)
        percentOff > 0 -> context.getString(com.jigar.me.R.string.paywall_percent_off_store, percentOff)
        else -> ""
    }
}

/**
 * Reads the discount offers of the lifetime product from Google Play. RevenueCat (10.21) only sees the normal price of a one-time product
 * and buys that, so while an offer exists the app shows it from here and buys it itself (see [StoreOfferPurchaser]); with no offer
 * nothing changes and RevenueCat sells the plan as before. Never together with the timed ".offer" product: while that one runs the regular
 * lifetime plan is not shown at all.
 */
object StoreOneTimeOffers {
    /** One Play offer without Play's classes, so the choice can be tested. */
    data class Facts(
        val priceMicros: Long,
        val fullPriceMicros: Long?,
        val percentage: Int?,
        val amountMicros: Long?,
        val currency: String,
        val offerId: String?,
        val token: String,
        val startMillis: Long? = null,
        val endMillis: Long? = null,
        val isPreorder: Boolean = false,
        val isRental: Boolean = false,
    )

    /** Remote Config `store_one_time_offers`: on unless it says off / false / 0 / no (the switch to stop using store offers without a release). */
    fun enabled(raw: String?): Boolean = raw?.trim()?.lowercase(Locale.ROOT) !in setOf("off", "false", "0", "no")

    private fun isDiscount(f: Facts, now: Long): Boolean {
        val full = f.fullPriceMicros ?: return false
        if (f.isPreorder || f.isRental || f.priceMicros <= 0L || f.priceMicros >= full) return false
        if (f.startMillis != null && now < f.startMillis) return false
        if (f.endMillis != null && now >= f.endMillis) return false
        return true
    }

    /** The cheapest valid discount offer (a pre-order or a rental is not a discount), or null. */
    fun choose(facts: List<Facts>, now: Long = System.currentTimeMillis()): Facts? =
        facts.filter { isDiscount(it, now) }.minByOrNull { it.priceMicros }

    fun toOffer(f: Facts): StoreOneTimeOffer {
        val full = f.fullPriceMicros ?: f.priceMicros
        val percent = f.percentage ?: ((full - f.priceMicros) * 100.0 / full).roundToInt()
        return StoreOneTimeOffer(
            offerPriceText = formatPlanPrice(f.priceMicros, f.currency),
            fullPriceText = formatPlanPrice(full, f.currency),
            percentOff = percent.coerceAtLeast(0),
            amountOffText = f.amountMicros?.takeIf { f.percentage == null && it > 0L }?.let { formatPlanPrice(it, f.currency) },
            offerToken = f.token,
            offerId = f.offerId,
            endMillis = f.endMillis,
            priceMicros = f.priceMicros,
            currencyCode = f.currency,
        )
    }

    /** Every offer of a product that Play says this person can buy now (Play lists only the offers the person is eligible for). */
    fun factsOf(details: ProductDetails?): List<Facts> =
        details?.oneTimePurchaseOfferDetailsList.orEmpty().mapNotNull { o ->
            val token = o.offerToken ?: return@mapNotNull null   // an offer without its token cannot be bought
            Facts(
                priceMicros = o.priceAmountMicros,
                fullPriceMicros = o.fullPriceMicros,
                percentage = o.discountDisplayInfo?.percentageDiscount,
                amountMicros = o.discountDisplayInfo?.discountAmount?.discountAmountMicros,
                currency = o.priceCurrencyCode,
                offerId = o.offerId,
                token = token,
                startMillis = o.validTimeWindow?.startTimeMillis,
                endMillis = o.validTimeWindow?.endTimeMillis,
                isPreorder = o.preorderDetails != null,
                isRental = o.rentalDetails != null,
            )
        }

    fun read(details: ProductDetails?, now: Long = System.currentTimeMillis()): StoreOneTimeOffer? =
        choose(factsOf(details), now)?.let(::toOffer)
}
