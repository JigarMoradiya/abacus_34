package com.jigar.me.ui.view.home.common_ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.math_game_zone.toDrawable
import com.jigar.me.ui.view.home.theme.MyApplicationTheme

@Composable
fun BackgroundUI(
    isGreenGrassShow: Boolean = true
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFFFFFFFF)
//                    Color(0xFFEFEFEF)
//                    Brush.verticalGradient(
//                        colors = listOf(
//                            Color(0xFFFCF7F7),
//                            Color(0xFFEFEEEE),
//                            Color(0xFFEFEFEF),
//                        )
//                    )
                )
        )

        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.background_new),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.07f
        )


    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=1280dp,height=720dp,dpi=240" // landscape feel
)
@Composable
fun BackgroundUIPreview() {
    MyApplicationTheme {
        BackgroundUI(isGreenGrassShow = true)
    }
}