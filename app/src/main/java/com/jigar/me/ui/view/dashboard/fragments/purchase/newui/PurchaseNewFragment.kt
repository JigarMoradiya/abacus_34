package com.jigar.me.ui.view.dashboard.fragments.purchase.newui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.R
import com.jigar.me.data.model.DisplayPurchaseData
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.databinding.FragmentPurchasePreviewBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.inapp.BillingRepository
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.OldPurchasesBottomSheet
import com.jigar.me.ui.view.dashboard.fragments.purchase.newui.adapter.PurchaseNewAdapter
import com.jigar.me.ui.view.dashboard.fragments.purchase.newui.adapter.PurchaseInfoAdapter
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.ui.viewmodel.InAppViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PurchaseNewFragment : BaseFragment(){
    private val inAppViewModel by activityViewModels<InAppViewModel>()
    private lateinit var binding: FragmentPurchasePreviewBinding
    private lateinit var mNavController: NavController
    private lateinit var purchaseNewAdapter: PurchaseNewAdapter
    private lateinit var purchaseInfoAdapter: PurchaseInfoAdapter
    private val apiViewModel by viewModels<AppViewModel>()

    private var isMonthlyPlanSubscribe = false
    private var isWeekPlanSubscribe = false
    private var isYearlyPlanSubscribe = false
    private var isYearlyPlanOfferSubscribe = false
    private var isYearPlanAssignFromAdmin = false
    private var displayItemList: ArrayList<DisplayPurchaseData> = arrayListOf()
    private val list: ArrayList<String> = arrayListOf(
        "⭐ <strong>Get unlimited access</strong> to all Abacus Levels, Exercises, Exams and Custom Challenge Modes Module.",
        "🧮 Practice Addition, Subtraction, Multiplication, Division, with <strong>smart bead directions</strong> and <strong>formula on every steps.</strong>",
        "🎯 Prepare for math competitions, UCMAS and abacus exams with <strong>real exam-style practice.</strong>",
        "📊 <strong>Track your child’s</strong> progress, speed and accuracy with detailed reports."
    )

    private var planListAssignFromAdmin : List<PlanAssignFromAdminData> = arrayListOf()
    private var inAppSkuDetailsList : ArrayList<InAppSkuDetails> = arrayListOf()
    private var oldPurchasedSkuList : List<InAppSkuDetails> = arrayListOf()
    private var discountPer : Int = 0
    private var discountPerLifetime : Int = 0
    private var original1YearData : InAppSkuDetails? = null
    private var originalLifetimeData : InAppSkuDetails? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPurchasePreviewBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initViews()
        initListener()
        return binding.root
    }
    private fun setNavigationGraph() {
        mNavController = requireActivity().findNavController(R.id.nav_host_fragment)
    }

    private fun initListener() {
        with(binding){
            imgClose.onClick { mNavController.navigateUp() }
            btnSubmit.onClick {
                val selectedPlan = purchaseNewAdapter.getSelectedData()
//                MyApplication.logEvent("Purchase_"+selectedPlan.sku, null)
                inAppViewModel.makePurchase(requireActivity(), selectedPlan)
            }
            btnOldSubscription.onClick {
                OldPurchasesBottomSheet.showPopup(requireActivity(),oldPurchasedSkuList)
            }
        }
    }

    private fun initViews() = with(binding){
        spaceNotch.layoutParams.width = prefManager.getCustomParamInt(AppConstants.NOTCH_HEIGHT,0)

        discountPer = prefManager.getCustomParamInt(AppConstants.RemoteConfig.discountPer,0)
        discountPerLifetime = prefManager.getCustomParamInt(AppConstants.RemoteConfig.discountPerLifeTime,0)
//        discountPer = 0
//        discountPerLifetime = 0
        if (prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA,"").isNotEmpty()) {
            planListAssignFromAdmin = Gson().fromJson(
                prefManager.getCustomParam( Constants.PLAN_ASSIGN_FROM_ADMIN_DATA, ""),
                object : TypeToken<List<PlanAssignFromAdminData>>() {}.type
            )
        }
        purchaseNewAdapter = PurchaseNewAdapter(arrayListOf(), planListAssignFromAdmin, discountPer, discountPerLifetime,recyclerview)
        recyclerview.adapter = purchaseNewAdapter

        purchaseInfoAdapter = PurchaseInfoAdapter(list)
        recyclerviewInfo.adapter = purchaseInfoAdapter

        setSubscription()
    }


    private fun setSubscription() = with(binding) {
        lifecycleScope.launch {
            val idList: ArrayList<String> = arrayListOf()
            val type = object : TypeToken<ArrayList<DisplayPurchaseData>>() {}.type
             displayItemList = Gson().fromJson(prefManager.getCustomParam(AppConstants.RemoteConfig.displayPlanList,""),type)
//            idList.add(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Week1)
//            idList.add(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month1)
//            idList.add(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1)
//            idList.add(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer)
            idList.add(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime)
            idList.add(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_offer)

            displayItemList.map {
                idList.add(it.id)
            }
//            check yearly plan is assigned from admin
            planListAssignFromAdmin.find { it.google_order_id == null && it.google_plan_id?.contains(BillingRepository.AbacusSku.PRODUCT_ID_1Year) == true }.also {
                if (it != null){
                    isYearPlanAssignFromAdmin = true
                    if (!idList.contains(BillingRepository.AbacusSku.PRODUCT_ID_1Year)){
                        idList.add(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1)
                    }
                }
            }

            val idListNew: ArrayList<String> = arrayListOf()
            idListNew.add(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Week1)
            idListNew.add(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month1)
            idListNew.add(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1)
            idListNew.add(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer)
            idListNew.add(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime)
            idListNew.add(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_offer)
            oldPurchasedSkuList = apiViewModel.getInAppSKUPurchasedLiveExclude(idListNew)
            if (oldPurchasedSkuList.isNotNullOrEmpty()){
                btnOldSubscription.show()
            }else{
                btnOldSubscription.hide()
            }

            val newPurchasedSkuList = apiViewModel.getInAppSKUPurchased(idListNew)
            newPurchasedSkuList.map {
                if (!idList.contains(it.sku)){
                    idList.add(it.sku)
                }
            }

            fetchSkuData(idList)
        }

    }

    private fun fetchSkuData(idList: ArrayList<String>) = with(binding) {
        apiViewModel.getInAppSKU(idList).observe(viewLifecycleOwner){
            inAppSkuDetailsList.clear()
            inAppSkuDetailsList.addAll(it)

            arrangeData(it)
            val sortedList = inAppSkuDetailsList.sortedWith { list1, list2 -> ((list1.price_amount_micros?:0) / 1000 - (list2.price_amount_micros?:0) / 1000).toInt() }
            purchaseNewAdapter.setData(sortedList,original1YearData,originalLifetimeData)
        }
    }

    private fun arrangeData(details: List<InAppSkuDetails>) = with(binding) {
        if (isYearPlanAssignFromAdmin){
            removePlan(BillingRepository.AbacusSku.PRODUCT_ID_Week)
            removePlan(BillingRepository.AbacusSku.PRODUCT_ID_1Month)
            removePlan(BillingRepository.AbacusSku.PRODUCT_ID_1Year_Offer)
            removePlan(BillingRepository.AbacusSku.PRODUCT_ID_All)
            btnSubmit.hide()
        }else{
            details.find { it.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_1Year) && it.isPurchase }.also {
                if (it != null){
                    if (it.sku == BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1){
                        removePlan(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer)
                    }else{
                        removePlan(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1)
                    }
                    removePlan(BillingRepository.AbacusSku.PRODUCT_ID_Week)
                    removePlan(BillingRepository.AbacusSku.PRODUCT_ID_1Month)
                }else{
                    if (discountPer > 0){
                        original1YearData = details.find { it.sku == BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1 }
                        removePlan(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1)
                    }else{
                        removePlan(BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer)
                    }
                }
            }
            details.find { it.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_All) && it.isPurchase }.also {
                if (it != null){
                    if (it.sku == BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime){
                        removePlan(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_offer)
                    }else{
                        removePlan(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime)
                    }
                    removePlan(BillingRepository.AbacusSku.PRODUCT_ID_Week)
                    removePlan(BillingRepository.AbacusSku.PRODUCT_ID_1Month)
                    removePlan(BillingRepository.AbacusSku.PRODUCT_ID_1Year)
                }else{
                    if (discountPerLifetime > 0){
                        originalLifetimeData = details.find { it.sku == BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime }
                        removePlan(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime)
                    }else{
                        removePlan(BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_offer)
                    }
                }
            }
            details.map {
                if (it.sku.contains(BillingRepository.AbacusSku.PRODUCT_ID_1Month) && it.isPurchase){
                    removePlan(BillingRepository.AbacusSku.PRODUCT_ID_Week)
                }
            }
        }
    }

    private fun removePlan(planId : String) {
        inAppSkuDetailsList.find { it.sku.contains(planId) }.also {
            if (it != null){
                inAppSkuDetailsList.remove(it)
            }
        }
    }

}