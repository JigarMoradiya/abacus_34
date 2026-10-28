package com.jigar.me.ui.view.home.screens.activities

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.home.theme.AppDimens

/**
 * Sizes shared by the restyled Exercise, Exam and Custom Challenge screens, per device class (phone / tablet / large
 * tablet). The same values as the iOS `DimensionsUtils` the screens were tuned with.
 */
internal object ActivityDimens {
    private fun pick(phone: Int, tablet: Int, large: Int): Dp =
        (if (DeviceInfo.isLargeTablet) large else if (DeviceInfo.isTablet) tablet else phone).dp

    val keypadItem: Dp get() = pick(36, 48, 60)
    val exerciseGridSize: Dp get() = pick(32, 48, 60)
    val exerciseItemGap: Dp get() = pick(10, 24, 30)
    val examOptionWidth: Dp get() = pick(200, 280, 360)
    val examOptionHeight: Dp get() = pick(40, 60, 72)
    val examHomeTopIcon: Dp get() = pick(48, 80, 100)
    val ccmHomeTopIcon: Dp get() = pick(40, 80, 100)
    val homeTitleTopSpacing: Dp get() = pick(16, 24, 32)
    val homeStartButtonTopSpacing: Dp get() = pick(16, 30, 36)
    val examTypeCardWidth: Dp get() = pick(120, 160, 200)
    val examDiffCardWidth: Dp get() = pick(108, 150, 170)
    val ccmToggleCardWidth: Dp get() = pick(68, 90, 110)
    val ccmKidTopIcon: Dp get() = pick(72, 100, 130)
    val ccmNumberTextSize: Float get() = pick(48, 100, 130).value
    val ccmWordTextSize: Float get() = pick(18, 30, 36).value

    /** Width of the empty slots either side of an exam question; the cheering friend stands in the left one. */
    val examMascotSlot: Dp get() = pick(124, 190, 230)
}

/** The six cheers a friend can call out after a right answer. */
@Composable
internal fun rememberEncouragements(): List<String> = listOf(
    stringResource(R.string.good_job),
    stringResource(R.string.you_are_clever),
    stringResource(R.string.you_are_glorious),
    stringResource(R.string.you_are_brilliant),
    stringResource(R.string.you_are_so_genius),
    stringResource(R.string.you_are_so_intelligent),
)

/**
 * The header of every restyled activity screen: the back circle and the title pill on the app's screen edge, with the
 * screen's own extras (a progress bar, a timer) at the trailing end.
 */
@Composable
internal fun ActivityTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    AbTopBar(
        title = title,
        onBack = onBack,
        modifier = modifier.padding(
            start = DeviceInfo.screenHorizontalPadding(),
            end = AppDimens.Dimens16,
            top = DeviceInfo.screenTopPadding(),
        ),
        trailing = trailing,
    )
}

/**
 * How far the toy answer bar hangs above the abacus frame. The abacus draws the bar outside its own bounds, so a screen
 * that fits or centres the abacus adds this room above it: the bar then stays inside the box and scales with the abacus.
 */
@Composable
internal fun rememberToyBarRoom(screenType: String): Dp {
    val context = LocalContext.current
    val s = LocalAbScale.current.factor
    return remember(context, screenType, s) {
        70.dp * s - AbacusTheme.dimensionPreset(context, screenType = screenType).rectLineWidth * 0.4f
    }
}
