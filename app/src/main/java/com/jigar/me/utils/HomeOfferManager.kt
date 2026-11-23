package com.jigar.me.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.pref.PreferencesHelper
import kotlin.math.roundToInt

// Parsed shape of the `home_offer` Remote Config JSON. Every field is nullable so
// a missing key, a partial object or a malformed value can never crash -- it just
// reads as "disabled". Unknown extra fields are ignored by Gson.
data class HomeOfferConfig(
    val enabled: Boolean? = null,
    val duration_min: Int? = null,
    val name: String? = null,
    val product: String? = null,
    /** "dismiss" = a one-time offer: the window starts the first time a parent closes a paywall without buying
     *  (missing or anything else = it starts on the first Home visit, as before). */
    val trigger: String? = null,
    /** A fixed calendar end shared by every phone (ISO-8601, e.g. "2026-12-15T18:29:59Z" or "...+05:30"). When it is set the window is
     *  open until that moment (no per-device start); `duration_min` is then only the fallback of app versions that do not know this field. */
    val end_at: String? = null,
    /** "biggest" = the "Biggest offer" campaign: the yearly ".offer" plan is the only plan that can be bought, the others stay visible but
     *  disabled, and the yearly card wears the "Biggest offer" badge. Missing or anything else = a plain timed offer, as before. */
    val campaign: String? = null,
    /** true = an offer with NO end: it runs until `enabled` is set to false, for every phone, with no countdown anywhere. It beats `end_at`,
     *  `duration_min` and `trigger` (all ignored). App versions that do not know this field read `duration_min` 0 as "off", so they show no offer. */
    val no_end: Boolean? = null,
) {
    val endAtMillis: Long? get() = parseEndAt(end_at)
    /** An offer with no end (`no_end` true and `enabled` true). */
    val isOpenEnded: Boolean get() = enabled == true && no_end == true
    /** The calendar end every phone shares: `end_at`, unless the offer has no end. */
    val sharedEndMillis: Long? get() = if (isOpenEnded) null else endAtMillis
    val isEnabled: Boolean get() = enabled == true && ((duration_min ?: 0) > 0 || endAtMillis != null || no_end == true)
    val durationMillis: Long get() = (duration_min ?: 0).toLong() * 60_000L
    val isBiggestCampaign: Boolean get() = campaign?.equals("biggest", ignoreCase = true) == true

    /** When the window of this campaign ends: the shared [endAtMillis] when there is one, else this device's start + duration (null: not started). */
    fun windowEndMillis(startedAt: Long?): Long? = if (isOpenEnded) null else endAtMillis ?: startedAt?.let { it + durationMillis }

    /** Milliseconds left at [now], 0 when over or not started. A per-device window is clamped to its length, so a clock moved backwards cannot stretch it. */
    fun remainingMillis(startedAt: Long?, now: Long): Long {
        if (!isEnabled) return 0L
        if (isOpenEnded) return Long.MAX_VALUE   // no end: it never runs out (nothing shows a countdown for it: windowEndMillis is null)
        val end = windowEndMillis(startedAt) ?: return 0L
        val left = end - now
        return if (endAtMillis != null) left.coerceAtLeast(0L) else left.coerceIn(0L, durationMillis)
    }
    // Which plan this campaign discounts. Missing/unrecognized value defaults to
    // yearly, so configs written before this field existed keep working unchanged.
    val targetsLifetime: Boolean get() = product?.equals("lifetime", ignoreCase = true) == true
    val startsOnDismiss: Boolean get() = trigger?.equals("dismiss", ignoreCase = true) == true && !isOpenEnded
}

/** An ISO-8601 instant ("2026-12-15T18:29:59Z", "2026-12-15T23:59:59+05:30", with or without fraction) as epoch millis; null when missing or unreadable. */
internal fun parseEndAt(text: String?): Long? {
    val t = text?.trim().takeIf { !it.isNullOrEmpty() } ?: return null
    val patterns = listOf("yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    for (p in patterns) {
        val ms = runCatching {
            java.text.SimpleDateFormat(p, java.util.Locale.US).apply { isLenient = false }.parse(t)?.time
        }.getOrNull()
        if (ms != null) return ms
    }
    return null
}

/**
 * The rules of the "Biggest offer" campaign that need no screen, so they can be unit tested: the others are locked (shown disabled, priced)
 * only while the ".offer" plan of the campaign (yearly, or lifetime when `home_offer` says product "lifetime") can really be bought AND is
 * cheaper than its regular plan. Anything else keeps every plan on offer, so the paywall can never end up with nothing to buy (offer missing
 * in a country, same price, already subscribed).
 */
object BiggestOffer {
    fun isLocked(campaignRequested: Boolean, offerBuyable: Boolean, offerMicros: Long?, regularMicros: Long?): Boolean =
        campaignRequested && offerBuyable && offerMicros != null && regularMicros != null && offerMicros in 1 until regularMicros
}

// The time-limited "Home offer" (Remote Config `home_offer`): while it runs the yearly (or lifetime) .offer plan is shown everywhere
// instead of the regular one, plus a countdown card on Home. The window is per device (start + duration_min) or, with `end_at`, one
// calendar end for everybody. Mirrors iOS HomeOfferManager.
object HomeOfferManager {

