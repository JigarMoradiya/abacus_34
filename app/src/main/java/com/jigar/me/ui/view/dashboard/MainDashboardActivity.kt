package com.jigar.me.ui.view.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.databinding.ActivityMainDashboardBinding
import com.jigar.me.ui.view.base.BaseActivity
import com.jigar.me.ui.view.dashboard.fragments.abacus.half.HalfAbacusFragment
import com.jigar.me.ui.view.dashboard.fragments.exercise.ExerciseHomeFragment
import com.jigar.me.ui.viewmodel.InAppViewModel
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainDashboardActivity : BaseActivity() {
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment
    private var selectedFragment: Int = -1
    private val inAppViewModel by viewModels<InAppViewModel>()
//    private val appViewModel by viewModels<AppViewModel>()
    private lateinit var binding: ActivityMainDashboardBinding
    var isPurchaseDataChecked = false
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
    }

    private fun initObserver() {
        inAppViewModel.inAppInit()

        if (BuildConfig.DEBUG) {
////            OneSignal.setEmail("jigar@gmail.com")
//            binding.viewBG.show()
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
        setSupportActionBar(binding.toolbar)
    }

    private fun initViews() {
        setNavigationGraph()
        onMainActivityBack()
    }

    private fun initListener() {

    }

    private fun showToolbarTitle(id: Int) {
        when (id) {
            R.id.homeFragment -> {
                binding.toolbar.hide()
            }
            else -> {
                binding.toolbar.hide()
            }
        }
    }

    private fun showOnlyBackArrow() { //  Hide bottom navigation bar, Show toolbar back icon
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    private fun onMainActivityBack() {
        onBackPressedDispatcher.addCallback(
            this, // lifecycle owner
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when (selectedFragment) {
                        R.id.homeFragment -> {
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
            else -> {
                navigationUp()
            }
        }
    }

    private fun navigationUp() {
        navController.navigateUp()
    }
}