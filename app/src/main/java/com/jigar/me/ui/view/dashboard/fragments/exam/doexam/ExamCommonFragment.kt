package com.jigar.me.ui.view.dashboard.fragments.exam.doexam

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.ExamPaper
import com.jigar.me.data.local.data.BeginnerExamQuestionType
import com.jigar.me.data.local.data.DataObjectsSize
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.local.data.ExamProvider
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.databinding.FragmentExamCommanBinding
import com.jigar.me.databinding.LayoutAbacusExamBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.abacus.AbacusUtils
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.view.confirm_alerts.dialogs.ExamCompleteDialog
import com.jigar.me.ui.viewmodel.ExamViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Calculator
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.isNetworkAvailable
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.secToTimeFormat
import com.jigar.me.utils.extensions.setBlinkAnimation
import com.jigar.me.utils.extensions.show
import com.jigar.me.utils.extensions.toastL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Random
import androidx.navigation.findNavController
import com.jigar.me.utils.PlaySound

@AndroidEntryPoint
class ExamCommonFragment : BaseFragment(), ExamCompleteDialog.TestCompleteDialogInterface{

    private lateinit var mBinding: FragmentExamCommanBinding
    private val examViewModel by viewModels<ExamViewModel>()
    private lateinit var mNavController: NavController

    private var listExam: List<ExamPaper> = ArrayList()
    private var currentQuestionPos = 0
    private var totalWrong = 0
    private var correctAns = ""
    private var theme = ""
    private var examLevel = AppConstants.EXAM.examDifficultyBeginner
    private lateinit var mCalculator: Calculator
    private var total_sec = 0
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var objectListAdapter1: ObjectListAdapter = ObjectListAdapter(0, null, DataObjectsSize.Small)
    private var objectListAdapter2: ObjectListAdapter = ObjectListAdapter(0, null,DataObjectsSize.Small)
    private var clickType = ""
    private var isNumberExam = false
    private var isAdditionExam = false
    private var isSubtractionExam = false
    private var isMultiplicationExam = false
    private var isDivisionExam = false
    private var examForList : ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        examLevel = ExamCommonFragmentArgs.fromBundle(requireArguments()).examLevel

