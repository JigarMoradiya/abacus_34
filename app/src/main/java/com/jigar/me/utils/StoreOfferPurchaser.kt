package com.jigar.me.utils

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Buys the lifetime product WITH its Play discount offer. RevenueCat's purchase call never sets the offer token of a one-time product, so
 * it would charge the full price while the card shows the offer: this talks to Google Play directly, only while an offer exists, then the
 * caller tells RevenueCat (`syncPurchases`) so the premium access is granted as for any other purchase.
 * Call from the main thread (the billing flow needs the activity).
 */
class StoreOfferPurchaser(context: Context) {
    private val appContext = context.applicationContext

    sealed class Result {
        data class Purchased(val purchase: Purchase) : Result()
        object Pending : Result()
        object Cancelled : Result()
        /** Play already has this purchase for the account: restore it */
        object AlreadyOwned : Result()
        /** the offer ended between the card and the tap (or the person is no longer eligible): nothing was charged */
        object OfferGone : Result()
        data class Failed(val code: Int, val message: String?) : Result()
    }

    suspend fun purchase(activity: Activity, productId: String): Result {
        val outcome = CompletableDeferred<Result>()
        val client = BillingClient.newBuilder(appContext)
            .setListener { billingResult, purchases -> outcome.complete(outcomeOf(billingResult, purchases, productId)) }
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()
        try {
            if (!client.connect()) return Result.Failed(BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE, "could not connect to Google Play")
            val details = client.queryProduct(productId)
                ?: return Result.Failed(BillingClient.BillingResponseCode.ITEM_UNAVAILABLE, "product not found")
            // asked again now: the offer may have ended since the card was drawn
            val offer = StoreOneTimeOffers.choose(StoreOneTimeOffers.factsOf(details)) ?: return Result.OfferGone
            val params = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(details).setOfferToken(offer.token).build())
                )
                .build()
            val launched = client.launchBillingFlow(activity, params)
            if (launched.responseCode != BillingClient.BillingResponseCode.OK) {
                return outcomeOf(launched, null, productId)
            }
            val result = outcome.await()
            // a non-consumable must be acknowledged within 3 days or Google refunds it: done here, RevenueCat would do it later
            if (result is Result.Purchased && !result.purchase.isAcknowledged) client.acknowledge(result.purchase)
            return result
        } finally {
            client.endConnection()
        }
    }

    private fun outcomeOf(r: BillingResult, purchases: List<Purchase>?, productId: String): Result = when (r.responseCode) {
        BillingClient.BillingResponseCode.OK -> {
            val purchase = purchases?.firstOrNull { productId in it.products }
            when (purchase?.purchaseState) {
                Purchase.PurchaseState.PURCHASED -> Result.Purchased(purchase)
                Purchase.PurchaseState.PENDING -> Result.Pending
                else -> Result.Failed(r.responseCode, "no purchase came back")
            }
        }
        BillingClient.BillingResponseCode.USER_CANCELED -> Result.Cancelled
        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> Result.AlreadyOwned
        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> Result.OfferGone   // Play also answers this when the offer's limit or region no longer fits
        else -> Result.Failed(r.responseCode, r.debugMessage)
    }

    private suspend fun BillingClient.connect(): Boolean = suspendCancellableCoroutine { cont ->
        startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (cont.isActive) cont.resume(result.responseCode == BillingClient.BillingResponseCode.OK)
            }
            override fun onBillingServiceDisconnected() {
                if (cont.isActive) cont.resume(false)
            }
        })
    }

    private suspend fun BillingClient.queryProduct(productId: String): ProductDetails? = suspendCancellableCoroutine { cont ->
        val product = QueryProductDetailsParams.Product.newBuilder().setProductId(productId).setProductType(BillingClient.ProductType.INAPP).build()
        queryProductDetailsAsync(QueryProductDetailsParams.newBuilder().setProductList(listOf(product)).build()) { _, result ->
            if (cont.isActive) cont.resume(result.productDetailsList.firstOrNull())
        }
    }

    private suspend fun BillingClient.acknowledge(purchase: Purchase) = suspendCancellableCoroutine<Unit> { cont ->
        acknowledgePurchase(AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()) {
            if (cont.isActive) cont.resume(Unit)
        }
    }
}
