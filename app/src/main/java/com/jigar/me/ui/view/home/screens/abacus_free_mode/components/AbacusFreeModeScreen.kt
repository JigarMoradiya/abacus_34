package com.jigar.me.ui.view.home.screens.abacus_free_mode.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.local.data.DeviceInfo.screenHorizontalPadding
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbFitToSpace
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.base.abacus_base.components.LocalSpotlightPortal
import com.jigar.me.ui.view.base.abacus_base.components.SpotlightPortal
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.screens.abacus_free_mode.viewmodel.AbacusFreeModeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
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

    val tourPortal = remember { SpotlightPortal() }

    AbTheme {
    CompositionLocalProvider(LocalSpotlightPortal provides tourPortal) {
    Box(modifier = Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Lagoon, Modifier.fillMaxSize())

        // room for the heaven-value tags (free mode on) or the toy answer bar (set-a-number mode) above the frame
        val roomAbove = if (viewModel.isFreeModeOn) {
            if (DeviceInfo.isLargeTablet) 48.dp else if (DeviceInfo.isTablet) 40.dp else 32.dp
        } else {
            70.dp * LocalAbScale.current.factor
        }

        // One column: header, the abacus in whatever room is left, footer. The abacus shrinks to fit that room (it never
        // grows), so it can never run into the range badge or the settings button.
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // ---------- HEADER ----------
            AbTopBar(
                title = stringResource(R.string.abacus_free_mode),
                onBack = onBackClick,
                modifier = Modifier.padding(
                    start = screenHorizontalPadding(),
                    end = Dimens16,
                    top = DeviceInfo.screenTopPadding(),
                ),
                trailing = {
                    if (viewModel.isFreeModeOn) {
                        AbButton(
                            text = stringResource(R.string.click_here_to_show_abacus_tour),
                            tone = AbTone.Secondary,
                            size = AbSize.Compact,
                            icon = Icons.Default.RemoveRedEye,
                            onClick = { viewModel.startHighlighter() }
                        )
                    } else {
                        FreeModeTargetBadge(number = viewModel.numberToMatch)
                    }
                }
            )

            // ===================== CENTER ABACUS ==========================
            Box(Modifier.weight(1f).fillMaxWidth().padding(vertical = Dimens8)) {
                AbFitToSpace(Modifier.fillMaxSize()) {
                    Box(Modifier.padding(top = roomAbove)) { FreeModeAbacus(viewModel) }
                }
            }

            // ---------- FOOTER ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = screenHorizontalPadding(), end = Dimens16, bottom = Dimens8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!viewModel.isFreeModeOn) {
                    FreeModeRangeBadge(low = viewModel.fromNumber, high = viewModel.toNumber)
                }

                Spacer(modifier = Modifier.weight(1f))

                AbButton(
                    text = stringResource(R.string.free_mode_settings),
                    tone = AbTone.Warning,
                    size = AbSize.Compact,
                    icon = Icons.Default.Settings,
                    onClick = { showFooterPopup = true }
                )
            }
        }

        // The tour dims the whole screen (the footer too) and lights one part of the abacus at a time. Its layer is drawn
        // here, at the screen root, because the abacus above sits in a box that scales it to fit.
        tourPortal.content?.invoke()

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
    }
}

/** "Set : 25", the number the child has to make on the abacus. */
@Composable
private fun FreeModeTargetBadge(number: Int) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Box(Modifier.padding(bottom = 4.dp * s)) {
        Row(
            modifier = Modifier
                .height(36.dp * s)
                .drawBehind {
                    drawRoundRect(
                        colors.outline, topLeft = Offset(0f, 3.dp.toPx() * s), size = size,
                        cornerRadius = CornerRadius(size.height / 2f)
                    )
                }
                .background(colors.bgSecondary, CircleShape)
                .border(2.5.dp, colors.outline, CircleShape)
                .padding(horizontal = 18.dp * s),
            horizontalArrangement = Arrangement.spacedBy(8.dp * s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Set :",
                style = AbTypography.headline(s),
                color = colors.textSecondary
            )
            // Baloo's line box is tall, so the digits get an exact-height box
            AnimatedContent(
                targetState = number,
                transitionSpec = {
                    (slideInVertically(spring()) { it / 2 } + fadeIn()) togetherWith
                        (slideOutVertically(spring()) { -it / 2 } + fadeOut())
                },
                label = "freeModeTarget"
            ) { target ->
                Box(Modifier.height(30.dp * s), contentAlignment = Alignment.Center) {
                    Text(
                        text = target.toString(),
                        style = AbTypography.fixed(28f * s, FontWeight.ExtraBold, tabular = true),
                        color = colors.brandPrimary,
                        modifier = Modifier.wrapContentHeight(unbounded = true)
                    )
                }
            }
        }
    }
}

/** "1 to 100 / numbers are generated between", at the bottom left. */
@Composable
private fun FreeModeRangeBadge(low: Int, high: Int) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.md * s)
    Box(Modifier.padding(bottom = 3.dp * s)) {
        Column(
            modifier = Modifier
                .drawBehind {
                    val r = AbShapes.md.toPx() * s
                    drawRoundRect(colors.outline, topLeft = Offset(0f, 3.dp.toPx() * s), size = size, cornerRadius = CornerRadius(r))
                }
                .background(colors.bgSecondary.copy(alpha = if (colors.isDark) 0.95f else 0.92f), shape)
                .border(2.dp, colors.outline, shape)
                .padding(horizontal = 14.dp * s, vertical = 4.dp * s),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.height(20.dp * s), contentAlignment = Alignment.Center) {
                Text(
                    text = "$low to $high",
                    style = AbTypography.fixed(18f * s, FontWeight.Bold),
                    color = colors.accentPink,
                    maxLines = 1,
                    modifier = Modifier.wrapContentHeight(unbounded = true)
                )
            }
            Box(Modifier.height(15.dp * s), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.numbers_generate_between),
                    style = AbTypography.fixed(12f * s, FontWeight.SemiBold),
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.wrapContentHeight(unbounded = true)
                )
            }
        }
    }
}

/** The toy abacus of Free Mode. It emits the abacus' own layers side by side, so it needs a parent that stacks them. */
@Composable
private fun FreeModeAbacus(viewModel: AbacusFreeModeViewModel) {
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
        frameStyle = AbacusFrameStyle.Toy,
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
