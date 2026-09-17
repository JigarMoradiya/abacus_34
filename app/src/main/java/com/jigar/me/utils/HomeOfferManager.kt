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
) {
    val isEnabled: Boolean get() = enabled == true && (duration_min ?: 0) > 0
    val durationMillis: Long get() = (duration_min ?: 0).toLong() * 60_000L
    // Which plan this campaign discounts. Missing/unrecognized value defaults to
    // yearly, so configs written before this field existed keep working unchanged.
    val targetsLifetime: Boolean get() = product?.equals("lifetime", ignoreCase = true) == true
}

// Time-limited "Home offer": a per-device window during which the yearly .offer
// plan is shown everywhere (exactly like the permanent `discount_per` switch) plus
// a countdown card on Home. Mirrors iOS HomeOfferManager.
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
        val cfg = config(prefs)
        val start = startedAt(prefs) ?: return null
        return start + cfg.durationMillis
    }

    // endMillis, but null unless the active window targets the yearly plan -- so a
    // campaign running for lifetime never leaks a countdown onto the yearly row.
    fun yearlyEndMillis(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Long? =
        endMillis(prefs, now)?.takeIf { !config(prefs).targetsLifetime }

    // Mirrors yearlyEndMillis for the lifetime plan.
    fun lifetimeEndMillis(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Long? =
        endMillis(prefs, now)?.takeIf { config(prefs).targetsLifetime }

    fun remainingMillis(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Long {
        val cfg = config(prefs)
        if (!cfg.isEnabled) return 0L
        val start = startedAt(prefs) ?: return 0L
        // Clamped so a device clock moved backwards can't produce an endless offer.
        return (start + cfg.durationMillis - now).coerceIn(0L, cfg.durationMillis)
    }

    fun isTimedOfferActive(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Boolean =
        remainingMillis(prefs, now) > 0L

    // The single flag every yearly paywall decision point consumes: the permanent
    // Remote Config discount OR the timed Home offer WHEN IT TARGETS YEARLY. When
    // the timed offer targets lifetime instead (or is inactive) this is exactly
    // `discount_per > 0`, so existing behaviour is unchanged.
    fun isYearlyDiscountActive(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Boolean =
        prefs.getCustomParamInt(AppConstants.RemoteConfig.discountPer, 0) > 0 ||
            (isTimedOfferActive(prefs, now) && !config(prefs).targetsLifetime)

    // Mirrors isYearlyDiscountActive for the lifetime plan.
    fun isLifetimeDiscountActive(prefs: PreferencesHelper, now: Long = System.currentTimeMillis()): Boolean =
        prefs.getCustomParamInt(AppConstants.RemoteConfig.discountPerLifeTime, 0) > 0 ||
            (isTimedOfferActive(prefs, now) && config(prefs).targetsLifetime)

    // % off derived from the real store prices (in micros), so it always matches
    // what the store actually charges -- including per-country pricing -- instead
    // of a hand-typed number. 0 when either price is unknown or there's no saving.
    fun discountPercent(baseMicros: Long?, offerMicros: Long?): Int {
        if (baseMicros == null || offerMicros == null || baseMicros <= 0L || offerMicros >= baseMicros) return 0
        return ((baseMicros - offerMicros) * 100.0 / baseMicros).roundToInt()
    }

    // The single "% OFF" every yearly-plan consumer should render: while the timed
    // offer is running for THIS product, it's always derived from the real base vs
    // .offer prices -- never the hand-typed Remote Config number, even if that
    // field happens to be set to something. The permanent value is only used as a
    // fallback when there's no timed offer running for this product at all. This
    // is the one formula (Home card, Purchase page, paywall badge) must all call
    // so they can't drift or disagree.
    fun effectiveYearDiscountPer(prefs: PreferencesHelper, baseMicros: Long?, offerMicros: Long?, now: Long = System.currentTimeMillis()): Int {
        if (isTimedOfferActive(prefs, now) && !config(prefs).targetsLifetime) {
            return discountPercent(baseMicros, offerMicros)
        }
        return prefs.getCustomParamInt(AppConstants.RemoteConfig.discountPer, 0).coerceAtLeast(0)
    }

    // Mirrors effectiveYearDiscountPer for the lifetime plan.
    fun effectiveLifetimeDiscountPer(prefs: PreferencesHelper, baseMicros: Long?, offerMicros: Long?, now: Long = System.currentTimeMillis()): Int {
        if (isTimedOfferActive(prefs, now) && config(prefs).targetsLifetime) {
            return discountPercent(baseMicros, offerMicros)
        }
        return prefs.getCustomParamInt(AppConstants.RemoteConfig.discountPerLifeTime, 0).coerceAtLeast(0)
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
