package com.jigar.me.data.local.data

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8

object DeviceInfo {
    var hasNotch: Boolean = false
    var isTablet: Boolean = false
    var isLargeTablet: Boolean = false

    fun screenHorizontalPadding() : Dp {
        return if (hasNotch) {
            0.dp
        } else {
            Dimens16
        }
    }

    fun screenTopPadding() : Dp {
        return if (isTablet) {
            0.dp
        } else {
            Dimens8
        }
    }
}