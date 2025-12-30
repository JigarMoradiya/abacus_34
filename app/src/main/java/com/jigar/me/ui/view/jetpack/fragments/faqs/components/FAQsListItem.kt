package com.jigar.me.ui.view.jetpack.fragments.faqs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.FAQs


@Composable
fun FAQsListItem(
    faqsList: List<FAQs>
) {
    LazyColumn {
        items(faqsList) { faq ->
            FaqItem(faq = faq)
        }
    }
}

@Composable
fun FaqItem(
    faq: FAQs
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        onClick = {
            if (faq.answer.isNotEmpty()) {
                expanded = !expanded
            }
        },
        shape = RoundedCornerShape(12.dp),
        color = colorResource(R.color.card_bg),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.activity_padding12),
                vertical = 0.dp
            )
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.activity_padding12),
                vertical = dimensionResource(R.dimen.activity_padding10)
            )
        ) {

            // ───────── Question Row ─────────
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                AutoLinkText(
                    text = faq.question,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        color = Color.Black
                    )
                )

                if (faq.answer.isNotEmpty()) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_down),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.rotate(if (expanded) 180f else 0f)
                    )
                }
            }

            // ───────── Answer ─────────
            if (expanded && faq.answer.isNotEmpty()) {
                Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding4)))

                AutoLinkText(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily(Font(R.font.font_medium)),
                        color = colorResource(R.color.colorEditTextBlack_33)
                    )
                )
            }
        }
    }
}


@Composable
fun AutoLinkText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle
) {
    val uriHandler = LocalUriHandler.current
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val annotatedText = remember(text) {
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
                        color = Color.Blue,
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


