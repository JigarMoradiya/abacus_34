package com.jigar.me.ui.view.dashboard.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jigar.me.R
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.local.data.OtherApps
import com.jigar.me.databinding.FragmentAboutUsBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.dashboard.fragments.home.OtherAppAdapter
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.openMail
import com.jigar.me.utils.extensions.openURL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : BaseFragment(), OtherAppAdapter.OnItemClickListener {
    private lateinit var binding: FragmentAboutUsBinding
    private lateinit var mNavController: NavController
    private lateinit var otherAppAdapter: OtherAppAdapter
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentAboutUsBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initViews()
        initListener()
        return binding.root
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    private fun initViews() {
        try {
            val pInfo =
                requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val version = pInfo.versionName
            binding.txtVersion.text = "${getString(R.string.version)} $version"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        binding.recyclerviewOtherApps.post {
            val height = binding.recyclerviewOtherApps.height
            val width = binding.recyclerviewOtherApps.width / 2
            otherAppAdapter = if (width > height){
                OtherAppAdapter(DataProvider.getOtherAppList(),this,height)
            }else{
                OtherAppAdapter(DataProvider.getOtherAppList(),this,width)
            }
            binding.recyclerviewOtherApps.adapter = otherAppAdapter
        }
    }

    override fun onItemOtherAppClick(data: OtherApps) {
        requireContext().openURL(data.url)
    }

    private fun initListener() {
        binding.cardBack.onClick { mNavController.navigateUp() }
        binding.txtRateUs.onClick { requireContext().openURL("https://play.google.com/store/apps/details?id=${requireContext().packageName}") }
        binding.txtNeedHelp.onClick { requireContext().openMail(prefManager) }
        binding.txtPrivacy.onClick {
            requireContext().openURL(prefManager.getCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl,""))
        }
    }
}