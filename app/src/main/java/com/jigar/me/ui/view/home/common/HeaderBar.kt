package com.jigar.me.ui.view.home.common

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.category.viewmodels.CategoryViewModel

@Composable
fun BackButtonWithText(
    title: String,
    color: Color = colorResource(R.color.back_icon_bg),
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val capsuleHeight = dimensionResource(R.dimen.menu_icons_text)
    val circleSize = dimensionResource(R.dimen.menu_icons_bg)
    val iconSize = dimensionResource(R.dimen.menu_icons)
    val overlapOffset = (circleSize - capsuleHeight) / 2f // how much the circle should overlap
    val shape = RoundedCornerShape(100.dp)
    Box(
        modifier = modifier
            .padding(top = dimensionResource(R.dimen.activity_padding12))
            .wrapContentHeight()
    ) {
        // 1) Capsule background (full width as needed)
        Box(
            modifier = Modifier
                .height(capsuleHeight)
                .clip(
                    RoundedCornerShape(
                        topStart = 100.dp,
                        bottomStart = 100.dp,
                        topEnd = 100.dp,
                        bottomEnd = 100.dp
                    )
                )
                .background(colorResource(R.color.back_icon_text_bg))
                .clip(shape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current,
                ) {
                    onBackClick()
                }
                .padding(start = circleSize / 2 + dimensionResource(R.dimen.activity_padding12), end = dimensionResource(R.dimen.activity_padding16)) // leave space for circle overlap
        ) {
            // Title centered vertically inside capsule
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(color = color,fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold))),
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.activity_padding16))
                )
            }
        }

        // 2) Circle overlapping on top (placed at start)
        Box(
            modifier = Modifier
                .size(circleSize)
                .align(Alignment.CenterStart)                  // place at left center of parent Box
                .offset(x = 0.dp, y = (-overlapOffset))      // shift it so it overlaps capsule
                .zIndex(1f)                                    // ensure it's above the capsule
                .background(color, CircleShape)
                .clip(CircleShape)                                   // 🔥 makes ripple rounded
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current,      // 🔥 Material3 ripple
                ) {
                    onBackClick()
                },                       // make circle clickable
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun HowToPlayButton(
    color: Color = Color(0xFF24A229),
    onClick: () -> Unit
) {
    val capsuleHeight = 30.dp
    val shape = RoundedCornerShape(100.dp)
    Box(
        modifier = Modifier
            .padding(start = dimensionResource(R.dimen.activity_padding16), top = dimensionResource(R.dimen.activity_padding12), end = dimensionResource(R.dimen.activity_padding16))
            .wrapContentHeight()
    ) {
        // 1) Capsule background (full width as needed)
        Box(
            modifier = Modifier
                .height(capsuleHeight)
                .clip(
                    RoundedCornerShape(
                        topStart = 100.dp,
                        bottomStart = 100.dp,
                        topEnd = 100.dp,
                        bottomEnd = 100.dp
                    )
                )
                .background(color.copy(alpha = 0.15f))
                .clip(shape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current,
                ) {
                    onClick()
                }
                .padding(horizontal = dimensionResource(R.dimen.activity_padding16)) // leave space for circle overlap
        ) {
            // Title centered vertically inside capsule
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = stringResource(R.string.how_to_play),
                    style = MaterialTheme.typography.labelLarge.copy(color = color,fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold)))
                )
            }
        }
    }
}