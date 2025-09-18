package com.jigar.me.ui.view.dashboard.fragments.purchase.video_play

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class VideoPagerAdapter(
    fragment: Fragment,
    private val videoFiles: List<String>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = videoFiles.size

    override fun createFragment(position: Int): Fragment {
        return VideoFragment.newInstance(videoFiles[position])
    }
}
