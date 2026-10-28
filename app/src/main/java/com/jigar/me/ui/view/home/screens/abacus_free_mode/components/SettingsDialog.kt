package com.jigar.me.ui.view.home.screens.abacus_free_mode.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbDialog
import com.jigar.me.ui.view.ab.AbSettingRow
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbSwitch
import com.jigar.me.ui.view.ab.AbTextField
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbScale

@Composable
fun SettingsDialog(
    isFreeModeOn: Boolean,
    setFreeMode: (Boolean) -> Unit,

    resetEveryTime: Boolean,
    setResetEveryTime: (Boolean) -> Unit,

    randomToggle: Boolean,
    setRandomToggle: (Boolean) -> Unit,

    randomRangeLow: Int,
    randomRangeHigh: Int,

    refreshBeadMovement: (Int?) -> Unit,
    onUpdateRange: (Int, Int) -> Unit,
    dismiss: () -> Unit
) {
    val s = LocalAbScale.current.factor
    var tempLow by remember { mutableStateOf(randomRangeLow.toString()) }
    var tempHigh by remember { mutableStateOf(randomRangeHigh.toString()) }
    var fromError by remember { mutableStateOf<String?>(null) }
    var toError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }

    // Flattened first, so the dimming is applied once to the finished picture (otherwise each layer of a switch is dimmed
    // on its own and they show through each other).
    val groupAlpha by animateFloatAsState(
        targetValue = if (isFreeModeOn) 0.4f else 1f,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "freeModeGroupAlpha"
    )

    AbDialog(
        title = stringResource(R.string.free_mode_settings),
        icon = Icons.Rounded.Tune,
        accent = AbColors.Light.accentGreen,
        onDismiss = dismiss,
        widthFraction = 0.72f,
        footer = {
            AbButton(
                text = stringResource(R.string.update),
                tone = AbTone.Success,
                size = AbSize.Compact,
                icon = Icons.Rounded.Refresh,
                onClick = {
                    if (!isFreeModeOn) {
                        val from = tempLow.toIntOrNull()
                        val to = tempHigh.toIntOrNull()

                        // Reset errors
                        fromError = null
                        toError = null
                        generalError = null

                        // 1) Empty validation
                        if (tempLow.isBlank()) {
                            fromError = "From number cannot be empty"
                            return@AbButton
                        }
                        if (tempHigh.isBlank()) {
                            toError = "To number cannot be empty"
                            return@AbButton
                        }

                        // 2) Parse fail
                        if (from == null) {
                            fromError = "Invalid number"
                            return@AbButton
                        }
                        if (to == null) {
                            toError = "Invalid number"
                            return@AbButton
                        }

                        // 3) Max number validation
                        if (from > 9_999_999) {
                            fromError = "Maximum allowed number is 9,999,999"
                            return@AbButton
                        }
                        if (to > 9_999_999) {
                            toError = "Maximum allowed number is 9,999,999"
                            return@AbButton
                        }

                        // 4) To >= From
                        if (to <= from) {
                            generalError = "To number must be greater than From number"
                            return@AbButton
                        }

                        // 5) Gap must be at least 5
                        if ((to - from) < 5) {
                            generalError = "Difference must be at least 5"
                            return@AbButton
                        }

                        // ✔ All good — Apply changes
                        onUpdateRange(from, to)
                    }
                    dismiss()
                }
            )
        }
    ) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // ----------------------------------------------------------------------------------------------------
            //  FREE MODE
            // ----------------------------------------------------------------------------------------------------
            AbSettingRow(label = "Want to learn the abacus parts and how it works? 🧠") {
                AbSwitch(
                    isOn = isFreeModeOn,
                    onChange = {
                        setFreeMode(it)
                        if (it) {
                            refreshBeadMovement(null)
                        }
                    }
                )
            }

            // ----------------------------------------------------------------------------------------------------
            // ALWAYS VISIBLE — Auto-disabled in Free Mode
            // ----------------------------------------------------------------------------------------------------
            Box {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.graphicsLayer {
                        alpha = groupAlpha
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                ) {
                    // Reset every time
                    AbSettingRow(label = "Should I clear the abacus after you finish a number? 🧹") {
                        AbSwitch(isOn = resetEveryTime, onChange = setResetEveryTime)
                    }

                    // Random Toggle
                    AbSettingRow(label = "Should I show numbers in a fun random order? 🎲") {
                        AbSwitch(isOn = randomToggle, onChange = setRandomToggle)
                    }

                    // Range
                    AbSettingRow(label = "Choose the numbers range you want to play with! 🔢") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AbTextField(
                                placeholder = "From",
                                text = tempLow,
                                onTextChange = { value ->
                                    tempLow = value.filter { it.isDigit() }.take(MAX_DIGITS)
                                    fromError = null
                                    generalError = null
                                },
                                modifier = Modifier.width(110.dp)
                            )
                            AbTextField(
                                placeholder = "To",
                                text = tempHigh,
                                onTextChange = { value ->
                                    tempHigh = value.filter { it.isDigit() }.take(MAX_DIGITS)
                                    toError = null
                                    generalError = null
                                },
                                modifier = Modifier.width(110.dp)
                            )
                        }
                    }
                }

                // the group is disabled while Free Mode is on: it swallows every touch
                if (isFreeModeOn) {
                    Box(Modifier.matchParentSize().pointerInput(Unit) { detectTapGestures { } })
                }
            }

            // Inline error message
            val error = fromError ?: toError ?: generalError
            if (error != null) {
                Text(
                    text = error,
                    style = AbTypography.label(s),
                    color = AbColors.Light.danger,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/** The most digits a range number may have (9,999,999). */
private const val MAX_DIGITS = 7
