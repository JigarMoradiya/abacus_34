package com.jigar.me.ui.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.jigar.me.R
import com.jigar.me.databinding.ActivityLoginDashboardBinding
import com.jigar.me.ui.view.base.BaseActivity
import com.jigar.me.ui.view.base.inapp.BillingRepository.Companion.LOG_TAG
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.ui.viewmodel.InAppViewModel
import com.jigar.me.utils.extensions.hide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginDashboardActivity : BaseActivity(){
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment
    var selectedFragment: Int = -1
    lateinit var binding: ActivityLoginDashboardBinding
    companion object {
        fun getInstance(context: Context?) {
            Intent(context, LoginDashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(this)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolBar()
        initViews()
        initListener()
        initObserver()

        window.apply {
//            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun initObserver() {
//        lifecycleScope.launch{
//            appViewModel.deleteInAppPurchase()
//            appViewModel.deleteInAppSKU()
//            inAppViewModel.inAppInit()
//        }
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
        setSupportActionBar(binding.toolbar)
    }

    private fun initViews() {
        Log.d(LOG_TAG, "LoginDashboardActivity")
        setNavigationGraph()
        onMainActivityBack()
    }

    private fun initListener() {

    }
    private fun showToolbarTitle(id: Int) {
        when (id) {
            R.id.splashFragment -> {
                binding.toolbar.hide()
                binding.relMain.hide()
                showBackArrow()
            }
            else ->{
                lifecycleScope.launch {
                    delay(100)
                    binding.toolbar.hide()
                    binding.relMain.hide()
                }
            }
        }
    }
    private fun showBackArrow() { //  Hide bottom navigation bar, Show toolbar back icon
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    private fun onMainActivityBack() {
        onBackPressedDispatcher.addCallback(
            this, // lifecycle owner
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    fragmentBackClick()
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                fragmentBackClick()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun fragmentBackClick() {
        when (selectedFragment) {
            R.id.splashFragment,R.id.loginHomeFragment -> {
                finish()
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