package com.jigar.me.ui.view.home.screens.levels.level3.config

import android.content.Context
import com.jigar.me.ui.view.home.screens.levels.level3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbHeroPanel
import com.jigar.me.ui.view.ab.AbInsetBox
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbSound
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbToggle
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abFixedStyle
import com.jigar.me.ui.view.ab.abSquash
import com.jigar.me.ui.view.home.screens.levels.level3.components.ScreenTopBar
import com.jigar.me.ui.view.home.screens.levels.level3.components.abEmojiStyle
import com.jigar.me.ui.view.home.screens.levels.level3.components.abFont
import com.jigar.me.ui.view.home.screens.levels.level3.components.abMainTitleSize
import com.jigar.me.ui.view.home.screens.levels.level3.components.abResponsive
import com.jigar.me.ui.view.home.screens.levels.level3.components.screenLeftEdge
import com.jigar.me.ui.view.home.screens.levels.level3.components.systemPrimary
import com.jigar.me.ui.view.home.screens.levels.level3.components.systemSecondary
import com.jigar.me.ui.view.home.theme.AppDimens
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.jigar.me.R
import androidx.compose.runtime.Composable
import com.jigar.me.ui.view.home.screens.levels.level3.l3SpeedLabel

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

    AbTheme {
        val colors = LocalAbColors.current

        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Lagoon)

            Row(
                modifier          = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ── LEFT: back button + mode info card ────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(0.40f)
                        .fillMaxHeight()
                        .padding(bottom = AppDimens.Dimens12, end = AppDimens.Dimens6)
                ) {
                    ScreenTopBar(title = stringResource(mode.titleRes), onBack = onBackClick)
                    Spacer(Modifier.height(AppDimens.Dimens8))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(start = AppDimens.Dimens12)
                    ) {
                        AbHeroPanel(mode.endColor, Modifier.matchParentSize(), padding = 0.dp)
                        Column(
                            modifier            = Modifier.fillMaxSize().padding(AppDimens.Dimens20),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12, Alignment.CenterVertically)
                        ) {
                            Text(mode.emoji, style = abEmojiStyle(abResponsive(52f)))
                            Text(
                                stringResource(mode.titleRes),
                                style     = abFixedStyle(abMainTitleSize(), FontWeight.Black),
                                color     = Color.White,
                                textAlign = TextAlign.Center,
                            )
                            AbInsetBox {
                                Text(
                                    modeDescription(mode),
                                    style     = abFont(14f, FontWeight.Medium),
                                    color     = Color.White.copy(alpha = 0.92f),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }

                // ── RIGHT: config options ─────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(0.60f)
                        .padding(start = AppDimens.Dimens6, top = AppDimens.Dimens8, bottom = AppDimens.Dimens12, end = AppDimens.Dimens12)
                ) {
                    AbGlassPanel(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier            = Modifier.fillMaxWidth().padding(AppDimens.Dimens16),
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                        ) {
                            // Terms
                            if (mode != L3Mode.SPEED_DRILL) {
                                L3ConfigRow(label = stringResource(R.string.l3_cfg_terms)) {
                                    listOf(3, 5, 7, 10).forEach { t ->
                                        L3Chip("$t", selected = terms == t) { terms = t }
                                    }
                                }
                            }

                            // Digits
                            L3ConfigRow(label = stringResource(R.string.l3_cfg_digits)) {
                                listOf(1, 2).forEach { d ->
                                    L3Chip(pluralStringResource(R.plurals.l3_n_digit, d, d), selected = digits == d) { digits = d }
                                }
                            }

                            // Flash speed (not for Speed Drill or Guided — user clicks Next manually)
                            if (mode != L3Mode.SPEED_DRILL && mode != L3Mode.GUIDED) {
                                L3ConfigRow(label = stringResource(R.string.l3_cfg_speed)) {
                                    listOf(1600, 1200, 800, 500).forEach { ms ->
                                        L3Chip(l3SpeedLabel(ms), selected = flashMs == ms) { flashMs = ms }
                                    }
                                }
                            }

                            // Time limit (Speed Drill only)
                            if (mode == L3Mode.SPEED_DRILL) {
                                L3ConfigRow(label = stringResource(R.string.l3_cfg_time)) {
                                    listOf(30, 60, 90).forEach { t ->
                                        L3Chip(stringResource(R.string.l3_secs, t), selected = timeLimitSecs == t) { timeLimitSecs = t }
                                    }
                                }
                            }

                            // Auto-abacus toggle (Guided only)
                            if (mode == L3Mode.GUIDED) {
                                Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens2)) {
                                    AbToggle(isOn = autoAbacus, label = stringResource(R.string.l3_cfg_auto), onChange = { autoAbacus = it })
                                    Text(
                                        if (autoAbacus) stringResource(R.string.l3_cfg_auto_on) else stringResource(R.string.l3_cfg_auto_off),
                                        style = abFont(12f),
                                        color = colors.systemSecondary,
                                    )
                                }
                            }

                            // Start button — wrap content, right-aligned
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                AbButton(
                                    text    = stringResource(R.string.l3_start),
                                    tone    = AbTone.Primary,
                                    size    = AbSize.Medium,
                                    onClick = {
                                        onStart(L3Config(
                                            mode          = mode,
                                            terms         = terms,
                                            digits        = digits,
                                            flashMs       = flashMs,
                                            autoAbacus    = autoAbacus,
                                            timeLimitSecs = timeLimitSecs,
                                        ))
                                    },
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
    val colors = LocalAbColors.current
    Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
        Text(
            label,
            style = abFont(14f, FontWeight.Bold),
            color = colors.systemPrimary.copy(alpha = 0.85f),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8), content = content)
    }
}

@Composable
private fun L3Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = LocalAbColors.current
    val s      = LocalAbScale.current.factor
    Box(
        modifier = Modifier
            .abSquash(
                face         = if (selected) colors.brandPrimary else colors.bgSecondary,
                edge         = if (selected) colors.brandPrimaryDark else colors.outline,
                stroke       = if (selected) null else colors.outline,
                cornerRadius = AbShapes.pill,
                edgeHeight   = (4f * s).dp,
                sound        = AbSound.Button,
                onClick      = onClick,
            )
            .heightIn(min = (36f * s).dp)
            .padding(horizontal = (14f * s).dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style    = abFixedStyle(14f * s, if (selected) FontWeight.ExtraBold else FontWeight.SemiBold),
            color    = if (selected) Color.White else colors.textPrimary,
            maxLines = 1,
        )
    }
}

@Composable
private fun modeDescription(mode: L3Mode): String = stringResource(when (mode) {
    L3Mode.GUIDED      -> R.string.l3_desc_guided
    L3Mode.SEMI_ANZAN  -> R.string.l3_desc_semi
    L3Mode.FULL_ANZAN  -> R.string.l3_desc_full
    L3Mode.SPEED_DRILL -> R.string.l3_desc_speed
    L3Mode.FLASH       -> R.string.l3_desc_flash
})
