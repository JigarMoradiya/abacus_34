package com.jigar.me.ui.view.ab

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The big title of a hub screen: the name in heavy Baloo 2 with a small tagline under it (optionally followed by a small
 * picture, e.g. the abacus emoji). Centred; the title shrinks (to 60%) rather than wrap.
 *
 * Sizes follow iOS `responsiveDimenstion` (1x phone, 1.5x tablet, 1.7x large tablet) rather than the kit factor, because
 * the iOS hero title does; the two tablet classes are told apart by the kit factor (1.35 / 1.5).
 *
 * @param taglineIcon a picture after the tagline (e.g. `painterResource(R.drawable.abacus_emoji)`), 14 dp on a phone and
 * 18 dp on a tablet.
 */
@Composable
fun AbHeroTitle(
    title: String,
    modifier: Modifier = Modifier,
    tagline: String? = null,
    taglineIcon: Painter? = null,
) {
    val colors = LocalAbColors.current
    val scale = LocalAbScale.current
    val density = LocalDensity.current

    // iOS responsiveDimenstion(base) and DimensionsUtils.padding4
    val responsive = when {
        !scale.isTablet -> 1f
        scale.factor >= 1.5f -> 1.7f
        else -> 1.5f
    }
    val titleSize = 40f * responsive
    val tagSize = 12f * responsive
    val gap = when {
        !scale.isTablet -> 4.dp
        scale.factor >= 1.5f -> 8.dp
        else -> 6.dp
    }
    val iconSize = if (scale.isTablet) 18.dp else 14.dp

    // Baloo's line box is tall, so each line gets a box of about its own size and the two sit close together.
    val titleStyle = AbTypography.fixed(titleSize, FontWeight.ExtraBold).copy(
        lineHeight = (titleSize * 1.02f).sp,
        shadow = if (colors.isDark) null else Shadow(
            color = colors.brandPrimary.copy(alpha = 0.18f),
            offset = Offset(0f, 2f * density.density),
            blurRadius = 4f * density.density,
        ),
    )

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AbShrinkText(
            text = title,
            style = titleStyle,
            color = colors.textPrimary,
            minScale = 0.6f,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
        if (tagline != null) {
            Row(
                // the tagline is pulled up under the title so the gap equals iOS (measured: 11 pt from baseline to the tagline's cap top)
                Modifier.offset(y = (-titleSize * 0.3f).dp).height((tagSize * 1.25f).dp),
                horizontalArrangement = Arrangement.spacedBy(gap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    tagline,
                    style = AbTypography.fixed(tagSize, FontWeight.Bold),
                    color = colors.textSecondary,
                    maxLines = 1,
                )
                if (taglineIcon != null) {
                    Image(taglineIcon, null, Modifier.size(iconSize), contentScale = ContentScale.Fit)
                }
            }
        }
    }
}
