package com.jigar.me.ui.view.dashboard.fragments.exam.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.exam.ExamHistory
import com.jigar.me.databinding.FragmentExamHistoryTabBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ExamHistoryTabFragment : BaseFragment(), ExamHistoryListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentExamHistoryTabBinding
    private val apiViewModel by viewModels<AppViewModel>()
    private val examHistoryListAdapter: ExamHistoryListAdapter = ExamHistoryListAdapter(arrayListOf(), this)
    companion object {
        const val ARG_POSITION = "arg_position"

        @JvmStatic
        fun newInstance(position: Int) = ExamHistoryTabFragment()
            .apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, position)
                }
            }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExamHistoryTabBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.recyclerview.adapter = examHistoryListAdapter
        val examType = when (arguments?.getInt(ARG_POSITION)) {
            0 -> { AppConstants.ExamType.exam_Level_Beginner }
            1 -> { AppConstants.ExamType.exam_Level_Intermediate }
            else -> { AppConstants.ExamType.exam_Level_Expert }
        }
        apiViewModel.getExamHistoryList(examType).observe(viewLifecycleOwner){
            fetchExamHistoryList(it)
        }
    }
    private fun fetchExamHistoryList(data: List<ExamHistory>) {
        CoroutineScope(Dispatchers.Main).launch{
            if (data.isNullOrEmpty()){
                binding.recyclerview.hide()
                binding.noDataView.show()
            }else{
                binding.recyclerview.show()
                binding.noDataView.hide()
                examHistoryListAdapter.setData(data)
            }
        }

    }

    override fun onItemClick(data: ExamHistory) {
        val bundle = Bundle()
        if (data.examFor.isNotNullOrEmpty()){
            bundle.putString(AppConstants.extras_Comman.type, "new")
            bundle.putString(AppConstants.extras_Comman.examResult, Gson().toJson(data.examBeginners))
            bundle.putString(AppConstants.extras_Comman.examAbacusType, data.theme?:AppConstants.Settings.theam_Default)
        }else{
            if (data.examType == AppConstants.ExamType.exam_Level_Beginner){
                bundle.putString(AppConstants.extras_Comman.type, "object")
                bundle.putString(AppConstants.extras_Comman.examResult, Gson().toJson(data.examBeginners))
                bundle.putString(AppConstants.extras_Comman.examAbacusType, data.theme?:AppConstants.Settings.theam_Default)
            }else{
                bundle.putString(AppConstants.extras_Comman.type, "detail")
                bundle.putString(AppConstants.extras_Comman.examResult, Gson().toJson(data.examDetails))
            }
        }

        bundle.putString(AppConstants.extras_Comman.From, "history")
        ((parentFragment as ExamHistoryHomeFragment)).mNavController.navigate(R.id.action_examHistoryHomeFragment_to_examResultFragment, bundle)
    }


}