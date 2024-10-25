package com.jigar.me.ui.view.dashboard.fragments.home_new.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.databinding.FragmentCategoryBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.invisible
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : BaseFragment() {
    private lateinit var binding: FragmentCategoryBinding
    private var root: View? = null
    private var mNavController: NavController? = null

    private val appViewModel by viewModels<AppViewModel>()
    private lateinit var categoryNewAdapter: CategoryNewAdapter
    private lateinit var pagesNewAdapter: PagesNewAdapter
    private var levelId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        levelId = CategoryFragmentArgs.fromBundle(requireArguments()).levelId
        initObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (root == null) {
            binding = FragmentCategoryBinding.inflate(inflater, container, false)
            root = binding.root
            setNavigationGraph()
            initViews()
            initListener()
        }
        return root
    }

    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun initViews() = with(binding) {
        CoroutineScope(Dispatchers.Main).launch {
            val purchasedSKU = appViewModel.getInAppSKUPurchased()
            categoryNewAdapter = CategoryNewAdapter(arrayListOf(),purchasedSKU) { position, previousPos, data ->
                clickCategory(position, previousPos, data)
            }
            recyclerviewCategory.adapter = categoryNewAdapter

            val allSetList = appViewModel.getAllSet()
            pagesNewAdapter = PagesNewAdapter(arrayListOf(),allSetList) { pagePosition,setPosition,setData,data ->
                val categoryData = categoryNewAdapter.listData[categoryNewAdapter.selectedPosition]
                val isPurchase = CommonUtils.checkLevelIsPurchase(purchasedSKU,categoryData)
                if (isPurchase){
                    gotoAbacus(setData)
                }else{
                    purchaseDialog()
                }
            }
            recyclerviewPages.adapter = pagesNewAdapter

            val categoryList = appViewModel.getCategory(levelId)
            categoryNewAdapter.setData(categoryList)
            if (categoryList.isNotNullOrEmpty()){
                setPages(categoryList.first().id)
            }
        }
    }

    private fun purchaseDialog() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.level_subcribe_title),getString(R.string.level_subcribe_msg)
            ,getString(R.string.yes_i_want_to_purchase),getString(R.string.no_purchase_later), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    goToInAppPurchase()
                }
                override fun onConfirmationNoClick(bundle: Bundle?) = Unit
            })
    }

    private fun gotoAbacus(setData: Set) {
        val action = CategoryFragmentDirections.toAbacusCalculationFragment(setData.id)
        mNavController?.navigate(action)
    }

    override fun onResume() {
        super.onResume()
        if (::categoryNewAdapter.isInitialized){
            if (categoryNewAdapter.listData.isNotNullOrEmpty()){
                CoroutineScope(Dispatchers.Main).launch {
                    val purchasedSKU = appViewModel.getInAppSKUPurchased()
                    categoryNewAdapter.purchasedSKU = purchasedSKU
                    categoryNewAdapter.setData(categoryNewAdapter.listData)

                    val allSetList = appViewModel.getAllSet()
                    pagesNewAdapter.allSetList = allSetList
                    setPages(categoryNewAdapter.listData[categoryNewAdapter.selectedPosition].id)
                }
            }
        }
    }

    private fun setPages(categoryId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val pagesList = appViewModel.getPages(categoryId)
            pagesNewAdapter.setData(pagesList)
            binding.recyclerviewPages.scrollToPosition(0)
        }
    }

    private fun initListener() {
        with(binding) {
            cardBack.onClick { onBack() }
        }
    }

    private fun initObserver() {

    }
    private fun clickCategory(position: Int, previousPos: Int, data: Category) = with(binding){
        setPages(data.id)
        val viewHolder = recyclerviewCategory.findViewHolderForAdapterPosition(previousPos) as? CategoryNewAdapter.ViewHolder
        if (viewHolder != null) {
            viewHolder.binding.imgArrow.invisible()
        } else {
            categoryNewAdapter.notifyItemChanged(previousPos)
        }
    }
    private fun onBack() {
        mNavController?.navigateUp()
    }

}