package com.jigar.me.ui.view.home.common_ui.dialogs

import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.jetpack.utils.ui.extensions.htmlToAnnotatedString
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors

/**
 * Visual treatment for CustomPopupView. Content (title/description/buttons/logic)
 * never changes between themes -- only the container styling does.
 */
enum class PopupTheme {
    /** Plain white card -- the original look. Default so existing call sites that
     * never opt in keep rendering exactly as before. */
    CLASSIC,
    /** "Candy Pop" -- the whole card becomes a saturated gradient block (accent),
     * white-outline title, icon in a white badge overlapping the top edge.
     * For payoff moments: results, streaks, rewards. */
    CELEBRATION,
    /** "Sticker Book" -- card stays white/readable, gets a colored header band
     * (accent) behind the icon. For confirmations, errors, and anything with
     * denser text that needs to stay easy to read. */
    CONFIRM,
}

@Composable
fun CustomPopupView(
    title: String? = null,
    description: String? = null,
    notes: String? = null,
    position: Alignment = Alignment.Center,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    onPositiveTapped: (() -> Unit)? = null,
    onNegativeTapped: (() -> Unit)? = null,
    widthMultiplier: Float = 0.5f,
    icon: Int? = null,
    theme: PopupTheme = PopupTheme.CLASSIC,
    accent: ButtonType = ButtonType.POSITIVE,
) {
    val accentColors = getButtonColors(accent)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = position
    ) {
        val screenWidthDp = with(LocalDensity.current) {
            LocalWindowInfo.current.containerSize.width.toDp()
        }
        val popupWidth = screenWidthDp * widthMultiplier

        when (theme) {
            PopupTheme.CLASSIC -> ClassicCard(
                popupWidth, title, description, notes, icon,
                positiveButtonText, negativeButtonText, onPositiveTapped, onNegativeTapped,
                ButtonType.POSITIVE
            )
            PopupTheme.CELEBRATION -> CelebrationCard(
                popupWidth, title, description, notes, icon,
                positiveButtonText, negativeButtonText, onPositiveTapped, onNegativeTapped,
                accent, accentColors
            )
            PopupTheme.CONFIRM -> ConfirmCard(
                popupWidth, title, description, notes, icon,
                positiveButtonText, negativeButtonText, onPositiveTapped, onNegativeTapped,
                accent, accentColors
            )
        }
    }
}

@Composable
private fun ClassicCard(
    popupWidth: androidx.compose.ui.unit.Dp,
    title: String?, description: String?, notes: String?, icon: Int?,
    positiveButtonText: String?, negativeButtonText: String?,
    onPositiveTapped: (() -> Unit)?, onNegativeTapped: (() -> Unit)?,
    buttonAccent: ButtonType,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(popupWidth)
            .background(Color.White, RoundedCornerShape(Dimens20))
            .padding(horizontal = Dimens20, vertical = Dimens4)
    ) {
        PopupIcon(icon, tint = null)
        PopupTitle(title, color = Color.Black)
        PopupDescription(description)
        PopupNotes(notes)
        PopupButtons(positiveButtonText, negativeButtonText, onPositiveTapped, onNegativeTapped, buttonAccent, negativeColor = Color.Black)
    }
}

@Composable
private fun CelebrationCard(
    popupWidth: androidx.compose.ui.unit.Dp,
    title: String?, description: String?, notes: String?, icon: Int?,
    positiveButtonText: String?, negativeButtonText: String?,
    onPositiveTapped: (() -> Unit)?, onNegativeTapped: (() -> Unit)?,
    buttonAccent: ButtonType, accentColors: com.jigar.me.ui.view.home.theme.ButtonColors,
) {
    Box(
        modifier = Modifier.width(popupWidth),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(Dimens20 * 1.2f), ambientColor = accentColors.base, spotColor = accentColors.base)
                .clip(RoundedCornerShape(Dimens20 * 1.2f))
                .background(accentColors.gradient)
                .border(3.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(Dimens20 * 1.2f))
        ) {
            ConfettiScatter()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens20, vertical = Dimens20)
            ) {
                if (icon != null) Box(Modifier.height(Dimens20))
                BubbleTitle(title, accentColors.base)
                PopupDescription(description, color = Color.White.copy(alpha = 0.92f))
                PopupNotes(notes)
                PopupButtons(positiveButtonText, negativeButtonText, onPositiveTapped, onNegativeTapped, buttonAccent, negativeColor = Color.White.copy(alpha = 0.9f))
            }
        }

        if (icon != null) {
            Box(
                modifier = Modifier
                    .offset(y = (-28).dp)
                    .size(76.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.8f),
                                radius = size.minDimension / 2f - 1.5.dp.toPx(),
                                style = Stroke(
                                    width = 3.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(7f, 5f), 0f)
                                )
                            )
                        }
                )
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(8.dp, CircleShape)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Text(
                    text = "✨",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = 2.dp)
                )
                Text(
                    text = "✨",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-2).dp, y = (-2).dp)
                )
            }
        }
    }
}

/** Scattered translucent dots + sparkles behind the celebration card content,
 * so the gradient block doesn't read as a flat rectangle. */
