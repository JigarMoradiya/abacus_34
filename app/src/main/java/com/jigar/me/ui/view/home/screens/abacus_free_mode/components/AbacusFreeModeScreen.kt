package com.jigar.me.ui.view.home.screens.abacus_free_mode.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.abacus_free_mode.viewmodel.AbacusFreeModeViewModel
import com.jigar.me.utils.AppConstants


@Composable
fun AbacusFreeModeScreen(
    viewModel: AbacusFreeModeViewModel,
    onBackClick: () -> Unit = {}
) {

    var showFooterPopup by remember { mutableStateOf(false) }

    // HANDLE ABACUS MOVEMENT
    LaunchedEffect(viewModel.abacusCalc.stateVersion) {
        viewModel.handleMatch()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ------- Settings FAB -------
        KidsActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(vertical = Dimens12, horizontal = Dimens16),
            text = stringResource(R.string.free_mode_settings),
            icon = Icons.Default.Settings,
            type = ButtonType.ORANGE,
            onClick = { showFooterPopup = true },
            isSmall = true
        )

        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // ---------- HEADER ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButtonWithText(
                    title = stringResource(R.string.abacus_free_mode),
                    onBackClick = onBackClick,
                    modifier = Modifier.weight(1f)
                )

                if (viewModel.isFreeModeOn) {
                    KidsActionButton(
                        modifier = Modifier.padding(end = Dimens16),
                        text = stringResource(R.string.click_here_to_show_abacus_tour),
                        icon = Icons.Default.RemoveRedEye,
                        type = ButtonType.BLUE,
                        onClick = { viewModel.startHighlighter() },
                        isSmall = true
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ---------- TARGET (guided mode only) ----------
            if (!viewModel.isFreeModeOn) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = Dimens8),
                    contentAlignment = Alignment.Center
                ) {
                    // Left info (range)
                    Box(
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${viewModel.fromNumber} to ${viewModel.toNumber}",
                                style = MaterialTheme.typography.titleSmall.scaled().copy(color = MaterialTheme.colorScheme.error,fontWeight = FontWeight.ExtraBold,fontFamily = FontFamily(Font(R.font.font_bold))),
                            )
                            Text(
                                text = stringResource(R.string.numbers_generate_between),
                                style = MaterialTheme.typography.bodySmall.scaled().copy(color = MaterialTheme.colorScheme.onBackground,fontWeight = FontWeight.SemiBold,fontFamily = FontFamily(Font(R.font.font_semibold))),
                            )
                        }
                    }

                    // Center: current target
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Set :",
                            style = MaterialTheme.typography.bodyLarge.scaled().copy(color = AbacusTheme.colorPreset(viewModel.selectedTheme).buttonColor,fontWeight = FontWeight.ExtraBold,fontFamily = FontFamily(Font(R.font.font_extra_bold))),
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        )
                        Spacer(modifier = Modifier.width(Dimens8))
                        Text(
                            text = viewModel.numberToMatch.toString(),
                            style = MaterialTheme.typography.headlineLarge.scaled().copy(color = AbacusTheme.colorPreset(viewModel.selectedTheme).buttonColor,fontWeight = FontWeight.ExtraBold,fontFamily = FontFamily(Font(R.font.font_extra_bold))),
                        )
                    }
                }
            }
        }

        // ===================== CENTER ABACUS ==========================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            AbacusWithDecimalCanvas(
                selectedTheme = viewModel.selectedTheme,
                screenType = AppConstants.AbacusScreen.screenTypeFreeMode,
                isFreeModeOn = viewModel.isFreeModeOn,
                isBeadSoundOn = viewModel.isBeadSoundEnabled,
                isDisplayCurrentAbacusInput = viewModel.isDisplayCurrentAbacusInput,
                abacusData = viewModel.abacusCalc,
                numberOfColumns = viewModel.currentNumberOfColumns,
                rodMovement = viewModel.rodMovements,
                showDirectionHint = viewModel.showDirectionHints,
                showHighlighter = viewModel.showHighlighter,
                onRodMovementChange = { viewModel.updateRodMovements(it) },
                onShowDirectionHintsChange = { viewModel.updateShowDirectionHints(it) },
                onShowHighlighterChange = { enabled ->
                    if (enabled) viewModel.startHighlighter()
                    else viewModel.stopHighlighter()
                },
                onReset = {},
                onNext = {}
            )
        }

        // ------- Settings Dialog -------
        if (showFooterPopup) {
            SettingsDialog(
                isFreeModeOn = viewModel.isFreeModeOn,
                setFreeMode = { viewModel.toggleFreeMode(it) },
                resetEveryTime = viewModel.isResetEveryTime,
                setResetEveryTime = { viewModel.toggleResetEveryTime(it) },
                randomToggle = viewModel.isRandomNumber,
                setRandomToggle = { viewModel.toggleRandom(it) },
                randomRangeLow = viewModel.fromNumber,
                randomRangeHigh = viewModel.toNumber,
                refreshBeadMovement = { value ->
                    viewModel.refreshBeads(value ?: viewModel.numberToMatch)
                },
                onUpdateRange = { low, high ->
                    viewModel.abacusCalc.resetAbacusData()
                    viewModel.updateRange(low, high)
                },
                dismiss = { showFooterPopup = false }
            )
        }
    }
}
