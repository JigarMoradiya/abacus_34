package com.jigar.me.ui.view.dashboard.fragments.practise_material

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.databinding.FragmentMaterialHomeBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_material_maths
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_material_nursery
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.ui.viewmodel.InAppViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.isNetworkAvailable
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MaterialHomeFragment : BaseFragment() {
    private lateinit var binding: FragmentMaterialHomeBinding
    private var root : View? = null
    private val appViewModel by viewModels<AppViewModel>()
    private val inAppViewModel by viewModels<InAppViewModel>()
    private lateinit var mNavController: NavController
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (root == null){
            binding = FragmentMaterialHomeBinding.inflate(inflater, container, false)
            root = binding.root
            setNavigationGraph()
            initView()
            initListener()
            ads()
        }
        return root!!
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    private fun initView() {
    }

    override fun onResume() {
        super.onResume()
        checkPurchase()
    }

    private fun ads() {
        with(prefManager){
            if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" &&
                getCustomParam(AppConstants.AbacusProgress.Ads, "") == "Y" &&
                getCustomParam(AppConstants.Purchase.Purchase_Material_Nursery, "") != "Y" &&
                getCustomParam(AppConstants.Purchase.Purchase_Material_Maths, "") != "Y" &&
                getCustomParam(AppConstants.Purchase.Purchase_All, "") != "Y" &&
                getCustomParam(AppConstants.Purchase.Purchase_Ads, "") != "Y"
            ) {
                showAMBannerAds(binding.adView,getString(R.string.banner_ad_unit_id_practise_material))
            }
        }
    }

    private fun checkPurchase() {
        with(prefManager){
            if (AppConstants.Purchase.AdsShow != "Y" ||
                getCustomParam(AppConstants.AbacusProgress.Ads, "") != "Y" ||
                getCustomParam(AppConstants.Purchase.Purchase_Material_Nursery, "") == "Y"
                || getCustomParam(AppConstants.Purchase.Purchase_Material_Maths, "") == "Y"
                || getCustomParam(AppConstants.Purchase.Purchase_All, "") == "Y"
                || getCustomParam(AppConstants.Purchase.Purchase_Ads, "") == "Y") {
                binding.adView.hide()
            }
            if (getCustomParam(AppConstants.Purchase.Purchase_Material_Maths, "N") == "Y"
                || getCustomParam(AppConstants.Purchase.Purchase_All, "N") == "Y"){
                binding.txtMathsBtn1.text = getString(R.string.txt_purchased)
            }else{
                binding.txtMathsBtn1.text = getString(R.string.txt_purchase_Now)
            }

            if (getCustomParam(AppConstants.Purchase.Purchase_Material_Nursery, "N") == "Y"
                || getCustomParam(AppConstants.Purchase.Purchase_All, "N") == "Y"){
                binding.txtNurseryBtn1.text = getString(R.string.txt_purchased)
            }else{
                binding.txtNurseryBtn1.text = getString(R.string.txt_purchase_Now)
            }
        }
    }

    private fun initListener() {
        binding.cardBack.onClick { mNavController.navigateUp() }
        binding.txtMathsBtn1.onClick {
            with(prefManager){
                if (getCustomParam(AppConstants.Purchase.Purchase_Material_Maths, "N") != "Y"
                    && getCustomParam(AppConstants.Purchase.Purchase_All, "N") != "Y"){
                    appViewModel.getInAppSKUDetail(PRODUCT_ID_material_maths).observe(viewLifecycleOwner){ list ->
                        if (list.isNotEmpty()){
                            fetchSKUDetail(list[0])
                        }
                    }
                }
            }
        }
        binding.txtNurseryBtn1.onClick {
            with(prefManager){
                if (getCustomParam(AppConstants.Purchase.Purchase_Material_Nursery, "N") != "Y"
                    && getCustomParam(AppConstants.Purchase.Purchase_All, "N") != "Y"){
                    appViewModel.getInAppSKUDetail(PRODUCT_ID_material_nursery).observe(viewLifecycleOwner){ list ->
                        if (list.isNotEmpty()){
                            fetchSKUDetail(list[0])
                        }
                    }
                }
            }
        }

        binding.txtMathsBtn2.onClick {
            val action = MaterialHomeFragmentDirections.actionMaterialHomeFragmentToMaterialDownloadFragment(AppConstants.extras_Comman.DownloadType_Maths)
            mNavController.navigate(action)
        }
        binding.txtNurseryBtn2.onClick {
            val action = MaterialHomeFragmentDirections.actionMaterialHomeFragmentToMaterialDownloadFragment(AppConstants.extras_Comman.DownloadType_Nursery)
            mNavController.navigate(action)
        }
    }

    private fun fetchSKUDetail(data: InAppSkuDetails?) {
        CoroutineScope(Dispatchers.Main).launch {
            if (data != null){
                inAppViewModel.makePurchase(requireActivity(), data)
            }
        }

    }



}