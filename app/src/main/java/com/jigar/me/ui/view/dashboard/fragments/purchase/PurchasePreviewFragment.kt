package com.jigar.me.ui.view.dashboard.fragments.purchase

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
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.databinding.FragmentPurchasePreviewBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month1
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.OldPurchasesBottomSheet
import com.jigar.me.ui.view.dashboard.fragments.purchase.video_play.PurchasePreviewAdapter
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
import kotlin.getValue

@AndroidEntryPoint
class PurchasePreviewFragment : BaseFragment(){
    private val inAppViewModel by activityViewModels<InAppViewModel>()
    private lateinit var binding: FragmentPurchasePreviewBinding
    private lateinit var mNavController: NavController
    private lateinit var purchasePreviewAdapter: PurchasePreviewAdapter
    private lateinit var purchasePreviewInfoAdapter: PurchasePreviewInfoAdapter
    private val apiViewModel by viewModels<AppViewModel>()

    private var isMonthlyPlanSubscribe = false
    private var isYearlyPlanSubscribe = false
    private var isYearlyPlanOfferSubscribe = false

    private val videoFiles = listOf(
        "video_free_mode.mp4",
        "video_free_mode_number.mp4"
    )

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
    private var original1YearData : InAppSkuDetails? = null
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
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
                val selectedPlan = purchasePreviewAdapter.getSelectedData()
//                MyApplication.logEvent("Purchase_"+selectedPlan.sku, null)
                inAppViewModel.makePurchase(requireActivity(), selectedPlan)
            }
            btnOldSubscription.onClick {
                OldPurchasesBottomSheet.showPopup(requireActivity(),oldPurchasedSkuList)
            }
        }
    }

    private fun initViews() = with(binding){
//        inAppViewModel.inAppInit()
//        viewPager.adapter = VideoPagerAdapter(this@PurchasePreviewFragment, videoFiles)
        spaceNotch.layoutParams.width = prefManager.getCustomParamInt(AppConstants.NOTCH_HEIGHT,0)

        discountPer = prefManager.getCustomParamInt(AppConstants.RemoteConfig.discountPer,0)
        if (prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA,"").isNotEmpty()) {
            planListAssignFromAdmin = Gson().fromJson(
                prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA, ""),
                object : TypeToken<List<PlanAssignFromAdminData>>() {}.type
            )
        }
        purchasePreviewAdapter = PurchasePreviewAdapter(arrayListOf(),planListAssignFromAdmin,discountPer,recyclerview)
        recyclerview.adapter = purchasePreviewAdapter

        purchasePreviewInfoAdapter = PurchasePreviewInfoAdapter(list)
        recyclerviewInfo.adapter = purchasePreviewInfoAdapter

        setSubscription()
    }

    private fun setSubscription() = with(binding) {
        val idList: ArrayList<String> = arrayListOf()
        idList.add(PRODUCT_ID_Subscription_Month1)
        idList.add(PRODUCT_ID_Subscription_Year1)
        idList.add(PRODUCT_ID_Subscription_Year1_Offer)
        idList.add(PRODUCT_ID_All_lifetime)

        lifecycleScope.launch {
            oldPurchasedSkuList = apiViewModel.getInAppSKUPurchasedLiveExclude(idList)
            if (oldPurchasedSkuList.isNotNullOrEmpty()){
                btnOldSubscription.show()
            }else{
                btnOldSubscription.hide()
            }
        }

        apiViewModel.getInAppSKU(idList).observe(viewLifecycleOwner){
            isMonthlyPlanSubscribe = false
            isYearlyPlanSubscribe = false
            isYearlyPlanOfferSubscribe = false
            inAppSkuDetailsList.clear()
            inAppSkuDetailsList.addAll(it)

            inAppSkuDetailsList.find { it.sku ==  PRODUCT_ID_All_lifetime}.also { lifeTimePlan ->
                if (lifeTimePlan != null){
                    if (lifeTimePlan.isPurchase){
                        inAppSkuDetailsList.find { it.sku ==  PRODUCT_ID_Subscription_Month1}.also {
                            if (it != null){
                                inAppSkuDetailsList.remove(it)
                            }
                        }
                        inAppSkuDetailsList.find { it.sku ==  PRODUCT_ID_Subscription_Year1}.also {
                            if (it != null){
                                inAppSkuDetailsList.remove(it)
                            }
                        }
                        inAppSkuDetailsList.find { it.sku ==  PRODUCT_ID_Subscription_Year1_Offer}.also {
                            if (it != null){
                                inAppSkuDetailsList.remove(it)
                            }
                        }

                        btnSubmit.hide()
                        txtDescription.hide()
                    }else{
                        inAppSkuDetailsList.remove(lifeTimePlan)
                        arrangeData()
                    }
                }else{
                    arrangeData()
                }

                val sortedList = inAppSkuDetailsList.sortedWith { list1, list2 -> list1.sortOrder - list2.sortOrder }
                purchasePreviewAdapter.setData(sortedList,original1YearData)
            }
        }
    }

    private fun arrangeData() = with(binding) {
        inAppSkuDetailsList.mapIndexed { index, mainList ->
            planListAssignFromAdmin.find { it.google_order_id == null && mainList.sku.contains(it.google_plan_id?:"") }.also {
                val isPlanAssignFromAdmin = it != null
                if (isPlanAssignFromAdmin){
                    when (mainList.sku) {
                        PRODUCT_ID_Subscription_Month1 -> {
                            isMonthlyPlanSubscribe = true
                        }
                        PRODUCT_ID_Subscription_Year1 -> {
                            isYearlyPlanSubscribe = true
                        }
                    }
                }
            }

            when (mainList.sku) {
                PRODUCT_ID_Subscription_Month1 -> {
                    if (mainList.isPurchase){
                        isMonthlyPlanSubscribe = true
                    }
                    inAppSkuDetailsList[index].sortOrder = 1
                }
                PRODUCT_ID_Subscription_Year1_Offer -> {
                    if (mainList.isPurchase){
                        isYearlyPlanOfferSubscribe = true
                    }
                    inAppSkuDetailsList[index].sortOrder = 2
                }
                PRODUCT_ID_Subscription_Year1 -> {
                    if (mainList.isPurchase){
                        isYearlyPlanSubscribe = true
                    }
                    inAppSkuDetailsList[index].sortOrder = 2
                }
            }
        }

        if (isYearlyPlanSubscribe || isYearlyPlanOfferSubscribe){
            inAppSkuDetailsList.find { it.sku ==  PRODUCT_ID_Subscription_Month1}.also {
                if (it != null){
                    inAppSkuDetailsList.remove(it)
                }
            }

            if (isYearlyPlanSubscribe){
                inAppSkuDetailsList.find { it.sku ==  PRODUCT_ID_Subscription_Year1_Offer}.also {
                    if (it != null){
                        inAppSkuDetailsList.remove(it)
                    }
                }
            }else{
                inAppSkuDetailsList.find { it.sku ==  PRODUCT_ID_Subscription_Year1}.also {
                    if (it != null){
                        inAppSkuDetailsList.remove(it)
                    }
                }
            }
        }else if (discountPer > 0){
            inAppSkuDetailsList.find { it.sku ==  PRODUCT_ID_Subscription_Year1}.also {
                if (it != null){
                    original1YearData = it
                    inAppSkuDetailsList.remove(it)
                }
            }
        }else{
            inAppSkuDetailsList.find { it.sku ==  PRODUCT_ID_Subscription_Year1_Offer}.also {
                if (it != null){
                    inAppSkuDetailsList.remove(it)
                }
            }
        }

        if (isYearlyPlanSubscribe || isYearlyPlanOfferSubscribe){
            btnSubmit.hide()
            txtDescription.hide()
        }
    }

}