package com.jigar.me.ui.view.ab

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A one-line form field with a leading icon, a disabled (pre-filled, padlocked) look and a friendly error line underneath.
 * The outline turns brand blue and thickens while focused, red while [error] is set; tapping anywhere on the field
 * focuses it.
 *
 * @param icon the leading icon (the iOS kit passes an SF Symbol name).
 * @param capitalization `KeyboardCapitalization.None` for an e-mail / password, `Words` for a name.
 * @param imeAction / [keyboardActions] Android extras (iOS has no equivalent): the keyboard's action key.
 */
@Composable
fun AbFormField(
    icon: ImageVector,
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    isSecure: Boolean = false,
    isDisabled: Boolean = false,
    error: String? = null,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.md * s)
    var focused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val hasError = error != null

    val ring by animateColorAsState(
        when {
            hasError -> colors.danger
            focused -> colors.brandPrimary
            else -> colors.outline
        },
        tween(AbMotion.FAST, easing = AbEaseOut), label = "abFormRing",
    )
    val ringWidth by animateDpAsState(
        if (focused || hasError) 3.dp else 2.dp,
        tween(AbMotion.FAST, easing = AbEaseOut), label = "abFormRingWidth",
    )
    val iconTint = when {
        hasError -> colors.danger
        focused -> colors.brandPrimary
        else -> colors.textSecondary
    }
    val textStyle = AbTypography.body(s).copy(
        fontWeight = FontWeight.Bold,
        color = if (isDisabled) colors.textSecondary else colors.textPrimary,
    )
    val plate = colors.surfaceTint.copy(alpha = if (isDisabled) 0.8f else if (colors.isDark) 0.5f else 0.6f)

    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp * s)) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp * s)
                .background(plate, shape)
                .border(ringWidth, ring, shape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() }, indication = null,
                    enabled = !isDisabled,
                ) { focusRequester.requestFocus() }
                .padding(horizontal = 14.dp * s),
            horizontalArrangement = Arrangement.spacedBy(10.dp * s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.width(22.dp * s), contentAlignment = Alignment.Center) {
                Icon(icon, null, Modifier.size((16f * AB_SF_TO_VECTOR).dp * s), tint = iconTint)
            }
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focused = it.isFocused },
                enabled = !isDisabled,
                textStyle = textStyle,
                singleLine = true,
                cursorBrush = SolidColor(colors.brandPrimary),
                keyboardOptions = KeyboardOptions(
                    capitalization = capitalization,
                    // an e-mail address is never auto-corrected
                    autoCorrectEnabled = if (keyboardType == KeyboardType.Email) false else null,
                    keyboardType = if (isSecure) KeyboardType.Password else keyboardType,
                    imeAction = imeAction,
                ),
                keyboardActions = keyboardActions,
                visualTransformation = if (isSecure) PasswordVisualTransformation() else VisualTransformation.None,
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (text.isEmpty()) {
                            // secondary text colour so the placeholder stays readable in Dark mode
                            Text(placeholder, style = textStyle, color = colors.textSecondary.copy(alpha = 0.85f), maxLines = 1)
                        }
                        inner()
                    }
                },
            )
            if (isDisabled) {
                Icon(Icons.Rounded.Lock, null, Modifier.size((12f * AB_SF_TO_VECTOR).dp * s), tint = colors.textSecondary)
            }
        }
        if (error != null) {
            Text(
                error,
                modifier = Modifier.padding(horizontal = 8.dp * s),
                style = AbTypography.label(s),
                color = colors.danger,
            )
        }
    }
}

/**
 * A multi-line box in the same style as [AbFormField], with the icon at the top-left, a placeholder while empty, and the
 * same error line. [height] is the unscaled height (scaled by the kit factor here).
 */
@Composable
fun AbFormTextArea(
    icon: ImageVector,
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 110.dp,
    error: String? = null,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.md * s)
    var focused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val hasError = error != null

    val ring by animateColorAsState(
        when {
            hasError -> colors.danger
            focused -> colors.brandPrimary
            else -> colors.outline
        },
        tween(AbMotion.FAST, easing = AbEaseOut), label = "abFormAreaRing",
    )
    val ringWidth by animateDpAsState(
        if (focused || hasError) 3.dp else 2.dp,
        tween(AbMotion.FAST, easing = AbEaseOut), label = "abFormAreaRingWidth",
    )
    val iconTint = when {
        hasError -> colors.danger
        focused -> colors.brandPrimary
        else -> colors.textSecondary
    }
    val textStyle = AbTypography.body(s).copy(fontWeight = FontWeight.Bold, color = colors.textPrimary)

    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp * s)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(height * s)
                .background(colors.surfaceTint.copy(alpha = if (colors.isDark) 0.5f else 0.6f), shape)
                .border(ringWidth, ring, shape)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { focusRequester.requestFocus() },
        ) {
            Box(Modifier.padding(top = 14.dp * s, start = 14.dp * s).width(22.dp * s), contentAlignment = Alignment.Center) {
                Icon(icon, null, Modifier.size((16f * AB_SF_TO_VECTOR).dp * s), tint = iconTint)
            }
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 14.dp * s, start = 46.dp * s, end = 12.dp * s, bottom = 8.dp * s)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focused = it.isFocused },
                textStyle = textStyle,
                cursorBrush = SolidColor(colors.brandPrimary),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                decorationBox = { inner ->
                    Box {
                        if (text.isEmpty()) {
                            Text(placeholder, style = textStyle, color = colors.textSecondary.copy(alpha = 0.7f))
                        }
                        inner()
                    }
                },
            )
        }
        if (error != null) {
            Text(
                error,
                modifier = Modifier.padding(horizontal = 8.dp * s),
                style = AbTypography.label(s),
                color = colors.danger,
            )
        }
    }
}
