package com.jigar.me.ui.view.dashboard.fragments.number_puzzles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
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
import com.jigar.me.MyApplication
import com.jigar.me.R
import com.jigar.me.databinding.FragmentPuzzleNumberBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.NumberSequenceCompleteBottomSheet
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.PlaySound
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.isNetworkAvailable
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PuzzleNumberFragment : BaseFragment(), NumberSequenceCompleteBottomSheet.NumberSequenceCompleteDialogInterface {
    private lateinit var mBinding: FragmentPuzzleNumberBinding
    private lateinit var mNavController: NavController
    private var check = false
    private var gridType = 3
    private var numbSteps = 0
    private var numbBestSteps = 0
    private var cards: Cards? = null
    private var button: Array<Array<AppCompatImageView?>> = emptyArray()
    private var btnIds = arrayOf(
        intArrayOf(R.id.b900, R.id.b901, R.id.b902),
        intArrayOf(R.id.b910, R.id.b911, R.id.b912),
        intArrayOf(R.id.b920, R.id.b921, R.id.b922)
    )
    private var cardImages = intArrayOf(
        R.drawable.pink0, R.drawable.pink1, R.drawable.pink2,
        R.drawable.pink3, R.drawable.pink4, R.drawable.pink5,
        R.drawable.pink6, R.drawable.pink7, R.drawable.pink8
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gridType = PuzzleNumberFragmentArgs.fromBundle(requireArguments()).type
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentPuzzleNumberBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initViews()
        initListener()
        ads()
        return mBinding.root
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun stepAds() {
        if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" &&
            prefManager.getCustomParam(AppConstants.AbacusProgress.Ads,"") == "Y" &&
            (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"") != "Y" && // purchase not
                    prefManager.getCustomParam(AppConstants.Purchase.Purchase_Ads,"") != "Y")
        ) {
            val isAdmob = prefManager.getCustomParamBoolean(AppConstants.AbacusProgress.isAdmob,true)
            if (isAdmob){
                newInterstitialAd(getString(R.string.interstitial_ad_unit_id_number_puzzle_step))
            }else{
                newAdxInterstitialAd(getString(R.string.interstitial_ad_unit_id_number_puzzle_step))
            }

        }
    }
    private fun ads() {
        if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" &&
            prefManager.getCustomParam(AppConstants.AbacusProgress.Ads,"") == "Y" &&
            (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"") != "Y" && // purchase not
                    prefManager.getCustomParam(AppConstants.Purchase.Purchase_Ads,"") != "Y")
        ){
            showAMBannerAds(mBinding.adView,getString(R.string.banner_ad_unit_id_number_puzzle))
        }
    }
    private fun newInterstitialAdCompletePuzzle() {
        if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" &&
            prefManager.getCustomParam(AppConstants.AbacusProgress.Ads,"") == "Y" &&
            (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"") != "Y" && // purchase not
                    prefManager.getCustomParam(AppConstants.Purchase.Purchase_Ads,"") != "Y")
        ){
            val isAdmob = prefManager.getCustomParamBoolean(AppConstants.AbacusProgress.isAdmob,true)
            val adUnit = getString(R.string.interstitial_ad_unit_id_number_puzzle_complete)
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
        NumberSequenceCompleteBottomSheet.showPopup(requireActivity(),gridType,numbSteps,numbBestSteps,this)
    }

    fun initViews() {
        when (gridType) {
            3 -> {
                mBinding.txtTitle.text = getString(R.string._3_x_3_puzzle)
                mBinding.grid3.show()
                mBinding.grid4.hide()
                mBinding.grid5.hide()
                btnIds = arrayOf(
                    intArrayOf(R.id.b900, R.id.b901, R.id.b902),
                    intArrayOf(R.id.b910, R.id.b911, R.id.b912),
                    intArrayOf(R.id.b920, R.id.b921, R.id.b922))
                cardImages = intArrayOf(
                    R.drawable.pink0, R.drawable.pink1, R.drawable.pink2,
                    R.drawable.pink3, R.drawable.pink4, R.drawable.pink5,
                    R.drawable.pink6, R.drawable.pink7, R.drawable.pink8)
            }
            4 -> {
                mBinding.txtTitle.text = getString(R.string._4_x_4_puzzle)
                mBinding.grid4.show()
                mBinding.grid3.hide()
                mBinding.grid5.hide()
                btnIds = arrayOf(
                    intArrayOf(R.id.b1500, R.id.b1501, R.id.b1502, R.id.b1503),
                    intArrayOf(R.id.b1510, R.id.b1511, R.id.b1512, R.id.b1513),
                    intArrayOf(R.id.b1520, R.id.b1521, R.id.b1522, R.id.b1523),
                    intArrayOf(R.id.b1530, R.id.b1531, R.id.b1532, R.id.b1533))
                cardImages = intArrayOf(
                    R.drawable.orange0, R.drawable.orange1, R.drawable.orange2,
                    R.drawable.orange3, R.drawable.orange4, R.drawable.orange5,
                    R.drawable.orange6, R.drawable.orange7, R.drawable.orange8,
                    R.drawable.orange9, R.drawable.orange10, R.drawable.orange11,
                    R.drawable.orange12, R.drawable.orange13, R.drawable.orange14, R.drawable.orange15)
            }
            5 -> {
                mBinding.txtTitle.text = getString(R.string._5_x_5_puzzle)
                mBinding.grid5.show()
                mBinding.grid3.hide()
                mBinding.grid4.hide()
                btnIds = arrayOf(
                    intArrayOf(R.id.b2400, R.id.b2401, R.id.b2402, R.id.b2403, R.id.b2404),
                    intArrayOf(R.id.b2410, R.id.b2411, R.id.b2412, R.id.b2413, R.id.b2414),
                    intArrayOf(R.id.b2420, R.id.b2421, R.id.b2422, R.id.b2423, R.id.b2424),
                    intArrayOf(R.id.b2430, R.id.b2431, R.id.b2432, R.id.b2433, R.id.b2434),
                    intArrayOf(R.id.b2440, R.id.b2441, R.id.b2442, R.id.b2443, R.id.b2444))
                cardImages = intArrayOf(
                    R.drawable.blue0, R.drawable.blue1, R.drawable.blue2,
                    R.drawable.blue3, R.drawable.blue4, R.drawable.blue5,
                    R.drawable.blue6, R.drawable.blue7, R.drawable.blue8,
                    R.drawable.blue9, R.drawable.blue10, R.drawable.blue11,
                    R.drawable.blue12, R.drawable.blue13, R.drawable.blue14, R.drawable.blue15,
                    R.drawable.blue16, R.drawable.blue17,
                    R.drawable.blue18, R.drawable.blue19, R.drawable.blue20,
                    R.drawable.blue21, R.drawable.blue22, R.drawable.blue23, R.drawable.blue24)
            }
        }
        // firebase event
        MyApplication.logEvent(AppConstants.FirebaseEvents.NumberPuzzleSequence+"_"+gridType,null)

        button = Array(gridType) { arrayOfNulls(gridType) }
        for (i in 0 until gridType) for (j in 0 until gridType) {
            button[i][j] = mBinding.root.findViewById(btnIds[i][j]) as AppCompatImageView
            button[i][j]?.setOnClickListener(onClickListener)
        }

        cards = Cards(gridType, gridType)
        newGame()
        continueGame()

    }
    private fun onStartNewGameClick() {
        newGame()
    }
    private fun onVolumeClick() {
        val isSoundOn = prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_NumberPuzzleVolume,true)
        prefManager.setCustomParamBoolean(AppConstants.Settings.Setting_NumberPuzzleVolume,!isSoundOn)
        setSoundIcon()
    }
    fun initListener() {
        mBinding.cardBack.onClick { onBack() }
        mBinding.cardVolume.onClick { onVolumeClick() }
        mBinding.cardStartNew.onClick { onStartNewGameClick() }
    }
    private fun continueGame() {
        val text: String = prefManager.getCustomParam(AppConstants.NUMBER_PUZZLE_SAVE+gridType, "")
        if (text.isEmpty()){
            newGame()
        }else{
            try {
                var k = 0
                for (i in 0 until gridType) for (j in 0 until gridType) {
                    cards?.setValueBoard(i, j, ("" + text[k] + text[k + 1]).toInt())
                    k += 2
                }
                if(cards?.finished(gridType, gridType) == true){
                    prefManager.setCustomParam(AppConstants.NUMBER_PUZZLE_SAVE+gridType, "")
                    newGame()
                }else{
                    numbSteps = prefManager.getCustomParamInt(AppConstants.NUMBER_PUZZLE_CURRENT_SCORE+gridType, 0)
                    setBestSteps()
                    showGame()
                    check = false
                }

            } catch (e: Exception) {
                newGame()
            }

        }

    }
    private fun newGame() {
        cards?.newCards
        numbSteps = 0
        setBestSteps()
        showGame()
        check = false
    }

    private fun setBestSteps() {
        numbBestSteps = prefManager.getCustomParamInt(AppConstants.NUMBER_PUZZLE_BEST_SCORE+gridType, 0)
        mBinding.txtBestMoves.text = numbBestSteps.toString()
    }

    var onClickListener = View.OnClickListener { v ->
        if (!check) for (i in 0 until gridType) for (j in 0 until gridType) if (v.id == btnIds[i][j]) buttonFunction(i,j)
    }

    private fun buttonFunction(row: Int, columb: Int) {
        cards?.moveCards(row, columb)
        if (cards?.resultMove() == true) {
            if (prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_NumberPuzzleVolume,true)){
                PlaySound.play(requireContext(),PlaySound.swap_sound)
            }
            numbSteps++
            showGame(true)
            checkFinish()
        }
    }
    private fun showGame(isShowAds : Boolean = false) {
        mBinding.txtCurrentMoves.text = numbSteps.toString()
        if (numbSteps > 0 && isShowAds){
            try {
                if (numbSteps % AppConstants.Purchase.AdsShowNumberPuzzleStep == 0){
                    stepAds()
                }
            } catch (e: Exception) {
            }
        }

        for (i in 0 until gridType) for (j in 0 until gridType) button[i][j]?.setImageResource(cardImages[cards?.getValueBoard(i,j)!!])
        setSoundIcon()
    }

    private fun setSoundIcon() {
        val isSoundOn = prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_NumberPuzzleVolume,true)
        if (isSoundOn){
            mBinding.imgVolume.setImageResource(R.drawable.ic_volume_on)
            mBinding.txtVolume.text = getString(R.string.volume_on)
        }else {
            mBinding.imgVolume.setImageResource(R.drawable.ic_volume_off)
            mBinding.txtVolume.text = getString(R.string.volume_off)
        }
    }

    private fun checkFinish() {
        if (cards?.finished(gridType, gridType) == true) {
            showGame()
            PlaySound.play(requireContext(),PlaySound.number_puzzle_win)
            if (numbSteps < numbBestSteps || numbBestSteps == 0) {
                numbBestSteps = numbSteps
                prefManager.setCustomParamInt(AppConstants.NUMBER_PUZZLE_BEST_SCORE+gridType, numbSteps)
                mBinding.txtBestMoves.text = numbSteps.toString()
            }
            prefManager.setCustomParam(AppConstants.NUMBER_PUZZLE_SAVE+gridType, "")
            check = true
            newInterstitialAdCompletePuzzle()
        }
    }
    private fun saveValueBoard() {
        var text = ""
        for (i in 0 until gridType) for (j in 0 until gridType) {
            if ((cards?.getValueBoard(i, j) ?: 0) < 10) text += "0" + cards?.getValueBoard(i,j) else text += cards?.getValueBoard(i, j)
        }
        prefManager.setCustomParam(AppConstants.NUMBER_PUZZLE_SAVE+gridType, text)
        prefManager.setCustomParamInt(AppConstants.NUMBER_PUZZLE_CURRENT_SCORE+gridType, numbSteps)
    }

    private fun onBack() {
        saveValueBoard()
        mNavController.navigateUp()
    }

    // complete dialog click listener
    override fun numberSequenceCompleteClose() {
        onBack()
    }

    override fun numberSequenceCompleteContinue() {
        onStartNewGameClick()
    }
}