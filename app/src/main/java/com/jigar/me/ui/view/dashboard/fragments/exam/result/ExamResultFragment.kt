package com.jigar.me.ui.view.dashboard.fragments.exam.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.BeginnerExamPaper
import com.jigar.me.data.local.data.BeginnerExamQuestionType
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.model.dbtable.exam.DailyExamData
import com.jigar.me.databinding.FragmentExamResultBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExamResultFragment : BaseFragment() {

    private lateinit var binding: FragmentExamResultBinding
    private lateinit var mNavController: NavController
    private var examResult = ""
    private var examAbacusTheme = AppConstants.Settings.theam_Default
    private var listAbacus: List<DailyExamData> = ArrayList()
    private var listAbacusLevel1: List<BeginnerExamPaper> = ArrayList()
    private var examType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        examResult = requireArguments().getString(AppConstants.extras_Comman.examResult, "")
        examType = requireArguments().getString(AppConstants.extras_Comman.type,"")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExamResultBinding.inflate(inflater, container, false)
        setNavigationGraph()
        init()
        clickListener()
        return binding.root
    }

    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun init() {
        if (examType == "object" || examType == "new"){
            examAbacusTheme = requireArguments().getString(AppConstants.extras_Comman.examAbacusType, AppConstants.Settings.theam_Default)
            prefManager.setCustomParam(AppConstants.Settings.TheamTempView, examAbacusTheme)
            val type = object : TypeToken<List<BeginnerExamPaper>>() {}.type
            listAbacusLevel1 = Gson().fromJson(examResult, type)

            binding.recyclerviewResult.setHasFixedSize(true)
            binding.recyclerviewResult.itemAnimator = null
            binding.recyclerviewResult.setItemViewCacheSize(listAbacusLevel1.size)

            val themeContent = DataProvider.findAbacusThemeType(requireContext(),examAbacusTheme, AbacusBeadType.ExamResult)
            if (examType == "object"){
                binding.recyclerviewResult.layoutManager = LinearLayoutManager(requireContext())
                val examResultLevel1Adapter = ExamResultLevel1Adapter(listAbacusLevel1,themeContent)
                binding.recyclerviewResult.adapter = examResultLevel1Adapter
            }else{
                val layoutManager = GridLayoutManager(requireContext(),8)
                layoutManager.spanSizeLookup = object : SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (listAbacusLevel1[position].isAbacusQuestion == true) {
                            if (listAbacusLevel1[position].type == BeginnerExamQuestionType.Count) {
                                2
                            }else{ // abacus addition subtraction
                                3
                            }
                        }else if (listAbacusLevel1[position].imageData != null) {
                            3
                        }else{
                            1
                        }

                    }
                }
                binding.recyclerviewResult.layoutManager = layoutManager
                val examResultCommonAdapter = ExamResultCommonAdapter(listAbacusLevel1,themeContent)
                binding.recyclerviewResult.adapter = examResultCommonAdapter
            }

        }else if (examType == "detail"){
            val type = object : TypeToken<List<DailyExamData>>() {}.type
            listAbacus = Gson().fromJson(examResult, type)
            val dailyExamResultAdapter = ExamResultAdapter(listAbacus)
            binding.recyclerviewResult.adapter = dailyExamResultAdapter
        }

        ads()
    }

    private fun clickListener() {
        binding.cardBack.onClick { mNavController.navigateUp() }
    }
    private fun ads() {
        if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" // local
            && prefManager.getCustomParam(AppConstants.AbacusProgress.Ads,"") == "Y" && // if yes in firebase
            (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"") != "Y" // if not purchased
                    && prefManager.getCustomParam(AppConstants.Purchase.Purchase_Ads,"") != "Y")) {
            showAMBannerAds(binding.adView,getString(R.string.banner_ad_unit_id_exam_result))
            if (requireArguments().getString(AppConstants.extras_Comman.From, "").equals("exam")){
                newInterstitialAdRequest()
            }
        }
    }

    // show leave ads
    private fun newInterstitialAdRequest() {
        showLoading()
        val isAdmob = prefManager.getCustomParamBoolean(AppConstants.AbacusProgress.isAdmob,true)
        val adUnit = getString(R.string.interstitial_ad_unit_id_exam_complete_show_result)
        if (isAdmob){
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(requireContext(),adUnit, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    hideLoading()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    hideLoading()
                    interstitialAd.show(requireActivity())
                }
            })
        }else{
            val adRequest = AdManagerAdRequest.Builder().build()
            AdManagerInterstitialAd.load(requireContext(),adUnit, adRequest, object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    hideLoading()
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    hideLoading()
                    interstitialAd.show(requireActivity())
                }
            })
        }

    }



}