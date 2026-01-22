package com.jigar.me.ui.view.dashboard.fragments.purchase.video_play.previews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.jigar.me.R
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.local.data.VideoTutorial
import com.jigar.me.databinding.FragmentVideoPreviewBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.dashboard.fragments.purchase.video_play.sub.VideoFragment
import com.jigar.me.ui.view.dashboard.fragments.purchase.video_play.sub.VideoPagerAdapter
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPreviewFragment : BaseFragment(){
    private lateinit var binding: FragmentVideoPreviewBinding
    private lateinit var mNavController: NavController
//    private lateinit var purchaseInfoAdapter: PurchaseInfoAdapter

    private var videoFilesList : List<VideoTutorial> = arrayListOf()
    private var currentPosition: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVideoPreviewBinding.inflate(inflater, container, false)
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
        }
    }

    private fun initViews() = with(binding){
        videoFilesList = DataProvider.getVideoPreviewList(requireContext())
        viewPager.adapter = VideoPagerAdapter(this@VideoPreviewFragment, videoFilesList)
        indicatorPager.attachToPager(viewPager)

//        purchaseInfoAdapter = PurchaseInfoAdapter(arrayListOf())
//        recyclerviewInfo.adapter = purchaseInfoAdapter
        setData(0)

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setData(position)

                // Pause previous
                (childFragmentManager.findFragmentByTag("f$currentPosition") as? VideoFragment)?.pauseVideo()

                // Play current
                (childFragmentManager.findFragmentByTag("f$position") as? VideoFragment)?.playVideo()

                currentPosition = position
            }
        })
    }

    private fun setData(position: Int) = with(binding){
        txtTitle.text = videoFilesList[position].title
//        purchaseInfoAdapter.setData(videoFilesList[position].pointsList)
    }
}