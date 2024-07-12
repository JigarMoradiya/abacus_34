package com.jigar.me.ui.view.dashboard.fragments.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jigar.me.R
import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.databinding.FragmentReportsHomeBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.confirm_alerts.dialogs.ExerciseCompleteDialog
import com.jigar.me.ui.view.dashboard.fragments.reports.adapter.ReportsListAdapter
import com.jigar.me.ui.viewmodel.ExamViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.DateTimeUtils
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.openYoutube
import com.jigar.me.utils.extensions.show
import com.jigar.me.utils.paging.EndlessRecyclerListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class ReportsHomeFragment : BaseFragment(), ReportsListAdapter.OnItemClickListener,
    ExerciseCompleteDialog.ExerciseCompleteDialogInterface {
    private val examViewModel by viewModels<ExamViewModel>()
    private lateinit var binding: FragmentReportsHomeBinding
    private lateinit var mNavController: NavController
    private var root : View? = null
    private lateinit var reportsAdapter: ReportsListAdapter
    private var totalRecord: Int = 0
    private var from: Int = 0
    private var fromDate: String? = null
    private var toDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        if (root == null){
            binding = FragmentReportsHomeBinding.inflate(inflater, container, false)
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
        reportsAdapter = ReportsListAdapter(arrayListOf(),this)
        with(binding){
            recyclerview.adapter = reportsAdapter
            val list : ArrayList<String> = arrayListOf()
            with(list) {
                add(getString(R.string.select_report_type))
                add(getString(R.string.all_report))
                add(AppConstants.ExamType.type_Exam)
                add(AppConstants.ExamType.type_Exercise)
                add(AppConstants.ExamType.type_CustomChallengeMode)
            }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list)
            adapter.setDropDownViewResource(R.layout.spinner_item_dropdown)
            spinnerFilter.adapter = adapter
            spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 > 0){
                        from = 0
                        txtFilter.text = list[p2]
                        reportsAdapter.clearList()
                        callApi()
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) = Unit
            }
            spinnerFilter.setSelection(1)
            txtFilter.onClick {
                spinnerFilter.performClick()
            }
        }
    }
    private fun initListener() {
        with(binding){
            cardBack.onClick { onBack() }
            txtDateRange.onClick { openDatePicker() }
            cardSettingTop.onClick { goToSetting() }
            cardSubscribe.onClick { goToInAppPurchase() }
            cardYoutube.onClick { requireContext().openYoutube() }

            recyclerview.addOnScrollListener(object :
                EndlessRecyclerListener(recyclerview.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    if (totalRecord > totalItemsCount){
                        from = totalItemsCount
                        callApi()
                    }
                }
            })
        }
    }

    private fun openDatePicker() {
        val calendarEnd = Calendar.getInstance()
        val upTo = calendarEnd.timeInMillis

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1)
        val startFrom = calendar.timeInMillis

        val constraints = CalendarConstraints.Builder()
            .setStart(startFrom)
            .setEnd(upTo)
            .build()
        val datePicker = MaterialDatePicker.Builder.dateRangePicker().setCalendarConstraints(constraints).build()
        datePicker.show(childFragmentManager, "DatePicker")
        // Setting up the event for when ok is clicked
        datePicker.addOnPositiveButtonClickListener {
            if ((datePicker.selection?.first?:0L) > 0 && (datePicker.selection?.second?:0L) > 0){
                binding.txtDateRange.text = datePicker.headerText
                fromDate = DateTimeUtils.milliSecondToFormat(datePicker.selection?.first?:0L,DateTimeUtils.yyyy_MM_dd)
                toDate = DateTimeUtils.milliSecondToFormat(datePicker.selection?.second?:0L,DateTimeUtils.yyyy_MM_dd)
                from = 0
                reportsAdapter.clearList()
                callApi()
            }

        }
    }

    private fun callApi() {
        val type = binding.txtFilter.text.toString()
        if (type.equals(getString(R.string.all_report),true)){
            examViewModel.getAllExam(from_date = fromDate, to_date = toDate, from = from)
        }else if (type.equals(AppConstants.ExamType.type_CustomChallengeMode,true)){
            examViewModel.getAllExam(AppConstants.ExamType.type_CCM,from_date = fromDate, to_date = toDate,from = from)
        }else{
            examViewModel.getAllExam(type,from_date = fromDate, to_date = toDate,from = from)
        }
    }

    private fun onBack() {
        mNavController.navigateUp()
    }

    private fun initObserver() {
        examViewModel.getAllExamResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    if (from == 0){
                        showLoading()
                    }
                }
                is Resource.Success -> {
                    if (from == 0){
                        hideLoading()
                    }
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        onSuccess(it.value.data)
                    else
                        onFailure(it.value.error?.message)
                }
                is Resource.Failure -> {
                    if (from == 0){
                        hideLoading()
                    }
                    onFailure(it.errorBody)
                }
            }
        }
    }

    private fun onSuccess(data: JsonObject?) {
        val list : ArrayList<AllExamData> = if (data?.has("data") == true){
            if (data.has("total_records")){
                totalRecord = data.get("total_records").asString.toInt()
            }
             Gson().fromJson(data.getAsJsonArray("data"), object : TypeToken<ArrayList<AllExamData>>() {}.type)
        }else{
            arrayListOf()
        }
        reportsAdapter.setData(list)
        with(binding){
            if (list.isEmpty()){
                noDataView.show()
            }else {
                noDataView.hide()
            }
        }
    }

    override fun onItemClick(data: AllExamData) {
        val bundle = Bundle()
        when (data.type) {
            AppConstants.ExamType.type_CCM -> { }
            AppConstants.ExamType.type_Exam -> {
                bundle.putString(AppConstants.extras_Comman.type, "new")
                bundle.putString(AppConstants.extras_Comman.examResult, Gson().toJson(data.toExamResult()))
                bundle.putString(AppConstants.extras_Comman.examAbacusType, data.theme?:AppConstants.Settings.theam_Default)
                bundle.putString(AppConstants.extras_Comman.From, "report")
                mNavController.navigate(R.id.action_reportsHomeFragment_to_examResultFragment, bundle)
            }
            AppConstants.ExamType.type_Exercise -> {
                ExerciseCompleteDialog.showPopup(requireContext(),data.toExerciseResult(),prefManager,null,null,this@ReportsHomeFragment)
            }
        }
    }

    override fun exerciseCompleteCloseDialog() = Unit

}