package com.jigar.me.ui.view.dashboard.fragments.exam.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jigar.me.R
import com.jigar.me.databinding.FragmentExamHistoryBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.isNetworkAvailable
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.findNavController

@AndroidEntryPoint
class ExamHistoryHomeFragment : BaseFragment() {

    private lateinit var binding: FragmentExamHistoryBinding
    private lateinit var tabsAdapter: ExamHistoryTabLayoutAdapter
    lateinit var mNavController: NavController
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExamHistoryBinding.inflate(inflater, container, false)
        setNavigationGraph()
        init()
        clickListener()
        return binding.root
    }
    private fun setNavigationGraph() {
        mNavController = requireActivity().findNavController(R.id.nav_host_fragment)
    }

    private fun init() {
        tabsAdapter = ExamHistoryTabLayoutAdapter(childFragmentManager, lifecycle)
        binding.pager.adapter = tabsAdapter
        binding.tabs.let {
            binding.pager.let { pager ->
                TabLayoutMediator(it, pager) { tab: TabLayout.Tab, position: Int ->
                    when (position) {
                        1 -> {
                            tab.text =  AppConstants.ExamType.exam_Level_Beginner+" Level"
                        }
                        2 -> {
                            tab.text =  AppConstants.ExamType.exam_Level_Intermediate+" Level"
                        }
                        else -> tab.text = AppConstants.ExamType.exam_Level_Expert+" Level"
                    }
                }.attach()
            }
        }
    }

    private fun clickListener() {
        binding.cardBack.onClick { mNavController.navigateUp() }
    }

}