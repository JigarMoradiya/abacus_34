package com.jigar.me.ui.view.home.screens.levels.level3.config

import android.content.Context
import com.jigar.me.ui.view.home.screens.levels.level3.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun Level3ConfigScreen(
    mode:        L3Mode,
    onBackClick: () -> Unit,
    onStart:     (L3Config) -> Unit,
) {
    val context  = LocalContext.current
    val prefs    = remember { context.getSharedPreferences("kotlin_basic_pref", Context.MODE_PRIVATE) }
    val modeKey  = "l3_${mode.name.lowercase()}"

    var terms         by remember { mutableIntStateOf(prefs.getInt("${modeKey}_terms",         5)) }
    var digits        by remember { mutableIntStateOf(prefs.getInt("${modeKey}_digits",        1)) }
    var flashMs       by remember { mutableIntStateOf(prefs.getInt("${modeKey}_flashMs",       1200)) }
    var autoAbacus    by remember { mutableStateOf(prefs.getBoolean("${modeKey}_autoAbacus",   false)) }
    var timeLimitSecs by remember { mutableIntStateOf(prefs.getInt("${modeKey}_timeLimitSecs", 60)) }

    LaunchedEffect(terms)         { prefs.edit().putInt("${modeKey}_terms",         terms).apply() }
    LaunchedEffect(digits)        { prefs.edit().putInt("${modeKey}_digits",        digits).apply() }
    LaunchedEffect(flashMs)       { prefs.edit().putInt("${modeKey}_flashMs",       flashMs).apply() }
    LaunchedEffect(autoAbacus)    { prefs.edit().putBoolean("${modeKey}_autoAbacus", autoAbacus).apply() }
    LaunchedEffect(timeLimitSecs) { prefs.edit().putInt("${modeKey}_timeLimitSecs", timeLimitSecs).apply() }

    val cardShape = RoundedCornerShape(AppDimens.Dimens20)

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        Row(
            modifier          = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ── LEFT: back button + mode info card ────────────────────────────
            Column(
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight()
                    .padding(bottom = AppDimens.Dimens12, end = AppDimens.Dimens6)
            ) {
                BackButtonWithText(title = mode.title, onBackClick = onBackClick)
                Spacer(Modifier.height(AppDimens.Dimens8))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(start = AppDimens.Dimens12)
                        .padding(AppDimens.Dimens4)
                        .shadow(AppDimens.Dimens6, cardShape,
                            spotColor    = mode.endColor.copy(0.35f),
                            ambientColor = mode.endColor.copy(0.15f))
                        .background(Brush.linearGradient(listOf(mode.startColor, mode.endColor)), cardShape)
                        .padding(AppDimens.Dimens20),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
                    ) {
                        Text(mode.emoji, style = MaterialTheme.typography.displaySmall.scaled())
                        Text(
                            mode.title,
                            style      = MaterialTheme.typography.headlineSmall.scaled(),
                            color      = Color.White,
                            fontWeight = FontWeight.Black,
                            textAlign  = TextAlign.Center,
                        )
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(0.20f), RoundedCornerShape(AppDimens.Dimens16))
                                .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens10)
                        ) {
                            Text(
                                modeDescription(mode),
                                style      = MaterialTheme.typography.bodyMedium.scaled(),
                                color      = Color.White.copy(0.92f),
                                textAlign  = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            // ── RIGHT: config options ─────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(0.58f)
                    .padding(start = AppDimens.Dimens6, top = AppDimens.Dimens8, bottom = AppDimens.Dimens12, end = AppDimens.Dimens12)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimens.Dimens4)
                        .shadow(AppDimens.Dimens8, cardShape,
                            spotColor    = Color.Black.copy(0.12f),
                            ambientColor = Color.Black.copy(0.06f))
                        .background(Color.White.copy(0.88f), cardShape)
                        .padding(AppDimens.Dimens20),
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                    ) {
                        // Terms
                        if (mode != L3Mode.SPEED_DRILL) {
                            L3ConfigRow(label = "📊 Terms") {
                                listOf(3, 5, 7, 10).forEach { t ->
                                    L3Chip("$t", selected = terms == t) { terms = t }
                                }
                            }
                        }

                        // Digits
                        L3ConfigRow(label = "🔢 Digits") {
                            listOf(1 to "1-digit", 2 to "2-digit").forEach { (d, label) ->
                                L3Chip(label, selected = digits == d) { digits = d }
                            }
                        }

                        // Flash speed (not for Speed Drill or Guided — user clicks Next manually)
                        if (mode != L3Mode.SPEED_DRILL && mode != L3Mode.GUIDED) {
                            L3ConfigRow(label = "⚡ Speed") {
                                listOf(1600 to "Slow", 1200 to "Normal", 800 to "Fast", 500 to "Blazing").forEach { (ms, label) ->
                                    L3Chip(label, selected = flashMs == ms) { flashMs = ms }
                                }
                            }
                        }

                        // Time limit (Speed Drill only)
                        if (mode == L3Mode.SPEED_DRILL) {
                            L3ConfigRow(label = "⏱ Time") {
                                listOf(30, 60, 90).forEach { t ->
                                    L3Chip("${t}s", selected = timeLimitSecs == t) { timeLimitSecs = t }
                                }
                            }
                        }

                        // Auto-abacus toggle (Guided only)
                        if (mode == L3Mode.GUIDED) {
                            Row(
                                modifier          = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "🧮 Auto-Abacus",
                                        style      = MaterialTheme.typography.labelLarge.scaled(),
                                        fontWeight = FontWeight.Bold,
                                        color      = Color.Black.copy(0.80f),
                                    )
                                    Text(
                                        if (autoAbacus) "Abacus updates automatically" else "You move the beads yourself",
                                        style = MaterialTheme.typography.labelSmall.scaled(),
                                        color = Color.Black.copy(0.55f),
                                    )
                                }
                                Switch(
                                    checked         = autoAbacus,
                                    onCheckedChange = { autoAbacus = it },
                                    colors          = SwitchDefaults.colors(
                                        checkedThumbColor = mode.startColor,
                                        checkedTrackColor = mode.startColor.copy(0.40f),
                                    )
                                )
                            }
                        }

                        // Start button — wrap content, right-aligned
                        val startShape = RoundedCornerShape(AppDimens.Dimens100)
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(AppDimens.Dimens3)
                                    .shadow(AppDimens.Dimens6, startShape,
                                        spotColor    = mode.endColor.copy(0.5f),
                                        ambientColor = mode.endColor.copy(0.3f))
                                    .background(Brush.linearGradient(listOf(mode.startColor, mode.endColor)), startShape)
                                    .clickable(remember { MutableInteractionSource() }, null) {
                                        AudioPlayerManager.playSoundBtnClick()
                                        onStart(L3Config(
                                            mode          = mode,
                                            terms         = terms,
                                            digits        = digits,
                                            flashMs       = flashMs,
                                            autoAbacus    = autoAbacus,
                                            timeLimitSecs = timeLimitSecs,
                                        ))
                                    }
                                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Start! 🚀",
                                    style      = MaterialTheme.typography.labelLarge.scaled(),
                                    color      = Color.White,
                                    fontWeight = FontWeight.Black,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun L3ConfigRow(label: String, content: @Composable RowScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
        Text(
            label,
            style      = MaterialTheme.typography.labelLarge.scaled(),
            fontWeight = FontWeight.Bold,
            color      = Color.Black.copy(0.80f),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
            content()
        }
    }
}

@Composable
private fun L3Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens100)
    Box(
        modifier = Modifier
            .shadow(AppDimens.Dimens3, shape)
            .background(
                if (selected) Color(0xFF1565C0) else Color.White.copy(0.55f),
                shape
            )
            .clickable(remember { MutableInteractionSource() }, null) { AudioPlayerManager.playSoundBtnClick(); onClick() }
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style      = MaterialTheme.typography.labelMedium.scaled(),
            color      = if (selected) Color.White else Color.Black.copy(0.75f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

private fun modeDescription(mode: L3Mode): String = when (mode) {
    L3Mode.GUIDED      -> "Each number appears one by one.\nFollow along on the abacus!"
    L3Mode.SEMI_ANZAN  -> "Watch the abacus update automatically.\nCalculate the total mentally!"
    L3Mode.FULL_ANZAN  -> "Numbers flash on screen.\nNo abacus — pure mental math!"
    L3Mode.SPEED_DRILL -> "Equations appear one by one.\nAnswer as fast as you can!"
    L3Mode.FLASH       -> "Rapid-fire number flash.\nConcentrate and count!"
}