@Composable
private fun ConfettiScatter() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight
        Box(Modifier.size(22.dp).offset(x = w * 0.12f - 11.dp, y = h * 0.15f - 11.dp).background(Color.White.copy(alpha = 0.18f), CircleShape))
        Box(Modifier.size(14.dp).offset(x = w * 0.85f - 7.dp, y = h * 0.22f - 7.dp).background(Color.White.copy(alpha = 0.14f), CircleShape))
        Box(Modifier.size(18.dp).offset(x = w * 0.9f - 9.dp, y = h * 0.75f - 9.dp).background(Color.White.copy(alpha = 0.16f), CircleShape))
        Box(Modifier.size(12.dp).offset(x = w * 0.08f - 6.dp, y = h * 0.8f - 6.dp).background(Color.White.copy(alpha = 0.12f), CircleShape))
        Text(
            text = "✨", fontSize = 16.sp, color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.offset(x = w * 0.78f - 8.dp, y = h * 0.12f - 9.dp)
        )
        Text(
            text = "✨", fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.offset(x = w * 0.18f - 6.dp, y = h * 0.88f - 7.dp)
        )
    }
}

@Composable
private fun ConfirmCard(
    popupWidth: androidx.compose.ui.unit.Dp,
    title: String?, description: String?, notes: String?, icon: Int?,
    positiveButtonText: String?, negativeButtonText: String?,
    onPositiveTapped: (() -> Unit)?, onNegativeTapped: (() -> Unit)?,
    buttonAccent: ButtonType, accentColors: com.jigar.me.ui.view.home.theme.ButtonColors,
) {
    Column(
        modifier = Modifier
            .width(popupWidth)
            .clip(RoundedCornerShape(Dimens20))
            .background(Color.White)
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .background(accentColors.gradient),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.height(dimensionResource(id = R.dimen.popup_icon_height) * 0.6f)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens20, vertical = Dimens20)
        ) {
            PopupTitle(title, color = Color.Black)
            PopupDescription(description)
            PopupNotes(notes)
            PopupButtons(positiveButtonText, negativeButtonText, onPositiveTapped, onNegativeTapped, buttonAccent, negativeColor = Color.Black)
        }
    }
}

// MARK: - Shared pieces

@Composable
private fun PopupIcon(icon: Int?, tint: Color?) {
    if (icon != null) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .height(dimensionResource(id = R.dimen.popup_icon_height))
                .padding(top = Dimens12)
        )
    }
}

@Composable
private fun PopupTitle(title: String?, color: Color) {
    if (!title.isNullOrEmpty()) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall.scaled(),
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            fontWeight = FontWeight.Bold,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Dimens16)
        )
    }
}

/** White fill with a soft dark outline -- the "Candy Pop" bubble-letter treatment. */
@Composable
private fun BubbleTitle(title: String?, outline: Color) {
    if (!title.isNullOrEmpty()) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall.scaled().copy(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = outline.copy(alpha = 0.9f),
                    offset = androidx.compose.ui.geometry.Offset(1.5f, 1.5f),
                    blurRadius = 0f
                )
            ),
            fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Dimens16)
        )
    }
}

@Composable
private fun PopupDescription(description: String?, color: Color = Color.Black) {
    if (!description.isNullOrEmpty()) {
        AndroidView(
            factory = { context ->
                TextView(context).apply {
                    textSize = 15f * appScale()
                    setTextColor(color.toArgbCompat())
                    textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    typeface = ResourcesCompat.getFont(context, R.font.font_medium)
                }
            },
            update = { textView ->
                textView.text = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
                textView.setTextColor(color.toArgbCompat())
            },
            modifier = Modifier.padding(top = Dimens4)
        )
    }
}

private fun Color.toArgbCompat(): Int {
    val a = (alpha * 255).toInt()
    val r = (red * 255).toInt()
    val g = (green * 255).toInt()
    val b = (blue * 255).toInt()
    return (a shl 24) or (r shl 16) or (g shl 8) or b
}

@Composable
private fun PopupNotes(notes: String?) {
    if (!notes.isNullOrEmpty()) {
        Text(
            text = notes.htmlToAnnotatedString(),
            style = MaterialTheme.typography.bodyMedium.scaled().copy(color = Color.Red, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Dimens8)
        )
    }
}

@Composable
private fun PopupButtons(
    positiveButtonText: String?, negativeButtonText: String?,
    onPositiveTapped: (() -> Unit)?, onNegativeTapped: (() -> Unit)?,
    buttonAccent: ButtonType, negativeColor: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = Dimens8)) {
        if (!positiveButtonText.isNullOrEmpty() && onPositiveTapped != null) {
            KidsActionButton(
                text = positiveButtonText,
                type = buttonAccent,
                onClick = onPositiveTapped,
                isSmall = true
            )
        }

        if (!negativeButtonText.isNullOrEmpty() && onNegativeTapped != null) {
            TextButton(
                onClick = {
                    AudioPlayerManager.playSoundBtnClick()
                    onNegativeTapped()
                },
            ) {
                Text(
                    text = negativeButtonText,
                    fontFamily = FontFamily(Font(R.font.font_medium)),
                    style = MaterialTheme.typography.bodySmall.scaled(),
                    fontWeight = FontWeight.Medium,
                    color = negativeColor
                )
            }
        }
    }
}
