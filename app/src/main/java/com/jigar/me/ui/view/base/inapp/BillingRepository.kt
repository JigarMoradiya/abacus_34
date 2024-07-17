package com.jigar.me.ui.view.base.inapp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.android.billingclient.api.*
import com.google.gson.Gson
import com.jigar.me.BuildConfig
import com.jigar.me.MyApplication
import com.jigar.me.data.local.db.inapp.purchase.InAppPurchaseDB
import com.jigar.me.data.local.db.inapp.sku.InAppSKUDB
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.productList
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.productListSubscription
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import kotlinx.coroutines.*
import java.lang.reflect.Type
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class BillingRepository @Inject constructor(
    private val context: Context,private val prefManager : AppPreferencesHelper, private val inAppSKUDB: InAppSKUDB,
    private val inAppPurchaseDB: InAppPurchaseDB) : PurchasesUpdatedListener, BillingClientStateListener{
    var playStoreBillingClient: BillingClient? = null

    companion object {
        private const val LOG_TAG = "BillingRepository"
    }

    fun startDataSourceConnections() {
        Log.d(LOG_TAG, "startDataSourceConnections")
        instantiateAndConnectToPlayBillingService()
    }

    fun endDataSourceConnections() {
        playStoreBillingClient?.endConnection()
        // normally you don't worry about closing a DB connection unless you have more than
        // one DB open. so no need to call 'localCacheBillingClient.close()'
        Log.d(LOG_TAG, "endDataSourceConnections")
    }

    private fun instantiateAndConnectToPlayBillingService() {
        init()
        connectToPlayBillingService()
    }

    private fun init() {
        playStoreBillingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases() // required or app will crash
            .setListener(this).build()
    }

    private fun connectToPlayBillingService(): Boolean {
        Log.d(LOG_TAG, "connectToPlayBillingService")
        if (playStoreBillingClient?.isReady == false) {
            playStoreBillingClient?.startConnection(this)
            return true
        }
        return false
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // will handle server verification, consumables, and updating the local cache
                Log.d(LOG_TAG, "onPurchasesUpdated **")
                purchases?.apply { processPurchases(this) }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                // item already owned? call queryPurchasesAsync to verify and process all such items
                Log.d(LOG_TAG, "onPurchasesUpdated ::"+billingResult.debugMessage)
                queryPurchasesAsync()
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                connectToPlayBillingService()
            }
            else -> {
                Log.i(LOG_TAG, billingResult.debugMessage)
            }
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.d(LOG_TAG, "onBillingSetupFinished successfully")
                queryProductDetailsAsync()
                queryPurchasesAsync()
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                //Some apps may choose to make decisions based on this knowledge.
                Log.d(LOG_TAG, billingResult.debugMessage)
            }
            else -> {
                //do nothing. Someone else will connect it through retry policy.
                //May choose to send to server though
                Log.d(LOG_TAG, billingResult.debugMessage)
            }
        }
    }

    private fun queryProductDetailsAsync() {
        if (playStoreBillingClient == null){
            init()
        }
        val paramsSubscription = QueryProductDetailsParams.newBuilder().setProductList(productListSubscription)
        playStoreBillingClient?.queryProductDetailsAsync(paramsSubscription.build()) { billingResult, productDetailsList ->
            if (productDetailsList.isNotEmpty()) {
                CoroutineScope(Job() + Dispatchers.IO).launch {
                    inAppSKUDB.saveInAppSKU(productDetailsList)
                }
            }
            // check billingResult
            // process returned productDetailsList
        }

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList)

        playStoreBillingClient?.queryProductDetailsAsync(params.build()) { billingResult, productDetailsList ->
            // Process the result
            if (productDetailsList.isNotEmpty()) {
                CoroutineScope(Job() + Dispatchers.IO).launch {
                    inAppSKUDB.saveInAppSKU(productDetailsList)
                }
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.d(LOG_TAG, "onBillingServiceDisconnected")
        connectToPlayBillingService()
    }

    /**
     * This is the function to call when user wishes to make a purchase. This function will
     * launch the Google Play Billing flow. The response to this call is returned in
     * [onPurchasesUpdated]
     */
    fun launchBillingFlow(activity: Activity, skuDetails: InAppSkuDetails) {
        val list : ArrayList<QueryProductDetailsParams.Product> = if (skuDetails.type == BillingClient.ProductType.INAPP){
            arrayListOf(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(skuDetails.sku)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build())
        }else{
            arrayListOf(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(skuDetails.sku)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build())
        }
        val params = QueryProductDetailsParams.newBuilder().setProductList(list)
        playStoreBillingClient?.queryProductDetailsAsync(params.build()) { billingResult, productDetailsList ->
            // Process the result
            if (productDetailsList.isNotEmpty()) {
                productDetailsList.filter { it.productId == skuDetails.sku }.also {
                    if (it.isNotNullOrEmpty()){
                        val productDetailsParamsList =
                            if (skuDetails.type == BillingClient.ProductType.INAPP){
                                listOf(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                        .setProductDetails(it.first())
                                        .build())
                            }else{
                                listOf(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                        .setProductDetails(it.first())
                                        .setOfferToken(skuDetails.offerToken?:"")
                                        .build())
                            }

                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
                            .build()

                        // Launch the billing flow
                        playStoreBillingClient?.launchBillingFlow(activity, billingFlowParams)
                    }
                }
            }
        }

    }
    private fun queryPurchasesAsync() {
        Log.d(LOG_TAG, "queryPurchasesAsync called")
        playStoreBillingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productList.isNotNullOrEmpty()){
                processPurchasesNew(purchaseList)
            }
        }
        playStoreBillingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productList.isNotNullOrEmpty()){
                processPurchasesNew(purchaseList)
            }
        }
    }

    private fun processPurchasesNew(purchasesResult: List<Purchase>) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            Log.d(LOG_TAG, "processPurchases called")
            val validPurchases = HashSet<Purchase>(purchasesResult.size)
            Log.d(LOG_TAG, "processPurchases newBatch content $purchasesResult")
            purchasesResult.forEach { purchase ->
//                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
//                    Log.d(LOG_TAG, "Received a pending purchase of SKU: ${purchase.sku}")
                    // handle pending purchases, e.g. confirm with users about the pending
                    // purchases, prompt them to complete it, etc.
                }else{
                    Log.d(LOG_TAG, "originalJson called else ="+Gson().toJson(purchase.originalJson))
                    Log.d(LOG_TAG, "signature called else ="+Gson().toJson(purchase.signature))
                    Log.d(LOG_TAG, "isAcknowledged called else ="+Gson().toJson(purchase.isAcknowledged))

                    if (isSignatureValid(purchase)) {

                        Log.d(LOG_TAG, "processPurchases called else success")
                        validPurchases.add(purchase)

                        // event log
                        MyApplication.logEvent(AppConstants.FirebaseEvents.InAppPurchase, Bundle().apply {
                            putString(AppConstants.FirebaseEvents.deviceId, prefManager.getDeviceId())
//                            putString(AppConstants.FirebaseEvents.InAppPurchaseSKU, purchase.sku)
                            putString(AppConstants.FirebaseEvents.InAppPurchaseOrderId, purchase.orderId)
                        })
                    }

                    if (!purchase.isAcknowledged) {
                        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                        withContext(Dispatchers.IO) {
                            playStoreBillingClient?.acknowledgePurchase(acknowledgePurchaseParams.build()) { result ->
                                Log.e(LOG_TAG,"isAcknowledged Result :" + Gson().toJson(result.responseCode)                                )
                                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                                    Log.e(LOG_TAG, "isAcknowledged success")
                                } else {
                                    Log.e(LOG_TAG, "isAcknowledged success not")
                                }
                            }
                        }
                    }

                }
            }
            /*
              As is being done in this sample, for extra reliability you may store the
              receipts/purchases to a your own remote/local database for until after you
              disburse entitlements. That way if the Google Play Billing library fails at any
              given point, you can independently verify whether entitlements were accurately
              disbursed. In this sample, the receipts are then removed upon entitlement
              disbursement.
             */
            Log.d(LOG_TAG, "processPurchases purchases ${Gson().toJson(validPurchases)}")
            CoroutineScope(Job() + Dispatchers.IO).launch {
                inAppPurchaseDB.saveInAppPurchase(*validPurchases.toTypedArray())
            }
        }
    }

    private fun processPurchases(purchasesResult: MutableList<Purchase>) =
        CoroutineScope(Job() + Dispatchers.IO).launch {
            Log.d(LOG_TAG, "processPurchases called")
            val validPurchases = HashSet<Purchase>(purchasesResult.size)
            Log.d(LOG_TAG, "processPurchases newBatch content $purchasesResult")
            purchasesResult.forEach { purchase ->
//                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
//                    Log.d(LOG_TAG, "Received a pending purchase of SKU: ${purchase.sku}")
                    // handle pending purchases, e.g. confirm with users about the pending
                    // purchases, prompt them to complete it, etc.
                }else{
                    Log.d(LOG_TAG, "originalJson called else ="+Gson().toJson(purchase.originalJson))
                    Log.d(LOG_TAG, "signature called else ="+Gson().toJson(purchase.signature))
                    Log.d(LOG_TAG, "isAcknowledged called else ="+Gson().toJson(purchase.isAcknowledged))

                    if (isSignatureValid(purchase)) {

                        Log.d(LOG_TAG, "processPurchases called else success")
                        validPurchases.add(purchase)

                        // event log
                        MyApplication.logEvent(AppConstants.FirebaseEvents.InAppPurchase, Bundle().apply {
                            putString(AppConstants.FirebaseEvents.deviceId, prefManager.getDeviceId())
//                            putString(AppConstants.FirebaseEvents.InAppPurchaseSKU, purchase.sku)
                            putString(AppConstants.FirebaseEvents.InAppPurchaseOrderId, purchase.orderId)
                        })
                    }

                    if (!purchase.isAcknowledged) {
                        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                        withContext(Dispatchers.IO) {
                            playStoreBillingClient?.acknowledgePurchase(acknowledgePurchaseParams.build()) { result ->
                                Log.e(LOG_TAG,"isAcknowledged Result :" + Gson().toJson(result.responseCode)                                )
                                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                                    Log.e(LOG_TAG, "isAcknowledged success")
                                } else {
                                    Log.e(LOG_TAG, "isAcknowledged success not")
                                }
                            }
                        }
                    }

                }
            }
            /*
              As is being done in this sample, for extra reliability you may store the
              receipts/purchases to a your own remote/local database for until after you
              disburse entitlements. That way if the Google Play Billing library fails at any
              given point, you can independently verify whether entitlements were accurately
              disbursed. In this sample, the receipts are then removed upon entitlement
              disbursement.
             */
            Log.d(LOG_TAG, "processPurchases purchases ${Gson().toJson(validPurchases)}")
            CoroutineScope(Job() + Dispatchers.IO).launch {
                inAppPurchaseDB.saveInAppPurchase(*validPurchases.toTypedArray())
            }
        }

    private fun isSignatureValid(purchase: Purchase): Boolean {
        return Security.verifyPurchase(
            Security.BASE_64_ENCODED_PUBLIC_KEY, purchase.originalJson, purchase.signature
        )
    }

    object AbacusSku {

        const val PRODUCT_ID_Test = "android.test.purchased"
        const val PRODUCT_ID_All_lifetime_old = "com.abacus.puzzle.onetime"

        const val PRODUCT_ID_All_lifetime = "com.abacus.all"
        const val PRODUCT_ID_level1_lifetime = "com.abacus.singledigit.starter"
        const val PRODUCT_ID_level2_lifetime = "com.abacus.addition.subtraction"
        const val PRODUCT_ID_level3_lifetime = "com.abacus.multiplication.division"
        const val PRODUCT_ID_ads = "com.abacus.ads"

        const val PRODUCT_ID_material_maths = "kids.material.maths.abacus"
        const val PRODUCT_ID_material_nursery = "kids.material.nursery"

        const val PRODUCT_ID_Subscription_Weekly_Test1 = "com.abacus.puzzle.week.test1"
        const val PRODUCT_ID_Subscription_Weekly_Test2 = "com.abacus.puzzle.week.test2"
        const val PRODUCT_ID_Subscription_Weekly = "com.abacus.puzzle.week"
        const val PRODUCT_ID_Subscription_Month1 = "com.abacus.puzzle.1month"
        const val PRODUCT_ID_Subscription_Month3 = "com.abacus.puzzle.3month"
        const val PRODUCT_ID_Subscription_Month6 = "com.abacus.puzzle.6month"
        const val PRODUCT_ID_Subscription_Year1 = "com.abacus.puzzle.1year"


        val productList: ArrayList<QueryProductDetailsParams.Product> = arrayListOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_All_lifetime)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_material_maths)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_material_nursery)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),

            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_All_lifetime_old)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_level1_lifetime)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_level2_lifetime)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_level3_lifetime)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_ads)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        )


        val productListSubscription: ArrayList<QueryProductDetailsParams.Product> =
            arrayListOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_ID_Subscription_Weekly)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_ID_Subscription_Month1)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_ID_Subscription_Month3)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_ID_Subscription_Month6)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_ID_Subscription_Year1)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )

    }

}