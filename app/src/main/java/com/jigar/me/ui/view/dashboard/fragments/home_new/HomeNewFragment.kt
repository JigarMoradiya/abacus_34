package com.jigar.me.ui.view.dashboard.fragments.home_new

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.BillingClient
import com.eftimoff.viewpagertransformers.DepthPageTransformer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.BuildConfig
import com.jigar.me.MyApplication
import com.jigar.me.R
import com.jigar.me.data.local.data.*
import com.jigar.me.data.model.data.GooglePurchasedPlanRequest
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.databinding.FragmentHomeNewBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.inapp.BillingRepository
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.OtherApplicationBottomSheet
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.SelectAvatarProfileDialog
import com.jigar.me.ui.view.confirm_alerts.dialogs.SelectThemeDialog
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.ui.view.dashboard.fragments.home.BannerPagerAdapter
import com.jigar.me.ui.view.dashboard.fragments.home.CurrentPlanPagerAdapter
import com.jigar.me.ui.view.other.ContactUsActivity
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.ui.viewmodel.SubscriptionsViewModel
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.Resource
import com.jigar.me.utils.checkPermissions
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.openURL
import com.jigar.me.utils.extensions.openYoutube
import com.jigar.me.utils.extensions.shareIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.samlss.lighter.IntroProvider
import me.samlss.lighter.Lighter
import me.samlss.lighter.parameter.Direction
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeNewFragment : BaseFragment(), BannerPagerAdapter.OnItemClickListener,
    SelectAvatarProfileDialog.AvatarProfileDialogInterface,
    CurrentPlanPagerAdapter.OnItemClickListener {
    private lateinit var binding: FragmentHomeNewBinding
    private var root : View? = null
    private var mNavController: NavController? = null

    private val studentViewModel by viewModels<StudentViewModel>()
    private val appViewModel by viewModels<AppViewModel>()
    private val subscriptionsViewModel by viewModels<SubscriptionsViewModel>()
    private var purchasedListReq : ArrayList<GooglePurchasedPlanRequest> = arrayListOf()

    private lateinit var bannerPagerAdapter: BannerPagerAdapter
    private lateinit var homeMenuNewAdapter: HomeMenuNewAdapter
    //handler for run auto scroll thread
    private var handler : Handler? = null
    private var runnable: Runnable? = null
    private var loginData: LoginData? = null

    private var currentPage = 0
    private var timer: Timer? = null
    private val DELAY_MS: Long = 5000 //delay in milliseconds before task is to be executed
    private val PERIOD_MS: Long = 5000 // time in milliseconds between successive task executions.

    private var lighter : Lighter? = null

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
        }
        setPurchaseData()
        return root!!
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    private fun initViews() = with(binding){
        getTrackData()
        setViewPager()
        linearMenu.post {
            appViewModel.getLevel().observe(viewLifecycleOwner){
                if (it.isNotNullOrEmpty()){
                    val column = (it.size / 2)
                    val height = linearMenu.height / 2
                    val width = linearMenu.width / column
                    val dimension = if (width > height){
                        height
                    }else{
                        width
                    }
                    homeMenuNewAdapter = HomeMenuNewAdapter(it,prefManager,dimension){ position, data->
                        moveToClick(data)
                    }
                    recyclerviewMenu.layoutManager = GridLayoutManager(requireContext(),column)
                    recyclerviewMenu.adapter = homeMenuNewAdapter
                }
            }
            themePopup()
        }
    }

    private fun setPurchaseData() {
        appViewModel.getInAppSKUPurchasedLive().observe(viewLifecycleOwner){
//            Log.e("jigarLogs","getInAppSKUPurchasedLive = "+(activity as MainDashboardActivity).isPurchaseDataChecked)
            if (!(activity as MainDashboardActivity).isPurchaseDataChecked){
                createPurchasedPlanRequest(it)
            }
        }
    }

    private fun initListener() {
        with(binding){
            cardProfileImage.onClick { cardEditImage.performClick() }
            txtWelcomeTitle.onClick { cardEditImage.performClick() }
            txtWelcomeMsg.onClick { cardEditImage.performClick() }
            txtMyAccount.onClick { cardEditImage.performClick() }
            cardEditImage.onClick { moveToClick(AppConstants.HomeClicks.Menu_My_Profile) }
            cardSettingTop.onClick { moveToClick(AppConstants.HomeClicks.Menu_Setting) }
            cardSubscribe.onClick { moveToClick(AppConstants.HomeClicks.Menu_Subscribe) }
            cardAboutUs.onClick { moveToClick(AppConstants.HomeClicks.Menu_AboutUs) }
            txtOtherApps.onClick { OtherApplicationBottomSheet.showPopup(requireActivity()) }
            txtWelcomeTitle.onClick {
                if (BuildConfig.DEBUG) {
                    showTour()
                }
            }
        }
    }

    private fun initObserver() {
        subscriptionsViewModel.accountDetailResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        setCurrentSubscriptionList(it.value.data)
                }
                is Resource.Failure -> {
                }
            }
        }
        studentViewModel.handleExistingPurchaseResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                    {
                        prefManager.setCustomParam(AppConstants.APIStatus.PURCHASE_ERROR_CODE,"")
                        setCurrentSubscription(purchasedListReq)
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
                    }else{
                        onFailure(it.value.error?.message)
                    }
                }
                is Resource.Failure -> {
                    hideLoading()
                    onFailure(it.errorBody)
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
    private fun setCurrentSubscriptionList(data: JsonObject?) {
//        val response = Gson().fromJson(data, UserData::class.java)
//        setCurrentSubscription(response)
//        prefManager.setLoginData(data.toString())
//        avatarProfileCloseDialog()
//        response.account_detail?.subscribed_plans?.let{
//            currentPlanPagerAdapter = CurrentPlanPagerAdapter(it,this)
//            binding.viewPagerCurrentPlan.adapter = currentPlanPagerAdapter
//            binding.viewPagerCurrentPlan.setPageTransformer( true , DepthPageTransformer() )
//        }
    }
    override fun onPurchaseRenewClick() {
        goToInAppPurchase()
    }
    override fun onPurchaseItemClick() {
        goToInAppPurchase()
    }
    private fun menuTour() {
//        lifecycleScope.launch {
//            delay(1000)
//            if (!prefManager.getCustomParamBoolean(AppConstants.Settings.isHomeTourWatch, false)) {
//                showTour()
//            }else if (prefManager.getCustomParamInt(AppConstants.Settings.appOpenCount, 0) == Constants.homePageShowIntroMaxAppOpen) {
//                prefManager.setCustomParamInt(AppConstants.Settings.appOpenCount, 0)
//                val introType = DataProvider.getHomeMenuRandomIntro(prefManager)
//                lighter = Lighter.with(binding.root)
//                var view : View? = null
//                var directions : Int? = null
//                var layoutId : Int? = null
//                var type : String = "rect"
//                when (introType) {
//                    HomeMenuIntroType.freeMode -> {
//                        view = (binding.recyclerviewMenu.findViewHolderForAdapterPosition(0) as HomeMenuNewAdapter.FormViewHolder).binding.conMain
//                        directions = Direction.RIGHT
//                        layoutId = R.layout.layout_tip_free_mode
//                    }
//                    HomeMenuIntroType.videoTutorial -> {
//                        view = (binding.recyclerviewMenu.findViewHolderForAdapterPosition(11) as HomeMenuNewAdapter.FormViewHolder).binding.conMain
//                        directions = Direction.LEFT
//                        layoutId = R.layout.layout_tip_video_tutorial
//                    }
//                    HomeMenuIntroType.exercise -> {
//                        view = (binding.recyclerviewMenu.findViewHolderForAdapterPosition(6) as HomeMenuNewAdapter.FormViewHolder).binding.conMain
//                        directions = Direction.TOP
//                        layoutId = R.layout.layout_tip_exercise
//                    }
//                    HomeMenuIntroType.exam -> {
//                        view = (binding.recyclerviewMenu.findViewHolderForAdapterPosition(7) as HomeMenuNewAdapter.FormViewHolder).binding.conMain
//                        directions = Direction.RIGHT
//                        layoutId = R.layout.layout_tip_exam
//                    }
//                    HomeMenuIntroType.material -> {
//                        view = (binding.recyclerviewMenu.findViewHolderForAdapterPosition(8) as HomeMenuNewAdapter.FormViewHolder).binding.conMain
//                        directions = Direction.TOP
//                        layoutId = R.layout.layout_tip_practice_material
//                    }
//                    HomeMenuIntroType.numberPuzzle -> {
//                        view = (binding.recyclerviewMenu.findViewHolderForAdapterPosition(10) as HomeMenuNewAdapter.FormViewHolder).binding.conMain
//                        directions = Direction.TOP
//                        layoutId = R.layout.layout_tip_number_sequence
//                    }
//                    HomeMenuIntroType.purchase -> {
//                        view = binding.cardSubscribe
//                        directions = Direction.LEFT
//                        layoutId = R.layout.layout_tip_purchase
//                        type = "circle"
//                    }
//                    HomeMenuIntroType.setting -> {
//                        view = binding.cardSettingTop
//                        directions = Direction.LEFT
//                        layoutId = R.layout.layout_tip_setting
//                        type = "circle"
//                    }
//                    HomeMenuIntroType.ccm -> {
//                        view = (binding.recyclerviewMenu.findViewHolderForAdapterPosition(8) as HomeMenuNewAdapter.FormViewHolder).binding.conMain
//                        directions = Direction.TOP
//                        layoutId = R.layout.layout_tip_ccm
//                    }
//                }
//                if (view != null && directions != null && layoutId != null){
//                    IntroProvider.videoTutorialSingleIntro(lighter,view,directions,layoutId,type)
//                }
//            }
//
//            val appOpenCount = prefManager.getCustomParamInt(AppConstants.Settings.appOpenCount, 0)
//            prefManager.setCustomParamInt(AppConstants.Settings.appOpenCount, (appOpenCount+1))
//        }
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
            if (requireActivity().checkPermissions(Constants.NOTIFICATION_PERMISSION, requestMultiplePermissions)){
                menuTour()
            }
        }
    }

    // permission result
    private var requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.filter { !it.value }.also {
                if (it.isNotEmpty()) {
                    notificationPermissionPopup()
                }else{
                    menuTour()
                }
            }
        }

    /**
     * Activity Result For Resume Result
     */
    private var resumeActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            menuTour()
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
                    menuTour()
                }
            })
    }



    private fun showTour() {
        lighter = Lighter.with(binding.root)
//        val freeModeViewHolder = binding.recyclerviewMenu.findViewHolderForAdapterPosition(0)
//        val exerciseViewHolder = binding.recyclerviewMenu.findViewHolderForAdapterPosition(6)
//        val examViewHolder = binding.recyclerviewMenu.findViewHolderForAdapterPosition(7)
//        val ccmViewHolder = binding.recyclerviewMenu.findViewHolderForAdapterPosition(8)
//        val numberPuzzleViewHolder = binding.recyclerviewMenu.findViewHolderForAdapterPosition(10)
//        val videoTutorialViewHolder = binding.recyclerviewMenu.findViewHolderForAdapterPosition(11)
//        if (freeModeViewHolder != null && exerciseViewHolder != null && examViewHolder != null && videoTutorialViewHolder != null && numberPuzzleViewHolder != null && ccmViewHolder != null){
//            IntroProvider.videoTutorialIntro(prefManager,lighter, binding.cardSettingTop,
//                (freeModeViewHolder as HomeMenuNewAdapter.ViewHolder).binding.conMain,
//                (videoTutorialViewHolder as HomeMenuNewAdapter.ViewHolder).binding.conMain,
//                (exerciseViewHolder as HomeMenuNewAdapter.ViewHolder).binding.conMain,
//                (examViewHolder as HomeMenuNewAdapter.ViewHolder).binding.conMain,
//                (numberPuzzleViewHolder as HomeMenuNewAdapter.ViewHolder).binding.conMain,
//                (ccmViewHolder as HomeMenuNewAdapter.ViewHolder).binding.conMain
//            )
//        }
    }
    private fun setViewPager() {
        handler = Handler(Looper.getMainLooper())
        with(binding){
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int,positionOffset: Float,positionOffsetPixels: Int) {}
                override fun onPageSelected(position: Int) {
                    currentPage = position
                }
            })

            bannerPagerAdapter = BannerPagerAdapter(DataProvider.getBannerList(requireContext()),this@HomeNewFragment)
            viewPager.adapter = bannerPagerAdapter
            viewPager.setPageTransformer( true , DepthPageTransformer() )
            indicatorPager.attachToPager(viewPager)
        }
    }

    override fun avatarProfileCloseDialog() {
        loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        if (loginData == null){
            binding.txtWelcomeTitle.text = CommonUtils.getCurrentTimeMessage(requireContext())
        }else{
            binding.txtWelcomeTitle.text = CommonUtils.getCurrentTimeMessage(requireContext()).plus(" "+loginData?.name+"!")
        }

        val id = prefManager.getCustomParamInt(Constants.avatarId,1)
        val avatarList = DataProvider.getAvatarList()
        avatarList.find { it.id == id }?.also {
            binding.imgUserProfile.setImageResource(it.image)
        }
    }
    private fun autoScrollBanner() {
        /*After setting the adapter use the timer */
        runnable?.let { handler?.removeCallbacks(it) }
        runnable = Runnable {
            currentPage++
            if (currentPage == (bannerPagerAdapter.listData.size)) {
                currentPage = 0
            }
            if (currentPage == 0){
                binding.viewPager.setCurrentItem(currentPage, false)
            }else{
                binding.viewPager.setCurrentItem(currentPage, true)
            }
        }

        timer = Timer() // This will create a new Thread
        timer?.schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                runnable?.let { handler?.post(it) }
            }
        }, DELAY_MS, PERIOD_MS)
    }

    override fun onBannerItemClick(data: HomeBanner) {
        // firebase event
        MyApplication.logEvent(data.type, null)
        when (data.type) {
            Constants.banner_rate_us -> {
                requireContext().openURL("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
            }
            Constants.banner_share -> {
                moveToClick(AppConstants.HomeClicks.Menu_Share)
            }
            Constants.banner_bulk_login -> {
                ContactUsActivity.getInstance(requireContext(),AppConstants.extras_Comman.typeBulkLogin)
            }
//            Constants.banner_purchase, Constants.banner_offer -> {
//                moveToClick(AppConstants.HomeClicks.Menu_Purchase)
//            }
        }
    }
    private fun moveToClick(data: Level) {
        when (data.name) {
            AppConstants.HomeClicks.Menu_Practice_Abacus -> {
                val action = HomeNewFragmentDirections.toCategoryFragment(data.id)
                mNavController?.navigate(action)
            }
            AppConstants.HomeClicks.Menu_Abacus_Free_Mode -> {
                mNavController?.navigate(R.id.action_homeFragment_to_fullAbacusFragment)
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
            AppConstants.HomeClicks.Menu_Practice_Material -> {
                mNavController?.navigate(R.id.action_homeFragment_to_materialHomeFragment)
            }
            AppConstants.HomeClicks.Menu_Number_Sequence_Puzzle -> {
                mNavController?.navigate(R.id.action_homeFragment_to_puzzleNumberHomeFragment)
            }
            AppConstants.HomeClicks.Menu_Settings -> {
                goToSetting()
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
    private fun moveToClick(clickType: Int) {
        when (clickType) {
            AppConstants.HomeClicks.Menu_My_Profile -> {
                mNavController?.navigate(R.id.action_homeFragment_to_myProfileFragment)
            }
            AppConstants.HomeClicks.Menu_AboutUs -> {
                mNavController?.navigate(R.id.action_homeFragment_to_aboutFragment)
            }
            AppConstants.HomeClicks.Menu_Share -> {
                requireContext().shareIntent()
            }
        }
    }

    private fun goToPages(clickType: Int,type: String) {
        val action =
            HomeNewFragmentDirections.actionHomeFragmentToPageFragment(
                clickType,
                type
            )
        mNavController?.navigate(action)
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        runnable?.let{ handler?.removeCallbacks(it) }
    }

    override fun onResume() {
        super.onResume()

        avatarProfileCloseDialog()
        if (::bannerPagerAdapter.isInitialized){
            autoScrollBanner()
        }
    }

    private fun getTrackData() {
        FirebaseDatabase.getInstance().reference.child(
            AppConstants.AbacusProgress.Track + "/" + prefManager.getDeviceId()
        ).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                for (snapshotdata in snapshot.children) {
                    val pageId = snapshotdata.key!!
                    val mapMessage = snapshotdata.value as HashMap<*, *>
                    val position = mapMessage[AppConstants.AbacusProgress.Position] as Long
                    try {
                        val pageSum: String = prefManager.getCustomParam(AppConstants.AbacusProgress.PREF_PAGE_SUM, "{}")
                        val objJson = JSONObject(pageSum)
                        objJson.put(pageId, (position + 1))
                        prefManager.setCustomParam(AppConstants.AbacusProgress.PREF_PAGE_SUM,objJson.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                prefManager.setCustomParam(AppConstants.AbacusProgress.TrackFetch, "Y")
            }

            override fun onCancelled(@NonNull error: DatabaseError) {
            }
        })
    }
    private fun createPurchasedPlanRequest(purchasedList: List<InAppSkuDetails>) {
        if (purchasedList.isNotNullOrEmpty()){
            (activity as MainDashboardActivity).isPurchaseDataChecked = true
        }
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