package com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode.components

import android.annotation.SuppressLint
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.ColorPrimary

@SuppressLint("ConfigurationScreenWidthHeight")
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

    numberToMatch: Int?,
    generateNextTarget: (Int?) -> Int?,
    refreshBeadMovement: (Int?) -> Unit,
    onUpdateRange: (Int, Int) -> Unit,
    dismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val dialogWidth = remember(screenWidth) {
        // 80% of screen, max 600, min 320
        val target = screenWidth * 0.7f
        target.coerceIn(320.dp, 600.dp)
    }

    var tempLow by remember { mutableStateOf(randomRangeLow.toString()) }
    var tempHigh by remember { mutableStateOf(randomRangeHigh.toString()) }
    var fromError by remember { mutableStateOf<String?>(null) }
    var toError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }


    Dialog(
        onDismissRequest = dismiss, properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // Fullscreen box to center the card
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.width(dialogWidth),     // but never more than 600dp
                shape = RoundedCornerShape(16.dp), color = Color.White, tonalElevation = 8.dp
            ) {

                Column(
                    modifier = Modifier
                ) {

                    // Title
                    Row {

                        Text(
                            stringResource(R.string.free_mode_settings),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp, bottom = 4.dp)
                                .weight(1f)
                        )

                        Box(
                            modifier = Modifier, contentAlignment = Alignment.CenterEnd
                        ) {

                            Text(
                                text = stringResource(R.string.update),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.font_bold)),
                                color = ColorPrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp, bottom = 4.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = LocalIndication.current    // ⭐ Material3 ripple, no deprecation
                                    ) {

                                        if (!isFreeModeOn){
                                            val from = tempLow.toIntOrNull()
                                            val to = tempHigh.toIntOrNull()

                                            // Reset errors
                                            fromError = null
                                            toError = null
                                            generalError = null

                                            // 1) Empty validation
                                            if (tempLow.isBlank()) {
                                                fromError = "From number cannot be empty"
                                                return@clickable
                                            }
                                            if (tempHigh.isBlank()) {
                                                toError = "To number cannot be empty"
                                                return@clickable
                                            }

                                            // 2) Parse fail
                                            if (from == null) {
                                                fromError = "Invalid number"
                                                return@clickable
                                            }
                                            if (to == null) {
                                                toError = "Invalid number"
                                                return@clickable
                                            }

                                            // 3) Max number validation
                                            if (from > 9_999_999) {
                                                fromError = "Maximum allowed number is 9,999,999"
                                                return@clickable
                                            }
                                            if (to > 9_999_999) {
                                                toError = "Maximum allowed number is 9,999,999"
                                                return@clickable
                                            }

                                            // 4) To >= From
                                            if (to <= from) {
                                                generalError = "To number must be greater than From number"
                                                return@clickable
                                            }

                                            // 5) Gap must be at least 20
                                            if ((to - from) < 20) {
                                                generalError = "Difference must be at least 20"
                                                return@clickable
                                            }

                                            // ✔ All good — Apply changes
                                            onUpdateRange(from, to)
                                        }
                                        dismiss()
                                    })
                        }


                    }

                    // ----------------------------------------------------------------------------------------------------
                    //  FREE MODE
                    // ----------------------------------------------------------------------------------------------------
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            "Want to learn the abacus parts and how it works? \uD83E\uDDE0",
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Switch(
                            modifier = Modifier.scale(0.9f),
                            checked = isFreeModeOn, onCheckedChange = {
                                setFreeMode(it)
                                if (!it) {
//                                    if (numberToMatch == null){
//                                        val newTarget = generateNextTarget(numberToMatch)
//                                        refreshBeadMovement(newTarget)
//                                    }else{
//                                        refreshBeadMovement(numberToMatch)
//                                    }
                                } else {
                                    refreshBeadMovement(null)
                                }
                            })
                    }

                    // ----------------------------------------------------------------------------------------------------
                    // ALWAYS VISIBLE — Auto-disabled in Free Mode
                    // ----------------------------------------------------------------------------------------------------
                    Column(
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        modifier = Modifier
                            .alpha(if (isFreeModeOn) 0.4f else 1f)
                            .padding(horizontal = 16.dp)
                    ) {

                        // Reset every time
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Should I clear the abacus after you finish a number? \uD83E\uDDF9",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily(Font(R.font.font_semibold)),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Switch(
                                modifier = Modifier.scale(0.9f),
                                checked = resetEveryTime,
                                onCheckedChange = setResetEveryTime,
                                enabled = !isFreeModeOn
                            )
                        }

                        // Random Toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Should I show numbers in a fun random order? \uD83C\uDFB2",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily(Font(R.font.font_semibold)),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Switch(
                                modifier = Modifier.scale(0.9f),
                                checked = randomToggle, onCheckedChange = {
                                    setRandomToggle(it)
//                                    val newTarget = generateNextTarget(null)
//                                    refreshBeadMovement(newTarget)
                                }, enabled = !isFreeModeOn
                            )
                        }

                        // Range display
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Text(
                                "Choose the numbers range you want to play with! \uD83D\uDD22",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily(Font(R.font.font_semibold)),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        // Input row (NEW)
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            var tempLowState by remember {
                                mutableStateOf(TextFieldValue(text = tempLow))
                            }

                            OutlinedTextField(
                                value = tempLowState,
                                enabled = !isFreeModeOn,
                                onValueChange = { newValue ->

                                    val maxLength = 7

                                    if (newValue.text.length <= maxLength) {
                                        // Accept the new value
                                        tempLowState = newValue
                                        tempLow = newValue.text
                                        fromError = null
                                        generalError = null
                                    } else {
                                        // BLOCK input and restore cursor position
                                        tempLowState = newValue.copy(
                                            text = newValue.text.take(maxLength),
                                            selection = TextRange(maxLength)
                                        )
                                        tempLow = newValue.text.take(maxLength)
                                    }
                                },
                                label = { Text("From Number") },
                                isError = fromError != null,
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )


                            var tempHighState by remember {
                                mutableStateOf(TextFieldValue(text = tempHigh))
                            }

                            OutlinedTextField(
                                value = tempHighState,
                                enabled = !isFreeModeOn,
                                onValueChange = { newValue ->

                                    val maxLength = 7

                                    // allow only digits
                                    if (!newValue.text.all { it.isDigit() }) {
                                        return@OutlinedTextField
                                    }

                                    if (newValue.text.length <= maxLength) {
                                        // Accept input normally
                                        tempHighState = newValue
                                        tempHigh = newValue.text
                                        toError = null
                                        generalError = null
                                    } else {
                                        // Block extra characters + fix cursor
                                        tempHighState = newValue.copy(
                                            text = newValue.text.take(maxLength),
                                            selection = TextRange(maxLength)
                                        )
                                        tempHigh = newValue.text.take(maxLength)
                                    }
                                },
                                label = { Text("To Number") },
                                isError = toError != null,
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                        }

                        if (fromError != null || toError != null || generalError != null) {
                            Text(
                                text = fromError ?: toError ?: generalError!!,
                                color = Color.Red,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                    }

                }
            }
        }
    }
}
