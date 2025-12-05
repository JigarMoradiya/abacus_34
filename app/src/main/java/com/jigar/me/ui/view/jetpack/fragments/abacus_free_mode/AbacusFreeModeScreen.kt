package com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme
import com.jigar.me.ui.view.jetpack.abacus_base.components.AbacusWithDecimal
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.LocalPreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.MathUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AbacusFreeModeFragment : Fragment() {
    @Inject
    lateinit var preferences: AppPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    CompositionLocalProvider(
                        LocalPreferencesHelper provides preferences
                    ) {
                        AbacusFreeModeScreen(
                            onBackClick = { findNavController().popBackStack() })
                    }

                }
            }
        }
    }
}

@Composable
fun AbacusFreeModeScreen(
    onBackClick: () -> Unit = {}
) {
    // ----- PREFS -----
    val prefs = LocalPreferencesHelper.current

    var isFreeModeOn by remember { mutableStateOf(prefs.getCustomParamBoolean(AppConstants.AbacusScreen.isFreeMode, true)) }
    var isResetEveryTime by remember { mutableStateOf(prefs.getCustomParamBoolean(AppConstants.AbacusScreen.isResetEveryTime, false)) }
    var isRandomNumber by remember { mutableStateOf(prefs.getCustomParamBoolean(AppConstants.AbacusScreen.isRandomNumber, false)) }
    var fromNumber by remember { mutableIntStateOf(prefs.getCustomParamInt(AppConstants.AbacusScreen.fromNumber, 1)) }
    var toNumber by remember { mutableIntStateOf(prefs.getCustomParamInt(AppConstants.AbacusScreen.toNumber, 100)) }
    var isShowDirection by remember { mutableStateOf(prefs.getCustomParamBoolean(AppConstants.Settings.Setting_direction, true)) }

    // Persist whenever toggled
    LaunchedEffect(isFreeModeOn) {
        prefs.setCustomParamBoolean(AppConstants.AbacusScreen.isFreeMode, isFreeModeOn)
    }
    LaunchedEffect(isResetEveryTime) {
        prefs.setCustomParamBoolean(AppConstants.AbacusScreen.isResetEveryTime, isResetEveryTime)
    }
    LaunchedEffect(isRandomNumber) {
        prefs.setCustomParamBoolean(AppConstants.AbacusScreen.isRandomNumber, isRandomNumber)
    }
    LaunchedEffect(fromNumber) {
        prefs.setCustomParamInt(AppConstants.AbacusScreen.fromNumber, fromNumber)
    }
    LaunchedEffect(toNumber) {
        prefs.setCustomParamInt(AppConstants.AbacusScreen.toNumber, toNumber)
    }

    // ----- ABACUS STATE -----
    val abacusCalc = remember { AbacusCalculations(numberOfColumns = 13) }
    var showFooterPopup by remember { mutableStateOf(false) }
    var numberToMatch by remember { mutableIntStateOf(0) }
    var rodMovements by remember { mutableStateOf(listOf<RodMovement>()) }
    var showHighlighter by remember { mutableStateOf(false) }
    val selectedTheme = "poligon_rainbow"

    // ----- HELPER: pick new target number -----
    fun generateNextTarget(prev: Int? = null): Int {
        return if (isRandomNumber) {
            (fromNumber..toNumber).random()
        } else {
            if (prev == null) fromNumber
            else {
                var next = prev + 1
                if (next > toNumber) next = fromNumber
                next
            }
        }
    }

    // ----- HELPER: recompute rod movements -----
    fun refreshBeadMovement(currentTarget: Int = numberToMatch) {
        if (!isFreeModeOn) {
            // Abacus "from" value = left part of pair (integer side)
            val currentIntValue = abacusCalc.totalValuePair.first.toIntOrNull() ?: 0
            val newValue = currentTarget

            // rods = max of digits between current and target
            val rods = maxOf(
                currentIntValue.toString().length, newValue.toString().length
            )

            val left = MathUtils().calculateRodMovements(
                from = currentIntValue, to = newValue, rods = rods, isForRightRods = false
            )

            rodMovements = left
        } else {
            isShowDirection = false
            rodMovements = emptyList()
        }
    }

    // ----- INITIAL TARGET + ARROWS -----
    LaunchedEffect(Unit) {
        numberToMatch = generateNextTarget(null)
        refreshBeadMovement(numberToMatch)
    }

    // ----- REACT ON BEAD MOVEMENT (stateVersion) -----
    LaunchedEffect(
        abacusCalc.stateVersion, isFreeModeOn, isRandomNumber, fromNumber, toNumber
    ) {
        if (!isFreeModeOn) {
            val pair = abacusCalc.totalValuePair
            val leftInt = pair.first.toIntOrNull() ?: 0
            val rightInt = pair.second.toIntOrNull() ?: 0

            if (rightInt == 0 && leftInt == numberToMatch) {
                // ✅ matched exactly
                if (isResetEveryTime) {
                    abacusCalc.resetAbacusData()
                }
                val nextTarget = generateNextTarget(numberToMatch)
                numberToMatch = nextTarget
                refreshBeadMovement(nextTarget)
            } else {
                // just update arrows for current configuration
                refreshBeadMovement(numberToMatch)
            }
        } else {
            // free mode → no hints
            isShowDirection = false
            rodMovements = emptyList()
        }
    }

    // =====================================
    // UI LAYOUT
    // =====================================
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // pop-up button
        Surface(
            onClick = { showFooterPopup = true },
            shape = FloatingActionButtonDefaults.extendedFabShape,
            color = FloatingActionButtonDefaults.containerColor,
            shadowElevation = 4.dp,         // 👈 correct elevation
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
        ) {

            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 6.dp), // 👈 custom inner padding
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = stringResource(R.string.free_mode_settings),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }



        // popup content
        if (showFooterPopup) {
            SettingsDialog(
                isFreeModeOn = isFreeModeOn,
                setFreeMode = { isFreeModeOn = it },
                resetEveryTime = isResetEveryTime,
                setResetEveryTime = { isResetEveryTime = it },
                randomToggle = isRandomNumber,
                setRandomToggle = { isRandomNumber = it },
                randomRangeLow = fromNumber,
                randomRangeHigh = toNumber,
                numberToMatch = numberToMatch,
                refreshBeadMovement = { value ->
                    refreshBeadMovement(value ?: 0)
                },
                generateNextTarget = ::generateNextTarget,

                onUpdateRange = { low, high ->
                    fromNumber = low
                    toNumber = high
                },
                dismiss = { showFooterPopup = false })
        }


        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ---------- HEADER ----------
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                BackButtonWithText(
                    title = stringResource(R.string.abacus_free_mode), onBackClick = onBackClick
                )

                Spacer(modifier = Modifier.weight(1f))

                if (isFreeModeOn) {
                    TextButton(onClick = { showHighlighter = true }) {
                        Text(
                            text = "Click here to show Abacus Tour", color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // ---------- SPACE (abacus sits in center Box below) ----------
            Spacer(modifier = Modifier.weight(1f))

            // Current target number (guided mode only)
            if (!isFreeModeOn) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(), contentAlignment = Alignment.Center       // vertical center for all children
                ) {

                    // 1️⃣ Left aligned text
                    Box(modifier = Modifier.align(Alignment.CenterStart)) {
                        Column(
                            modifier = Modifier.align(Alignment.Center), // Center the column vertically & horizontally inside parent
                            horizontalAlignment = Alignment.CenterHorizontally // Center text inside column
                        ) {
                            Text(
                                text = "$fromNumber to $toNumber", color = Color.Red, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleSmall, fontFamily = FontFamily(Font(R.font.font_bold))
                            )

                            Text(
                                text = "Numbers generate between", color = Color.Black, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily(Font(R.font.font_semibold))
                            )
                        }
                    }

                    // 2️⃣ Center aligned text
                    Row(
                        modifier = Modifier.align(Alignment.Center), // Center the column vertically & horizontally inside parent
                        verticalAlignment = Alignment.CenterVertically // Center text inside column
                    ) {

                        Text(
                            text = "Set :", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily(Font(R.font.font_extra_bold)), color = AbacusTheme.colorPreset(selectedTheme).buttonColor, textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = numberToMatch.toString(), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily(Font(R.font.font_extra_bold)), color = AbacusTheme.colorPreset(selectedTheme).buttonColor, textAlign = TextAlign.Center
                        )
                    }


                }
            }
        }

        // ---------- ABACUS CENTER ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), contentAlignment = Alignment.Center
        ) {
            AbacusWithDecimal(
                numberOfColumns = 13,
                abacusData = abacusCalc,
                rodMovements = rodMovements,
                showDirectionHints = isShowDirection,
                showHighlighter = showHighlighter,
                selectedTheme = selectedTheme,
                screenType = AppConstants.AbacusScreen.screenTypeFreeMode,
                isFreeModeOn = isFreeModeOn,
                modifier = Modifier,
                onReset = {
                    abacusCalc.resetAbacusData()
                    if (!isFreeModeOn) {
                        refreshBeadMovement(numberToMatch)
                    }
                },
                onNext = {
                    if (!isFreeModeOn) {
                        val next = generateNextTarget(numberToMatch)
                        numberToMatch = next
                        refreshBeadMovement(next)
                    }
                })
        }
    }
}

@Preview(
    showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 780, heightDp = 400
)
@Composable
fun PreviewAbacusFreeModeScreen() {
    MaterialTheme {
        AbacusFreeModeScreen()
    }
}