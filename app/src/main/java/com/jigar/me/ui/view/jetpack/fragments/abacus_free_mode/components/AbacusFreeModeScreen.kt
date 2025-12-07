package com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode.components

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
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme
import com.jigar.me.ui.view.jetpack.abacus_base.components.withcanvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode.viewmodel.AbacusFreeModeViewModel
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.LocalPreferencesHelper
import com.jigar.me.utils.AppConstants


@Composable
fun AbacusFreeModeScreen(
    viewModel: AbacusFreeModeViewModel,
    onBackClick: () -> Unit = {}
) {
    val prefs = LocalPreferencesHelper.current

    val selectedTheme =
        prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    var showFooterPopup by remember { mutableStateOf(false) }

    // -------- INIT TARGET --------
    LaunchedEffect(Unit) {
        viewModel.numberToMatch = viewModel.generateNextTarget(null)
        viewModel.refreshBeads(viewModel.numberToMatch)
    }

    // HANDLE ABACUS MOVEMENT
    LaunchedEffect(viewModel.abacusCalc.stateVersion) {
        viewModel.handleMatch()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ------- Settings FAB -------
        Surface(
            onClick = { showFooterPopup = true },
            shape = FloatingActionButtonDefaults.extendedFabShape,
            color = FloatingActionButtonDefaults.containerColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.free_mode_settings),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
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
                numberToMatch = viewModel.numberToMatch,
                refreshBeadMovement = { value ->
                    viewModel.refreshBeads(value ?: viewModel.numberToMatch)
                },
                generateNextTarget = { prev ->
                    viewModel.generateNextTarget(prev)
                },
                onUpdateRange = { low, high ->
                    viewModel.updateRange(low, high)
                },
                dismiss = { showFooterPopup = false }
            )
        }

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

                if (viewModel.isFreeModeOn) {
                    TextButton(onClick = { viewModel.startHighlighter() }) {
                        Text(
                            text = stringResource(R.string.click_here_to_show_abacus_tour),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ---------- TARGET (guided mode only) ----------
            if (!viewModel.isFreeModeOn) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
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
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = stringResource(R.string.numbers_generate_between),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodySmall
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
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                            color = AbacusTheme.colorPreset(selectedTheme).buttonColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = viewModel.numberToMatch.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                            color = AbacusTheme.colorPreset(selectedTheme).buttonColor
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
                selectedTheme = selectedTheme,
                screenType = AppConstants.AbacusScreen.screenTypeFreeMode,
                isFreeModeOn = viewModel.isFreeModeOn,
                abacusData = viewModel.abacusCalc,
                numberOfColumns = AbacusFreeModeViewModel.Companion.COLUMNS,
                rodMovement = viewModel.rodMovements,
                showDirectionHint = viewModel.showDirectionHints,
                showHighlighter = viewModel.showHighlighter,
                onRodMovementChange = { viewModel.updateRodMovements(it) },
                onShowDirectionHintsChange = { viewModel.updateShowDirectionHints(it) },
                onShowHighlighterChange = { enabled ->
                    if (enabled) viewModel.startHighlighter()
                    else viewModel.stopHighlighter()
                }
            )
        }
    }
}
