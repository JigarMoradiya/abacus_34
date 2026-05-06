package com.jigar.me.ui.view.home.common_ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.jigar.me.R

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
