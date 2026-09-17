package com.jigar.me.ui.view.home.screens.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.rememberCountdownRemainingSeconds
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.DateTimeUtils

// Clamped to [0, durationMin] so a device clock moved backwards can't make the
// card advertise more time than the offer could ever have (matches
// HomeOfferManager.remainingMillis's clamp).
@Composable
private fun rememberOfferRemainingSeconds(startedAtMillis: Long, durationMin: Int, onExpired: () -> Unit): Long {
    val endMillis = startedAtMillis + durationMin * 60_000L
    val remainingSec = rememberCountdownRemainingSeconds(endMillis, onExpired)
    return remainingSec.coerceAtMost(durationMin * 60L)
}

// Full-size offer strip -- used on tablet, which has room for its own dedicated
// row without squeezing the activity tile list.
@Composable
fun HomeOfferCard(
    title: String?,          // campaign name from Remote Config, null -> generic title
    startedAtMillis: Long,
    durationMin: Int,
    discountPercent: Int,
    targetsLifetime: Boolean,
    onClick: () -> Unit,
    onExpired: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val remainingSec = rememberOfferRemainingSeconds(startedAtMillis, durationMin, onExpired)
    val endingSoon = remainingSec <= 60L

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "offerScale")
    val shape = RoundedCornerShape(AppDimens.Dimens16)

    Row(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF7043))))
            .border(2.dp, Color.White.copy(alpha = 0.7f), shape)
            .clickable(interactionSource = interaction, indication = null) { onClick() }
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🏷️", style = MaterialTheme.typography.titleLarge.scaled())
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title?.takeIf { it.isNotBlank() } ?: stringResource(R.string.home_offer_title),
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                style = MaterialTheme.typography.titleSmall.scaled(),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            val productLabel = stringResource(
                if (targetsLifetime) R.string.home_offer_product_lifetime else R.string.home_offer_product_yearly
            )
            Text(
                text = if (discountPercent > 0) stringResource(R.string.home_offer_save_pct, discountPercent, productLabel)
                       else stringResource(R.string.home_offer_special_price, productLabel),
                color = Color.White.copy(alpha = 0.9f),
                fontFamily = FontFamily(Font(R.font.font_semibold)),
                style = MaterialTheme.typography.labelMedium.scaled(),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        Text(
            text = stringResource(R.string.home_offer_ends_in, DateTimeUtils.displayDurationHourMinSec(remainingSec)),
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            style = MaterialTheme.typography.labelLarge.scaled(),
            modifier = Modifier
                .background(if (endingSoon) Color(0xFFE53935) else Color.Black.copy(alpha = 0.2f), CircleShape)
                .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens4)
        )
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.9f))
    }
}

// Compact pill -- used on phone, in the same bottom-row slot as WeeklyReportCard/
// MadeInIndiaBadge/EnglishBanner, so enabling the timed offer never shrinks the
// activity tile list above it (unlike the full HomeOfferCard in its own row).
@Composable
fun HomeOfferBadge(
    title: String?,
    startedAtMillis: Long,
    durationMin: Int,
    discountPercent: Int,
    targetsLifetime: Boolean,
    onClick: () -> Unit,
    onExpired: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val remainingSec = rememberOfferRemainingSeconds(startedAtMillis, durationMin, onExpired)
    val endingSoon = remainingSec <= 60L
    val shape = RoundedCornerShape(percent = 50)

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, label = "offerBadgeScale")

    Row(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(Brush.horizontalGradient(listOf(Color(0xFFFFB300).copy(alpha = 0.16f), Color(0xFFFF7043).copy(alpha = 0.16f))), shape)
            .border(1.dp, Color(0xFFFF7043).copy(alpha = 0.4f), shape)
            .clickable(interactionSource = interaction, indication = null) { onClick() }
            .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)
    ) {
        Text("🏷️", style = MaterialTheme.typography.labelSmall.scaled())
        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(
                text = title?.takeIf { it.isNotBlank() } ?: stringResource(R.string.home_offer_title),
                color = Color(0xFFE65100),
                fontFamily = FontFamily(Font(R.font.font_bold)),
                style = MaterialTheme.typography.labelSmall.scaled(),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            val productLabel = stringResource(
                if (targetsLifetime) R.string.home_offer_product_lifetime else R.string.home_offer_product_yearly
            )
            val pctText = if (discountPercent > 0) stringResource(R.string.home_offer_save_pct, discountPercent, productLabel)
                          else stringResource(R.string.home_offer_special_price, productLabel)
            Text(
                text = pctText,
                color = Color(0xFFE65100).copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall.scaled(),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        // Countdown gets its own highlighted pill -- distinct from the surrounding
        // text so it draws the eye, matching the full HomeOfferCard's treatment.
        Text(
            text = DateTimeUtils.displayDurationHourMinSec(remainingSec),
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            style = MaterialTheme.typography.labelSmall.scaled(),
            maxLines = 1,
            modifier = Modifier
                .background(if (endingSoon) Color(0xFFE53935) else Color(0xFFFF7043), RoundedCornerShape(percent = 50))
                .padding(horizontal = AppDimens.Dimens8, vertical = 2.dp)
        )
    }
}
