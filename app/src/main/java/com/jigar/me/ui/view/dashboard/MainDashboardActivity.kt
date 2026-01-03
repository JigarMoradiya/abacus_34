package com.jigar.me.ui.view.dashboard

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.gson.Gson
import com.jigar.me.R
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.databinding.ActivityMainDashboardBinding
import com.jigar.me.ui.view.base.BaseActivity
import com.jigar.me.ui.view.dashboard.fragments.exam.doexam.ExamCommonFragment
import com.jigar.me.ui.view.dashboard.fragments.exercise.ExerciseHomeFragment
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.jetpack.utils.TextToSpeechManager
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainDashboardActivity : BaseActivity() {
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment
    private var selectedFragment: Int = -1
    private lateinit var binding: ActivityMainDashboardBinding
    var isPurchaseDataChecked = false
    private var loginData: LoginData? = null

    val homeActivityViewModel: HomeActivityViewModel by viewModels() // dont remove this line

    // text to speech common for whole app (for activity)
    @Inject
    lateinit var ttsManager: TextToSpeechManager

    override fun onDestroy() {
        super.onDestroy()
        ttsManager.shutdown()
    }

    override fun onResume() {
        super.onResume()
        homeActivityViewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        homeActivityViewModel.onPause()
    }

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
        initObserver()
        initListener()
        initToolBar()
        initViews()
    }

    private fun initObserver() {
        loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
//        inAppViewModel.inAppInit()
    }

    private fun setNavigationGraph() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            selectedFragment = destination.id
        }
    }

    private fun initToolBar() {
    }
    private fun initViews() {

        // Allow drawing behind system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Allow using short edges in landscape (needed for notch handling)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.viewMain) { view, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val cutout = insets.displayCutout

            val leftInset = cutout?.safeInsetLeft ?: sysBars.left
            val rightInset = sysBars.right

            val topInset = 0 // no top padding in landscape
            val bottomInset = 0 // no bottom padding in landscape
            view.setPadding(leftInset, topInset, rightInset, bottomInset)
            insets
        }
        homeActivityViewModel.fetchAbacusData()

        setNavigationGraph()
        onMainActivityBack()
        prefManager.setCustomParamInt(AppConstants.NOTCH_HEIGHT,0)
    }

    private fun initListener() {
        logDeviceQualifiers(this)
    }

    fun logDeviceQualifiers(context: Context) {
        val config = context.resources.configuration
        val screenWidthDp = config.screenWidthDp
        prefManager.setCustomParamInt(AppConstants.screenWidthDp,screenWidthDp)
    }

    private fun onMainActivityBack() {
        onBackPressedDispatcher.addCallback(
            this, // lifecycle owner
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when (selectedFragment) {
                        R.id.homeFragmentNew -> {
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
}