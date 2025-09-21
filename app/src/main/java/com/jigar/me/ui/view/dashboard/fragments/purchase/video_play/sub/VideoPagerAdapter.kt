package com.jigar.me.ui.view.dashboard.fragments.purchase.video_play.sub

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jigar.me.data.local.data.VideoTutorial

class VideoPagerAdapter(
    fragment: Fragment,
    private val videoFiles: List<VideoTutorial>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = videoFiles.size

    override fun createFragment(position: Int): Fragment {
        return VideoFragment.newInstance(videoFiles[position].videoName)
    }
}
