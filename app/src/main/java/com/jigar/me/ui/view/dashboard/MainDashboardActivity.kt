package com.jigar.me.ui.view.dashboard

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.gson.Gson
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.data.model.data.DiscountData
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.databinding.ActivityMainDashboardBinding
import com.jigar.me.ui.view.base.BaseActivity
import com.jigar.me.ui.view.confirm_alerts.dialogs.OfferDialog
import com.jigar.me.ui.view.dashboard.fragments.abacus.half.HalfAbacusFragment
import com.jigar.me.ui.view.dashboard.fragments.exam.doexam.ExamCommonFragment
import com.jigar.me.ui.view.dashboard.fragments.exercise.ExerciseHomeFragment
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.ui.viewmodel.InAppViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.getBottomNavBarHeight
import com.jigar.me.utils.extensions.hasNotch
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainDashboardActivity : BaseActivity() {
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment
    private var selectedFragment: Int = -1
    private val inAppViewModel by viewModels<InAppViewModel>()
    private val appViewModel by viewModels<AppViewModel>()
    private lateinit var binding: ActivityMainDashboardBinding
    var isPurchaseDataChecked = false
    var allSetList: ArrayList<Set> = arrayListOf()
    private var discountData : DiscountData? = null
    private var loginData: LoginData? = null
    companion object {
        @JvmStatic
        fun getInstance(context: Context?) {
            Intent(context, MainDashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolBar()
        initViews()
        initListener()
        initObserver()
        playBackgroundMusic()
//        FetchAbacusDataWorkManager.fetchAbacusDetails()
    }

    private fun initObserver() {
        loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        inAppViewModel.inAppInit()

        if (BuildConfig.DEBUG) {
////            OneSignal.setEmail("jigar@gmail.com")
//            binding.viewBG.show()
        }

        if (!loginData?.email.equals("abacus@yopmail.com")){
            val appOpenCount = prefManager.getCustomParamInt(AppConstants.Settings.appOpenCountForOffer, 0)
            if (prefManager.getCustomParam(AppConstants.RemoteConfig.discountData,"").isNotEmpty()){
                discountData = Gson().fromJson(prefManager.getCustomParam(AppConstants.RemoteConfig.discountData,""), DiscountData::class.java)
                if(appOpenCount == Constants.homePageShowOffer && !discountData?.image.isNullOrEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(5000)
                        val purchasedSKU = appViewModel.getInAppSKUPurchased()
                        val isNotPurchase = CommonUtils.checkPurchaseForAllLevel(purchasedSKU)
                        if (isNotPurchase){
                            OfferDialog.showPopup(this@MainDashboardActivity,discountData,object : OfferDialog.DialogOfferInterface {
                                override fun onSubmitYesClick() {
                                    navController.navigate(R.id.purchaseFragment)
                                }
                            })
                        }
                    }
                }
            }
            if (appOpenCount > Constants.homePageShowOffer){
                prefManager.setCustomParamInt(AppConstants.Settings.appOpenCountForOffer, 0)
            }else{
                prefManager.setCustomParamInt(AppConstants.Settings.appOpenCountForOffer, (appOpenCount+1))
            }
        }
    }

    private fun setNavigationGraph() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            selectedFragment = destination.id
            showToolbarTitle(destination.id)
        }
    }

    private fun initToolBar() {
    }
    private fun initViews() {
        setNavigationGraph()
        onMainActivityBack()
//        hasNotch {
//            Log.e("jigarLogs", "Re-check hasNotch = $it")
//        }

        getBottomNavBarHeight { topInset,bottomInset ->
//            Log.e("jigarLogs", "Bottom nav topInset = $topInset px")
//            Log.e("jigarLogs", "Bottom nav bottomInset = $bottomInset px")
        }

    }

    private fun initListener() {

    }

    private fun showToolbarTitle(id: Int) {
    }

    private fun onMainActivityBack() {
        onBackPressedDispatcher.addCallback(
            this, // lifecycle owner
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when (selectedFragment) {
                        R.id.homeNewFragment -> {
                            finish()
                        }
                        else -> {
                            onBackOfHalfAbacusFragment()
                        }
                    }
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackOfHalfAbacusFragment()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onBackOfHalfAbacusFragment() {
        val fragment = navHostFragment.childFragmentManager.fragments[0]
        when (fragment) {
            is HalfAbacusFragment -> {
                fragment.onBackClick()
            }
            is ExerciseHomeFragment -> {
                fragment.exerciseLeaveAlert()
            }
            is ExamCommonFragment -> {
                fragment.examLeaveAlert()
            }
            else -> {
                navigationUp()
            }
        }
    }

    private fun navigationUp() {
        navController.navigateUp()
    }

    fun fetchSetData() {
        CoroutineScope(Dispatchers.Main).launch {
            val list = appViewModel.getAllSet()
            allSetList.clear()
            allSetList.addAll(list)
        }
    }
}