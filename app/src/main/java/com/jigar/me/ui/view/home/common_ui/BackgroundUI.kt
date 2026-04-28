package com.jigar.me.ui.view.home.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x0FFFB300),
                            Color(0x19FB8C00),
                            Color(0x20F4511E),
                        )
                    )
                )
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