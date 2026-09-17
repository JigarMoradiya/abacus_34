package com.jigar.me.ui.view.home.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.DateTimeUtils
import kotlinx.coroutines.delay

// Shared 1-second tick for any "time-limited offer" UI (Home card/chip, Purchase
// page, paywall sheet, ...) -- only the caller recomposes, not the whole screen.
// Wall-clock based: remaining time is recomputed from the fixed end time on every
// tick, so backgrounding or a relaunch can never drift the countdown.
@Composable
fun rememberCountdownRemainingSeconds(endMillis: Long, onExpired: () -> Unit): Long {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(endMillis) {
        while (true) {
            now = System.currentTimeMillis()
            if (endMillis - now <= 0L) { onExpired(); break }
            delay(1000L - now % 1000L)
        }
    }
    return ((endMillis - now).coerceAtLeast(0L) + 999L) / 1000L
}

// Small live countdown pill meant to sit right beside a "% OFF" label on a plan
// card/row (Purchase page, paywall sheet), so the urgency is tied directly to the
// specific discounted plan rather than floating as a separate banner.
@Composable
fun InlineCountdownPill(endMillis: Long, onExpired: () -> Unit, modifier: Modifier = Modifier) {
    val remainingSec = rememberCountdownRemainingSeconds(endMillis, onExpired)
    val endingSoon = remainingSec <= 60L
    Text(
        text = stringResource(R.string.home_offer_ends_in, DateTimeUtils.displayDurationHourMinSec(remainingSec)),
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
        style = MaterialTheme.typography.labelSmall.scaled(),
        modifier = modifier
            .background(if (endingSoon) Color(0xFFE53935) else Color(0xFFFF7043), RoundedCornerShape(percent = 50))
            .padding(horizontal = AppDimens.Dimens8, vertical = 2.dp)
    )
}
