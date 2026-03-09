package com.jigar.me.ui.view.jetpack.fragments.purchase.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.model.DisplayPurchaseData
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.base.inapp.BillingRepository
import com.jigar.me.ui.view.jetpack.core.StatefulViewModel
import com.jigar.me.ui.view.jetpack.core.repository.abacus_data.PurchaseRepository
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    private val purchaseRepository: PurchaseRepository,
    private val billingRepository: BillingRepository
) : StatefulViewModel<PurchaseUiState>() {

    override val TAG = "PurchaseViewModel"

    override fun getInitialState() = PurchaseUiState(
        benefitList = listOf(
            "⭐ <strong>Get unlimited access</strong> to all Abacus Levels, Exercises, Exams and Custom Challenge Modes Module.",
            "🧮 Practice Addition, Subtraction, Multiplication, Division, with <strong>smart bead directions</strong> and <strong>formula on every steps.</strong>",
            "🎯 Prepare for math competitions, UCMAS and abacus exams with <strong>real exam-style practice.</strong>",
            "📊 <strong>Track your child’s</strong> progress, speed and accuracy with detailed reports."
        )
    )

    // make purchase
    fun makePurchase(context: Activity) {
        billingRepository.launchBillingFlow(context, state().sortedSkuList[state().selectedIndex])
    }

    fun loadInitialData(purchasedSKU: List<InAppSkuDetails>) {
        val discountPer = prefManager.getCustomParamInt(AppConstants.RemoteConfig.discountPer, 0)
        val discountLifetime = prefManager.getCustomParamInt(AppConstants.RemoteConfig.discountPerLifeTime, 0)

        // admin assigned plan list
        val adminPlanList = prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA, "")
            .takeIf { it.isNotEmpty() }
            ?.let { Gson().fromJson<List<PlanAssignFromAdminData>>(it, object : TypeToken<List<PlanAssignFromAdminData>>() {}.type) }
            ?: emptyList()

        updateState_ {
            copy(
                discountPer = discountPer,
                discountPerLifetime = discountLifetime,
                planListAssignFromAdmin = adminPlanList
            )
        }
        setSubscription(purchasedSKU)
    }

    private fun setSubscription(purchasedSKU: List<InAppSkuDetails>) {
        viewModelScope.launch {
            val displayType = object : TypeToken<List<DisplayPurchaseData>>() {}.type
            val displayList: List<DisplayPurchaseData> = Gson().fromJson(prefManager.getCustomParam(AppConstants.RemoteConfig.displayPlanList, ""), displayType)
                ?: emptyList()

            val skuIds = displayList.map { it.id }.toMutableList()

            // admin assigned
            val yearPlanAssignFromAdmin = state().planListAssignFromAdmin.find {
                it.google_order_id == null && it.google_plan_id?.contains(BillingRepository.AbacusSku.PRODUCT_ID_1Year) == true
            }

            val allPlanAssignFromAdmin = state().planListAssignFromAdmin.find {
                it.google_order_id == null && it.google_plan_id?.contains(BillingRepository.AbacusSku.PRODUCT_ID_All) == true
            }

            if (yearPlanAssignFromAdmin != null && !skuIds.contains(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer)) {
                skuIds.add(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer)
            }
            if (allPlanAssignFromAdmin != null && !skuIds.contains(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_offer)) {
                skuIds.add(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_offer)
            }

            val allSkuIds = arrayListOf(
                BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Week1,
                BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month1,
                BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1,
                BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer,
                BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime,
                BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_offer
            )

            val oldPurchased = purchasedSKU.filter { sku -> allSkuIds.none { sku.sku.contains(it) } }
            val newPurchased = purchasedSKU.filter { sku -> allSkuIds.any { sku.sku.contains(it) } }

            updateState_ {
                copy(
                    displayItemList = displayList,
                    yearPlanAssignFromAdmin = yearPlanAssignFromAdmin,
                    allPlanAssignFromAdmin = allPlanAssignFromAdmin,
                    oldPurchasedSkuList = oldPurchased,
                    isOldSubscriptionThere = oldPurchased.isNotEmpty()
                )
            }


            newPurchased.forEach {
                if (!skuIds.contains(it.sku)) skuIds.add(it.sku)
            }

            purchaseRepository.getInAppSku(skuIds).collect {
                arrangeData(it)
            }
        }
    }


    private fun arrangeData(details: List<InAppSkuDetails>) {
        val skuList = details.toMutableList()
        val originalYear: InAppSkuDetails? = details.find { it.sku == BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1 }
        val originalLifetime: InAppSkuDetails? = details.find { it.sku == BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime }
        val original1MonthData: InAppSkuDetails? = details.find { it.sku == BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month1 }
        var showSubmit = true

        fun remove(planId: String) {
            skuList.removeAll { it.sku.contains(planId) }
        }

        fun removeExact(planId: String) {
            skuList.removeAll { it.sku == planId }
        }

        if (state().allPlanAssignFromAdmin != null) {
            remove(BillingRepository.AbacusSku.PRODUCT_ID_Week)
            remove(BillingRepository.AbacusSku.PRODUCT_ID_1Month)
            remove(BillingRepository.AbacusSku.PRODUCT_ID_1Year)
            removeExact(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime)
            showSubmit = false
        } else if (state().yearPlanAssignFromAdmin != null) {
            remove(BillingRepository.AbacusSku.PRODUCT_ID_Week)
            remove(BillingRepository.AbacusSku.PRODUCT_ID_1Month)
            removeExact(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1)
            remove(BillingRepository.AbacusSku.PRODUCT_ID_All)
            showSubmit = false
        } else {

            val monthlyPurchased = details.find {
                it.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_1Month) && it.isPurchase
            }
            if (monthlyPurchased != null) {
                remove(BillingRepository.AbacusSku.PRODUCT_ID_Week)
            }

            val yearPurchased = details.find {
                it.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_1Year) && it.isPurchase
            }

            if (yearPurchased != null) {
                if (yearPurchased.sku == BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1) {
                    removeExact(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer)
                } else {
                    removeExact(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1)
                }
                remove(BillingRepository.AbacusSku.PRODUCT_ID_Week)
                remove(BillingRepository.AbacusSku.PRODUCT_ID_1Month)
                showSubmit = false
            } else {
                if (state().discountPer > 0) {
                    removeExact(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1)
                } else {
                    removeExact(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer)
                }
            }

            val lifetimePurchased = details.find {
                it.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_All) && it.isPurchase
            }

            if (lifetimePurchased != null) {
                if (lifetimePurchased.sku == BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime) {
                    removeExact(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_offer)
                } else {
                    removeExact(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime)
                }
                remove(BillingRepository.AbacusSku.PRODUCT_ID_Week)
                remove(BillingRepository.AbacusSku.PRODUCT_ID_1Month)
                remove(BillingRepository.AbacusSku.PRODUCT_ID_1Year)
                showSubmit = false
            } else {
                if (state().discountPerLifetime > 0) {
                    removeExact(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime)
                } else {
                    removeExact(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_offer)
                }
            }
        }

        val sorted = skuList.sortedBy { (it.price_amount_micros ?: 0) }

        updateState_ {
            copy(
                inAppSkuDetailsList = skuList,
                sortedSkuList = sorted,
                original1YearData = originalYear,
                originalLifetimeData = originalLifetime,
                original1MonthData = original1MonthData,
                showSubmitButton = showSubmit
            )
        }
    }
    fun onPlanSelected(selectedIndex: Int) {
        updateState_ {
            copy(
                selectedIndex = selectedIndex
            )
        }
    }
    fun onShowOldSubClick() {
        updateState_ {
            copy(
                showOldSubscriptionPopup = true
            )
        }
    }
    fun oldSubPopupClose() {
        updateState_ {
            copy(
                showOldSubscriptionPopup = false
            )
        }
    }
    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }

}