package com.jigar.me.ui.view.home.theme

import androidx.compose.ui.unit.dp
import com.jigar.me.data.local.data.DeviceInfo

object AppDimens {
    val isTablet = DeviceInfo.isTablet
    val isLargeTablet = DeviceInfo.isLargeTablet
    // Spacing
    val ToolbarIconSize = if(isLargeTablet) 72.dp else if (isTablet) 64.dp else 42.dp
    val KidsIconSize = if(isLargeTablet) 84.dp else if (isTablet) 72.dp else 56.dp
    val KidIconMedium = if(isLargeTablet) 56.dp else if (isTablet) 48.dp else 40.dp
    val KidIconSmall = if(isLargeTablet) 50.dp else if (isTablet) 40.dp else 30.dp
    val ShadowOffset = if(isLargeTablet) 3.dp else if (isTablet) 2.5.dp else 1.5.dp
    val ShadowOffsetText = if(isLargeTablet) 3.dp else if (isTablet) 2.dp else 1.dp
    val Dimens1 = if(isLargeTablet) 3.dp else if (isTablet) 2.dp else 1.dp
    val Dimens2 = if(isLargeTablet) 4.dp else if (isTablet) 3.dp else 2.dp
    val Dimens3 = if(isLargeTablet) 6.dp else if (isTablet) 4.dp else 3.dp
    val Dimens4 = if(isLargeTablet) 8.dp else if (isTablet) 6.dp else 4.dp
    val Dimens6 = if(isLargeTablet) 12.dp else if (isTablet) 8.dp else 6.dp
    val Dimens8 = if(isLargeTablet) 16.dp else if (isTablet) 12.dp else 8.dp
    val Dimens10 = if(isLargeTablet) 20.dp else if (isTablet) 13.dp else 10.dp
    val Dimens12 = if(isLargeTablet) 24.dp else if (isTablet) 16.dp else 12.dp
    val Dimens16 = if(isLargeTablet) 30.dp else if (isTablet) 22.dp else 16.dp
    val Dimens20 = if(isLargeTablet) 40.dp else if (isTablet) 30.dp else 20.dp
    val Dimens24 = if(isLargeTablet) 48.dp else if (isTablet) 36.dp else 24.dp
    val Dimens28 = if(isLargeTablet) 56.dp else if (isTablet) 36.dp else 28.dp
    val Dimens32 = if(isLargeTablet) 64.dp else if (isTablet) 44.dp else 32.dp
    val Dimens40 = if(isLargeTablet) 80.dp else if (isTablet) 48.dp else 40.dp
    val Dimens50 = if(isLargeTablet) 100.dp else if (isTablet) 60.dp else 50.dp
    val DimensColorCircles = if(isLargeTablet) 56.dp else if (isTablet) 48.dp else 36.dp
    val CommonPopupImageSize = if(isLargeTablet) 120.dp else if (isTablet) 96.dp else 72.dp

    val HomePageLogo = if(isLargeTablet) 90.dp else if (isTablet) 72.dp else 56.dp
}