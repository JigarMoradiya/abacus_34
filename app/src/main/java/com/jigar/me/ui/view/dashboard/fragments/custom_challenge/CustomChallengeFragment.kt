package com.jigar.me.ui.view.dashboard.fragments.custom_challenge

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.local.data.CustomChallengeData
import com.jigar.me.data.local.data.CustomChallengeQuestion
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.databinding.FragmentAbacusExerciseBinding
import com.jigar.me.databinding.FragmentCustomChallengeBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.abacus.AbacusMasterBeadShiftListener
import com.jigar.me.ui.view.base.abacus.AbacusMasterCompleteListener
import com.jigar.me.ui.view.base.abacus.AbacusMasterSound
import com.jigar.me.ui.view.base.abacus.AbacusMasterView
import com.jigar.me.ui.view.base.abacus.OnAbacusValueChangeListener
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CCMCompleteBottomSheet
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.viewmodel.ExamViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.convert
import com.jigar.me.utils.extensions.dp
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.invisible
import com.jigar.me.utils.extensions.isNetworkAvailable
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class CustomChallengeFragment : BaseFragment(), AbacusMasterBeadShiftListener,
    OnAbacusValueChangeListener, CCMCompleteBottomSheet.CCMCompleteDialogInterface {

    private var typeAds = ""
    private val examViewModel by viewModels<ExamViewModel>()
    private lateinit var binding: FragmentCustomChallengeBinding
    private lateinit var mNavController: NavController
    private var totalQuestion = 5
    private var minQuestion = 1
    private var maxQuestion = 3
    private var secGap = 4
    private var isVoiceOn = false
    private var isShowNumber = true
    private var isShowWord = false
    private var position = 0
    private var currentNumber = 0
    private var challengeData : CustomChallengeData? = null
    private var challengeQuestionList : MutableList<CustomChallengeQuestion> = arrayListOf()
    private var themeContent : AbacusContent? = null
    private var textToSpeech: TextToSpeech? = null

    private var currentSumVal = 0L
    private var listKeyboardAnswer = arrayListOf<String>()
    private var isResetRunning = false
    private var abacusBinding: FragmentAbacusExerciseBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arg = CustomChallengeFragmentArgs.fromBundle(requireArguments())
        totalQuestion = arg.totalQuestion
        minQuestion = arg.minQuestion
        maxQuestion = arg.maxQuestion
        secGap = arg.secGap
        isVoiceOn = arg.isVoiceOn
        isShowNumber = arg.isShowNumber
        isShowWord = arg.isShowWord
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCustomChallengeBinding.inflate(inflater, container, false)
        setNavigationGraph()
        init()
        clickListener()
        setAbacus()
        ads()
        return binding.root
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
        val isAnswerTrue = binding.txtAnswer.text.equals((challengeData?.answer?:"").toString())
        CCMCompleteBottomSheet.showPopup(requireActivity(),challengeData,isAnswerTrue,this@CustomChallengeFragment)
        newInterstitialAdCompleteCCM()
    }

    private fun ads() {
        with(prefManager){
            if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" &&
                getCustomParam(AppConstants.AbacusProgress.Ads, "") == "Y" &&
                getCustomParam(AppConstants.Purchase.Purchase_All, "") != "Y" &&
                getCustomParam(AppConstants.Purchase.Purchase_Ads, "") != "Y") {
                showAMBannerAds(binding.adView,getString(R.string.banner_ad_unit_id_ccm))
            }
        }
    }
    private fun ttsInit() {
        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status != TextToSpeech.ERROR) {

                textToSpeech?.let { CommonUtils.applySpeechSettings(prefManager, it) }
                setChallengeNumber()
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener(){
                    override fun onStart(p0: String?) = Unit
                    override fun onDone(p0: String?) {
                        if (!p0.equals("complete")){
                            goToNextNumber()
                        }
                    }
                    override fun onError(p0: String?) = Unit
                })

            }
        }

    }
    private fun speak(txt: String,id : String) {
        lifecycleScope.launch {
            if (textToSpeech != null){
                textToSpeech?.speak(txt, TextToSpeech.QUEUE_FLUSH, null, id)
            }else{
                txtToSpeechInit()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (textToSpeech != null) {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    private fun init() {
        val theme = prefManager.getCustomParam(AppConstants.Settings.TheamTempView,AppConstants.Settings.theam_Default)
        themeContent = DataProvider.findAbacusThemeType(requireContext(),theme, AbacusBeadType.None)
        themeContent?.resetBtnColor8?.let {
            binding.tvNumber.setTextColor(ContextCompat.getColor(requireContext(), it))
            binding.txtAnswer.setTextColor(ContextCompat.getColor(requireContext(),it))
            themeContent?.dividerColor1?.let {it2 ->
                val finalColor40 = CommonUtils.mixTwoColors(ContextCompat.getColor(requireContext(),it2), ContextCompat.getColor(requireContext(),it), 0.40f)
                binding.tvNumberWord.setTextColor(finalColor40)
            }
        }

        if (isVoiceOn){
            binding.imgListen.show()
        }
        if (isShowWord){
            binding.tvNumberWord.show()
        }
        if (isShowNumber){
            binding.tvNumber.show()
        }

        startCCM()
    }

    private fun startCCM() {
        if(requireContext().isNetworkAvailable){
            challengeData = DataProvider.generateChallengeModeQuestion(totalQuestion,minQuestion,maxQuestion)
            challengeQuestionList = challengeData?.questions?: arrayListOf()
            if (challengeQuestionList.isNotNullOrEmpty()){
                if (isVoiceOn) {
                    ttsInit()
                } else {
                    setChallengeNumber()
                }
            }
        }else{
            notOfflineSupportDialog()
        }
    }

    private fun notOfflineSupportDialog() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.no_internet_working),getString(R.string.no_internet)
            ,getString(R.string.continue_working_internet),getString(R.string.no_working_internet), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    if (requireContext().isNetworkAvailable){
                        startCCM()
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

    // complete dialog listened
    override fun ccmCompleteClose() {
        typeAds = "close"
        startAgain()
    }

    override fun ccmCompleteContinue() {
        if (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"").equals("Y",true)){
            typeAds = "start_again"
            startAgain()
        }else{
            CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.ccm_subcribe_title),getString(R.string.ccm_subcribe_msg)
                ,getString(R.string.yes_i_want_to_purchase),getString(R.string.no_purchase_later), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
                clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                    override fun onConfirmationYesClick(bundle: Bundle?) {
                        goToInAppPurchase()
                    }
                    override fun onConfirmationNoClick(bundle: Bundle?){
                        typeAds = "close"
                        startAgain()
                    }
                })
        }
    }

    private fun startAgain() {

        if (typeAds == "start_again"){
            showLoading()
            listKeyboardAnswer.clear()
            binding.txtAnswer.text = "0"
            abacusBinding?.tvCurrentVal?.text = "0"
            lifecycleScope.launch {
                delay(1000)
                hideLoading()

//                resetClick()
                setAbacus()
                binding.linearQuestion.show()
                binding.linearKeyboard.invisible()
                binding.linearAbacus.invisible()
                position = 0
                startCCM()
            }
        }else{
            binding.cardBack.performClick()
        }

    }

    private fun clickListener() {
        binding.cardBack.onClick  { mNavController.navigateUp() }
        binding.btnCheckAnswer.onClick {
            submitAnswers()
        }
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

    }

    private fun submitAnswers() {
        val isAnswerTrue = binding.txtAnswer.text.equals((challengeData?.answer?:"").toString())

        val submitExamRequest = SubmitAllExamDataRequest()
        with(submitExamRequest){
            type = AppConstants.ExamType.type_CCM
            total_set_of_question = 1
            total_numbers_in_set = totalQuestion
            gap_between_two_question = secGap
            question_min_length = minQuestion
            question_max_length = maxQuestion
            is_question_speak = isVoiceOn
            is_question_show_in_number = isShowNumber
            is_question_show_in_word = isShowWord
            no_of_right_answers = if (isAnswerTrue){1}else{0}

            val questionsList : ArrayList<Any> = arrayListOf()
            challengeData?.let{
                questionsList.add(QuestionDataRequest(it.fullQuestion,binding.txtAnswer.text.toString(),isAnswerTrue))
            }
            questions = questionsList
        }
        examViewModel.submitAllExam(submitExamRequest)
    }

    private fun setChallengeNumber() {
        if (position < challengeQuestionList.size){
            currentNumber = challengeQuestionList[position].question
            if (isShowNumber){
                val text : String = when (challengeQuestionList[position].sign) {
                    "-" -> {
                        "- "
                    }
                    "+" -> {
                        "+ "
                    }
                    else -> {
                        ""
                    }
                }
                binding.tvNumber.text = text+currentNumber.toString()
            }
            val text : String = when (challengeQuestionList[position].sign) {
                "-" -> {
                    requireContext().getString(R.string.minus)+" "
                }
                "+" -> {
                    requireContext().getString(R.string.plus)+" "
                }
                else -> {
                    ""
                }
            }
            val word = text+requireContext().convert(currentNumber)
            if (isShowWord){
                binding.tvNumberWord.text = word
            }
            if (isVoiceOn){
                val word1 : String = when (challengeQuestionList[position].sign) {
                    "-" -> {
                        String.format(requireContext().getString(R.string.minus_value), currentNumber)
                    }
                    "+" -> {
                        String.format(requireContext().getString(R.string.plus_value), currentNumber)
                    }
                    else -> {
                        String.format(requireContext().getString(R.string.txt_set_only), currentNumber)
                    }
                }
                speak(word1,position.toString())
            }
            if (!isVoiceOn) {
                goToNextNumber()
            }

        }else{
            speak(getString(R.string.set_your_answer),"complete")
            binding.linearQuestion.hide()
            binding.linearKeyboard.show()
            binding.linearAbacus.show()
        }

    }

    private fun goToNextNumber() {
        lifecycleScope.launch {
            delay((secGap*1000L))
            position++
            setChallengeNumber()
        }
    }


    // abacus answer view
    private fun setAbacus() {
        binding.linearAbacus.removeAllViews()
        abacusBinding = FragmentAbacusExerciseBinding.inflate(layoutInflater, null, false)
        abacusBinding?.linearDot9?.hide()
        abacusBinding?.linearDot8?.hide()
        binding.linearAbacus.addView(abacusBinding?.root)

        abacusBinding?.ivReset?.onClick {
            resetClick()
        }

        themeContent?.abacusFrame135?.let { abacusBinding?.rlAbacusMain?.setBackgroundResource(it) }
        themeContent?.dividerColor1?.let { abacusBinding?.ivDivider?.setBackgroundColor(ContextCompat.getColor(requireContext(),it)) }
        themeContent?.resetBtnColor8?.let {
            abacusBinding?.imgDot4?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.imgDot7?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)

            abacusBinding?.imgDot1?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.imgDot1?.layoutParams?.width = 3.dp
            abacusBinding?.imgDot1?.layoutParams?.height = 3.dp

            abacusBinding?.ivReset?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.ivRight?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.ivLeft?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
        }

        abacusBinding?.abacusTop?.setNoOfRowAndBeads(0, 7, 1,AbacusBeadType.CustomeChallenge,6)
        abacusBinding?.abacusBottom?.setNoOfRowAndBeads(0, 7, 4,AbacusBeadType.CustomeChallenge,6)

        abacusBinding?.abacusTop?.onBeadShiftListener = this
        abacusBinding?.abacusBottom?.onBeadShiftListener = this

        lifecycleScope.launch {
            delay(300)
            abacusBinding?.relAbacus?.show()
        }
    }

    private fun resetClick() {
        if (!isResetRunning) {
            AbacusMasterSound.playResetSound(requireContext())
            isResetRunning = true
            abacusBinding?.ivReset?.animate()?.setDuration(200)
                ?.translationYBy((abacusBinding?.ivReset?.height!! / 2).toFloat())?.withEndAction {
                    abacusBinding?.ivReset?.animate()?.setDuration(200)
                        ?.translationYBy((-abacusBinding?.ivReset?.height!! / 2).toFloat())?.withEndAction {
                            isResetRunning = false
                        }?.start()
                }?.start()
            onAbacusValueDotReset()
        }
    }

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
        val value = sum.toString()
        listKeyboardAnswer.clear()
        for (i in value.indices){
            listKeyboardAnswer.add(value[i].toString())
        }
        binding.txtAnswer.text = sum.toInt().toString()
        abacusBinding?.tvCurrentVal?.text = sum.toInt().toString()
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

    private fun addKeyboardValue(value : String){
        if (listKeyboardAnswer.size < 9){
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
        val totalLength = 9
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


    private fun newInterstitialAdCompleteCCM() {
        if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" &&
            prefManager.getCustomParam(AppConstants.AbacusProgress.Ads,"") == "Y" &&
            (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"") != "Y" && // purchase not
                    prefManager.getCustomParam(AppConstants.Purchase.Purchase_Ads,"") != "Y")){
            val isAdmob = prefManager.getCustomParamBoolean(AppConstants.AbacusProgress.isAdmob,true)
            val adUnit = getString(R.string.interstitial_ad_unit_id_ccm)
            if (isAdmob){
                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(requireContext(), adUnit, adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        // Show the ad if it's ready. Otherwise toast and reload the ad.
                        interstitialAd.show(requireActivity())
                    }

                })
            }else{
                val adRequest = AdManagerAdRequest.Builder().build()
                AdManagerInterstitialAd.load(requireContext(),adUnit, adRequest, object : AdManagerInterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                    }

                    override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                        // Show the ad if it's ready. Otherwise toast and reload the ad.
                        interstitialAd.show(requireActivity())
                    }
                })
            }
        }
    }
}