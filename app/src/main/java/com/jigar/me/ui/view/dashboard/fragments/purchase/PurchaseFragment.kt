package com.jigar.me.ui.view.dashboard.fragments.purchase

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.MyApplication
import com.jigar.me.R
import com.jigar.me.data.model.DisplayPurchaseData
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.databinding.FragmentPurchaseBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.PaidFeatureListDialog
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.ui.viewmodel.InAppViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.isNetworkAvailable
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.toastS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

@AndroidEntryPoint
class PurchaseFragment : BaseFragment(), PurchaseAdapter.OnItemClickListener {
    private lateinit var binding: FragmentPurchaseBinding
    private val apiViewModel by viewModels<AppViewModel>()
    private val inAppViewModel by viewModels<InAppViewModel>()
    private var listSKU: MutableList<InAppSkuDetails> = arrayListOf()
    private lateinit var skuListAdapter: PurchaseAdapter
    private lateinit var mNavController: NavController
    private var displayItemList: ArrayList<DisplayPurchaseData> = arrayListOf()
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentPurchaseBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initViews()
        initListener()
        return binding.root
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun initViews() {
        skuListAdapter = PurchaseAdapter(listSKU,prefManager, this)
        binding.recyclerview.adapter = skuListAdapter
        inAppViewModel.inAppInit()

        CoroutineScope(Dispatchers.Main).launch{
            val idList: ArrayList<String> = arrayListOf()
            if (prefManager.getCustomParam(AppConstants.RemoteConfig.displayPlanList,"").isNotEmpty()){
                val type = object : TypeToken<ArrayList<DisplayPurchaseData>>() {}.type
                displayItemList = Gson().fromJson(prefManager.getCustomParam(AppConstants.RemoteConfig.displayPlanList,""),type)
                displayItemList.map {
                    idList.add(it.id)
                }
            }

            val list = apiViewModel.getPurchasesSku()
            if (list.isNotNullOrEmpty()){
                idList.addAll(list)
            }
            apiViewModel.getInAppSKU(idList).observe(viewLifecycleOwner){
                setSKU(it)
            }
        }


    }

    private fun initListener() {
        with(binding){
            cardBack.onClick { mNavController.navigateUp() }
            txtShowPaidFeatureList.onClick {
                PaidFeatureListDialog.showPopup(requireActivity())
            }
        }
    }

    private fun setSKU(listData: List<InAppSkuDetails>) {
        if (displayItemList.isNotEmpty()){
            listData.mapIndexed { index, mainList ->
                displayItemList.find { it.id == mainList.sku  }.also {
                    if (it != null){
                        listData[index].sortOrder = it.so
                    }else{
                        listData[index].sortOrder = 9999
                    }
                }
            }
        }
        val sortedList = listData.sortedWith { list1, list2 -> list1.sortOrder - list2.sortOrder }
        listSKU.clear()
        listSKU.addAll(sortedList)
//        val find = listSKU.indexOfFirst { it.sku.lowercase(Locale.getDefault()) == PRODUCT_ID_All_lifetime }
//        if (find > -1) {
//            val changingData = listSKU[find]
//            listSKU.removeAt(find)
//            listSKU.add(0, changingData)
//        }
        skuListAdapter.notifyItemRangeChanged(0,listSKU.size)
    }

    override fun onPurchaseItemClick(position: Int) {
        if (requireContext().isNetworkAvailable) {
            if (!listSKU[position].isPurchase) {
                (activity as MainDashboardActivity).isPurchaseDataChecked = false
                // firebase event
                MyApplication.logEvent("Purchase_"+listSKU[position].sku, null)
                inAppViewModel.makePurchase(requireActivity(), listSKU[position])
            }
        }else{
            requireContext().toastS(getString(R.string.no_internet))
        }
    }
}