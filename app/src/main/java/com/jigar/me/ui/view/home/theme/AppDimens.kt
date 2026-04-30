package com.jigar.me.ui.view.home.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val Dimens25 = if(isLargeTablet) 50.dp else if (isTablet) 36.dp else 25.dp
    val Dimens30 = if(isLargeTablet) 60.dp else if (isTablet) 44.dp else 30.dp
    val Dimens36 = if(isLargeTablet) 72.dp else if (isTablet) 54.dp else 36.dp
    val Dimens45 = if(isLargeTablet) 90.dp else if (isTablet) 64.dp else 45.dp
    val Dimens46 = if(isLargeTablet) 92.dp else if (isTablet) 64.dp else 46.dp
    val Dimens48 = if(isLargeTablet) 96.dp else if (isTablet) 70.dp else 48.dp
    val Dimens56 = if(isLargeTablet) 110.dp else if (isTablet) 80.dp else 56.dp
    val Dimens60 = if(isLargeTablet) 120.dp else if (isTablet) 84.dp else 60.dp
    val Dimens64 = if(isLargeTablet) 128.dp else if (isTablet) 90.dp else 64.dp
    val Dimens72 = if(isLargeTablet) 144.dp else if (isTablet) 100.dp else 72.dp
    val Dimens80 = if(isLargeTablet) 160.dp else if (isTablet) 112.dp else 80.dp
    val Dimens90 = if(isLargeTablet) 180.dp else if (isTablet) 128.dp else 90.dp
    val Dimens100 = if(isLargeTablet) 200.dp else if (isTablet) 144.dp else 100.dp
    val Dimens110 = if(isLargeTablet) 220.dp else if (isTablet) 156.dp else 110.dp
    val Dimens120 = if(isLargeTablet) 240.dp else if (isTablet) 168.dp else 120.dp
    val Dimens150 = if(isLargeTablet) 300.dp else if (isTablet) 210.dp else 150.dp
    val Dimens180 = if(isLargeTablet) 360.dp else if (isTablet) 250.dp else 180.dp
    val Dimens200 = if(isLargeTablet) 400.dp else if (isTablet) 280.dp else 200.dp
    val Dimens240 = if(isLargeTablet) 480.dp else if (isTablet) 336.dp else 240.dp
    val Dimens260 = if(isLargeTablet) 520.dp else if (isTablet) 364.dp else 260.dp
    val Dimens300 = if(isLargeTablet) 600.dp else if (isTablet) 420.dp else 300.dp
    val Dimens320 = if(isLargeTablet) 640.dp else if (isTablet) 448.dp else 320.dp
    val Dimens600 = if(isLargeTablet) 1200.dp else if (isTablet) 840.dp else 600.dp
    val examOptionWidth = if(isLargeTablet) 360.dp else if (isTablet) 330.dp else 240.dp
    val examOptionHeight = if(isLargeTablet) 84.dp else if (isTablet) 60.dp else 42.dp
    val keyPadHeight = if(isLargeTablet) 80.dp else if (isTablet) 60.dp else 40.dp
    val keyPadWidth = if(isLargeTablet) 100.dp else if (isTablet) 80.dp else 56.dp

    val HomePageLogo = if(isLargeTablet) 90.dp else if (isTablet) 72.dp else 56.dp
    val exerciseWidth = if(isLargeTablet) 420.dp else if (isTablet) 360.dp else 300.dp
    val doPracticeNumberFonts = if(isLargeTablet) 140.sp else if (isTablet) 110.sp else 78.sp
}