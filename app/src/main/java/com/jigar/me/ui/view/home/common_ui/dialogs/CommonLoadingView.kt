package com.jigar.me.ui.view.home.common_ui.dialogs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R

@Composable
fun CommonLoadingView(
    title: String? = null,
    text: String? = null,
    dotStyle: String = "square"   // 👉 circle | square | glow | emoji
) {

    val dot1 = remember { Animatable(0f) }
    val dot2 = remember { Animatable(0f) }
    val dot3 = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            dot1.animateTo(1f, tween(300))
            dot2.animateTo(1f, tween(300))
            dot3.animateTo(1f, tween(300))
            dot1.snapTo(0f); dot2.snapTo(0f); dot3.snapTo(0f)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
//            .background(Color.Black.copy(alpha = 0.15f))
        ,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Dot(dot1.value, dotStyle,Color(0xFF3F51B5))
                Dot(dot2.value, dotStyle,Color(0xFF009688))
                Dot(dot3.value, dotStyle,Color(0xFFBD4811))
            }

            Spacer(Modifier.height(16.dp))

            if (!title.isNullOrEmpty()){
                Text(
                    text = title,
                    fontSize = dimensionResource(R.dimen.textSizeExtraLarge).value.sp,
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    color = Color(0xFF333333)
                )
            }

            if (!text.isNullOrEmpty()){
                Text(
                    text = text,
                    fontSize = dimensionResource(R.dimen.textSize20).value.sp,
                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                    color = Color(0xFF000000)
                )
            }
        }
    }
}

@Composable
private fun Dot(scale: Float, style: String,color : Color) {

    when (style) {

        // ● Circle Dots
        "circle" -> Box(
            Modifier
                .size(16.dp)
                .graphicsLayer {
                    scaleX = 0.5f + scale * 0.5f
                    scaleY = 0.5f + scale * 0.5f
                }
                .background(color, CircleShape)
        )

        // ■ Square Blocks
        "square" -> Box(
            Modifier
                .size(16.dp)
                .graphicsLayer {
                    scaleX = 0.4f + scale * 0.6f
                    scaleY = 0.4f + scale * 0.6f
                }
                .background(color, RoundedCornerShape(4.dp))
        )

        // ✨ Glowing Dots
        "glow" -> Box(
            Modifier
                .size(16.dp)
                .graphicsLayer {
                    scaleX = 0.6f + scale * 0.8f
                    scaleY = 0.6f + scale * 0.8f
                    alpha = 0.7f + scale * 0.3f
                }
                .shadow(12.dp, CircleShape)
                .background(Color(0xFFFFC107), CircleShape)
        )

        // 😄 Emoji Loading Dots
        "emoji" -> {
            val emojis = listOf("✨", "🟢", "🔵", "🟡", "🧩")
            val emoji = emojis[(scale * 4).toInt().coerceIn(0, 4)]

            Text(
                text = emoji,
                fontSize = 24.sp,
                modifier = Modifier.graphicsLayer {
                    scaleX = 0.5f + scale * 0.7f
                    scaleY = 0.5f + scale * 0.7f
                }
            )
        }

        else -> {}
    }
}

