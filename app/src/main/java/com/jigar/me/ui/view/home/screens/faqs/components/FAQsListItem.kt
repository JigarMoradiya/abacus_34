package com.jigar.me.ui.view.home.screens.faqs.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.local.data.FAQs
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbChevron
import com.jigar.me.ui.view.ab.AbMotion
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.ab.abSquash
import com.jigar.me.ui.view.home.theme.AppDimens


@Composable
fun FAQsListItem(
    faqsList: List<FAQs>
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = DeviceInfo.screenHorizontalPadding(),
            end = AppDimens.Dimens16,
            top = AppDimens.Dimens12,
            bottom = AppDimens.Dimens12
        ),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)
    ) {
        items(faqsList) { faq ->
            FaqItem(faq = faq)
        }
    }
}

/** One question: a chunky tappable header that opens to show its answer in the same card. */
@Composable
fun FaqItem(
    faq: FAQs
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    var expanded by remember { mutableStateOf(false) }
    val hasAnswer = faq.answer.isNotEmpty()
    val shape = RoundedCornerShape(AbShapes.md * s)
    val rim by animateColorAsState(
        if (expanded) colors.accentOrange.copy(alpha = 0.7f) else colors.outline,
        tween(AbMotion.NORMAL), label = "faqRim"
    )
    val chevronTurn by animateFloatAsState(
        if (expanded) 180f else 0f,
        spring(dampingRatio = 0.7f, stiffness = 322f), label = "faqChevron"
    )
    val linkSize = if (DeviceInfo.isLargeTablet) 22f else if (DeviceInfo.isTablet) 20f else 16f

    Column(
        Modifier
            .fillMaxWidth()
            .abChunkyFace(
                face = colors.bgSecondary.copy(alpha = if (colors.isDark) 0.96f else 0.94f),
                edge = colors.outline, stroke = null,
                cornerRadius = AbShapes.md * s, edgeHeight = 3.dp * s,
            )
            .border(2.dp, rim, shape)
    ) {

        // ───────── Question Row ─────────
        Row(
            Modifier
                .fillMaxWidth()
                .abSquash(cornerRadius = AbShapes.md * s, edgeHeight = 0.dp, enabled = hasAnswer) { expanded = !expanded }
                .padding(horizontal = 14.dp * s, vertical = 10.dp * s),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp * s)
        ) {
            Icon(Icons.Rounded.Help, null, Modifier.size((20f * AB_SF_TO_VECTOR).dp * s), tint = colors.accentOrange)

            AutoLinkText(
                text = faq.question,
                modifier = Modifier.weight(1f),
                style = AbTypography.body(s).copy(fontWeight = FontWeight.ExtraBold, color = colors.textPrimary),
                linkColor = colors.brandPrimary
            )

            if (hasAnswer) {
                AbChevron(
                    colors.textSecondary,
                    Modifier.size(18.dp * s).rotate(chevronTurn - 90f),
                    pointingLeft = true
                )
            }
        }

        // ───────── Answer ─────────
        AnimatedVisibility(
            visible = expanded && hasAnswer,
            enter = fadeIn(tween(AbMotion.NORMAL)) + expandVertically(spring(dampingRatio = 0.7f, stiffness = 322f)),
            exit = fadeOut(tween(AbMotion.NORMAL)) + shrinkVertically(spring(dampingRatio = 0.7f, stiffness = 322f))
        ) {
            AutoLinkText(
                text = faq.answer,
                modifier = Modifier.padding(start = 16.dp * s, end = 16.dp * s, bottom = 12.dp * s),
                style = AbTypography.fixed(linkSize, FontWeight.SemiBold).copy(color = colors.textPrimary),
                linkColor = colors.brandPrimary
            )
        }
    }
}


@Composable
fun AutoLinkText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    linkColor: Color = Color.Blue
) {
    val uriHandler = LocalUriHandler.current
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val annotatedText = remember(text, linkColor) {
        buildAnnotatedString {
            val regex = Regex(
                "(https?://\\S+)|([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})"
            )

            var lastIndex = 0
            regex.findAll(text).forEach { match ->
                val start = match.range.first
                val end = match.range.last + 1

                append(text.substring(lastIndex, start))

                val linkText = match.value
                val link = if (linkText.contains("@")) {
                    "mailto:$linkText"
                } else {
                    linkText
                }

                pushStringAnnotation("LINK", link)
                withStyle(
                    SpanStyle(
                        color = linkColor,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(linkText)
                }
                pop()

                lastIndex = end
            }

            if (lastIndex < text.length) {
                append(text.substring(lastIndex))
            }
        }
    }

    BasicText(
        text = annotatedText,
        style = style,
        onTextLayout = { layoutResult = it },
        modifier = modifier.pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown()

                val layout = layoutResult ?: return@awaitEachGesture
                val offset = layout.getOffsetForPosition(down.position)

                val link = annotatedText
                    .getStringAnnotations("LINK", offset, offset)
                    .firstOrNull()

                if (link != null) {
                    uriHandler.openUri(link.item)
                    down.consume() // ✅ consume ONLY when link clicked
                }
                // ❌ otherwise DO NOT consume → parent Surface click works
            }
        }
    )
}


