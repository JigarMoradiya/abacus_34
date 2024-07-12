package com.jigar.me.ui.view.dashboard.fragments.tables

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jigar.me.R
import com.jigar.me.databinding.FragmentMultiplicationTablesBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.openYoutube
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class  MultiplicationTablesFragment : BaseFragment(){
    private lateinit var binding: FragmentMultiplicationTablesBinding
    private lateinit var mNavController: NavController
    private var root : View? = null
    private lateinit var tableAdapter: MultiplicationTablesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        if (root == null){
            binding = FragmentMultiplicationTablesBinding.inflate(inflater, container, false)
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
        binding.title = getString(R.string.times_table)
        tableAdapter = MultiplicationTablesListAdapter(20)
        binding.recyclerview.adapter = tableAdapter
    }


    private fun initListener() {
        binding.cardBack.onClick { onBack() }
        binding.cardSettingTop.onClick { goToSetting() }
        binding.cardSubscribe.onClick { goToInAppPurchase() }
        binding.cardYoutube.onClick { requireContext().openYoutube() }
    }
    private fun onBack() {
        mNavController.navigateUp()
    }

    private fun initObserver() {
    }

}