        isNumberExam = ExamCommonFragmentArgs.fromBundle(requireArguments()).isNumberExam
        isAdditionExam = ExamCommonFragmentArgs.fromBundle(requireArguments()).isAdditionExam
        isSubtractionExam = ExamCommonFragmentArgs.fromBundle(requireArguments()).isSubtractionExam
        isMultiplicationExam = ExamCommonFragmentArgs.fromBundle(requireArguments()).isMultiplicationExam
        isDivisionExam = ExamCommonFragmentArgs.fromBundle(requireArguments()).isDivisionExam
        if (isNumberExam){examForList.add(AppConstants.EXAM.isNumberSelected)}
        if (isAdditionExam){examForList.add(AppConstants.EXAM.isAdditionSelected)}
        if (isSubtractionExam){examForList.add(AppConstants.EXAM.isSubtractionSelected)}
        if (isMultiplicationExam){examForList.add(AppConstants.EXAM.isMultiplicationSelected)}
        if (isDivisionExam){examForList.add(AppConstants.EXAM.isDivisionSelected)}

        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentExamCommanBinding.inflate(inflater, container, false)
        setNavigationGraph()
        init()
        clickListener()
        return mBinding.root
    }
    private fun setNavigationGraph() {
        mNavController = requireActivity().findNavController(R.id.nav_host_fragment)
    }
    private fun init() {
        mCalculator = Calculator()
        handler = Handler(Looper.getMainLooper())

        getAndStartExam()
    }
    private fun initObserver() {
        examViewModel.submitAllExamResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        onSuccess()
                    else
                        onSuccess()
                        onFailure(it.value.error?.message)
                }
                is Resource.Failure -> {
                    hideLoading()
                    onSuccess()
                    onFailure(it.errorBody)
                }
            }
        }
    }

    private fun onSuccess() {
        val right = listExam.size - totalWrong
        ExamCompleteDialog.showPopup(requireActivity(),total_sec.secToTimeFormat(),"0",totalWrong.toString(),right.toString(),listExam.size.toString(),this,prefManager)
    }

    private fun completePopup() {
        mBinding.cardAnswer11.isEnabled = false
        mBinding.cardAnswer22.isEnabled = false
        mBinding.cardAnswer33.isEnabled = false
        mBinding.cardAnswer44.isEnabled = false
        if (runnable != null){
            handler?.removeCallbacks(runnable!!)
        }
        val right = listExam.size - totalWrong
        val submitExamRequest = SubmitAllExamDataRequest()
        with(submitExamRequest){
            type = AppConstants.EXAM.type_Exam
            level = examLevel
            theme = theme
            sub_type = examForList.joinToString(separator = ",")
            total_time_taken = total_sec
            no_of_questions = listExam.size
            no_of_right_answers = right
            val questionsList : ArrayList<Any> = arrayListOf()
            listExam.map {
                var question = it.value

                val queType = if (it.imageData != null || it.isAbacusQuestion == true){
                    if (it.type == BeginnerExamQuestionType.Count){
                        if (it.isAbacusQuestion == true){
                            AppConstants.EXAM.exam_Que_type_abacus
                        }else{
                            AppConstants.EXAM.exam_Que_type_object
                        }
                    }else if (it.type == BeginnerExamQuestionType.Additions || it.type == BeginnerExamQuestionType.Subtractions){
                        when (it.type) {
                            BeginnerExamQuestionType.Additions -> {
                                question = it.value + "+" + it.value2
                            }

                            BeginnerExamQuestionType.Subtractions -> {
                                question = it.value + "-" + it.value2
                            }

                            else -> {}
                        }
                        if (it.isAbacusQuestion == true){
                            AppConstants.EXAM.exam_Que_type_abacus
                        }else{
                            AppConstants.EXAM.exam_Que_type_object
                        }
                    }else{
                        ""
                    }
                }else{
                    AppConstants.EXAM.exam_Que_type_question
                }

                val resultObject = mCalculator.getResult(question,question)
                val correctAns = CommonUtils.removeTrailingZero(resultObject)
                if (it.imageData?.name.isNullOrEmpty()){
                    questionsList.add(QuestionDataRequest(question,it.userAnswer,(correctAns == it.userAnswer),queType))
                }else{
                    questionsList.add(QuestionDataRequest(question,it.userAnswer,(correctAns == it.userAnswer),queType,image = it.imageData?.name))
                }

            }
            questions = questionsList
        }
        examViewModel.submitAllExam(submitExamRequest)
    }

    private fun getAndStartExam(){
        lifecycleScope.launch {
            theme = AbacusUtils.setAbacusTempThemeExam(requireContext(),prefManager,AbacusBeadType.Exam)
        }
        listExam = ExamProvider.generateExamPaperNew(examLevel,examForList)
        if(requireContext().isNetworkAvailable){
            setDailyExamAbacus()
        } else {
            notOfflineSupportDialog()
        }
    }
    private fun notOfflineSupportDialog() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.no_internet_working),getString(R.string.no_internet)
            ,getString(R.string.continue_working_internet),getString(R.string.no_working_internet), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    if (requireContext().isNetworkAvailable){
                        setDailyExamAbacus()
                    } else{
                        showToast(R.string.still_no_internet)
                        notOfflineSupportDialog()
                    }
                }
                override fun onConfirmationNoClick(bundle: Bundle?){
                    mNavController.navigateUp()
                }
            })
    }

    private fun clickListener() {
        // blink animation
        mBinding.txtTapCorrectAns1.setBlinkAnimation()
        mBinding.cardBack.onClick { examLeaveAlert() }

        mBinding.cardAnswer11.onClick { onViewClick("answer11") }
        mBinding.cardAnswer22.onClick { onViewClick("answer22") }
        mBinding.cardAnswer33.onClick { onViewClick("answer33") }
        mBinding.cardAnswer44.onClick { onViewClick("answer44") }
    }

    private fun setDailyExamAbacus() {
        mBinding.progressHorizontal.max = listExam.size
        total_sec = 0
        currentQuestionPos = 0
        totalWrong = 0
        mBinding.cardAnswer11.isEnabled = true
        mBinding.cardAnswer22.isEnabled = true
        mBinding.cardAnswer33.isEnabled = true
        mBinding.cardAnswer44.isEnabled = true
        val delay = 1000 //milliseconds

        runnable = object : Runnable {
            override fun run() {
                //do something
                total_sec++
                mBinding.txtTimer.text = total_sec.secToTimeFormat()
                handler?.postDelayed(this, delay.toLong())
            }
        }
        handler?.postDelayed(runnable!!, delay.toLong())
        setExamPaper()
    }

    private fun setExamPaper() {
        if (currentQuestionPos >= listExam.size) {
            completePopup()
        } else {
            mBinding.txtHeaderTitle.hide()
            mBinding.conObject.hide()
            mBinding.conTxt.show()
            setQuestionTxt()
        }
    }

    private fun setQuestionTxt() {
        mBinding.progressHorizontal.progress = (currentQuestionPos + 1)
        mBinding.txtAbacus.text = listExam[currentQuestionPos].value
            .replace("+", " + ")
            .replace("-", " - ")
            .replace("x", " x ")
            .replace("/", " ÷ ")+" = "
        if (listExam[currentQuestionPos].value.contains("x")){
            val list = listExam[currentQuestionPos].value.split("x")
            correctAns = (list[0].toLong()*list[1].toLong()).toString()
        }else{
            val resultObject = mCalculator.getResult(
                listExam[currentQuestionPos].value,
                listExam[currentQuestionPos].value
            )
            correctAns = CommonUtils.removeTrailingZero(resultObject)
        }

        val listAnswerTemp: MutableList<Long> = ArrayList()
        val listAnswer: MutableList<Long?> = ArrayList()
        if (correctAns.toLong() - 1 > 0) {
            listAnswerTemp.add(correctAns.toLong() - 1)
        }
        if (correctAns.toInt() - 2 > 0) {
            listAnswerTemp.add(correctAns.toLong() - 2)
        }
        if (correctAns.toInt() - 3 > 0) {
            listAnswerTemp.add(correctAns.toLong() - 3)
        }
        listAnswerTemp.add(correctAns.toLong() + 1)
        listAnswerTemp.add(correctAns.toLong() + 2)
        listAnswerTemp.add(correctAns.toLong() + 3)
        for (i in 0..2) {
            val pos = Random().nextInt(listAnswerTemp.size)
            listAnswer.add(listAnswerTemp[pos])
            listAnswerTemp.removeAt(pos)
        }
        listAnswer.add(correctAns.toLong())
        listAnswer.shuffle()
        try {
            mBinding.txtAnswer11.text = listAnswer[0].toString()
            mBinding.txtAnswer22.text = listAnswer[1].toString()
            mBinding.txtAnswer33.text = listAnswer[2].toString()
            mBinding.txtAnswer44.text = listAnswer[3].toString()
        } catch (e: Exception) {
            e.printStackTrace()
            requireContext().toastL(getString(R.string.some_thing_wrong))
            onBack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (runnable != null){
            handler?.removeCallbacks(runnable!!)
        }
    }

    private fun onViewClick(clickType: String) {
        this.clickType = clickType
        PlaySound.playTap(requireContext())
        clickOtions()

    }

    private fun clickOtions() {
        when (clickType) {
            "answer11" -> {
                if (correctAns != mBinding.txtAnswer11.text.toString()) {
                    totalWrong++
                }
                listExam[currentQuestionPos].userAnswer = mBinding.txtAnswer11.text.toString()
                if (currentQuestionPos == listExam.lastIndex){
                    completePopup()
                }else if (currentQuestionPos < listExam.size){
                    currentQuestionPos++
                    setExamPaper()
                }
            }
            "answer22" -> {
                if (correctAns != mBinding.txtAnswer22.text.toString()) {
                    totalWrong++
                }
                listExam[currentQuestionPos].userAnswer = mBinding.txtAnswer22.text.toString()
                if (currentQuestionPos == listExam.lastIndex){
                    completePopup()
                }else if (currentQuestionPos < listExam.size){
                    currentQuestionPos++
                    setExamPaper()
                }
            }
            "answer33" -> {
                if (correctAns != mBinding.txtAnswer33.text.toString()) {
                    totalWrong++
                }
                listExam[currentQuestionPos].userAnswer = mBinding.txtAnswer33.text.toString()
                if (currentQuestionPos == listExam.lastIndex){
                    completePopup()
                }else if (currentQuestionPos < listExam.size){
                    currentQuestionPos++
                    setExamPaper()
                }
            }
            "answer44" -> {
                if (correctAns != mBinding.txtAnswer44.text.toString()) {
                    totalWrong++
                }
                listExam[currentQuestionPos].userAnswer = mBinding.txtAnswer44.text.toString()
                if (currentQuestionPos == listExam.lastIndex){
                    completePopup()
                }else if (currentQuestionPos < listExam.size){
                    currentQuestionPos++
                    setExamPaper()
                }
            }
        }
    }


    // exam leave listener
    override fun testGiveAgain() {
        getAndStartExam()
    }

    fun examLeaveAlert() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.leave_exam_alert),getString(R.string.leave_exam_msg)
            ,getString(R.string.yes_i_m_sure),getString(R.string.no_please_continue), icon = R.drawable.ic_alert,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    testLeaveConfirm()
                }
                override fun onConfirmationNoClick(bundle: Bundle?) = Unit
            })
    }

    private fun testLeaveConfirm() {
        if (handler != null && runnable != null) {
            handler?.removeCallbacks(runnable!!)
        }
        onBack()
    }
    // exam complete listner
    override fun testCompleteClose() {
        onBack()
    }

    override fun testCompleteGotoResult() {
        val bundle = Bundle()
        bundle.putString(AppConstants.extras_Comman.examResult, Gson().toJson(listExam))
        bundle.putString(AppConstants.extras_Comman.type, "new")
        bundle.putString(AppConstants.extras_Comman.examAbacusType, theme)
        bundle.putString(AppConstants.extras_Comman.From, "exam")
        mNavController.navigate(R.id.action_examCommonFragment_to_examResultFragment, bundle)
    }
    private fun onBack() {
        mNavController.navigateUp()
    }

    private fun notOfflineSupportDialog2() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.no_internet_working),getString(R.string.no_internet)
            ,getString(R.string.continue_working_internet),getString(R.string.no_working_internet), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    if (requireContext().isNetworkAvailable){
                        clickOtions()
                    } else{
                        showToast(R.string.still_no_internet)
                        notOfflineSupportDialog2()
                    }
                }
                override fun onConfirmationNoClick(bundle: Bundle?){
                    mNavController.navigateUp()
                }
            })
    }
}