    fun config(prefs: PreferencesHelper): HomeOfferConfig {
        val json = prefs.getCustomParam(AppConstants.RemoteConfig.homeOffer, "")
        if (json.isBlank()) return HomeOfferConfig()
        return runCatching { Gson().fromJson(json, HomeOfferConfig::class.java) }.getOrNull() ?: HomeOfferConfig()
    }

    // Prefs have no Long accessor, so the epoch is stored as a String (same pattern
    // as MathGameZoneViewModel's daily-challenge day).
    fun startedAt(prefs: PreferencesHelper): Long? =
        prefs.getCustomParam(AppConstants.HomeOffer.startedAt, "").toLongOrNull()

    // Called from Home on appearance. The window starts the first time THIS device
    // sees the offer enabled -- or when the campaign `name` changes, which counts as
    // a new campaign (so OFF -> ON isn't the only way to re-run an offer). An
    // already-expired window with the same name stays expired.
    //
    // `forceRestart` (debug builds only, set by the caller) skips the "already
    // started" check below so every app launch gets a fresh window -- testing never
    // has to wait out or manually reset an expired countdown. Release behaviour
    // (forceRestart = false) is completely unchanged.
    fun startIfNeeded(prefs: PreferencesHelper, now: Long = System.currentTimeMillis(), forceRestart: Boolean = false) {
        val cfg = config(prefs)
        if (!cfg.isEnabled) return
        if (!forceRestart) {
            val storedName = prefs.getCustomParam(AppConstants.HomeOffer.startedName, "")
            if (startedAt(prefs) != null && storedName == (cfg.name ?: "")) return
        }
        prefs.setCustomParam(AppConstants.HomeOffer.startedAt, now.toString())
        prefs.setCustomParam(AppConstants.HomeOffer.startedName, cfg.name ?: "")
    }

    // A paywall sheet was closed without a purchase after a parent passed the grown-up question: for a campaign with
    // `"trigger": "dismiss"` this is the moment its one-time window starts. A normal campaign ignores it.
    fun startOnDismissIfNeeded(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()) {
        val cfg = config(prefs)
        if (!cfg.isEnabled || !cfg.startsOnDismiss) return
        startIfNeeded(prefs, now)
    }

    // Called at Remote Config persist time when the config comes back disabled or
    // missing, so turning it OFF then ON later is a fresh campaign for everyone.
    fun clearStart(prefs: PreferencesHelper) {
        prefs.setCustomParam(AppConstants.HomeOffer.startedAt, "")
        prefs.setCustomParam(AppConstants.HomeOffer.startedName, "")
    }

    // Wall-clock end time of the current window, or null if not running (including
    // already expired). Lets any screen (Home, Purchase page, ...) render its own
    // live countdown from a single shared timestamp instead of each re-deriving
    // start+duration separately.
    fun endMillis(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Long? {
        if (!isTimedOfferActive(prefs, now)) return null
        return config(prefs).windowEndMillis(startedAt(prefs))
    }

    /** The "Biggest offer" campaign is running (the Home offer is active and says campaign "biggest"), on the plan `product` names: yearly, or lifetime. */
    fun isBiggestCampaignActive(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Boolean =
        isTimedOfferActive(prefs, now) && config(prefs).isBiggestCampaign

    // endMillis, but null unless the active window targets the yearly plan -- so a
    // campaign running for lifetime never leaks a countdown onto the yearly row.
    fun yearlyEndMillis(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Long? =
        endMillis(prefs, now)?.takeIf { !config(prefs).targetsLifetime }

    // Mirrors yearlyEndMillis for the lifetime plan.
    fun lifetimeEndMillis(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Long? =
        endMillis(prefs, now)?.takeIf { config(prefs).targetsLifetime }

    fun remainingMillis(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Long =
        config(prefs).remainingMillis(startedAt(prefs), now)   // a per-device window is clamped, so a clock moved backwards can't make it endless

    fun isTimedOfferActive(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Boolean =
        remainingMillis(prefs, now) > 0L

    // % off derived from the real store prices (in micros), so it always matches
    // what the store actually charges -- including per-country pricing -- instead
    // of a hand-typed number. 0 when either price is unknown or there's no saving.
    fun discountPercent(baseMicros: Long?, offerMicros: Long?): Int {
        if (baseMicros == null || offerMicros == null || baseMicros <= 0L || offerMicros >= baseMicros) return 0
        return ((baseMicros - offerMicros) * 100.0 / baseMicros).roundToInt()
    }

    // The base+.offer product ids the active timed offer targets, or empty when
    // inactive. The paywall always includes this pair regardless of the
    // display_plan whitelist -- home_offer is meant to be fully self-contained
    // (enabled + product decide everything), so an owner never has to remember to
    // also update display_plan just to make the timed offer visible.
    fun targetProductIds(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Set<String> {
        if (!isTimedOfferActive(prefs, now)) return emptySet()
        return if (config(prefs).targetsLifetime) setOf(AppConstants.Products.lifetime, AppConstants.Products.lifetimeOffer)
        else setOf(AppConstants.Products.year, AppConstants.Products.yearOffer)
    }
}
