package com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val navController = findNavController()
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    CompositionLocalProvider(
                        LocalPreferencesHelper provides preferences
                    ) {
                        AbacusFreeModeScreen(
                            onBackClick = { findNavController().popBackStack() },
                            onSettingsClick = {  },
                            onVideoClick = {  }
                        )
                    }

                }
            }
        }
    }
}
@Composable
fun AbacusFreeModeScreen(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onVideoClick: () -> Unit = {}
) {
    // ----- PREFS -----
    val prefs = LocalPreferencesHelper.current

    var isFreeModeOn by remember {
        mutableStateOf(
            prefs.getCustomParamBoolean(
                AppConstants.AbacusScreen.isFreeMode,
                true
            )
        )
    }

    // Persist whenever toggled
    LaunchedEffect(isFreeModeOn) {
        prefs.setCustomParamBoolean(AppConstants.AbacusScreen.isFreeMode, isFreeModeOn)
    }

    // ----- ABACUS STATE -----
    val abacusCalc = remember { AbacusCalculations(numberOfColumns = 13) }

    var numberToMatch by remember { mutableIntStateOf(0) }
    var resetEveryTime by remember { mutableStateOf(false) }
    var randomToggle by remember { mutableStateOf(false) }
    var randomRangeLow by remember { mutableIntStateOf(1) }
    var randomRangeHigh by remember { mutableIntStateOf(100) }

    var rodMovements by remember { mutableStateOf(listOf<RodMovement>()) }
    var showDirectionHints by remember { mutableStateOf(false) }
    var showHighlighter by remember { mutableStateOf(false) }

    val selectedTheme = "poligon_rainbow"

    // ----- HELPER: pick new target number -----
    fun generateNextTarget(prev: Int? = null): Int {
        return if (randomToggle) {
            (randomRangeLow..randomRangeHigh).random()
        } else {
            if (prev == null) randomRangeLow
            else {
                var next = prev + 1
                if (next > randomRangeHigh) next = randomRangeLow
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
                currentIntValue.toString().length,
                newValue.toString().length
            )

            val left = MathUtils().calculateRodMovements(
                from = currentIntValue,
                to = newValue,
                rods = rods,
                isForRightRods = false
            )

            rodMovements = left
            showDirectionHints = true
        } else {
            showDirectionHints = false
            rodMovements = emptyList()
        }
    }

    // ----- INITIAL TARGET + ARROWS -----
    LaunchedEffect(Unit) {
        numberToMatch = generateNextTarget(null)
        refreshBeadMovement(numberToMatch)
    }

    // ----- REACT ON BEAD MOVEMENT (stateVersion) -----
    LaunchedEffect(abacusCalc.stateVersion, isFreeModeOn, randomToggle, randomRangeLow, randomRangeHigh) {
        if (!isFreeModeOn) {
            val pair = abacusCalc.totalValuePair
            val leftInt = pair.first.toIntOrNull() ?: 0
            val rightInt = pair.second.toIntOrNull() ?: 0

            if (rightInt == 0 && leftInt == numberToMatch) {
                // ✅ matched exactly
                if (resetEveryTime) {
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
            showDirectionHints = false
            rodMovements = emptyList()
        }
    }

    // =====================================
    // UI LAYOUT
    // =====================================
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ---------- HEADER ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButtonWithText(
                    title = stringResource(R.string.abacus_free_mode),
                    onBackClick = onBackClick
                )

                Spacer(modifier = Modifier.weight(1f))

                if (!isFreeModeOn) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(onClick = onSettingsClick) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = Color(0x33000000)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = Color.Magenta
                                    )
                                }
                            }
                        }

                        IconButton(onClick = onVideoClick) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = Color(0x33000000)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.Magenta
                                    )
                                }
                            }
                        }
                    }
                } else {
                    TextButton(onClick = { showHighlighter = true }) {
                        Text(
                            text = "Click here to show Abacus tour",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // ---------- SPACE (abacus sits in center Box below) ----------
            Spacer(modifier = Modifier.weight(1f))

            // ---------- FOOTER ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Free mode toggle
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Free Mode", style = MaterialTheme.typography.labelSmall)
                    Switch(
                        checked = isFreeModeOn,
                        onCheckedChange = { checked ->
                            isFreeModeOn = checked
                            // When switching back to guided mode, re-sync target + arrows
                            if (!checked) {
                                numberToMatch = generateNextTarget(numberToMatch)
                                refreshBeadMovement(numberToMatch)
                            } else {
                                refreshBeadMovement()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Current target number (guided mode only)
                if (!isFreeModeOn) {
                    Text(
                        text = numberToMatch.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = AbacusTheme.colorPreset(selectedTheme).buttonColor
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Right side toggles only when guided mode
                if (!isFreeModeOn) {
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Reset Every Time", style = MaterialTheme.typography.labelSmall)
                            Switch(
                                checked = resetEveryTime,
                                onCheckedChange = { resetEveryTime = it }
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Random", style = MaterialTheme.typography.labelSmall)
                            Switch(
                                checked = randomToggle,
                                onCheckedChange = {
                                    randomToggle = it
                                    // new mode → new target
                                    numberToMatch = generateNextTarget(null)
                                    refreshBeadMovement(numberToMatch)
                                }
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Range", style = MaterialTheme.typography.labelSmall)
                            Text("$randomRangeLow to $randomRangeHigh", color = Color.Red)
                            // (you can plug a range picker here later)
                        }
                    }
                }
            }
        }

        // ---------- ABACUS CENTER ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            AbacusWithDecimal(
                numberOfColumns = 13,
                abacusData = abacusCalc,
                rodMovements = rodMovements,
                showDirectionHints = showDirectionHints,
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
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 780,
    heightDp = 400
)
@Composable
fun PreviewAbacusFreeModeScreen() {
    MaterialTheme {
        AbacusFreeModeScreen()
    }
}