package com.jigar.me.ui.view.dashboard.fragments.purchase

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.jigar.me.R
import com.jigar.me.databinding.FragmentPurchasePreviewBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.dashboard.fragments.purchase.video_play.VideoPagerAdapter
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class PurchasePreviewFragment : BaseFragment() {
    private lateinit var binding: FragmentPurchasePreviewBinding
    private lateinit var mNavController: NavController
    private val videoFiles = listOf(
        "video_free_mode.mp4",
        "video_free_mode_number.mp4"
    )

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

    private fun initViews() = with(binding){
        binding.spaceNotch.layoutParams.width = prefManager.getCustomParamInt(AppConstants.NOTCH_HEIGHT,0)
        binding.viewPager.adapter = VideoPagerAdapter(this@PurchasePreviewFragment, videoFiles)
    }

    private fun initListener() {
        with(binding){
            cardBack.onClick { mNavController.navigateUp() }
        }
    }



}