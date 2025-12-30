package com.jigar.me.ui.view.jetpack.fragments.home.viewmodels

import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.jigar.me.data.model.data.GooglePurchasedPlanRequest
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.base.inapp.BillingRepository
import com.jigar.me.ui.view.jetpack.core.StatefulViewModel
import com.jigar.me.ui.view.jetpack.core.domain.ConsumableCommand
import com.jigar.me.ui.view.jetpack.core.repository.abacus_data.AbacusDataRepository
import com.jigar.me.ui.view.jetpack.core.repository.abacus_data.PurchaseRepository
import com.jigar.me.ui.view.jetpack.fragments.home.interator.ChangePurchaseUseCase
import com.jigar.me.ui.view.jetpack.fragments.home.interator.DevicePurchaseVerifyUseCase
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val prefs: AppPreferencesHelper,
    private val abacusDataRepository: AbacusDataRepository,
    private val purchaseRepository: PurchaseRepository,
    private val devicePurchaseVerifyUseCase: DevicePurchaseVerifyUseCase,
    private val changePurchaseUseCase: ChangePurchaseUseCase
) : StatefulViewModel<HomeUiState>() {

    override val TAG = "HomeFragmentViewModel"

    override fun getInitialState() = HomeUiState()

    init {
        viewModelScope.launch{
            loadHomeMenu()
            setPurchaseData()
        }
    }
    private suspend fun loadHomeMenu() {
        try {
            val displayMenuList = getDisplayMenuList()
            abacusDataRepository.getLevels(displayMenuList).collect {
                updateState_ {
                    copy(menuLevels = it)
                }
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private suspend fun setPurchaseData() {
        purchaseRepository.getPurchasedSku().collect { purchasedList ->
            updateState_ {
                copy(purchasedList = purchasedList)
            }
            if (purchasedList.isNotEmpty()) {
                val list = createPurchasedPlanRequest(purchasedList)
                if (list.isNotEmpty()){
                    updateState_ {
                        copy(purchasedRequest = purchasedRequest)
                    }
                    devicePurchaseVerifyUseCase(
                        params = PurchasedPlanCheckRequest(ArrayList(list)),
                        onStart = { },
                        onEachEmit = {
                            when (it) {
                                AppConstants.APIStatus.SUCCESS -> {
                                    checkFreeTrial()
                                }
                                AppConstants.APIStatus.ERROR_CODE_OTHER_STUDENT_IS_ASSOCIATED_WITH_THIS_ORDER,AppConstants.APIStatus.ERROR_CODE_THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER -> {
                                    updateState_ { copy(purchasedConflictPopup = ConsumableCommand(it)) }
                                }
                            }
                        },
                        onCompletion = {},
                        onError = {}
                    ).catch {}.collect()
                }
            } else {
                checkFreeTrial()
            }
        }
    }
    fun changePurchase() = viewModelScope.launch{
        if (state().purchasedRequest.isNotNullOrEmpty()){
            devicePurchaseVerifyUseCase(
                params = PurchasedPlanCheckRequest(ArrayList(state().purchasedRequest)),
                onStart = { updateState_ { copy(isLoading = true) } },
                onEachEmit = { },
                onCompletion = {
                    updateState_ { copy(isLoading = false) }
                    checkFreeTrial()
                },
                onError = {
                    updateState_ { copy(isLoading = false) }
                }
            ).catch {}.collect()
        }
    }

    private fun checkFreeTrial() {
        prefs.setUserInFreeTrial(false)
        val purchasedSKU = state().purchasedList
        val isPurchased = CommonUtils.checkPurchaseForExerciseExamCCM(prefs, purchasedSKU)

        if (isPurchased) {
            updateState_ { copy(checkNotificationPermission = ConsumableCommand(Unit)) }
            return
        }

        val remainingDays = prefs.getCustomParamInt(Constants.free_trial_remaining_days, 0)
        prefs.setUserInFreeTrial(remainingDays > 0)
        val lastChecked = prefs.getCustomParamInt(Constants.free_trial_remaining_days_last_checked, -1)
        if (lastChecked != remainingDays || remainingDays == 0) {
            prefs.setCustomParamInt(Constants.free_trial_remaining_days_last_checked,remainingDays)
            val discountPer = prefs.getCustomParamInt(AppConstants.RemoteConfig.discountPer,0)
            val discountPerLifetime = prefs.getCustomParamInt(AppConstants.RemoteConfig.discountPerLifeTime,0)
            updateState_ { copy(showFreeTrialPopup = ConsumableCommand(FreeTrialParam(remainingDays,discountPer,discountPerLifetime))) }
        } else {
            updateState_ { copy(checkNotificationPermission = ConsumableCommand(Unit)) }
        }
    }

    private fun getDisplayMenuList(): List<String> {
        val menuListStr = prefs.getCustomParam(AppConstants.RemoteConfig.displayMenuList, "")
        return if (menuListStr.isNotEmpty() && menuListStr.length > 5) {
            val type = object : TypeToken<ArrayList<String>>() {}.type
            Gson().fromJson(menuListStr, type)
        } else {
            listOf(
                AppConstants.HomeClicks.Menu_Abacus_Free_Mode,
                AppConstants.HomeClicks.Menu_Practice_Abacus,
                AppConstants.HomeClicks.Menu_Abacus_Exercise,
                AppConstants.HomeClicks.Menu_Exam,
                AppConstants.HomeClicks.Menu_CCM,
                AppConstants.HomeClicks.Menu_Math_Game,
                AppConstants.HomeClicks.Menu_Purchase_Store,
                AppConstants.HomeClicks.Menu_Settings,
                AppConstants.HomeClicks.Menu_My_Account,
                AppConstants.HomeClicks.Menu_Video_Tutorial
            )
        }
    }

    private val purchasedListReq = mutableListOf<GooglePurchasedPlanRequest>()
    fun createPurchasedPlanRequest(purchasedList: List<InAppSkuDetails>): List<GooglePurchasedPlanRequest> {
        purchasedListReq.clear()
        purchasedList.forEach { skuDetails ->
            purchasedListReq.add(buildPurchasedPlanRequest(skuDetails))
        }
        return purchasedListReq
    }

    private fun buildPurchasedPlanRequest(item: InAppSkuDetails): GooglePurchasedPlanRequest {
        val googlePlanId = item.sku
        val googleOrderId = item.orderId
        val isAllFeature = googlePlanId == BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime || googlePlanId == BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_old
        val startDate = item.purchaseTime
        val purchasePrice = (item.price_amount_micros ?: 0L).toDouble() / 1_000_000
        val purchaseCurrency = item.price_currency_code
        var isLifetimePlan = false
        var endDate = 0L
        var noOfRenewals = 0

        if (item.type == BillingClient.ProductType.INAPP) { // Lifetime purchase
            isLifetimePlan = true
        } else { // Subscription purchase
            noOfRenewals = extractRenewalCount(item.orderId)
            endDate = calculateSubscriptionEndDate(purchaseTime = startDate, billingPeriod = item.billingPeriod)
        }

        return GooglePurchasedPlanRequest(
            google_plan_id = googlePlanId,
            google_order_id = googleOrderId,
            is_lifetime_plan = isLifetimePlan,
            is_all_feature = isAllFeature,
            start_date = startDate,
            end_date = endDate,
            purchase_price = purchasePrice,
            purchase_currency = purchaseCurrency,
            no_of_renewals = noOfRenewals
        )
    }

    private fun extractRenewalCount(orderId: String): Int {
        if (!orderId.contains("_")) return 0

        return try {
            orderId.split("_")[1].toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    private fun calculateSubscriptionEndDate(purchaseTime: Long, billingPeriod: String?): Long {

        val calendarEnd = Calendar.getInstance().apply {
            timeInMillis = purchaseTime
        }

        val daysPassed = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - purchaseTime)

        when {
            billingPeriod.equals("p1w", true) -> {
                val weeks = (daysPassed / 7) + 1
                calendarEnd.add(Calendar.WEEK_OF_MONTH, weeks.toInt())
            }

            billingPeriod.equals("p1y", true) -> {
                val years = (daysPassed / 365) + 1
                calendarEnd.add(Calendar.YEAR, years.toInt())
            }

            billingPeriod.equals("p1m", true) -> {
                calendarEnd.add(Calendar.MONTH, ((daysPassed / 30) + 1).toInt())
            }

            billingPeriod.equals("p3m", true) -> {
                calendarEnd.add(Calendar.MONTH, ((daysPassed / 90) + 3).toInt())
            }

            else -> { // p6m
                calendarEnd.add(Calendar.MONTH, ((daysPassed / 180) + 6).toInt())
            }
        }

        return calendarEnd.timeInMillis
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }

}