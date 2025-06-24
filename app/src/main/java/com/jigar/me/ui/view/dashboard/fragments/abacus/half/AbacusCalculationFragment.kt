package com.jigar.me.ui.view.dashboard.fragments.abacus.half

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.local.data.AbacusProvider
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.local.data.ExamProvider
import com.jigar.me.data.local.data.ExamProvider.detectFormulaSteps
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import com.jigar.me.databinding.FragmentCalculationBinding
import com.jigar.me.databinding.FragmentHalfAbacusBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.abacus.AbacusMasterCompleteListener
import com.jigar.me.ui.view.base.abacus.OnAbacusValueChangeListener
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.view.dashboard.fragments.abacus.half.adapter.AbacusAdditionSubtractionTypeAdapter
import com.jigar.me.ui.view.dashboard.fragments.abacus.half.adapter.AbacusDivisionTypeAdapter
import com.jigar.me.ui.view.dashboard.fragments.abacus.half.adapter.AbacusMultiplicationTypeAdapter
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.ui.viewmodel.ExamViewModel
import com.jigar.me.utils.*
import com.jigar.me.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AbacusCalculationFragment : BaseFragment(), OnAbacusValueChangeListener, AbacusAdditionSubtractionTypeAdapter.HintListener{
    lateinit var binding: FragmentCalculationBinding
    private var root : View? = null
    private val appViewModel by viewModels<AppViewModel>()
    private val examViewModel by viewModels<ExamViewModel>()
    private lateinit var themeContent : AbacusContent
    private var setId : String? = null
    private var isStepByStep = false
    private var current_pos = 0
    private var abacusColumn = 13
    private var hintPage : String? = null
    private var abacusType = ""
    private var number = 0L // required only for number
    private var abacus_number = 0 // required only for number

    private var total_sec = 0L

    // Settings Constants
    private var isDisplayHelpMessage = true
    private var isAutoRefresh = false
    private var isHideTable = false
    private var isHintSound = false

    private var speek_hint = ""

    private var abacus_type = 0 // 0 = sum-sub-single  1 = multiplication 2 = divide
    private var abacusTotalColumns = 0
    private var noOfDecimalPlace = 0
    private lateinit var mNavController: NavController
    private lateinit var adapterAdditionSubtraction: AbacusAdditionSubtractionTypeAdapter
    private lateinit var adapterMultiplication: AbacusMultiplicationTypeAdapter
    private lateinit var adapterDivision: AbacusDivisionTypeAdapter

    private var list_abacus: List<Abacus> = arrayListOf()
    private lateinit var currentAbacus : Abacus
    private var list_abacus_main = ArrayList<HashMap<String, String>>()

    // abacus move
    private var isMoveNext: Boolean = false
    private var shouldResetAbacus = false

    private var abacusFragment: HalfAbacusSubFragment? = null
    private var setDetail: com.jigar.me.data.model.dbtable.abacus_all_data.Set? = null
    private var setProgress: SetProgress? = null

    private lateinit var mCalculator: Calculator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setId = AbacusCalculationFragmentArgs.fromBundle(requireArguments()).setId
        initObserver()
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        if (root == null){
            binding = FragmentCalculationBinding.inflate(inflater, container, false)
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

    private fun initObserver() {
        examViewModel.submitAllExamResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    if (examViewModel.submitAllExamDataRequest?.is_set_completed == true || setDetail?.answer_setting == AppConstants.apiParams.answerFormalAnswer){
                        showLoading()
                    }
                }
                is Resource.Success -> {
                    if (examViewModel.submitAllExamDataRequest?.is_set_completed == true || setDetail?.answer_setting == AppConstants.apiParams.answerFormalAnswer){
                        hideLoading()

                        if (it.value.status == AppConstants.APIStatus.SUCCESS){
                            CoroutineScope(Dispatchers.Main).launch {
                                setProgress?.let{
                                    it.is_set_completed = true
                                    it.latest_abacus_id = null
                                    if (setDetail?.show_time_setting == true){
                                        it.total_time_taken = total_sec.toInt()
                                    }
                                    appViewModel.insertSetProgress(listOf(it))
                                }
                                if (examViewModel.submitAllExamDataRequest?.type == AppConstants.apiParams.answerFormalAnswer) {
                                    examViewModel.submitAllExamDataRequest?.set_id?.let { it1 ->
//                                        appViewModel.deleteSetProgress(it1)
                                        appViewModel.removeUserAnswer(it1)
                                    }
                                }
                            }
                            completeSetAlert()
                        } else{
                            onFailure(it.value.error?.message)
                        }
                    }

                }
                is Resource.Failure -> {
                    if (examViewModel.submitAllExamDataRequest?.is_set_completed == true || setDetail?.answer_setting == AppConstants.apiParams.answerFormalAnswer){
                        hideLoading()
                        onFailure(it.errorBody)
                    }
                }
            }
        }
    }

    private fun completeSetAlert() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.congratulations),getString(R.string.txt_set_completed_msg)
            ,getString(R.string.ok_thanks),noBtn = getString(R.string.close), icon = R.drawable.ic_alert_complete_page,isCancelable = false,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    mNavController.navigateUp()
                }
                override fun onConfirmationNoClick(bundle: Bundle?) {
                    mNavController.navigateUp()
                }
            })
    }

    private fun initViews() {
        abacusColumn = prefManager.getCustomParamInt(AppConstants.Settings.AbacusMaxColumn,13)
        binding.isLeftHand = prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_left_hand, true)

        isDisplayHelpMessage = prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_display_help_message, true)
        isHintSound = prefManager.getCustomParamBoolean(AppConstants.Settings.Setting__hint_sound, false)
        isHideTable = prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_hide_table, false)
        if (prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_left_hand, true)){
            setLeftAbacusRules()
        }else{
            setRightAbacusRules()
        }
        // set abacus theme base on purchase or share preferences
        setTempTheme()

        binding.imgRightAbacusTools.hide()
        binding.imgLeftAbacusTools.hide()

        startAbacus()
    }

    private fun setThemeColor() {
        themeContent.resetBtnColor8?.let{
            binding.tvAnsNumberWord.setTextColor(ContextCompat.getColor(requireContext(),it))
            val finalColor = CommonUtils.mixTwoColors(ContextCompat.getColor(requireContext(),R.color.white), ContextCompat.getColor(requireContext(),it), 0.75f)
            binding.ivDivider1.setBackgroundColor(finalColor)
            binding.cardQuestions.setStrokeColor(ColorStateList.valueOf(finalColor))
            binding.txtTitle.setTextColor(ContextCompat.getColor(requireContext(),it))
            binding.txtTitleHand.setTextColor(ContextCompat.getColor(requireContext(),it))
            binding.tvAns.setTextColor(ContextCompat.getColor(requireContext(),it))

            themeContent?.dividerColor1?.let {it2 ->
                val finalColor40 = CommonUtils.mixTwoColors(ContextCompat.getColor(requireContext(),it2), ContextCompat.getColor(requireContext(),it), 0.40f)
                binding.tvAnsNumber.setTextColor(finalColor40)
            }

            themeContent?.dividerColor1?.let {it2 ->
                val finalColor60 = CommonUtils.mixTwoColors(ContextCompat.getColor(requireContext(),it2), ContextCompat.getColor(requireContext(),it), 0.60f)
                binding.cardHint.setStrokeColor(ColorStateList.valueOf(finalColor60))
                binding.cardHint2.setStrokeColor(ColorStateList.valueOf(finalColor60))
            }

        }
    }
    private fun initListener() {
        binding.cardBack.onClick { goBack() }
        binding.cardResetProgress.onClick { resetProgressClick() }
        binding.cardSettingTop.onClick { goToSetting() }
        binding.cardYoutube.onClick { requireContext().openYoutube() }
        binding.cardSubscribe.onClick { goToInAppPurchase()  }
        binding.cardTable.onClick { tableClick() }
        binding.relativeTable.onClick { binding.relativeTable.hide() }
    }

    private fun tableClick() {
        if (binding.relativeTable.isVisible) {
            binding.relativeTable.hide()
        } else {
            binding.relativeTable.show()
        }
    }

    private fun resetProgressClick() {
        paidResetPageProgressDialog()
    }

    fun onBackClick(){
        binding.flAbacus.removeAllViews()
        binding.relAbacus.hide()
        goBack()
    }

    override fun onPause() {
        super.onPause()
        tickerChannel.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (setDetail != null){
            startAbacus()
//            if (setDetail?.show_time_setting == true){
//                Log.e("jigarLogs","show_time_setting resume")
//                startTimer()
//            }
        }
    }

    private var tickerChannel = ticker(delayMillis = 1000, initialDelayMillis = 0)
    private fun startTimer() {
        tickerChannel = ticker(delayMillis = 1000, initialDelayMillis = 0)
        launch {
            for (event in tickerChannel) {
                total_sec++

                CoroutineScope(Dispatchers.Main).launch {
//                    setId?.let {
//                        appViewModel.updateSetTimer(it,total_sec)
//                    }
                    setProgress?.let{
                        it.total_time_taken = total_sec.toInt()
                        appViewModel.insertSetProgress(listOf(it))
                    }
                    val time = DateTimeUtils.displayDurationHourMinSec(total_sec)
                    binding.txtTimer.text = time
                    binding.txtTimer.show()
                }

            }
            tickerChannel.cancel()
        }
    }

    private fun startAbacus() {
        if(requireContext().isNetworkAvailable){
            CoroutineScope(Dispatchers.Main).launch {
                setId?.let {
                    setDetail = appViewModel.getSetDetail(it)
                    setProgress = appViewModel.getSetProgress(it)
                    if (setDetail != null){
                        isStepByStep = setDetail?.answer_setting == AppConstants.apiParams.answerSettingStepByStep
                        if (setDetail?.answer_setting == AppConstants.apiParams.answerFormalAnswer){
                            binding.tvAns.text = "?"
                        }else{
                            binding.tvAns.text = ""
                        }
                        if (!setDetail?.hint.isNullOrEmpty()){
                            val list : ArrayList<String> = arrayListOf()
                            val json = JSONArray(setDetail?.hint)
                            for (i in 0 until json.length()) {
                                list.add(json.getString(i))
                            }

                            hintPage = list.joinToString("<br/>")
                        }

                        list_abacus = appViewModel.getAbacus(it)
                        if (list_abacus.isNotNullOrEmpty()){
                            if (setProgress != null){
                                if (setProgress?.is_set_completed == false){
                                    list_abacus.indexOfFirst { it.id == setProgress?.latest_abacus_id }.also {
                                        if (it > -1){
                                            current_pos = it
                                        }
                                    }
                                }
                            }

                            startAbacusNow()
                            if (setDetail?.show_time_setting == true){
                                if (setProgress != null){
                                    if (setProgress?.is_set_completed == false){
                                        total_sec = (setProgress?.total_time_taken?:0).toLong()
                                    }
                                }
                                startTimer()
                            }
                        }
                    }else{
                        mNavController.navigateUp()
                    }
                }
            }
        }else{
            notOfflineSupportDialog2()
        }
    }
    private fun notOfflineSupportDialog2() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.no_internet_working),getString(R.string.no_internet)
            ,getString(R.string.continue_working_internet),getString(R.string.no_working_internet), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    if (requireContext().isNetworkAvailable){
                        startAbacusNow()
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

    private fun startAbacusNow() {
        if (list_abacus.lastIndex < current_pos){
            current_pos = 0
        }
        currentAbacus = list_abacus[current_pos]
//        currentAbacus = AbacusData(1,"1","999+111-777")
        abacusType = if (currentAbacus.question.contains("+") || currentAbacus.question.contains("-")){
            AppConstants.extras_Comman.AbacusTypeAdditionSubtraction
        }else if (currentAbacus.question.contains("*",true)){
            AppConstants.extras_Comman.AbacusTypeMultiplication
        }else if (currentAbacus.question.contains("/",true)){
            AppConstants.extras_Comman.AbacusTypeDivision
        }else{
            AppConstants.extras_Comman.AbacusTypeNumber
        }
        when (abacusType) {
            AppConstants.extras_Comman.AbacusTypeAdditionSubtraction -> {
                setDataOfAdditionSubtraction()
            }
            AppConstants.extras_Comman.AbacusTypeMultiplication -> {
                setDataOfMultiplication()
            }
            AppConstants.extras_Comman.AbacusTypeDivision -> {
                setDataOfDivision()
            }
            AppConstants.extras_Comman.AbacusTypeNumber -> {
                binding.txtTitle.invisible()
                binding.tvAns.text = ""
                binding.tvAns.invisible()
                setDataOfNumber()
            }
            else -> {
                goBack()
            }
        }


        setThemeColor()
    }

    private fun setTempTheme() {
        with(prefManager){
            setCustomParam(AppConstants.Settings.TheamTempView,getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default))

            val theme = prefManager.getCustomParam(AppConstants.Settings.TheamTempView,AppConstants.Settings.theam_Default)
            themeContent = DataProvider.findAbacusThemeType(requireContext(),theme,AbacusBeadType.AbacusPrecise)

            adapterAdditionSubtraction = AbacusAdditionSubtractionTypeAdapter(arrayListOf(), this@AbacusCalculationFragment, true,themeContent)
            adapterMultiplication = AbacusMultiplicationTypeAdapter(arrayListOf(), true,themeContent)
            adapterDivision = AbacusDivisionTypeAdapter(arrayListOf(), true,themeContent)
        }
    }

    private fun setDataOfNumber() {
        binding.txtTitle.text = String.format(getString(R.string.abacus_no),(current_pos + 1))
        binding.tvAnsNumber.text = ""
        abacus_type = 0
        list_abacus_main = AbacusProvider.getHashMapList(currentAbacus)
        number = (list_abacus_main[0][Constants.Que]?:"0").toLong()
        lifecycleScope.launch {
            delay(500)
            if (isHintSound) {
                speakOut(String.format(resources.getString(R.string.speech_set), " ${number}"))
            }
        }
        val noOfDecimalPlace = 0
        binding.tvAnsNumber.text = number.toString()
        binding.tvAnsNumberWord.text = requireContext().convert(number.toInt())

        binding.relativeQueNumber.show()
        replaceAbacusFragment(abacusColumn, noOfDecimalPlace,number.toString())
    }

    private fun setDataOfDivision() {
        binding.txtTitle.text = String.format(getString(R.string.abacus_no),(current_pos + 1))
        binding.tvAns.text = ""
        binding.tvAns.invisible()
        abacus_type = 2
        list_abacus_main = AbacusProvider.getHashMapList(currentAbacus)
        if (list_abacus_main.size == 2) {
            lifecycleScope.launch {
                delay(500)
                if (isHintSound) {
                    val q1 = " ${(list_abacus_main[0][Constants.Que]?:"0")}"
                    val q2 = " ${(list_abacus_main[1][Constants.Que]?:"0")}"
                    speakOut(String.format(getString(R.string.speak_divide_by),q1,q2))
                }
            }
            binding.cardAbacusQue.show()
            adapterDivision = AbacusDivisionTypeAdapter(list_abacus_main,isStepByStep,themeContent)
            binding.recyclerview.adapter = adapterDivision
            adapterDivision.clearIterationCount()
            val divisor = Integer.valueOf(list_abacus_main[1][Constants.Que]!!)
            val finalAns = adapterDivision.getDivideIterationCount(list_abacus_main[0][Constants.Que]!!,divisor)

            for (i in 1 until adapterDivision.getTotalRequiredIteration()) {
                val data: HashMap<String, String> = HashMap<String, String>()
                data[Constants.Que] = ""
                data[Constants.Sign] = ""
                data[Constants.Hint] = ""
                list_abacus_main.add(data)
            }
            adapterDivision.setDefaultHighlight()
            binding.recyclerview.layoutManager?.requestLayout()
            adapterDivision.notifyDataSetChanged()
            val topPositions = ArrayList<Int>()
            val bottomPositions = ArrayList<Int>()
            val question = list_abacus_main[0][Constants.Que]?:""

            val finalAnsLength = finalAns.toString().length
            val queLength = question.length

            for (i in 0 until abacusColumn) {
                if (i < question.length) {
                    val charAt = question[i] - '1' //convert char to int. minus 1 from question as in abacus 0 item have 1 value.
                    if (charAt >= 0) {
                        if (charAt >= 4) {
                            topPositions.add(i, 0)
                            bottomPositions.add(i, charAt - 5)
                        } else {
                            topPositions.add(i, -1)
                            bottomPositions.add(i, charAt)
                        }
                    } else {
                        topPositions.add(i, -1)
                        bottomPositions.add(i, -1)
                    }
                } else {
                    topPositions.add(i, -1)
                    bottomPositions.add(i, -1)
                }
            }
            val subTop: MutableList<Int> = ArrayList()
            subTop.addAll(topPositions.subList(0, question.length))
            val subBottom: MutableList<Int> = ArrayList()
            subBottom.addAll(bottomPositions.subList(0, question.length))
            for (i in question.indices) {
                topPositions.removeAt(0)
                bottomPositions.removeAt(0)
            }
            topPositions.addAll(subTop)
            bottomPositions.addAll(subBottom)
            val toValue = (adapterDivision.getCurrentSumVal()?:0.0).toInt()
            replaceAbacusFragment(abacusColumn, 0,toValue.toString(),topPositions,bottomPositions) // divide doesn't have decimal places
            //set table
            setTableDataAndVisiblilty(true)
            abacusFragment?.setQuestionAndDividerLength(queLength, finalAnsLength)
        } else {
            setDataOfDivision()
        }
    }

    private fun setDataOfMultiplication() {
        binding.txtTitle.text = String.format(getString(R.string.abacus_no),(current_pos + 1))
        binding.tvAns.text = ""
        binding.tvAns.invisible()
        list_abacus_main = AbacusProvider.getHashMapList(currentAbacus)
        if (list_abacus_main.size == 2) {
            abacus_type = 1
            lifecycleScope.launch {
                delay(500)
                if (isHintSound) {
                    val q1 = " ${(list_abacus_main[0][Constants.Que]?:"0")}"
                    val q2 = " ${(list_abacus_main[1][Constants.Que]?:"0")}"
                    speakOut(String.format(getString(R.string.speak_multiply_by),q1,q2))
                }
            }
            val noOfDecimalPlace = 0
            binding.cardAbacusQue.show()
            binding.recyclerview.adapter = adapterMultiplication
            adapterMultiplication.setData(list_abacus_main, isStepByStep)

            val toValue = (adapterMultiplication.getCurrentSumVal()?:0.0).toInt()
            replaceAbacusFragment(abacusColumn, noOfDecimalPlace,toValue.toString())
            //set table
            setTableDataAndVisiblilty(true)
        } else {
            setDataOfMultiplication()
        }
    }

    private fun setDataOfAdditionSubtraction() {
        abacus_type = 0
        binding.txtTitle.text = String.format(getString(R.string.abacus_no),(current_pos + 1))
        binding.tvAns.text = ""
        binding.tvAns.invisible()

        // TODO jigar set hint
        if (!hintPage.isNullOrEmpty()) {
            if (isDisplayHelpMessage) {
                binding.cardHint.show()
            } else {
                binding.cardHint.hide()
            }
            binding.cardTable.hide()
            binding.relativeTable.hide()
            binding.txtHint.text = HtmlCompat.fromHtml(hintPage!!,HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        val list_abacus_main_temp = AbacusProvider.getHashMapList(currentAbacus)
        if (!list_abacus_main_temp.isNullOrEmpty()){
            if (isHintSound && isStepByStep) {
                lifecycleScope.launch {
                    delay(700)
                    speakOut(String.format(resources.getString(R.string.speech_set), " ${list_abacus_main_temp[0].get(Constants.Que)?:"0".toInt()}"))
                }
            }

            val que = currentAbacus.question
            val resultObject = Calculator().getResult(que,que)
            val answer = CommonUtils.removeTrailingZero(resultObject)
            var noOfDecimalPlace = 0
            var column = 3
            binding.recyclerview.adapter = adapterAdditionSubtraction
            if (answer.toDouble() == answer.toLong().toDouble()) {
                val ans = answer.toLong().toString() + ""
                column = if (ans.length == 1) 3 else ans.length
                noOfDecimalPlace = 0
            } else {
                val ans = answer.toFloat().toString()
                noOfDecimalPlace = ans.length - ans.indexOf(".") - 1
                column = ans.length
            }


            var answerTemp = ""
            val newQue = que.replace("+", "$$+").replace("-", "$$-")
            val list = newQue.split("$$")
            val listNew : ArrayList<Int> = arrayListOf()
            list.map {
                listNew.add(it.toInt())
                if (it.contains("+") || it.contains("-")) {
                    answerTemp += it
                    val resultObject = Calculator().getResult(answerTemp,answerTemp)
                    answerTemp = CommonUtils.removeTrailingZero(resultObject)
                    if (answerTemp.length > column){
                        column = answerTemp.length
                    }
                }else{
                    answerTemp = it
                }
            }

            list_abacus_main.clear()
            list_abacus_main.addAll(list_abacus_main_temp)
            binding.cardAbacusQue.show()

//            val questionFormulaStep : ArrayList<ExamProvider.QuestionFormulaStep> = arrayListOf()
//            list_abacus.map {
////                    val ques = que
//                    val ques = it.question
////                    val ques = "7+5-1"
//                    val newQue1 = ques.replace("+", "$$+").replace("-", "$$-")
//                    val list1 = newQue1.split("$$")
//                    val listNew1 : ArrayList<Int> = arrayListOf()
//                    list1.map {
//                        listNew1.add(it.toInt())
//    //                    result.forEach {
//    //                        Log.e("jigarSteps","steps = "+"Value: ${it.value}, Formula: ${it.formulaUsed ?: "None"}, Type: ${it.formulaType.description}")
//    //                    }
//                    }
//                    val result = detectFormulaSteps(initial = 0, steps = listNew1)
//                    questionFormulaStep.add(ExamProvider.QuestionFormulaStep(ques,result))
//            }
//            Log.e("jigarFormula","questionFormulaStep = "+Gson().toJson(questionFormulaStep).replace("\u003d","="))


            adapterAdditionSubtraction.setData(list_abacus_main, isStepByStep)
            replaceAbacusFragment(abacusColumn, noOfDecimalPlace,list_abacus_main_temp[0].get(Constants.Que)?:"0")
        }
    }
    // purchased and reset page progress
    private fun paidResetPageProgressDialog() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.txt_reset_page),getString(R.string.txt_reset_page_alert)
            ,getString(R.string.yes_i_m_sure),getString(R.string.no_please_continue), icon = R.drawable.ic_alert,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    resetProgressConfirm()
                }
                override fun onConfirmationNoClick(bundle: Bundle?) = Unit
            })
    }
    // reset page progress and start from 1st abacus
    private fun resetProgressConfirm() {
        current_pos = 0
        // TODO jigar
//        updateToFirebase()
//        removeSum()
        startAbacus()
    }
    private fun goBack() {
        mNavController.navigateUp()
    }
    private fun setTableDataAndVisiblilty(isSetDirectionFor1stTime : Boolean = false) {
        if (isStepByStep){
            if (list_abacus_main.size >= 2) {
                if (abacus_type == 1) {
                    val spannableString = adapterMultiplication.getTable(requireContext(),themeContent)

//                    if (isStepByStep && !isSetDirectionFor1stTime){
//                        val toValue = (adapterMultiplication.getCurrentSumVal()?:0.0).toInt()
//                        Log.e("jigarLogs","toValue = "+toValue)
//                        if (toValue > 0){
//                            abacusFragment?.clearDirection()
//                            abacusFragment?.addDirection(toValue)
//                        }else{
//                            abacusFragment?.hideDirection()
//                        }
//                    }
                    if (!TextUtils.isEmpty(spannableString)) {
                        if (isHideTable) {
                            binding.cardHint.hide()
                        } else {
                            binding.cardHint.show()
                        }
                        binding.cardTable.hide()
                        binding.relativeTable.hide()
                        binding.txtHint.text = spannableString
                        binding.txtHintTable.text = spannableString
                    }
                } else if (abacus_type == 2) {
//                    if (isStepByStep && !isSetDirectionFor1stTime){
//                        val toValue = (adapterDivision.getCurrentSumVal()?:0.0).toInt()
//                        if (toValue > 0){
//                            abacusFragment?.clearDirection()
//                            abacusFragment?.addDirection(toValue)
//                        }else{
//                            abacusFragment?.hideDirection()
//                        }
//                    }
                    if (isHideTable) {
                        binding.cardHint.hide()
                    } else {
                        binding.cardHint.show()
                    }
                    binding.cardTable.hide()
                    binding.relativeTable.hide()

                    binding.txtHint.text = ViewUtils.getTable(
                        requireContext(), list_abacus_main[1][Constants.Que]!!.toInt(),
                        adapterDivision.currentTablePosition, themeContent
                    )
                    binding.txtHintTable.text = ViewUtils.getTable(
                        requireContext(), list_abacus_main[1][Constants.Que]!! .toInt(),
                        adapterDivision.currentTablePosition, themeContent
                    )
                } else {
                    binding.cardHint.hide()
                    binding.cardTable.hide()
                    binding.relativeTable.hide()
                }
            } else {
                binding.cardHint.hide()
                binding.cardTable.hide()
                binding.relativeTable.hide()
            }
        }else{
            binding.cardHint.hide()
            binding.cardTable.hide()
            binding.relativeTable.hide()
        }
    }
    override fun onCheckHint(hintOld: String?, que: String?, Sign: String?) {
        // if answer with abacus tools then return
        // TODO jigar direction
        if (isHintSound) {
            val q1 = " ${que?:"0"}"
//            val q1 = " ${requireContext().convert((que?:"0").toInt())}"
            if (Sign == "-") {
                speakOut(String.format(resources.getString(R.string.speech_set_minus), q1))
            } else {
                speakOut(String.format(resources.getString(R.string.speech_set_plus), q1))
            }
        }
//        val sumVal: Long = adapterAdditionSubtraction.getCurrentSumVal()!!.toLong()
//        if (isStepByStep){
//            abacusFragment?.clearDirection()
//            abacusFragment?.addDirection(sumVal.toInt())
//        }

        speek_hint = ""
        val currentStep = adapterAdditionSubtraction.getCurrentStep()
        var hint: String? = null
        if (!currentAbacus.hint.isNullOrEmpty()){
            val json = JSONObject(currentAbacus.hint)
            if (json.has("$currentStep")){
                hint = json.getString("$currentStep")
            }
        }
        if (!hint.isNullOrEmpty()) {
            if (isDisplayHelpMessage) {
                binding.cardHint.show()
            } else {
                binding.cardHint.hide()
            }
            binding.cardTable.hide()
            binding.relativeTable.hide()
            binding.txtHint.text = hint
            val temp_hint = hint
            speek_hint = temp_hint.replace("-", " "+getString(R.string.minus)+" ").replace("+", " "+getString(R.string.plus)+" ")
                .replace("=", " "+getString(R.string.equal_to)+" ")
            lifecycleScope.launch {
                delay(1500)
                if (isHintSound) {
                    speakOut(String.format(resources.getString(R.string.speech_formula_for), " $speek_hint"))
                }
            }
        } else if (hintPage.isNullOrEmpty()){
            binding.cardHint.hide()
            binding.cardTable.hide()
            binding.relativeTable.hide()
        }

    }

    private fun replaceAbacusFragment(column: Int, noOfDecimalPlace: Int,firstQuestionForDirection : String,topPositions : ArrayList<Int> = arrayListOf(),bottomPositions : ArrayList<Int> = arrayListOf()) {
        try {
            this.abacusTotalColumns = column
            this.noOfDecimalPlace = noOfDecimalPlace
            if (abacusFragment == null){
                val que = if (isStepByStep){
                    firstQuestionForDirection
                }else{
                    "0"
                }
                abacusFragment = HalfAbacusSubFragment().newInstance(abacusTotalColumns, noOfDecimalPlace, abacus_type,themeContent,setDetail?.answer_setting == AppConstants.apiParams.answerFormalAnswer,que,topPositions,bottomPositions)
            }else{
                abacusFragment?.setSelectedPositions(topPositions,bottomPositions)

                if (isStepByStep){
                    abacusFragment?.hideDirection()
                    lifecycleScope.launch {
                        delay(Constants.DELAY_DIRECTION)
                        abacusFragment?.clearDirection()
                        abacusFragment?.addDirection(firstQuestionForDirection.toInt())
                    }
                }
            }
            binding.flAbacus.show()
            if (abacusFragment != null){
                abacusFragment?.setOnAbacusValueChangeListener(this)
                val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flAbacus,abacusFragment!!,abacusFragment?.javaClass?.simpleName)
                transaction.commit()
            }
            if (!binding.relAbacus.isVisible){
                lifecycleScope.launch {
                    delay(200)
                    binding.relAbacus.show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var previousSum = ""
    override fun onAbacusValueChange(abacusView: View, sum1: Long) {
        if (isMoveNext) {
            goToNextAbacus()
            return
        }

        val abacusValue = sum1.toString()
        var newValue = abacusValue
        if (abacusValue.length != abacusTotalColumns){
            for (i in 1..(abacusTotalColumns - abacusValue.length)) {
                newValue = "0$newValue"
            }
        }
        val sb = StringBuilder(newValue)
        sb.insert(7, ".")
        newValue = sb.toString()

        val splitResult = newValue.split(".")
        if (splitResult.size == 2){
            val value1 = splitResult[0].toLong()
            val value2 = splitResult[1]
            val sum = if (value2.toLong() > 0){
//                abacusBinding?.tvCurrentVal?.text = "$value1.$value2"
                "$value1.$value2"
            }else{
//                abacusBinding?.tvCurrentVal?.text = "$value1"
                "$value1"
            }
            if (abacus_type == 0) {
                if (abacusType == AppConstants.extras_Comman.AbacusTypeNumber) {
                    val sumVal = number
                    if (sum == (sumVal.toInt()).toString()) {
                        if (setDetail?.answer_setting != AppConstants.apiParams.answerFormalAnswer){
                            abacusFragment?.clearDirection()
                            onAbacusValueSubmit(sumVal)
                        }
                    }else{
                        if (!binding.tvAns.isVisible && previousSum.isNotEmpty()){
                            abacusFragment?.clearDirection()
                            abacusFragment?.addDirection(sumVal.toInt())
                            if (value2.toInt() > 0){
                                abacusFragment?.addDirectionDivisor(0,value2.toInt())
                            }
                        }
                    }
                } else if (adapterAdditionSubtraction.itemCount > 0) {
                    if (adapterAdditionSubtraction.getCurrentSumVal() != null) {
                        val sumVal: Long = adapterAdditionSubtraction.getCurrentSumVal()!!.toLong()
                        if (isStepByStep) {
                            if (sum == (sumVal.toInt()).toString()) {
                                adapterAdditionSubtraction.goToNextStep()
                                if (adapterAdditionSubtraction.getCurrentStep() == list_abacus_main.size) {
                                    val finalans = (adapterAdditionSubtraction.getFinalSumVal()?:0.0).toLong()
                                    if (setDetail?.answer_setting != AppConstants.apiParams.answerFormalAnswer){
                                        onAbacusValueSubmit(finalans)
                                    }
                                }
                                val sumValNew: Long = adapterAdditionSubtraction.getCurrentSumVal()!!.toLong()
                                abacusFragment?.clearDirection()
                                abacusFragment?.addDirection(sumValNew.toInt())
                            }else{
                                if (!binding.tvAns.isVisible && previousSum.isNotEmpty()){
                                    abacusFragment?.clearDirection()
                                    abacusFragment?.addDirection(sumVal.toInt())
                                    if (value2.toInt() > 0){
                                        abacusFragment?.addDirectionDivisor(0,value2.toInt())
                                    }
                                }
                            }
                        } else {
                            val finalans = (adapterAdditionSubtraction.getFinalSumVal()?:0.0).toLong()
                            if (sum == (finalans.toInt()).toString()) {
                                if (setDetail?.answer_setting != AppConstants.apiParams.answerFormalAnswer){
                                    onAbacusValueSubmit(finalans)
                                }
                            }
                        }
                    }
                }
            } else if (abacus_type == 1) {
                if (adapterMultiplication.getCurrentSumVal() != null) {
                    val sumVal: Long = adapterMultiplication.getCurrentSumVal()!!.toLong()
                    if (isStepByStep) {
                        if (sum == (sumVal.toInt()).toString()) {
                            adapterMultiplication.goToNextStep()
                            setTableDataAndVisiblilty()
                        }
                        val curVal = adapterMultiplication.getCurrentStep()
                        val finalans = (adapterMultiplication.getFinalSumVal()?:0.0).toLong()
                        if (sum == (finalans.toInt()).toString() && curVal[0]!! >= adapterMultiplication.getItem(0)[Constants.Que]!!
                                .length - 1 && curVal[1]!! >= adapterMultiplication.getItem(1)[Constants.Que]!!.length - 1
                        ) {
                            adapterMultiplication.clearHighlight()
                            if (setDetail?.answer_setting != AppConstants.apiParams.answerFormalAnswer){
                                onAbacusValueSubmit(finalans)
                            }
                        }else{
                            val sumValNew = (adapterMultiplication.getCurrentSumVal()?:0.0).toInt()
                            if (!binding.tvAns.isVisible && previousSum.isNotEmpty()){
                                abacusFragment?.clearDirection()
                                abacusFragment?.addDirection(sumValNew.toInt())
                                if (value2.toInt() > 0){
                                    abacusFragment?.addDirectionDivisor(0,value2.toInt())
                                }
                            }
                        }
                    } else {
                        val finalans = (adapterMultiplication.getFinalSumVal()?:0.0).toLong()
                        if (sum == (finalans.toInt()).toString()) {
                            if (setDetail?.answer_setting != AppConstants.apiParams.answerFormalAnswer){
                                onAbacusValueSubmit(finalans)
                            }
                        }
                    }
                }
            } else if (abacus_type == 2) {
                var remainQuestion = newValue.replace(".","").takeLast(6).trimStart('0')
                if (remainQuestion.isEmpty()){
                    remainQuestion = "0"
                }
                val answers = newValue.replace(".","").take(7).trimStart('0')
                if (adapterDivision.getCurrentSumVal() != null) {
                    if (isStepByStep) {
                        val nextDivider = adapterDivision.getNextDivider()
                        if (adapterDivision.getCurrentSumVal().toString() == answers && nextDivider.toString() == remainQuestion){
                            adapterDivision.goToNextStep()
                            setTableDataAndVisiblilty()
                            val toValue = (adapterDivision.getCurrentSumVal()?:0.0).toInt()
                            if (toValue > 0){
                                abacusFragment?.clearDirection()
                                abacusFragment?.addDirection(toValue)
                            }
                        }else{
                            if (!binding.tvAns.isVisible && previousSum.isNotEmpty()){
                                abacusFragment?.clearDirection()
                                abacusFragment?.addDirection((adapterDivision.getCurrentSumVal()?:0L).toInt())
                                if (adapterDivision.getCurrentSumVal().toString() == answers){
                                    abacusFragment?.addDirectionDivisor(adapterDivision.getNextDivider().toInt(),remainQuestion.toInt())
                                }
                            }
                        }
                        val finalans = adapterDivision.getFinalSumVal()!!.toLong()
                        if (answers == finalans.toString() && adapterDivision.isLastStep() && (remainQuestion.isEmpty() || remainQuestion == "0")) {
                            adapterDivision.clearHighlight()
                            if (setDetail?.answer_setting != AppConstants.apiParams.answerFormalAnswer){
                                onAbacusValueSubmit(finalans)
                            }
                        }
                    } else {
                        val finalAns = (adapterDivision.getFinalSumVal()?:0.0).toLong()
                        if (answers == (finalAns.toInt()).toString()) {
                            if (setDetail?.answer_setting != AppConstants.apiParams.answerFormalAnswer){
                                onAbacusValueSubmit(finalAns)
                            }
                        }
                    }
                }
            }
        }else{
            mNavController.navigateUp()
        }

        previousSum = abacusValue
    }

    private fun goToNextAbacus() {
        if(requireContext().isNetworkAvailable){
            moveToNext()
            isMoveNext = false
        }else{
            notOfflineSupportDialog()
        }
    }

    private fun resetData() {
        if (abacusType != AppConstants.extras_Comman.AbacusTypeNumber) {
            binding.tvAns.text = ""
            binding.tvAns.invisible()
            binding.cardHint.hide()
            when (abacus_type) {
                0 -> {
                    adapterAdditionSubtraction.reset()
                    //                    setDataOfAdditionSubtraction()
                }
                1 -> {
                    adapterMultiplication.reset()
                    //                    setDataOfMultiplication()
                }
                2 -> {
                    shouldResetAbacus = true
                    adapterDivision.reset()
                    //                    setDataOfDivision()
                }
            }
        }
    }
    private fun moveToNext() {
        CoroutineScope(Dispatchers.Main).launch {
            setId?.let {
                val submitExamRequest = SubmitAllExamDataRequest()
                with(submitExamRequest) {
                    type = setDetail?.answer_setting
                    if (current_pos >= list_abacus.lastIndex){
                        tickerChannel.cancel()
                        if(requireContext().isNetworkAvailable) {
                            if (setDetail?.answer_setting == AppConstants.apiParams.answerFormalAnswer){
                                set_id = it
                                list_abacus = appViewModel.getAbacus(it)
                                mCalculator = Calculator()
                                var rightAnswerCount = 0
                                if (setDetail?.show_time_setting == true){
                                    total_time_taken = total_sec.toInt()
                                }
                                no_of_questions = list_abacus.size
                                val questionsList : ArrayList<Any> = arrayListOf()
                                list_abacus.map {
                                    val question = it.question
                                    val resultObject = mCalculator.getResult(question,question)
                                    val correctAns = CommonUtils.removeTrailingZero(resultObject)
                                    val isRightAnswer = (correctAns == it.userAnswer)
                                    if (isRightAnswer){
                                        rightAnswerCount++
                                    }
                                    questionsList.add(QuestionDataRequest(question,it.userAnswer,isRightAnswer))
                                }
                                no_of_right_answers = rightAnswerCount
                                questions = questionsList
                                examViewModel.submitAllExam(submitExamRequest)
                            }else{
                                if (setProgress == null){
                                    retry_count = 1
                                }else if(setProgress?.is_set_completed == true){
                                    retry_count =  (setProgress?.retry_count?:0)+1
                                }else{
                                    retry_count = setProgress?.retry_count?:1
                                }
                                if (setDetail?.show_time_setting == true){
                                    total_time_taken = total_sec.toInt()
                                }
                                is_set_completed = true
                                abacus_id = null
                                set_id = it
                                examViewModel.submitAllExam(submitExamRequest)
                            }
                        }else{
                            CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.no_internet_working),getString(R.string.no_internet)
                                ,getString(R.string.continue_working_internet),getString(R.string.no_working_internet), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
                                clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                                    override fun onConfirmationYesClick(bundle: Bundle?) {
                                        if (!requireContext().isNetworkAvailable) {
                                            showToast(R.string.still_no_internet)
                                        }
                                        moveToNext()
                                    }
                                    override fun onConfirmationNoClick(bundle: Bundle?){
                                        mNavController.navigateUp()
                                    }
                                })
                        }
                    }else{
                        resetData()
                        current_pos++
                        var retryCounts = 1
                        if (setProgress == null){
                            retryCounts = 1
                            setProgress = SetProgress(it,list_abacus[current_pos].id,false)
                        }else if(setProgress?.is_set_completed == true){
                            retryCounts =  (setProgress?.retry_count?:0)+1
                            setProgress = SetProgress(it,list_abacus[current_pos].id,false)
                        }else{
                            retryCounts = setProgress?.retry_count?:1
                            setProgress?.latest_abacus_id = list_abacus[current_pos].id
                        }
                        setProgress?.retry_count = retryCounts
                        if (current_pos > 0 && (current_pos % 5 == 0)){
                            retry_count = retryCounts
                            if (setDetail?.answer_setting != AppConstants.apiParams.answerFormalAnswer){
                                if (setDetail?.show_time_setting == true){
                                    total_time_taken = total_sec.toInt()
                                }
                                is_set_completed = false
                                abacus_id = list_abacus[current_pos].id
                                set_id = it
                                examViewModel.submitAllExam(submitExamRequest)
                            }
                        }
                        if (setDetail?.show_time_setting == true){
                            setProgress?.total_time_taken = total_sec.toInt()
                        }
                        setProgress?.let{
                            appViewModel.insertSetProgress(listOf(it))
                        }
                        startAbacusNow()
                    }
                }
            }
        }
    }

    private fun notOfflineSupportDialog() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.no_internet_working),getString(R.string.no_internet)
            ,getString(R.string.continue_working_internet),getString(R.string.no_working_internet), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    if (requireContext().isNetworkAvailable){
                        moveToNext()
                        isMoveNext = false
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

    override fun onAbacusSubmitValue(userAnswer : String) {
        CoroutineScope(Dispatchers.Main).launch {
            appViewModel.updateUserAnswer(currentAbacus.id,userAnswer)
            isMoveNext = true
            resetOrMoveNext()
        }
    }

    override fun onAbacusValueSubmit(sum: Long) {
        if (setDetail?.answer_setting != AppConstants.apiParams.answerFormalAnswer) {
            when (abacus_type) {
                0 -> {
                    val sumVal: Long
                    if (abacusType == AppConstants.extras_Comman.AbacusTypeNumber) {
                        abacus_number = sum.toInt()
                        binding.tvAns.show()
                        makeAutoRefresh()
                    } else {
                        sumVal = adapterAdditionSubtraction.getFinalSumVal()!!.toLong()
                        if (sumVal == sum) {
                            binding.tvAns.text = sum.toInt().toString()
                        } else {
                            binding.tvAns.text = sum.toString()
                        }
                        binding.tvAns.show()
                        abacusFragment?.hideDirection()
                        makeAutoRefresh()
                    }
                }

                1 -> {
                    val sumVal: Float = adapterMultiplication.getFinalSumVal()!!.toFloat()
                    if (sumVal == (sum.toInt().toString()).toFloat()) {
                        binding.tvAns.text = sum.toInt().toString()
                    } else {
                        binding.tvAns.text = sum.toString()
                    }
                    binding.tvAns.show()
                    abacusFragment?.hideDirection()
                    makeAutoRefresh()
                }

                2 -> {
                    var sumStr: String = sum.toInt().toString()
                    binding.tvAns.text = sumStr
                    binding.tvAns.show()
                    abacusFragment?.hideDirection()
                    makeAutoRefresh()
                }
            }
        }
    }

    private fun makeAutoRefresh() {
        if (isStepByStep && isAutoRefresh){
            abacusFragment?.resetButtonEnable(false)
            lifecycleScope.launch {
                delay(1500)
                abacusFragment?.resetButtonEnable(true)
                onAbacusValueDotReset()
            }
        }else{
            abacusFragment?.resetButtonEnable(true)
            abacusFragment?.showResetToContinue(true)
        }
    }

    override fun onAbacusValueDotReset() {
        previousSum = ""
        if (abacusType == AppConstants.extras_Comman.AbacusTypeNumber) {
            if (binding.tvAnsNumber.text.toString() == abacus_number.toString()) {
                isMoveNext = true
            }
        } else {
            if (!TextUtils.isEmpty(binding.tvAns.text.toString())) {
                isMoveNext = true
            }
        }
        resetOrMoveNext()
    }

    private fun resetOrMoveNext() {
        // speak 1st question
        if (!isMoveNext){
            lifecycleScope.launch {
                delay(500)
                val text = when (abacusType) {
                    AppConstants.extras_Comman.AbacusTypeDivision -> {
                        val q1 = " ${(list_abacus_main[0][Constants.Que]?:"0")}"
                        val q2 = " ${(list_abacus_main[1][Constants.Que]?:"0")}"
                        String.format(getString(R.string.speak_divide_by),q1,q2)
                    }
                    AppConstants.extras_Comman.AbacusTypeMultiplication -> {
                        val q1 = " ${(list_abacus_main[0][Constants.Que]?:"0")}"
                        val q2 = " ${(list_abacus_main[1][Constants.Que]?:"0")}"
                        String.format(getString(R.string.speak_multiply_by),q1,q2)

                    }
                    AppConstants.extras_Comman.AbacusTypeAdditionSubtraction -> {
                        if (isStepByStep){
                            binding.cardHint.hide()
                        }
                        val q1 = " ${(list_abacus_main[0][Constants.Que]?:"0")}"
                        String.format(resources.getString(R.string.speech_set), q1)
                    }
                    else -> { // number
                        String.format(resources.getString(R.string.speech_set), " ${number}")
                    }
                }
                if (isHintSound) {
                    speakOut(text)
                }
            }
        }
        reset()
    }

    private fun reset() {
        abacusFragment?.resetAbacus()
        binding.tvAns.text = ""
        binding.tvAns.invisible()
        if (abacusType != AppConstants.extras_Comman.AbacusTypeNumber) {
            when (abacus_type) {
                0 -> {
                    adapterAdditionSubtraction.reset()
                }
                1 -> {
                    adapterMultiplication.reset()
                }
                2 -> {
                    adapterDivision.reset()
                }
            }
        }

    }

    // abacus ui rules
    @SuppressLint("SuspiciousIndentation")
    private fun setRightAbacusRules() {
        val paramsAbacus = binding.relAbacus.layoutParams as RelativeLayout.LayoutParams
        paramsAbacus.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        paramsAbacus.addRule(RelativeLayout.CENTER_VERTICAL)
        binding.relAbacus.layoutParams = paramsAbacus

        val paramscardTable = binding.cardTable.layoutParams as RelativeLayout.LayoutParams
        paramscardTable.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        paramscardTable.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        binding.cardTable.layoutParams = paramscardTable

        val paramsTitleHand = binding.txtTitleHand.layoutParams as RelativeLayout.LayoutParams
        paramsTitleHand.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        paramsTitleHand.addRule(RelativeLayout.START_OF, R.id.relAbacus)
        binding.txtTitleHand.layoutParams = paramsTitleHand
        lifecycleScope.launch {
            delay(400)
            binding.txtTitleHand.show()
        }

        val paramsRelativeTable = binding.relativeTable.layoutParams as RelativeLayout.LayoutParams
        paramsRelativeTable.addRule(RelativeLayout.BELOW, R.id.cardTable)
        paramsRelativeTable.addRule(RelativeLayout.LEFT_OF, R.id.relAbacus)
        binding.relativeTable.layoutParams = paramsRelativeTable

        val paramsQuestionsMain = binding.linearQuestions.layoutParams as RelativeLayout.LayoutParams
        paramsQuestionsMain.addRule(RelativeLayout.CENTER_VERTICAL)
        paramsQuestionsMain.addRule(RelativeLayout.LEFT_OF, R.id.relAbacus)
        binding.linearQuestions.layoutParams = paramsQuestionsMain

        val paramsQueNumber = binding.relativeQueNumber.layoutParams as RelativeLayout.LayoutParams
        paramsQueNumber.addRule(RelativeLayout.ALIGN_PARENT_START)
        paramsQueNumber.addRule(RelativeLayout.CENTER_VERTICAL)
        binding.relativeQueNumber.layoutParams = paramsQueNumber

        binding.imgRightAbacusTools.hide()
        binding.imgLeftAbacusTools.hide()

        val paramsQuestions = binding.cardAbacusQue.layoutParams as RelativeLayout.LayoutParams
        paramsQuestions.addRule(RelativeLayout.ALIGN_PARENT_START)
        paramsQuestions.addRule(RelativeLayout.CENTER_VERTICAL)
        binding.cardAbacusQue.layoutParams = paramsQuestions


        val paramsHint = binding.cardHint.layoutParams as RelativeLayout.LayoutParams
        paramsHint.addRule(RelativeLayout.CENTER_VERTICAL)
        paramsHint.addRule(RelativeLayout.END_OF, R.id.cardAbacusQue)
        binding.cardHint.layoutParams = paramsHint
    }
    private fun setLeftAbacusRules() {
//        val paramsTitle = binding.txtTitle.layoutParams as RelativeLayout.LayoutParams
//        paramsTitle.addRule(RelativeLayout.CENTER_HORIZONTAL)
//        paramsTitle.addRule(RelativeLayout.END_OF, R.id.relAbacus)
//        binding.txtTitle.layoutParams = paramsTitle
//        lifecycleScope.launch {
//            delay(400)
//            binding.txtTitle.show()
//        }

        val paramsTitleHand = binding.txtTitleHand.layoutParams as RelativeLayout.LayoutParams
        paramsTitleHand.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        paramsTitleHand.addRule(RelativeLayout.END_OF, R.id.relAbacus)
        binding.txtTitleHand.layoutParams = paramsTitleHand
        lifecycleScope.launch {
            delay(400)
            binding.txtTitleHand.show()
        }

        val paramsAbacus = binding.relAbacus.layoutParams as RelativeLayout.LayoutParams
        paramsAbacus.addRule(RelativeLayout.ALIGN_PARENT_START)
        paramsAbacus.addRule(RelativeLayout.CENTER_VERTICAL)
        binding.relAbacus.layoutParams = paramsAbacus

        val paramscardTable = binding.cardTable.layoutParams as RelativeLayout.LayoutParams
        paramscardTable.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        paramscardTable.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        binding.cardTable.layoutParams = paramscardTable

        val paramsRelativeTable = binding.relativeTable.layoutParams as RelativeLayout.LayoutParams
        paramsRelativeTable.addRule(RelativeLayout.BELOW, R.id.cardTable)
        paramsRelativeTable.addRule(RelativeLayout.END_OF, R.id.relAbacus)
        binding.relativeTable.layoutParams = paramsRelativeTable

        val paramsQuestionsMain = binding.linearQuestions.layoutParams as RelativeLayout.LayoutParams
        paramsQuestionsMain.addRule(RelativeLayout.ALIGN_PARENT_END)
        paramsQuestionsMain.addRule(RelativeLayout.CENTER_VERTICAL)
        paramsQuestionsMain.addRule(RelativeLayout.END_OF, R.id.relAbacus)
        binding.linearQuestions.layoutParams = paramsQuestionsMain

        val paramsQueNumber = binding.relativeQueNumber.layoutParams as RelativeLayout.LayoutParams
        paramsQueNumber.addRule(RelativeLayout.ALIGN_PARENT_END)
        paramsQueNumber.addRule(RelativeLayout.CENTER_VERTICAL)
        binding.relativeQueNumber.layoutParams = paramsQueNumber

        binding.imgRightAbacusTools.hide()
        binding.imgLeftAbacusTools.hide()

        val paramsQuestions = binding.cardAbacusQue.layoutParams as RelativeLayout.LayoutParams
        paramsQuestions.addRule(RelativeLayout.ALIGN_PARENT_END)
        paramsQuestions.addRule(RelativeLayout.CENTER_VERTICAL)
        binding.cardAbacusQue.layoutParams = paramsQuestions

        val paramsHint = binding.cardHint.layoutParams as RelativeLayout.LayoutParams
        paramsHint.addRule(RelativeLayout.CENTER_VERTICAL)
        paramsHint.addRule(RelativeLayout.START_OF, R.id.cardAbacusQue)
        binding.cardHint.layoutParams = paramsHint
    }

}