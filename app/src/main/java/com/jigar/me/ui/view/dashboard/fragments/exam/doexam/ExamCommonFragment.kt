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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.gson.Gson
import com.jigar.me.MyApplication
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.BeginnerExamPaper
import com.jigar.me.data.local.data.BeginnerExamQuestionType
import com.jigar.me.data.local.data.DataObjectsSize
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.local.data.ExamProvider
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.data.model.dbtable.exam.ExamHistory
import com.jigar.me.databinding.FragmentExamCommanBinding
import com.jigar.me.databinding.LayoutAbacusExamBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.abacus.AbacusMasterSound
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

@AndroidEntryPoint
class ExamCommonFragment : BaseFragment(), ExamCompleteDialog.TestCompleteDialogInterface{

    private lateinit var mBinding: FragmentExamCommanBinding
    private val examViewModel by viewModels<ExamViewModel>()
    private lateinit var mNavController: NavController

    private var listExam: List<BeginnerExamPaper> = ArrayList()
    private var currentQuestionPos = 0
    private var totalWrong = 0
    private var correctAns = ""
    private var theme = ""
    private var examLevel = AppConstants.ExamType.exam_Level_Beginner
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
        if (isNumberExam){examForList.add(AppConstants.ExamType.exam_Type_Number)}
        if (isAdditionExam){examForList.add(AppConstants.ExamType.exam_Type_Addition)}
        if (isSubtractionExam){examForList.add(AppConstants.ExamType.exam_Type_Subtraction)}
        if (isMultiplicationExam){examForList.add(AppConstants.ExamType.exam_Type_Multiplication)}
        if (isDivisionExam){examForList.add(AppConstants.ExamType.exam_Type_Division)}

        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentExamCommanBinding.inflate(inflater, container, false)
        setNavigationGraph()
        init()
        clickListener()
        ads()
        return mBinding.root
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
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
//        val minutes = total_sec / 60
//        val seconds = total_sec % 60
//        val totalTime = String.format("%d:%02d", minutes, seconds)
//        CoroutineScope(Dispatchers.Main).launch{
//            apiViewModel.saveExamResultDB(ExamHistory(0,total_sec,examLevel, arrayListOf(), listExam,theme = theme, examFor = examForList))
//        }
//        ExamCompleteDialog.showPopup(requireActivity(),totalTime,"0",totalWrong.toString(),right.toString(),listExam.size.toString(),this,prefManager)
        val submitExamRequest = SubmitAllExamDataRequest()
        with(submitExamRequest){
            type = AppConstants.ExamType.type_Exam
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
                            AppConstants.ExamType.exam_Que_type_abacus
                        }else{
                            AppConstants.ExamType.exam_Que_type_object
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
                            AppConstants.ExamType.exam_Que_type_abacus
                        }else{
                            AppConstants.ExamType.exam_Que_type_object
                        }
                    }else{
                        ""
                    }
                }else{
                    AppConstants.ExamType.exam_Que_type_question
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

    private fun ads() {
        if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" // local
            && prefManager.getCustomParam(AppConstants.AbacusProgress.Ads,"") == "Y" && // if yes in firebase
            (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"") != "Y" // if not purchased
                    && prefManager.getCustomParam(AppConstants.Purchase.Purchase_Ads,"") != "Y")) {
            showAMBannerAds(mBinding.adView,getString(R.string.banner_ad_unit_id_exam))
        }
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
                mBinding.txtTimer.text = resources.getString(R.string.Time) + " : " + total_sec.secToTimeFormat()
                handler?.postDelayed(this, delay.toLong())
            }
        }
        handler?.postDelayed(runnable!!, delay.toLong())
        setExamPaper()

        // event log
        MyApplication.logEvent(AppConstants.FirebaseEvents.DailyExam, Bundle().apply {
            putString(AppConstants.FirebaseEvents.deviceId, prefManager.getDeviceId())
            putString(AppConstants.FirebaseEvents.DailyExamLevel, examLevel)
        })
    }

    private fun setExamPaper() {
        if (currentQuestionPos >= listExam.size) {
            completePopup()
        } else {
            if (listExam[currentQuestionPos].imageData != null || listExam[currentQuestionPos].isAbacusQuestion == true){
                mBinding.txtHeaderTitle.show()
                mBinding.conObject.show()
                mBinding.conTxt.hide()
                setQuestionObjectOrAbacus()
            }else{
                mBinding.txtHeaderTitle.hide()
                mBinding.conObject.hide()
                mBinding.conTxt.show()
                setQuestionTxt()
            }

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

    private fun setQuestionObjectOrAbacus() {
        mBinding.recyclerviewObjects2.hide()
        mBinding.spaceBetween.hide()
        mBinding.imgSign.hide()

        if (listExam[currentQuestionPos].type == BeginnerExamQuestionType.Count){
            val str = getString(R.string.count_the)+" <b><font color='#E14A4D'>Abacus Beads</font></b>"
            mBinding.txtHeaderTitle.text = HtmlCompat.fromHtml(str,HtmlCompat.FROM_HTML_MODE_COMPACT)

            if (listExam[currentQuestionPos].isAbacusQuestion == true){
                mBinding.linearQuestion.hide()
                mBinding.relAbacus.show()

                mBinding.layoutAbacus1.removeAllViews()
                mBinding.layoutAbacus2.removeAllViews()
                val abacusBinding1 = LayoutAbacusExamBinding.inflate(layoutInflater, null, false)
                mBinding.layoutAbacus1.addView(abacusBinding1.root)
                abacusBinding1.tvCurrentVal.hide()
                if (DataProvider.generateIndex() == 0){
                    abacusBinding1.imgKidLeft.setImageResource(R.drawable.ic_boy_abacus_left)
                    abacusBinding1.imgKidHandLeft.setImageResource(R.drawable.ic_boy_abacus_hand_left)
                }else{
                    abacusBinding1.imgKidLeft.setImageResource(R.drawable.ic_girl_abacus_left)
                    abacusBinding1.imgKidHandLeft.setImageResource(R.drawable.ic_girl_abacus_hand_left)
                }
                abacusBinding1.imgKidLeft.show()
                abacusBinding1.imgKidHandLeft.show()

                abacusBinding1.imgKidRight.hide()
                abacusBinding1.imgKidHandRight.hide()

                mBinding.layoutAbacus1.show()
                mBinding.layoutAbacus2.hide()
                mBinding.imgSign1.hide()
                lifecycleScope.launch {
                    val themeContent = DataProvider.findAbacusThemeType(requireContext(),theme,AbacusBeadType.Exam)
                    themeContent.abacusFrameExam135.let {
                        abacusBinding1.rlAbacusMain.setBackgroundResource(it)
                    }
                    themeContent.dividerColor1.let {
                        abacusBinding1.ivDivider.setBackgroundColor(ContextCompat.getColor(requireContext(),it))
                    }
                    themeContent.resetBtnColor8.let {
                        abacusBinding1.ivReset.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
                    }
                    AbacusUtils.setAbacusColumnTheme(AbacusBeadType.ExamResult,abacusBinding1.abacusTop,abacusBinding1.abacusBottom, column = listExam[currentQuestionPos].value.length)
                    AbacusUtils.setNumber(listExam[currentQuestionPos].value,abacusBinding1.abacusTop,abacusBinding1.abacusBottom, totalLength = listExam[currentQuestionPos].value.length)
                }
            }else{
                mBinding.relAbacus.hide()
                mBinding.linearQuestion.show()
                val str = getString(R.string.count_the)+" <b><font color='#E14A4D'>"+listExam[currentQuestionPos].imageData?.name+"</font></b>"
                mBinding.txtHeaderTitle.text = HtmlCompat.fromHtml(str,HtmlCompat.FROM_HTML_MODE_COMPACT)

                var list1ImageCount = 0
                var list2ImageCount = 0
                val totalCount = listExam[currentQuestionPos].value.toInt()
                if (totalCount > 10){
                    list1ImageCount = totalCount/2
                    list2ImageCount = totalCount - list1ImageCount
                }else if (totalCount < 6){
                    list1ImageCount = totalCount
                }else{
                    list1ImageCount = 5
                    list2ImageCount = totalCount - 5
                }
                val size = if (list1ImageCount < 5 && list2ImageCount == 0){
                    DataObjectsSize.Large
                }else if (list1ImageCount == 5 && list2ImageCount == 0){
                    DataObjectsSize.Medium
                }else{
                    DataObjectsSize.Small
                }

                if (list1ImageCount > 0){
                    objectListAdapter1= ObjectListAdapter(list1ImageCount, listExam[currentQuestionPos].imageData,size)
                    mBinding.recyclerviewObjects1.layoutManager = GridLayoutManager(requireContext(),list1ImageCount)
                    mBinding.recyclerviewObjects1.adapter = objectListAdapter1
                }
                if (list2ImageCount > 0){
                    objectListAdapter2= ObjectListAdapter(list2ImageCount, listExam[currentQuestionPos].imageData,size)
                    mBinding.recyclerviewObjects2.layoutManager = GridLayoutManager(requireContext(),list2ImageCount)
                    mBinding.recyclerviewObjects2.adapter = objectListAdapter2
                    mBinding.recyclerviewObjects2.show()
                    mBinding.spaceBetween.show()
                }
            }
        }else if (listExam[currentQuestionPos].type == BeginnerExamQuestionType.Additions || listExam[currentQuestionPos].type == BeginnerExamQuestionType.Subtractions){

            val list1ImageCount = listExam[currentQuestionPos].value.toInt()
            val list2ImageCount = listExam[currentQuestionPos].value2.toInt()

            if (listExam[currentQuestionPos].isAbacusQuestion == true){
                mBinding.relAbacus.show()
                mBinding.linearQuestion.hide()

                mBinding.layoutAbacus1.removeAllViews()
                mBinding.layoutAbacus2.removeAllViews()
                val abacusBinding1 = LayoutAbacusExamBinding.inflate(layoutInflater, null, false)
                mBinding.layoutAbacus1.addView(abacusBinding1.root)

                val abacusBinding2 = LayoutAbacusExamBinding.inflate(layoutInflater, null, false)
                mBinding.layoutAbacus2.addView(abacusBinding2.root)

                abacusBinding1.tvCurrentVal.hide()
                abacusBinding2.tvCurrentVal.hide()

                abacusBinding1.imgKidLeft.show()
                abacusBinding1.imgKidHandLeft.show()
                abacusBinding1.imgKidRight.hide()
                abacusBinding1.imgKidHandRight.hide()

                abacusBinding2.imgKidLeft.hide()
                abacusBinding2.imgKidHandLeft.hide()
                abacusBinding2.imgKidRight.show()
                abacusBinding2.imgKidHandRight.show()

                if (DataProvider.generateIndex() == 0){
                    abacusBinding1.imgKidLeft.setImageResource(R.drawable.ic_boy_abacus_left)
                    abacusBinding1.imgKidHandLeft.setImageResource(R.drawable.ic_boy_abacus_hand_left)
                    abacusBinding2.imgKidRight.setImageResource(R.drawable.ic_girl_abacus_right)
                    abacusBinding2.imgKidHandRight.setImageResource(R.drawable.ic_girl_abacus_hand_right)
                }else{
                    abacusBinding1.imgKidLeft.setImageResource(R.drawable.ic_girl_abacus_left)
                    abacusBinding1.imgKidHandLeft.setImageResource(R.drawable.ic_girl_abacus_hand_left)
                    abacusBinding2.imgKidRight.setImageResource(R.drawable.ic_boy_abacus_right)
                    abacusBinding2.imgKidHandRight.setImageResource(R.drawable.ic_boy_abacus_hand_right)
                }


                mBinding.imgSign1.show()
                mBinding.layoutAbacus1.show()
                mBinding.layoutAbacus2.show()

                lifecycleScope.launch {
                    val themeContent = DataProvider.findAbacusThemeType(requireContext(),theme,AbacusBeadType.Exam)
                    themeContent.abacusFrameExam135.let {
                        abacusBinding1.rlAbacusMain.setBackgroundResource(it)
                        abacusBinding2.rlAbacusMain.setBackgroundResource(it)
                    }
                    themeContent.dividerColor1.let {
                        abacusBinding1.ivDivider.setBackgroundColor(ContextCompat.getColor(requireContext(),it))
                        abacusBinding2.ivDivider.setBackgroundColor(ContextCompat.getColor(requireContext(),it))
                    }
                    themeContent.resetBtnColor8.let {
                        abacusBinding1.ivReset.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
                        abacusBinding2.ivReset.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
                    }
                    AbacusUtils.setAbacusColumnTheme(AbacusBeadType.ExamResult,abacusBinding1.abacusTop,abacusBinding1.abacusBottom,abacusBinding2.abacusTop,abacusBinding2.abacusBottom,column = list1ImageCount.toString().length,column2 = list2ImageCount.toString().length)
                    AbacusUtils.setNumber(list1ImageCount.toString(),abacusBinding1.abacusTop,abacusBinding1.abacusBottom,list2ImageCount.toString(),abacusBinding2.abacusTop,abacusBinding2.abacusBottom, totalLength = list1ImageCount.toString().length, totalLength1 = list2ImageCount.toString().length)
                }

                if (listExam[currentQuestionPos].type == BeginnerExamQuestionType.Additions){
                    val str = getString(R.string.additions_of)+" <b><font color='#E14A4D'>Abacus Beads</font></b>"
                    mBinding.txtHeaderTitle.text = HtmlCompat.fromHtml(str,HtmlCompat.FROM_HTML_MODE_COMPACT)
                    mBinding.imgSign1.setImageResource(R.drawable.cal_plus)
                }else if (listExam[currentQuestionPos].type == BeginnerExamQuestionType.Subtractions){
                    val str = getString(R.string.subtraction_of)+" <b><font color='#E14A4D'>Abacus Beads</font></b>"
                    mBinding.txtHeaderTitle.text = HtmlCompat.fromHtml(str,HtmlCompat.FROM_HTML_MODE_COMPACT)
                    mBinding.imgSign1.setImageResource(R.drawable.cal_minus)
                }
            }else{
                mBinding.relAbacus.hide()
                mBinding.linearQuestion.show()
                if (listExam[currentQuestionPos].type == BeginnerExamQuestionType.Additions){
                    val str = getString(R.string.additions_of)+" <b><font color='#E14A4D'>"+listExam[currentQuestionPos].imageData?.name+"</font></b>"
                    mBinding.txtHeaderTitle.text = HtmlCompat.fromHtml(str,HtmlCompat.FROM_HTML_MODE_COMPACT)
                    mBinding.imgSign.setImageResource(R.drawable.cal_plus)
                }else if (listExam[currentQuestionPos].type == BeginnerExamQuestionType.Subtractions){
                    val str = getString(R.string.subtraction_of)+" <b><font color='#E14A4D'>"+listExam[currentQuestionPos].imageData?.name+"</font></b>"
                    mBinding.txtHeaderTitle.text = HtmlCompat.fromHtml(str,HtmlCompat.FROM_HTML_MODE_COMPACT)
                    mBinding.imgSign.setImageResource(R.drawable.cal_minus)
                }

                objectListAdapter1= ObjectListAdapter(list1ImageCount, listExam[currentQuestionPos].imageData,DataObjectsSize.ExtraSmall)
                mBinding.recyclerviewObjects1.layoutManager = GridLayoutManager(requireContext(),list1ImageCount)
                mBinding.recyclerviewObjects1.adapter = objectListAdapter1

                objectListAdapter2= ObjectListAdapter(list2ImageCount, listExam[currentQuestionPos].imageData,DataObjectsSize.ExtraSmall)
                mBinding.recyclerviewObjects2.layoutManager = GridLayoutManager(requireContext(),list2ImageCount)
                mBinding.recyclerviewObjects2.adapter = objectListAdapter2

                mBinding.recyclerviewObjects2.show()
                mBinding.imgSign.show()
            }

        }
        mBinding.progressHorizontal.progress = (currentQuestionPos + 1)

        val tempAns = when (listExam[currentQuestionPos].type) {
            BeginnerExamQuestionType.Additions -> {
                listExam[currentQuestionPos].value+"+"+listExam[currentQuestionPos].value2
            }
            BeginnerExamQuestionType.Subtractions -> {
                listExam[currentQuestionPos].value+"-"+listExam[currentQuestionPos].value2
            }
            else -> {
                listExam[currentQuestionPos].value
            }
        }
        val resultObject = mCalculator.getResult(tempAns,tempAns)
        correctAns = CommonUtils.removeTrailingZero(resultObject)
        val listAnswerTemp: MutableList<Int> = ArrayList()
        val listAnswer: MutableList<Int?> = ArrayList()
        if (correctAns.toInt() - 1 > 0) {
            listAnswerTemp.add(correctAns.toInt() - 1)
        }
        if (correctAns.toInt() - 2 > 0) {
            listAnswerTemp.add(correctAns.toInt() - 2)
        }
        if (correctAns.toInt() - 3 > 0) {
            listAnswerTemp.add(correctAns.toInt() - 3)
        }
        listAnswerTemp.add(correctAns.toInt() + 1)
        listAnswerTemp.add(correctAns.toInt() + 2)
        listAnswerTemp.add(correctAns.toInt() + 3)
        for (i in 0..2) {
            val pos = Random().nextInt(listAnswerTemp.size)
            listAnswer.add(listAnswerTemp[pos])
            listAnswerTemp.removeAt(pos)
        }
        listAnswer.add(correctAns.toInt())
        listAnswer.shuffle()
        try {
            mBinding.txtAnswer11.text = listAnswer[0].toString()
            mBinding.txtAnswer22.text = listAnswer[1].toString()
            mBinding.txtAnswer33.text = listAnswer[2].toString()
            mBinding.txtAnswer44.text = listAnswer[3].toString()
        } catch (e: Exception) {
            e.printStackTrace()
            requireContext().getString(R.string.some_thing_wrong)
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
        if(requireContext().isNetworkAvailable){
            AbacusMasterSound.playTap(requireContext())
            clickOtions()
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
        if (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"").equals("Y",true)){
            getAndStartExam()
        }else{
            CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.exam_subcribe_title),getString(R.string.exam_subcribe_msg)
                ,getString(R.string.yes_i_want_to_purchase),getString(R.string.no_purchase_later), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
                clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                    override fun onConfirmationYesClick(bundle: Bundle?) {
                        goToInAppPurchase()
                    }
                    override fun onConfirmationNoClick(bundle: Bundle?){
                        testCompleteClose()
                    }
                })
        }
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
        if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" // local
            && prefManager.getCustomParam(AppConstants.AbacusProgress.Ads,"") == "Y" && // if yes in firebase
            (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"") != "Y" // if not purchased
                    && prefManager.getCustomParam(AppConstants.Purchase.Purchase_Ads,"") != "Y")) {
            newInterstitialAdRequest(getString(R.string.interstitial_ad_unit_id_exam_leave))
        }else{
            onBack()
        }
    }
    // exam complete listner
    override fun testCompleteClose() {
        if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" // local
            && prefManager.getCustomParam(AppConstants.AbacusProgress.Ads,"") == "Y" && // if yes in firebase
            (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"") != "Y" // if not purchased
                    && prefManager.getCustomParam(AppConstants.Purchase.Purchase_Ads,"") != "Y")) {
            newInterstitialAdRequest(getString(R.string.interstitial_ad_unit_id_exam_complete_close))
        }else{
            onBack()
        }
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

    // exam complate and close, leave exam
    private fun newInterstitialAdRequest(adUnit : String) {
        showLoading()
        val isAdmob = prefManager.getCustomParamBoolean(AppConstants.AbacusProgress.isAdmob,true)
        if (isAdmob){
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(requireContext(),adUnit, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    hideLoadingAndFinish()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    showInterstitialRequest(interstitialAd)
                }
            })
        }else{
            val adRequest = AdManagerAdRequest.Builder().build()
            AdManagerInterstitialAd.load(requireContext(),adUnit, adRequest, object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    hideLoadingAndFinish()
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    showInterstitialRequest(interstitialAd)
                }
            })
        }
    }

    fun showInterstitialRequest(mInterstitialAd: AdManagerInterstitialAd) {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("AdmobInterStitialAds", "exam Ad was dismissed.")
                // Don't forget to set the ad reference to null so you
                // don't show the ad a second time.
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d("AdmobInterStitialAds", "exam Ad failed to show.")
                // Don't forget to set the ad reference to null so you
                // don't show the ad a second time.
                hideLoadingAndFinish()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("AdmobInterStitialAds", "exam Ad showed fullscreen content.")
                // Called when ad is dismissed.
                hideLoadingAndFinish()
            }
        }
        mInterstitialAd.show(requireActivity())
    }

    fun showInterstitialRequest(mInterstitialAd: InterstitialAd) {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("AdmobInterStitialAds", "exam Ad was dismissed.")
                // Don't forget to set the ad reference to null so you
                // don't show the ad a second time.
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d("AdmobInterStitialAds", "exam Ad failed to show.")
                // Don't forget to set the ad reference to null so you
                // don't show the ad a second time.
                hideLoadingAndFinish()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("AdmobInterStitialAds", "exam Ad showed fullscreen content.")
                // Called when ad is dismissed.
                hideLoadingAndFinish()
            }
        }
        mInterstitialAd.show(requireActivity())
    }

    private fun hideLoadingAndFinish() {
        hideLoading()
        onBack()
    }

}