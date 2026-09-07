package com.jigar.me.ui.view.home.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

// Pale-tinted "Underline Chip": an icon + label with most of the color kept
// to a thick gradient bar flush along the bottom edge, matching the Home
// screen's other small chips (This Week, Vedaavi English).
// `Modifier.width(IntrinsicSize.Max)` makes the Column hug the text row's
// natural width instead of the bar's fillMaxWidth stretching it out.
// (IntrinsicSize.Min is the wrong tool here: for non-wrapping text, the
// minimum width before height must increase degenerates toward zero, since
// height never changes with width when there's nothing to wrap -- Max asks
// for the natural single-line width instead.)
@Composable
fun UnderlineChip(
    icon: String,
    label: String,
    barColors: List<Color>,
    backgroundColor: Color,
    borderColor: Color,
) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .shadow(elevation = 3.dp, shape = shape, ambientColor = Color.Black.copy(alpha = 0.06f), spotColor = Color.Black.copy(alpha = 0.06f))
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 7.dp, bottom = 5.dp)
        ) {
            Text(text = icon, fontSize = 13.sp)
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.65f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(Brush.horizontalGradient(barColors))
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

// Device-locale-based India check -- no network call, matches how most apps do this.
fun isIndianUser(): Boolean = Locale.getDefault().country == "IN"
