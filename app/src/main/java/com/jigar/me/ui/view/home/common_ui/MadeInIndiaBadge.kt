package com.jigar.me.ui.view.home.common_ui

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.home.theme.AppDimens
import java.util.Locale

// "Underline Chip" on the kit surface: an icon + label over a thick gradient bar flush along the bottom edge, matching
// the Home screen's other small chips (This Week, Vedaavi English). Only Home shows it, so it always wears the kit look.
// [backgroundColor] is kept for the callers; the kit surface replaces the old pale per-chip colour in both modes.
@Suppress("UNUSED_PARAMETER")
@Composable
fun UnderlineChip(
    icon: String,
    label: String,
    barColors: List<Color>,
    backgroundColor: Color,
    borderColor: Color,
) {
    val colors = LocalAbColors.current
    val shape = RoundedCornerShape(AbShapes.sm)
    val k = if (AppDimens.isLargeTablet) 1.7f else if (AppDimens.isTablet) 1.5f else 1f
    val shadowColor = (if (colors.isDark) Color.Black else colors.outline).copy(alpha = 0.6f)
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .shadow(elevation = 3.dp, shape = shape, ambientColor = shadowColor, spotColor = shadowColor)
            .clip(shape)
            .background(colors.bgSecondary)
            .drawBehind {
                val bar = 5.dp.toPx()
                drawRect(Brush.horizontalGradient(barColors), Offset(0f, size.height - bar), Size(size.width, bar))
            }
            .border(2.dp, if (colors.isDark) borderColor.copy(alpha = 0.8f) else borderColor, shape)
            .padding(horizontal = 12.dp)
            .padding(top = 6.dp, bottom = 10.dp),
    ) {
        Text(text = icon, fontSize = (14 * k).sp)
        Text(
            text = label,
            style = AbTypography.fixed(12f * k, FontWeight.ExtraBold),
            color = colors.textPrimary,
            maxLines = 1,
        )
    }
}

// Shown only for Indian users, filling otherwise-empty space on the Home screen.
@Composable
fun MadeInIndiaBadge() {
    UnderlineChip(
        icon = "🇮🇳",
        label = "Made in India",
        barColors = listOf(Color(0xFFFF9933), Color.White, Color(0xFF138808)),
        backgroundColor = Color(0xFFFFE0B2),
        borderColor = Color(0xFFFF9933).copy(alpha = 0.35f)
    )
}

// Shown for non-Indian users in the same slot as MadeInIndiaBadge -- a
// universal trust claim instead of a locale-specific one.
@Composable
fun SafeAdFreeBadge() {
    UnderlineChip(
        icon = "🛡️",
        label = "Safe & Ad-Free",
        barColors = listOf(Color(0xFF42A5F5), Color(0xFF26A69A)),
        backgroundColor = Color(0xFFB2DFDB),
        borderColor = Color(0xFF26A69A).copy(alpha = 0.35f)
    )
}

// Device locale alone is unreliable -- plenty of users in India leave their
// phone's display language set to "English (United States)" or similar, which
// reports as a non-IN locale even though they're clearly Indian (e.g. billed
// in INR). SIM/network country comes from the actual carrier, not a display
// preference, so it catches that case; locale is kept as a fallback for
// SIM-less devices (tablets, Wi-Fi only).
fun isIndianUser(context: Context): Boolean {
    if (Locale.getDefault().country.equals("IN", ignoreCase = true)) return true
    return try {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        telephonyManager?.simCountryIso?.equals("in", ignoreCase = true) == true ||
            telephonyManager?.networkCountryIso?.equals("in", ignoreCase = true) == true
    } catch (e: Exception) {
        false
    }
}
