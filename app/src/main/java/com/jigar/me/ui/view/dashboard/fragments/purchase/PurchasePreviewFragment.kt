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
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class PurchasePreviewFragment : BaseFragment() {
    private lateinit var binding: FragmentPurchasePreviewBinding
    private lateinit var mNavController: NavController

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

        val assetFileName = "video_free_mode.mp4"
//        val assetFileName = "video_free_mode_number.mp4"

        val constraintSet = ConstraintSet()
        constraintSet.clone(conVideo)
        if (assetFileName == "video_free_mode.mp4") {
            constraintSet.setDimensionRatio(videoView.id, "1920:882")
        }else{
            constraintSet.setDimensionRatio(videoView.id, "1920:954")
        }

        constraintSet.applyTo(conVideo)

        val file = File(requireContext().cacheDir, assetFileName)
        file.delete()
        if (!file.exists()) {
            requireContext().assets.open(assetFileName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

        val videoUri = Uri.fromFile(file)

        // Set up MediaController for playback controls
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
//        videoView.setMediaController(mediaController)
        videoView.setVideoURI(videoUri)
        videoView.requestFocus()

        // Loop video
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
        }

        // Completion listener (will not be called due to looping)
        videoView.setOnCompletionListener {
            // This block will not trigger because of looping
            // But you can log or handle something if needed
            println("Video playback completed")
        }
        videoView.start()
    }

    private fun initListener() {
        with(binding){
            cardBack.onClick { mNavController.navigateUp() }
        }
    }



}