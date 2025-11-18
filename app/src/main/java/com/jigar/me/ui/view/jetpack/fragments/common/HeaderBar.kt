package com.jigar.me.ui.view.jetpack.fragments.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.jigar.me.R


@Composable
fun HeaderBar(
    title: String = "Number Sequences Puzzle", onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                onClick = { onBackClick() },
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.light_back)
                ),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.menu_icons_corner)),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = dimensionResource(id = R.dimen.card_elevation)
                ),
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.icons_margin2))
                    .size(dimensionResource(id = R.dimen.menu_icons))
            ) {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.menu_icons))
                            .padding(dimensionResource(id = R.dimen.menu_icons_padding))
                    )
                }
            }

            // 🔹 Center Title (same as MaterialTextView in XML)
            Text(
                text = title,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                fontSize = dimensionResource(id = R.dimen.textSize24).value.sp,
                color = colorResource(id = R.color.colorPrimaryDark),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.CenterHorizontally),
            )

        }
    }
}

@Composable
fun BackButtonWithText(
    title: String,
    color: Color = Color(0xFF9C27B0),
    onBackClick: () -> Unit
) {
    val capsuleHeight = 34.dp
    val circleSize = 42.dp
    val iconSize = 20.dp
    val overlapOffset = (circleSize - capsuleHeight) / 2f // how much the circle should overlap
    val shape = RoundedCornerShape(100.dp)
    Box(
        modifier = Modifier
            .padding(start = 16.dp, top = 12.dp, end = 16.dp)
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
                .clip(shape)                                   // 🔥 makes ripple rounded
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current,      // 🔥 Material3 ripple
                ) {
                    onBackClick()
                }
                .padding(start = circleSize / 2 + 12.dp, end = 16.dp) // leave space for circle overlap
        ) {
            // Title centered vertically inside capsule
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = title,
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    fontSize = dimensionResource(id = R.dimen.textSizeTitle).value.sp,
                    color = color,
                    modifier = Modifier.padding(start = 16.dp)
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


@Preview(showBackground = true)
@Composable
fun BackButtonWithTextPreview() {
    MaterialTheme {
        BackButtonWithText(
            title = "Abacus Child Learning App",
            onBackClick = {}
        )
    }
}
