package com.jigar.me.ui.view.dashboard.fragments.home_new

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.android.billingclient.api.BillingClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.model.data.GooglePurchasedPlanRequest
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.databinding.FragmentHomeNewBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.inapp.BillingRepository
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.SelectAvatarProfileDialog
import com.jigar.me.ui.view.confirm_alerts.dialogs.FreeTrialLeftDialog
import com.jigar.me.ui.view.confirm_alerts.dialogs.FreeTrialLeftDialog.DialogFreeTrialInterface
import com.jigar.me.ui.view.confirm_alerts.dialogs.SelectThemeDialog
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.Resource
import com.jigar.me.utils.checkPermissions
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.openYoutube
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeNewFragment : BaseFragment(), SelectAvatarProfileDialog.AvatarProfileDialogInterface{
    private lateinit var binding: FragmentHomeNewBinding
    private var root : View? = null
    private var mNavController: NavController? = null

    private val studentViewModel by viewModels<StudentViewModel>()
    private val appViewModel by viewModels<AppViewModel>()
    private var purchasedListReq : ArrayList<GooglePurchasedPlanRequest> = arrayListOf()
    private lateinit var homeMenuNewAdapter: HomeMenuNewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        if (root == null){
            binding = FragmentHomeNewBinding.inflate(inflater, container, false)
            root = binding.root
            setNavigationGraph()
            initViews()
            initListener()
            setPurchaseData()
        }
        avatarProfileCloseDialog()
        return root!!
    }
    private fun setNavigationGraph() {
        mNavController = requireActivity().findNavController(R.id.nav_host_fragment)
    }
    private fun initViews() = with(binding){
        val menuListStr = prefManager.getCustomParam(AppConstants.RemoteConfig.displayMenuList,"")
        val displayMenuList :List<String> = if (menuListStr.isNotEmpty() && menuListStr.length > 5){
            val type = object : TypeToken<ArrayList<String>>() {}.type
            Gson().fromJson(prefManager.getCustomParam(AppConstants.RemoteConfig.displayMenuList,""),type)
        }else{
            arrayListOf(
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
        appViewModel.getLevel(displayMenuList).observe(viewLifecycleOwner){
            if (it.isNotNullOrEmpty()){
                homeMenuNewAdapter = HomeMenuNewAdapter(it,prefManager,0){ position, data->
                    moveToClick(data)
                }
                recyclerviewMenu.adapter = homeMenuNewAdapter
            }
        }
    }

    private fun setPurchaseData() {
        lifecycleScope.launch{
            // TODO its for temp
//            val purchasedList = appViewModel.getInAppSKUPurchased()
//            if (purchasedList.isNotNullOrEmpty()){
//                createPurchasedPlanRequest(purchasedList)
//            }else{
//                checkFreeTrial()
//            }
        }
    }

    private fun initListener() {
        with(binding){
            cardProfileImage.onClick {
                if (BuildConfig.DEBUG){
//                    mNavController?.navigate(R.id.toHomeFragment)
//                    openReviewOfferPopup()
                }else{
                    txtMyAccount.performClick()
                }
            }
            txtWelcomeTitle.onClick { txtMyAccount.performClick() }
            txtWelcomeMsg.onClick { txtMyAccount.performClick() }
            txtMyAccount.onClick { mNavController?.navigate(R.id.action_homeFragment_to_myProfileFragment) }
            cardMyAccountTop.onClick { txtMyAccount.performClick() }
            cardSettingTop.onClick {
                goToSetting()
            }
            cardSubscribe.onClick { goToInAppPurchase() }
            cardYoutube.onClick {
                requireContext().openYoutube()
            }
            cardEditImage.onClick { txtMyAccount.performClick() }

        }
    }

    private fun initObserver() {
        studentViewModel.handleExistingPurchaseResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                    {
                        prefManager.setCustomParam(AppConstants.APIStatus.PURCHASE_ERROR_CODE,"")
                        setCurrentSubscription(purchasedListReq)
                        checkFreeTrial()
                    }else{
                        onFailure(it.value.error?.message)
                    }

                }
                is Resource.Failure -> {
                    if (it.errorType == AppConstants.APIStatus.ERROR_CODE_OTHER_STUDENT_IS_ASSOCIATED_WITH_THIS_ORDER){
                        prefManager.setCustomParam(AppConstants.APIStatus.PURCHASE_ERROR_CODE,AppConstants.APIStatus.ERROR_CODE_OTHER_STUDENT_IS_ASSOCIATED_WITH_THIS_ORDER)
                        errorPurchaseDialog(getString(R.string.your_device_purchases_is_associated_with_other_login),
                            getString(R.string.want_to_move_purchase_with_this_login),
                            getString(R.string.yes_i_want_to_move),
                            getString(R.string.no_move_later))
                    }else if (it.errorType == AppConstants.APIStatus.ERROR_CODE_THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER){
                        prefManager.setCustomParam(AppConstants.APIStatus.PURCHASE_ERROR_CODE,AppConstants.APIStatus.ERROR_CODE_THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER)
                        errorPurchaseDialog(getString(R.string.your_login_is_associated_with_other_purchases),
                            getString(R.string.want_to_move_purchase_with_this_login),
                            getString(R.string.yes_i_want_to_move),
                            getString(R.string.no_move_later))
                    }else{
                        (activity as MainDashboardActivity).isPurchaseDataChecked = false
                        onFailure(it.errorBody)
                    }
                }
                else -> {}
            }
        }

        studentViewModel.changePlanResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                    {
                        prefManager.setCustomParam(AppConstants.APIStatus.PURCHASE_ERROR_CODE,"")
                        setCurrentSubscription(purchasedListReq)
                        checkFreeTrial()
                    }else{
                        onFailure(it.value.error?.message)
                    }
                }
                is Resource.Failure -> {
                    hideLoading()
                    onFailure(it.errorBody)
                }
                else -> {}
            }
        }
    }

    private fun checkFreeTrial() {
        prefManager.setUserInFreeTrial(false)
        lifecycleScope.launch {
            val purchasedSKU = appViewModel.getInAppSKUPurchased()
            val isPurchased = CommonUtils.checkPurchaseForExerciseExamCCM(prefManager,purchasedSKU)
            if (isPurchased){
                themePopup()
            }else{
                val free_trial_remaining_days =  prefManager.getCustomParamInt(Constants.free_trial_remaining_days,0)
                val free_trial_remaining_days_last_checked = prefManager.getCustomParamInt(Constants.free_trial_remaining_days_last_checked,-1)
                if (free_trial_remaining_days > 0){
                    prefManager.setUserInFreeTrial(true)
                }else{
                    prefManager.setUserInFreeTrial(false)
                }
                if (free_trial_remaining_days_last_checked != free_trial_remaining_days || free_trial_remaining_days == 0){
                    FreeTrialLeftDialog.showPopup(requireActivity(),prefManager,object : DialogFreeTrialInterface{
                        override fun onCloseClick() {
                            prefManager.setCustomParamInt(Constants.free_trial_remaining_days_last_checked,free_trial_remaining_days)
                        }
                        override fun onSubmitYesClick() {
                            prefManager.setCustomParamInt(Constants.free_trial_remaining_days_last_checked, free_trial_remaining_days)
                            if (free_trial_remaining_days >= 7){
                                mNavController?.navigate(R.id.toVideoPreviewFragment)
                            }else{
                                goToInAppPurchase()
                            }
                        }
                    })
                }else{
                    themePopup()
                }
            }
        }

    }


    private fun errorPurchaseDialog(title: String, msg: String, btnYes: String, btnNo: String) {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),title,msg,
            btnYes, btnNo, icon = R.drawable.ic_alert_not_purchased,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    if (purchasedListReq.isNotNullOrEmpty()){
                        studentViewModel.changePlan(PurchasedPlanCheckRequest(purchasedListReq))
                    }
                }
                override fun onConfirmationNoClick(bundle: Bundle?) = Unit
            })
    }

    private fun themePopup() {
        if (isAdded){
            if (!prefManager.getCustomParamBoolean(AppConstants.Settings.isSetTheam, false)) {
                SelectThemeDialog.showPopup(requireActivity(),prefManager,object : SelectThemeDialog.DialogInterface {
                    override fun themeCloseDialogClick() {
                        prefManager.setCustomParamBoolean(AppConstants.Settings.isSetTheam, true)
                        checkNotificationPermission()
                    }
                })
            } else {
                checkNotificationPermission()
            }
        }
    }


    private fun checkNotificationPermission() {
        if (isAdded){
            requireActivity().checkPermissions(Constants.NOTIFICATION_PERMISSION, requestMultiplePermissions)
        }
    }

    // permission result
    private var requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.filter { !it.value }.also {
                if (it.isNotEmpty()) {
                    notificationPermissionPopup()
                }
            }
        }

    /**
     * Activity Result For Resume Result
     */
    private var resumeActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        }

    private fun notificationPermissionPopup() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),
            getString(R.string.permission_alert),
            getString(R.string.notification_permission_msg),
            getString(R.string.okay),
            getString(R.string.give_later),
            icon = R.drawable.ic_alert,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener {
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    resumeActivityResultLauncher.launch(intent)
                }

                override fun onConfirmationNoClick(bundle: Bundle?) {
//                    menuTour()
                }
            })
    }

    override fun avatarProfileCloseDialog() {
        binding.txtWelcomeTitle.text = CommonUtils.getCurrentTimeMessage(requireContext())
        val id = prefManager.getCustomParamInt(Constants.avatarId,1)
        val avatarList = DataProvider.getAvatarList()
        avatarList.find { it.id == id }?.also {
            binding.imgUserProfile.setImageResource(it.image)
        }
    }
    private fun moveToClick(data: Level) {
        when (data.name) {
            AppConstants.HomeClicks.Menu_Practice_Abacus -> {
                val action = HomeNewFragmentDirections.toCategoryFragmentNew(data.id)
                mNavController?.navigate(action)
            }
            AppConstants.HomeClicks.Menu_Abacus_Free_Mode -> {
                mNavController?.navigate(R.id.toAbacusFreeModeFragment)
            }
            AppConstants.HomeClicks.Menu_Math_Game -> {
                mNavController?.navigate(R.id.toMathGameZoneFragment)
            }
            AppConstants.HomeClicks.Menu_Settings -> {
                goToSetting()
            }

            AppConstants.HomeClicks.Menu_My_Account -> {
                mNavController?.navigate(R.id.action_homeFragment_to_myProfileFragment)
            }
            AppConstants.HomeClicks.Menu_Abacus_Exercise -> {
                mNavController?.navigate(R.id.action_homeFragment_to_exerciseHomeFragment)
            }
            AppConstants.HomeClicks.Menu_Exam -> {
                mNavController?.navigate(R.id.action_homeFragment_to_examHomeFragment)
            }
            AppConstants.HomeClicks.Menu_CCM -> {
                mNavController?.navigate(R.id.action_homeFragment_to_customChallengeHomeFragment)
            }
            AppConstants.HomeClicks.Menu_Purchase_Store -> {
                goToInAppPurchase()
            }
            AppConstants.HomeClicks.Menu_Video_Tutorial -> {
                if (prefManager.getCustomParam(AppConstants.RemoteConfig.videoList,"").isEmpty()){
                    requireContext().openYoutube()
                }else{
                    mNavController?.navigate(R.id.action_homeFragment_to_youtubeVideoFragment)
                }
            }
        }
    }

    private fun createPurchasedPlanRequest(purchasedList: List<InAppSkuDetails>) {
        purchasedListReq.clear()
        purchasedList.map {
            with(it){
                val google_plan_id = sku
                val google_order_id = orderId
                 val is_all_feature = (sku == BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime || sku == BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_old)
                val start_date = purchaseTime

                var is_lifetime_plan = false
                var end_date = 0L
                var no_of_renewals = 0
                val purchase_price : Double = (price_amount_micros?:0L).toDouble() / 1000000
                val purchase_currency = price_currency_code
                if (type == BillingClient.ProductType.INAPP){
                    is_lifetime_plan = true
                }else{
                    if (orderId.contains("_")){
                        val orderIdSplit = orderId.split("_")
                        try {
                            no_of_renewals = orderIdSplit[1].toInt()
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                        }
                    }
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = purchaseTime?:0L
                    val calendarEnd = Calendar.getInstance()
                    calendarEnd.timeInMillis = purchaseTime?:0L

                    val dateDiff = System.currentTimeMillis() - calendar.timeInMillis
                    val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)

                    if (billingPeriod.equals("p1w",true)){
                        var weeks = day/7
                        weeks += 1
                        calendarEnd.add(Calendar.WEEK_OF_MONTH,weeks.toInt())
                    }else if (billingPeriod.equals("p1y",true)){
                        var year = day/365
                        year += 1
                        calendarEnd.add(Calendar.YEAR,year.toInt())
                    }else{
                        val months = if (billingPeriod.equals("p1m",true)){
                            var months = day/30
                            months += 1
                            months
                        }else if (billingPeriod.equals("p3m",true)){
                            var months = day/90
                            months += 3
                            months
                        }else{ // if (billingPeriod.equals("p6m",true))
                            var months = day/180
                            months += 6
                            months
                        }

                        calendarEnd.add(Calendar.MONTH,months.toInt())
                    }

                    end_date = calendarEnd.timeInMillis
                }
                purchasedListReq.add(GooglePurchasedPlanRequest(google_plan_id,google_order_id, is_lifetime_plan, is_all_feature, start_date, end_date,purchase_price,purchase_currency, no_of_renewals))
            }
        }
        studentViewModel.handleExistingPurchase(PurchasedPlanCheckRequest(purchasedListReq))
    }
}