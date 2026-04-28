package com.jigar.me.ui.jetpack.utils.ui.extensions

import android.text.style.StyleSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.core.text.HtmlCompat

fun String.htmlToAnnotatedString(): AnnotatedString {
    val spanned = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)

    return buildAnnotatedString {
        append(spanned.toString())

        spanned.getSpans(0, spanned.length, StyleSpan::class.java)
            .forEach { span ->
                val start = spanned.getSpanStart(span)
                val end = spanned.getSpanEnd(span)

                when (span.style) {
                    android.graphics.Typeface.BOLD -> {
                        addStyle(
                            SpanStyle(fontWeight = FontWeight.Bold),
                            start,
                            end
                        )
                    }
                }
            }
    }
}