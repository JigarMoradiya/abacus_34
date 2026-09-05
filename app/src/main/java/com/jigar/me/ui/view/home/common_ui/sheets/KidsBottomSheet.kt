package com.jigar.me.ui.view.home.common_ui.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun KidsBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    widthFraction: Float = 0.75f,
    overlay: @Composable (androidx.compose.foundation.layout.BoxScope.() -> Unit) = {},
    // Optional theming hooks -- default values reproduce the original plain white
    // sheet exactly, so existing call sites are visually untouched.
    containerBackground: Brush? = null,
    handleColor: Color = Color(0xFFDDDDDD),
    // Optional decorative layer (confetti/sparkles) painted behind the content,
    // inside the sheet's clipped bounds. No-op by default -- existing call sites unaffected.
    decoration: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit = {},
    // Optional "sticker" outline drawn along the sheet's rounded top edge. Null by default.
    borderColor: Color? = null,
    // Drag handle at the top of the sheet. True by default (existing call sites unaffected);
    // set false for sheets with no drag-to-dismiss gesture (e.g. a centered card dismissed by button).
    showHandle: Boolean = true,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(200)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() }
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(320)),
                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(260)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AppDimens.Dimens12)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(widthFraction)
                        .wrapContentHeight()
                        .shadow(elevation = 16.dp, shape = RoundedCornerShape(AppDimens.Dimens24))
                        .clip(RoundedCornerShape(AppDimens.Dimens24))
                        .then(
                            if (containerBackground != null) Modifier.background(containerBackground)
                            else Modifier.background(Color.White)
                        )
                        .then(
                            if (borderColor != null) Modifier.border(
                                width = 1.5.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(AppDimens.Dimens24)
                            ) else Modifier
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { }
                ) {
                    decoration()
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (showHandle) {
                            Spacer(modifier = Modifier.height(AppDimens.Dimens8))
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(100f))
                                    .background(handleColor)
                            )
                            Spacer(modifier = Modifier.height(AppDimens.Dimens4))
                        }
                        content()
                    }
                }
            }

            // Full-screen overlay slot — renders inside the fillMaxSize Box,
            // so it sits on top of everything including the sheet panel.
            overlay()
        }
    }
}
