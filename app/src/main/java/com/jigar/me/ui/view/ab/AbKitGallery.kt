package com.jigar.me.ui.view.ab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Debug-only gallery of every kit component, reachable with
 * `adb shell am start -n <pkg>/com.jigar.me.ui.view.home.HomeActivity --es ab_screen kit [--ez ab_dark true]`.
 * Lets a component be checked in light and dark, on phone and tablet, without walking the app.
 */
object AbDebugScreens {
    @Composable
    fun Render(name: String, dark: Boolean) {
        AbTheme(forcing = if (dark) AbThemeMode.Dark else AbThemeMode.Light) {
            when (name) {
                "kit" -> AbKitGallery()
                // the abacus with the toy frame: a 13-rod practice abacus, a 7-rod exercise one, and Free Mode with the tags
                "toy" -> AbToyAbacusPreview(13, com.jigar.me.utils.AppConstants.AbacusScreen.screenTypeAbacusPractice, false)
                "toy7" -> AbToyAbacusPreview(7, com.jigar.me.utils.AppConstants.AbacusScreen.screenTypeExercise, false)
                "toyfree" -> AbToyAbacusPreview(13, com.jigar.me.utils.AppConstants.AbacusScreen.screenTypeFreeMode, true)
                "art" -> AbArtPreview()
                "board" -> AbBoardPreview()
                "paywall" -> AbPaywallPreview()
                "purchase" -> AbPurchasePreview()
                "account" -> AbAccountPreview()
                "reports" -> AbReportsPreview()
                "ccmresult" -> AbCcmResultPreview(true)
                "ccmwrong" -> AbCcmResultPreview(false)
                else -> AbKitGallery()
            }
        }
    }
}

@Composable
fun AbKitGallery() {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    var on by remember { mutableStateOf(true) }
    var off by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Meadow)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp * s),
        ) {
            AbTopBar(title = "Kit Gallery", onBack = {}) { AbChip("Step by step", AbTone.Primary) }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp * s), verticalAlignment = Alignment.CenterVertically) {
                AbButton("Primary", tone = AbTone.Primary, onClick = {})
                AbButton("Secondary", tone = AbTone.Secondary, onClick = {})
                AbButton("Success", tone = AbTone.Success, icon = Icons.Rounded.PlayArrow, onClick = {})
                AbButton("Warning", tone = AbTone.Warning, onClick = {})
                AbButton("Pink", tone = AbTone.Pink, onClick = {})
                AbButton("Purple", tone = AbTone.Purple, onClick = {})
                AbButton("Danger", tone = AbTone.Danger, onClick = {})
                AbButton("Ghost", tone = AbTone.Ghost, onClick = {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp * s), verticalAlignment = Alignment.CenterVertically) {
                AbButton("Compact", size = AbSize.Compact, onClick = {})
                AbButton("Small", size = AbSize.Small, onClick = {})
                AbButton("Medium", size = AbSize.Medium, onClick = {})
                AbButton("Large", size = AbSize.Large, icon = Icons.Rounded.PlayArrow, onClick = {})
                AbButton("Disabled", enabled = false, onClick = {})
                AbButton("Disabled", tone = AbTone.Success, enabled = false, onClick = {})
                AbIconButton(Icons.Rounded.Settings, onClick = {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp * s), verticalAlignment = Alignment.Top) {
                AbCard(Modifier.width(260.dp * s)) {
                    Text("Meet the Abacus", style = AbTypography.headline(s), color = colors.textPrimary)
                    Text("Baloo 2 body text on a kit card.", style = AbTypography.body(s), color = colors.textSecondary)
                    AbProgressBar(filled = 3, total = 10)
                }
                Column(Modifier.width(280.dp * s), verticalArrangement = Arrangement.spacedBy(8.dp * s)) {
                    AbToggle(isOn = on, label = "Display current number", onChange = { on = it })
                    AbToggle(isOn = off, label = "Dark mode", onChange = { off = it })
                    AbBeadStars(earned = 2)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp * s)) {
                        colors.placeValue.forEach { AbBead(it) }
                    }
                    AbCountPill("Question 3 of 10", onColor = false)
                }
                Text("Display\n0123456789\n12 + 34 = 46", style = AbTypography.number(s), color = colors.textPrimary)
            }
        }
    }
}


/** Debug preview of the abacus drawn with the toy frame. */
@Composable
private fun AbToyAbacusPreview(columns: Int, screenType: String, freeMode: Boolean) {
    val abacusData = remember { com.jigar.me.ui.view.base.abacus_base.AbacusCalculations(columns) }
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Meadow)
        Box(Modifier.fillMaxSize().padding(top = 110.dp, start = 40.dp), contentAlignment = Alignment.TopStart) {
            com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas(
                selectedTheme = "poligon_purple",
                screenType = screenType,
                isFreeModeOn = freeMode,
                isBeadSoundOn = false,
                isDisplayCurrentAbacusInput = true,
                abacusData = abacusData,
                numberOfColumns = columns,
                rodMovement = emptyList(),
                showDirectionHint = false,
                abacusType = "step_by_step_answer",
                frameStyle = AbacusFrameStyle.Toy,
                onRodMovementChange = {},
                onShowDirectionHintsChange = {},
                onShowHighlighterChange = {},
                onReset = {},
                onNext = {},
            )
        }
    }
}


/** Debug preview of the drawn artwork: menu tiles, level characters and the Exercise art. */
@Composable
private fun AbArtPreview() {
    val colors = LocalAbColors.current
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Meadow)
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AbMenuKind.entries.forEach { AbMenuTile(it, 150.dp) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (level in 1..10) Box(Modifier.width(90.dp).height(120.dp)) { AbCritterView(level) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                for (page in 0..2) Box(Modifier.width(220.dp).height(150.dp).background(colors.bgSecondary)) {
                    ExercisePageDecor(page); Box(Modifier.align(Alignment.Center)) { ExercisePageArt(page) }
                }
                for (page in 0..2) Box(Modifier.width(150.dp).height(150.dp).background(colors.bgSecondary)) { ExerciseQuestionDecor(page) }
            }
        }
    }
}
