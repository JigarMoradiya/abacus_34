package com.jigar.me.ui.view.dashboard.fragments.faqs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jigar.me.R
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.databinding.FragmentFaqsBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.openYoutube
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FAQsFragment : BaseFragment() {
    private lateinit var binding: FragmentFaqsBinding
    private lateinit var mNavController: NavController
    private var root: View? = null
    private lateinit var adapter: FaqsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (root == null) {
            binding = FragmentFaqsBinding.inflate(inflater, container, false)
            root = binding.root
            setNavigationGraph()
            initViews()
            initListener()
        }
        return root!!
    }

    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun initViews() {
        val emailId = prefManager.getCustomParam(AppConstants.RemoteConfig.supportEmail,"")
        adapter = FaqsListAdapter(DataProvider.getFaqsList(requireContext(),emailId))
        with(binding){
            recyclerview.adapter = adapter
            if (prefManager.isUserLoggedIn()){
                linearTopEnd.show()
            }else{
                linearTopEnd.hide()
            }
        }
    }


    private fun initListener() {
        with(binding){
            cardBack.onClick { onBack() }
            cardSettingTop.onClick { goToSetting() }
            cardSubscribe.onClick { goToInAppPurchase() }
            cardYoutube.onClick { requireContext().openYoutube() }
        }
    }

    private fun onBack() {
        mNavController.navigateUp()
    }

}