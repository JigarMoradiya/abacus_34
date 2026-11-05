package com.jigar.me.ui.jetpack.utils.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.jigar.me.data.local.data.DeviceInfo

fun appScale(): Float {
    val phoneScale = 1f
    val tabletScale = 1.3f
    val largeTabletScale = 1.5f

    return if (DeviceInfo.isLargeTablet) {
        largeTabletScale
    } else if (DeviceInfo.isTablet) {
        tabletScale
    } else {
        phoneScale
    }
}

@Composable
fun TextStyle.scaled(): TextStyle {
    val scale = appScale()

    return this.copy(
        fontSize = if (fontSize != TextUnit.Unspecified) {
            fontSize * scale
        } else fontSize,

        lineHeight = if (lineHeight != TextUnit.Unspecified) {
            lineHeight * scale
        } else lineHeight
    )
}

@Composable
fun TextUnit.scaled(): TextUnit {
    return this * appScale()
}

@Composable
fun Dp.scaled(): Dp {
    val scale = appScale()
    return this * scale
}