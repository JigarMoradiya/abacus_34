package com.jigar.me.utils

import android.content.Context
import android.telephony.TelephonyManager
import com.revenuecat.purchases.Purchases
import java.util.Locale

/** The user's country as ISO 3166-1 alpha-2 ("IN"), for the admin's push filters; null when nothing is known. */
object DeviceCountry {
    // The Play Store country (what the user is billed in) is the most reliable; RevenueCat knows it once the store has
    // connected. Before that: the SIM or network country (the carrier, not a display setting), then the phone's region.
    fun code(context: Context): String? {
        val store = try { Purchases.sharedInstance.storefrontCountryCode } catch (e: Exception) { null }
        val telephony = try { context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager } catch (e: Exception) { null }
        val sim = try { telephony?.simCountryIso } catch (e: Exception) { null }
        val network = try { telephony?.networkCountryIso } catch (e: Exception) { null }
        return listOf(store, sim, network, Locale.getDefault().country)
            .map { it?.trim()?.uppercase(Locale.ROOT).orEmpty() }
            .firstOrNull { it.length == 2 && it.all { c -> c in 'A'..'Z' } }
    }
}
