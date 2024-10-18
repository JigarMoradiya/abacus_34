package com.jigar.me.ui.view.dashboard.fragments.home_new.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.databinding.FragmentCategoryBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.invisible
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.onClick
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
        categoryNewAdapter = CategoryNewAdapter(arrayListOf()) { position, previousPos, data ->
            clickCategory(position, previousPos, data)
        }
        recyclerviewCategory.adapter = categoryNewAdapter

        CoroutineScope(Dispatchers.Main).launch {
            val allSetList = appViewModel.getAllSet()
//            recyclerviewPages.apply {
//                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
//                    gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
//                }
//                setHasFixedSize(true)
//            }
            pagesNewAdapter = PagesNewAdapter(arrayListOf(),allSetList) { pagePosition,setPosition,setData,data ->
                val action = CategoryFragmentDirections.toAbacusCalculationFragment(setData.id)
                mNavController?.navigate(action)
            }
            recyclerviewPages.adapter = pagesNewAdapter

            val categoryList = appViewModel.getCategory(levelId)
            categoryNewAdapter.setData(categoryList)
            if (categoryList.isNotNullOrEmpty()){
                setPages(categoryList.first().id)
            }
        }
    }

    private fun setPages(categoryId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val pagesList = appViewModel.getPages(categoryId)
            pagesNewAdapter.setData(pagesList)
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