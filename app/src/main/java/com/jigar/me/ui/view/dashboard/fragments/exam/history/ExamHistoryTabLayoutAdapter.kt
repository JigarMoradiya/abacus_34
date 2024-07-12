package com.jigar.me.ui.view.dashboard.fragments.exam.history

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ExamHistoryTabLayoutAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return ExamHistoryTabFragment.newInstance(position)
    }

    override fun getItemCount(): Int {
        return 3
    }

}