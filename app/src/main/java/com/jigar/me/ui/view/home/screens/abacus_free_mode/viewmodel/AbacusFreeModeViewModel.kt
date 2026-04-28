package com.jigar.me.ui.view.home.screens.abacus_free_mode.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.TextToSpeechManager
import com.jigar.me.ui.view.base.abacus_base.utils.MathUtils
import com.jigar.me.ui.view.base.abacus_base.viewmodel.BaseAbacusViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.convertNumberToWords
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class AbacusFreeModeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: AppPreferencesHelper,
    private val ttsManager: TextToSpeechManager,
) : BaseAbacusViewModel(numberOfColumns = 13, prefs = prefs) {

    // --------- UI / Settings state (persisted in prefs) ----------

    var isFreeModeOn by mutableStateOf(
        prefs.getCustomParamBoolean(AppConstants.AbacusScreen.isFreeMode, true)
    )
        private set

    var isResetEveryTime by mutableStateOf(
        prefs.getCustomParamBoolean(AppConstants.AbacusScreen.isResetEveryTime, false)
    )
        private set

    var isRandomNumber by mutableStateOf(
        prefs.getCustomParamBoolean(AppConstants.AbacusScreen.isRandomNumber, false)
    )
        private set

    var fromNumber by mutableIntStateOf(
        prefs.getCustomParamInt(AppConstants.AbacusScreen.fromNumber, 1)
    )
        private set

    var toNumber by mutableIntStateOf(
        prefs.getCustomParamInt(AppConstants.AbacusScreen.toNumber, 100)
    )
        private set

    var numberToMatch by mutableIntStateOf(0)


    var showHighlighter by mutableStateOf(false)
        private set

    var currentSpot by mutableStateOf<Int?>(null)
        private set

    // Direction hint default from prefs
    init {
        showDirectionHints = prefs.getCustomParamBoolean(AppConstants.Settings.Setting_direction, true)
        if (!isFreeModeOn){
            numberToMatch = if (isRandomNumber){
                (fromNumber..toNumber).random()
            }else{
                var next = prefs.getCustomParamInt(AppConstants.AbacusScreen.currentReachNumber,fromNumber)
                if (next > toNumber) next = fromNumber
                next
            }
            speakCurrentNumber(numberToMatch)
        }
    }


    fun toggleFreeMode(enabled: Boolean) {
        isFreeModeOn = enabled
        prefs.setCustomParamBoolean(AppConstants.AbacusScreen.isFreeMode, enabled)

        if (enabled) {
            // 👉 Entering FREE MODE
            showDirectionHints = false
            rodMovements = emptyList()
        } else {
            // 👉 Entering GUIDED MODE
            showDirectionHints = prefs.getCustomParamBoolean(AppConstants.Settings.Setting_direction, true)
        }
    }

    fun toggleResetEveryTime(enabled: Boolean) {
        isResetEveryTime = enabled
        prefs.setCustomParamBoolean(AppConstants.AbacusScreen.isResetEveryTime, enabled)
    }

    fun toggleRandom(enabled: Boolean) {
        isRandomNumber = enabled
        prefs.setCustomParamBoolean(AppConstants.AbacusScreen.isRandomNumber, enabled)
    }

    fun updateRange(low: Int, high: Int) {
        fromNumber = low
        toNumber = high
        prefs.setCustomParamInt(AppConstants.AbacusScreen.fromNumber, low)
        prefs.setCustomParamInt(AppConstants.AbacusScreen.toNumber, high)

        numberToMatch = generateNextTarget()
        refreshBeads(numberToMatch)
    }

    // --------- Target generation ----------

    fun generateNextTarget(prev: Int? = null): Int {
        val number = if (isRandomNumber) {
            (fromNumber..toNumber).random()
        } else {
            if (prev == null) fromNumber
            else {
                var next = prev + 1
                if (next > toNumber) next = fromNumber
                next
            }
        }
        if (!isRandomNumber){
            prefs.setCustomParamInt(AppConstants.AbacusScreen.currentReachNumber, number)
        }

        speakCurrentNumber(number)
        return number
    }

    private fun speakCurrentNumber(number: Int) {
        if (isAbacusQuestionSpeak){
            val questionWord = context.convertNumberToWords(number)
            ttsManager.speak(questionWord,AppConstants.AbacusScreen.screenTypeFreeMode)
        }
    }

    // --------- Rod movement (for arrows) ----------

    fun refreshBeads(target: Int = numberToMatch) {
        if (showHighlighter) return  // During tour, we control movements from spotlight logic

        if (isFreeModeOn) {
            // Free mode → no arrows / hints
            updateShowDirectionHints(false)
            updateRodMovements(emptyList())
        } else {
            val leftInt = abacusCalc.totalValuePair.first.toIntOrNull() ?: 0
            val rightInt = abacusCalc.totalValuePair.second.toIntOrNull() ?: 0

            val rods = max(leftInt.toString().length, target.toString().length)
            val left = MathUtils.calculateRodMovements(from = leftInt, to = target, rods = rods, isForRightRods = false)
            val right = MathUtils.calculateRodMovements(from = rightInt, to = 0, rods = 6, isForRightRods = true)
            updateRodMovements(left + right)
        }
    }

    // --------- Matching logic (guided mode) ----------

    fun handleMatch() {
        if (isFreeModeOn || showHighlighter) {
            // In free mode or tour mode, we don't auto-advance numbers
            return
        }

        val leftInt = abacusCalc.totalValuePair.first.toIntOrNull() ?: 0
        val rightInt = abacusCalc.totalValuePair.second.toIntOrNull() ?: 0

        if (rightInt == 0 && leftInt == numberToMatch) {
            // ✅ matched
            if (isResetEveryTime) {
                viewModelScope.launch {
                    delay(500)
                    abacusCalc.resetAbacusData()
                    numberToMatch = generateNextTarget(numberToMatch)
                }
            }else{
                numberToMatch = generateNextTarget(numberToMatch)
            }
        }
        refreshBeads(numberToMatch)


    }

    // --------- Highlighter / Tour ----------
    fun startHighlighter() {
        showHighlighter = true
        currentSpot = 0
        // Reset abacus display when tour starts
        abacusCalc.resetAbacusData()
        updateRodMovements(emptyList())
        updateShowDirectionHints(false)
    }

    fun stopHighlighter() {
        showHighlighter = false
        currentSpot = null
        abacusCalc.resetAbacusData()
        updateRodMovements(emptyList())
        updateShowDirectionHints(false)
    }
}