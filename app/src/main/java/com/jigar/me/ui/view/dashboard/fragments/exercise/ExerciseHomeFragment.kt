package com.jigar.me.ui.view.dashboard.fragments.exercise

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.R
import com.jigar.me.data.local.data.*
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.data.model.data.Statistics
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.databinding.FragmentAbacusExerciseBinding
import com.jigar.me.databinding.FragmentExerciseHomeBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.abacus.*
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.view.confirm_alerts.dialogs.ExerciseCompleteDialog
import com.jigar.me.ui.view.dashboard.fragments.exercise.adapter.ExerciseAdditionSubtractionAdapter
import com.jigar.me.ui.view.dashboard.fragments.exercise.adapter.ExerciseLevelPagerAdapter
import com.jigar.me.ui.viewmodel.ExamViewModel
import com.jigar.me.utils.*
import com.jigar.me.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ExerciseHomeFragment : BaseFragment(), AbacusMasterBeadShiftListener, OnAbacusValueChangeListener,
    ExerciseLevelPagerAdapter.OnItemClickListener,
    ExerciseCompleteDialog.ExerciseCompleteDialogInterface {
    private val examViewModel by viewModels<ExamViewModel>()
    private lateinit var binding: FragmentExerciseHomeBinding
    private var abacusBinding: FragmentAbacusExerciseBinding? = null
    private lateinit var mNavController: NavController
    private var valuesAnswer: Int = -1
    private var currentSumVal = 0L
    private var totalTimeLeft = 0L
    private var isPurchased = false
    private var themeContent : AbacusContent? = null
    private var theme = AppConstants.Settings.theam_Default
    private var isResetRunning = false
    private lateinit var exerciseLevelPagerAdapter: ExerciseLevelPagerAdapter
    private lateinit var exerciseAdditionSubtractionAdapter: ExerciseAdditionSubtractionAdapter
    private var listExerciseAdditionSubtractionQuestion = arrayListOf<String>()
    private var listKeyboardAnswer = arrayListOf<String>()
    private var listExerciseAdditionSubtraction : MutableList<ExerciseList> = arrayListOf()
    private var exercisePosition = 0
    private var currentChildData : ExerciseLevelDetail? = null
    private var currentParentData: ExerciseLevel? = null
    private lateinit var mCalculator: Calculator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentExerciseHomeBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initViews()
        initListener()
        ads()
        return binding.root
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    private fun ads() {
        if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" // local
            && prefManager.getCustomParam(AppConstants.AbacusProgress.Ads,"") == "Y" && // if yes in firebase
            (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"") != "Y" // if not purchased
                    && prefManager.getCustomParam(AppConstants.Purchase.Purchase_Ads,"") != "Y")) {
            showAMBannerAds(binding.adView,getString(R.string.banner_ad_unit_id_exercise))
        }
    }
    private fun initViews() {
        if (prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_left_hand, true)){
            setLeftAbacusRules()
        }else{
            setRightAbacusRules()
        }
        mCalculator = Calculator()
        with(prefManager){
            isPurchased = getCustomParam(AppConstants.Purchase.Purchase_All,"") == "Y"
            if (isPurchased){
                setCustomParam(AppConstants.Settings.TheamTempView,getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default))
            }else{
                if (getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default).contains(AppConstants.Settings.theam_Default,true)){
                    setCustomParam(AppConstants.Settings.TheamTempView,getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default))
                }else{
                    setCustomParam(AppConstants.Settings.TheamTempView,AppConstants.Settings.theam_Default)
                }
            }
            theme = getCustomParam(AppConstants.Settings.TheamTempView,AppConstants.Settings.theam_Default)

        }

        themeContent = DataProvider.findAbacusThemeType(requireContext(),theme,AbacusBeadType.Exercise)
        themeContent?.dividerColor1?.let{
            val finalColor = CommonUtils.mixTwoColors(ContextCompat.getColor(requireContext(),R.color.white), ContextCompat.getColor(requireContext(),it), 0.85f)
            binding.cardMain.backgroundTintList = ColorStateList.valueOf(finalColor)
        }
        themeContent?.resetBtnColor8?.let{
            binding.indicatorPager.selectedDotColor = ContextCompat.getColor(requireContext(),it)
            binding.txtTimer.setTextColor(ContextCompat.getColor(requireContext(),it))
            binding.txtQueLabel.setTextColor(ContextCompat.getColor(requireContext(),it))
        }

        exerciseAdditionSubtractionAdapter = ExerciseAdditionSubtractionAdapter(listExerciseAdditionSubtractionQuestion,themeContent)
        binding.recyclerviewExercise.adapter = exerciseAdditionSubtractionAdapter

        exerciseLevelPagerAdapter = ExerciseLevelPagerAdapter(DataProvider.getExerciseList(requireContext()),prefManager,this@ExerciseHomeFragment,themeContent)
        binding.viewPager.adapter = exerciseLevelPagerAdapter
        binding.indicatorPager.attachToPager(binding.viewPager)
        setAbacus()
    }
    private fun initListener() {
        binding.imgEarse.onClick {
            if (listKeyboardAnswer.isNotNullOrEmpty()){
                listKeyboardAnswer.removeAt(listKeyboardAnswer.lastIndex)
                setKeyboardAnswer()
            }
        }
        binding.txtAllClear.onClick {
            if (listKeyboardAnswer.isNotNullOrEmpty()){
                listKeyboardAnswer.clear()
                setKeyboardAnswer()
            }
        }
        binding.cardBack.onClick { exerciseLeaveAlert() }
        binding.txt0.onClick { addKeyboardValue("0") }
        binding.txt1.onClick { addKeyboardValue("1") }
        binding.txt2.onClick { addKeyboardValue("2") }
        binding.txt3.onClick { addKeyboardValue("3") }
        binding.txt4.onClick { addKeyboardValue("4") }
        binding.txt5.onClick { addKeyboardValue("5") }
        binding.txt6.onClick { addKeyboardValue("6") }
        binding.txt7.onClick { addKeyboardValue("7") }
        binding.txt8.onClick { addKeyboardValue("8") }
        binding.txt9.onClick { addKeyboardValue("9") }

        binding.txtNext.onClick {
            if(requireContext().isNetworkAvailable){
                if (binding.tvAnswer.text.toString().isNotEmpty() && binding.tvAnswer.text.toString() != "0"){
                    listExerciseAdditionSubtraction[exercisePosition].userAnswer = binding.tvAnswer.text.toString().toInt()
                }
                abacusBinding?.ivReset?.performClick()
                if (exercisePosition < listExerciseAdditionSubtraction.lastIndex){
                    exercisePosition++
                    setQuestions()
                }else{
                    tickerChannel.cancel()
                    openCompleteDialog()
                }
            }else{
                notOfflineSupportDialog()
            }

        }
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
        PlaySound.play(requireContext(), PlaySound.number_puzzle_win)
        newInterstitialAdCompleteExercise()
    }

    private fun addKeyboardValue(value : String){
        if (listKeyboardAnswer.size < 7){
            if (value == "0"){
                if (listKeyboardAnswer.isNotEmpty()){
                    listKeyboardAnswer.add(value)
                }
            }else{
                listKeyboardAnswer.add(value)
            }
            setKeyboardAnswer()
        }
    }

    private fun setKeyboardAnswer() {
        AbacusMasterSound.playTap(requireContext())
        setNumber()
    }

    private fun setNumber() {
        val questionTemp = if (listKeyboardAnswer.isNotEmpty()){
            listKeyboardAnswer.joinToString("")
        }else{
            "0"
        }
        val topPositions = ArrayList<Int>()
        val bottomPositions = ArrayList<Int>()
        val totalLength = 7
        val remainLength = totalLength - questionTemp.length
        var zero = ""
        for (i in 1..remainLength){
            zero += "0"
        }
        val question = zero+questionTemp
        for (i in 0 until if (totalLength == 1) 2 else totalLength) {
            if (i < question.length) {
                val charAt = question[i] - '1' //convert char to int. minus 1 from question as in abacuse 0 item have 1 value.
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

        setSelectedPositions(topPositions, bottomPositions,null)
    }

    private fun setSelectedPositions(
        topSelectedPositions: ArrayList<Int>,
        bottomSelectedPositions: ArrayList<Int>,
        setPositionCompleteListener: AbacusMasterCompleteListener?
    ) {
        if (isAdded) {
            //app was crashing if position set before update no of row count. so added this delay.
            abacusBinding?.abacusBottom?.post {
                abacusBinding?.abacusTop?.setSelectedPositions(topSelectedPositions,setPositionCompleteListener)
                abacusBinding?.abacusBottom?.setSelectedPositions(bottomSelectedPositions,setPositionCompleteListener)
            }
        }
    }

    override fun exerciseCompleteCloseDialog() {
        val currentPos = binding.viewPager.currentItem
        val list = exerciseLevelPagerAdapter.listData
        exerciseLevelPagerAdapter = ExerciseLevelPagerAdapter(list,prefManager,this@ExerciseHomeFragment)
        binding.viewPager.adapter = exerciseLevelPagerAdapter
        binding.viewPager.currentItem = currentPos

        binding.linearExerciseAddSub.hide()
        binding.linearTime.hide()
        binding.linearLevel.show()
        binding.cardBack.show()
    }
    override fun onExerciseStartClick() {
        if (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"").equals("Y",true)){
            startInit()
        }else {
            getStatisticData(object : Companion.StatisticApiResponseListener{
                override fun statisticApiData(data: JsonObject?) {
                    val response = Gson().fromJson(data, Statistics::class.java)
                    if (response.EXERCISE?.can_give_exam == true){
                        startInit()
                    }else{
                        canNotAccess()
                    }
                }
            })
        }
    }
    private fun canNotAccess() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.exercise_subcribe_title),getString(R.string.exercise_subcribe_msg)
            ,getString(R.string.yes_i_want_to_purchase),getString(R.string.no_purchase_later), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    goToInAppPurchase()
                }
                override fun onConfirmationNoClick(bundle: Bundle?) = Unit
            })
    }

    private fun startInit() {
        if(requireContext().isNetworkAvailable){
            abacusBinding?.ivReset?.performClick()
            lifecycleScope.launch {
                delay(400)
                val parentData = exerciseLevelPagerAdapter.listData[binding.viewPager.currentItem]
                val childData = parentData.list[parentData.selectedChildPos]
                currentChildData = childData
                currentParentData = parentData

                exercisePosition = 0
                binding.linearExerciseAddSub.show()
                binding.linearTime.show()
                binding.linearLevel.hide()
//                binding.cardBack.hide()
                when (parentData.id) {
                    "1" -> {
                        binding.recyclerviewExercise.show()
                        binding.txtMultiplication.hide()
                        listExerciseAdditionSubtraction = DataProvider.generateAdditionSubExercise(childData)
                        setQuestions()
                    }
                    "2" -> {
                        binding.recyclerviewExercise.hide()
                        binding.txtMultiplication.show()
                        listExerciseAdditionSubtraction = DataProvider.generateMultiplicationExercise(childData)
                        setQuestions()
                    }
                    "3" -> {
                        binding.recyclerviewExercise.hide()
                        binding.txtMultiplication.show()
                        listExerciseAdditionSubtraction = DataProvider.generateDivisionExercise(childData)

                        setQuestions()
                    }
                }
                totalTimeLeft = TimeUnit.MINUTES.toSeconds(childData.totalTime.toLong())
//                totalTimeLeft = TimeUnit.MINUTES.toMillis(1L)
                startTimer()
            }
        }else{
            notOfflineSupportDialog()
        }
    }

    private var tickerChannel = ticker(delayMillis = 1000, initialDelayMillis = 0)
    private fun startTimer() {
        tickerChannel = ticker(delayMillis = 1000, initialDelayMillis = 0)
        launch {
            for (event in tickerChannel) {
                CoroutineScope(Dispatchers.Main).launch {
                    val time = DateTimeUtils.displayDurationHourMinSec(totalTimeLeft)
                    binding.txtTimer.text = time
                }
                totalTimeLeft--
                if (totalTimeLeft == 0L){
                    CoroutineScope(Dispatchers.Main).launch {
                        openCompleteDialog()
                    }
                    break
                }
            }
            tickerChannel.cancel()
        }
    }

    private fun openCompleteDialog() {
        binding.txtNext.isEnabled = false
        binding.txtTimer.text = "00:00"

        val submitExamRequest = SubmitAllExamDataRequest()
        with(submitExamRequest){
            type = AppConstants.ExamType.type_Exercise
            category = currentParentData?.title
            when (currentParentData?.id) {
                "1" -> {
                    label = "${currentChildData?.digits} Digits ${currentChildData?.queLines} Lines X ${currentChildData?.totalQue} Questions"
                }
                "2" -> {
                    label = "The answer is ${currentChildData?.digits} Digits X ${currentChildData?.totalQue} Questions"
                }
                "3" -> {
                    label = "The dividend is MAX ${currentChildData?.digits} Digits X ${currentChildData?.totalQue} Questions"
                }
            }
            val maxTime = TimeUnit.SECONDS.convert((currentChildData?.totalTime?:0).toLong(), TimeUnit.MINUTES).toInt()
            allowed_max_time = maxTime
            total_time_taken = maxTime - totalTimeLeft.toInt()
            no_of_questions = (currentChildData?.totalQue?:0)
            listExerciseAdditionSubtraction.filter { it.userAnswer == it.answer }.also { no_of_right_answers = it.size }
            val questionsList : ArrayList<Any> = arrayListOf()
            listExerciseAdditionSubtraction.map {
                val question = it.question
                val resultObject = mCalculator.getResult(question,question)
                val correctAns = CommonUtils.removeTrailingZero(resultObject)
                if (it.userAnswer == -1){
                    questionsList.add(QuestionDataRequest(it.question,"",(correctAns == it.userAnswer.toString())))
                }else{
                    questionsList.add(QuestionDataRequest(it.question,it.userAnswer.toString(),(correctAns == it.userAnswer.toString())))
                }

            }
            questions = questionsList
        }
        examViewModel.submitAllExam(submitExamRequest)
    }

    override fun onPause() {
        super.onPause()
        tickerChannel.cancel()
    }

    override fun onResume() {
        super.onResume()
        continueExercise()
    }

    private fun continueExercise() {
        if (binding.linearExerciseAddSub.isVisible){
            startTimer()
        }
    }

    private fun setQuestions() {
        if (listExerciseAdditionSubtraction.lastIndex >= exercisePosition){
            binding.txtNext.isEnabled = true
            valuesAnswer = listExerciseAdditionSubtraction[exercisePosition].answer
            binding.txtQueLabel.text = "Q".plus((exercisePosition+1))

            if (currentParentData?.id == "1"){
                val question = listExerciseAdditionSubtraction[exercisePosition].question.replace("+"," +").replace("-"," -")
                val list = question.split(" ")
                listExerciseAdditionSubtractionQuestion.clear()
                list.map {
                    listExerciseAdditionSubtractionQuestion.add(it)
                }
                exerciseAdditionSubtractionAdapter.currentStep = 0
                exerciseAdditionSubtractionAdapter.notifyDataSetChanged()
            }else if (currentParentData?.id == "2"){
                binding.txtMultiplication.text = listExerciseAdditionSubtraction[exercisePosition].question.replace("x"," x ").plus(" = ?")
            }else{
                binding.txtMultiplication.text = listExerciseAdditionSubtraction[exercisePosition].question.replace("/"," ÷ ").plus(" = ?")
            }

        }
    }

    private fun resetClick() {
        if (!isResetRunning) {
            AbacusMasterSound.playResetSound(requireContext())
            isResetRunning = true
//            abacusBinding?.ivReset?.y = 0f
            abacusBinding?.ivReset?.animate()?.setDuration(200)
                ?.translationYBy((abacusBinding?.ivReset?.height!! / 2).toFloat())?.withEndAction {
                    abacusBinding?.ivReset?.animate()?.setDuration(200)
                        ?.translationYBy((-abacusBinding?.ivReset?.height!! / 2).toFloat())?.withEndAction {
                            isResetRunning = false
                        }?.start()
                }?.start()
            if (!binding.linearExerciseAddSub.isVisible){
                onAbacusValueDotReset()
            }
        }
    }

    private fun setAbacus() {
        binding.linearAbacus.removeAllViews()
        abacusBinding = FragmentAbacusExerciseBinding.inflate(layoutInflater, null, false)
        abacusBinding?.linearDot9?.hide()
        abacusBinding?.linearDot8?.hide()
        binding.linearAbacus.addView(abacusBinding?.root)

        abacusBinding?.ivReset?.onClick {
            if (binding.linearExerciseAddSub.isVisible){
                binding.txtAllClear.performClick()
                resetClick()
            }else{
                if (abacusBinding?.tvCurrentVal?.text?.toString() != "0"){
                    resetClick()
                }
            }

        }

        themeContent?.abacusFrame135?.let { abacusBinding?.rlAbacusMain?.setBackgroundResource(it) }
        themeContent?.dividerColor1?.let { abacusBinding?.ivDivider?.setBackgroundColor(ContextCompat.getColor(requireContext(),it)) }
        themeContent?.resetBtnColor8?.let {
            abacusBinding?.imgDot1?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.imgDot4?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.imgDot7?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.ivReset?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.ivRight?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.ivLeft?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
        }

        abacusBinding?.abacusTop?.setNoOfRowAndBeads(0, 7, 1,AbacusBeadType.Exercise)
        abacusBinding?.abacusBottom?.setNoOfRowAndBeads(0, 7, 4,AbacusBeadType.Exercise)

        abacusBinding?.abacusTop?.onBeadShiftListener = this
        abacusBinding?.abacusBottom?.onBeadShiftListener = this

        lifecycleScope.launch {
            delay(300)
            abacusBinding?.relAbacus?.show()
        }
    }

    // abacus Bead Shift Listener
    override fun onBeadShift(abacusView: AbacusMasterView, rowValue: IntArray) {
        val singleBeadWeight = abacusView.singleBeadValue
        var accumulator = 0L
        when (abacusView.id) {
            R.id.abacusTop -> if (abacusBinding?.abacusBottom?.engine != null) {
                val bottomVal = abacusBinding?.abacusBottom?.engine!!.getValue()
                var i = 0
                while (i < rowValue.size) {
                    accumulator *= 10
                    val rval = rowValue[i]
                    if (rval > -1) accumulator += rval * singleBeadWeight
                    i++
                }
                val intSumVal = bottomVal + accumulator
                currentSumVal = intSumVal
                onAbacusValueChange(abacusView, currentSumVal)
            }
            R.id.abacusBottom -> if (abacusBinding?.abacusTop?.engine != null) {
                val topVal = abacusBinding?.abacusTop?.engine!!.getValue()
                var i = 0
                while (i < rowValue.size) {
                    accumulator *= 10
                    val rval = rowValue[i]
                    if (rval > -1) accumulator += rval * singleBeadWeight
                    i++
                }
                val intSumVal = topVal + accumulator
                currentSumVal = intSumVal
                onAbacusValueChange(abacusView, currentSumVal)
            }
        }
    }

    override fun onAbacusValueChange(abacusView: View, sum: Long) {
        lifecycleScope.launch {

            if (binding.linearExerciseAddSub.isVisible){
                val value = sum.toString()
                listKeyboardAnswer.clear()
                for (i in value.indices){
                    listKeyboardAnswer.add(value[i].toString())
                }
                binding.tvAnswer.text = sum.toInt().toString()
                abacusBinding?.tvCurrentVal?.text = sum.toInt().toString()
            }else{
                abacusBinding?.tvCurrentVal?.text = sum.toInt().toString()
            }
        }
    }

    override fun onAbacusValueSubmit(sum: Long) {

    }

    override fun onAbacusValueDotReset() {
        resetAbacus()
    }

    private fun resetAbacus() {
        abacusBinding?.abacusTop?.reset()
        abacusBinding?.abacusBottom?.reset()
    }

    // exercise leave listener
    fun exerciseLeaveAlert() {
        if (binding.linearExerciseAddSub.isVisible){
            CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.leave_exercise_alert),getString(R.string.leave_exercise_msg)
                ,getString(R.string.yes_i_m_sure),getString(R.string.no_please_continue), icon = R.drawable.ic_alert,
                clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                    override fun onConfirmationYesClick(bundle: Bundle?) {
                        goToMainView()
                    }
                    override fun onConfirmationNoClick(bundle: Bundle?) = Unit
                })
        }else{
            mNavController.navigateUp()
        }

    }

    private fun goToMainView() {
        binding.linearExerciseAddSub.hide()
        binding.linearTime.hide()
        binding.linearLevel.show()
        binding.cardBack.show()
        tickerChannel.cancel()
    }

    private fun newInterstitialAdCompleteExercise() {
        if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" &&
            prefManager.getCustomParam(AppConstants.AbacusProgress.Ads,"") == "Y" &&
            (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"") != "Y" && // purchase not
                    prefManager.getCustomParam(AppConstants.Purchase.Purchase_Ads,"") != "Y")
        ){
            showLoading()
            val isAdmob = prefManager.getCustomParamBoolean(AppConstants.AbacusProgress.isAdmob,true)
            val adUnit = getString(R.string.interstitial_ad_unit_id_exercise)
            if (isAdmob){
                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(requireContext(), adUnit, adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        hideLoading()
                        showCompleteDialog()
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        hideLoading()
                        // Show the ad if it's ready. Otherwise toast and reload the ad.
                        interstitialAd.show(requireActivity())
                        lifecycleScope.launch {
                            delay(400)
                            showCompleteDialog()
                        }
                    }
                })
            }else{
                val adRequest = AdManagerAdRequest.Builder().build()
                AdManagerInterstitialAd.load(requireContext(),adUnit, adRequest, object : AdManagerInterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        hideLoading()
                        showCompleteDialog()
                    }

                    override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                        hideLoading()
                        // Show the ad if it's ready. Otherwise toast and reload the ad.
                        interstitialAd.show(requireActivity())
                        lifecycleScope.launch {
                            delay(400)
                            showCompleteDialog()
                        }
                    }
                })
            }

        }else{
            showCompleteDialog()
        }

    }

    private fun showCompleteDialog() {
        if (ExerciseCompleteDialog.alertdialog?.isShowing != true){
            ExerciseCompleteDialog.showPopup(requireContext(),listExerciseAdditionSubtraction,prefManager,currentParentData,currentChildData,this@ExerciseHomeFragment)
        }
    }

    private fun notOfflineSupportDialog() {
        tickerChannel.cancel()
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.no_internet_working),getString(R.string.no_internet)
            ,getString(R.string.continue_working_internet),getString(R.string.no_working_internet), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    if (requireContext().isNetworkAvailable){
                        continueExercise()
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


    private fun setRightAbacusRules() { // default UI
        with(binding){
            txtQueLabel.setPadding(70.dp,0,0,0)
            cardMain.setBackgroundResource(R.drawable.bg_right_curved_30)
            cardBack.cardElevation = resources.getDimension(R.dimen.card_elevation8)
            val set = ConstraintSet()
            set.clone(conParent)
            set.clear(cardMain.id,ConstraintSet.START)
            set.connect(cardMain.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)

            set.clear(linearAbacus.id,ConstraintSet.END)
            set.connect(linearAbacus.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16.dp)

            set.clear(linearTime.id,ConstraintSet.START)
            set.connect(linearTime.id, ConstraintSet.START, cardMain.id, ConstraintSet.END, 16.dp)

            set.applyTo(conParent)
        }
    }

    private fun setLeftAbacusRules() {
        with(binding){
            txtQueLabel.setPadding(24.dp,0,0,0)
            cardBack.cardElevation = resources.getDimension(R.dimen.card_elevation)
            cardMain.setBackgroundResource(R.drawable.bg_left_curved_30)
            val set = ConstraintSet()
            set.clone(conParent)
            set.clear(cardMain.id,ConstraintSet.START)
            set.connect(cardMain.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)

            set.clear(linearAbacus.id,ConstraintSet.END)
            set.connect(linearAbacus.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16.dp)

            set.clear(linearTime.id,ConstraintSet.START)
            set.connect(linearTime.id, ConstraintSet.END, cardMain.id, ConstraintSet.START, 16.dp)

            set.applyTo(conParent)
        }
    }

}