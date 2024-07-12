package com.jigar.me.ui.view.dashboard.fragments.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.R
import com.jigar.me.data.model.AdditionSubtractionAbacus
import com.jigar.me.data.model.pages.Pages
import com.jigar.me.databinding.FragmentTempAbacusListBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.dashboard.fragments.page.adapter.AbacusTempListAdapter
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.readJsonAsset
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TempAbacusListFragment : BaseFragment(), AbacusTempListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentTempAbacusListBinding
    private lateinit var mNavController: NavController
    private var root: View? = null

    private var additionSubtraction: Pages? = null
    private var hintPage: String? = null
    private var fileAbacus: String? = null
    private var abacusType = ""
    private var pageId = ""
    private var isRandomGenerate = false
    private var Que2_str = "" // required only for Multiplication and Division
    private var Que2_type = "" //required only for Multiplication and Division
    private var Que1_digit_type = 0 // required only for Multiplication
    private var From = 0 // required only for number
    private var To = 0 // required only for number
    private var isRandom = false // required only for number
    private var number = 0L // required only for number
    private var abacus_number = 0 // required only for number
    private lateinit var abacusTempListAdapter: AbacusTempListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        abacusType = requireArguments().getString(AppConstants.extras_Comman.AbacusType, "")
        when (abacusType) {
            AppConstants.extras_Comman.AbacusTypeNumber -> {
                pageId = requireArguments().getString(AppConstants.apiParams.pageId, "")
                From = requireArguments().getInt(AppConstants.extras_Comman.From, 0)
                To = requireArguments().getInt(AppConstants.extras_Comman.To, 0)
                isRandom =
                    requireArguments().getBoolean(AppConstants.extras_Comman.isType_random, false)
            }

            AppConstants.extras_Comman.AbacusTypeAdditionSubtraction -> {
                additionSubtraction = Gson().fromJson(
                    requireArguments().getString(AppConstants.extras_Comman.data),
                    Pages::class.java
                )
                hintPage = additionSubtraction?.hint
                fileAbacus = additionSubtraction?.file
                pageId = additionSubtraction?.page_id ?: ""
                isRandomGenerate = additionSubtraction?.isGenerate == true
            }

            else -> { // for multiplication and division
                pageId = requireArguments().getString(AppConstants.apiParams.pageId, "")
                Que2_str = requireArguments().getString(AppConstants.extras_Comman.Que2_str, "")
                Que2_type = requireArguments().getString(AppConstants.extras_Comman.Que2_type, "")
                if (abacusType == AppConstants.extras_Comman.AbacusTypeMultiplication) {
                    Que1_digit_type =
                        requireArguments().getInt(AppConstants.extras_Comman.Que1_digit_type, 0)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (root == null) {
            binding = FragmentTempAbacusListBinding.inflate(inflater, container, false)
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
        if (additionSubtraction?.ref_pages != null) {
        } else if (isRandomGenerate) {
        } else {
            val abacus = if (!fileAbacus.isNullOrEmpty()) {
                requireContext().readJsonAsset(fileAbacus)
            } else {
                requireContext().readJsonAsset("abacus.json")
            }

            val type = object : TypeToken<List<AdditionSubtractionAbacus>>() {}.type
            val temp: List<AdditionSubtractionAbacus> = Gson().fromJson(abacus, type)
            temp.filter { it.id == pageId }.also {
                if (it.isNotNullOrEmpty()) {
                    abacusTempListAdapter = AbacusTempListAdapter(it,this,prefManager)
                    binding.recyclerviewPage.adapter = abacusTempListAdapter
                }
            }
        }
    }


    private fun initListener() {
        binding.cardBack.onClick { onBack() }
    }

    private fun onBack() {
        mNavController.navigateUp()
    }
}