package com.jigar.me.ui.view.home.common_ui

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.FreemiumWindow
import com.jigar.me.utils.FreemiumWindowLogic.Phase

/**
 * "Free access: 4 days left" for a parent, in My Account. Shown only to a new install with a free window that is not
 * subscribed (nothing for a grandfathered install, an install with no limit, or a subscriber). Never on a child screen.
 * Mirrors the iOS `FreemiumStatusCard`.
 */
/** True while the card has something to say: a new install with a free window, not subscribed. */
@Composable
fun rememberFreemiumStatusVisible(): Boolean {
    val prefs = LocalPreferencesHelper.current
    return FreemiumWindow.phase(prefs) != Phase.OFF && !CommonUtils.checkPurchaseForExerciseExamCCM(prefs)
}

/**
 * [vertical] stacks the icon, the words and the button (for the narrow slot to the right of the "This Week" card when the
 * parent is not logged in); the default is one row (above the weekly card when logged in).
 */
@Composable
fun FreemiumStatusCard(onSeePlans: () -> Unit, modifier: Modifier = Modifier, vertical: Boolean = false) {
    if (!rememberFreemiumStatusVisible()) return
    val prefs = LocalPreferencesHelper.current
    val phase = FreemiumWindow.phase(prefs)
    val colors = LocalAbColors.current
    val left = FreemiumWindow.daysLeft(prefs) ?: 0
    val ended = phase == Phase.ENDED
    val shape = RoundedCornerShape(AppDimens.Dimens16)
    val title = if (ended) "Your free week is over" else if (left == 1) "Free access: 1 day left" else "Free access: $left days left"
    val body = if (ended) {
        if (FreemiumWindow.coreAfterWindow(prefs) >= 2) "Free Mode and Level 1 stay free. Premium opens everything else."
        else "Premium opens the whole app."
    } else "Start a free trial to keep every level, game and report."
    val frame = modifier
        .fillMaxWidth()
        .padding(bottom = 3.dp)
        .drawBehind {
            val r = AppDimens.Dimens16.toPx()
            drawRoundRect(colors.outline, Offset(0f, 3.dp.toPx()), size, CornerRadius(r, r))
        }
        .background(colors.bgSecondary.copy(alpha = if (colors.isDark) 0.97f else 0.95f), shape)
        .border(2.dp, colors.accentOrange.copy(alpha = if (colors.isDark) 0.7f else 0.55f), shape)
        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens10)
    if (vertical) {
        Column(frame, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(if (ended) "\u23F3" else "\uD83C\uDF81", style = AbTypography.fixed(30f, FontWeight.Normal))
            Text(title, style = AbTypography.fixed(16f, FontWeight.ExtraBold), color = colors.textPrimary, textAlign = TextAlign.Center)
            Text(body, style = AbTypography.fixed(12f, FontWeight.SemiBold), color = colors.textSecondary, textAlign = TextAlign.Center)
            AbButton("See plans", tone = AbTone.Warning, size = AbSize.Small, onClick = onSeePlans)
        }
    } else {
        Row(frame, horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12), verticalAlignment = Alignment.CenterVertically) {
            Text(if (ended) "\u23F3" else "\uD83C\uDF81", style = AbTypography.fixed(26f, FontWeight.Normal))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, style = AbTypography.fixed(15f, FontWeight.ExtraBold), color = colors.textPrimary)
                Text(body, style = AbTypography.fixed(12f, FontWeight.SemiBold), color = colors.textSecondary)
            }
            AbButton("See plans", tone = AbTone.Warning, size = AbSize.Small, onClick = onSeePlans)
        }
    }
}
