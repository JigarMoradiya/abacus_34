package com.jigar.me.ui.view.home.common_ui

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ShadowOffset
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.text.style.TextOverflow
import com.jigar.me.ui.view.ab.AbBackButton
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abChunkyFace

@Composable
fun BackButtonWithText(
    title: String,
    modifier: Modifier = Modifier,
    size: Dp = ToolbarIconSize,   // kept for the old call sites: the kit bar has one size
    enabled : Boolean = true,
    onBackClick: () -> Unit
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Row(
        modifier = modifier.padding(top = DeviceInfo.screenTopPadding(), bottom = Dimens8, start = DeviceInfo.screenHorizontalPadding(), end = Dimens16),
        horizontalArrangement = Arrangement.spacedBy(12.dp * s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AbBackButton(onClick = { if (enabled) onBackClick() })
        Box(
            Modifier
                .abChunkyFace(colors.brandPrimary, colors.brandPrimaryDark, null, AbShapes.pill, 4.dp * s)
                .heightIn(min = 36.dp * s)
                .padding(horizontal = 20.dp * s),
            contentAlignment = Alignment.Center,
        ) {
            Text(title, style = AbTypography.headline(s), color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}